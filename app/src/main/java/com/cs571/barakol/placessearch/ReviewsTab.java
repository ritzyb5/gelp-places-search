package com.cs571.barakol.placessearch;

import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cs571.barakol.placessearch.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the
 * interface.
 */
public class ReviewsTab extends Fragment {

    private String jsonStr;
    JSONObject placeDetailsJSON;
    private String URL_DATA;
    private ArrayList<CustomReviewDetails> googleReviews;
    private ArrayList<CustomReviewDetails> yelpReviews;

    private ArrayList<CustomReviewDetails> def_googleReviews;
    private ArrayList<CustomReviewDetails> def_yelpReviews;

    private RecyclerView recyclerView;
    private Context context;
    private String reviewTypeVal;
    private String orderType="Default Order";

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
        yelpReviews = new ArrayList<>();

        def_googleReviews = new ArrayList<>();
        def_yelpReviews = new ArrayList<>();

        if (getArguments() != null) {
            jsonStr = getArguments().getString("PLACES_JSON");
        }

        //Get the Google Reviews
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
//            placeDetailsJSON = jsonObject.getJSONObject("result");
            URL_DATA = getResources().getString(R.string.url)+"/yelpReviews?name="+jsonObject.getString("name").replace(' ','+');
            URL_DATA += "&latitude="+jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
            URL_DATA += "&longitude="+jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");

            String state="",country="",address1="",city="";
            JSONArray address_components = jsonObject.getJSONArray("address_components");
            for(int i = 0; i <address_components.length(); i++){
                JSONObject addr = (JSONObject)address_components.get(i);
                JSONArray addr_types = addr.getJSONArray("types");
                Log.i("YELP_URL",addr_types.get(0).toString());
                if(addr_types.get(0).toString().equals("administrative_area_level_1"))  state = addr.getString("short_name");
                if(addr_types.get(0).toString().equals("country"))  country = addr.getString("short_name");
                if(addr_types.get(0).toString().equals("route"))  address1 = addr.getString("short_name").replace(' ','+');
                if(addr_types.get(0).toString().equals("locality"))  city = addr.getString("short_name").replace(' ','+');

            }

            URL_DATA += "&address1="+address1;//jsonObject.getString("formatted_address").replace(' ','+');
            URL_DATA += "&city="+city;
            URL_DATA += "&state="+state;
            URL_DATA += "&country="+country;


            Log.i("YELP_URL",URL_DATA);

            JSONArray reviewsArr =jsonObject.getJSONArray("reviews");

