package me.khun.ybsway.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.view.BusView;

public class BusListViewModel extends ViewModel {

    private BusMapper busMapper;
    private BusService busService;
    private final MutableLiveData<List<BusView>> busList = new MutableLiveData<List<BusView>>();

    public BusListViewModel(BusMapper busMapper, BusService busService) {
        this.busMapper = busMapper;
        this.busService = busService;
    }

    public void loadBusData() {
        List<Bus> buses = busService.getAll();
        List<BusView> busViews = busMapper.mapToBusViewList(buses);
        busList.setValue(busViews);
    }

    public LiveData<List<BusView>> getAllBusListData() {
        return busList;
    }
}
