package com.cs571.barakol.placessearch;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class PlacesSearchResult implements Parcelable{
    private String place_name;
    private String address;
    private String image_url;
    private String place_id;

    public PlacesSearchResult(String place_name, String address, String image_url, String place_id){
        this.place_name = place_name;
        this.address = address;
        this.image_url = image_url;
        this.place_id = place_id;
    }

    public PlacesSearchResult(Parcel in){
        this.place_name = in.readString();
        this.address = in.readString();
        this.image_url = in.readString();
        this.place_id = in.readString();
    }

    public String getPlaceName(){
        return place_name;
    }

    public String getAddress(){
        return address;
    }

    public String getImage_url(){
        return image_url;
    }

    public String getPlaceId(){
        return place_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(place_name);
        parcel.writeString(address);
        parcel.writeString(image_url);
        parcel.writeString(place_id);

    }

    public static final Parcelable.Creator<PlacesSearchResult> CREATOR = new Parcelable.Creator<PlacesSearchResult>(){
        public PlacesSearchResult createFromParcel(Parcel in){
            return new PlacesSearchResult(in);
        }
        public PlacesSearchResult[] newArray(int size) {
            return new PlacesSearchResult[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
        PlacesSearchResult other = (PlacesSearchResult) obj;
        Log.i("CHECK_ID",other.getPlaceId()+" ID: "+place_id);
        if (place_id.equals(other.getPlaceId()))
            return true;
        return false;
    }
}
