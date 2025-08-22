package me.khun.ybsway.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;

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

import java.util.List;

import me.khun.ybsway.R;
import me.khun.ybsway.custom.BusStopMarker;
import me.khun.ybsway.view.BusStopView;

public class ActivityBaseMap  extends BaseActivity implements MapListener, MapEventsReceiver {
    public static final double YANGON_LATITUDE = 16.851544;
    public static final double YANGON_LONGITUDE = 96.176099;
    public static final double MAX_NORTH_LATITUDE = 18.400445;
    public static final double MAX_SOUTH_LATITUDE = 15.573180;
    public static final double MAX_WEST_LONGITUDE = 95.121037;
    public static final double MAX_EAST_LONGITUDE = 97.253402;
    public static final double DEFAULT_MAP_ZOOM_LEVEL = 13;
    public static final double MIN_ZOOM_LEVEL = 10;
    public static final double MAX_ZOOM_LEVEL = 22;

    protected MapView map ;
    protected IMapController mapController;
    protected int currentZoomLevel;
    protected Drawable busStopIcon;
    protected GeoPoint mapCenterPoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    }

    protected void setupMap(MapView map) {
        mapController = map.getController();
        mapController.setZoom(DEFAULT_MAP_ZOOM_LEVEL);
        mapController.setCenter(new GeoPoint(YANGON_LATITUDE, YANGON_LONGITUDE));

        map.setTileSource(getTileSource());
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        map.setMinZoomLevel(MIN_ZOOM_LEVEL);
        map.setMaxZoomLevel(MAX_ZOOM_LEVEL);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);
        map.setScrollableAreaLimitLatitude(MAX_NORTH_LATITUDE, MAX_SOUTH_LATITUDE, 0);
        map.setScrollableAreaLimitLongitude(MAX_WEST_LONGITUDE, MAX_EAST_LONGITUDE, 0);
        map.addMapListener(this);
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(0, mapEventsOverlay);

        currentZoomLevel = map.getZoomLevel();
        mapCenterPoint = new GeoPoint(map.getMapCenter().getLatitude(), map.getMapCenter().getLongitude());
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

    protected BusStopMarker getBusStopMarker(BusStopView busStop) {
        BusStopMarker busStopMarker = new BusStopMarker(map, busStop);
        GeoPoint point = new GeoPoint(busStop.getLatitude(), busStop.getLongitude());
        busStopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        busStopMarker.setPosition(point);
        busStopMarker.setIcon(busStopIcon);
        busStopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        busStopMarker.setVisible(busStopIcon != null);
        return busStopMarker;
    }

    protected Drawable getScaledBusStopIcon(int zoomLevel) {
        int drawableId;

        if (zoomLevel > 20) {
            drawableId = R.drawable.bus_stop_icon_44dp;
        } else if (zoomLevel > 19) {
            drawableId = R.drawable.bus_stop_icon_40dp;
        } else if (zoomLevel > 18) {
            drawableId = R.drawable.bus_stop_icon_36dp;
        } else if (zoomLevel > 17) {
            drawableId = R.drawable.bus_stop_icon_32dp;
        } else if (zoomLevel > 16) {
            drawableId = R.drawable.bus_stop_icon_28dp;
        } else if (zoomLevel > 15) {
            drawableId = R.drawable.bus_stop_icon_24dp;
        } else if (zoomLevel > 14) {
            drawableId = R.drawable.bus_stop_icon_20dp;
        } else if (zoomLevel > 13) {
            drawableId = R.drawable.bus_stop_icon_16dp;
        } else if (zoomLevel > 12) {
            drawableId = R.drawable.bus_stop_icon_12dp;
        } else if (zoomLevel > 11) {
            drawableId = R.drawable.bus_stop_icon_10dp;
        } else {
            drawableId = R.drawable.bus_stop_icon_8dp;
        }

        return AppCompatResources.getDrawable(getApplicationContext(), drawableId);
    }

    @Override
    public boolean onScroll(ScrollEvent event) {
        mapCenterPoint = new GeoPoint(map.getMapCenter().getLatitude(), map.getMapCenter().getLongitude());
        return true;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        int zoomLevel = (int) event.getZoomLevel();
        if ( currentZoomLevel != zoomLevel ) {
            currentZoomLevel = (int) event.getZoomLevel();
            busStopIcon = getScaledBusStopIcon(currentZoomLevel);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
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
}
