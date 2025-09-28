package me.khun.ybsway.view;

import androidx.annotation.Nullable;

import java.util.Objects;

public class BusStopSearchHistoryItem {
    private BusStopView busStopView;

    public BusStopSearchHistoryItem() {

    }

    public BusStopSearchHistoryItem(BusStopView busStopView) {
        this.busStopView = busStopView;
    }

    public BusStopView getBusStopView() {
        return busStopView;
    }

    public void setBusStopView(BusStopView busStopView) {
        this.busStopView = busStopView;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof BusStopSearchHistoryItem)) {
            return false;
        }
        return Objects.equals(busStopView, ((BusStopSearchHistoryItem) obj).busStopView);
    }

    @Override
    public int hashCode() {
        return busStopView.getId().hashCode();
    }

}
