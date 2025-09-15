package me.khun.ybsway.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import java.util.List;

import me.khun.ybsway.R;
import me.khun.ybsway.view.BusView;

public class BusIconAdapter extends BaseAdapter {
    private Context context;
    private List<BusView> busViewList;

    public BusIconAdapter(Context context, List<BusView> busViewList) {
        this.context = context;
        this.busViewList = busViewList;
    }

    @Override
    public int getCount() {
        return busViewList.size();
    }

    @Override
    public Object getItem(int position) {
        return busViewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_related_bus_icon, parent, false);
        }

        View bg = convertView.findViewById(R.id.icon_bg);
        TextView tvBusName = convertView.findViewById(R.id.tv_bus_name);
        TextView tvBusSubName = convertView.findViewById(R.id.tv_bus_sub_name);
        ImageView ivBusIcon = convertView.findViewById(R.id.iv_bus_icon);

        BusView busView = busViewList.get(position);

        GradientDrawable bgDrawable = (GradientDrawable) bg.getBackground();
        int newColor = Color.parseColor("#" + busView.getHexColorCode());
        bgDrawable.setColor(newColor);

        if (busView.getDisplayIconId() == null) {
            tvBusName.setText(busView.getName());
            ivBusIcon.setImageDrawable(null);
        } else {
            tvBusSubName.setText(null);
            ivBusIcon.setImageDrawable(AppCompatResources.getDrawable(context, busView.getDisplayIconId()));
        }

        if (busView.getSubName() == null) {
            tvBusSubName.setVisibility(View.GONE);
        } else {
            tvBusSubName.setVisibility(View.VISIBLE);
            tvBusSubName.setText(busView.getSubName());
        }
        return convertView;
    }
}
