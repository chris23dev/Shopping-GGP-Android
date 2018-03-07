package com.ggp.theclub.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ggp.theclub.R;
import com.ggp.theclub.adapter.MapLevelAdapter;
import com.ggp.theclub.event.MapLevelUpdateEvent;
import com.ggp.theclub.event.MapReadyEvent;
import com.ggp.theclub.manager.MapManager;
import com.ggp.theclub.model.MapState;
import com.jibestream.jibestreamandroidlibrary.main.EngineView;

import butterknife.Bind;

public abstract class MapActivity extends BaseActivity {
    @Bind(R.id.map_layout) FrameLayout mapLayout;
    @Bind(R.id.map_status_view) TextView mapStatusView;
    @Bind(R.id.engine_view) EngineView engineView;
    @Bind(R.id.level_list_view) RecyclerView levelList;
    protected MapLevelAdapter mapLevelAdapter = new MapLevelAdapter();
    protected MapManager mapManager = MapManager.getInstance();
    protected MapState mapState = new MapState();

    @Override
    public void onResume() {
        super.onResume();
        updateMapLayout();
    }

    @Override
    public void onPause() {
        mapState = mapManager.getMapState();
        mapManager.pause();
        super.onPause();
    }

    private void updateMapLayout() {
        mapManager.resume(mapState, engineView);
        if (mapManager.isMapReady()) {
            mapLayout.setVisibility(View.VISIBLE);
            if (displayLevelSelector() && mapManager.isMultiLevel() && levelList.getAdapter() == null) {
                mapLevelAdapter.setMapLevels(mapManager.getMapLevels());
                levelList.setLayoutManager(new LinearLayoutManager(levelList.getContext(), LinearLayoutManager.VERTICAL, true));
                levelList.setAdapter(mapLevelAdapter);
            }
        }
    }

    public void onEvent(MapReadyEvent event) {
        if (event.isMapReady()) {
            updateMapLayout();
        } else {
            mapStatusView.setText(R.string.map_error);
        }
    }

    public void onEvent(MapLevelUpdateEvent event) {
            mapLevelAdapter.updateLevel(event.getLevel());
    }

    protected boolean displayLevelSelector() {
        return true;
    }
}