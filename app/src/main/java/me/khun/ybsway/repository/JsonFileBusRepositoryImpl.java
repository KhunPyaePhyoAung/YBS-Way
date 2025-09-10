package me.khun.ybsway.repository;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.khun.ybsway.application.YBSWayApplication;
import me.khun.ybsway.entity.Bus;
import me.khun.ybsway.entity.Coordinate;

public class JsonFileBusRepositoryImpl implements BusRepository {

    private final List<Bus> busList = new ArrayList<>(200);

    private static volatile JsonFileBusRepositoryImpl instance = null;

    public static JsonFileBusRepositoryImpl getInstance(Context context) {
        if (instance == null) {
            synchronized (JsonFileBusStopRepositoryImpl.class) {
                if (instance == null) {
                    instance = new JsonFileBusRepositoryImpl(context);
                }
            }
        }
        return instance;
    }

    private JsonFileBusRepositoryImpl(Context context) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("bus.json")))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray busArray = new JSONArray(sb.toString());
            for (int i = 0; i < busArray.length(); i++) {
                JSONObject busObject = busArray.getJSONObject(i);
                Bus bus = new Bus();
                bus.setNameMM(busObject.getString("name_mm"));
                bus.setNameEN(busObject.getString("name_en"));

                if (!busObject.isNull("sub_name_mm")) {
                    bus.setSubNameMM(busObject.getString("sub_name_mm"));
                }
                if (!busObject.isNull("sub_name_en")) {
                    bus.setSubNameEN(busObject.getString("sub_name_en"));
                }

                bus.setOriginNameMM(busObject.getString("origin_name_mm"));
                bus.setOriginNameEN(busObject.getString("origin_name_en"));
                bus.setDestinationNameMM(busObject.getString("destination_name_mm"));
                bus.setDestinationNameEN(busObject.getString("destination_name_en"));
                bus.setRouteId(busObject.getString("route_id"));
                bus.setHexColorCode(busObject.getString("color"));

                List<Integer> busStopIdList = new ArrayList<>(YBSWayApplication.DEFAULT_BUS_STOP_LIST_SIZE);

                JSONArray routeArray = busObject.getJSONArray("stops");

//                for (int j = 0; j < routeArray.length(); j++) {
//                    busStopIdList.add(routeArray.getInt(j));
//                }

                bus.setBusStopIdList(busStopIdList);

                JSONArray coordinateArray = busObject.getJSONObject("shape").getJSONObject("geometry").getJSONArray("coordinates");
                List<Coordinate> coordinates = new ArrayList<>(coordinateArray.length());

                for (int k = 0; k < coordinateArray.length(); k++) {
                    JSONArray coordinateEntity = coordinateArray.getJSONArray(k);
                    Coordinate coordinate = new Coordinate(coordinateEntity.getDouble(1), coordinateEntity.getDouble(0));
                    coordinates.add(coordinate);
                }

                bus.setRouteCoordinateList(coordinates);

                busList.add(bus);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Bus> getAll() {
        return List.copyOf(busList);
    }

    @Override
    public Bus findOneByRouteId(String routeId) {
        for ( Bus bus : busList) {
            if ( bus.getRouteId().equals(routeId) ) {
                return bus;
            }
        }
        return null;
    }
}
