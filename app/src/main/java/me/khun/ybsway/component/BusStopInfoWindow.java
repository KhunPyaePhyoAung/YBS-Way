package me.khun.ybsway.component;

import android.view.View;
import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.List;

import me.khun.ybsway.R;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;

public class BusStopInfoWindow extends InfoWindow {
    protected BusStopMarker marker;
    protected BusStopView busStop;
    protected List<BusView> relatedBusList;
    protected TextView busCountBadgeTextView;
    protected TextView nameTextView;
    protected TextView townshipTextView;
    protected TextView streetTextView;

    public BusStopInfoWindow(MapView mapView, BusStopView busStop, List<BusView> relatedBusList) {
        super(R.layout.bus_stop_info, mapView);
        this.busStop = busStop;
        this.relatedBusList = relatedBusList;

        busCountBadgeTextView = mView.findViewById(R.id.badge_related_bus);
        nameTextView = mView.findViewById(R.id.name);
        townshipTextView = mView.findViewById(R.id.township);
        streetTextView = mView.findViewById(R.id.street);

        busCountBadgeTextView.setText(String.format("%s", relatedBusList.size()));
        nameTextView.setText(String.format("%s : %s", busStop.getId(), busStop.getName()));
        streetTextView.setText(busStop.getRoadName());
        townshipTextView.setText(String.format("(%s)", busStop.getTownshipName()));
    }

    public List<BusView> getRelatedBusList() {
        return List.copyOf(relatedBusList);
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
