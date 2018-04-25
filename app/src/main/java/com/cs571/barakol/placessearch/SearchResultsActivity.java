package com.cs571.barakol.placessearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private CustomSharedPreference customSharedPreference;
    private ArrayList<PlacesSearchResult> placesResultsList;
    private ArrayList<ArrayList<PlacesSearchResult>> prevResultList;
    private String next_page_token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customSharedPreference = new CustomSharedPreference();
        prevResultList = new ArrayList<>();

        setContentView(R.layout.activity_search_results);

        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Intent i = getIntent();

        getSupportActionBar().setTitle("Search Results");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        next_page_token = i.getStringExtra("next_page_token");
        placesResultsList =  i.getParcelableArrayListExtra("placesJSON");

        mRecyclerView = (RecyclerView) findViewById(R.id.placesResultView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PlacesSearchResultAdapter(this,placesResultsList,"detailsTab");
        mRecyclerView.setAdapter(adapter);

        Log.i("CREATE_ACTIVITY","search result");
        Button nextBtn = findViewById(R.id.searchResultNextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("BTN","next clicked");
                prevResultList.add(placesResultsList);
                getNextPageResult();
                findViewById(R.id.searchResultPrevBtn).setEnabled(true);

            }
        });

        Button prevBtn = findViewById(R.id.searchResultPrevBtn);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter = new PlacesSearchResultAdapter(getApplicationContext(),prevResultList.remove(prevResultList.size()-1),"detailsTab");
                mRecyclerView.setAdapter(adapter);
                if(prevResultList.size()==0)
                    view.setEnabled(false);
                findViewById(R.id.searchResultNextBtn).setEnabled(true);
            }
        });
    }

    private void getNextPageResult(){
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        String URL_DATA = getResources().getString(R.string.url)+"/submitForm2?pagetoken="+next_page_token;
        placesResultsList = new ArrayList<>();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Results");
        progressDialog.show();
        Log.i("URL_DATA",URL_DATA);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("RESPONSE_JSON","json rec");
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        PlacesSearchResult places = new PlacesSearchResult(jo.getString("name"),jo.getString("formatted_address"), jo.getString("icon"), jo.getString("place_id"));
                        placesResultsList.add(places);
                        Log.i("PLACE","Name: "+places.getPlaceName()+"ID: "+places.getPlaceId()+" Address: "+places.getAddress()+"Icon: "+places.getImage_url());
                    }

                    if(jsonObject.has("next_page_token"))
                        next_page_token = jsonObject.getString("next_page_token");
                    else {
                        next_page_token = null;
                        findViewById(R.id.searchResultNextBtn).setEnabled(false);
                    }

                    adapter = new PlacesSearchResultAdapter(getApplicationContext(),placesResultsList,"detailsTab");
                    mRecyclerView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        adapter = new PlacesSearchResultAdapter(getApplicationContext(),placesResultsList,"detailsTab");
        mRecyclerView.setAdapter(adapter);
    }


}
