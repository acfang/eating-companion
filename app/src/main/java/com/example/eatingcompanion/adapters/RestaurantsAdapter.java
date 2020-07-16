package com.example.eatingcompanion.adapters;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.MainActivity;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.fragments.RestaurantDetailFragment;
import com.example.eatingcompanion.models.Category;
import com.example.eatingcompanion.models.Restaurant;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {

    public static final String TAG = "RestaurantsAdapter";
    private Context context;
    private List<Restaurant> restaurants;

    public RestaurantsAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public RestaurantsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivRestaurant;
        private TextView tvRestaurantName;
        private RatingBar rbRestaurant;
        private TextView tvReviews;
        private TextView tvAddress;
        private TextView tvDistance;
        private TextView tvPrice;
        private TextView tvRestaurantType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRestaurant = itemView.findViewById(R.id.ivRestaurant);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            rbRestaurant = itemView.findViewById(R.id.rbRestaurant);
            tvReviews = itemView.findViewById(R.id.tvReviews);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRestaurantType = itemView.findViewById(R.id.tvRestaurantType);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Restaurant clicked");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // get restaurant at the position
                        Restaurant restaurant = restaurants.get(position);
                        Fragment fragment;
                        fragment = new RestaurantDetailFragment();
                        // create bundle of post info to send to detail fragment
                        Bundle args = new Bundle();
                        args.putString("id", restaurant.getId());
                        fragment.setArguments(args);
                        ((MainActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                    }
                }
            });
        }

        public void bind(Restaurant restaurant) {
            Glide.with(context).load(restaurant.getImageUrl()).centerCrop().into(ivRestaurant);
            tvRestaurantName.setText(restaurant.getName());
            rbRestaurant.setRating((float) restaurant.getRating());
            String numReviews = restaurant.getNumReviews() + " Reviews";
            tvReviews.setText(numReviews);
            tvAddress.setText(restaurant.getLocation().getAddress());
            tvDistance.setText(restaurant.getDistance());
            tvPrice.setText(restaurant.getPrice());
            String concatenatedCategories = "";
            List<Category> categories = restaurant.getCategories();
            for (int i = 0; i < categories.size(); i++) {
                concatenatedCategories += ", " + categories.get(i).getTitle();
            }
            concatenatedCategories = concatenatedCategories.substring(2);
            tvRestaurantType.setText(concatenatedCategories);
        }
    }
}
