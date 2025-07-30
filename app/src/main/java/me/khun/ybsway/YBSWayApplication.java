package me.khun.ybsway;

import android.content.Context;

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

    public static BusRepository busRepository;
    public static BusStopRepository busStopRepository;
    public static BusService busService;
    public static BusStopService busStopService;

    private static YBSWayApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        busRepository = JsonFileBusRepositoryImpl.getInstance(getAppContext());
        busStopRepository = JsonFileBusStopRepositoryImpl.getInstance(getAppContext());
        busStopService = BusStopServiceImpl.getInstance(busStopRepository);
        busService = BusServiceImpl.getInstance(busRepository, busStopService);
    }

    public static int dpToPx(double dp) {
        return (int) (dp * getAppContext().getResources().getDisplayMetrics().density);
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
