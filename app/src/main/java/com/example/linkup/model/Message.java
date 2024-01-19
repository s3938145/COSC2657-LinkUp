package com.example.linkup.model;

public class Message {

    private String senderId;
    private String text;
    private long timestamp;
    private String receiverId;

    public Message() {
        // Default constructor required for DataSnapshot.getValue(Message.class)
    }

    public Message(String senderId, String receiverID, String text, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverID;
        this.text = text;
        this.timestamp = timestamp;
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
