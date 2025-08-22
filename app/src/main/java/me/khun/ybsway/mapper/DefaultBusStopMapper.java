package me.khun.ybsway.mapper;

import java.util.List;
import java.util.stream.Collectors;

import me.khun.ybsway.application.LanguageConfig;
import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.view.BusStopView;

public class DefaultBusStopMapper implements BusStopMapper {

    private final LanguageConfig languageConfig;

    public DefaultBusStopMapper(LanguageConfig languageConfig) {
        this.languageConfig = languageConfig;
    }

    @Override
    public BusStopView mapToBusStopView(BusStop busStop) {
        BusStopView busStopView = new BusStopView();
        busStopView.setId(busStop.getId());
        busStopView.setCoordinate(busStop.getCoordinate());

        switch (languageConfig.getLanguage()) {
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
    public List<BusStopView> mapToBusStopList(List<BusStop> busStopList) {
        return busStopList.stream().map(this::mapToBusStopView).collect(Collectors.toList());
    }
}
