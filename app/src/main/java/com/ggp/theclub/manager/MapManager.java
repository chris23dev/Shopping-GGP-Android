package com.ggp.theclub.manager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.ggp.theclub.MallApplication;
import com.ggp.theclub.R;
import com.ggp.theclub.customlocale.LocaleUtils;
import com.ggp.theclub.event.MapLevelUpdateEvent;
import com.ggp.theclub.event.MapReadyEvent;
import com.ggp.theclub.event.MapSelectEvent;
import com.ggp.theclub.model.MallConfig;
import com.ggp.theclub.model.MapAmenityFilter;
import com.ggp.theclub.model.MapLevel;
import com.ggp.theclub.model.MapMoverType;
import com.ggp.theclub.model.MapState;
import com.ggp.theclub.model.Tenant;
import com.ggp.theclub.util.StringUtils;
import com.ggp.theclub.view.MapAmenity;
import com.ggp.theclub.view.MapEndPin;
import com.ggp.theclub.view.MapMarkerPin;
import com.ggp.theclub.view.MapMover;
import com.ggp.theclub.view.MapMoverHead;
import com.ggp.theclub.view.MapMoverTail;
import com.ggp.theclub.view.MapParkingZone;
import com.ggp.theclub.view.MapRoute;
import com.ggp.theclub.view.MapStartPin;
import com.ggp.theclub.view.MapUnit;
import com.ggp.theclub.view.MapUnitLabel;
import com.jibestream.jibestreamandroidlibrary.elements.Element;
import com.jibestream.jibestreamandroidlibrary.elements.MapLabel;
import com.jibestream.jibestreamandroidlibrary.elements.MoverHead;
import com.jibestream.jibestreamandroidlibrary.elements.MoverTail;
import com.jibestream.jibestreamandroidlibrary.elements.Unit;
import com.jibestream.jibestreamandroidlibrary.http.BasicAuthentication;
import com.jibestream.jibestreamandroidlibrary.intentFilters.IntentFilterEngine;
import com.jibestream.jibestreamandroidlibrary.intentFilters.IntentFilterMap;
import com.jibestream.jibestreamandroidlibrary.main.EngineView;
import com.jibestream.jibestreamandroidlibrary.main.M;
import com.jibestream.jibestreamandroidlibrary.main.M.OnElementClickListener;
import com.jibestream.jibestreamandroidlibrary.main.M.OnMapReadyCallback;
import com.jibestream.jibestreamandroidlibrary.main.VenueDataService;
import com.jibestream.jibestreamandroidlibrary.main.VenueDataService.OnVenueDataServiceReadyCallback;
import com.jibestream.jibestreamandroidlibrary.mapBuilderV3.astar.PathPerFloor;
import com.jibestream.jibestreamandroidlibrary.mapBuilderV3.dataObjects.Destination;
import com.jibestream.jibestreamandroidlibrary.mapBuilderV3.dataObjects.MapFull;
import com.jibestream.jibestreamandroidlibrary.mapBuilderV3.dataObjects.PathType;
import com.jibestream.jibestreamandroidlibrary.mapBuilderV3.dataObjects.Waypoint;
import com.jibestream.jibestreamandroidlibrary.mapBuilderV3.dataObjects.WaypointEntityLink;
import com.jibestream.jibestreamandroidlibrary.mapBuilderV3.textDirections.TDInstruction;
import com.jibestream.jibestreamandroidlibrary.math.Transform;
import com.jibestream.jibestreamandroidlibrary.shapes.IconShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.greenrobot.event.EventBus;
import java8.util.Objects;
import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import lombok.Getter;

public class MapManager implements OnVenueDataServiceReadyCallback, OnMapReadyCallback {
    private final String LOG_TAG = getClass().getSimpleName();

