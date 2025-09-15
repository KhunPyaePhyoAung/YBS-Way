package me.khun.ybsway.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.entity.Route;
import me.khun.ybsway.repository.BusRepository;

public class BusServiceImpl implements BusService {

    private static volatile BusServiceImpl instance;
    private BusRepository busRepository;
    private BusStopService busStopService;
    private List<Bus> busList;

    public static BusServiceImpl getInstance(BusRepository busRepository, BusStopService busStopService) {
        if ( instance == null ) {
            synchronized(BusServiceImpl.class) {
                if (instance == null) {
                    instance = new BusServiceImpl(busRepository, busStopService);
                }
            }
        }
        return instance;
    }

    private BusServiceImpl(BusRepository busRepository, BusStopService busStopService) {
        this.busRepository = busRepository;
        this.busStopService = busStopService;

        busList = busRepository.getAll();
        for ( Bus bus : busList ) {
            Route route = new Route(busStopService.findAllByIds(bus.getBusStopIdList()));
            bus.setRoute(route);
        }
    }

    @Override
    public List<Bus> getAll() {
        return List.copyOf(busList);
    }

    @Override
    public Bus findOneByRouteId(String routeId) {
        Bus bus = busRepository.findOneByRouteId(routeId);
        Route route = new Route(busStopService.findAllByIds(bus.getBusStopIdList()));
        bus.setRoute(route);
        return bus;
    }

    @Override
    public List<Bus> getBusListByBusStopId(Integer busStopId) {
        List<Bus> resultList = new ArrayList<>(200);
        for ( Bus bus : busList ) {
            if ( bus.getBusStopIdList().contains(busStopId) ) {
                resultList.add(bus);
            }
        }
        return List.copyOf(resultList);
    }

}
