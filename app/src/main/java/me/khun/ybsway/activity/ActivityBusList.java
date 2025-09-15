package me.khun.ybsway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.R;
import me.khun.ybsway.component.BusListToRoutePageItemClickListener;
import me.khun.ybsway.component.BusListViewAdapter;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.viewmodel.BusListViewModel;

public class ActivityBusList extends ActivityBase {

    private ListView busListView;
    private BusMapper busMapper;
    private BusService busService;
    private BusListViewModel busViewModel;
    private ActionBar actionBar;
    private BusListToRoutePageItemClickListener listItemClickListener;

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

        listItemClickListener = new BusListToRoutePageItemClickListener(this);

        busViewModel = new BusListViewModel(busMapper, busService);
        busListView.setOnItemClickListener(listItemClickListener);
        busViewModel.getAllBusListData().observe(this, busViewList -> {
            listItemClickListener.setBusList(busViewList);
            busListView.setAdapter(new BusListViewAdapter(this, busViewList));
        });

        busViewModel.loadBusData();

    }

}
