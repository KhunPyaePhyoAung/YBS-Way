package me.khun.ybsway.activity;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;

import me.khun.ybsway.application.Language;
import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.R;
import me.khun.ybsway.custom.BusListViewAdapter;
import me.khun.ybsway.hepler.LanguageHelper;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.viewmodel.BusListViewModel;

public class ActivityBusList extends BaseActivity {

    private ListView busListView;
    private BusMapper busMapper;
    private BusService busService;
    private BusListViewModel busViewModel;
    private Language language;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_line_list);

        busListView = findViewById(R.id.bus_list);
        busMapper = YBSWayApplication.busMapper;
        busService = YBSWayApplication.busService;
        language = LanguageHelper.getLanguage(this);

        busViewModel = new BusListViewModel(busMapper, busService);
        busViewModel.getAllBusListData().observe(this, busViewList -> {
            busListView.setAdapter(new BusListViewAdapter(this, busViewList));
        });

        busViewModel.loadBusData(language);

    }

}
