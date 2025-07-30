package me.khun.ybsway.service;

import java.util.List;

import me.khun.ybsway.entity.Bus;

public interface BusService {
    List<Bus> getAll();

    Bus findOneByRouteId(String routeId);
}
