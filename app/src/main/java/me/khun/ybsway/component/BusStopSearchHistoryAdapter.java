package me.khun.ybsway.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.khun.ybsway.R;

public class BusStopSearchHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<String> searchList;
    private ItemClickListener itemClickListener;

    public BusStopSearchHistoryAdapter(Context context, List<String> searchList) {
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

        TextView tvSearchText = view.findViewById(R.id.tv_search_text);
        String searchText = searchList.get(i);
        tvSearchText.setText(searchText);

        if (itemClickListener != null) {
            view.setOnClickListener(view1 -> {
                itemClickListener.onItemClick(searchText, view1, i);
            });
        }

        return view;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void changeData(List<String> searchList) {
        this.searchList = searchList;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(String searchText, View itemView, int position);
    }
}
