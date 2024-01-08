package com.example.linkup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;
    private String currentUserId;

    public ChatAdapter() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
        chatMessages = new ArrayList<>();
    }

    public void setMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        boolean isCurrentUser = message.getSenderId().equals(currentUserId);
        holder.bind(message, isCurrentUser);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextViewUser;
        TextView messageTextViewOther;

        ChatViewHolder(View itemView) {
            super(itemView);
            messageTextViewUser = itemView.findViewById(R.id.text_message_user);
            messageTextViewOther = itemView.findViewById(R.id.text_message_other);
        }

        void bind(ChatMessage message, boolean isCurrentUser) {
            if (isCurrentUser) {
                messageTextViewUser.setText(message.getMessage());
                messageTextViewUser.setVisibility(View.VISIBLE);
                messageTextViewOther.setVisibility(View.GONE);
            } else {
                messageTextViewOther.setText(message.getMessage());
                messageTextViewOther.setVisibility(View.VISIBLE);
                messageTextViewUser.setVisibility(View.GONE);
            }
        }
    }
}

