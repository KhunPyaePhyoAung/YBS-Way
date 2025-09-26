package me.khun.ybsway.activity;

import static me.khun.ybsway.application.YBSWayApplication.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import me.khun.ybsway.R;
import me.khun.ybsway.component.BusListToRoutePageItemClickListener;
import me.khun.ybsway.component.BusStopListViewAdapter;
import me.khun.ybsway.component.BusStopMarker;
import me.khun.ybsway.component.BusStopSearchHistoryAdapter;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;
import me.khun.ybsway.viewmodel.MainViewModel;

public class ActivityMain extends ActivityBaseMap implements NavigationView.OnNavigationItemSelectedListener {

    private MainViewModel.Dependencies mainViewModelDependencies;
    private MainViewModel mainViewModel;
    private DrawerLayout drawerLayout;
    private LinearLayout loadingContainer;
    private EditText busStopInput;
    private ImageButton btnNavToggle;
    private ImageButton searchRouteButton;
    private ImageButton btnClearSearch;
    private ImageButton btnClearBusStopSearchHistory;
    private ImageView ivBusStopSearchNotFound;
    private NavigationView navigationView;
    private View searchView;
    private LinearLayout busStopResultContainer;
    private LinearLayout busStopHistoryContainer;
    private RecyclerView busStopResultRecyclerView;
    private ListView busStopSearchHistoryListView;
    private BusStopListViewAdapter busStopListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupMap(R.id.map_view);
        setupGpsButton(R.id.btn_gps);
        setupZoomButtons(R.id.btn_zoom_in, R.id.btn_zoom_out);

        mainViewModelDependencies = new MainViewModel.Dependencies();
        mainViewModelDependencies.busStopMapper = busStopMapper;
        mainViewModelDependencies.busStopService = busStopService;
        mainViewModelDependencies.busStopSearchHistoryManager = busStopSearchHistoryManager;
        mainViewModel = new MainViewModel(mainViewModelDependencies);
        mainViewModel.getAllBusStopsData().observe(this, this::onBusStopsLoaded);

        searchView = findViewById(R.id.search_container);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        busStopInput = findViewById(R.id.bus_stop_input);
        btnNavToggle = findViewById(R.id.btn_nav_toggle);
        searchRouteButton = findViewById(R.id.btn_search_route);
        btnClearSearch = findViewById(R.id.btn_clear_search);
        btnClearBusStopSearchHistory = findViewById(R.id.btn_clear_bus_stop_search_history);
        loadingContainer = findViewById(R.id.loading_container);
        busStopResultContainer = findViewById(R.id.bus_stop_result_container);
        busStopHistoryContainer = findViewById(R.id.bus_stop_history_container);
        busStopResultRecyclerView = findViewById(R.id.bus_stop_result_rc);
        busStopSearchHistoryListView = findViewById(R.id.bus_stop_search_history_list_view);
        ivBusStopSearchNotFound = findViewById(R.id.iv_not_found);

        navigationView.setVisibility(View.VISIBLE);
        busStopInput.setEnabled(false);
        searchRouteButton.setEnabled(false);
        hideSearchOverlay();

