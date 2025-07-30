package me.khun.ybsway.custom;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import me.khun.ybsway.R;
import me.khun.ybsway.YBSWayApplication;

public class BusStopIcon extends GradientDrawable {

    public BusStopIcon(Context context, int size) {
//        size = YBSWayApplication.dpToPx(size);
        setShape(GradientDrawable.OVAL);
        setColor(context.getResources().getColor(R.color.bus_stop_fill));
        setStroke(
                YBSWayApplication.dpToPx(1),
                context.getResources().getColor(R.color.bus_stop_border)
        );
        setSize(size, size);
    }
}
