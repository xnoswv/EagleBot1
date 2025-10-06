package com.example.overlaybot;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class CoordinatesManager {

    private static final String PREFS = "coords";
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    public CoordinatesManager(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save(String key, int x, int y){
        sharedPreferences.edit().putString(key, gson.toJson(new int[]{x,y})).apply();
    }

    public int[] load(String key){
        String json = sharedPreferences.getString(key,null);
        if(json==null) return null;
        return gson.fromJson(json,int[].class);
    }
}
