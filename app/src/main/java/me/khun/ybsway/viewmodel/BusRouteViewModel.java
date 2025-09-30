package me.khun.ybsway.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.view.BusStopView;
import me.khun.ybsway.view.BusView;

public class BusRouteViewModel extends ViewModel {

    private final BusMapper busMapper;
    private final BusStopMapper busStopMapper;
    private final BusService busService;
    private final BusStopService busStopService;
    private final MutableLiveData<BusView> busViewData = new MutableLiveData<>();
    private final MutableLiveData<List<BusStopView>> busStopListData = new MutableLiveData<>();
    private final LiveData<String> toolbarTitle;

    public BusRouteViewModel(Dependencies dependencies) {
        this.busMapper = dependencies.busMapper;
        this.busStopMapper = dependencies.busStopMapper;
        this.busService = dependencies.busService;
        this.busStopService = dependencies.busStopService;
        toolbarTitle = Transformations.map(busViewData, b -> String.format("%s %s %s", b.getPrefixName(), b.getName(), b.getSubName() == null ? "" : String.format("(%s)", b.getSubName())));
    }

    public void loadBusDataByRouteId(String routeId) {
        Bus bus = busService.findOneByRouteId(routeId);
        BusView busView = busMapper.mapToBusView(bus);
        List<BusStop> busStopList = busStopService.findAllByIds(bus.getBusStopIdList());
        busStopListData.setValue(busStopMapper.mapToBusStopList(busStopList));
        busViewData.setValue(busView);
    }

    public LiveData<BusView> getBusData() {
        return busViewData;
    }

    public LiveData<String> getToolbarTitle() {
        return toolbarTitle;
    }

    public static class Dependencies {
        public BusMapper busMapper;
        public BusStopMapper busStopMapper;
        public BusService busService;
        public BusStopService busStopService;
    }
}
