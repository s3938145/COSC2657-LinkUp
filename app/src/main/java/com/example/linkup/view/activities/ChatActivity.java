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


    // Receiver's user ID obtained from MessageScreenFragment
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

        // Use receiverUserId in your logic
        if (receiverUserId != null) {
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Retrieve the message text
                    String messageText = messageEditText.getText().toString().trim();

                    // Call the sendMessage method with the receiverUserId
                    sendMessage(receiverUserId, messageText);
                }
            });
        }
        messagesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                if (snapshot.exists()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        Log.d("ChatActivity", "Message added: " + message.getText());
                        messageAdapter.addMessage(message);
                        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    }
                } else {
                    Log.d("ChatActivity", "Received null message");
                }
            }



            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Not used in this example
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Not used in this example
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Not used in this example
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void sendMessage(String receiverUserId, String messageText) {
        if (currentUser != null && !messageText.isEmpty()) {
            // Create a Message object
            String senderUserId = currentUser.getUid();
            Message message = new Message(senderUserId, receiverUserId, messageText, System.currentTimeMillis());

            // Push the message to Firebase Realtime Database under the specific user's node
            messagesReference.child(receiverUserId).push().setValue(message);

            // Clear the input field
            messageEditText.setText("");
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // This is the ID of the back button
            // Navigate back to MessageScreenFragment or finish() to close the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
