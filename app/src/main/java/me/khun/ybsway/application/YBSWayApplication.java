package me.khun.ybsway.application;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import me.khun.ybsway.manager.BusStopSearchHistoryManager;
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
    public static final String BUS_REPO_SERIALIZATION_NAME = "bus_repo.ser";
    public static final String BUS_STOP_REPO_SERIALIZATION_NAME = "bus_stop_repo.ser";

    public static BusRepository busRepository;
    public static BusStopRepository busStopRepository;
    public static BusService busService;
    public static BusStopService busStopService;
    public static BusMapper busMapper;
    public static BusStopMapper busStopMapper;
    public static LanguageMapper languageMapper;
    public static BusStopSearchHistoryManager busStopSearchHistoryManager;

    private static LanguageConfig LANGUAGE_CONFIG;
    private static YBSWayApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LANGUAGE_CONFIG = LanguageConfig.getInstance(getAppContext());
        busRepository = JsonFileBusRepositoryImpl.getInstance(getAppContext());
//        busRepository = deserialize(BusRepository.class, BUS_REPO_SERIALIZATION_NAME);
        busStopRepository = JsonFileBusStopRepositoryImpl.getInstance(getAppContext());
//        busStopRepository = deserialize(BusStopRepository.class, BUS_STOP_REPO_SERIALIZATION_NAME);
        busStopService = BusStopServiceImpl.getInstance(busStopRepository);
        busService = BusServiceImpl.getInstance(busRepository, busStopService);
        busStopMapper = new DefaultBusStopMapper(LANGUAGE_CONFIG);
        busMapper = new DefaultBusMapper(LANGUAGE_CONFIG, busStopMapper);
        languageMapper = new DefaultLanguageMapper();
        busStopSearchHistoryManager = new BusStopSearchHistoryManager(getAppContext());

//        serialize(busRepository, BUS_REPO_SERIALIZATION_NAME);
//        serialize(busStopRepository, BUS_STOP_REPO_SERIALIZATION_NAME);
    }

    public static LanguageConfig languageConfig() {
        return LANGUAGE_CONFIG;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static <T extends Serializable> void serialize(T obj, String name) {
        try {
            File file = new File(getAppContext().getFilesDir(), name);
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
            fileOut.close();
            System.out.printf("%s serialized.%n", name);
        } catch (IOException i) {
            System.out.printf("%s not serialized.%n", name);
            throw new RuntimeException(i.getMessage());
        }
    }

    public static <T extends Serializable> T deserialize(Class<T> clazz, String name) {
        try {
            File file = new File(getAppContext().getFilesDir(), name);
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            T repo = (T) in.readObject();
            in.close();
            fileIn.close();
            return repo;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
