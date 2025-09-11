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

        for (String token : tokens) {
            for (BusStop bs : sourceList) {
                int nameMmRatio = FuzzySearch.partialRatio(token, bs.getNameMM().toLowerCase());
                int nameEnRatio = FuzzySearch.partialRatio(token, bs.getNameEN().toLowerCase());
                int roadNameMmRatio = FuzzySearch.partialRatio(token, bs.getRoadNameMM().toLowerCase());
                int roadNameEnRatio = FuzzySearch.partialRatio(token, bs.getRoadNameEN().toLowerCase());
                int townshipNameMmRatio = FuzzySearch.partialRatio(token, bs.getTownshipMM().toLowerCase());
                int townshipNameEnRatio = FuzzySearch.partialRatio(token, bs.getTownshipEN().toLowerCase());

                int[] ratios = {nameMmRatio * 2, nameEnRatio * 2, roadNameMmRatio, roadNameEnRatio, townshipNameMmRatio, townshipNameEnRatio};

                if (nameMmRatio >= minRatio || nameEnRatio >= minRatio
                        || roadNameMmRatio >= minRatio || roadNameEnRatio >= minRatio
                        || townshipNameMmRatio >= minRatio || townshipNameEnRatio >= minRatio) {

                    int max = Arrays.stream(ratios).max().orElse(0);
                    double average = Arrays.stream(ratios).average().orElse(0);
                    int score = (int) (max + average);

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
}
