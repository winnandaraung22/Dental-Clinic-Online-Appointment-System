package com.example.gentletouchdentalclinic;

import com.google.firebase.Timestamp;

public class AdminMessage {

    private String messageId;
    private String senderId;
    private String receiverId;
    private String message;
    private boolean isRead;
    private Timestamp timestamp;

    public AdminMessage() {

    }

    public AdminMessage(
            String messageId,
            String senderId,
            String receiverId,
            String message,
            boolean isRead,
            Timestamp timestamp) {

        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.isRead = isRead;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
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

    public boolean isRead() {
        return isRead;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}