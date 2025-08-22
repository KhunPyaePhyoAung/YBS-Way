package me.khun.ybsway.mapper;

import java.util.List;

import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.view.BusStopView;

public interface BusStopMapper {
    BusStopView mapToBusStopView(BusStop busStop);

    List<BusStopView> mapToBusStopList(List<BusStop> busStopList);
}
