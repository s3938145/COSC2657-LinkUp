package com.example.linkup.model;

public class ChatMessage {

    private String messageId;
    private String chatId;     // ID for the chat room (individual or group)
    private String senderId;
    private String message;
    private long timestamp;

    public ChatMessage() {}

    public ChatMessage(String messageId, String chatId, String senderId, String message, long timestamp) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
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

