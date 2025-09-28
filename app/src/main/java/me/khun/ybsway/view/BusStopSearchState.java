package me.khun.ybsway.view;

import java.util.Collections;
import java.util.List;

public class BusStopSearchState {
    public enum Status { EMPTY_QUERY, NO_RESULTS, RESULTS };

    public final Status status;
    public final List<BusStopView> results;

    private BusStopSearchState(Status status, List<BusStopView> results) {
        this.status = status;
        this.results = results;
    }

    public static BusStopSearchState emptyQuery() {
        return new BusStopSearchState(Status.EMPTY_QUERY, Collections.emptyList());
    }

    public static BusStopSearchState noResults() {
        return new BusStopSearchState(Status.NO_RESULTS, Collections.emptyList());
    }

    public static BusStopSearchState results(List<BusStopView> results) {
        return new BusStopSearchState(Status.RESULTS, results);
    }
}
