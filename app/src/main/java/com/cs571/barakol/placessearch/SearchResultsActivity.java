package com.cs571.barakol.placessearch;

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

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private CustomSharedPreference customSharedPreference;
    private ArrayList<PlacesSearchResult> placesResultsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customSharedPreference = new CustomSharedPreference();

        setContentView(R.layout.activity_search_results);

        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Intent i = getIntent();

        getSupportActionBar().setTitle("Search Results");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        placesResultsList =  i.getParcelableArrayListExtra("placesJSON");

        mRecyclerView = (RecyclerView) findViewById(R.id.placesResultView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PlacesSearchResultAdapter(getApplicationContext(),placesResultsList,"detailsTab");
        mRecyclerView.setAdapter(adapter);
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
