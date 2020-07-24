package com.example.eatingcompanion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.models.Post;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPostImage;
        private ImageView ivProfilePicture;
        private TextView tvUser;
        private TextView tvRestaurant;
        private TextView tvOtherUser;
        private TextView tvCaption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvRestaurant = itemView.findViewById(R.id.tvRestaurant);
            tvOtherUser = itemView.findViewById(R.id.tvOtherUser);
            tvCaption = itemView.findViewById(R.id.tvCaption);
        }

        public void bind(Post post) {
            Glide.with(context).load(post.getImage().getUrl())
                    .placeholder(R.drawable.default_avatar)
                    .transform(new CenterCrop(), new RoundedCornersTransformation(30, 0))
                    .into(ivPostImage);
            Glide.with(context).load(post.getUser().getProfilePicture().getUrl())
                    .placeholder(R.drawable.default_avatar)
                    .circleCrop()
                    .into(ivProfilePicture);
            tvUser.setText(post.getUser().getName());
            tvRestaurant.setText(post.getRestaurant());
            tvOtherUser.setText(post.getOtherUser());
            tvCaption.setText(post.getCaption());
        }
    }
}
