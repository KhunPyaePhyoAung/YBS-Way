package me.khun.ybsway.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import java.util.List;

import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.component.BusListToRoutePageItemClickListener;
import me.khun.ybsway.component.BusListViewAdapter;
import me.khun.ybsway.databinding.ActivityBusLineListBinding;
import me.khun.ybsway.view.BusView;
import me.khun.ybsway.viewmodel.BusListViewModel;

public class ActivityBusList extends ActivityBase {

    private BusListViewModel busViewModel;
    private ActionBar actionBar;
    private BusListToRoutePageItemClickListener listItemClickListener;

    private ActivityBusLineListBinding viewBinding;

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

    private void initViews() {
        setSupportActionBar(viewBinding.toolBar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        viewBinding.toolBar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void initObjects() {
        BusListViewModel.Dependencies dependencies = new BusListViewModel.Dependencies();
        dependencies.busMapper = YBSWayApplication.busMapper;
        dependencies.busService = YBSWayApplication.busService;
        busViewModel = new BusListViewModel(dependencies);
    }

    private void initListeners() {
        listItemClickListener = new BusListToRoutePageItemClickListener(this);
        viewBinding.busList.setOnItemClickListener(listItemClickListener);

        busViewModel.getAllBusListData().observe(this, this::onBusListLoaded);
    }

    private void onBusListLoaded(List<BusView> busViewList) {
        listItemClickListener.setBusList(busViewList);
        viewBinding.busList.setAdapter(new BusListViewAdapter(this, busViewList));
    }

}
