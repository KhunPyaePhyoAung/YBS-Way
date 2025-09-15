package me.khun.ybsway.component;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import java.util.Collections;
import java.util.List;

import me.khun.ybsway.activity.ActivityBusRoute;
import me.khun.ybsway.view.BusView;

public class BusListToRoutePageItemClickListener implements AdapterView.OnItemClickListener {

    private Context context;
    private List<BusView> busViewList;
    private Runnable postRunnable;

    public BusListToRoutePageItemClickListener(Context context) {
        this.context = context;
    }

    public void setBusList(List<BusView> busViewList) {
        if (busViewList == null) {
            this.busViewList = Collections.emptyList();
            return;
        }
        this.busViewList = busViewList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(context, ActivityBusRoute.class);
        intent.putExtra("route_id", busViewList.get(i).getRouteId());
        context.startActivity(intent);
        if (postRunnable != null) {
            postRunnable.run();
        }
    }

    public void postRunnable(Runnable postRunnable) {
        this.postRunnable = postRunnable;
    }
}