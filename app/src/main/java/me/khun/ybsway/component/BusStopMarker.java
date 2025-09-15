package me.khun.ybsway.component;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import me.khun.ybsway.view.BusStopView;

public class BusStopMarker extends Marker {
    protected MapView mapView;
    protected BusStopView busStop;

    public BusStopMarker(MapView mapView, BusStopView busStop) {
        super(mapView);
        this.mapView = mapView;
        this.busStop = busStop;
        setInfoWindow(null);
    }

    public BusStopView getBusStop() {
        return busStop;
    }

    public void resetAnchor(float anchorU, float anchorV) {
        setInfoWindowAnchor(anchorU, anchorV);
        if ( isInfoWindowOpen() ) {
            showInfoWindow();
        }
    }

}
