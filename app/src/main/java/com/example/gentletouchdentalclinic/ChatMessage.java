package com.example.gentletouchdentalclinic;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String message;
    private Timestamp timestamp;
    private boolean isRead;

    public ChatMessage() {

    }

    public ChatMessage(
            String senderId,
            String receiverId,
            String message,
            Timestamp timestamp,
            boolean isRead) {

        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }


    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}