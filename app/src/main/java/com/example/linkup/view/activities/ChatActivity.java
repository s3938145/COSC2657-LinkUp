package com.example.linkup.view.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.adapter.MessageAdapter;
import com.example.linkup.model.ChatSession;
import com.example.linkup.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;
    private CardView sendButton;

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    private DatabaseReference messagesReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    ArrayList<Message> m;

    private String receiverUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acitivity);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        m = new ArrayList<>();
        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            finish();
        }

        messagesReference = FirebaseDatabase.getInstance().getReference("messages");

        String receiverUserId = getIntent().getStringExtra("receiverUserId");

        if (receiverUserId != null) {
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String messageText = messageEditText.getText().toString().trim();
                    sendMessage(receiverUserId, messageText);
                }
            });
        }
        messagesReference.child(getChatKey(currentUser.getUid(), receiverUserId)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                if (snapshot.exists()) {
                    ChatSession chatSession = snapshot.getValue(ChatSession.class);
                    Log.d("ChatActivity", "DataSnapshot: " + snapshot.getValue());
                    if (chatSession != null && chatSession.getMessages() != null) {
                        Log.d("ChatActivity", "Messages received: " + chatSession.getMessages().size());
                        messageAdapter.setMessages(chatSession.getMessages());
                        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    }
                } else {
                    Log.d("ChatActivity", "Received null chat session");
                }
            }



            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                Log.d("ChatActivity", "DataSnapshot changed: " + snapshot.getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void sendMessage(String receiverUserId, String messageText) {
        if (currentUser != null && !messageText.isEmpty()) {
            String senderUserId = currentUser.getUid();
            Message message = new Message(senderUserId, receiverUserId, messageText, System.currentTimeMillis());

            // Get the chat key based on user IDs
            String chatKey = getChatKey(senderUserId, receiverUserId);

            // Update the specific chat session in the database
            messagesReference.child(chatKey).push().setValue(message);
            messageEditText.setText("");
            Log.d("ChatActivity", "Sending message: " + messageText);
        }
    }
    private String getChatKey(String userId1, String userId2) {
        return (userId1.compareTo(userId2) < 0) ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
