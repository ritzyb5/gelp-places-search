package com.cs571.barakol.placessearch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoTab extends Fragment {
    CustomPlaceDetails placeDetailsObj;
    String jsonStr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_details_info, container, false);

        jsonStr = getArguments().getString("PLACES_JSON");

        return view;
    }

    public static InfoTab newInstance(String resultJson) {
        InfoTab fragment = new InfoTab();
        Bundle args = new Bundle();
        args.putString("PLACES_JSON",resultJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            placeDetailsObj = new CustomPlaceDetails(jsonObject.getString("place_id"),jsonObject.getString("name"),jsonObject.getString("formatted_address"),
                    jsonObject.getString("international_phone_number"),jsonObject.getInt("price_level"),jsonObject.getDouble("rating")
                    ,jsonObject.getString("url"),jsonObject.getString("website"));

            TextView address = view.findViewById(R.id.addrTextView);
            TextView phone_number = view.findViewById(R.id.phNumTextView);
            TextView price_level = view.findViewById(R.id.priceLevelTextView);
            RatingBar rating = view.findViewById(R.id.ratingTextView);
            TextView website_url = view.findViewById(R.id.websiteTextView);
            TextView googlePage_url = view.findViewById(R.id.gPageTextView);

            address.setText(jsonObject.getString("formatted_address"));
            phone_number.setText(jsonObject.getString("international_phone_number"));

            //Setting price level
            int intPriceLevel = jsonObject.getInt("price_level");
            String strPriceLevel="$";
            if(intPriceLevel==0)
                strPriceLevel="Free";
            else{
                for(int i=2;i<=intPriceLevel;i++)
                    strPriceLevel += "$";
            }
            price_level.setText(strPriceLevel);

            Double rate_val = jsonObject.getDouble("rating");
            Float rate_float = rate_val.floatValue();
            rating.setRating(rate_float);

            website_url.setText(jsonObject.getString("website"));
            googlePage_url.setText(jsonObject.getString("url"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
