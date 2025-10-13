package me.khun.ybsway.component;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import me.khun.ybsway.R;
import me.khun.ybsway.view.BusView;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.BusViewHolder> {

    private List<BusView> busViewList;
    private ItemClickListener itemClickListener;

    public BusListAdapter() {
        this(Collections.emptyList());
    }

    public BusListAdapter(List<BusView> busViewList) {
        this.busViewList = busViewList;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_list_item, parent, false);
        return new BusViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        BusView busView = busViewList.get(position);
        if (busView.getDisplayIconId() == null) {
            holder.tvBusName.setText(busView.getName());
            holder.ivBusIcon.setImageDrawable(null);
        } else {
            holder.tvBusName.setText(null);
            holder.ivBusIcon.setImageDrawable(AppCompatResources.getDrawable(holder.ivBusIcon.getContext(), busView.getDisplayIconId()));
        }

        if (busView.getSubName() == null) {
            holder.tvBusSubName.setText(null);
        } else {
            holder.tvBusSubName.setText(busView.getSubName());
        }

        holder.iconLayout.setBackgroundColor(Color.parseColor("#" + busView.getHexColorCode()));
        holder.tvBusOriginName.setText(busView.getOriginName());
        holder.tvBusDestName.setText(busView.getDestinationName());

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(view -> {
                itemClickListener.onItemClick(busView, view, position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return busViewList.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void changeData(List<BusView> busViewList) {
        this.busViewList = busViewList;
        notifyDataSetChanged();
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout iconLayout;
        private final TextView tvBusName;
        private final ImageView ivBusIcon;
        private final TextView tvBusSubName;
        private final TextView tvBusOriginName;
        private final TextView tvBusDestName;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            iconLayout = itemView.findViewById(R.id.icon_layout);
            tvBusName = itemView.findViewById(R.id.bus_name);
            ivBusIcon = itemView.findViewById(R.id.bus_icon);
            tvBusSubName = itemView.findViewById(R.id.bus_sub_name);
            tvBusOriginName = itemView.findViewById(R.id.bus_from);
            tvBusDestName = itemView.findViewById(R.id.bus_to);
        }
    }

    public interface ItemClickListener {
        void onItemClick(BusView busView, View view, int position);
    }
}
