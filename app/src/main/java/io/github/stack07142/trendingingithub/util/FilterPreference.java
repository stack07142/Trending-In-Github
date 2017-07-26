package io.github.stack07142.trendingingithub.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class FilterPreference {

    // TODO : StringDef
    public static final String LANGUAGE = "Language";
    public static final String CREATED = "Created";

    public static void setStringArrayPref(Context context, String key, ArrayList<String> values) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        if (values != null && !values.isEmpty()) {

            JSONArray a = new JSONArray();

            for (int i = 0; i < values.size(); i++) {

                a.put(values.get(i));
            }

            editor.putString(key, a.toString());
        } else {

            editor.putString(key, null);
        }

        editor.apply();
    }

    public static ArrayList<String> getStringArrayPref(Context context, String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<>();

        if (json != null) {

            try {

                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {

                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

        return urls;
    }
}
