package com.example.medisync;

import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
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
                list.add(new AlarmModel(
                        o.getString("id"),
                        o.getString("d"),
                        o.getInt("h"),
                        o.getInt("m"),
                        o.getBoolean("e"),
                        new ArrayList<>()
                ));
            }
        } catch (Exception ignored) {}
        return list;
    }
}
