package com.cs571.barakol.placessearch;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
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


public class SearchTab extends Fragment implements LocationListener{
    private static final int REQUEST_LOCATION = 0;
    private LocationManager locationManager;
    private Location clientLoc;

    private String URL_DATA;
    private ArrayList<PlacesSearchResult> searchResultList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Set adapter for autocomplete text view
        AutoCompleteTextView searchPlace = (AutoCompleteTextView) view.findViewById(R.id.locTextView);
        Log.i("MYERROR",searchPlace.toString());
        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(getContext());
        Log.i("MYERROR",adapter.toString());
        searchPlace.setAdapter(adapter);
        searchPlace.setOnItemClickListener(onItemClickListener);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //Set onClick Handler for Search button
        Button searchBtn = view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("MYERR","CLICKED");
                EditText keywordTxt = getActivity().findViewById(R.id.keyword_txt);
                RadioButton locRadioBtn = getActivity().findViewById(R.id.from_loc);

                URL_DATA = getString(R.string.url);

                String keyword = keywordTxt.getText().toString().trim();
                if(keyword==""|| keyword.matches("^\\s*$")){
                    TextView keywordErr = getActivity().findViewById(R.id.keywordErr);
                    keywordErr.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(),
                            "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                }
                else if(locRadioBtn.isChecked()){
                    AutoCompleteTextView locTxt = getActivity().findViewById(R.id.locTextView);
                    String location = locTxt.getText().toString();
                    if(location=="" ||location.matches("^\\s*$")){
                        TextView locErr = getActivity().findViewById(R.id.locErr);
                        locErr.setVisibility(View.VISIBLE);

                        Toast.makeText(getContext(),
                                "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    String tmp_keyword = keyword.replace(" ","+");
                    EditText distanceTxt = getActivity().findViewById(R.id.dist_txt);
                    String distance = distanceTxt.getText().toString();

                    RadioButton hereRadioBtn = getActivity().findViewById(R.id.from_here);

                    Spinner categoryVal = getActivity().findViewById(R.id.category);
                    String category = categoryVal.getSelectedItem().toString();

                    String tmp_location="";
                    String from;

                    URL_DATA += "/submitForm?" + "keyword="+ tmp_keyword + "&dist="+distance + "&category="+category;

                    if(hereRadioBtn.isChecked()){
                        from="here";
                        if(ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getContext(),
                                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_LOCATION);
                        }
                        else{
                            Log.i("PERMISSION", "granted");
                            getLocation();
//                            URL_DATA += "&lat=34.02865068&lng=-118.2877391";
                        }
                        URL_DATA += "&lat=34.02865068&lng=-118.2877391";//CHANGE THIS
                    }
                    else{//get location text
                        from="loc";
                        EditText locTxt = getActivity().findViewById(R.id.locTextView);
                        String location = locTxt.getText().toString().trim();
                        tmp_location = location.replace(" ","+");

                        URL_DATA += "&locTxt="+ tmp_location;
                    }

                    URL_DATA += "&from=" + from;
                    Log.i("URL",URL_DATA);

                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());


                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Fetching Results");
                    progressDialog.show();
                    Log.i("URL_DATA",URL_DATA);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("results");
                                Log.i("PLACESJSON", "Places Output: "+ jsonObject.toString());

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    PlacesSearchResult places = new PlacesSearchResult(jo.getString("name"),jo.getString("vicinity"), jo.getString("icon"), jo.getString("place_id"));
                                    searchResultList.add(places);
                                    Log.i("PLACE","Name: "+places.getPlaceName()+"ID: "+places.getPlaceId()+" Address: "+places.getAddress()+"Icon: "+places.getImage_url());
                                }

                                String next_page_token = jsonObject.getString("next_page_token");
                                Intent intent = new Intent(getActivity(),SearchResultsActivity.class);
                                intent.putExtra("next_page_token",next_page_token);
                                intent.putParcelableArrayListExtra("placesJSON", searchResultList);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Error"+error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });



                    queue.add(stringRequest);

                }
            }
        });

        //Set onClick handler for Clear button
        Button clearBtn = view.findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView keywordErr = getActivity().findViewById(R.id.keywordErr);
                keywordErr.setVisibility(View.INVISIBLE);

                ViewGroup grp = getActivity().findViewById(R.id.searchLayout);
                for (int i = 0, count = grp.getChildCount(); i < count; ++i) {
                    View view = grp.getChildAt(i);
                    if (view instanceof EditText) {
                        ((EditText) view).setText("");
                    }
                }

                Spinner category = getActivity().findViewById(R.id.category);
                category.setSelection(0);

                RadioButton hereBtn = getActivity().findViewById(R.id.from_here);
                hereBtn.setChecked(true);

                TextView locErr = getActivity().findViewById(R.id.locErr);
                locErr.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }


    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                    Toast.makeText(getContext(),
//                            "selected place "
//                                    + ((com.cs571.barakol.placessearch.Place)adapterView.
//                                    getItemAtPosition(i)).getPlaceText()
//                            , Toast.LENGTH_SHORT).show();
                    //do something with the selection
//                    searchScreen();
                }
            };


    private void getLocation() {
        try {
//            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            clientLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            Log.i("MYLOCATION","success ");
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        clientLoc = location;
        Log.i("MYLOCATION", "Lat: "+location.getLatitude()+" Lng: "+location.getLongitude());
//        URL_DATA += "&lat=" +location.getLatitude()+ "&lng=" +location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchResultList = new ArrayList<PlacesSearchResult>();
    }

}
