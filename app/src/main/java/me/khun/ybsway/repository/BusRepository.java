package me.khun.ybsway.repository;

import java.util.List;

import me.khun.ybsway.entity.Bus;

public interface BusRepository {
    List<Bus> getAll();

    Bus findOneByRouteId(String routeId);
}
