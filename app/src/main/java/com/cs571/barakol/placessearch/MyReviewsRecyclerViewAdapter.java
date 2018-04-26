package com.cs571.barakol.placessearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cs571.barakol.placessearch.dummy.DummyContent.DummyItem;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified
 * TODO: Replace the implementation with code for your data type.
 */
public class MyReviewsRecyclerViewAdapter extends RecyclerView.Adapter<MyReviewsRecyclerViewAdapter.ViewHolder> {

    private final List<CustomReviewDetails> mValues;
    private String reviewType;


    public MyReviewsRecyclerViewAdapter(List<CustomReviewDetails> items, String type) {
        mValues = items;
        reviewType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_reviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        CustomReviewDetails review = mValues.get(position);

        if(reviewType.equals("google_reviews"))
            Picasso.get().load(review.getImage_url()).into(holder.image_url);
        else
            Picasso.get().load(review.getImage_url()).transform(new CircleTransform()).into((holder.image_url));

        holder.author_name.setText(review.getAuthor_name());
        holder.review_rating.setRating(review.getReview_rating());

//        Date rating_date = new Date(Long.parseLong(review.getReview_date()));
//        DateFormat formatted_rating_date = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        holder.review_date.setText(review.getReview_date());

        holder.review_text.setText(review.getReview_text());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_url;
        private TextView author_name;
        private RatingBar review_rating;
        private  TextView review_date;
        private  TextView review_text;
        private final Context context;

        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
//            mView = view;

            image_url = view.findViewById(R.id.authorImage);
            author_name = view.findViewById(R.id.authorName);
            review_rating = view.findViewById(R.id.reviewRating);
            review_date = view.findViewById(R.id.reviewDate);
            review_text = view.findViewById(R.id.reviewText);

            context = view.getContext();

        }
    }
}
