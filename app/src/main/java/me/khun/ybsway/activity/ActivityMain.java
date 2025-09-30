package me.khun.ybsway.activity;

import static me.khun.ybsway.application.YBSWayApplication.*;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import me.khun.ybsway.R;
import me.khun.ybsway.component.BusListToRoutePageItemClickListener;
import me.khun.ybsway.component.BusStopListViewAdapter;
import me.khun.ybsway.component.BusStopSearchHistoryAdapter;
import me.khun.ybsway.databinding.ActivityMainBinding;
import me.khun.ybsway.view.BusStopSearchHistoryItem;
import me.khun.ybsway.view.BusStopSearchState;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.viewmodel.MainViewModel;

public class ActivityMain extends ActivityBaseMap implements NavigationView.OnNavigationItemSelectedListener {

    private MainViewModel mainViewModel;
    private BusStopListViewAdapter busStopListAdapter;
    private BusStopSearchHistoryAdapter searchHistoryAdapter;
    private BusStopSearchHintProvider busStopSearchHintProvider;

    private ActivityMainBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        initViews();
        initObjects();
        initListeners();

        mainViewModel.loadAllBusStopsData();
        mainViewModel.loadBusStopSearchHistory();
    }

    @Override
    protected void postDrawBusStops() {
        super.postDrawBusStops();
        viewBinding.loadingContainer.setVisibility(View.GONE);
        viewBinding.busStopInput.setEnabled(true);
        viewBinding.btnSearchRoute.setEnabled(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int menuId = item.getItemId();
        Intent intent = null;

        if (menuId == R.id.nav_home) {
            viewBinding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (menuId == R.id.nav_bus_lines) {
            intent = new Intent(this, ActivityBusList.class);
        } else if (menuId == R.id.nav_language_settings) {
            intent = new Intent(this, ActivityLanguageSetting.class);
        } else if (menuId == R.id.nav_about) {
            intent = new Intent(this, ActivityAbout.class);
        }

        startActivity(intent);
        item.setChecked(true);
        viewBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewBinding.navView.setCheckedItem(R.id.nav_home);
    }

    @Override
    protected BusListToRoutePageItemClickListener getOnRelatedBusItemClickListener() {
        BusListToRoutePageItemClickListener busItemClickListener = new BusListToRoutePageItemClickListener(this);
        return busItemClickListener;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewBinding.navView.setCheckedItem(R.id.nav_home);
    }

    private void initViews() {
        setupMap(R.id.map_view);
        setupGpsButton(R.id.btn_gps);
        setupZoomButtons(R.id.btn_zoom_in, R.id.btn_zoom_out);

        viewBinding.navView.setVisibility(View.VISIBLE);
        viewBinding.busStopInput.setEnabled(false);
        viewBinding.btnSearchRoute.setEnabled(false);
        viewBinding.rcBusStopSearchResult.setLayoutManager(new LinearLayoutManager(this));
        hideSearchOverlay();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
        );
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.bus_stop_search_result_list_divider, null)));
        viewBinding.rcBusStopSearchResult.addItemDecoration(dividerItemDecoration);
        getOnBackPressedDispatcher().addCallback(this, new ActivityMainOnBackPressedCallback(true));
    }

    private void initObjects() {
        MainViewModel.Dependencies mainViewModelDependencies = new MainViewModel.Dependencies();
        mainViewModelDependencies.busStopMapper = busStopMapper;
        mainViewModelDependencies.busStopService = busStopService;
        mainViewModelDependencies.busStopSearchHistoryManager = busStopSearchHistoryManager;
        mainViewModel = new MainViewModel(mainViewModelDependencies);
        mainViewModel.getAllBusStopsData().observe(this, this::onBusStopsLoaded);

        busStopListAdapter = new BusStopListViewAdapter();
        viewBinding.rcBusStopSearchResult.setAdapter(busStopListAdapter);

        searchHistoryAdapter = new BusStopSearchHistoryAdapter(this, Collections.emptyList());
        viewBinding.busStopSearchHistoryListView.setAdapter(searchHistoryAdapter);

        busStopSearchHintProvider = new BusStopSearchHintProvider();
    }

    private void initListeners() {
        viewBinding.navView.setNavigationItemSelectedListener(this);

        busStopSearchHintProvider.setConsumer(this::onBusStopSearchInputHintChanged);

        viewBinding.btnNavToggle.setOnClickListener(v -> {
            onNavToggleButtonClick();
        });

        viewBinding.busStopInput.setOnFocusChangeListener((view, hasFocus) -> {
            onBusStopSearchInputFocusChanged(hasFocus);
        });

        viewBinding.busStopInput.setOnEditorActionListener(
                (v, actionId, event) -> onBusStopSearchInputActionEvent(actionId, event)
        );

        viewBinding.busStopInput.addTextChangedListener(new BusStopSearchInputTextWatcher());

        viewBinding.btnClearSearch.setOnClickListener(view -> {
            onBusStopSearchInputClearButtonClick();
        });

        viewBinding.btnClearBusStopSearchHistory.setOnClickListener(view -> {
            onBusStopSearchHistoryClearButtonClick();
        });

        viewBinding.btnSearchRoute.setOnClickListener(view -> {
            onSearchRouteButtonClick();
        });

        busStopListAdapter.setOnItemClickListener(this::onBusStopResultItemClick);

        searchHistoryAdapter.setOnItemClickListener((searchItem, itemView, position) -> {
            onBusStopSearchHistoryItemClick(searchItem);
        });

        mainViewModel.getBusStopSearchStateData().observe(this, this::onBusStopSearchResultStateChanged);

        mainViewModel.getBusStopSearchHistoryData().observe(this, this::onBusStopSearchHistoryChanged);
    }

    private void onNavToggleButtonClick() {
        if (viewBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            viewBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            viewBinding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void onBusStopSearchInputHintChanged(String hint) {
        mainHandler.post(() -> viewBinding.busStopInput.setHint(hint));
    }

    private void onBusStopSearchInputFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            showSearchOverlay();
            searchBusStop();
        }
    }

    private boolean onBusStopSearchInputActionEvent(int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN)) {

            closeSoftInput(viewBinding.busStopInput.getWindowToken());
            searchBusStop();
        }
        return true;
    }

    private void onBusStopSearchInputClearButtonClick() {
        viewBinding.busStopInput.requestFocus();
        viewBinding.busStopInput.getText().clear();
        searchBusStop();
        showSoftInput(viewBinding.busStopInput);
    }

    private void onBusStopSearchHistoryItemClick(BusStopSearchHistoryItem searchItem) {
        BusStopView busStopView = searchItem.getBusStopView();
        hideSearchOverlay();
        viewBusStopOnMap(busStopView);
        mainViewModel.addBusStopSearchHistory(searchItem);
    }

    private void onBusStopSearchHistoryClearButtonClick() {
        showClearBusStopHistoryConfirmDialog();
    }

    private void onSearchRouteButtonClick() {
        closeSoftInput(viewBinding.busStopInput.getWindowToken());
        searchBusStop();
    }

    private void showSearchOverlay() {
        if (viewBinding.searchContainer.getVisibility() == View.VISIBLE) {
            return;
        }

        viewBinding.searchContainer.setVisibility(View.VISIBLE);
        viewBinding.searchContainer.setAlpha(0f);
        viewBinding.searchContainer.animate().alpha(1f).setDuration(300).start();
        viewBinding.busStopSearchHistoryListView.smoothScrollToPosition(0);
    }

    private void hideSearchOverlay() {
        viewBinding.searchContainer.setVisibility(View.GONE);
        viewBinding.searchContainer.animate().alpha(0f).setDuration(300).start();
        viewBinding.busStopInput.clearFocus();
        closeSoftInput(viewBinding.busStopInput.getWindowToken());
    }

    private void onBusStopsLoaded(List<BusStopView> busStopViewList) {
        drawBusStops(busStopViewList);
    }

    private void onBusStopResultItemClick(BusStopView busStopView, View itemView, int position) {
        hideSearchOverlay();
        viewBusStopOnMap(busStopView);
        String searchText = busStopView.formatText();
        viewBinding.busStopInput.setText(searchText);
        mainViewModel.addBusStopSearchHistory(new BusStopSearchHistoryItem(busStopView));
    }

    private void onBusStopSearchResultStateChanged(BusStopSearchState state) {
        switch (state.status) {
            case EMPTY_QUERY:
                hideBusStopSearchResults();
                break;
            case NO_RESULTS:
                showNoBusStopSearchResults();
                break;
            case RESULTS:
                showBusStopSearchResults(state.results);
                break;
        }
    }

    private void onBusStopSearchHistoryChanged(List<BusStopSearchHistoryItem> searchHistoryItems) {
        if (searchHistoryItems.isEmpty()) {
            viewBinding.busStopHistoryContainer.setVisibility(View.GONE);
        } else {
            viewBinding.busStopHistoryContainer.setVisibility(View.VISIBLE);
        }
        searchHistoryAdapter.changeData(searchHistoryItems);
    }

    private void searchBusStop() {
        String query = viewBinding.busStopInput.getText().toString();
        mainViewModel.searchBusStops(query);
        viewBinding.rcBusStopSearchResult.scrollToPosition(0);
    }

    private void hideBusStopSearchResults() {
        viewBinding.busStopResultContainer.setVisibility(View.GONE);
    }

    private void showNoBusStopSearchResults() {
        busStopListAdapter.changeData(Collections.emptyList());
        viewBinding.busStopResultContainer.setVisibility(View.VISIBLE);
        viewBinding.tvBusStopSearchResultTitle.setVisibility(View.GONE);
        viewBinding.tvNoBusStopFound.setVisibility(View.VISIBLE);
    }

    private void showBusStopSearchResults(List<BusStopView> searchResults) {
        busStopListAdapter.changeData(searchResults);
        viewBinding.busStopResultContainer.setVisibility(View.VISIBLE);
        viewBinding.tvBusStopSearchResultTitle.setVisibility(View.VISIBLE);
        viewBinding.tvNoBusStopFound.setVisibility(View.GONE);
    }

    private void showClearBusStopHistoryConfirmDialog() {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle(R.string.clear_search_history_confirm_title)
                .setMessage(R.string.clear_search_history_confirm_message)
                .setPositiveButton(R.string.clear_search_history_confirm_positive_message, (dialog, which) -> {
                    mainViewModel.clearBusStopSearchHistory();
                })
                .setNegativeButton(R.string.clear_search_history_confirm_negative_message, (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private class BusStopSearchInputTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            searchBusStop();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private class ActivityMainOnBackPressedCallback extends OnBackPressedCallback {

        public ActivityMainOnBackPressedCallback(boolean enabled) {
            super(enabled);
        }

        @Override
        public void handleOnBackPressed() {
            if (viewBinding.searchContainer.getVisibility() == View.VISIBLE) {
                hideSearchOverlay();
            } else {
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        }
    }

}
