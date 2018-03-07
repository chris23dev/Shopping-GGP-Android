package com.ggp.theclub.fragment;

import android.os.Bundle;
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
import de.greenrobot.event.EventBus;

public abstract class MapFragment extends BaseFragment {
    @Bind(R.id.map_layout) FrameLayout mapLayout;
    @Bind(R.id.map_status_view) TextView mapStatusView;
    @Bind(R.id.engine_view) EngineView engineView;
    @Bind(R.id.level_list_view) RecyclerView levelList;
    protected MapLevelAdapter mapLevelAdapter = new MapLevelAdapter();
    protected MapManager mapManager = MapManager.getInstance();
    protected MapState mapState = new MapState();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onFragmentVisible() {
        updateMapLayout();
    }

    @Override
    public void onFragmentInvisible() {
        mapState = mapManager.getMapState();
        mapManager.pause();
    }

    private void updateMapLayout() {
        mapManager.resume(mapState, engineView);
        if (mapManager.isMapReady()) {
            if(mapLayout != null) {
                mapLayout.setVisibility(View.VISIBLE);
            }
            if (mapManager.isMultiLevel() && !isLevelListInitialized() && levelList != null) {
                mapLevelAdapter.setMapLevels(mapManager.getMapLevels());
                levelList.setLayoutManager(new LinearLayoutManager(levelList.getContext(), LinearLayoutManager.VERTICAL, true));
                levelList.setAdapter(mapLevelAdapter);
            }
        }
    }

    private boolean isLevelListInitialized() {
        return levelList != null && levelList.getAdapter() != null;
    }

    protected void enableMapSelection() {
        mapState.setSelectionEnabled(true);
    }

    public void onEvent(MapReadyEvent event) {
        if (event.isMapReady()) {
            if (getUserVisibleHint() && isResumed()) {
                updateMapLayout();
            }
        } else {
            mapStatusView.setText(R.string.map_error);
        }
    }

    public void onEvent(MapLevelUpdateEvent event) {
            mapLevelAdapter.updateLevel(event.getLevel());
    }
}