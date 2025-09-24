package me.khun.ybsway.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.Objects;

import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.R;
import me.khun.ybsway.component.BusListToRoutePageItemClickListener;
import me.khun.ybsway.component.BusStopMarker;
import me.khun.ybsway.component.CompactBusStopListAdapter;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.viewmodel.BusRouteViewModel;

public class ActivityBusRoute extends ActivityBaseMap implements Marker.OnMarkerClickListener {

    private Bundle bundle;
    private BusMapper busMapper;
    private BusStopMapper busStopMapper;
    private BusService busService ;
    private BusStopService busStopService ;
    private BusRouteViewModel busRouteViewModel;
    private ActionBar actionBar;
    private LinearLayout fabContainer;
    private View bottomSheet;
    private AppCompatImageView ivSheetArrowDrop;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private RecyclerView rcBusStopList;
    private View sheetStateView;
    private Drawable arrowDropDownDrawable;
    private Drawable arrowDropUpDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);

        setupMap(R.id.map_view);
        setupGpsButton(R.id.btn_gps);
        setupZoomButtons(R.id.btn_zoom_in, R.id.btn_zoom_out);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        fabContainer = findViewById(R.id.fab_container);
        rcBusStopList = findViewById(R.id.recycler_bus_stop);
        rcBusStopList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
        );
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.compact_ordered_bus_stop_list_item_divider));
        rcBusStopList.addItemDecoration(dividerItemDecoration);

        arrowDropUpDrawable = AppCompatResources.getDrawable(this, R.drawable.arrow_drop_up);
        arrowDropDownDrawable = AppCompatResources.getDrawable(this, R.drawable.arrow_drop_down);

        ivSheetArrowDrop = findViewById(R.id.iv_sheet_arrow_drop);
        sheetStateView = findViewById(R.id.sheet_state_view);
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            private int initialSheetTop = -1;
            private final ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mapView.getLayoutParams();

            @Override
            public void onStateChanged(@NonNull View sheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    ivSheetArrowDrop.setImageDrawable(arrowDropUpDrawable);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    ivSheetArrowDrop.setImageDrawable(arrowDropDownDrawable);
                }
                doOnSlide(sheet);
            }

            @Override
            public void onSlide(@NonNull View sheet, float slideOffset) {
                doOnSlide(sheet);
            }

            private void doOnSlide(View sheet) {
                if (initialSheetTop == -1) {
                    initialSheetTop = sheet.getTop();
                }
                int dy = initialSheetTop - sheet.getTop();
                params.bottomMargin = dy;
                mapView.setLayoutParams(params);
            }

        });

        sheetStateView.setOnClickListener(view -> {
            int state = bottomSheetBehavior.getState();
            if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        busMapper = YBSWayApplication.busMapper;
        busStopMapper = YBSWayApplication.busStopMapper;
        busService = YBSWayApplication.busService;
        busStopService = YBSWayApplication.busStopService;
        busRouteViewModel = new BusRouteViewModel(busMapper, busStopMapper, busService, busStopService);

        busRouteViewModel.getToolbarTitle().observe(this, actionBar::setTitle);
        busRouteViewModel.getBusData().observe(this, busView -> {
            drawRoute(busView);
            CompactBusStopListAdapter adapter = new CompactBusStopListAdapter(busView.getBusStopViewList());
            adapter.setOnItemClickListener((busStopView, position) -> {
                for (BusStopMarker marker : busStopMarkerList) {
                    BusStopView markerStop = marker.getBusStop();
                    if (Objects.equals(markerStop.getId(), busStopView.getId())) {
                        onMarkerClick(marker, mapView);
                        return;
                    }
                }
            });
            rcBusStopList.setAdapter(adapter);
        });

        bundle = getIntent().getExtras();
        if ( bundle != null) {
            String routeId = bundle.getString("route_id");
            busRouteViewModel.loadBusDataByRouteId(routeId);
        }

    }

    @Override
    protected void postDrawBusStops() {
        super.postDrawBusStops();
        if (!busStopMarkerList.isEmpty()) {
            BusStopMarker centerStopMarker = busStopMarkerList.get(busStopMarkerList.size() / 4);
            GeoPoint centerGeoPoint = centerStopMarker.getPosition();
            mapController.animateTo(centerGeoPoint);
        }
    }

    @Override
    protected BusListToRoutePageItemClickListener getOnRelatedBusItemClickListener() {
        BusListToRoutePageItemClickListener busItemClickListener = new BusListToRoutePageItemClickListener(this);
        busItemClickListener.postRunnable(this::finish);
        return busItemClickListener;
    }

}
