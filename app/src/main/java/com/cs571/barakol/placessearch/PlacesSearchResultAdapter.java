package com.cs571.barakol.placessearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlacesSearchResultAdapter extends RecyclerView.Adapter<PlacesSearchResultAdapter.ViewHolder> {

    private ArrayList<PlacesSearchResult> mPlacesSearchResultList;
    private CustomSharedPreference customSharedPreference;
    private Context pContext;
    private String tabName;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View placeView = inflater.inflate(R.layout.place_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(placeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlacesSearchResult placesSearchResult = mPlacesSearchResultList.get(position);

        Picasso.get().load(placesSearchResult.getImage_url()).into(holder.placeImageView);
        holder.placeTextView.setText(Html.fromHtml("<b>"+placesSearchResult.getPlaceName()+"</b><br>"+placesSearchResult.getAddress()));

        //If a place exists in shared preferences then set heart_red drawable
        if (checkFavoriteItem(placesSearchResult)) {
            holder.favImageView.setImageResource(R.drawable.heart_fill_red);
            holder.favImageView.setTag("red");
        } else {
            holder.favImageView.setImageResource(R.drawable.heart_outline_black);
            holder.favImageView.setTag("white");
        }
    }

    @Override
    public int getItemCount() {
        return mPlacesSearchResultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public ImageView placeImageView;
        public TextView placeTextView;
        public ImageView favImageView;
        private final Context context;

        public ViewHolder(View itemView){
            super(itemView);

            placeImageView = itemView.findViewById(R.id.placeImageView);
            placeTextView = itemView.findViewById(R.id.placeTextView);
            favImageView = itemView.findViewById(R.id.favImageView);
            context = itemView.getContext();

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            Intent intent = new Intent(context,PlaceDetailsActivity.class);
            intent.putExtra("position",position);
            intent.putParcelableArrayListExtra("resultList",mPlacesSearchResultList);
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            ImageView favBtn = view.findViewById(R.id.favImageView);
            String tag = favBtn.getTag().toString();
            String place_name = mPlacesSearchResultList.get(position).getPlaceName()+" ";

            if (tag.equalsIgnoreCase("white")) {
                customSharedPreference.addFavorite(context, mPlacesSearchResultList.get(position));

                Toast.makeText(context,
                        place_name+context.getResources().getString(R.string.add_fav),
                        Toast.LENGTH_SHORT).show();

                favBtn.setTag("red");
                favBtn.setImageResource(R.drawable.heart_fill_red);
            } else {
                customSharedPreference.removeFavorite(context, mPlacesSearchResultList.get(position));

                if(tabName=="favTab"){
                    mPlacesSearchResultList.remove(position);
                    notifyDataSetChanged();
                }

                favBtn.setTag("white");
                favBtn.setImageResource(R.drawable.heart_outline_black);
                Toast.makeText(context,
                        place_name+context.getResources().getString(R.string.remove_fav),
                        Toast.LENGTH_SHORT).show();
            }

            return true;
        }
    }

    public PlacesSearchResultAdapter(Context context,ArrayList<PlacesSearchResult> resultList,String tabName){
        mPlacesSearchResultList = resultList;
        this.pContext = context;
        this.tabName = tabName;
        customSharedPreference = new CustomSharedPreference();
    }

    /*Checks whether a particular product exists in SharedPreferences*/
    public boolean checkFavoriteItem(PlacesSearchResult checkPlace) {
        boolean check = false;
        List<PlacesSearchResult> favorites = customSharedPreference.getFavorites(pContext);
        if (favorites != null) {
            for (PlacesSearchResult place : favorites) {
                if (place.equals(checkPlace)) {
                    Log.i("FAVS","equal");
                    check = true;
                    break;
                }
            }
        }
        return check;
    }


}
