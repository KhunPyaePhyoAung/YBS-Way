package me.khun.ybsway.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.view.BusSearchState;
import me.khun.ybsway.view.BusView;

public class BusListViewModel extends ViewModel {

    private static final int MIN_SEARCH_RATIO = 75;
    private final BusMapper busMapper;
    private final BusService busService;
    private final ExecutorService busSearchExecutorService = Executors.newSingleThreadExecutor();
    private Future<?> busSearchFuture;
    private List<Bus> busList = new ArrayList<>();
    private final MutableLiveData<BusSearchState> busSearchStateData = new MutableLiveData<>();
    private final MutableLiveData<List<BusView>> busListData = new MutableLiveData<List<BusView>>();

    private volatile String lastQuery;

    public BusListViewModel(Dependencies dependencies) {
        this.busMapper = dependencies.busMapper;
        this.busService = dependencies.busService;
    }

    public void loadBusData() {
        busList = busService.getAll();
        List<BusView> busViews = busMapper.mapToBusViewList(busList);
        busListData.setValue(busViews);
    }

    public void searchBus(String query) {
        if (busSearchFuture != null  && !busSearchFuture.isDone()) {
            busSearchFuture.cancel(true);
        }

        lastQuery = query;

        if (query == null || query.trim().isEmpty()) {
            busSearchStateData.postValue(BusSearchState.allResults(busListData.getValue()));
            return;
        }

        final String submittedQuery = query;

        busSearchFuture = busSearchExecutorService.submit(() -> {
            List<Bus> resultList = busService.searchFromList(query, MIN_SEARCH_RATIO, List.copyOf(busList));
            List<BusView> resultViewList = busMapper.mapToBusViewList(resultList);

            if (!submittedQuery.equals(lastQuery)) {
                return;
            }

            if (resultViewList.isEmpty()) {
                busSearchStateData.postValue(BusSearchState.noResults());
            } else {
                busSearchStateData.postValue(BusSearchState.results(resultViewList));
            }
        });
    }

    public LiveData<BusSearchState> getBusSearchStateData() {
        return busSearchStateData;
    }

    public LiveData<List<BusView>> getAllBusListData() {
        return busListData;
    }

    public static class Dependencies {
        public BusMapper busMapper;
        public BusService busService;
    }
}
