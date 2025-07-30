package me.khun.ybsway.custom;

import android.view.View;
import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import me.khun.ybsway.R;
import me.khun.ybsway.entity.BusStop;

public class BusStopInfoWindow extends InfoWindow {
    protected BusStopMarker marker;
    protected BusStop busStop;
    protected TextView nameTextView;
    protected TextView townshipTextView;
    protected TextView streetTextView;

    public BusStopInfoWindow(MapView mapView, BusStop busStop) {
        super(R.layout.bus_stop_info, mapView);
        this.busStop = busStop;

        nameTextView = mView.findViewById(R.id.name);
        townshipTextView = mView.findViewById(R.id.township);
        streetTextView = mView.findViewById(R.id.street);

        nameTextView.setText(busStop.getNameMM());
        townshipTextView.setText(busStop.getTownshipMM());
        streetTextView.setText(String.format("(%s)", busStop.getStreetMM()));
    }

    public BusStopMarker getMarkerReference() {
        return marker;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }

    @Override
    public void onOpen(Object item) {
        marker = (BusStopMarker) item;
    }

    @Override
    public void onClose() {
        marker = null;
    }
}