    private final String JIBESTREAM_USERNAME = "jsapiclient";
    private final String JIBESTREAM_PASSWORD = "JSapi23";
    private final String JIBESTREAM_API_KEY = "JMJ143880vl1045185ENRLW143880fz1";
    private final String JIBESTREAM_LANGUAGE_CODE = "en";
    private final String JIBESTREAM_ENDPOINT = "https://ggp.js-network.co";
    private final String ANCHOR_STORE_TYPE = "ANC";
    private final String ANCHOR_STORE_LABEL = "Anchor Store";
    private final String PARKING_AMENITY_DESCRIPTION = "Parking";
    private final String PARKING_ZONE_DESCRIPTION = "Parking-Zones";
    private final String WAYPOINT_UNIT_KEY = "waypoint-unit";
    private final String NUMBER_FORMAT_PATTERN = "\\d+";
    private final String onText = "on";
    private final String nearText = MallApplication.getApp().getString(R.string.near_text);
    private final String nearTextCapitalized = StringUtils.capitalizeFirstLetter(nearText);
    private final int INVALID_LEVEL_POSITION = -1;
    private final int WAYFIND_UTURN_DISTANCE = 30;
    private final int ALL_PARKING_LEVEL = 99;
    private int WAYFIND_MIN_DISTANCE;
    private String MAP_STATE_UID;
    @Getter private M map;
    @Getter private boolean mapStarted;
    @Getter private boolean mapReady;
    @Getter private boolean selectionEnabled;
    private int selection;
    private List<Integer> highlights = new ArrayList<>();
    private MapAmenityFilter mapAmenityFilter;
    private Waypoint startWaypoint;
    private Waypoint endWaypoint;
    private MapStartPin mapStartPin;
    private MapEndPin mapEndPin;
    private MapMarkerPin mapMarkerPin;
    private Handler handler = new Handler();
    private LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MallApplication.getApp());
    private static MapManager mapManager = new MapManager();

    /**
     * Returns instance of MapManager that can be used to call public methods from anywhere in the app
     */
    public static MapManager getInstance() {
        return mapManager;
    }

    /**
     * Called on application startup, initializes new M instance with lifecycle callbacks,
     * overrides default classes with custom implementations,
     * and kicks off initial fetch of map data
     */
    public void start() {
        map = new M(MallApplication.getApp());
        map.setOnMapReadyCallback(this);
        map.classLib.amenityClass = MapAmenity.class;
        map.classLib.unitClass = MapUnit.class;
        map.classLib.unitLabelClass = MapUnitLabel.class;
        map.classLib.youAreHereClass = MapStartPin.class;
        map.classLib.pinClass = MapEndPin.class;
        map.classLib.routeClass = MapRoute.class;
        map.classLib.moverHeadClass = MapMoverHead.class;
        map.classLib.moverTailClass = MapMoverTail.class;
        map.classLib.elementCustomClass = MapParkingZone.class;
        mapStarted = true;
        refresh();
    }

    /**
     * Called via start or whenever a new mall is selected.
     * Locks out any method which depends on map data being present,
     * sends an authenticated request to Jibestream to fetch map data by mall ID,
     * and initializes a callback for receipt of requested data
     */
    public void refresh() {
        if (mapStarted && MallApplication.getApp().getMallManager().hasValidMall()) {
            mapReady = false;

            WAYFIND_MIN_DISTANCE = MallApplication.getApp()
                    .getMallManager().getMall().getMallConfig().getWayfindingMinDistance();

            BasicAuthentication basicAuthentication = new BasicAuthentication(
                    JIBESTREAM_USERNAME,
                    JIBESTREAM_PASSWORD,
                    JIBESTREAM_API_KEY,
                    getLanguage(),
                    map.getSdkVersion()
            );

            VenueDataService.setOnVenueDataServiceReadyCallback(this);

            VenueDataService.getData(
                    MallApplication.getApp(),
                    JIBESTREAM_ENDPOINT,
                    MallApplication.getApp().getMallManager().getMall().getId(),
                    basicAuthentication);
        }
    }

    /**
     * Called when map is ready or whenever a screen that uses a map becomes visible to the user.
     * Kicks off map resume operations if and only if map is initialized and ready,
     * and if map screen to be resumed is the same one that was just started or recently paused,
     * and is not already in a resumed state.
     * If map screens don't match and map is currently in resumed state,
     * wait for confirmation that pause state has been entered on that map screen before resuming new map screen
     */
    public void resume(MapState mapState, EngineView engineView) {
        if (mapStarted && mapReady) {
            setAllLabels(map);
            if (mapState.getUID() != null && !mapState.getUID().equals(MAP_STATE_UID) && map.getEngineState().equalsIgnoreCase(IntentFilterEngine.RESUME)) {
                localBroadcastManager.registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        localBroadcastManager.unregisterReceiver(this);
                        resume(engineView, mapState);
                    }
                }, new IntentFilterEngine(IntentFilterEngine.PAUSE));
            } else {
                resume(engineView, mapState);
            }
        }
    }

    /**
     * Called by public facing resume method once the right conditions are met
     * Updates the current map state and engine view,
     * and registers map interaction listeners
     */
    private void resume(EngineView engineView, MapState mapState) {
        setMapState(mapState);
        map.onResume();
        if (engineView != null) {
            map.setEngineView(engineView);
        }
        localBroadcastManager.registerReceiver(doubleTapReceiver, new IntentFilter(EngineView.ON_DOUBLE_TAP));
        localBroadcastManager.registerReceiver(levelReceiver, new IntentFilterMap(IntentFilterMap.ACTION));
    }

    /**
     * Called after rendering a map snapshot or whenever a screen that uses a map is no longer visible to the user
     * and after that screen has stored the current map state.
     * Clears the existing map state and unregisters map interaction listeners
     */
    public void pause() {
        if (mapStarted) {
            resetMapState();
            map.onPause();
            localBroadcastManager.unregisterReceiver(doubleTapReceiver);
            localBroadcastManager.unregisterReceiver(levelReceiver);
        }
    }

    /**
     * Called on application shutdown to terminate map operations and free up resources
     */
    public void destroy() {
        if (mapStarted) {
            map.onDestroy();
        }
    }

    /**
     * Called by the VenueDataService once map data response,
     * triggered by refresh method, has returned successfully.
     * Passes the received map data file identifier and configuration to a map instance method
     * which parses the file and generates the map view with the data and specified styles.
     * Once complete, onMapReady will be called
     */
    @Override
    public void onVenueDataReady(String id) {
        map.start(id, R.raw.config);
    }

    /**
     * Called by the VenueDataService once map data response,
     * triggered by refresh method, has returned unsuccessfully.
     * Logs the error and lets listening map screens know that map data fetching failed
     */
    @Override
    public void onVenueDataError() {
        Log.e(LOG_TAG, "Error retrieving map data.");
        EventBus.getDefault().post(new MapReadyEvent(false));
    }

    /**
     * Called by the map instance once map data has been parsed and the map view generated.
     * Kicks off the initialization of people mover objects, parking zone objects,
     * creates and adds custom elements to the map,
     * registers map interaction listener,
     * unlocks access to any method which depends on map data being present,
     * and lets listening map screens know that map data fetching succeeded,
     * and that map is now ready for manipulation and user interaction
     */
    @Override
    public void onMapReady() {
        setupPeopleMovers();
        setupParkingZones();
        map.youAreHere.setActive(false);
        map.pin.setActive(false);
        mapStartPin = new MapStartPin();
        mapEndPin = new MapEndPin();
        mapMarkerPin = new MapMarkerPin();
        mapStartPin.setVisible(false);
        mapEndPin.setVisible(false);
        mapMarkerPin.setVisible(false);
        map.addToMap(mapStartPin);
        map.addToMap(mapEndPin);
        map.addToMap(mapMarkerPin);
        map.setOnElementClickListener(onElementClickListener);
        mapReady = true;
        EventBus.getDefault().post(new MapReadyEvent(true));

        if(map != null)
            setAllLabels(map);
    }

    /**
     * Called by map instance whenever map has entered into resumed state
     */
    @Override
    public void onMapResume() {
    }

    /**
     * Called by map instance whenever map has entered into paused state
     */
    @Override
    public void onMapPause() {}

    /**
     * Called by map instance whenever map has entered into stopped state
     */
    @Override
    public void onMapStop() {}

    /**
     * Called by map instance whenever map has entered into destroyed state
     * and could be used for final instance cleanup measures, if necessary
     */
    @Override
    public void onMapDestroy() {}

    /**
     * Called whenever a user taps on a map screen element.
     * If the element is a store and selections are allowed,
     * shows the element as selected and lets listeners know that a store was selected.
     * Kicks off a wayfind level change if the element is related to a people mover.
     * Otherwise, clears any current map selection and lets listeners know to do the same
     */
    private OnElementClickListener onElementClickListener = element -> {
        if (element instanceof Unit && isSelectionEnabled()) {
            Destination[] destinations = ((Unit) element).getDestinations();
            if (destinations != null && destinations.length > 0) {
                Integer leaseId = getLeaseIdByDestination(destinations[0]);
                if (leaseId != null) {
                    EventBus.getDefault().post(new MapSelectEvent(leaseId, ANCHOR_STORE_TYPE.equals(destinations[0].destinationType)));
                    setSelection(leaseId);
                }
            }
        } else if (element instanceof MoverHead) {
            goToPreviousWayfindLevel();
        } else if (element instanceof MoverTail) {
            goToNextWayfindLevel();
        } else {
            EventBus.getDefault().post(new MapSelectEvent(0, false));
            setSelection(0);
        }
    };

    /**
     * Called when a user double taps on a map screen to zoom the map view in by a set scale
     */
    private BroadcastReceiver doubleTapReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            map.camera.zoomIn();
        }
    };

    /**
     * Called by map instance whenever map level changes.
     * Updates view bounds to show path if wayfind is in progress
     */
    private BroadcastReceiver levelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (map.getPathPerFloors() != null) {
                map.cameraToPath();
            }
        }
    };

    /**
     * Called by a map screen before going into paused state.
     * Returns the current state of the map view and map instance
     * so that it can be restored when the same map screen is resumed
     */
    public MapState getMapState() {
        MapState mapState = new MapState();
        if (mapReady) {
            mapState.setUID(MAP_STATE_UID);
            mapState.setX(map.camera.getX());
            mapState.setY(map.camera.getY());
            mapState.setScale(map.camera.getScale());
            mapState.setRotation(map.camera.getRoll());
            mapState.setMapId(map.getCurrentMap().map.mapId);
            mapState.setSelectionEnabled(isSelectionEnabled());
            mapState.setSelection(selection);
            mapState.setHighlights(highlights);
            mapState.setMapAmenityFilter(mapAmenityFilter);
        }
        return mapState;
    }

    /**
     * Called before a map screen goes into resumed state.
     * Overwrites the current state of the map view and map instance
     * with the passed in values in order to restore a previously paused state
     */
    private void setMapState(MapState mapState) {
        if (mapReady) {
            MAP_STATE_UID = mapState.getUID();
            int mapId = mapState.getMapId() == 0 ? map.getDefaultMap().map.mapId : mapState.getMapId();
            setCurrentLevel(map.getLevelsIndexWithMapID(mapId));
            if (mapState.getScale() == 0) {
                map.camera.zoomTo(map.defaultFramings[getCurrentLevel()]);
            } else {
                map.camera.setTranslation(mapState.getX(), mapState.getY());
                map.camera.setScale(mapState.getScale());
                map.camera.setRoll(mapState.getRotation());
            }
            selectionEnabled = mapState.isSelectionEnabled();
            setSelection(mapState.getSelection());
            setHighlights(mapState.getHighlights());
            setFilteredAmenities(mapState.getMapAmenityFilter());
        }
    }

    /**
     * Called before a map screen goes into paused state.
     * Clears the current state of the map view and map instance
     */
    private void resetMapState() {
        if (mapReady) {
            map.camera.reset();
            map.setLevel(map.getDefaultMap());
            setSelection(0);
            setHighlights(new ArrayList<>());
            setFilteredAmenities(MapAmenityFilter.NONE);
        }
    }

    /**
     * Called whenever map state is being set or reset or when a user taps an element on a map screen.
     * Clears any existing map selections and selects the requested element, if it exists
     */
    public void setSelection(int id) {
        if (mapReady) {
            selection = id;
            map.deselectUnits();
            if (id > 0) {
                Destination destination = getDestinationByLeaseId(id);
                if (destination != null) {
                    List<Element> elements = Arrays.asList(map.getElementsOfDestination(destination.id));
                    StreamSupport.stream(elements).forEach(element -> element.setSelectState(true));
                }
            }
        }
    }

    /**
     * Called whenever map state is being set or reset
     * or when a user sets or clears a filter from a map screen.
     * Clears any existing map highlights and highlights the requested list of elements, if they exist
     */
    public void setHighlights(List<Integer> ids) {
        if (mapReady) {
            highlights = ids;
            map.unHighlightUnits();
            StreamSupport.stream(ids).forEach(id -> {
                Destination destination = getDestinationByLeaseId(id);
                if (destination != null) {
                    map.setHighlightOnUnitsByDestinationID(destination.id, true);
                }
            });
        }
    }

    /**
     * Shows or hides all instances of the included elements
     */
    private void toggleElementVisibility(boolean visible) {
        map.setVisibilityOnType(MapAmenity.class, visible);
        map.setVisibilityOnType(MapMover.class, visible);
    }

    /**
     * Called whenever a level is selected by a user on a map screen,
     * or when a particular level should be shown to highlight a destination or a wayfind path.
     * If position is a valid level index, alerts listeners to level update, and updates map instance and map view
     */
    public void setCurrentLevel(int position) {
        if (mapReady) {
            MapFull[] levels = map.venueData.maps;
            if (position >= 0 && position < levels.length) {
                EventBus.getDefault().post(getMapLevelUpdateEvent(position));
                if (position == map.getCurrentMapIndex() && map.getPathPerFloors() != null) {
                    map.cameraToPath();
                }
                map.setLevel(levels[position]);
            }
        }
    }

    /**
     * Called whenever current level index is needed.
     * If map is not ready, an invalid position is returned, which callers can check
     */
    public int getCurrentLevel() {
        return mapReady ? map.getCurrentMapIndex() : INVALID_LEVEL_POSITION;
    }

    /**
     * Returns whether or not current map has multiple levels
     */
    public boolean isMultiLevel() {
        return mapReady && map.venueData.maps.length > 1;
    }

    /**
     * Returns a list of all map levels, excluding the specific ALL level, used only for parking availability
     */
    public List<MapLevel> getMapLevels() {
        List<MapLevel> levels = new ArrayList<>();
        if (mapReady) {
            levels = StreamSupport.stream(Arrays.asList(map.venueData.maps))
                    .filter(mapFull -> mapFull.map.floorSequence != ALL_PARKING_LEVEL)
                    .map(level -> new MapLevel(map.getLevelsIndexWithMapID(level.map.mapId), level.map.description)).collect(Collectors.toList());
        }
        return levels;
    }

    /**
     * Returns a list of all parking levels, including the specific ALL level, used for parking availability
     */
    public List<MapLevel> getParkingLevels() {
        List<MapLevel> levels = new ArrayList<>();
        if (mapReady) {
            levels.addAll(getMapLevels());
            Optional<MapLevel> allParkingLevelOptional = StreamSupport.stream(Arrays.asList(map.venueData.maps))
                    .filter(mapFull -> mapFull.map.floorSequence == ALL_PARKING_LEVEL)
                    .map(level -> new MapLevel(map.getLevelsIndexWithMapID(level.map.mapId), level.map.description)).findFirst();
            if (allParkingLevelOptional.isPresent()) {
                levels.add(0, allParkingLevelOptional.get());
            }
        }
        return levels;
    }

    /**
     * Returns a list denoting the number of matching tenants that exist per map level
     */
    public List<Integer> getTenantCountsPerLevel(List<? extends Tenant> tenants) {
        List<Integer> tenantCounts = new ArrayList<>();
        if (mapReady) {
            List<MapLevel> mapLevels = getMapLevels();
            for (int i = 0; i < mapLevels.size(); i++) {
                final int levelIndex = i;
                long tenantsOnLevel = StreamSupport.stream(tenants)
                        .filter(tenant -> StreamSupport.stream(getLevelsByLeaseId(tenant.getLeaseId()))
                                .filter(mapLevel -> mapLevel.getLevel() == levelIndex).count() > 0)
                        .count();
                tenantCounts.add((int) tenantsOnLevel);
            }
        }
        return tenantCounts;
    }

    /**
     * Returns the map level index for a given tenant's lease ID.
     * If tenant is found on multiple levels, the level matching the map's default level is returned
     */
    private int getLevelByLeaseId(int id) {
        int level = 0;
        if (mapReady) {
            int defaultLevel = map.getLevelsIndexWithMapID(map.getDefaultMap().map.mapId);
            List<MapLevel> mapLevels = getLevelsByLeaseId(id);
            if (!mapLevels.isEmpty() && getDestinationByLeaseId(id) != null) {
                boolean isDestinationOnDefaultLevel = StreamSupport.stream(mapLevels).filter(mapLevel -> mapLevel.getLevel() == defaultLevel).findFirst().isPresent();
                level = isDestinationOnDefaultLevel ? defaultLevel : map.getLevelsIndexWithMapID(map.getMapFullByDestinationId(getDestinationByLeaseId(id).id).map.mapId);
            } else {
                level = defaultLevel;
            }
        }
        return level;
    }

    /**
     * Returns a list of all level indexes where a given tenant is found
     */
    public List<MapLevel> getLevelsByLeaseId(int id) {
        List<MapLevel> levels = new ArrayList<>();
        if (mapReady) {
            Destination destination = getDestinationByLeaseId(id);
            if (destination != null) {
                List<Element> elements = Arrays.asList(map.getElementsOfDestination(destination.id));
                levels = StreamSupport.stream(elements).map(element -> new MapLevel(element.getLevel(), map.venueData.maps[element.getLevel()].map.name)).collect(Collectors.toList());
            }
        }
        return levels;
    }

    /**
     * Returns map destination matching a given tenant's lease ID.
     * If no destination is found, null is returned, which callers can check
     */
    private Destination getDestinationByLeaseId(int id) {
        if(map != null && map.venueData != null) {
            Optional<Destination> destination = StreamSupport.stream(Arrays.asList(map.venueData.destinations)).filter(d -> d.clientId.startsWith(String.valueOf(id))).findFirst();
            return destination.isPresent() ? destination.get() : null;
        } else {
            return null;
        }
    }

    /**
     * Returns whether or not a map destination exists for a given tenant
     */
    public boolean isDestinationMapped(int leaseId) {
        return (getDestinationByLeaseId(leaseId) != null);
    }

    /**
     * Returns whether or not a given tenant has a destination that allows for wayfind operations
     */
    public boolean isDestinationWayfindingEnabled(Tenant tenant) {
        MallConfig mallConfig = MallApplication.getApp().getMallManager().getMall().getMallConfig();
        return mallConfig.isWayfindingEnabled() && isDestinationMapped(tenant.getLeaseId()) && !tenant.isTemporarilyClosed();
    }

    /**
     * The following six methods are used to create and return displayable text denotations of location for tenants
     */
    private Integer getLeaseIdByDestination(Destination destination) {
        String leaseIdString = destination.clientId.split("-")[0];
        try {
            return Integer.parseInt(leaseIdString);
        } catch(NumberFormatException e) {
            Log.e(LOG_TAG, "Error parsing client id " + leaseIdString);
            return null;
        }
    }

    private String getLevelDescriptionByLeaseId(int id) {
        String levelDescription = "";
        Destination destination = getDestinationByLeaseId(id);
        if (destination != null) {
            if (ANCHOR_STORE_TYPE.equals(destination.destinationType)) {
                levelDescription = ANCHOR_STORE_LABEL;
            } else if (isMultiLevel()) {
                levelDescription = destination.level;
            }
        }
        return levelDescription;
    }

    private String getProximityDestinationByLeaseId(int id) {
        Destination[] proximityDestinations = map.getDestinationsOfProximities(getDestinationByLeaseId(id));
        return proximityDestinations != null && proximityDestinations.length > 0 ? proximityDestinations[0].name : "";
    }

    public String getTenantShortLocationByLeaseId(int id) {
        String location = "";
        if (mapReady) {
            int level = getLevelByLeaseId(id);
            String levelDescription = getLevelDescriptionByLeaseId(id);
            if (!StringUtils.isEmpty(levelDescription) && !levelDescription.equals(ANCHOR_STORE_LABEL)) {
                String levelShortDescription = map.venueData.maps[level].map.description;
                if (!StringUtils.isEmpty(levelShortDescription)) {
                    location = String.format("%s %s", onText, levelShortDescription);
                }
            }
        }
        return location;
    }

    public String getTenantLocationByLeaseId(int id) {
        String location = "";
        if (mapReady) {
            String levelDescription = getLevelDescriptionByLeaseId(id);
            String proximityDestination = getProximityDestinationByLeaseId(id);
            if (!StringUtils.isEmpty(levelDescription) || !StringUtils.isEmpty(proximityDestination)) {
                if (levelDescription.equals(ANCHOR_STORE_LABEL)) {
                    location = levelDescription;
                } else if (!StringUtils.isEmpty(levelDescription)) {
                            if (!StringUtils.isEmpty(proximityDestination)) {
                                location = String.format("%s, %s %s", levelDescription, nearText, proximityDestination);
                            } else {
                                location = levelDescription;
                            }
                } else if (!StringUtils.isEmpty(proximityDestination)) {
                    location = String.format("%s %s", nearTextCapitalized, proximityDestination);
                }
            }
        }
        return location;
    }

    public String getParkingLocationByLeaseId(int id) {
        String location = "";
        if (mapReady) {
            String proximityDestination = getProximityDestinationByLeaseId(id);
            if (!StringUtils.isEmpty(proximityDestination)) {
                String parkNearText = MallApplication.getApp().getString(R.string.park_near_text);
                location = parkNearText.concat(" ").concat(proximityDestination);
            }
        }
        return location;
    }

    /**
     * Called by the parking overview screen.
     * Generates and displays a static map snapshot set to default map bounds in the given image view.
     * The default map view bounds are used to include parking lots in the snapshot
     */
    public void renderParkingImage(ImageView imageView, EngineView engineView, Activity activity) {
        if (mapReady) {
            if (engineView != null) {
                map.setEngineView(engineView);
            }
            map.onResume();
            map.camera.setViewport(imageView.getWidth(), imageView.getHeight());
            map.camera.zoomTo(map.defaultFramings[getCurrentLevel()]);
            Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            map.renderToCanvas(canvas, () -> activity.runOnUiThread(() -> {
                imageView.setImageBitmap(bitmap);
                pause();
            }));
        }
    }

    /**
     * Called by the tenant and promotion detail screens.
     * Generates and displays a static map snapshot cropped to show the given tenant in the given image view.
     * The given tenant is shown as selected, and all other amenities and mover elements are hidden from view
     */
    public void renderTenantImage(int id, ImageView imageView, EngineView engineView, Activity activity) {
        if (mapReady) {
            if (engineView != null) {
                map.setEngineView(engineView);
            }
            map.onResume();
            map.camera.setViewport(imageView.getWidth(), imageView.getHeight());
            frameDestination(id);
            setSelection(id);
            toggleElementVisibility(false);
            Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            map.renderToCanvas(canvas, () -> activity.runOnUiThread(() -> {
                imageView.setImageBitmap(bitmap);
                toggleElementVisibility(true);
                pause();
            }));
        }
    }

    /**
     * The following two methods crop the map view's view bounds to display the given tenant,
     * and change the visible map level to show the level where the tenant exists
     */
    public void frameDestination(int id) {
        if (mapReady) {
            frameDestination(id, getLevelByLeaseId(id));
        }
    }

    private void frameDestination(int id, int level) {
        Destination destination = getDestinationByLeaseId(id);
        if (destination != null) {
            map.cameraToUnit(destination.id);
            setCurrentLevel(level);
        }
    }

    public void setStartWaypoint(int id, int level) {
        if (mapReady) {
            startWaypoint = getClosestWaypoint(level, getDestinationByLeaseId(id));
            mapStartPin.setLevel(getStartWaypointLevel());
            mapStartPin.getTransform().setTranslationX((float) startWaypoint.x);
            mapStartPin.getTransform().setTranslationY((float) startWaypoint.y);
            mapStartPin.setVisible(true);

            if (canStartWayfind()) {
                startWayfind();
            } else {
                handler.postDelayed(() -> frameDestination(id, level), 100);
            }
        }
    }

    public void setEndWaypoint(int id) {
        if (mapReady) {
            setEndWaypoint(id, getLevelByLeaseId(id));

            if (canStartWayfind()) {
                startWayfind();
            } else {
                handler.postDelayed(() -> frameDestination(id), 100);
            }
        }
    }

    private void setEndWaypoint(int id, int level) {
        if (mapEndPin != null) {
            endWaypoint = getClosestWaypoint(level, getDestinationByLeaseId(id));
            mapEndPin.setLevel(getEndWaypointLevel());
            mapEndPin.getTransform().setTranslationX((float) endWaypoint.x);
            mapEndPin.getTransform().setTranslationY((float) endWaypoint.y);
            mapEndPin.setVisible(true);
        }
    }

    /**
     * The following two methods are only used to toggle visibility of a pin
     * denoting the location of a given tenant by lease ID.
     * This is currently used on the parking availability screen in place of
     * the usual selected state of the tenant's destination map element
     */
    public void showMapMarkerPin(int id) {
        if (mapMarkerPin != null) {
            int level = getLevelByLeaseId(id);
            Waypoint waypoint = getClosestWaypoint(level, getDestinationByLeaseId(id));
            mapMarkerPin.setLevel(level);
            mapMarkerPin.getTransform().setTranslationX((float) waypoint.x);
            mapMarkerPin.getTransform().setTranslationY((float) waypoint.y);
            mapMarkerPin.setVisible(true);
        }
    }

    public void hideMapMarkerPin() {
        if (mapMarkerPin != null) {
            mapMarkerPin.setVisible(false);
        }
    }

    public int getStartWaypointLevel() {
        if (mapReady) {
            return startWaypoint != null ? map.getLevelIndexOfWaypointWithID(startWaypoint.id) : INVALID_LEVEL_POSITION;
        }
        return INVALID_LEVEL_POSITION;
    }

    public int getEndWaypointLevel() {
        if (mapReady) {
            return endWaypoint != null ? map.getLevelIndexOfWaypointWithID(endWaypoint.id) : INVALID_LEVEL_POSITION;
        }
        return INVALID_LEVEL_POSITION;
    }

    public void goToNextWayfindLevel() {
        if (mapReady) {
            int nextWayfindPathPosition = getWayfindPathPositionByLevel(getCurrentLevel()) + 1;
            if (nextWayfindPathPosition < map.getPathPerFloors().length) {
                setWayfindLevel(nextWayfindPathPosition);
            }
        }
    }

    public void goToPreviousWayfindLevel() {
        if (mapReady) {
            int previousWayfindPathPosition = getWayfindPathPositionByLevel(getCurrentLevel()) - 1;
            if (previousWayfindPathPosition >= 0) {
                setWayfindLevel(previousWayfindPathPosition);
            }
        }
    }

    /**
     * This method searches the current array of wayfind paths, one per floor that has a path, and finds the position in that array that represents the level index passed in.
     * For example, if a mall has map level indexes 0, 1, and 2, and we are navigating from A on level 2 to B on level 1,
     * this method will return the index of the pathPerFloors array that matches the current map level index.
     * That will allow the goToPreviousWayfindLevel or goToNextWayfindLevel methods to properly choose the right pathPerFloor index to use to navigate to the appropriate floor.
     * It also allows methods to determine if a given map level has a matching wayfind path on that floor.
     */
    private int getWayfindPathPositionByLevel(int level) {
        if (map.getPathPerFloors() != null) {
            List<PathPerFloor> pathPerFloors = Arrays.asList(map.getPathPerFloors());
            int mapId = map.venueData.maps[level].map.mapId;
            Optional<PathPerFloor> currentPathPerFloor = StreamSupport.stream(pathPerFloors).filter(pathPerFloor -> pathPerFloor.mapId == mapId).findFirst();
            return currentPathPerFloor.isPresent() ? pathPerFloors.indexOf(currentPathPerFloor.get()) : INVALID_LEVEL_POSITION;
        }
        return INVALID_LEVEL_POSITION;
    }

    public void resetStartWaypoint() {
        if (mapReady) {
            map.resetWayfind();
            startWaypoint = null;
            mapStartPin.setVisible(false);
        }
    }

    public void resetEndWaypoint() {
        if (mapReady) {
            map.resetWayfind();
            endWaypoint = null;
            mapEndPin.setVisible(false);
        }
    }

    public void resetWaypoints() {
        if (mapReady) {
            map.resetWayfind();
            toggleElementVisibility(true);
            resetStartWaypoint();
            resetEndWaypoint();
        }
    }

    private boolean canStartWayfind() {
        if (startWaypoint != null && endWaypoint != null) {
            WaypointEntityLink[] startWaypointEntities = startWaypoint.associations;
            WaypointEntityLink[] endWaypointEntities = endWaypoint.associations;
            return startWaypointEntities != null && endWaypointEntities != null
                   && startWaypointEntities.length > 0 && endWaypointEntities.length > 0
                   && startWaypointEntities[0].entityId != endWaypointEntities[0].entityId;
        }
        return false;
    }

    private void startWayfind() {
        Destination destination = map.getDestinationsOfWaypoint(endWaypoint)[0];
        setEndWaypoint(getLeaseIdByDestination(destination), getStartWaypointLevel());
        map.wayfind(startWaypoint, endWaypoint, false, (pathPerFloors) -> {
            if (pathPerFloors != null) {
                toggleElementVisibility(false);
                setWayfindLevel(0);
            }
        });
    }

    private void setWayfindLevel(int wayfindPathPosition) {
        setCurrentLevel(map.getLevelsIndexWithMapID(map.getPathPerFloors()[wayfindPathPosition].mapId));
    }

    private Waypoint getClosestWaypoint(int level, Destination destination) {
        int min = Integer.MAX_VALUE;
        Waypoint[] waypoints = map.getWaypointsOfDestination(destination);
        Waypoint closestWaypoint = waypoints[0];
        for (Waypoint waypoint : waypoints) {
            int waypointLevel = map.getLevelIndexOfWaypointWithID(waypoint.id);
            int diff = Math.abs(waypointLevel - level);
            if (diff == 0) {
                closestWaypoint = waypoint;
                break;
            } else if (diff < min) {
                min = diff;
                closestWaypoint = waypoint;
            }
        }
        return closestWaypoint;
    }

    private boolean isValidWaypointId(String waypointIdString) {
        return waypointIdString.matches(NUMBER_FORMAT_PATTERN) && map.getWaypointWithID(Integer.parseInt(waypointIdString)) != null;
    }

    /**
     * Creates and adds people mover elements to the map instance and map view.
     * Currently, these must be added manually by filtering them from the map venue data.
     * People movers include stairs, escalators, and elevators
     */
    private void setupPeopleMovers() {
        StreamSupport.stream(Arrays.asList(map.venueData.paths)).filter(path -> path.type != PathType.PATH_TYPE_NORMAL_PATH).forEach(path -> {
            for (int id : path.waypoints) {
                Optional<String> pathTypeName = StreamSupport.stream(Arrays.asList(map.venueData.pathTypes))
                        .filter(pathType -> pathType.pathTypeId == path.type).map(pathType -> pathType.typeName).findFirst();
                if (pathTypeName.isPresent()) {
                    Waypoint waypoint = map.getWaypointWithID(id);
                    int level = map.getLevelIndexOfWaypointWithID(waypoint.id);
                    IconShape iconShape = map.iconShapeLib.getIcon(pathTypeName.get());
                    MapMover mapMover = new MapMover(waypoint, iconShape, level);
                    map.addToMap(mapMover);
                }
            }
        });
    }

    /**
     * Creates a list of parking zones that are used to display heatmap polygons denoting parking availability.
     * These parking zones must be manually filtered from the list of map elements
     * and are not the same as ParkingLot objects
     */
    private void setupParkingZones() {
        MallConfig mallConfig = MallApplication.getApp().getMallManager().getMall().getMallConfig();
        if (mallConfig.isParkingAvailabilityEnabled()) {
            StreamSupport.stream(getParkingZones()).forEach(mapParkingZone -> {
                String waypointString = mapParkingZone.attributes.get(WAYPOINT_UNIT_KEY);
                waypointString = waypointString == null ? "" : waypointString;
                String[] waypointIdStrings = waypointString.split(",");
                List<Integer> waypointIds = StreamSupport.stream(Arrays.asList(waypointIdStrings))
                        .filter(this::isValidWaypointId).map(Integer::parseInt).collect(Collectors.toList());

                mapParkingZone.setParkingZoneLevels(StreamSupport.stream(waypointIds)
                        .map(waypointId -> map.getLevelIndexOfWaypointWithID(waypointId)).collect(Collectors.toList()));

                mapParkingZone.setParkingZoneIds(StreamSupport.stream(waypointIds)
                        .map(waypointId -> map.getWaypointWithID(waypointId).unitNumber)
                        .filter(Objects::nonNull).filter(unitNumber -> unitNumber.matches(NUMBER_FORMAT_PATTERN))
                        .map(Integer::parseInt).distinct().collect(Collectors.toList()));
            });
        }
    }

    /**
     * Returns a list of parking zones filtered from the list of all map elements
     */
    private List<MapParkingZone> getParkingZones() {
        List<MapParkingZone> mapParkingZones = new ArrayList<>();
        Element[] elements = map.getElementsByType(MapParkingZone.class);
        if (elements != null && elements.length > 0) {
            mapParkingZones = StreamSupport.stream(Arrays.asList(elements)).map(MapParkingZone.class::cast)
                    .filter(mapParkingZone -> mapParkingZone.classNameCSS.equals(PARKING_ZONE_DESCRIPTION)).collect(Collectors.toList());
        }
        return mapParkingZones;
    }

    public int getBestParkingLevel() {
        return StreamSupport.stream(getMapLevels()).map(MapLevel::getLevel).filter(this::hasParkingOnLevel).min(Integer::compare)
                .orElseGet(() -> map.getLevelsIndexWithMapID(map.getDefaultMap().map.mapId));
    }

    public int getAllParkingLevel() {
        List<MapFull> mapFulls = map != null && map.venueData != null  && map.venueData.maps != null ?
                Arrays.asList(map.venueData.maps) : new ArrayList<>();

        MapFull parkingMapFull = StreamSupport.stream(mapFulls)
                .filter(mapFull -> mapFull.map.floorSequence == ALL_PARKING_LEVEL).findFirst().orElse(map.getDefaultMap());
        try {
            return map.getLevelsIndexWithMapID(parkingMapFull.map.mapId);
        } catch (Exception e){
            Crashlytics.logException(e);
            return -1;
        }
    }

    public boolean hasParkingOnLevel(int level) {
        return StreamSupport.stream(getParkingZones()).filter(mapParkingZone -> mapParkingZone.isHighlighted() && mapParkingZone.getLevel() == level).findFirst().isPresent();
    }

    public void highlightParkingZones(Map<Integer, Integer> parkingZoneToColorMap) {
        StreamSupport.stream(getParkingZones()).forEach(mapParkingZone -> {
            mapParkingZone.resetColor();
            Optional<Integer> parkingZoneIdOptional = StreamSupport.stream(parkingZoneToColorMap.keySet())
                    .filter(parkingZoneId -> mapParkingZone.getParkingZoneIds().contains(parkingZoneId)).findFirst();
            if (parkingZoneIdOptional.isPresent()) {
                mapParkingZone.setColor(parkingZoneToColorMap.get(parkingZoneIdOptional.get()));
            }
        });
    }

    public void showParkingZones() {
        StreamSupport.stream(getParkingAmenities()).forEach(parkingAmenity -> parkingAmenity.setScale(1f));
        map.setHighlightStateByType(MapParkingZone.class, true);
    }

    public void hideParkingZones() {
        map.setHighlightStateByType(MapParkingZone.class, false);
        StreamSupport.stream(getParkingAmenities()).forEach(parkingAmenity -> parkingAmenity.setScale(0.5f));
    }

    public List<Integer> getClosestParkingZoneIdsByLeaseId(int id) {
        List<Integer> closestParkingZoneIds = new ArrayList<>();

        if (isDestinationMapped(id)) {
            Destination destination = getDestinationByLeaseId(id);
            Waypoint[] waypoints = map.getWaypointsOfDestination(destination.id);

            if (waypoints != null & waypoints.length > 0) {
                TreeMap<Float, List<Integer>> treeMap = new TreeMap<>();

                StreamSupport.stream(getParkingZones()).forEach(mapParkingZone -> {
                    float distance = getDistanceFromWaypoint(mapParkingZone, waypoints[0]);
                    treeMap.put(distance, mapParkingZone.getParkingZoneIds());
                });

                StreamSupport.stream(treeMap.entrySet()).forEach(entry -> closestParkingZoneIds.addAll(entry.getValue()));
            }
        }

        return closestParkingZoneIds;
    }

    private float getDistanceFromWaypoint(Element element, Waypoint waypoint) {
        Transform transform = element.getTransform();
        float dX = transform.getTranslationX() - (float) waypoint.x;
        float dY = transform.getTranslationY() - (float) waypoint.y;
        return (float) Math.sqrt(dX * dX + dY * dY);
    }

    private List<MapAmenity> getAmenities() {
        Element[] elements = map.getElementsByType(MapAmenity.class);
        return StreamSupport.stream(Arrays.asList(elements)).map(MapAmenity.class::cast).collect(Collectors.toList());
    }

    private List<MapAmenity> getParkingAmenities() {
        return StreamSupport.stream(getAmenities())
                .filter(mapAmenity -> mapAmenity.amenityComponent.bean.description.equals(PARKING_AMENITY_DESCRIPTION)).collect(Collectors.toList());
    }

    public void setFilteredAmenities(MapAmenityFilter amenityFilter) {
        if (mapReady) {
            mapAmenityFilter = amenityFilter;
            if (amenityFilter == null || amenityFilter.equals(MapAmenityFilter.NONE)) {
                resetAmenityFilters();
            } else {
                List<MapAmenity> parkingAmenities = getParkingAmenities();
                StreamSupport.stream(getAmenities()).filter(mapAmenity -> !parkingAmenities.contains(mapAmenity)).forEach(mapAmenity -> {
                    mapAmenity.setVisible(false);
                    boolean amenityMatchesDescription = mapAmenity.amenityComponent.bean.description.contains(amenityFilter.getAmenityFilter());
                    if (amenityMatchesDescription) {
                        mapAmenity.setScale(1.0f);
                        mapAmenity.setSelectState(true);
                        mapAmenity.setVisible(true);
                    } else {
                        mapAmenity.setScale(0.5f);
                        mapAmenity.setSelectState(false);
                    }
                });
            }
        }
    }

    private void resetAmenityFilters() {
        StreamSupport.stream(getAmenities()).forEach(mapAmenity -> {
            mapAmenity.setScale(0.5f);
            mapAmenity.setSelectState(false);
            mapAmenity.setVisible(true);
        });
    }

    public boolean hasAmenitiesOnLevel(int level) {
        return mapAmenityFilter != null &&
                !mapAmenityFilter.equals(MapAmenityFilter.NONE) &&
                StreamSupport.stream(getAmenities())
                        .filter(mapAmenity -> mapAmenity.isSelectState() && mapAmenity.getLevel() == level)
                        .findFirst()
                        .isPresent();
    }

    public boolean hasAmenitiesOfFilter(MapAmenityFilter amenityFilter) {
        return getAmenityCount(amenityFilter) > 0;
    }

    public int getAmenityCount(MapAmenityFilter amenityFilter) {
        return (int) StreamSupport.stream(getAmenities())
                .filter(mapAmenity -> mapAmenity.amenityComponent.bean.description.contains(amenityFilter.getAmenityFilter())).count();
    }

    /**
     * Returns an object sent as an event, letting listeners know the new level index,
     * as well as all of the pertinent wayfind information, if applicable
     */
    private MapLevelUpdateEvent getMapLevelUpdateEvent(int position) {
        MapLevelUpdateEvent mapLevelUpdateEvent = new MapLevelUpdateEvent(position);
        int wayfindPathPosition = getWayfindPathPositionByLevel(position);
        if (wayfindPathPosition != INVALID_LEVEL_POSITION) {
            mapLevelUpdateEvent.setWayfindLevel(true);
            mapLevelUpdateEvent.setStartWayfindLevel(position == getStartWaypointLevel());
            mapLevelUpdateEvent.setEndWayfindLevel(position == getEndWaypointLevel());
            ArrayList<ArrayList<TDInstruction>> textDirections = map.getTextDirectionInstruction(true, WAYFIND_UTURN_DISTANCE, WAYFIND_MIN_DISTANCE);
            if (textDirections != null) {
                ArrayList<TDInstruction> floorTextDirections = textDirections.get(wayfindPathPosition);
                TDInstruction textDirection = floorTextDirections.get(floorTextDirections.size() - 1);
                mapLevelUpdateEvent.setTextDirection(textDirection.output);
                mapLevelUpdateEvent.setMapMoverType(MapMoverType.fromString(textDirection.moverType));
            }
        }
        return mapLevelUpdateEvent;
    }

    private static String getLanguage(){
        return LocaleUtils.getCurrentLanguageCode();
    }

    private void setAllLabels(M m) {
        for (Element element : m.getElementsByType(MapLabel.class)) {
            MapLabel mapLabel = (MapLabel) element;
            mapLabel.setLabelName("");
        }
    }
}