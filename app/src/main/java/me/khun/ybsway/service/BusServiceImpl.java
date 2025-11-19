package me.khun.ybsway.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.entity.Route;
import me.khun.ybsway.repository.BusRepository;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class BusServiceImpl implements BusService {

    private static volatile BusServiceImpl instance;
    private final BusRepository busRepository;
    private final BusStopService busStopService;
    private final List<Bus> busList;

    public static BusServiceImpl getInstance(BusRepository busRepository, BusStopService busStopService) {
        if ( instance == null ) {
            synchronized(BusServiceImpl.class) {
                if (instance == null) {
                    instance = new BusServiceImpl(busRepository, busStopService);
                }
            }
        }
        return instance;
    }

    private BusServiceImpl(BusRepository busRepository, BusStopService busStopService) {
        this.busRepository = busRepository;
        this.busStopService = busStopService;

        busList = busRepository.getAll().stream().filter(Bus::isActive).collect(Collectors.toList());
        for ( Bus bus : busList ) {
            Route route = new Route(busStopService.findAllByIds(bus.getBusStopIdList()));
            bus.setRoute(route);
        }
    }

    @Override
    public List<Bus> getAll() {
        return List.copyOf(busList);
    }

    @Override
    public Bus findOneByRouteId(String routeId) {
        Bus bus = busRepository.findOneByRouteId(routeId);
        Route route = new Route(busStopService.findAllByIds(bus.getBusStopIdList()));
        bus.setRoute(route);
        return bus;
    }

    @Override
    public List<Bus> getBusListByBusStopId(Integer busStopId) {
        List<Bus> resultList = new ArrayList<>(200);
        for ( Bus bus : busList ) {
            if ( bus.getBusStopIdList().contains(busStopId) ) {
                resultList.add(bus);
            }
        }
        return List.copyOf(resultList);
    }

    @Override
    public List<Bus> searchFromList(String keyword, int minRatio, List<Bus> sourceList) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Map<Bus, Integer> resultMap = new HashMap<>();
        keyword = keyword.toLowerCase().trim();
        String[] tokens = keyword.split("\\s+");


        for (Bus bs : sourceList) {
            for (String token : tokens) {
                int nameMmRatio = getRatio(bs.getNameMM().toLowerCase(), token);
                int nameEnRatio = getRatio(bs.getNameEN().toLowerCase(), token);
                int subNameMmRatio = bs.getSubNameMM() == null ? 0 : getRatio(bs.getSubNameMM().toLowerCase(), token);
                int subNameEnRatio = bs.getSubNameEN() == null ? 0 : getRatio(bs.getSubNameEN().toLowerCase(), token);
                int originNameMmRatio = getRatio(bs.getOriginNameMM().toLowerCase(), token);
                int originNameEnRatio = getRatio(bs.getOriginNameEN().toLowerCase(), token);
                int destNameMmRatio = getRatio(bs.getDestinationNameMM().toLowerCase(), token);
                int destNameEnRatio = getRatio(bs.getDestinationNameEN().toLowerCase(), token);

                int[] ratios = {
                        nameMmRatio * 2, nameEnRatio * 2,
                        subNameMmRatio * 2, subNameEnRatio * 2,
                        originNameMmRatio, originNameEnRatio,
                        destNameMmRatio, destNameEnRatio};

                if (nameMmRatio >= minRatio || nameEnRatio >= minRatio
                        || subNameMmRatio >= minRatio || subNameEnRatio >= minRatio
                        || originNameMmRatio >= minRatio || originNameEnRatio >= minRatio
                        || destNameMmRatio >= minRatio || destNameEnRatio >= minRatio) {

                    int max = Arrays.stream(ratios).max().orElse(0);
                    double average = Arrays.stream(ratios).average().orElse(0);
                    int bonus = 0;
                    if (bs.getNameMM().toLowerCase().startsWith(token)
                            || bs.getNameEN().toLowerCase().startsWith(token)) {
                        bonus += 100;
                    }
                    if (bs.getNameMM().toLowerCase().equals(token)
                            || bs.getNameEN().toLowerCase().equals(token)) {
                        bonus += 100;
                    }
                    int score = (int) (max + average + bonus);

                    if (resultMap.containsKey(bs)) {
                        resultMap.merge(bs, score, Integer::sum);
                    } else {
                        resultMap.put(bs, score);
                    }
                }
            }
        }

        return resultMap.entrySet().stream().sorted((a, b) -> b.getValue().compareTo(a.getValue())).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private int getRatio(String source, String target) {
        int tolerance = 0;
        if (target.length() > source.length() + tolerance) {
            return 0;
        }

        return FuzzySearch.partialRatio(source, target);
    }

}