            for(int i=0; i<reviewsArr.length(); i++){
                JSONObject reviewsObj = reviewsArr.getJSONObject(i);

                //Rating
                Double rate_val = reviewsObj.getDouble("rating");
                Float rate_float = rate_val.floatValue();

                //Date
                Date rating_date = new Date((Long.parseLong(reviewsObj.getString("time")))*1000);
                DateFormat formatted_rating_date = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

                CustomReviewDetails review = new CustomReviewDetails(reviewsObj.getString("profile_photo_url"),reviewsObj.getString("author_name"),rate_float, formatted_rating_date.format(rating_date),reviewsObj.getString("text"),reviewsObj.getString("author_url"));
                googleReviews.add(review);
                def_googleReviews.add(review);
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

        context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

//        Log.i("REVIEWS",googleReviews.get(0).getAuthor_name());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(googleReviews, "google_reviews"));

        Spinner reviewType = view.findViewById(R.id.reviewType);
        reviewType.setOnItemSelectedListener(itemSelectedListener);

        Spinner reviewOrder = view.findViewById(R.id.reviewOrder);
        reviewOrder.setOnItemSelectedListener(reviewOrderItemSelectedListener);
        return view;
    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selectedItem = adapterView.getSelectedItem().toString();
            reviewTypeVal = selectedItem;

            Log.i("REVIEWS",selectedItem);
//                final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

            if(selectedItem.equals("Google Reviews")){
                orderReviews(orderType);
                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(googleReviews,"google_reviews"));
            }
            else{

                RequestQueue queue = Volley.newRequestQueue(context);

                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Fetching Results");
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONArray yelpReviewsArr = new JSONArray(response);
//                                JSONArray yelpReviewsArr = jsonObject.getJSONArray("reviews");

                            for(int i=0; i<yelpReviewsArr.length(); i++){
                                JSONObject reviews = (JSONObject)yelpReviewsArr.get(i);

                                //Rating
                                Double rate_val = reviews.getDouble("rating");
                                Float rate_float = rate_val.floatValue();

                                CustomReviewDetails review = new CustomReviewDetails(reviews.getJSONObject("user").getString("image_url"),reviews.getJSONObject("user").getString("name"),rate_float, reviews.getString("time_created"),reviews.getString("text"),reviews.getString("url"));

                                yelpReviews.add(review);
                                def_yelpReviews.add(review);
                            }

                            orderReviews(orderType);
                            recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(yelpReviews,"yelp_reviews"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error"+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                queue.add(stringRequest);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private AdapterView.OnItemSelectedListener reviewOrderItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selectedItem = adapterView.getSelectedItem().toString();
            orderType = selectedItem;
            orderReviews(selectedItem);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void orderReviews(String selectedItem){
        if(selectedItem.equals("Default Order")){
            if(reviewTypeVal.equals("Google Reviews")){
                googleReviews = def_googleReviews;
                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(googleReviews, "google_reviews"));
            }
            else{
                yelpReviews = def_yelpReviews;
                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(yelpReviews, "yelp_reviews"));
            }

        }
        else if(selectedItem.equals("Highest Rating")){
            if(reviewTypeVal.equals("Google Reviews")){
                Collections.sort(googleReviews, new Comparator<CustomReviewDetails>() {
                    @Override
                    public int compare(CustomReviewDetails reviewDetails, CustomReviewDetails t1) {
                        return (int)(t1.getReview_rating() - reviewDetails.getReview_rating());
                    }
                });

                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(googleReviews, "google_reviews"));
            }
            else{
                Collections.sort(yelpReviews, new Comparator<CustomReviewDetails>() {
                    @Override
                    public int compare(CustomReviewDetails reviewDetails, CustomReviewDetails t1) {
                        return (int)(t1.getReview_rating() - reviewDetails.getReview_rating());
                    }
                });

                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(yelpReviews, "yelp_reviews"));
            }

        }
        else if(selectedItem.equals("Lowest Rating")){
            if(reviewTypeVal.equals("Google Reviews")){
                Collections.sort(googleReviews, new Comparator<CustomReviewDetails>() {
                    @Override
                    public int compare(CustomReviewDetails reviewDetails, CustomReviewDetails t1) {
                        return (int)(reviewDetails.getReview_rating() - t1.getReview_rating());
                    }
                });

                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(googleReviews, "google_reviews"));
            }
            else{
                Collections.sort(yelpReviews, new Comparator<CustomReviewDetails>() {
                    @Override
                    public int compare(CustomReviewDetails reviewDetails, CustomReviewDetails t1) {
                        return (int)(reviewDetails.getReview_rating() - t1.getReview_rating());
                    }
                });

                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(yelpReviews, "yelp_reviews"));
            }
        }
        else if(selectedItem.equals("Most Recent")){
            if(reviewTypeVal.equals("Google Reviews")){
                Collections.sort(googleReviews, new Comparator<CustomReviewDetails>() {
                    @Override
                    public int compare(CustomReviewDetails reviewDetails, CustomReviewDetails t1) {
                        Date date1=null,date2=null;
                        try{
                            date1 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(reviewDetails.getReview_date());
                            date2 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(t1.getReview_date());
                            Log.i("G_D",date1.toString());
                        }
                        catch (ParseException e){
                            e.printStackTrace();
                        }

                        return date2.compareTo(date1);
                    }
                });

                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(googleReviews, "google_reviews"));
            }
            else{
                Collections.sort(yelpReviews, new Comparator<CustomReviewDetails>() {
                    @Override
                    public int compare(CustomReviewDetails reviewDetails, CustomReviewDetails t1) {
                        Date date1=null,date2=null;
                        try{
                            date1 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(reviewDetails.getReview_date());
                            date2 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(t1.getReview_date());
                        }
                        catch (ParseException e){
                            e.printStackTrace();
                        }

                        return date2.compareTo(date1);
                    }
                });

                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(yelpReviews, "yelp_reviews"));
            }

        }
        else if(selectedItem.equals("Least Recent")){
            if(reviewTypeVal.equals("Google Reviews")){
                Collections.sort(googleReviews, new Comparator<CustomReviewDetails>() {
                    @Override
                    public int compare(CustomReviewDetails reviewDetails, CustomReviewDetails t1) {
                        Date date1=null,date2=null;
                        try{
                            date1 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(reviewDetails.getReview_date());
                            date2 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(t1.getReview_date());
                            Log.i("G_D",date1.toString());
                        }
                        catch (ParseException e){
                            e.printStackTrace();
                        }

                        return date1.compareTo(date2);
                    }
                });

                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(googleReviews, "google_reviews"));
            }
            else{
                Collections.sort(yelpReviews, new Comparator<CustomReviewDetails>() {
                    @Override
                    public int compare(CustomReviewDetails reviewDetails, CustomReviewDetails t1) {
                        Date date1=null,date2=null;
                        try{
                            date1 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(reviewDetails.getReview_date());
                            date2 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(t1.getReview_date());
                        }
                        catch (ParseException e){
                            e.printStackTrace();
                        }

                        return date1.compareTo(date2);
                    }
                });

                recyclerView.setAdapter(new MyReviewsRecyclerViewAdapter(yelpReviews, "yelp_reviews"));
            }

        }
    }
}
