package com.lords.becomebetter;

public class ChatListItem {
    private int coachId;
    private String coachName;
    private String coachSpecialization;
    private String lastMessage;
    private String lastMessageTime;
    private int unreadCount;

    public ChatListItem() {}

    // Getters and Setters
    public int getCoachId() {
        return coachId;
    }

    public void setCoachId(int coachId) {
        this.coachId = coachId;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getCoachSpecialization() {
        return coachSpecialization;
    }

    public void setCoachSpecialization(String coachSpecialization) {
        this.coachSpecialization = coachSpecialization;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    // Utility methods
    public boolean hasUnreadMessages() {
        return unreadCount > 0;
    }

    public String getUnreadCountText() {
        if (unreadCount > 99) {
            return "99+";
        }
        return String.valueOf(unreadCount);
    }
}