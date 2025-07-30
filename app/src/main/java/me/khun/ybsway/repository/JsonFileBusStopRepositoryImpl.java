package me.khun.ybsway.repository;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import me.khun.ybsway.entity.BusStop;
import me.khun.ybsway.entity.Coordinate;

public class JsonFileBusStopRepositoryImpl implements BusStopRepository {

    private final List<BusStop> busStopList = new LinkedList<>();

    private static volatile JsonFileBusStopRepositoryImpl instance = null;

    public static JsonFileBusStopRepositoryImpl getInstance(Context context) {
        if (instance == null) {
            synchronized (JsonFileBusStopRepositoryImpl.class) {
                if (instance == null) {
                    instance = new JsonFileBusStopRepositoryImpl(context);
                }
            }
        }
        return instance;
    }

    private JsonFileBusStopRepositoryImpl(Context context) {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open("bus_stop.json")))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray busStopArray = new JSONArray(sb.toString());
            for (int i = 0; i < busStopArray.length(); i++) {
                JSONObject busStopObject = busStopArray.getJSONObject(i);

                BusStop bs = new BusStop();
                bs.setId(busStopObject.getInt("id"));
                bs.setNameMM(busStopObject.getString("name_mm"));
                bs.setNameEN(busStopObject.getString("name_en"));
                bs.setStreetMM(busStopObject.getString("road_mm"));
                bs.setStreetEN(busStopObject.getString("road_en"));
                bs.setTownshipMM(busStopObject.getString("township_mm"));
                bs.setTownshipEN(busStopObject.getString("township_en"));
                bs.setCoordinate(new Coordinate(busStopObject.getDouble("lat"), busStopObject.getDouble("lng")));

                busStopList.add(bs);
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BusStop> getAll() {
        return List.copyOf(busStopList);
    }

    @Override
    public BusStop findOneById(Integer id) {
        for (BusStop busStop : busStopList) {
            if (busStop.getId().equals(id)) {
                return busStop;
            }
        }
        return null;
    }
}
