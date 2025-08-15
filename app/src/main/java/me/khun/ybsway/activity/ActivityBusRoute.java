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

import me.khun.ybsway.YBSWayApplication;
import me.khun.ybsway.R;
import me.khun.ybsway.custom.BusStopInfoWindow;
import me.khun.ybsway.custom.BusStopMarker;
import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.entity.Coordinate;
import me.khun.ybsway.entity.Route;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.service.BusStopService;

public class ActivityBusRoute extends ActivityBaseMap implements Marker.OnMarkerClickListener {

    private List<BusStopMarker> busStopMarkerList;
    private BusService busService ;
    private BusStopService busStopService ;
    private Marker nearestMarker;
    private boolean showDynamicInfoWindow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);

        busService = YBSWayApplication.busService;
        busStopService = YBSWayApplication.busStopService;

        Toolbar toolbar = findViewById(R.id.tool_bar);
        map = findViewById(R.id.map);
        setupMap(map);

        Bundle bundle = getIntent().getExtras();

        if ( bundle != null) {
            String routeId = bundle.getString("route_id");
            Bus bus = busService.findOneByRouteId(routeId);
            if (bus != null) {
                toolbar.setTitle(bus.getRouteId());
                drawRoute(bus);
            }
        }
    }

    private void drawRoute(Bus bus) {
        if ( bus == null || bus.getRouteCoordinateList().isEmpty() ) {
            return;
        }

        int roadWidth = 15;
        int roadColor = getResources().getColor(R.color.bus_route);
        Route route = bus.getRoute();

        Polyline roadOverlay = new Polyline(map);
        roadOverlay.getOutlinePaint().setStrokeWidth(roadWidth);
        roadOverlay.getOutlinePaint().setColor(roadColor);
        roadOverlay.getOutlinePaint().setStrokeJoin(Paint.Join.ROUND);
        map.getOverlays().add(roadOverlay);

        List<Coordinate> coordinateList = bus.getRouteCoordinateList();

        if ( coordinateList == null || coordinateList.isEmpty() ) {
            return;
        }

        Coordinate centerCoordinate = coordinateList.get(coordinateList.size() / 4);
        for ( Coordinate coordinate : bus.getRouteCoordinateList() ) {
            GeoPoint point = new GeoPoint(coordinate.getLatitude(), coordinate.getLongitude());
            roadOverlay.addPoint(point);
            roadOverlay.setInfoWindow(null);
        }

        List<BusStop> busStopList = route.getBusStopList();

        if ( busStopList == null || busStopList.isEmpty() ) {
            mapController.animateTo(new GeoPoint(centerCoordinate.getLatitude(), centerCoordinate.getLongitude()));
            return;
        }

        busStopMarkerList = new ArrayList<>(busStopList.size());

        for ( BusStop busStop : busStopList ) {
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

        BusStop centerStop = busStopList.get(busStopList.size() / 4);
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
