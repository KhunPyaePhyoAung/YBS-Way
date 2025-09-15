package me.khun.ybsway.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

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
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.khun.ybsway.R;
import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.component.BusListToRoutePageItemClickListener;
import me.khun.ybsway.component.BusStopInfoWindow;
import me.khun.ybsway.component.BusStopMarker;
import me.khun.ybsway.component.RelatedBusListDialog;
import me.khun.ybsway.entity.Coordinate;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;
import me.khun.ybsway.viewmodel.BaseMapViewModel;

public class ActivityBaseMap extends ActivityBase implements MapListener, MapEventsReceiver, Marker.OnMarkerClickListener {
    public static final double YANGON_LATITUDE = 16.866931;
    public static final double YANGON_LONGITUDE = 96.172709;
    public static final double MAX_NORTH_LATITUDE = 18.400445;
    public static final double MAX_SOUTH_LATITUDE = 15.573180;
    public static final double MAX_WEST_LONGITUDE = 95.121037;
    public static final double MAX_EAST_LONGITUDE = 97.253402;
    public static final double DEFAULT_MAP_ZOOM_LEVEL = 13;
    public static final double MIN_ZOOM_LEVEL = 10;
    public static final double MAX_ZOOM_LEVEL = 22;
    public static final double PROPER_ZOOM_LEVEL = 17;
    public static final long ZOOM_ANIMATION_SPEED = 1500L;
    protected static final int LOCATION_PERMISSIONS_REQUEST_CODE = 1;
    protected static final int LOCATION_PERMISSIONS_FORCE_SETTING_REQUEST_CODE = 2;
    protected static final int STORAGE_PERMISSIONS_FORCE_SETTING_REQUEST_CODE = 3;
    protected static final int ICON_RESET_INTERVAL_MILLS = 250;
    protected static final int STATIC_MARKER_SIZE_IN_DP = 45;
    protected static final int LOCATION_PERSON_DRAWABLE_ID = R.drawable.my_location_person_icon;
    protected static final int LOCATION_DIRECTION_DRAWABLE_ID = R.drawable.test;

