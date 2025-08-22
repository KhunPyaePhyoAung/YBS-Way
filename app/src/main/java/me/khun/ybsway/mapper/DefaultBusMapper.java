package me.khun.ybsway.mapper;

import java.util.List;
import java.util.stream.Collectors;

import me.khun.ybsway.R;
import me.khun.ybsway.application.Language;
import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.view.BusView;

public class DefaultBusMapper implements BusMapper {

    private static final int AIRPLANE_ICON = R.drawable.plane;
    private final BusStopMapper busStopMapper;

    public DefaultBusMapper(BusStopMapper busStopMapper) {
        this.busStopMapper = busStopMapper;
    }

    @Override
    public BusView mapToBusView(Bus bus, Language language) {
        BusView busView = new BusView();
        busView.setRouteId(bus.getRouteId());
        busView.setName(bus.getName());
        busView.setNumber(bus.getNumber());
        busView.setOriginName(bus.getOriginName());
        busView.setDestinationName(bus.getDestinationName());
        busView.setHexColorCode(bus.getHexColorCode());
        busView.setBusStopIdList(bus.getBusStopIdList());
        busView.setRouteCoordinateList(bus.getRouteCoordinateList());
        busView.setBusStopViewList(busStopMapper.mapToBusStopList(bus.getRoute().getBusStopList(), language));
        if (bus.getName().contains("Airport")) {
            busView.setDisplayIconId(AIRPLANE_ICON);
        }

        return busView;
    }

    @Override
    public List<BusView> mapToBusViewList(List<Bus> busList, Language language) {
        return busList.stream().map(bus -> mapToBusView(bus, language)).collect(Collectors.toList());
    }
}
