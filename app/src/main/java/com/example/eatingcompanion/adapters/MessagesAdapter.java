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
import com.example.eatingcompanion.databinding.ItemMessageBinding;
import com.example.eatingcompanion.fragments.OtherUserProfileFragment;
import com.example.eatingcompanion.fragments.ProfileFragment;
import com.example.eatingcompanion.models.Message;
import com.example.eatingcompanion.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    public static final String TAG = "MessagesAdapter";
    private Context context;
    private List<Message> messages;

    ItemMessageBinding binding;

    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemMessageBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MessagesAdapter.ViewHolder(binding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        try {
            holder.bind(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfileOther;
        private ImageView ivProfileMe;
        private TextView tvBody;
        private ImageView ivMedia;
        private TextView tvTimestampOther;
        private TextView tvTimestampMe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileOther = binding.ivProfileOther;
            ivProfileMe = binding.ivProfileMe;
            tvBody = binding.tvBody;
            ivMedia = binding.ivMedia;
            tvTimestampOther = binding.tvTimestampOther;
            tvTimestampMe = binding.tvTimestampMe;

            ivProfileOther.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Other user clicked");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // get restaurant at the position
                        Message message = messages.get(position);
                        Fragment fragment;
                        fragment = new OtherUserProfileFragment();
                        // create bundle of post info to send to detail fragment
                        Bundle args = new Bundle();
                        args.putSerializable("user", message.getUser());
                        fragment.setArguments(args);
                        ((MainActivity)context).getSupportFragmentManager().beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
                    }
                }
            });

            ivProfileMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment;
                    fragment = new ProfileFragment();
                    ((MainActivity)context).getSupportFragmentManager().beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Message message) throws ParseException {
            final boolean isMe = message.getUser() != null && message.getUser().fetchIfNeeded().getUsername().equals(((User) ParseUser.getCurrentUser()).getUsername());
            Log.i(TAG, "Equal: " + message.getUser().equals((User) ParseUser.getCurrentUser()));
            Log.i(TAG, "Current user: " + ((User) ParseUser.getCurrentUser()).getUsername());
            Log.i(TAG, "Message sender: " +  message.getUser().fetchIfNeeded().getUsername());
            if (isMe) {
                ivProfileMe.setVisibility(View.VISIBLE);
                ivProfileOther.setVisibility(View.GONE);
                tvTimestampOther.setVisibility(View.GONE);
                tvTimestampMe.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mma", Locale.US);
                String date = sdf.format(message.getCreatedAt());
                tvTimestampMe.setText(date);
                ParseFile profilePicture = ((User) message.getUser().fetchIfNeeded()).getProfilePicture();
                if (profilePicture != null) {
                    Glide.with(context)
                            .load(profilePicture.getUrl())
                            .fitCenter()
                            .circleCrop()
                            .into(ivProfileMe);
                } else {
                    Glide.with(context)
                            .load(R.drawable.default_avatar)
                            .fitCenter()
                            .circleCrop()
                            .into(ivProfileMe);
                }
            } else {
                ivProfileOther.setVisibility(View.VISIBLE);
                ivProfileMe.setVisibility(View.GONE);
                tvTimestampOther.setVisibility(View.VISIBLE);
                tvTimestampMe.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mma", Locale.US);
                String date = sdf.format(message.getCreatedAt());
                tvTimestampOther.setText(date);
                ParseFile profilePicture = ((User) message.getUser().fetchIfNeeded()).getProfilePicture();
                if (profilePicture != null) {
                    Glide.with(context)
                            .load(profilePicture.getUrl())
                            .fitCenter()
                            .circleCrop()
                            .into(ivProfileOther);
                } else {
                    Glide.with(context)
                            .load(R.drawable.default_avatar)
                            .fitCenter()
                            .circleCrop()
                            .into(ivProfileOther);
                }
            }

            if (message.getMedia() != null) {
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getMedia().getUrl())
                        .placeholder(R.drawable.default_avatar)
                        .transform(new CenterCrop(), new RoundedCornersTransformation(30, 0))
                        .into(ivMedia);
            } else {
                ivMedia.setVisibility(View.GONE);
            }
            tvBody.setText(message.getBody());
        }
    }
}
