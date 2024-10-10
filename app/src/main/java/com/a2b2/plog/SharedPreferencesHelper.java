package com.a2b2.plog;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_SELECTED_ROUTE = "selected_route";

    private SharedPreferences preferences;
    private Gson gson;

    public SharedPreferencesHelper(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveRoute(Route route) {
        SharedPreferences.Editor editor = preferences.edit();
        String routeJson = gson.toJson(route);
        editor.putString(KEY_SELECTED_ROUTE, routeJson);
        editor.apply();
    }

    public Route getRoute() {
        String routeJson = preferences.getString(KEY_SELECTED_ROUTE, null);
        return gson.fromJson(routeJson, Route.class);
    }
}
