package me.khun.ybsway.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.khun.ybsway.R;
import me.khun.ybsway.view.BusStopSearchHistoryItem;
import me.khun.ybsway.view.BusStopView;

public class BusStopSearchHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<BusStopSearchHistoryItem> searchList;
    private ItemClickListener itemClickListener;

    public BusStopSearchHistoryAdapter(Context context, List<BusStopSearchHistoryItem> searchList) {
        this.context = context;
        this.searchList = searchList;
    }

    @Override
    public int getCount() {
        return searchList.size();
    }

    @Override
    public Object getItem(int i) {
        return searchList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.bus_stop_search_history_list_item, null);
        }

        TextView tvBusStopName = view.findViewById(R.id.tv_bus_stop_name);
        BusStopSearchHistoryItem searchItem = searchList.get(i);
        BusStopView busStopView = searchItem.getBusStopView();
        tvBusStopName.setText(busStopView.formatText());

        if (itemClickListener != null) {
            view.setOnClickListener(view1 -> {
                itemClickListener.onItemClick(searchItem, view1, i);
            });
        }

        return view;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void changeData(List<BusStopSearchHistoryItem> searchList) {
        this.searchList = searchList;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(BusStopSearchHistoryItem searchItem, View itemView, int position);
    }
}
