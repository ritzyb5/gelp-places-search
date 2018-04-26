package com.cs571.barakol.placessearch;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FavTab extends Fragment {
    public static final String ARG_ITEM_ID = "favorite_list";

    RecyclerView favoriteList;
    CustomSharedPreference customSharedPreference;
    ArrayList<PlacesSearchResult> favorites;

    Activity activity;
    PlacesSearchResultAdapter placesSearchResultAdapter;

    TextView errorText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Get favorite items from CustomSharedPreferences.
        customSharedPreference = new CustomSharedPreference();
        favorites = customSharedPreference.getFavorites(activity);

        errorText = view.findViewById(R.id.error_favTextView);

        if (favorites == null) {
            errorText.setVisibility(View.VISIBLE);
        } else {

            if (favorites.size() == 0) {
                errorText.setVisibility(View.VISIBLE);
            }
            else{
                errorText.setVisibility(View.INVISIBLE);

                favoriteList = (RecyclerView) view.findViewById(R.id.favRecyclerView);
                favoriteList.setHasFixedSize(true);
                favoriteList.setLayoutManager(new LinearLayoutManager(getContext()));

                if (favorites != null) {
                    placesSearchResultAdapter = new PlacesSearchResultAdapter(activity, favorites, "favTab");
                    favoriteList.setAdapter(placesSearchResultAdapter);

                }
            }
//            placesSearchResultAdapter.notifyDataSetChanged();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        favorites = customSharedPreference.getFavorites(activity);

        if (favorites == null) {
            errorText.setVisibility(View.VISIBLE);
        } else {

            if (favorites.size() == 0) {
                errorText.setVisibility(View.VISIBLE);
            }
            else{
                errorText.setVisibility(View.INVISIBLE);
                favoriteList = getActivity().findViewById(R.id.favRecyclerView);
                favoriteList.setHasFixedSize(true);
                favoriteList.setLayoutManager(new LinearLayoutManager(getContext()));

                if (favorites != null) {
                    placesSearchResultAdapter = new PlacesSearchResultAdapter(activity, favorites, "favTab");
                    favoriteList.setAdapter(placesSearchResultAdapter);

                }
            }
        }
//        placesSearchResultAdapter.notifyDataSetChanged();
    }
}
