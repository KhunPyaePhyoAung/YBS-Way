package me.khun.ybsway.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;

public class MainViewModel extends ViewModel {
    private static final int MIN_SEARCH_RATIO = 70;
    private BusStopMapper busStopMapper;
    private BusStopService busStopService;
    private List<BusStop> busStopList = new ArrayList<>();
    private MutableLiveData<List<BusStopView>> busStopViewListData = new MutableLiveData<>();

    public MainViewModel(BusStopMapper busStopMapper, BusStopService busStopService) {
        this.busStopMapper = busStopMapper;
        this.busStopService = busStopService;
    }

    public LiveData<List<BusStopView>> getAllBusStopsData() {
        return busStopViewListData;
    }

    public void loadAllBusStopsData() {
        busStopList.clear();
        busStopList.addAll(busStopService.getAll());
        busStopViewListData.setValue(busStopMapper.mapToBusStopList(busStopList));

    }

    public List<BusStopView> searchStops(String keyword) {
        List<BusStop> resultList = busStopService.searchFromList(keyword, MIN_SEARCH_RATIO, List.copyOf(busStopList));
        return busStopMapper.mapToBusStopList(resultList);
    }
}
