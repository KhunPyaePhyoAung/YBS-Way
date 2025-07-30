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
import me.khun.ybsway.entity.Bus;

public class BusListViewAdapter extends BaseAdapter {

    private final Context context;
    private final List<Bus> buses;
    private LayoutInflater inflater = null;

    public BusListViewAdapter(Context context, List<Bus> buses) {
        this.context = context;
        this.buses = buses;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return buses.size();
    }

    @Override
    public Object getItem(int i) {
        return buses.get(i);
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
        ImageView busIcon = vi.findViewById(R.id.bus_icon);
        TextView busNumberTextView = vi.findViewById(R.id.bus_number);
        TextView busStartTextView = vi.findViewById(R.id.bus_from);
        TextView busEndTextView = vi.findViewById(R.id.bus_to);

        Bus bus = buses.get(i);

        String busName = bus.getName();
        if (busName.contains("Airport")) {
            busNumberTextView.setText(null);
            busIcon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.plane));
        } else {
            busNumberTextView.setText(bus.getNumber());
            busIcon.setImageDrawable(null);
        }

        iconLayout.setBackgroundColor(Color.parseColor("#" + bus.getHexColorCode()));
        busStartTextView.setText(bus.getStartName());
        busEndTextView.setText(bus.getEndName());

        vi.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, ActivityBusRoute.class);
            intent.putExtra("route_id", bus.getRouteId());
            context.startActivity(intent);
        });

        return vi;
    }
}
