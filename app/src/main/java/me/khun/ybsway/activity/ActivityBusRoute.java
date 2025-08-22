package me.khun.ybsway.activity;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.List;

import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.R;
import me.khun.ybsway.custom.BusStopInfoWindow;
import me.khun.ybsway.custom.BusStopMarker;
import me.khun.ybsway.entity.Coordinate;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;
import me.khun.ybsway.viewmodel.BusRouteViewModel;

public class ActivityBusRoute extends ActivityBaseMap implements Marker.OnMarkerClickListener {

    private List<BusStopMarker> busStopMarkerList;
    private BusMapper busMapper;
    private BusStopMapper busStopMapper;
    private BusService busService ;
    private BusStopService busStopService ;
    private BusRouteViewModel busRouteViewModel;
    private Marker nearestMarker;
    private boolean showDynamicInfoWindow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        map = findViewById(R.id.map);
        setupMap(map);

        busMapper = YBSWayApplication.busMapper;
        busStopMapper = YBSWayApplication.busStopMapper;
        busService = YBSWayApplication.busService;
        busStopService = YBSWayApplication.busStopService;
        busStopMarkerList = new ArrayList<>(YBSWayApplication.DEFAULT_BUS_STOP_LIST_SIZE);
        busRouteViewModel = new BusRouteViewModel(busMapper, busStopMapper, busService, busStopService);

        busRouteViewModel.getToolbarTitle().observe(this, toolbar::setTitle);
        busRouteViewModel.getBusData().observe(this, this::drawRoute);

        Bundle bundle = getIntent().getExtras();
        if ( bundle != null) {
            String routeId = bundle.getString("route_id");
            busRouteViewModel.loadBusDataByRouteId(routeId);
        }
    }

    private void drawRoute(BusView busView) {
        if ( busView == null || busView.getRouteCoordinateList().isEmpty() ) {
            return;
        }

        int roadWidth = 15;
        int roadColor = getResources().getColor(R.color.bus_route);

        Polyline roadOverlay = new Polyline(map);
        roadOverlay.getOutlinePaint().setStrokeWidth(roadWidth);
        roadOverlay.getOutlinePaint().setColor(roadColor);
        roadOverlay.getOutlinePaint().setStrokeJoin(Paint.Join.ROUND);
        map.getOverlays().add(roadOverlay);

        List<Coordinate> coordinateList = busView.getRouteCoordinateList();

        if ( coordinateList == null || coordinateList.isEmpty() ) {
            return;
        }

        for ( Coordinate coordinate : busView.getRouteCoordinateList() ) {
            GeoPoint point = new GeoPoint(coordinate.getLatitude(), coordinate.getLongitude());
            roadOverlay.addPoint(point);
            roadOverlay.setInfoWindow(null);
        }

        List<BusStopView> busStopViewList = busView.getBusStopViewList();
        for ( BusStopView busStop : busStopViewList ) {
            BusStopMarker busStopMarker = getBusStopMarker(busStop);
            busStopMarker.setOnMarkerClickListener(this);
            BusStopInfoWindow infoWindow = (new BusStopInfoWindow(map, busStop));
            infoWindow.setOnClickListener(view -> {
                infoWindow.close();
                showDynamicInfoWindow = false;
            });
            busStopMarker.setInfoWindow(infoWindow);
            map.getOverlays().add(busStopMarker);
            busStopMarkerList.add(busStopMarker);
        }

        BusStopView centerStop = busStopViewList.get(busStopViewList.size() / 4);
        GeoPoint centerGeoPoint = new GeoPoint(centerStop.getLatitude(), centerStop.getLongitude());
        mapController.animateTo(centerGeoPoint);
        map.invalidate();
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        super.onZoom(event);
        for (BusStopMarker busStopMarker : busStopMarkerList) {
            busStopMarker.setIcon(busStopIcon);
            busStopMarker.resetAnchor(0.5f, 0f);
        }
        return true;
    }

    @Override
    public boolean onScroll(ScrollEvent event) {
        super.onScroll(event);
        Marker newNearestMarker = findNearestMarker(mapCenterPoint, busStopMarkerList);

        if ( nearestMarker == newNearestMarker ) {
            return false;
        }

        nearestMarker = newNearestMarker;

        if ( nearestMarker != null && showDynamicInfoWindow) {
            InfoWindow.closeAllInfoWindowsOn(map);
            nearestMarker.showInfoWindow();
        }
        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {
        InfoWindow.closeAllInfoWindowsOn(mapView);
        mapController.animateTo(marker.getPosition());
        marker.showInfoWindow();
        showDynamicInfoWindow = true;
        return true;
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(map);
        showDynamicInfoWindow = false;
        return true;
    }

}
