package me.khun.ybsway.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.views.overlay.Marker;

import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.R;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.viewmodel.BusRouteViewModel;

public class ActivityBusRoute extends ActivityBaseMap implements Marker.OnMarkerClickListener {

    private BusMapper busMapper;
    private BusStopMapper busStopMapper;
    private BusService busService ;
    private BusStopService busStopService ;
    private BusRouteViewModel busRouteViewModel;
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        setupMap(R.id.map_view);

        busMapper = YBSWayApplication.busMapper;
        busStopMapper = YBSWayApplication.busStopMapper;
        busService = YBSWayApplication.busService;
        busStopService = YBSWayApplication.busStopService;
        busRouteViewModel = new BusRouteViewModel(busMapper, busStopMapper, busService, busStopService);

        busRouteViewModel.getToolbarTitle().observe(this, actionBar::setTitle);
        busRouteViewModel.getBusData().observe(this, this::drawRoute);

        Bundle bundle = getIntent().getExtras();
        if ( bundle != null) {
            String routeId = bundle.getString("route_id");
            busRouteViewModel.loadBusDataByRouteId(routeId);
        }
    }
}
