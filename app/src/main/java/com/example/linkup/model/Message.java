package com.example.linkup.model;

public class Message {

    private String senderId;
    private String text;
    private long timestamp;
    private String receiverId;
    private boolean hasRecentMessage;

    public boolean hasRecentMessage() {
        return hasRecentMessage;
    }

    public void setHasRecentMessage(boolean hasRecentMessage) {
        this.hasRecentMessage = hasRecentMessage;
    }

    public Message() {}

    public Message(String senderId, String receiverID, String text, long timestamp, boolean hasRecentMessage) {
        this.senderId = senderId;
        this.receiverId = receiverID;
        this.text = text;
        this.timestamp = timestamp;
        this.hasRecentMessage = false;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public String getReceiverId(){
        return receiverId;
    }
}
