package me.khun.ybsway.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.khun.ybsway.application.Language;
import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.view.BusStopView;

public class DefaultBusStopMapper implements BusStopMapper {

    @Override
    public BusStopView mapToBusStopView(BusStop busStop, Language language) {
        BusStopView busStopView = new BusStopView();
        busStopView.setId(busStop.getId());
        busStopView.setCoordinate(busStop.getCoordinate());

        switch (language) {
            case BURMESE:
            default:
                busStopView.setName(busStop.getNameMM());
                busStopView.setStreetName(busStop.getStreetMM());
                busStopView.setTownshipName(busStop.getTownshipMM());
                break;
            case ENGLISH:
                busStopView.setName(busStop.getNameEN());
                busStopView.setStreetName(busStop.getStreetEN());
                busStopView.setTownshipName(busStop.getTownshipEN());
                break;
        }

        return busStopView;
    }

    @Override
    public List<BusStopView> mapToBusStopList(List<BusStop> busStopList, Language language) {
        return busStopList.stream().map(busStop -> mapToBusStopView(busStop, language)).collect(Collectors.toList());
    }
}