        btnNavToggle.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        busStopInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showSearchOverlay();
                searchBusStop();
            }
        });

        busStopInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == KeyEvent.ACTION_DOWN)) {

                closeSoftInput();
                searchBusStop();
            }
            return false;
        });

        busStopInput.addTextChangedListener(new BusStopSearchInputTextWatcher());

        btnClearSearch.setOnClickListener(view -> {
            busStopInput.requestFocus();
            busStopInput.getText().clear();
        });

        btnClearBusStopSearchHistory.setOnClickListener(view -> {
            showClearBusStopHistoryConfirmDialog();
        });

        searchRouteButton.setOnClickListener(view -> {
            closeSoftInput();
            searchBusStop();
        });

        getOnBackPressedDispatcher().addCallback(this, new ActivityMainOnBackPressedCallback(true));

        navigationView.setNavigationItemSelectedListener(this);

        busStopListAdapter = new BusStopListViewAdapter();
        busStopListAdapter.setOnItemClickListener(this::onBusStopResultItemClick);


        busStopResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
        );
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.bus_stop_search_result_list_divider));
        busStopResultRecyclerView.addItemDecoration(dividerItemDecoration);
        busStopResultRecyclerView.setAdapter(busStopListAdapter);

        BusStopSearchHistoryAdapter searchHistoryAdapter = new BusStopSearchHistoryAdapter(this, Collections.emptyList());
        searchHistoryAdapter.setOnItemClickListener((searchText, itemView, position) -> {
            busStopInput.setText(searchText);
        });
        busStopSearchHistoryListView.setAdapter(searchHistoryAdapter);

        mainViewModel.getBusStopSearchHistoryData().observe(this, strings -> {
            if (strings.isEmpty()) {
                busStopHistoryContainer.setVisibility(View.GONE);
            } else {
                busStopHistoryContainer.setVisibility(View.VISIBLE);
            }
            searchHistoryAdapter.changeData(strings);
        });

        mainViewModel.loadAllBusStopsData();
        mainViewModel.loadBusStopSearchHistory();
    }

    private void showSearchOverlay() {
        if (searchView.getVisibility() == View.VISIBLE) {
            return;
        }

        searchView.setVisibility(View.VISIBLE);
        searchView.setAlpha(0f);
        searchView.animate().alpha(1f).setDuration(300).start();
    }

    private void hideSearchOverlay() {
        searchView.setVisibility(View.GONE);
        searchView.animate().alpha(0f).setDuration(300).start();
        busStopInput.clearFocus();
        closeSoftInput();
    }

    private void closeSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(busStopInput.getWindowToken(), 0);
        }
    }

    @Override
    protected void postDrawBusStops() {
        super.postDrawBusStops();
        loadingContainer.setVisibility(View.GONE);
        busStopInput.setEnabled(true);
        searchRouteButton.setEnabled(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int menuId = item.getItemId();
        Intent intent = null;

        if (menuId == R.id.nav_home) {
            drawerLayout.closeDrawer(GravityCompat.START);
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
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    protected BusListToRoutePageItemClickListener getOnRelatedBusItemClickListener() {
        BusListToRoutePageItemClickListener busItemClickListener = new BusListToRoutePageItemClickListener(this);
        return busItemClickListener;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    private void onBusStopsLoaded(List<BusStopView> busStopViewList) {
        drawBusStops(busStopViewList);
    }

    public void onBusStopResultItemClick(BusStopView busStopView, View itemView, int position) {
        hideSearchOverlay();
        for (BusStopMarker mk : busStopMarkerList) {
            if (Objects.equals(busStopView.getId(), mk.getBusStop().getId())) {
                String searchText = String.format("%s %s %s", busStopView.getName(), busStopView.getRoadName(), busStopView.getTownshipName());
                busStopInput.setText(searchText);
                mainViewModel.addBusStopSearchHistory(searchText);
                onMarkerClick(mk, mapView);
                return;
            }
        }
    }

    private void searchBusStop() {
        String keyword = busStopInput.getText().toString();
        if (keyword.isEmpty() && busStopSearchHistoryManager.getHistory().isEmpty()) {
            busStopResultContainer.setVisibility(View.GONE);
            busStopHistoryContainer.setVisibility(View.GONE);
            ivBusStopSearchNotFound.setVisibility(View.VISIBLE);
        } else if (keyword.isEmpty()) {
            busStopResultContainer.setVisibility(View.GONE);
            busStopHistoryContainer.setVisibility(View.VISIBLE);
            ivBusStopSearchNotFound.setVisibility(View.GONE);
        } else {
            List<BusStopView> busStopViewList = mainViewModel.searchStops(keyword);
            busStopListAdapter.changeData(busStopViewList);
            busStopResultRecyclerView.setAdapter(busStopListAdapter);
            busStopResultContainer.setVisibility(View.VISIBLE);
            busStopHistoryContainer.setVisibility(View.GONE);
            ivBusStopSearchNotFound.setVisibility(View.GONE);
        }

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
            if (searchView.getVisibility() == View.VISIBLE) {
                hideSearchOverlay();
            } else {
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        }
    }

    private class RelatedBusListObserver implements Observer<List<BusView>> {

        @Override
        public void onChanged(List<BusView> busViews) {
            if (busViews.isEmpty()) {
                System.out.println("No related bus");
            } else {
                System.out.println("Related bus list:");
                for (BusView bv : busViews) {
                    System.out.print(bv.getName());
                    System.out.print(",");
                }
                System.out.println();
            }
        }
    }
}
