package com.lords.becomebetter;

public class Video {
    private int videoId;
    private int studentId;
    private int coachId;
    private String videoPath;
    private String videoTitle;
    private String videoDescription;
    private long videoDuration; // in milliseconds
    private String uploadDate;
    private String status; // pending, annotated, reviewed
    private String thumbnailPath;
    private String createdAt;

    // Default constructor
    public Video() {}

    // Constructor for new video upload
    public Video(int studentId, int coachId, String videoPath, String videoTitle,
                 String videoDescription, long videoDuration, String thumbnailPath) {
        this.studentId = studentId;
        this.coachId = coachId;
        this.videoPath = videoPath;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoDuration = videoDuration;
        this.thumbnailPath = thumbnailPath;
        this.status = "pending";
    }

    // Full constructor
    public Video(int videoId, int studentId, int coachId, String videoPath, String videoTitle,
                 String videoDescription, long videoDuration, String uploadDate, String status,
                 String thumbnailPath, String createdAt) {
        this.videoId = videoId;
        this.studentId = studentId;
        this.coachId = coachId;
        this.videoPath = videoPath;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoDuration = videoDuration;
        this.uploadDate = uploadDate;
        this.status = status;
        this.thumbnailPath = thumbnailPath;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCoachId() {
        return coachId;
    }

    public void setCoachId(int coachId) {
        this.coachId = coachId;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public String getFormattedDuration() {
        long seconds = videoDuration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public boolean isPending() {
        return "pending".equals(status);
    }

    public boolean isAnnotated() {
        return "annotated".equals(status);
    }

    public boolean isReviewed() {
        return "reviewed".equals(status);
    }

    @Override
    public String toString() {
        return "Video{" +
                "videoId=" + videoId +
                ", studentId=" + studentId +
                ", coachId=" + coachId +
                ", videoTitle='" + videoTitle + '\'' +
                ", status='" + status + '\'' +
                ", videoDuration=" + videoDuration +
                '}';
    }
}