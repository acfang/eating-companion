package com.example.eatingcompanion.adapters;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.eatingcompanion.MainActivity;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpDetailResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.databinding.ItemChatBinding;
import com.example.eatingcompanion.fragments.MessagesFragment;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.Message;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    public static final String TAG = "ChatsAdapter";
    public static final String BASE_URL = "https://api.yelp.com/v3/";

    private Context context;
    private List<Chat> chats;

    ItemChatBinding binding;

    public ChatsAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemChatBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivChatPicture;
        private TextView tvTime;
        private TextView tvRestaurantName;
        private TextView tvMessagePreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChatPicture = binding.ivChatPicture;
            tvTime = binding.tvTime;
            tvRestaurantName = binding.tvRestaurantName;
            tvMessagePreview = binding.tvMessagePreview;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Chat clicked");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // get chat at the position
                        Chat chat = chats.get(position);
                        Fragment fragment;
                        fragment = new MessagesFragment();
                        // create bundle of post info to send to detail fragment
                        Bundle args = new Bundle();
                        args.putSerializable("chat", chat);
                        fragment.setArguments(args);
                        ((MainActivity)context).getSupportFragmentManager().beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
                    }
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Chat chat) {
            Glide.with(context).clear(ivChatPicture);
            ivChatPicture.setImageResource(android.R.color.transparent);
            ivChatPicture.setImageDrawable(null);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy, hh:mma", Locale.US);
            String date = sdf.format(chat.getTime());
            tvTime.setText(date);
            String restaurantId = chat.getRestaurantId();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final YelpService yelpService = retrofit.create(YelpService.class);

            yelpService.getRestaurantDetail("Bearer " + context.getString(R.string.yelp_api_key), restaurantId).enqueue(new Callback<YelpDetailResponse>() {
                @Override
                public void onResponse(Call<YelpDetailResponse> call, Response<YelpDetailResponse> response) {
                    Log.i(TAG, "onResponse " + response);
                    if (response.body() == null) {
                        Log.e(TAG, "Did not receive valid response body from Yelp API");
                        return;
                    }
                    Glide.with(context).load(response.body().getImageUrl())
                            .placeholder(R.drawable.default_avatar)
                            .transform(new CenterCrop(), new RoundedCornersTransformation(30, 0))
                            .into(ivChatPicture);
                    tvRestaurantName.setText(response.body().getName());
                }

                @Override
                public void onFailure(Call<YelpDetailResponse> call, Throwable t) {
                    Log.i(TAG, "onFailure " + t);
                }
            });

            // query messages in the chat, look for the latest one
            ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
            query.include(Message.KEY_CHAT);
            query.include(Message.KEY_USER);
            query.whereEqualTo(Message.KEY_CHAT, chat);
            query.addDescendingOrder(Message.KEY_CREATED_KEY);
            query.getFirstInBackground(new GetCallback<Message>() {
                @Override
                public void done(Message object, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error when retrieving latest message", e);
                        return;
                    }
                    if (object.getUser() != null) {
                        String messagePreview = object.getUser().getName() + ": " + object.getBody();
                        tvMessagePreview.setText(messagePreview);
                    }
                }
            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        chats.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Chat> list) {
        chats.addAll(list);
        notifyDataSetChanged();
    }
}
