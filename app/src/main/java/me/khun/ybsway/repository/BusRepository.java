package me.khun.ybsway.repository;

import java.io.Serializable;
import java.util.List;

import me.khun.ybsway.entity.Bus;

public interface BusRepository extends Serializable {
    List<Bus> getAll();

    Bus findOneByRouteId(String routeId);
}
