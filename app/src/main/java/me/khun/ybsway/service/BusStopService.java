package me.khun.ybsway.service;

import java.util.List;

import me.khun.ybsway.entity.BusStop;

public interface BusStopService {
    List<BusStop> getAll();

    BusStop findOneById(Integer id);

    List<BusStop> findAllByIds(List<Integer> idList);

    List<BusStop> searchFromList(String keyword, int minRatio, List<BusStop> sourceList);
}
