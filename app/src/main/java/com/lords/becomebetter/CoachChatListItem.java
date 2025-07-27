package com.lords.becomebetter;

public class CoachChatListItem {
    private int studentId;
    private String studentName;
    private String studentSkillLevel;
    private String lastMessage;
    private String lastMessageTime;
    private int unreadCount;

    public CoachChatListItem() {}

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentSkillLevel() { return studentSkillLevel; }
    public void setStudentSkillLevel(String studentSkillLevel) { this.studentSkillLevel = studentSkillLevel; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }

    public boolean hasUnreadMessages() { return unreadCount > 0; }

    public String getUnreadCountText() {
        if (unreadCount > 99) return "99+";
        return String.valueOf(unreadCount);
    }
}