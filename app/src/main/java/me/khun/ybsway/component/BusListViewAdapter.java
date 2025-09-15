package me.khun.ybsway.component;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.content.res.AppCompatResources;

import java.util.List;

import me.khun.ybsway.R;
import me.khun.ybsway.activity.ActivityBusRoute;
import me.khun.ybsway.view.BusView;

public class BusListViewAdapter extends BaseAdapter {

    private final Context context;
    private final List<BusView> busViewList;

    public BusListViewAdapter(Context context, List<BusView> busViewList) {
        this.context = context;
        this.busViewList = busViewList;
    }

    @Override
    public int getCount() {
        return busViewList.size();
    }

    @Override
    public Object getItem(int i) {
        return busViewList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.bus_list_item, null);
        }

        RelativeLayout iconLayout = view.findViewById(R.id.icon_layout);
        ImageView busIconImageView = view.findViewById(R.id.bus_icon);
        TextView busNameTextView = view.findViewById(R.id.bus_name);
        TextView busSubNameTextView = view.findViewById(R.id.bus_sub_name);
        TextView busOriginTextView = view.findViewById(R.id.bus_from);
        TextView busDestinationTextView = view.findViewById(R.id.bus_to);

        BusView busView = busViewList.get(i);

        if (busView.getDisplayIconId() == null) {
            busNameTextView.setText(busView.getName());
            busIconImageView.setImageDrawable(null);
        } else {
            busNameTextView.setText(null);
            busIconImageView.setImageDrawable(AppCompatResources.getDrawable(context, busView.getDisplayIconId()));
        }

        if (busView.getSubName() == null) {
            busSubNameTextView.setText("");
        } else {
            busSubNameTextView.setText(busView.getSubName());
        }

        iconLayout.setBackgroundColor(Color.parseColor("#" + busView.getHexColorCode()));
        busOriginTextView.setText(busView.getOriginName());
        busDestinationTextView.setText(busView.getDestinationName());
        return view;
    }
}
