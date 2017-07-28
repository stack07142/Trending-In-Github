package io.github.stack07142.trendingingithub.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class FilterPreference {

    public static boolean editedFlag = false;

    public static final String LANGUAGE = "Language";
    public static final String CREATED = "Created";
    public static final String EDITED = "Edited";

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
        // 최초 실행 시 preference에 저장된 것이 없는 경우
        else {

            switch (key) {
                case LANGUAGE:

                    urls.add("All");
                    setStringArrayPref(context, LANGUAGE, urls);
                    break;
                case CREATED:

                    urls.add("this week");
                    setStringArrayPref(context, CREATED, urls);
                    break;
                case EDITED:

                    urls.add("All");
                    urls.add("Python");
                    urls.add("Java");
                    urls.add("C++");
                    urls.add("C#");
                    urls.add("C");
                    urls.add("JavaScript");
                    urls.add("Ruby");
                    urls.add("PHP");
                    urls.add("Haskell");
                    urls.add("Go");
                    urls.add("Scala");
                    urls.add("Swift");
                    urls.add("Kotlin");
                    urls.add("Perl");
                    urls.add("Objective-C");
                    urls.add("R");
                    urls.add("Visual Basic");
                    urls.add("Lua");
                    urls.add("Clojure");
                    urls.add("TypeScript");
                    urls.add("Rust");
                    urls.add("Tcl");
                    urls.add("CoffeeScript");
                    urls.add("Elixir");
                    urls.add("Julia");
                    urls.add("Crystal");
                    urls.add("PowerShell");
                    urls.add("Groovy");
                    urls.add("CSS");
                    urls.add("Shell");
                    urls.add("Matlab");
                    setStringArrayPref(context, EDITED, urls);
                    break;
            }
        }

        return urls;
    }
}
