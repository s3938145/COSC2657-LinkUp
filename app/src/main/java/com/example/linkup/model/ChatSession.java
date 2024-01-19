package com.example.linkup.model;

import java.util.Map;

public class ChatSession {

    private String receiverId; // Add this field

    private Map<String, Message> messages;

    public ChatSession() {}

    public ChatSession(String receiverId, Map<String, Message> messages) {
        this.receiverId = receiverId;
        this.messages = messages;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public Map<String, Message> getMessages() {
        return messages;
    }

}
