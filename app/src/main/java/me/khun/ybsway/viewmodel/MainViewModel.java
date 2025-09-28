package me.khun.ybsway.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.manager.BusStopSearchHistoryManager;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.view.BusStopSearchHistoryItem;
import me.khun.ybsway.view.BusStopSearchState;
import me.khun.ybsway.view.BusStopView;

public class MainViewModel extends ViewModel {
    private static final int MIN_SEARCH_RATIO = 75;
    private final BusStopMapper busStopMapper;
    private final BusStopService busStopService;
    private final BusStopSearchHistoryManager busStopSearchHistoryManager;
    private final List<BusStop> busStopList = new ArrayList<>();
    private final ExecutorService busStopSearchExecutorService = Executors.newSingleThreadExecutor();
    private Future<?> busStopSearchFuture;
    private final MutableLiveData<BusStopSearchState> busStopSearchStateData = new MutableLiveData<>();
    private final MutableLiveData<List<BusStopSearchHistoryItem>> busStopSearchHistoryListData = new MutableLiveData<>();
    private final MutableLiveData<List<BusStopView>> busStopViewListData = new MutableLiveData<>();

    private volatile String lastQuery;

    public MainViewModel(Dependencies dependencies) {
        busStopMapper = dependencies.busStopMapper;
        busStopService = dependencies.busStopService;
        busStopSearchHistoryManager = dependencies.busStopSearchHistoryManager;
        busStopSearchHistoryListData.setValue(busStopSearchHistoryManager.getHistory());
    }

    public LiveData<List<BusStopView>> getAllBusStopsData() {
        return busStopViewListData;
    }

    public void loadAllBusStopsData() {
        busStopList.clear();
        busStopList.addAll(busStopService.getAll());

    }

    public void searchBusStops(String query) {
        if (busStopSearchFuture != null  && !busStopSearchFuture.isDone()) {
            busStopSearchFuture.cancel(true);
        }

        lastQuery = query;

        if (query == null || query.trim().isEmpty()) {
            busStopSearchStateData.postValue(BusStopSearchState.emptyQuery());
            return;
        }

        final String submittedQuery = query;

        busStopSearchFuture = busStopSearchExecutorService.submit(() -> {
            List<BusStop> resultList = busStopService.searchFromList(query, MIN_SEARCH_RATIO, List.copyOf(busStopList));
            List<BusStopView> resultViewList = busStopMapper.mapToBusStopList(resultList);

            if (!submittedQuery.equals(lastQuery)) {
                return;
            }

            if (resultViewList.isEmpty()) {
                busStopSearchStateData.postValue(BusStopSearchState.noResults());
            } else {
                busStopSearchStateData.postValue(BusStopSearchState.results(resultViewList));
            }
        });
    }

    public LiveData<BusStopSearchState> getBusStopSearchStateData() {
        return busStopSearchStateData;
    }

    public void loadBusStopSearchHistory() {
        busStopViewListData.setValue(busStopMapper.mapToBusStopList(busStopList));
    }

    public void addBusStopSearchHistory(BusStopSearchHistoryItem item) {
        busStopSearchHistoryManager.addSearchQuery(item);
        busStopSearchHistoryListData.setValue(busStopSearchHistoryManager.getHistory());
    }

    public void clearBusStopSearchHistory() {
        busStopSearchHistoryManager.clearHistory();
        busStopSearchHistoryListData.setValue(busStopSearchHistoryManager.getHistory());
    }

    public LiveData<List<BusStopSearchHistoryItem>> getBusStopSearchHistoryData() {
        return busStopSearchHistoryListData;
    }

    public static class Dependencies {
        public BusStopMapper busStopMapper;
        public BusStopService busStopService;
        public BusStopSearchHistoryManager busStopSearchHistoryManager;
    }
}
