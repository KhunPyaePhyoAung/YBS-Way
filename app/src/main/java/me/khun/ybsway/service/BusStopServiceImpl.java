package me.khun.ybsway.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.repository.BusStopRepository;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class BusStopServiceImpl implements BusStopService {

    private static volatile BusStopServiceImpl instance;
    private final BusStopRepository busStopRepository;

    public static BusStopServiceImpl getInstance(BusStopRepository busStopRepository) {
        if (instance == null) {
            synchronized (BusStopServiceImpl.class) {
                if (instance == null) {
                    instance = new BusStopServiceImpl(busStopRepository);
                }
            }
        }
        return instance;
    }

    private BusStopServiceImpl(BusStopRepository busStopRepository) {
        this.busStopRepository = busStopRepository;
    }

    @Override
    public List<BusStop> getAll() {
        return busStopRepository.getAll();
    }

    @Override
    public BusStop findOneById(Integer id) {
        return busStopRepository.findOneById(id);
    }

    @Override
    public List<BusStop> findAllByIds(List<Integer> idList) {
        List<BusStop> allBusStopList = getAll().stream().filter(busStop -> idList.contains(busStop.getId())).collect(Collectors.toList());
        List<BusStop> resultBusStopList = new ArrayList<>(allBusStopList.size());

        for ( Integer id : idList ) {
            for ( BusStop busStop : allBusStopList ) {
                if ( Objects.equals(busStop.getId(), id) ) {
                    resultBusStopList.add(busStop);
                }
            }
        }

        return resultBusStopList;
    }

    @Override
    public List<BusStop> searchFromList(String keyword, int minRatio, List<BusStop> sourceList) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Map<BusStop, Integer> resultMap = new HashMap<>();
        keyword = keyword.toLowerCase().trim();
        String[] tokens = keyword.split("\\s+");


        for (BusStop bs : sourceList) {
            for (String token : tokens) {
                int nameMmRatio = getRatio(bs.getNameMM().toLowerCase(), token);
                int nameEnRatio = getRatio(bs.getNameEN().toLowerCase(), token);
                int roadNameMmRatio = getRatio(bs.getRoadNameMM().toLowerCase(), token);
                int roadNameEnRatio = getRatio(bs.getRoadNameEN().toLowerCase(), token);
                int townshipNameMmRatio = getRatio(bs.getTownshipMM().toLowerCase(), token);
                int townshipNameEnRatio = getRatio(bs.getTownshipEN().toLowerCase(), token);

                int[] ratios = {nameMmRatio * 2, nameEnRatio * 2, roadNameMmRatio, roadNameEnRatio, townshipNameMmRatio, townshipNameEnRatio};

                if (nameMmRatio >= minRatio || nameEnRatio >= minRatio
                        || roadNameMmRatio >= minRatio || roadNameEnRatio >= minRatio
                        || townshipNameMmRatio >= minRatio || townshipNameEnRatio >= minRatio) {

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
        int tolerence = 5;
        if (target.length() > source.length() + tolerence) {
            return 0;
        }

        return FuzzySearch.partialRatio(source, target);
    }
}
