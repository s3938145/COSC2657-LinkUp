package com.example.linkup.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linkup.model.ChatMessage;
import com.example.linkup.repository.ChatRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private final ChatRepository chatRepository;
    private final MutableLiveData<List<ChatMessage>> messagesLiveData;
    private final MutableLiveData<String> errorLiveData;
    private final List<ChatMessage> currentMessages;

    public ChatViewModel() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case when the user is not authenticated.
            // You might want to show an error message or navigate to the login screen.
            chatRepository = null;
            messagesLiveData = null;
            errorLiveData = null;
            currentMessages = null;
            return;
        }

        chatRepository = new ChatRepository();
        messagesLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        currentMessages = messagesLiveData.getValue();
    }

    public void sendMessage(String chatId, String messageContent, long timestamp) {
        if (chatRepository != null) {
            chatRepository.sendMessage(chatId, messageContent, timestamp);
        }
    }

    public void addChatMessagesListener(String chatId) {
        if (chatRepository != null) {
            chatRepository.addChatMessagesListener(chatId, new ChatRepository.ChatUpdateListener() {
                @Override
                public void onChatUpdated(List<ChatMessage> messages) {
                    if (currentMessages != null) {
                        currentMessages.clear();
                        currentMessages.addAll(messages);
                        messagesLiveData.postValue(currentMessages);
                    }
                }

                @Override
                public void onChatMessageChanged(ChatMessage message) {
                    if (currentMessages != null) {
                        for (int i = 0; i < currentMessages.size(); i++) {
                            if (currentMessages.get(i).getMessageId().equals(message.getMessageId())) {
                                currentMessages.set(i, message);
                                break;
                            }
                        }
                        messagesLiveData.postValue(currentMessages);
                    }
                }

                @Override
                public void onChatMessageDeleted(String messageId) {
                    if (currentMessages != null) {
                        currentMessages.removeIf(chatMessage -> chatMessage.getMessageId().equals(messageId));
                        messagesLiveData.postValue(currentMessages);
                    }
                }

                @Override
                public void onChatUpdateFailed(DatabaseError error) {
                    if (errorLiveData != null) {
                        errorLiveData.postValue(error.getMessage());
                    }
                }
            });
        }
    }

    public void removeChatMessagesListener(String chatId) {
        if (chatRepository != null) {
            chatRepository.removeChatMessagesListener(chatId);
        }
    }

    public LiveData<List<ChatMessage>> getMessagesLiveData() {
        return messagesLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}
