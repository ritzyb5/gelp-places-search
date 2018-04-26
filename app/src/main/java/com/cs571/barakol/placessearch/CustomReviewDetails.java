package com.cs571.barakol.placessearch;

import android.support.annotation.NonNull;


public class CustomReviewDetails{
    private String image_url;
    private String author_name;
    private Float review_rating;
    private String review_date;
    private String review_text;
    private String review_url;

    public CustomReviewDetails(String image_url, String author_name, Float review_rating, String review_date, String review_text, String review_url) {
        this.image_url = image_url;
        this.author_name = author_name;
        this.review_rating = review_rating;
        this.review_date = review_date;
        this.review_text = review_text;
        this.review_url = review_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public Float getReview_rating() {
        return review_rating;
    }

    public String getReview_date() {
        return review_date;
    }

    public String getReview_text() {
        return review_text;
    }

    public String getReview_url() {
        return review_url;
    }

}
