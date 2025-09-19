package me.khun.ybsway.activity;

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
import android.widget.AdapterView;
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

import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Objects;

import me.khun.ybsway.R;
import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.component.BusListToRoutePageItemClickListener;
import me.khun.ybsway.component.BusStopListViewAdapter;
import me.khun.ybsway.component.BusStopMarker;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;
import me.khun.ybsway.viewmodel.MainViewModel;

public class ActivityMain extends ActivityBaseMap implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {
    private BusStopMapper busStopMapper;
    private BusStopService busStopService;
    private MainViewModel mainViewModel;
    private DrawerLayout drawerLayout;
    private LinearLayout loadingContainer;
    private EditText busStopInput;
    private ImageButton btnNavToggle;
    private ImageButton searchRouteButton;
    private ImageButton btnClearSearch;
    private ImageView ivBusStopSearchNotFound;
    private NavigationView navigationView;
    private View searchView;
    private ListView busStopResultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupMap(R.id.map_view);
        setupGpsButton(R.id.btn_gps);
        setupZoomButtons(R.id.btn_zoom_in, R.id.btn_zoom_out);

        busStopMapper = YBSWayApplication.busStopMapper;
        busStopService = YBSWayApplication.busStopService;

        mainViewModel = new MainViewModel(busStopMapper, busStopService);
        mainViewModel.getAllBusStopsData().observe(this, this::onBusStopsLoaded);

        searchView = findViewById(R.id.search_container);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        busStopInput = findViewById(R.id.bus_stop_input);
        btnNavToggle = findViewById(R.id.btn_nav_toggle);
        searchRouteButton = findViewById(R.id.btn_search_route);
        btnClearSearch = findViewById(R.id.btn_clear_search);
        loadingContainer = findViewById(R.id.loading_container);
        busStopResultListView = findViewById(R.id.bus_stop_result_list_view);
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
            busStopInput.getText().clear();
        });

        searchRouteButton.setOnClickListener(view -> {
            closeSoftInput();
            searchBusStop();
        });

        busStopResultListView.setOnItemClickListener(this);

        getOnBackPressedDispatcher().addCallback(this, new ActivityMainOnBackPressedCallback(true));

        navigationView.setNavigationItemSelectedListener(this);
        mainViewModel.loadAllBusStopsData();
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        hideSearchOverlay();
        int busStopId = (int) adapterView.getItemIdAtPosition(i);
        for (BusStopMarker mk : busStopMarkerList) {
            if (Objects.equals(busStopId, mk.getBusStop().getId())) {
                onMarkerClick(mk, mapView);
                return;
            }
        }
    }

    private void searchBusStop() {
        List<BusStopView> busStopViewList = mainViewModel.searchStops(busStopInput.getText().toString());
        busStopResultListView.setAdapter(new BusStopListViewAdapter(ActivityMain.this, busStopViewList));
        ivBusStopSearchNotFound.setVisibility(busStopViewList.isEmpty() ? View.VISIBLE : View.GONE);
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