    protected final Map<Integer, Drawable> busStopIconMap = new HashMap<>();
    protected final List<BusStopMarker> busStopMarkerList = new ArrayList<>(YBSWayApplication.DEFAULT_BUS_STOP_LIST_SIZE);
    protected final List<String> MAP_OVERLAY_SORTED_LIST = List.of("event", "bus_route", "bus_stops", "my_location");
    protected final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    protected final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    protected BaseMapViewModel baseMapViewModel;
    protected MapView mapView;
    protected IMapController mapController;
    protected MyLocationNewOverlay mLocationOverlay;
    protected BusStopView selectedBusStopView;
    protected List<BusView> relatedBusList;
    protected View relatedBusBtnWrapper;
    protected ImageButton btnGPS;
    protected ImageButton btnRelatedBus;
    protected TextView badgeRelatedBus;
    protected Drawable busStopIcon;
    protected GeoPoint mapCenterPoint;
    protected BroadcastReceiver gpsSwitchReceiver;
    protected Marker nearestMarker;
    protected BusListToRoutePageItemClickListener busItemClickListener;
    protected int currentZoomLevel;
    protected volatile boolean isLoadingBusStops = false;
    protected boolean showDynamicInfoWindow = false;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Runnable pendingIconUpdateRunnable;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        requestPermissionsIfNecessary(STORAGE_PERMISSIONS, STORAGE_PERMISSIONS_FORCE_SETTING_REQUEST_CODE);

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
        busStopIconMap.put(6, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_6dp));
        busStopIconMap.put(4, AppCompatResources.getDrawable(getApplicationContext(), R.drawable.bus_stop_icon_4dp));

        baseMapViewModel = new BaseMapViewModel(YBSWayApplication.busMapper, YBSWayApplication.busService);
        baseMapViewModel.getSelectedBusStopData().observe(this, busStopView -> {
            selectedBusStopView = busStopView;
        });
        baseMapViewModel.getRelatedBusStopData().observe(this, busViewList -> {
            relatedBusList = busViewList;
            syncRelatedBusData();
        });
        busItemClickListener = new BusListToRoutePageItemClickListener(this);
    }

    protected void setupGpsButton(@IdRes int btnGpsId) {
        btnGPS = findViewById(btnGpsId);

        btnGPS.setOnClickListener(view -> {
            if (isLocationServiceOn()) {
                mLocationOverlay.enableFollowLocation();
                double zoomLevel = Math.max(PROPER_ZOOM_LEVEL, mapView.getZoomLevelDouble());
                mapController.animateTo(mLocationOverlay.getMyLocation(), zoomLevel, ZOOM_ANIMATION_SPEED);
            } else {
                turnOnLocationService(true);
            }
        });
    }

    protected void setupRelatedBusComponent(@IdRes int btnWrapperId, @IdRes int btnRelatedBusId, @IdRes int badgeRelatedBusId) {
        relatedBusBtnWrapper = findViewById(btnWrapperId);
        btnRelatedBus = findViewById(btnRelatedBusId);
        badgeRelatedBus = findViewById(badgeRelatedBusId);

        relatedBusBtnWrapper.setVisibility(View.GONE);
        btnRelatedBus.setOnClickListener(view -> {
            showRelatedBusListDialog();
        });
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
        mapView.getOverlays().add(MAP_OVERLAY_SORTED_LIST.indexOf("event"), mapEventsOverlay);

        Polyline roadOverlay = new Polyline(mapView);
        mapView.getOverlays().add(MAP_OVERLAY_SORTED_LIST.indexOf("bus_route"), roadOverlay);

        FolderOverlay markerOverlay = new FolderOverlay();
        mapView.getOverlays().add(MAP_OVERLAY_SORTED_LIST.indexOf("bus_stops"), markerOverlay);


        Bitmap personBitmap = getBitmapFromDrawable(this, LOCATION_PERSON_DRAWABLE_ID, STATIC_MARKER_SIZE_IN_DP);
        Bitmap busBitmap = getBitmapFromDrawable(this, LOCATION_DIRECTION_DRAWABLE_ID, STATIC_MARKER_SIZE_IN_DP);
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.setPersonIcon(personBitmap);
        mLocationOverlay.setPersonAnchor(0.5f, 1f);
        mLocationOverlay.setDirectionIcon(busBitmap);
        mLocationOverlay.setDirectionAnchor(0.5f, 0.5f);
        mapView.getOverlays().add(MAP_OVERLAY_SORTED_LIST.indexOf("my_location"), mLocationOverlay);

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
            dpUnit = 28;
        } else if (zoomLevel > 16) {
            dpUnit = 24;
        } else if (zoomLevel > 15) {
            dpUnit = 20;
        } else if (zoomLevel > 14) {
            dpUnit = 16;
        } else if (zoomLevel > 13) {
            dpUnit = 12;
        } else if (zoomLevel > 12) {
            dpUnit = 10;
        } else if (zoomLevel > 11) {
            dpUnit = 6;
        } else if (zoomLevel > 10) {
            dpUnit = 4;
        } else {
            dpUnit = 4;
        }

        return busStopIconMap.get(dpUnit);
    }

    protected void drawRoute(BusView busView) {
        if (busView == null || busView.getRouteCoordinateList().isEmpty()) {
            return;
        }

        int roadWidth = 15;
        int roadColor = getResources().getColor(R.color.bus_route);

        Polyline roadOverlay = (Polyline) mapView.getOverlays().get(MAP_OVERLAY_SORTED_LIST.indexOf("bus_route"));
        roadOverlay.getOutlinePaint().setStrokeWidth(roadWidth);
        roadOverlay.getOutlinePaint().setColor(roadColor);
        roadOverlay.getOutlinePaint().setStrokeJoin(Paint.Join.ROUND);

        List<Coordinate> coordinateList = busView.getRouteCoordinateList();

        if (coordinateList == null || coordinateList.isEmpty()) {
            return;
        }

        for (Coordinate coordinate : busView.getRouteCoordinateList()) {
            GeoPoint point = new GeoPoint(coordinate.getLatitude(), coordinate.getLongitude());
            roadOverlay.addPoint(point);
            roadOverlay.setInfoWindow(null);
        }

        List<BusStopView> busStopViewList = busView.getBusStopViewList();
        drawBusStops(busStopViewList);
    }

    protected void drawBusStops(List<BusStopView> busStopViewList) {

        if (busStopViewList.isEmpty()) {
            return;
        }

        isLoadingBusStops = true;
        FolderOverlay markerOverlay = (FolderOverlay) mapView.getOverlays().get(MAP_OVERLAY_SORTED_LIST.indexOf("bus_stops"));
        List<BusStopMarker> markerList = new ArrayList<>();

        for (BusStopView bs : busStopViewList) {
            BusStopMarker busStopMarker = createBusStopMarker(bs);
            busStopMarker.setOnMarkerClickListener(this);
            markerList.add(busStopMarker);
        }
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Queue<BusStopMarker> queue = new LinkedList<>(markerList);

        int refreshEveryBatch = 1;
        int[] counter = {0};
        int batchSize = 10; // add 30 per tick
        executor.scheduleWithFixedDelay(() -> {
            if (!queue.isEmpty()) {
                List<BusStopMarker> batch = new ArrayList<>();
                for (int i = 0; i < batchSize && !queue.isEmpty(); i++) {
                    batch.add(queue.poll());
                }
                mainHandler.post(() -> {
                    for (BusStopMarker stop : batch) {
                        markerOverlay.add(stop);
                    }
                    counter[0]++;
                    if (counter[0] % refreshEveryBatch == 0) {
                        mapView.invalidate();
                    }
                });
            } else {
                executor.shutdown();
                synchronized (busStopMarkerList) {
                    busStopMarkerList.clear();
                    busStopMarkerList.addAll(markerList);
                }
                mapView.invalidate();
                mainHandler.post(this::postDrawBusStops);
            }
        }, 0, 5, TimeUnit.MILLISECONDS);
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

        if (nearestMarker == newNearestMarker) {
            return false;
        }

        nearestMarker = newNearestMarker;

        if (nearestMarker != null && showDynamicInfoWindow) {
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
        System.out.println("Zoom Level : " + zoomLevel);
        if (currentZoomLevel != zoomLevel) {
            currentZoomLevel = zoomLevel;

            if (pendingIconUpdateRunnable != null) {
                mainHandler.removeCallbacks(pendingIconUpdateRunnable);
            }

            pendingIconUpdateRunnable = () -> {
                resetBusStopIcons();
                mapView.postInvalidateOnAnimation();
            };
            mainHandler.postDelayed(pendingIconUpdateRunnable, ICON_RESET_INTERVAL_MILLS);
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
        afterBusStopMarkerIsShown(busStopMarker);
    }

    protected void createBusStopInfoWindowIfNull(BusStopMarker busStopMarker) {
        if (busStopMarker.getInfoWindow() == null) {
            BusStopInfoWindow infoWindow = new BusStopInfoWindow(mapView, busStopMarker.getBusStop());
            infoWindow.setOnClickListener(view -> {
                closeAllInfoWindow();
            });
            busStopMarker.setInfoWindow(infoWindow);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gpsSwitchReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                    syncStates();
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(gpsSwitchReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        mapView.onResume();
        syncStates();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(gpsSwitchReceiver);
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
        }

        return true;
    }

    protected void closeAllInfoWindow() {
        InfoWindow.closeAllInfoWindowsOn(mapView);
        afterInfoWindowClosed();
    }

    protected void afterInfoWindowClosed() {
        showDynamicInfoWindow = false;
        baseMapViewModel.setSelectedBusStop(null);
        baseMapViewModel.setSelectedBusStop(null);
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        closeAllInfoWindow();
        return true;
    }

    protected void afterBusStopMarkerIsShown(BusStopMarker busStopMarker) {
        showDynamicInfoWindow = true;
        baseMapViewModel.setSelectedBusStop(busStopMarker.getBusStop());
    }

    protected void syncRelatedBusData() {
        if (relatedBusList == null || relatedBusList.isEmpty()) {
            relatedBusBtnWrapper.setVisibility(View.GONE);
            return;
        }

        relatedBusBtnWrapper.setVisibility(View.VISIBLE);
        badgeRelatedBus.setText(String.format("%s", relatedBusList.size()));
    }

    protected void showRelatedBusListDialog() {
        RelatedBusListDialog dialog = new RelatedBusListDialog(this);
        dialog.setBusStop(selectedBusStopView);
        dialog.setRelatedBusList(relatedBusList);
        busItemClickListener.setBusList(relatedBusList);
        dialog.setOnItemClickListener(busItemClickListener);
        dialog.show();
    }

    protected void turnOnLocationService(boolean forceToSetting) {
        int requestCode = forceToSetting ?
                LOCATION_PERMISSIONS_FORCE_SETTING_REQUEST_CODE
                : LOCATION_PERMISSIONS_REQUEST_CODE;
        requestPermissionsIfNecessary(LOCATION_PERMISSIONS, requestCode);

        if (isLocationPermissionGranted()) {
            promptEnableGpsIfOff();
        }
        syncStates();
    }

    protected void syncStates() {
        if (isLocationServiceOn()) {
            btnGPS.setActivated(true);
            mLocationOverlay.enableMyLocation();
        } else {
            btnGPS.setActivated(false);
            mLocationOverlay.disableMyLocation();
        }
    }

    protected void requestPermissionsIfNecessary(String[] permissions, int requestCode) {
        List<String> permissionsToRequest = new ArrayList<>(permissions.length);

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                     this,
                            permissionsToRequest.toArray(new String[0]),
                            requestCode
                    );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    // Permission denied → just return
                    return;
                }
            }
            // Location permission granted
            promptEnableGpsIfOff();
        } else if (requestCode == LOCATION_PERMISSIONS_FORCE_SETTING_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // Permission denied
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        showLocationPermissionSettingsDialog();
                    }
                    return;
                }
            }

            // Location permission granted
            promptEnableGpsIfOff();
        }

        syncStates();
    }

    protected void openLocationSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    protected void showLocationPermissionSettingsDialog() {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle(R.string.location_permission_required_title)
                .setMessage(R.string.location_permission_required_message)
                .setPositiveButton(R.string.location_permission_required_confirm, (dialog, which) -> {
                    openLocationSettings();
                })
                .setNegativeButton(R.string.location_permission_required_deny, (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    protected boolean isLocationServiceOn() {
        return isLocationPermissionGranted() && isGpsOn();
    }

    protected boolean isLocationPermissionGranted() {
        for (String permission : LOCATION_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    protected boolean isGpsOn() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    protected void promptEnableGpsIfOff() {
        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, 1001);
                } catch (IntentSender.SendIntentException sendEx) {
                    new AlertDialog.Builder(this, R.style.MyDialogTheme)
                            .setTitle(R.string.gps_prompt_error_title)
                            .setMessage(R.string.gps_prompt_error_message)
                            .setNegativeButton(R.string.gps_prompt_error_action, (dialog, which) -> dialog.dismiss())
                            .setCancelable(false)
                            .show();
                }
            }
            syncStates();
        });
    }

}
