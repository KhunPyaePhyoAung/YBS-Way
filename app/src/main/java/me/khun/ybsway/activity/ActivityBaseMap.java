package me.khun.ybsway.activity;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.khun.ybsway.R;
import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.custom.BusStopInfoWindow;
import me.khun.ybsway.custom.BusStopMarker;
import me.khun.ybsway.entity.Coordinate;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;

public class ActivityBaseMap  extends ActivityBase implements MapListener, MapEventsReceiver, Marker.OnMarkerClickListener {
    public static final double YANGON_LATITUDE = 16.851544;
    public static final double YANGON_LONGITUDE = 96.176099;
    public static final double MAX_NORTH_LATITUDE = 18.400445;
    public static final double MAX_SOUTH_LATITUDE = 15.573180;
    public static final double MAX_WEST_LONGITUDE = 95.121037;
    public static final double MAX_EAST_LONGITUDE = 97.253402;
    public static final double DEFAULT_MAP_ZOOM_LEVEL = 13;
    public static final double MIN_ZOOM_LEVEL = 10;
    public static final double MAX_ZOOM_LEVEL = 22;
    public static final double PROPER_ZOOM_LEVEL = 16;
    public static final long ZOOM_ANIMATION_SPEED = 1500L;

    protected final Map<Integer, Drawable> busStopIconMap = new HashMap<>();
    protected MapView mapView;
    protected IMapController mapController;
    protected int currentZoomLevel;
    protected Drawable busStopIcon;
    protected GeoPoint mapCenterPoint;
    protected boolean showDynamicInfoWindow = false;
    protected final List<BusStopMarker> busStopMarkerList = new ArrayList<>(YBSWayApplication.DEFAULT_BUS_STOP_LIST_SIZE);
    protected Marker nearestMarker;
    protected volatile boolean isLoadingBusStops = false;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        busStopIconMap.put(48, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_48dp));
        busStopIconMap.put(44, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_44dp));
        busStopIconMap.put(40, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_40dp));
        busStopIconMap.put(36, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_36dp));
        busStopIconMap.put(32, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_32dp));
        busStopIconMap.put(28, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_28dp));
        busStopIconMap.put(24, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_24dp));
        busStopIconMap.put(20, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_20dp));
        busStopIconMap.put(16, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_16dp));
        busStopIconMap.put(12, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_12dp));
        busStopIconMap.put(10, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_10dp));
        busStopIconMap.put(8, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_8dp));
    }

    protected void setupMap(@IdRes int mapViewId) {
        mapView = findViewById(mapViewId);
        mapController = mapView.getController();
        mapController.setZoom(DEFAULT_MAP_ZOOM_LEVEL);
        mapController.setCenter(new GeoPoint(YANGON_LATITUDE, YANGON_LONGITUDE));

        mapView.setTileSource(getTileSource());
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(MIN_ZOOM_LEVEL);
        mapView.setMaxZoomLevel(MAX_ZOOM_LEVEL);
        mapView.setHorizontalMapRepetitionEnabled(false);
        mapView.setVerticalMapRepetitionEnabled(false);
        mapView.setScrollableAreaLimitLatitude(MAX_NORTH_LATITUDE, MAX_SOUTH_LATITUDE, 0);
        mapView.setScrollableAreaLimitLongitude(MAX_WEST_LONGITUDE, MAX_EAST_LONGITUDE, 0);
        mapView.addMapListener(this);
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        mapView.getOverlays().add(0, mapEventsOverlay);

        currentZoomLevel = mapView.getZoomLevel();
        mapCenterPoint = new GeoPoint(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
        busStopIcon = getScaledBusStopIcon(currentZoomLevel);
    }

    protected ITileSource getTileSource() {
        ITileSource tileSource = new XYTileSource("Mapnik",
                (int) MIN_ZOOM_LEVEL, (int) MAX_ZOOM_LEVEL, 256, ".png", new String[]{
                "https://tile.openstreetmap.org/"}, "© OpenStreetMap contributors",
                new TileSourcePolicy(2,
                        TileSourcePolicy.FLAG_NO_BULK
                                | TileSourcePolicy.FLAG_NO_PREVENTIVE
                                | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                                | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
                ));
        
        return tileSource;
    }

    protected BusStopMarker createBusStopMarker(BusStopView busStop) {
        BusStopMarker busStopMarker = new BusStopMarker(mapView, busStop);
        GeoPoint point = new GeoPoint(busStop.getLatitude(), busStop.getLongitude());
        busStopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        busStopMarker.setPosition(point);
        busStopMarker.setIcon(busStopIcon);
        busStopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        busStopMarker.setVisible(busStopIcon != null);
        return busStopMarker;
    }

    protected Drawable getScaledBusStopIcon(int zoomLevel) {
        int dpUnit;

        if (zoomLevel > 21) {
            dpUnit = 48;
        } else if (zoomLevel > 20) {
            dpUnit = 44;
        } else if (zoomLevel > 19) {
            dpUnit = 40;
        } else if (zoomLevel > 18) {
            dpUnit = 36;
        } else if (zoomLevel > 17) {
            dpUnit = 32;
        } else if (zoomLevel > 16) {
            dpUnit = 28;
        } else if (zoomLevel > 15) {
            dpUnit = 24;
        } else if (zoomLevel > 14) {
            dpUnit = 20;
        } else if (zoomLevel > 13) {
            dpUnit = 16;
        } else if (zoomLevel > 12) {
            dpUnit = 12;
        } else if (zoomLevel > 11) {
            dpUnit = 10;
        } else {
            dpUnit = 8;
        }

        return busStopIconMap.get(dpUnit);
    }

    protected void drawRoute(BusView busView) {
        if ( busView == null || busView.getRouteCoordinateList().isEmpty() ) {
            return;
        }

        int roadWidth = 15;
        int roadColor = getResources().getColor(R.color.bus_route);

        Polyline roadOverlay = new Polyline(mapView);
        roadOverlay.getOutlinePaint().setStrokeWidth(roadWidth);
        roadOverlay.getOutlinePaint().setColor(roadColor);
        roadOverlay.getOutlinePaint().setStrokeJoin(Paint.Join.ROUND);
        mapView.getOverlays().add(roadOverlay);

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
        drawBusStops(busStopViewList);

        BusStopView centerStop = busStopViewList.get(busStopViewList.size() / 4);
        GeoPoint centerGeoPoint = new GeoPoint(centerStop.getLatitude(), centerStop.getLongitude());
        mapController.animateTo(centerGeoPoint);
        mapView.invalidate();
    }

    protected void drawBusStops(List<BusStopView> busStopViewList) {

        new Thread(() -> {
            isLoadingBusStops = true;
            List<BusStopMarker> markerList = new ArrayList<>();
            for ( BusStopView busStop : busStopViewList ) {
                BusStopMarker busStopMarker = createBusStopMarker(busStop);
                busStopMarker.setOnMarkerClickListener(this);
                markerList.add(busStopMarker);
                mainHandler.post(() -> {
                    mapView.getOverlays().add(busStopMarker);
                    mapView.invalidate();
                });
            }

            synchronized (busStopMarkerList) {
                busStopMarkerList.clear();
                busStopMarkerList.addAll(markerList);
            }

            mainHandler.post(this::postDrawBusStops);
        }).start();
    }

    protected void postDrawBusStops() {
        isLoadingBusStops = false;
        resetBusStopIcons();
        mapView.invalidate();
    }

    @Override
    public boolean onScroll(ScrollEvent event) {
        mapCenterPoint = new GeoPoint(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
        Marker newNearestMarker = findNearestMarker(mapCenterPoint, busStopMarkerList);

        if ( nearestMarker == newNearestMarker ) {
            return false;
        }

        nearestMarker = newNearestMarker;

        if ( nearestMarker != null && showDynamicInfoWindow) {
            InfoWindow.closeAllInfoWindowsOn(mapView);
            if (nearestMarker instanceof BusStopMarker) {
                BusStopMarker busStopMarker = (BusStopMarker) nearestMarker;
                showBusStopInfoWindow(busStopMarker);
            }
        }
        return true;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        int zoomLevel = (int) event.getZoomLevel();
        if ( currentZoomLevel != zoomLevel ) {
            currentZoomLevel = (int) event.getZoomLevel();
            resetBusStopIcons();
            return true;
        }
        return false;
    }

    protected void resetBusStopIcons() {
        if (isLoadingBusStops) {
            return;
        }

        busStopIcon = getScaledBusStopIcon(currentZoomLevel);
        for (BusStopMarker busStopMarker : busStopMarkerList) {
            busStopMarker.setIcon(busStopIcon);
            busStopMarker.resetAnchor(0.5f, 0f);
        }
    }

    protected void showBusStopInfoWindow(BusStopMarker busStopMarker) {
        createBusStopInfoWindowIfNull(busStopMarker);
        busStopMarker.showInfoWindow();
    }

    protected void createBusStopInfoWindowIfNull(BusStopMarker busStopMarker) {
        if (busStopMarker.getInfoWindow() == null) {
            BusStopInfoWindow infoWindow = new BusStopInfoWindow(mapView, busStopMarker.getBusStop());
            infoWindow.setOnClickListener(view -> {
                infoWindow.close();
                showDynamicInfoWindow = false;
            });
            busStopMarker.setInfoWindow(infoWindow);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    public Marker findNearestMarker(GeoPoint target, List<? extends Marker> markers) {
        if (markers.isEmpty()) {
            return null;
        }

        Marker nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Marker marker : markers) {
            double distance = target.distanceToAsDouble(marker.getPosition()); // in meters
            if (distance < minDistance) {
                minDistance = distance;
                nearest = marker;
            }
        }

        return nearest;
    }

    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {
        InfoWindow.closeAllInfoWindowsOn(mapView);
        if (mapView.getZoomLevelDouble() > PROPER_ZOOM_LEVEL) {
            mapController.animateTo(marker.getPosition());
        } else {
            mapController.animateTo(marker.getPosition(), PROPER_ZOOM_LEVEL, ZOOM_ANIMATION_SPEED);
        }

        if (marker instanceof BusStopMarker) {
            BusStopMarker busStopMarker = (BusStopMarker) marker;
            showBusStopInfoWindow(busStopMarker);
            showDynamicInfoWindow = true;
        }

        return true;
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(mapView);
        showDynamicInfoWindow = false;
        return true;
    }
}
