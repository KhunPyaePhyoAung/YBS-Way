package me.khun.ybsway.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.osmdroid.util.GeoPoint;

import java.util.Objects;

import me.khun.ybsway.R;
import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.component.BusListToRoutePageItemClickListener;
import me.khun.ybsway.component.BusStopMarker;
import me.khun.ybsway.component.CompactBusStopListAdapter;
import me.khun.ybsway.databinding.ActivityBusRouteBinding;
import me.khun.ybsway.view.BusView;
import me.khun.ybsway.viewmodel.BusRouteViewModel;

public class ActivityBusRoute extends ActivityBaseMap {

    private Bundle bundle;
    private BusRouteViewModel busRouteViewModel;
    private ActionBar actionBar;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Drawable arrowDropDownDrawable;
    private Drawable arrowDropUpDrawable;

    private ActivityBusRouteBinding viewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityBusRouteBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        initViews();
        initObjects();
        initListeners();

        bundle = getIntent().getExtras();
        if ( bundle != null) {
            String routeId = bundle.getString("route_id");
            busRouteViewModel.loadBusDataByRouteId(routeId);
        }

    }

    private void initViews() {
        setupMap(R.id.map_view);
        setupActionToggleButtons(R.id.btn_yps_toggle, R.id.btn_anchor_toggle);
        setupGpsButton(R.id.btn_gps);
        setupZoomButtons(R.id.btn_zoom_in, R.id.btn_zoom_out);

        setSupportActionBar(viewBinding.toolBar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        viewBinding.recyclerBusStop.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
        );
        Drawable dividerDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.compact_ordered_bus_stop_list_item_divider, null);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(dividerDrawable));
        viewBinding.recyclerBusStop.addItemDecoration(dividerItemDecoration);

        arrowDropUpDrawable = AppCompatResources.getDrawable(this, R.drawable.arrow_drop_up);
        arrowDropDownDrawable = AppCompatResources.getDrawable(this, R.drawable.arrow_drop_down);

        bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomSheet);
    }

    private void initObjects() {
        BusRouteViewModel.Dependencies dependencies = new BusRouteViewModel.Dependencies();
        dependencies.busMapper = YBSWayApplication.busMapper;
        dependencies.busStopMapper = YBSWayApplication.busStopMapper;
        dependencies.busService = YBSWayApplication.busService;
        dependencies.busStopService = YBSWayApplication.busStopService;
        busRouteViewModel = new BusRouteViewModel(dependencies);
    }

    private void initListeners() {
        viewBinding.toolBar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        bottomSheetBehavior.addBottomSheetCallback(new BusStopListBottomSheetCallback());

        viewBinding.sheetStateView.setOnClickListener(view -> onSheetStateViewClick());

        busRouteViewModel.getToolbarTitle().observe(this, actionBar::setTitle);
        busRouteViewModel.getBusData().observe(this, this::onBusDataLoaded);
    }

    @Override
    protected void postDrawBusStops() {
        super.postDrawBusStops();
        animateToCenterBusStop();
    }

    @Override
    protected BusListToRoutePageItemClickListener getOnRelatedBusItemClickListener() {
        BusListToRoutePageItemClickListener busItemClickListener = new BusListToRoutePageItemClickListener(this);
        busItemClickListener.postRunnable(this::finish);
        return busItemClickListener;
    }

    private void onBusDataLoaded(BusView busView) {
        drawRoute(busView);
        CompactBusStopListAdapter adapter = new CompactBusStopListAdapter(busView.getBusStopViewList());
        adapter.setOnItemClickListener((busStopView, itemView, position) -> {
            viewBusStopOnMap(busStopView);
        });
        viewBinding.recyclerBusStop.setAdapter(adapter);
    }

    private void onSheetStateViewClick() {
        int state = bottomSheetBehavior.getState();
        if (state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void animateToCenterBusStop() {
        if (!busStopMarkerList.isEmpty()) {
            BusStopMarker centerStopMarker = busStopMarkerList.get(busStopMarkerList.size() / 4);
            GeoPoint centerGeoPoint = centerStopMarker.getPosition();
            mapController.animateTo(centerGeoPoint);
        }
    }

    private class BusStopListBottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {
        private int initialSheetTop = -1;
        private final ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mapView.getLayoutParams();

        @Override
        public void onStateChanged(@NonNull View sheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                viewBinding.ivSheetArrowDrop.setImageDrawable(arrowDropUpDrawable);
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                viewBinding.ivSheetArrowDrop.setImageDrawable(arrowDropDownDrawable);
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
    }

}
