package com.cs571.barakol.placessearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetailsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    protected CustomPlaceDetails placeDetailsObj;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    //Fragment List
    private final List<Fragment> mFragmentList = new ArrayList<>();
    //Title List
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private String place_id;
    private String place_name;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        Intent i = getIntent();
        int clickedPos = i.getIntExtra("position",0);

        ArrayList<PlacesSearchResult> placesResultsList =  i.getParcelableArrayListExtra("resultList");
        place_name = placesResultsList.get(clickedPos).getPlaceName();
        place_id = placesResultsList.get(clickedPos).getPlaceId();

        //Make request to node.js server to get place details
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(place_name);
        mViewPager = (ViewPager) findViewById(R.id.detailsContainer);

        requestData();

        tabLayout = (TabLayout) findViewById(R.id.detailsTabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    private void requestData(){
        String URL_DATA = getString(R.string.url) +"/reqPlaceDetails?placeid="+place_id;

        RequestQueue queue = Volley.newRequestQueue(this);


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Results");
        progressDialog.show();
        Log.i("URL_DATA",URL_DATA);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    jsonObject = new JSONObject(response);

                    placeDetailsObj = new CustomPlaceDetails(jsonObject.getString("place_id"),jsonObject.getString("name"),jsonObject.getString("formatted_address"),
                            jsonObject.getString("international_phone_number"),jsonObject.getInt("price_level"),jsonObject.getDouble("rating")
                            ,jsonObject.getString("url"),jsonObject.getString("website"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Add fragments
                mFragmentList.add(InfoTab.newInstance(jsonObject.toString()));
                Bundle bundle = new Bundle();
                bundle.putString("place_id",place_id);
                PhotosTab photosTab = new PhotosTab();
                photosTab.setArguments(bundle);
                mFragmentList.add(photosTab);
                mFragmentList.add(MapTab.newInstance(jsonObject.toString()));
                mFragmentList.add(InfoTab.newInstance(jsonObject.toString()));//Add reviews tab here

                //Add Titles
                mFragmentTitleList.add("Info");
                mFragmentTitleList.add("Photos");
                mFragmentTitleList.add("Map");
                mFragmentTitleList.add("Reviews");

                setupViewPager(mViewPager);
                tabLayout.setupWithViewPager(mViewPager);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    private void setupViewPager(ViewPager viewPager) {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mFragmentList, mFragmentTitleList);
        viewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_place_details_info, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titleLists) {
            super(fm);
            this.mFragmentList = fragments;
            this.mFragmentTitleList = titleLists;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
