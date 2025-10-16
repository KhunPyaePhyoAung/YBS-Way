package me.khun.ybsway.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.khun.ybsway.application.YBSWayApplication;

public class Route implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<BusStop> busStopList = new ArrayList<>(YBSWayApplication.DEFAULT_BUS_STOP_LIST_SIZE);
    private int currentIndex = 0;

    public Route() {}

    public Route(List<BusStop> busStopList) {
        addAll(busStopList);
    }

    public void add(BusStop busStop) {
        if ( busStop == null ) {
            throw new IllegalArgumentException("Invalid value");
        }
        busStopList.add(busStop);
    }

    public void addAll(List<BusStop> busStopList) {
        if ( busStopList == null ) {
            throw new IllegalArgumentException("BusStopList is null!");
        }
        this.busStopList.addAll(busStopList);
    }

    public BusStop getNext() {
        if ( busStopList.isEmpty() ) {
            throw new IllegalStateException("Empty Route");
        }
        if ( currentIndex >= busStopList.size() ) {
            currentIndex = 0;
        }
        return busStopList.get(currentIndex++);
    }

    public List<BusStop> getBusStopList() {
        return List.copyOf(busStopList);
    }

    public void toStart() {
        currentIndex = 0;
    }

    public void toEnd() {
        currentIndex = busStopList.size() - 1;
    }
}
