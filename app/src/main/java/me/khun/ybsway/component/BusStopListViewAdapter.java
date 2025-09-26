package me.khun.ybsway.component;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import me.khun.ybsway.R;
import me.khun.ybsway.view.BusStopView;

public class BusStopListViewAdapter extends RecyclerView.Adapter<BusStopListViewAdapter.BusStopViewHolder> {

    private List<BusStopView> busStopViewList;
    private ItemClickListener itemClickListener;

    public BusStopListViewAdapter() {
        this(Collections.emptyList());
    }

    public BusStopListViewAdapter(List<BusStopView> busStopViewList) {
        this.busStopViewList = busStopViewList;
    }

    @NonNull
    @Override
    public BusStopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_stop_list_item, parent, false);
        return new BusStopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BusStopViewHolder holder, int position) {
        BusStopView busStopView = busStopViewList.get(position);
        holder.tvBusStopName.setText(busStopView.getName());
        holder.tvRoadName.setText(busStopView.getRoadName());
        holder.tvTownshipName.setText(busStopView.getTownshipName());

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(view -> {
                itemClickListener.onItemClick(busStopView, view, position);
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return busStopViewList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeData(List<BusStopView> busStopViewList) {
        this.busStopViewList = busStopViewList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class BusStopViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvBusStopName;
        private final TextView tvRoadName;
        private final TextView tvTownshipName;

        public BusStopViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBusStopName = itemView.findViewById(R.id.bus_stop_name_tv);
            tvRoadName = itemView.findViewById(R.id.road_name_tv);
            tvTownshipName = itemView.findViewById(R.id.township_name_tv);
        }
    }

    public interface ItemClickListener {
        void onItemClick(BusStopView busStopView, View itemView, int position);
    }
}
