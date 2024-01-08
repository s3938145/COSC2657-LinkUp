package com.example.linkup.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linkup.model.ChatMessage;
import com.example.linkup.repository.ChatRepository;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private final ChatRepository chatRepository;
    private final MutableLiveData<List<ChatMessage>> messagesLiveData;
    private final MutableLiveData<String> errorLiveData;
    private List<ChatMessage> currentMessages;

    public ChatViewModel() {
        chatRepository = new ChatRepository();
        messagesLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        currentMessages = new ArrayList<>();
    }

    public void sendMessage(String chatId, String messageContent, long timestamp) {
        chatRepository.sendMessage(chatId, messageContent, timestamp);
    }

    public void addChatMessagesListener(String chatId) {
        chatRepository.addChatMessagesListener(chatId, new ChatRepository.ChatUpdateListener() {
            @Override
            public void onChatUpdated(List<ChatMessage> messages) {
                currentMessages.addAll(messages);
                messagesLiveData.postValue(currentMessages);
            }

            @Override
            public void onChatMessageChanged(ChatMessage message) {
                for (int i = 0; i < currentMessages.size(); i++) {
                    if (currentMessages.get(i).getMessageId().equals(message.getMessageId())) {
                        currentMessages.set(i, message);
                        break;
                    }
                }
                messagesLiveData.postValue(currentMessages);
            }

            @Override
            public void onChatMessageDeleted(String messageId) {
                Iterator<ChatMessage> iterator = currentMessages.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getMessageId().equals(messageId)) {
                        iterator.remove();
                        break;
                    }
                }
                messagesLiveData.postValue(currentMessages);
            }

            @Override
            public void onChatUpdateFailed(DatabaseError error) {
                errorLiveData.postValue(error.getMessage());
            }
        });
    }

    public void removeChatMessagesListener(String chatId) {
        chatRepository.removeChatMessagesListener(chatId);
    }

    public LiveData<List<ChatMessage>> getMessagesLiveData() {
        return messagesLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}


