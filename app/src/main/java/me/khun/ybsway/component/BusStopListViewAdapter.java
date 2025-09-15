package me.khun.ybsway.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.khun.ybsway.R;
import me.khun.ybsway.view.BusStopView;

public class BusStopListViewAdapter extends BaseAdapter {

    private final Context context;
    private final List<BusStopView> busViewList;
    private View.OnClickListener onClickListener;

    public BusStopListViewAdapter(Context context, List<BusStopView> busViewList) {
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
        return busViewList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.bus_stop_list_item, null);
        }

        BusStopView busStopView = busViewList.get(i);

        TextView busStopNameTv = view.findViewById(R.id.bus_stop_name_tv);
        TextView townshipNameTv = view.findViewById(R.id.township_name_tv);
        TextView roadNameTv = view.findViewById(R.id.road_name_tv);

        busStopNameTv.setText(busStopView.getName());
        townshipNameTv.setText(busStopView.getTownshipName());
        roadNameTv.setText(busStopView.getRoadName());

        if (onClickListener != null) {
            view.setOnClickListener(onClickListener);
        }

        return view;
    }
}
