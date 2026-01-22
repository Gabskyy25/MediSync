package com.example.medisync;

import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlarmStorage {

    public static void save(SharedPreferences p, List<AlarmModel> list) {
        JSONArray arr = new JSONArray();
        try {
            for (AlarmModel m : list) {
                JSONObject o = new JSONObject();
                o.put("id", m.id);
                o.put("d", m.description);
                o.put("h", m.hour);
                o.put("m", m.minute);
                o.put("e", m.enabled);
                // Also save the days list to JSON
                o.put("days", new JSONArray(m.days));
                arr.put(o);
            }
        } catch (Exception ignored) {}
        p.edit().putString("data", arr.toString()).apply();
    }

    public static List<AlarmModel> load(SharedPreferences p) {
        List<AlarmModel> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(p.getString("data", "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                // Create the list of days from the JSON array
                List<Integer> days = new ArrayList<>();
                if (o.has("days")) {
                    JSONArray daysArray = o.getJSONArray("days");
                    for (int j = 0; j < daysArray.length(); j++) {
                        days.add(daysArray.getInt(j));
                    }
                }

                // 1. Create the AlarmModel object using the matching constructor
                AlarmModel model = new AlarmModel(
                        o.getString("id"),
                        o.getString("d"),
                        o.getBoolean("e"),
                        days
                );

                // 2. Set the hour and minute fields after the object is created
                model.hour = o.getInt("h");
                model.minute = o.getInt("m");

                list.add(model);
            }
        } catch (Exception ignored) {
            // It's often better to log the exception for debugging
            // e.g., android.util.Log.e("AlarmStorage", "Error loading alarms", ignored);
        }
        return list;
    }
}
