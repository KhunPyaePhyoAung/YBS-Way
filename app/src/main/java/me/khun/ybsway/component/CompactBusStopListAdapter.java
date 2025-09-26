package me.khun.ybsway.component;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.khun.ybsway.R;
import me.khun.ybsway.view.BusStopView;

public class CompactBusStopListAdapter extends RecyclerView.Adapter<CompactBusStopListAdapter.CompactBusStopListViewHolder> {

    private final List<BusStopView> busStopViewList;
    private OnItemClickListener onItemClickListener;

    public CompactBusStopListAdapter(List<BusStopView> busStopViewList) {
        this.busStopViewList = busStopViewList;
    }

    @NonNull
    @Override
    public CompactBusStopListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_stop_list_item_compact_ordered, parent, false);
        return new CompactBusStopListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompactBusStopListViewHolder holder, int position) {
        BusStopView busStopView = busStopViewList.get(position);
        holder.tvBusStopName.setText(busStopView.getName());
        holder.tvTownshipName.setText(busStopView.getTownshipName());
        holder.tvRoadName.setText(busStopView.getRoadName());
        holder.straightLineTop.setVisibility(View.VISIBLE);
        holder.straightLineBottom.setVisibility(View.VISIBLE);

        if (position == 0) {
            holder.straightLineTop.setVisibility(View.INVISIBLE);
        }
        if (position == getItemCount() - 1) {
            holder.straightLineBottom.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(busStopView, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return busStopViewList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class CompactBusStopListViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvBusStopName;
        private final TextView tvTownshipName;
        private final TextView tvRoadName;
        private final View straightLineTop;
        private final View straightLineBottom;

        public CompactBusStopListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBusStopName = itemView.findViewById(R.id.tv_bus_stop_name);
            tvTownshipName = itemView.findViewById(R.id.tv_township_name);
            tvRoadName = itemView.findViewById(R.id.tv_road_name);
            straightLineTop = itemView.findViewById(R.id.straight_line_top);
            straightLineBottom = itemView.findViewById(R.id.straight_line_bottom);
        }
    }

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(BusStopView busStopView, int position);
    }
}
