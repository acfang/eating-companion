package com.example.eatingcompanion.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatingcompanion.R;
import com.example.eatingcompanion.models.Chat;
import com.parse.ParseException;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    public static final String TAG = "ChatsAdapter";
    private Context context;
    private List<Chat> chats;

    public ChatsAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

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
            ivChatPicture = itemView.findViewById(R.id.ivChatPicture);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvMessagePreview = itemView.findViewById(R.id.tvMessagePreview);
        }

        public void bind(Chat chat) {
            tvTime.setText(chat.getTime());
        }
    }


}
