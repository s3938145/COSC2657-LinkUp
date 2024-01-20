package com.example.linkup.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.linkup.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

public class ChatRepository {
    private String currentUserId;
    private DatabaseReference chatRef;
    private ChildEventListener chatEventListener;

    public interface ChatUpdateListener {
        void onChatUpdated(List<ChatMessage> messages);
        void onChatMessageChanged(ChatMessage message);
        void onChatMessageDeleted(String messageId);
        void onChatUpdateFailed(DatabaseError error);
    }

    public ChatRepository() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            chatRef = FirebaseDatabase.getInstance().getReference("chats");
        } else {
            // Handle the case when the user is not authenticated.
            // You might want to show an error message or navigate to the login screen.
        }
    }

    public void sendMessage(String chatId, String messageContent, long timestamp) {
        if (currentUserId != null && !currentUserId.isEmpty()) {
            DatabaseReference newMessageRef = chatRef.child(chatId).push();
            String messageId = newMessageRef.getKey();
            ChatMessage message = new ChatMessage(messageId, chatId, currentUserId, messageContent, timestamp);
            newMessageRef.setValue(message);
        }
    }

    public void addChatMessagesListener(String chatId, final ChatUpdateListener listener) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            return;
        }

        if (chatEventListener != null) {
            chatRef.child(chatId).removeEventListener(chatEventListener);
        }

        chatEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                if (message != null) {
                    listener.onChatUpdated(Collections.singletonList(message));
                }
            }

            @Override
            public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                ChatMessage changedMessage = dataSnapshot.getValue(ChatMessage.class);
                if (changedMessage != null) {
                    listener.onChatMessageChanged(changedMessage);
                }
            }

            @Override
            public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                listener.onChatMessageDeleted(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot snapshot, @Nullable String previousChildName) {
                // Not implemented
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onChatUpdateFailed(databaseError);
            }
        };

        chatRef.child(chatId).addChildEventListener(chatEventListener);
    }

    public void removeChatMessagesListener(String chatId) {
        if (chatEventListener != null) {
            chatRef.child(chatId).removeEventListener(chatEventListener);
            chatEventListener = null;
        }
    }
}
