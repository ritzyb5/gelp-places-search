package com.cs571.barakol.placessearch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs571.barakol.placessearch.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the
 * interface.
 */
public class ReviewsTab extends Fragment {

    private String jsonStr;
    private ArrayList<CustomReviewDetails> googleReviews;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReviewsTab() {
    }


    public static ReviewsTab newInstance(String resultJSON) {
        ReviewsTab fragment = new ReviewsTab();
        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString("PLACES_JSON",resultJSON);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleReviews = new ArrayList<>();

        if (getArguments() != null) {
            jsonStr = getArguments().getString("PLACES_JSON");
            Log.i("REVIEWS",jsonStr);
        }

        //Get the Google Reviews
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray reviewsArr =jsonObject.getJSONArray("reviews");

            for(int i=0; i<reviewsArr.length(); i++){
                JSONObject reviewsObj = reviewsArr.getJSONObject(i);

                //Rating
                Double rate_val = reviewsObj.getDouble("rating");
                Float rate_float = rate_val.floatValue();

                CustomReviewDetails review = new CustomReviewDetails(reviewsObj.getString("profile_photo_url"),reviewsObj.getString("author_name"),rate_float, reviewsObj.getString("time"),reviewsObj.getString("text"),reviewsObj.getString("author_url"));
                googleReviews.add(review);
            }

        }
        catch(JSONException e){
            e.printStackTrace();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews_list, container, false);

        // Set the adapter
//        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
            Log.i("REVIEWS",googleReviews.get(0).getAuthor_name());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(googleReviews));
//        }
        return view;
    }

}
