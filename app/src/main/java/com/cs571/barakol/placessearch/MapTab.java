package com.cs571.barakol.placessearch;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapTab extends Fragment {
    GoogleMap googleMap;
    private double lat;
    private double lng;
    private String place_name;
    private String place_id;
    private String jsonStr;
    private String from_place_name;
    JSONObject jsonObject;
    LatLngBounds mapBounds;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_details_map, container,false);

        jsonStr = getArguments().getString("PLACES_JSON");
        try{
            jsonObject = new JSONObject(jsonStr);

            //Get the lat & lng of the selected place
            lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

            //Get Place Name
            place_name = jsonObject.getString("name");

            place_id = jsonObject.getString("place_id");
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return view;
    }

    public static MapTab newInstance(String resultJson) {
        MapTab fragment = new MapTab();
        Bundle args = new Bundle();
        args.putString("PLACES_JSON",resultJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapView mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }
                else {
                    googleMap.setMyLocationEnabled(true);

                    // For dropping a marker at a point on the Map
                    LatLng location = new LatLng(lat, lng);
                    googleMap.addMarker(new MarkerOptions().position(location).title(place_name)).showInfoWindow();

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(15).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        AutoCompleteTextView fromView= view.findViewById(R.id.mapFromText);
        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(getContext());
        fromView.setAdapter(adapter);
        fromView.setOnItemClickListener(onItemClickListener);

    }

    private void getDirectionsJSON(String origin, String travel_mode){
        RequestQueue queue = Volley.newRequestQueue(getContext());

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching Results");
        progressDialog.show();

        String URL_DATA = getResources().getString(R.string.url)+"/reqPlaceDirections?origin="+origin.replace(' ','+')+"&destination="+place_id+"&mode="+travel_mode;
        Log.i("URL_DATA_DIR",URL_DATA);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    Log.i("URL_DATA_DIR",jsonObject.toString());

                    JSONArray route = jsonObject.getJSONArray("routes");
                    LatLng l1 = new LatLng(Double.parseDouble(((JSONObject)route.get(0)).getJSONObject("bounds").getJSONObject("northeast").getString("lat")),Double.parseDouble(((JSONObject)route.get(0)).getJSONObject("bounds").getJSONObject("northeast").getString("lng")));
                    LatLng l2 = new LatLng(Double.parseDouble(((JSONObject)route.get(0)).getJSONObject("bounds").getJSONObject("southwest").getString("lat")),Double.parseDouble(((JSONObject)route.get(0)).getJSONObject("bounds").getJSONObject("southwest").getString("lng")));
                    mapBounds = new LatLngBounds(l2,l1);

//                    MarkerPoints.clear();
                    googleMap.clear();
//                    googleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)));
//                    MarkerPoints.add(new LatLng(lat, lng));

                    ParserTask parserTask = new ParserTask();
                    parserTask.execute(jsonObject.toString());


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

    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final String start = ((Place)adapterView.getItemAtPosition(i)).getPlaceText();
                    from_place_name = start.split(",")[0];
                    final Spinner travel_mode_spinner = getActivity().findViewById(R.id.travelModeSpinner);

                    getDirectionsJSON(start,travel_mode_spinner.getSelectedItem().toString().toLowerCase());

                    travel_mode_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            getDirectionsJSON(start,travel_mode_spinner.getSelectedItem().toString().toLowerCase());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            };


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DirectionsParser parser = new DirectionsParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            LatLng end=new LatLng(lat,lng);

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                if(i==result.size()-1){
                    googleMap.addMarker(new MarkerOptions().position(points.get(points.size()-1)));
//                    mapBounds = new LatLngBounds(points.get(points.size()-1),end);
                }
                if(i==0){
//                    end = points.get(0);
                    googleMap.addMarker(new MarkerOptions().position(points.get(0)).title(from_place_name)).showInfoWindow();
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                Log.d("POLYLINE","draw");
                googleMap.addPolyline(lineOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapBounds.getCenter(),getBoundsZoomLevel(mapBounds.northeast,mapBounds.southwest,256,256)));
//                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }

        public int getBoundsZoomLevel(LatLng northeast,LatLng southwest,
                                             int width, int height) {
            final int GLOBE_WIDTH = 256; // a constant in Google's map projection
            final int ZOOM_MAX = 21;
            double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI;
            double lngDiff = northeast.longitude - southwest.longitude;
            double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
            double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
            double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
            double zoom = Math.min(Math.min(latZoom, lngZoom),ZOOM_MAX);
            return (int)(zoom);
        }
        private double latRad(double lat) {
            double sin = Math.sin(lat * Math.PI / 180);
            double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
            return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
        }
        private double zoom(double mapPx, double worldPx, double fraction) {
            final double LN2 = .693147180559945309417;
            return (Math.log(mapPx / worldPx / fraction) / LN2);
        }
    }

}
