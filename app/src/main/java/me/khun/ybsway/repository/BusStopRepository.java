package me.khun.ybsway.repository;

import java.io.Serializable;
import java.util.List;

import me.khun.ybsway.entity.BusStop;

public interface BusStopRepository extends Serializable {
    List<BusStop> getAll();

    BusStop findOneById(Integer id);
}
