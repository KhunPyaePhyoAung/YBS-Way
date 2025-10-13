package me.khun.ybsway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.khun.ybsway.R;
import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.component.BusListAdapter;
import me.khun.ybsway.databinding.ActivityBusLineListBinding;
import me.khun.ybsway.provider.TextProvider;
import me.khun.ybsway.view.BusSearchState;
import me.khun.ybsway.view.BusView;
import me.khun.ybsway.viewmodel.BusListViewModel;

public class ActivityBusList extends ActivityBase {

    private BusListViewModel busViewModel;
    private ActionBar actionBar;
    private BusListAdapter busListAdapter;
    private BusSearchHintProvider busSearchHintProvider;

    private ActivityBusLineListBinding viewBinding;

    protected final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityBusLineListBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        initViews();
        initObjects();
        initListeners();

        busViewModel.loadBusData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeSoftInput(viewBinding.busSearchInput.getWindowToken());
        viewBinding.busSearchInput.clearFocus();
    }

    private void initViews() {
        setSupportActionBar(viewBinding.toolBar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        viewBinding.toolBar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        busListAdapter = new BusListAdapter();
        viewBinding.busList.setAdapter(busListAdapter);
        viewBinding.busList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
        );
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.bus_list_divider, null)));
        viewBinding.busList.addItemDecoration(dividerItemDecoration);
    }

    private void initObjects() {
        BusListViewModel.Dependencies dependencies = new BusListViewModel.Dependencies();
        dependencies.busMapper = YBSWayApplication.busMapper;
        dependencies.busService = YBSWayApplication.busService;
        busViewModel = new BusListViewModel(dependencies);
        busSearchHintProvider = new BusSearchHintProvider();
    }

    private void initListeners() {
        viewBinding.busSearchInput.addTextChangedListener(new BusSearchInputTextWatcher());
        viewBinding.btnClearSearch.setOnClickListener(view -> onBtnClearBusSearchInputClick());
        busListAdapter.setItemClickListener(new BusListItemClickListener());
        busViewModel.getAllBusListData().observe(this, this::onBusListLoaded);
        busViewModel.getBusSearchStateData().observe(this, this::onBusSearchStateChanged);
        busSearchHintProvider.setConsumer(this::onBusSearchInputHintChanged);
    }

    private void onBusListLoaded(List<BusView> busViewList) {
        busListAdapter.changeData(busViewList);
    }

    private void searchBus(String query) {
        busViewModel.searchBus(query);
        viewBinding.busList.scrollToPosition(0);
    }

    private void onBusSearchStateChanged(BusSearchState busSearchState) {
        switch (busSearchState.status) {
            case ALL_RESULTS:
                showAllBusResults(busSearchState.results);
                break;
            case NO_RESULTS:
                showNoBusResults();
                break;
            case RESULTS:
                showBusResults(busSearchState.results);
        }
    }

    private void onBtnClearBusSearchInputClick() {
        viewBinding.busSearchInput.getText().clear();
    }

    private void onBusSearchInputHintChanged(String hint) {
        mainHandler.post(() -> viewBinding.busSearchInput.setHint(hint));
    }

    private void showAllBusResults(List<BusView> busViewList) {
        viewBinding.noBusFoundView.setVisibility(View.GONE);
        busListAdapter.changeData(busViewList);
    }

    private void showNoBusResults() {
        viewBinding.noBusFoundView.setVisibility(View.VISIBLE);
        busListAdapter.changeData(Collections.emptyList());
    }

    private void showBusResults(List<BusView> busViewList) {
        viewBinding.noBusFoundView.setVisibility(View.GONE);
        busListAdapter.changeData(busViewList);
    }

    private class BusListItemClickListener implements BusListAdapter.ItemClickListener {

        @Override
        public void onItemClick(BusView busView, View view, int position) {
            Intent intent = new Intent(ActivityBusList.this, ActivityBusRoute.class);
            intent.putExtra("route_id", busView.getRouteId());
            startActivity(intent);
        }
    }

    private class BusSearchInputTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            searchBus(viewBinding.busSearchInput.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    protected class BusSearchHintProvider extends TextProvider {
        private final List<String> hintList = new ArrayList<>();
        private ScheduledExecutorService scheduledExecutorService;
        private int currentIndex = 0;
        private final int INTERVAL_SECOND = 2;

        public BusSearchHintProvider() {
            hintList.add(getResources().getString(R.string.bus_search_input_hint_1));
            hintList.add(getResources().getString(R.string.bus_search_input_hint_2));
            hintList.add(getResources().getString(R.string.bus_search_input_hint_3));
            hintList.add(getResources().getString(R.string.bus_search_input_hint_4));

            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                if (hintList.isEmpty()) {
                    return;
                }
                if (currentIndex >= hintList.size()) {
                    currentIndex = 0;
                }
                String text = hintList.get(currentIndex++);
                provide(text);
            }, 0, INTERVAL_SECOND, TimeUnit.SECONDS);
        }
    }

}
