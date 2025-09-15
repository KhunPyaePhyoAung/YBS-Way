package me.khun.ybsway.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;

public class BaseMapViewModel extends ViewModel {

    private BusService busService;
    private BusMapper busMapper;

    private final MutableLiveData<BusStopView> selectedBusStopViewData = new MutableLiveData<>();
    private final MutableLiveData<List<BusView>> relatedBusViewListData = new MutableLiveData<>();

    public BaseMapViewModel(BusMapper busMapper, BusService busService) {
        this.busMapper = busMapper;
        this.busService = busService;
    }

    public LiveData<BusStopView> getSelectedBusStopData() {
        return selectedBusStopViewData;
    }

    public void setSelectedBusStop(BusStopView busStopView) {
        selectedBusStopViewData.setValue(busStopView);

        if (busStopView != null) {
            List<Bus> busList = busService.getBusListByBusStopId(busStopView.getId());
            relatedBusViewListData.setValue(busMapper.mapToBusViewList(busList));
        } else {
            relatedBusViewListData.setValue(Collections.emptyList());
        }
    }

    public LiveData<List<BusView>> getRelatedBusStopData() {
        return relatedBusViewListData;
    }

}
