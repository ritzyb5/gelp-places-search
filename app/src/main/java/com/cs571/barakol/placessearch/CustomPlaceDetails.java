package com.cs571.barakol.placessearch;

public class CustomPlaceDetails {
    private String place_id;
    private String place_name;
    private String address;
    private String phone_num;
    private int price_level;
    private double rating;
    private String google_url;
    private String web_url;


    public CustomPlaceDetails(String place_id, String place_name, String address, String phone_num, int price_level, double rating, String google_url, String web_url) {
        this.place_id = place_id;
        this.place_name = place_name;
        this.address = address;

        this.phone_num = phone_num;
        this.price_level = price_level;
        this.rating = rating;
        this.google_url = google_url;
        this.web_url = web_url;
    }

    public String getPlace_id() {
        return place_id;
    }


    public String getPlace_name() {
        return place_name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public int getPrice_level() {
        return price_level;
    }

    public double getRating() {
        return rating;
    }

    public String getGoogle_url() {
        return google_url;
    }

    public String getWeb_url() {
        return web_url;
    }

}
