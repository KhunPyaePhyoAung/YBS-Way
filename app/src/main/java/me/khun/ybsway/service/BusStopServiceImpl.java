package me.khun.ybsway.service;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import me.khun.ybsway.YBSWayApplication;
import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.repository.BusStopRepository;

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
}
