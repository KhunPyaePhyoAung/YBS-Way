package me.khun.ybsway.component;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import java.util.List;

import me.khun.ybsway.R;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;

public class RelatedBusListDialog extends AlertDialog {

    private GridView gridView;
    private TextView tvBusStopName;
    private AppCompatButton btnClose;
    private BusStopView busStopView;
    private List<BusView> relatedBusList;

    public RelatedBusListDialog(Context context) {
        super(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_bus_related, null);
        setView(dialogView);
        setCancelable(true);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gridView = dialogView.findViewById(R.id.grid_bus_icon);
        tvBusStopName = dialogView.findViewById(R.id.tv_bus_stop_name);
        btnClose = dialogView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(view -> {
            dismiss();
        });
    }

    public void setBusStop(BusStopView busStopView) {
        this.busStopView = busStopView;
        tvBusStopName.setText(busStopView.getName());
    }

    public void setRelatedBusList(List<BusView> relatedBusList) {
        this.relatedBusList = relatedBusList;
        BusIconAdapter adapter = new BusIconAdapter(getContext(), relatedBusList);
        gridView.setAdapter(adapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        gridView.setOnItemClickListener(itemClickListener);
    }
}
