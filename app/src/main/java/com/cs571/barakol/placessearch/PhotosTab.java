package com.cs571.barakol.placessearch;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class PhotosTab extends Fragment {
    private GeoDataClient mGeoDataClient;
    private List<PlacePhotoMetadata> photosList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_details_photos, container, false);
        mGeoDataClient = Places.getGeoDataClient(getActivity());
        String place_id = getArguments().getString("place_id");
        getPhotos(place_id);
        return view;
    }

    private void getPhotos(String placeId) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching Results");
        progressDialog.show();
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                progressDialog.dismiss();
                photosList = new ArrayList<>();

                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                // Get the first photo in the list.
                int size = photoMetadataBuffer.getCount();
                for(int i=0;i<size;i++){
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);

                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();

                            ImageView image = new ImageView(getContext());
                            image.setImageBitmap(bitmap);
                            image.setPadding(0,20,0,20);
                            image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            image.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
//                            image.setScaleType(ImageView.ScaleType.FIT_XY);
                            LinearLayout layout =  getActivity().findViewById(R.id.photosTab);
                            layout.addView(image);
                        }
                    });
                }
            }
        });
    }

}
