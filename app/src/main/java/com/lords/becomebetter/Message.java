package com.lords.becomebetter;

public class Message {
    private int messageId;
    private int senderId;
    private int receiverId;
    private String senderType; // "student" or "coach"
    private String messageText;
    private String timestamp;
    private boolean isRead;

    // Default constructor
    public Message() {}

    // Constructor with parameters
    public Message(int senderId, int receiverId, String senderType, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderType = senderType;
        this.messageText = messageText;
        this.isRead = false;
    }

    // Getters and Setters
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    // Utility method to check if message is sent by current user
    public boolean isSentByCurrentUser(int currentUserId, String currentUserType) {
        return this.senderId == currentUserId && this.senderType.equals(currentUserType);
    }

    // Format timestamp for display
    public String getFormattedTime() {
        if (timestamp == null) return "";

        try {
            // Extract time part from timestamp (assuming format: "YYYY-MM-DD HH:MM:SS")
            String[] parts = timestamp.split(" ");
            if (parts.length > 1) {
                String timePart = parts[1];
                String[] timeParts = timePart.split(":");
                if (timeParts.length >= 2) {
                    return timeParts[0] + ":" + timeParts[1]; // HH:MM format
                }
            }
        } catch (Exception e) {
            // Handle parsing errors gracefully
        }
        return timestamp;
    }
}