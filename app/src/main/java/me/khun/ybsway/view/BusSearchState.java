package me.khun.ybsway.view;

import java.util.Collections;
import java.util.List;

public class BusSearchState {
    public enum Status {EMPTY_QUERY, NO_RESULTS, RESULTS, ALL_RESULTS};

    public final Status status;
    public final List<BusView> results;

    public BusSearchState(Status status, List<BusView> results) {
        this.status = status;
        this.results = results;
    }

    public static BusSearchState emptyQuery() {
        return new BusSearchState(Status.EMPTY_QUERY, Collections.emptyList());
    }

    public static BusSearchState noResults() {
        return new BusSearchState(Status.NO_RESULTS, Collections.emptyList());
    }

    public static BusSearchState results(List<BusView> results) {
        return new BusSearchState(Status.RESULTS, results);
    }

    public static BusSearchState allResults(List<BusView> allResults) {
        return new BusSearchState(Status.ALL_RESULTS, allResults);
    }
}
