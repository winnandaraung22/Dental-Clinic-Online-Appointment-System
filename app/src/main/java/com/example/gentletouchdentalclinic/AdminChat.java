package com.example.gentletouchdentalclinic;

public class AdminChat {
    private String chatId;
    private String doctorId;
    private String doctorName;
    private String doctorPhoto;
    private String lastMessage;
    private String lastMessageTime;
    private boolean unread;
    public AdminChat() {

    }

    public AdminChat(
            String chatId,
            String doctorId,
            String doctorName,
            String doctorPhoto,
            String lastMessage,
            String lastMessageTime,
            boolean unread) {

        this.chatId = chatId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorPhoto = doctorPhoto;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unread = unread;
    }
    public String getChatId() {
        return chatId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDoctorPhoto() {
        return doctorPhoto;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public boolean isUnread() { return unread; }
}