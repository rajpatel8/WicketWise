package com.lords.becomebetter;

public class VoiceRecording {
    private int recordingId;
    private int feedbackId;
    private String recordingPath;
    private int duration; // Duration in milliseconds
    private int videoTimestamp; // Timestamp in video when this recording was made (milliseconds)
    private String createdDate;
    private String title; // Optional title for the recording
    private String description; // Optional description

    // Constructors
    public VoiceRecording() {
    }

    public VoiceRecording(int feedbackId, String recordingPath, int videoTimestamp) {
        this.feedbackId = feedbackId;
        this.recordingPath = recordingPath;
        this.videoTimestamp = videoTimestamp;
    }

    public VoiceRecording(int feedbackId, String recordingPath, int duration, int videoTimestamp) {
        this.feedbackId = feedbackId;
        this.recordingPath = recordingPath;
        this.duration = duration;
        this.videoTimestamp = videoTimestamp;
    }

    // Getters and Setters
    public int getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(int recordingId) {
        this.recordingId = recordingId;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getRecordingPath() {
        return recordingPath;
    }

    public void setRecordingPath(String recordingPath) {
        this.recordingPath = recordingPath;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getVideoTimestamp() {
        return videoTimestamp;
    }

    public void setVideoTimestamp(int videoTimestamp) {
        this.videoTimestamp = videoTimestamp;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Helper methods
    public String getFormattedDuration() {
        if (duration <= 0) return "00:00";

        int seconds = duration / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getFormattedVideoTimestamp() {
        int seconds = videoTimestamp / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public boolean isValidRecording() {
        return recordingPath != null &&
                !recordingPath.trim().isEmpty() &&
                videoTimestamp >= 0;
    }

    public String getDisplayTitle() {
        if (title != null && !title.trim().isEmpty()) {
            return title;
        }
        return "Voice feedback at " + getFormattedVideoTimestamp();
    }

    public String getDisplayDescription() {
        if (description != null && !description.trim().isEmpty()) {
            return description;
        }
        return "Recording duration: " + getFormattedDuration();
    }

    // File management helpers
    public String getFileName() {
        if (recordingPath == null) return null;

        int lastSlash = recordingPath.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < recordingPath.length() - 1) {
            return recordingPath.substring(lastSlash + 1);
        }
        return recordingPath;
    }

    public boolean fileExists() {
        if (recordingPath == null) return false;

        try {
            java.io.File file = new java.io.File(recordingPath);
            return file.exists() && file.length() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public long getFileSizeBytes() {
        if (!fileExists()) return 0;

        try {
            java.io.File file = new java.io.File(recordingPath);
            return file.length();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getFormattedFileSize() {
        long bytes = getFileSizeBytes();
        if (bytes == 0) return "0 KB";

        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    @Override
    public String toString() {
        return "VoiceRecording{" +
                "recordingId=" + recordingId +
                ", feedbackId=" + feedbackId +
                ", duration=" + getFormattedDuration() +
                ", videoTimestamp=" + getFormattedVideoTimestamp() +
                ", fileName='" + getFileName() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        VoiceRecording other = (VoiceRecording) obj;
        return recordingId == other.recordingId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(recordingId);
    }
}