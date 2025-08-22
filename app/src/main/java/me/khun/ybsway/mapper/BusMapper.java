package me.khun.ybsway.mapper;

import java.util.List;

import me.khun.ybsway.application.Language;
import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.view.BusView;

public interface BusMapper {
    BusView mapToBusView(Bus bus, Language language);

    List<BusView> mapToBusViewList(List<Bus> busList, Language language);
}
