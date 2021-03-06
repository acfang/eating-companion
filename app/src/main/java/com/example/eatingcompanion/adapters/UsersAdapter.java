package com.example.eatingcompanion.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.MainActivity;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.databinding.ItemUserBinding;
import com.example.eatingcompanion.fragments.OtherUserProfileFragment;
import com.example.eatingcompanion.models.User;
import com.parse.ParseFile;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    public static final String TAG = "UsersAdapter";

    private Context context;
    private List<User> users;

    ItemUserBinding binding;

    public UsersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new UsersAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfilePicture;
        private TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = binding.ivProfilePicture;
            tvName = binding.tvName;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "User clicked");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // get chat at the position
                        User user = users.get(position);
                        Fragment fragment;
                        fragment = new OtherUserProfileFragment();
                        // create bundle of post info to send to detail fragment
                        Bundle args = new Bundle();
                        args.putSerializable("user", user);
                        fragment.setArguments(args);
                        ((MainActivity)context).getSupportFragmentManager().beginTransaction().add(R.id.flContainer, fragment).addToBackStack(null).commit();
                    }
                }
            });
        }

        public void bind(User user) {
            ParseFile profilePicture = user.getProfilePicture();
            if (profilePicture != null) {
                Glide.with(context)
                        .load(profilePicture.getUrl())
                        .fitCenter()
                        .circleCrop()
                        .into(ivProfilePicture);
            } else {
                Glide.with(context)
                        .load(R.drawable.default_avatar)
                        .fitCenter()
                        .circleCrop()
                        .into(ivProfilePicture);
            }
            tvName.setText(user.getName());
        }
    }
}
