package me.khun.ybsway.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.khun.ybsway.view.BusStopSearchHistoryItem;

public class BusStopSearchHistoryManager {

    private static final String PREFS_NAME = "search_prefs";
    private static final String KEY_HISTORY = "bus_stop_search_history";
    private static final int MAX_HISTORY = 20;

    private SharedPreferences prefs;
    private Gson gson;

    public BusStopSearchHistoryManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addSearchQuery(BusStopSearchHistoryItem item) {
        List<BusStopSearchHistoryItem> history = getHistory();

        // Remove if already exists (avoid duplicates)
        history.remove(item);

        // Add at the top
        history.add(0, item);

        // Keep only latest MAX_HISTORY items
        if (history.size() > MAX_HISTORY) {
            history = history.subList(0, MAX_HISTORY);
        }

        saveHistory(history);
    }

    public List<BusStopSearchHistoryItem> getHistory() {
        String json = prefs.getString(KEY_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<BusStopSearchHistoryItem>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private void saveHistory(List<BusStopSearchHistoryItem> history) {
        String json = gson.toJson(history);
        prefs.edit().putString(KEY_HISTORY, json).apply();
    }

    public void clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply();
    }
}
