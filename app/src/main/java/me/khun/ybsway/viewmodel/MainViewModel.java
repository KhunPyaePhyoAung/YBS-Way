package me.khun.ybsway.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.manager.BusStopSearchHistoryManager;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.view.BusStopView;

public class MainViewModel extends ViewModel {
    private static final int MIN_SEARCH_RATIO = 70;
    private Dependencies dependencies;
    private BusStopMapper busStopMapper;
    private BusStopService busStopService;
    private BusStopSearchHistoryManager busStopSearchHistoryManager;
    private List<BusStop> busStopList = new ArrayList<>();
    private final MutableLiveData<List<String>> busStopSearchHistoryListData = new MutableLiveData<>();
    private final MutableLiveData<List<BusStopView>> busStopViewListData = new MutableLiveData<>();

    public MainViewModel(Dependencies dependencies) {
        this.dependencies = dependencies;
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

    public List<BusStopView> searchStops(String keyword) {
        List<BusStop> resultList = busStopService.searchFromList(keyword, MIN_SEARCH_RATIO, List.copyOf(busStopList));
        return busStopMapper.mapToBusStopList(resultList);
    }

    public void loadBusStopSearchHistory() {
        busStopViewListData.setValue(busStopMapper.mapToBusStopList(busStopList));
    }

    public void addBusStopSearchHistory(String searchText) {
        busStopSearchHistoryManager.addSearchQuery(searchText);
        busStopSearchHistoryListData.setValue(busStopSearchHistoryManager.getHistory());
    }

    public void clearBusStopSearchHistory() {
        busStopSearchHistoryManager.clearHistory();
        busStopSearchHistoryListData.setValue(busStopSearchHistoryManager.getHistory());
    }

    public LiveData<List<String>> getBusStopSearchHistoryData() {
        return busStopSearchHistoryListData;
    }

    public static class Dependencies {
        public BusStopMapper busStopMapper;
        public BusStopService busStopService;
        public BusStopSearchHistoryManager busStopSearchHistoryManager;
    }
}
