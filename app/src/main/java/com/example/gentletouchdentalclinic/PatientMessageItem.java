package com.example.gentletouchdentalclinic;

import com.google.firebase.Timestamp;

public class PatientMessageItem {

    private String patientId;
    private String patientName;
    private String lastMessage;
    private String profileImage;
    private String messageTime;
    private Timestamp lastMessageTimestamp;
    private String messageType;
    private boolean unread;


    public PatientMessageItem(
            String patientId,
            String patientName,
            String lastMessage,
            String profileImage,
            String messageTime,
            Timestamp lastMessageTimestamp,
            boolean unread) {

        this.patientId = patientId;
        this.patientName = patientName;
        this.lastMessage = lastMessage;
        this.profileImage = profileImage;
        this.messageTime = messageTime;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.unread = unread;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public String getMessageType() {return messageType;}
    public void setMessageType(String messageType) {this.messageType = messageType;}
}