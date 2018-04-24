package com.cs571.barakol.placessearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomSharedPreference {

    public static final String PREFS_NAME = "PLACES_SEARCH";
    public static final String FAVORITES = "Places_Favorite";

    public CustomSharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<PlacesSearchResult> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, PlacesSearchResult place) {
        List<PlacesSearchResult> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<PlacesSearchResult>();
        favorites.add(place);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, PlacesSearchResult place) {
        ArrayList<PlacesSearchResult> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(place);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<PlacesSearchResult> getFavorites(Context context) {
        SharedPreferences settings;
        List<PlacesSearchResult> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            Log.i("FAV_SP","YES");
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            PlacesSearchResult[] favoriteItems = gson.fromJson(jsonFavorites,
                    PlacesSearchResult[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<PlacesSearchResult>(favorites);
        } else
            return null;

        return (ArrayList<PlacesSearchResult>) favorites;
    }
}
