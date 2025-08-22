package me.khun.ybsway.mapper;

import java.util.List;
import java.util.stream.Collectors;

import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.view.BusStopView;

public class DefaultBusStopMapper implements BusStopMapper {

    @Override
    public BusStopView mapToBusStopView(BusStop busStop) {
        BusStopView busStopView = new BusStopView();
        busStopView.setId(busStop.getId());
        busStopView.setName(busStop.getNameMM());
        busStopView.setStreetName(busStop.getNameMM());
        busStopView.setTownshipName(busStop.getTownshipMM());
        busStopView.setCoordinate(busStop.getCoordinate());

        return busStopView;
    }

    @Override
    public List<BusStopView> mapToBusStopList(List<BusStop> busStopList) {
        return busStopList.stream().map(this::mapToBusStopView).collect(Collectors.toList());
    }
}
