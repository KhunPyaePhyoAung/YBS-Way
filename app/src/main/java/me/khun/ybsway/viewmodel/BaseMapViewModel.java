package me.khun.ybsway.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;

public class BaseMapViewModel extends ViewModel {

    private final BusService busService;
    private final BusMapper busMapper;

    private final MutableLiveData<Boolean> ypsToggleData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> anchorToggleData = new MutableLiveData<>();
    private final MutableLiveData<BusStopView> selectedBusStopViewData = new MutableLiveData<>();

    public BaseMapViewModel(BusMapper busMapper, BusService busService) {
        this.busMapper = busMapper;
        this.busService = busService;
        ypsToggleData.setValue(false);
        anchorToggleData.setValue(false);
    }

    public LiveData<BusStopView> getSelectedBusStopData() {
        return selectedBusStopViewData;
    }

    public void setSelectedBusStop(BusStopView busStopView) {
        selectedBusStopViewData.setValue(busStopView);
    }

    public List<BusView> getRelatedBusListByBusStopId(Integer busStopId) {
        List<Bus> busList = busService.getBusListByBusStopId(busStopId);
        return busMapper.mapToBusViewList(busList);
    }

    public void toggleYps() {
        ypsToggleData.setValue(Boolean.FALSE.equals(ypsToggleData.getValue()));
    }

    public LiveData<Boolean> getYpsToggleData() {
        return ypsToggleData;
    }

    public void toggleAnchor() {
        anchorToggleData.setValue(Boolean.FALSE.equals(anchorToggleData.getValue()));
    }

    public LiveData<Boolean> getAnchorToggleData() {
        return anchorToggleData;
    }

}
