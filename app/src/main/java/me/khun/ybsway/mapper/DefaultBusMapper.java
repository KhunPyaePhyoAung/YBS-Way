package me.khun.ybsway.mapper;

import java.util.List;
import java.util.stream.Collectors;

import me.khun.ybsway.R;
import me.khun.ybsway.application.LanguageConfig;
import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.view.BusView;

public class DefaultBusMapper implements BusMapper {

    private static final int AIRPLANE_ICON = R.drawable.aeroplane;
    private final BusStopMapper busStopMapper;
    private final LanguageConfig languageConfig;

    public DefaultBusMapper(LanguageConfig languageConfig, BusStopMapper busStopMapper) {
        this.busStopMapper = busStopMapper;
        this.languageConfig = languageConfig;
    }

    @Override
    public BusView mapToBusView(Bus bus) {
        BusView busView = new BusView();
        busView.setRouteId(bus.getRouteId());
        busView.setName(bus.getName());
        busView.setNumber(bus.getNumber());
        busView.setOriginName(bus.getOriginName());
        busView.setDestinationName(bus.getDestinationName());
        busView.setHexColorCode(bus.getHexColorCode());
        busView.setBusStopIdList(bus.getBusStopIdList());
        busView.setRouteCoordinateList(bus.getRouteCoordinateList());
        busView.setBusStopViewList(busStopMapper.mapToBusStopList(bus.getRoute().getBusStopList()));
        if (bus.getName().contains("Airport")) {
            busView.setDisplayIconId(AIRPLANE_ICON);
        }

        return busView;
    }

    @Override
    public List<BusView> mapToBusViewList(List<Bus> busList) {
        return busList.stream().map(this::mapToBusView).collect(Collectors.toList());
    }
}
