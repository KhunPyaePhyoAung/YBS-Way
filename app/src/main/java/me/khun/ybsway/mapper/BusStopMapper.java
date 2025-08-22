package me.khun.ybsway.mapper;

import java.util.List;

import me.khun.ybsway.application.Language;
import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.view.BusStopView;

public interface BusStopMapper {
    BusStopView mapToBusStopView(BusStop busStop, Language language);

    List<BusStopView> mapToBusStopList(List<BusStop> busStopList, Language language);
}
