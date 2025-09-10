package me.khun.ybsway.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import me.khun.ybsway.R;
import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.viewmodel.MainViewModel;

public class ActivityMain extends ActivityBaseMap implements NavigationView.OnNavigationItemSelectedListener {
    private BusStopMapper busStopMapper;
    private BusStopService busStopService;
    private MainViewModel mainViewModel;
    private DrawerLayout drawerLayout;
    private LinearLayout loadingContainer;
    private EditText busStopInput;
    private ImageButton btnNavToggle;
    private ImageButton searchRouteButton;
    private NavigationView navigationView;
    private View searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupMap(R.id.map_view);
        setupGpsButton(R.id.btn_gps);

        busStopMapper = YBSWayApplication.busStopMapper;
        busStopService = YBSWayApplication.busStopService;

        mainViewModel = new MainViewModel(busStopMapper, busStopService);
        mainViewModel.getAllBusStopsData().observe(this, this::drawBusStops);

        searchView = findViewById(R.id.search_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        busStopInput = findViewById(R.id.bus_stop_input);
        btnNavToggle = findViewById(R.id.btn_nav_toggle);
        searchRouteButton = findViewById(R.id.btn_search_route);
        loadingContainer = findViewById(R.id.loading_container);


        navigationView.setVisibility(View.VISIBLE);
        busStopInput.setEnabled(false);
        searchRouteButton.setEnabled(false);

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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (searchView.getVisibility() == View.VISIBLE) {
                    hideSearchOverlay();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(busStopInput.getWindowToken(), 0);
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
    protected void onRestart() {
        super.onRestart();
        navigationView.setCheckedItem(R.id.nav_home);
    }
}
