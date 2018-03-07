package com.ggp.theclub.manager;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.ggp.theclub.MallApplication;
import com.ggp.theclub.model.MapOptions;
import com.ggp.theclub.repository.MallRepository;
import com.ggp.theclub.util.TenantUtils;
import com.ggp.theclub.view.TenantMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashSet;
import java.util.Set;

import java8.util.stream.StreamSupport;

public class GoogleMapManager {
    private final String LOG_TAG = GoogleMapManager.class.getSimpleName();
    private final long DROP_PIN_DURATION = 300;
    private Context context = MallApplication.getApp();
    private MallRepository mallRepository = MallApplication.getApp().getMallRepository();
    private MapFragment mapFragment;
    private MapOptions mapOptions;
    private Set<Marker> droppedPins = new HashSet<>();
    private Set<Marker> tenantMarkers = new HashSet<>();
    private CameraPosition cameraPosition;
    private GoogleMap googleMap;

    public GoogleMapManager(MapOptions mapOptions) {
        this.mapOptions = mapOptions;
        mapFragment = createMapFragment();
        configureMap(mapOptions);
    }

    public void insertMap(Fragment parentFragment, int containerViewId) {
        FragmentTransaction fragmentTransaction = parentFragment.getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, mapFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void removeMap(Fragment parentFragment) {
        FragmentTransaction fragmentTransaction = parentFragment.getChildFragmentManager().beginTransaction();
        if(parentFragment.isAdded()) {
            fragmentTransaction.remove(mapFragment).commit();
        }
    }

    public Location getLocationFromPoint(Point point) {
        Location location = new Location("");

        if(googleMap == null) {
            return location;
        }

        LatLng latLng = googleMap.getProjection().fromScreenLocation(point);
        if(latLng != null) {
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
        }
        return location;
    }

    public void dropPin(double lat, double lng) {
        dropPin(null, lat, lng, false);
    }

    public void dropPin(Integer markerImageId, double lat, double lng, boolean draggable) {
        OnMapReadyCallback callback = (map) -> {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .draggable(draggable));
            if(markerImageId != null) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(markerImageId));
            }

            droppedPins.add(marker);
            dropPinEffect(marker);
        };
        mapFragment.getMapAsync(callback);
    }

    public void setPinDragListener(PinDragListener pinDragListener) {

        GoogleMap.OnMarkerDragListener onMarkerDragListener = new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}

            @Override
            public void onMarkerDrag(Marker marker) {}

            @Override
            public void onMarkerDragEnd(Marker marker) {
                pinDragListener.onPinDrag(marker.getPosition().latitude, marker.getPosition().longitude);
            }
        };

        OnMapReadyCallback callback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
               map.setOnMarkerDragListener(onMarkerDragListener);
            }
        };
        mapFragment.getMapAsync(callback);
    }

    public void clearDroppedPins() {
        OnMapReadyCallback callback = map -> {
            StreamSupport.stream(droppedPins).forEach(pin -> pin.remove());
            droppedPins.clear();
        };
        mapFragment.getMapAsync(callback);
    }

    public void setCameraPosition(Location position) {
        mapOptions.setLatitude(position.getLatitude());
        mapOptions.setLongitude(position.getLongitude());
        configureMap(mapOptions);
    }

    private MapFragment createMapFragment() {
        LatLng coordinates = new LatLng(mapOptions.getLatitude(), mapOptions.getLongitude());
        cameraPosition = new CameraPosition(coordinates, mapOptions.getInitialZoomLevel(), 0, 0);
        GoogleMapOptions options = new GoogleMapOptions()
                .camera(cameraPosition)
                .mapToolbarEnabled(mapOptions.isMapToolbarEnabled());
        return MapFragment.newInstance(options);
    }

    private void configureMap(MapOptions mapOptions) {

        OnMapReadyCallback callback = map -> {
            googleMap = map;
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            map.setIndoorEnabled(mapOptions.isIndoorsMapEnabled());
            map.getUiSettings().setAllGesturesEnabled(mapOptions.isGesturesEnabled());
            try {
                map.setMyLocationEnabled(mapOptions.isCurrentLocationEnabled());
            } catch (SecurityException e) {
                Log.e(LOG_TAG, e.getMessage());
            }

            if (mapOptions.isAnchorStoreMarkersEnabled()) {
                displayAnchorStoreMarkers(map);
            }

            map.setOnCameraChangeListener(cameraPosition -> {
                this.cameraPosition = cameraPosition;
                updateStoreMarkersVisibility(cameraPosition.zoom);
            });
        };
        mapFragment.getMapAsync(callback);
    }

    private void displayAnchorStoreMarkers(GoogleMap map) {
        mallRepository.queryForTenants(tenants -> {
            if (!tenants.isEmpty()) {
                StreamSupport.stream(TenantUtils.getAnchorTenants(tenants)).forEach(store -> {
                    TenantMarker tenantMarker = new TenantMarker(context);
                    tenantMarker.setTitle(store.getName());

                    MarkerOptions options = new MarkerOptions();
                    options.position(new LatLng(store.getLatitude(), store.getLongitude()));
                    options.icon(BitmapDescriptorFactory.fromBitmap(generateIcon(context, tenantMarker)));

                    Marker marker = map.addMarker(options);
                    tenantMarkers.add(marker);
                });
            }
        });
    }

    private static Bitmap generateIcon(Context context, View view) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setContentView(view);
        iconGenerator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        return iconGenerator.makeIcon();
    }

    private void updateStoreMarkersVisibility(float zoom) {
        for (Marker storeMarker : tenantMarkers) {
            storeMarker.setVisible(zoom >= mapOptions.getPlacesZoomLevelThreshold());
        }
    }

    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long stepTime = 10;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / DROP_PIN_DURATION), 0);
                marker.setAnchor(0.5f, 1.0f + stepTime * t);

                if (t > 0.0) {
                    handler.postDelayed(this, stepTime);
                }
            }
        });
    }

    public interface PinDragListener {
        void onPinDrag(double latitude, double longitude);
    }
}
