package me.khun.ybsway.mapper;

import java.util.List;
import java.util.stream.Collectors;

import me.khun.ybsway.R;
import me.khun.ybsway.application.LanguageConfig;
import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.view.BusView;

public class DefaultBusMapper implements BusMapper {

    private static final int AIRPLANE_ICON = R.drawable.bus_list_item_plane;
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
        busView.setHexColorCode(bus.getHexColorCode());
        busView.setBusStopIdList(bus.getBusStopIdList());
        busView.setRouteCoordinateList(bus.getRouteCoordinateList());
        busView.setBusStopViewList(busStopMapper.mapToBusStopList(bus.getRoute().getBusStopList()));


        switch (languageConfig.getCurrentLanguage()) {
            case BURMESE:
                busView.setPrefixName("ဝိုင်ဘီအက်စ်");
                busView.setName(bus.getNameMM());
                busView.setSubName(bus.getSubNameMM());
                busView.setOriginName(bus.getOriginNameMM());
                busView.setDestinationName(bus.getDestinationNameMM());
                break;
            case ENGLISH:
            default:
                busView.setPrefixName("YBS");
                busView.setName(bus.getNameEN());
                busView.setSubName(bus.getSubNameEN());
                busView.setOriginName(bus.getOriginNameEN());
                busView.setDestinationName(bus.getDestinationNameEN());
                break;
        }

        if (bus.getNameEN().equals("APS")) {
            busView.setDisplayIconId(AIRPLANE_ICON);
        }

        return busView;
    }

    @Override
    public List<BusView> mapToBusViewList(List<Bus> busList) {
        return busList.stream().map(this::mapToBusView).collect(Collectors.toList());
    }
}
