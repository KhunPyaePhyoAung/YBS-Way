package me.khun.ybsway.custom;

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
    private LayoutInflater inflater = null;

    public BusListViewAdapter(Context context, List<BusView> busViewList) {
        this.context = context;
        this.busViewList = busViewList;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        View vi = view;

        if (view == null) {
            vi = inflater.inflate(R.layout.bus_list_item, null);
        }

        RelativeLayout iconLayout = vi.findViewById(R.id.icon_layout);
        ImageView busIconImageView = vi.findViewById(R.id.bus_icon);
        TextView busNumberTextView = vi.findViewById(R.id.bus_number);
        TextView busOriginTextView = vi.findViewById(R.id.bus_from);
        TextView busDestinationTextView = vi.findViewById(R.id.bus_to);

        BusView busView = busViewList.get(i);

        if (busView.getDisplayIconId() == null) {
            busNumberTextView.setText(busView.getNumber());
            busIconImageView.setImageDrawable(null);
        } else {
            busNumberTextView.setText(null);
            busIconImageView.setImageDrawable(AppCompatResources.getDrawable(context, busView.getDisplayIconId()));
        }

        iconLayout.setBackgroundColor(Color.parseColor("#" + busView.getHexColorCode()));
        busOriginTextView.setText(busView.getOriginName());
        busDestinationTextView.setText(busView.getDestinationName());

        vi.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, ActivityBusRoute.class);
            intent.putExtra("route_id", busView.getRouteId());
            context.startActivity(intent);
        });

        return vi;
    }
}
