package com.faisal.bangunruang.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.faisal.bangunruang.R;
import com.faisal.bangunruang.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.tvMessage.setText(message.getMessage());

        LinearLayout layoutMessage = holder.itemView.findViewById(R.id.layout_message);
        LinearLayout rootLayout = (LinearLayout) holder.itemView;

        if (message.getType() == ChatMessage.TYPE_USER) {
            rootLayout.setGravity(Gravity.END);
            layoutMessage.setBackgroundResource(R.drawable.bg_chat_user);
        } else {
            rootLayout.setGravity(Gravity.START);
            layoutMessage.setBackgroundResource(R.drawable.bg_chat_bubble);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        ViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
        }
    }
}
