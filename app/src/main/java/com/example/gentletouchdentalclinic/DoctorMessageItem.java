package com.example.gentletouchdentalclinic;

import com.google.firebase.Timestamp;

public class DoctorMessageItem {

    private String doctorId;
    private String doctorName;
    private String lastMessage;
    private String profileImage;
    private String messageTime;
    private Timestamp lastMessageTimestamp;
    private boolean unread;

    public DoctorMessageItem(
            String doctorId,
            String doctorName,
            String lastMessage,
            String profileImage,
            String messageTime,
            Timestamp lastMessageTimestamp,
            boolean unread) {

        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.lastMessage = lastMessage;
        this.profileImage = profileImage;
        this.messageTime = messageTime;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.unread = unread;
    }

    public String getDoctorId() {

        return doctorId;
    }

    public String getDoctorName() {

        return doctorName;
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

}