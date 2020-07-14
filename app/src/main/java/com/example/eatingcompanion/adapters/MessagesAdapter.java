package com.example.eatingcompanion.adapters;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.Message;
import com.example.eatingcompanion.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    public static final String TAG = "MessagesAdapter";
    private Context context;
    private List<Message> messages;

    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessagesAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfileOther;
        private ImageView ivProfileMe;
        private TextView tvBody;
        private TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileOther = itemView.findViewById(R.id.ivProfileOther);
            ivProfileMe = itemView.findViewById(R.id.ivProfileMe);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Message message) {
            final boolean isMe = message.getUser() != null && message.getUser().equals((User) ParseUser.getCurrentUser());
            if (isMe) {
                ivProfileMe.setVisibility(View.VISIBLE);
                ivProfileOther.setVisibility(View.GONE);
                ParseFile profilePicture = message.getUser().getProfilePicture();
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
                ParseFile profilePicture = message.getUser().getProfilePicture();
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

            tvBody.setText(message.getBody());
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            String date = sdf.format(message.getCreatedAt());
            tvTimestamp.setText(date);
        }
    }
}
