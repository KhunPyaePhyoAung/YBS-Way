package me.khun.ybsway.application;

import android.content.Context;

import me.khun.ybsway.mapper.BusMapper;
import me.khun.ybsway.mapper.BusStopMapper;
import me.khun.ybsway.mapper.DefaultBusMapper;
import me.khun.ybsway.mapper.DefaultBusStopMapper;
import me.khun.ybsway.mapper.DefaultLanguageMapper;
import me.khun.ybsway.mapper.LanguageMapper;
import me.khun.ybsway.repository.BusRepository;
import me.khun.ybsway.repository.BusStopRepository;
import me.khun.ybsway.repository.JsonFileBusRepositoryImpl;
import me.khun.ybsway.repository.JsonFileBusStopRepositoryImpl;
import me.khun.ybsway.service.BusService;
import me.khun.ybsway.service.BusServiceImpl;
import me.khun.ybsway.service.BusStopService;
import me.khun.ybsway.service.BusStopServiceImpl;

public class YBSWayApplication extends android.app.Application {
    public static final int DEFAULT_BUS_STOP_LIST_SIZE = 500;
    public static final Language DEFAULT_LANGUAGE = Language.ENGLISH;

    public static BusRepository busRepository;
    public static BusStopRepository busStopRepository;
    public static BusService busService;
    public static BusStopService busStopService;
    public static BusMapper busMapper;
    public static BusStopMapper busStopMapper;
    public static LanguageMapper languageMapper;

    private static LanguageConfig LANGUAGE_CONFIG;
    private static YBSWayApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LANGUAGE_CONFIG = LanguageConfig.getInstance(getAppContext());
        busRepository = JsonFileBusRepositoryImpl.getInstance(getAppContext());
        busStopRepository = JsonFileBusStopRepositoryImpl.getInstance(getAppContext());
        busStopService = BusStopServiceImpl.getInstance(busStopRepository);
        busService = BusServiceImpl.getInstance(busRepository, busStopService);
        busStopMapper = new DefaultBusStopMapper(LANGUAGE_CONFIG);
        busMapper = new DefaultBusMapper(LANGUAGE_CONFIG, busStopMapper);
        languageMapper = new DefaultLanguageMapper();
    }

    public static LanguageConfig languageConfig() {
        return LANGUAGE_CONFIG;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
