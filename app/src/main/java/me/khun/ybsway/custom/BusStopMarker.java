package me.khun.ybsway.custom;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import me.khun.ybsway.entity.BusStop;

public class BusStopMarker extends Marker {
    protected MapView mapView;
    protected BusStop busStop;

    public BusStopMarker(MapView mapView, BusStop busStop) {
        super(mapView);
        this.mapView = mapView;
        this.busStop = busStop;
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public void resetAnchor(float anchorU, float anchorV) {
        setInfoWindowAnchor(anchorU, anchorV);
        if ( isInfoWindowOpen() ) {
            showInfoWindow();
        }
    }
}
