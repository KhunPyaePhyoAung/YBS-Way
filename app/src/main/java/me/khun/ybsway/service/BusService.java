package me.khun.ybsway.service;

import java.util.List;

import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.entity.BusStop;

public interface BusService {
    List<Bus> getAll();

    Bus findOneByRouteId(String routeId);

    List<Bus> getBusListByBusStopId(Integer busStopId);

    List<Bus> searchFromList(String keyword, int minRatio, List<Bus> sourceList);
}
