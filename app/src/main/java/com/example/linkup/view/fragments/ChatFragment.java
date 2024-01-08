package com.example.linkup.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.adapter.ChatAdapter;
import com.example.linkup.model.ChatMessage;
import com.example.linkup.viewModel.ChatViewModel;

import java.util.List;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private String chatId; // Set this based on your chat logic

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize ChatViewModel
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        setupRecyclerView(view);
        setupMessageInput(view);

        // Observe chat messages
        chatViewModel.getMessagesLiveData().observe(getViewLifecycleOwner(), this::onChatMessagesReceived);
        chatViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), this::onErrorReceived);

        // Add listener for chat updates
        chatViewModel.addChatMessagesListener(chatId);

        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);
    }

    private void setupMessageInput(View view) {
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString();
        if (!messageText.isEmpty()) {
            chatViewModel.sendMessage(chatId, messageText, System.currentTimeMillis());
            editTextMessage.setText("");
        }
    }

    private void onChatMessagesReceived(List<ChatMessage> messages) {
        chatAdapter.setMessages(messages);
        recyclerView.scrollToPosition(messages.size() - 1); // Scroll to the latest message
    }

    private void onErrorReceived(String error) {
        // Handle error, e.g., show a toast message
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatViewModel.removeChatMessagesListener(chatId); // Clean up listener
    }

    // Additional methods as needed
}


