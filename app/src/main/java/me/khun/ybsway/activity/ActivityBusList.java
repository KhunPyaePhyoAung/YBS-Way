package me.khun.ybsway.activity;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.khun.ybsway.YBSWayApplication;
import me.khun.ybsway.R;
import me.khun.ybsway.custom.BusListViewAdapter;
import me.khun.ybsway.service.BusService;

public class ActivityBusList extends AppCompatActivity {

    private BusService busService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_line_list);
        busService = YBSWayApplication.busService;

        ListView busListView = findViewById(R.id.bus_list);
        busListView.setAdapter(new BusListViewAdapter(this, busService.getAll()));
    }

}
