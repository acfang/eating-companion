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
import com.example.eatingcompanion.databinding.ItemInfoUserBinding;
import com.example.eatingcompanion.fragments.OtherUserProfileFragment;
import com.example.eatingcompanion.models.User;

import java.util.List;

public class InfoUsersAdapter extends RecyclerView.Adapter<InfoUsersAdapter.ViewHolder> {
    public static final String TAG = "InfoUsersAdapter";

    private Context context;
    private List<User> users;

    ItemInfoUserBinding binding;

    public InfoUsersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public InfoUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemInfoUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new InfoUsersAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull InfoUsersAdapter.ViewHolder holder, int position) {
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
            Glide.with(context).load(user.getProfilePicture().getUrl()).circleCrop().into(ivProfilePicture);
            tvName.setText(user.getName());
        }
    }
}
