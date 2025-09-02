package me.khun.ybsway.activity;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.R;
import me.khun.ybsway.custom.BusListViewAdapter;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.viewmodel.BusListViewModel;

public class ActivityBusList extends ActivityBase {

    private ListView busListView;
    private BusMapper busMapper;
    private BusService busService;
    private BusListViewModel busViewModel;
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_line_list);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        busListView = findViewById(R.id.bus_list);
        busMapper = YBSWayApplication.busMapper;
        busService = YBSWayApplication.busService;

        busViewModel = new BusListViewModel(busMapper, busService);
        busViewModel.getAllBusListData().observe(this, busViewList -> {
            busListView.setAdapter(new BusListViewAdapter(this, busViewList));
        });

        busViewModel.loadBusData();

    }

}
