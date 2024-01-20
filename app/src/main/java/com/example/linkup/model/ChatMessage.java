package com.example.linkup.model;

public class ChatMessage {

    private String messageId;
    private String chatId;
    private String senderId;
    private String message;
    private long timestamp;

    // Existing constructor
    public ChatMessage(String messageId, String chatId, String senderId, String message, long timestamp) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Additional constructor without messageId and timestamp
    public ChatMessage(String chatId, String senderId, String message) {
        this.messageId = ""; // You can assign a default value or leave it empty
        this.chatId = chatId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = System.currentTimeMillis(); // Use the current timestamp as default
    }

    public String getMessageId() {
        return messageId;
    }
    public String getChatId() {
        return chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

