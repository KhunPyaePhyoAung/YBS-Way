package me.khun.ybsway.repository;

import java.util.List;

import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.entity.BusStop;

public interface BusStopRepository {
    List<BusStop> getAll();

    BusStop findOneById(Integer id);
}
