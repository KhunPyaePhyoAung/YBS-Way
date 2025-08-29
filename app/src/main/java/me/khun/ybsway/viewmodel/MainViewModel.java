package me.khun.ybsway.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.view.BusStopView;

public class MainViewModel extends ViewModel {
    private BusStopMapper busStopMapper;
    private BusStopService busStopService;
    private MutableLiveData<List<BusStopView>> busStopViewList = new MutableLiveData<>();

    public MainViewModel(BusStopMapper busStopMapper, BusStopService busStopService) {
        this.busStopMapper = busStopMapper;
        this.busStopService = busStopService;
    }

    public LiveData<List<BusStopView>> getAllBusStopsData() {
        return busStopViewList;
    }

    public void loadAllBusStopsData() {
        List<BusStop> busStopList = busStopService.getAll();
        busStopViewList.setValue(busStopMapper.mapToBusStopList(busStopList));

    }
}
