package com.example.linkup.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.linkup.R;
import com.example.linkup.adapter.ChatAdapter;
import com.example.linkup.model.ChatMessage;
import com.example.linkup.model.User;
import com.example.linkup.viewModel.UserViewModel;
import java.util.List;

public class ChatFragment extends Fragment {

    private UserViewModel userViewModel;
    private RecyclerView recyclerViewChat;
    private ChatAdapter chatAdapter;
    private EditText editTextMessage;
    private Button buttonSendMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSendMessage = view.findViewById(R.id.buttonSendMessage);

        setupRecyclerView();

        // Observe the selected user's chat messages
        userViewModel.getSelectedUser().observe(getViewLifecycleOwner(), selectedUser -> {
            if (selectedUser != null) {
                loadChatMessages(selectedUser.getUserId());
            }
        });

        buttonSendMessage.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void loadChatMessages(String chatId) {
        // Implement logic to load chat messages from the database using the chatId
        // For example, you can use the userViewModel to fetch chat messages

        // Dummy data for testing
        List<ChatMessage> dummyChatMessages = getDummyChatMessages();
        chatAdapter.setMessages(dummyChatMessages);

        // Scroll to the last message
        if (dummyChatMessages.size() > 0) {
            recyclerViewChat.smoothScrollToPosition(dummyChatMessages.size() - 1);
        }
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Implement logic to send the message to the selected user
            // For example, you can use the userViewModel to send the message

            // Dummy data for testing
            ChatMessage sentMessage = new ChatMessage("senderId", "receiverId", messageText);
            chatAdapter.addMessage(sentMessage);

            // Clear the input field
            editTextMessage.setText("");

            // Scroll to the last message
            recyclerViewChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    // Dummy data for testing
    private List<ChatMessage> getDummyChatMessages() {
        // Replace this with your actual logic to fetch chat messages from the database
        // This is just a dummy list for testing
        return List.of(
                new ChatMessage("senderId", "receiverId", "Hello!"),
                new ChatMessage("receiverId", "senderId", "Hi there!"),
                new ChatMessage("senderId", "receiverId", "How are you?")
        );
    }
}
