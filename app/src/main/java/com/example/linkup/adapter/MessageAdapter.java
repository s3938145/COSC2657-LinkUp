package com.example.linkup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.model.Message;
import com.example.linkup.viewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        String timestamp = getFormattedTime(message.getTimestamp());
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SENDER:
                holder.bindSenderMessage(message, timestamp);
                break;
            case VIEW_TYPE_RECEIVER:
                holder.bindReceiverMessage(message, timestamp);
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

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView messageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageTextView);
            profilePic = itemView.findViewById(R.id.profileImage);
        }

        public void bindSenderMessage(Message message, String timestamp) {
            profilePic = itemView.findViewById(R.id.profilesender);
            messageText = itemView.findViewById(R.id.senderText);
            TextView timestamptext = itemView.findViewById(R.id.senderTimestampTextView);
            timestamptext.setText(timestamp);
            messageText.setText(message.getText());
            loadProfileImage(message.getSenderId());
        }

        public void bindReceiverMessage(Message message, String timestamp) {
            profilePic = itemView.findViewById(R.id.profilereceiver);
            messageText = itemView.findViewById(R.id.receiverText);
            TextView timestamptext = itemView.findViewById(R.id.receiverTimestampTextView);
            timestamptext.setText(timestamp);
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
    private String getFormattedTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}


