package com.cs571.barakol.placessearch;

public class Place {
    private String placeId;
    private String placeText;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceText() {
        return placeText;
    }

    public void setPlaceText(String placeText) {
        this.placeText = placeText;
    }
    public String toString(){
        return placeText;
    }
}