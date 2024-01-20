package com.example.linkup.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.model.ChatSession;
import com.example.linkup.model.Message;
import com.example.linkup.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;

    private List<Message> messages = new ArrayList<>();
    private String currentUserId;
    private static UserViewModel userViewModel;
    private static LifecycleOwner lifecycleOwner;

    public MessageAdapter(UserViewModel userViewModel, LifecycleOwner lifecycleOwner) {
        this.userViewModel = userViewModel;
        this.lifecycleOwner = lifecycleOwner;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = (currentUser != null) ? currentUser.getUid() : "";
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = (viewType == VIEW_TYPE_SENDER) ? R.layout.item_sender_message : R.layout.item_receiver_message;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SENDER:
                holder.bindSenderMessage(message);
                break;
            case VIEW_TYPE_RECEIVER:
                holder.bindReceiverMessage(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (currentUserId.equals(message.getSenderId())) {
            return VIEW_TYPE_SENDER;
        } else {
            return VIEW_TYPE_RECEIVER;
        }
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(Map<String, Message> messageMap) {
        Log.d("MessageAdapter", "setMessages called with " + messageMap.size() + " messages");
        messages.clear();
        messages.addAll(new ArrayList<>(messageMap.values()));
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView messageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageTextView);
            profilePic = itemView.findViewById(R.id.profileImage); // Ensure this ID matches your layout

        }

        public void bindSenderMessage(Message message) {
            profilePic = itemView.findViewById(R.id.profilesender);
            messageText = itemView.findViewById(R.id.senderText);
            messageText.setText(message.getText());
            loadProfileImage(message.getSenderId());
        }

        public void bindReceiverMessage(Message message) {
            profilePic = itemView.findViewById(R.id.profilereceiver);
            messageText = itemView.findViewById(R.id.receiverText);
            messageText.setText(message.getText());
            loadProfileImage(message.getSenderId());
        }

        private void loadProfileImage(String userId) {
            userViewModel.getUserLiveData(userId).observe(lifecycleOwner, user -> {
                if (user != null && user.getUserId().equals(userId) && user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    Picasso.get().load(user.getProfileImage()).into(profilePic);
                }
            });
        }
    }

}


