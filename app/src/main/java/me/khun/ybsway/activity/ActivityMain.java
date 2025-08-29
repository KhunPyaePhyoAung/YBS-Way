package me.khun.ybsway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
    private ImageButton btnNavToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupMap(R.id.map_view);

        busStopMapper = YBSWayApplication.busStopMapper;
        busStopService = YBSWayApplication.busStopService;
        mainViewModel = new MainViewModel(busStopMapper, busStopService);
        mainViewModel.getAllBusStopsData().observe(this, this::drawBusStops);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnNavToggle = findViewById(R.id.btn_nav_toggle);
        loadingContainer = findViewById(R.id.loading_container);

        btnNavToggle.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        mainViewModel.loadAllBusStopsData();
    }

    @Override
    protected void postDrawBusStops() {
        super.postDrawBusStops();
        loadingContainer.setVisibility(View.GONE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, ActivityBusList.class));

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
