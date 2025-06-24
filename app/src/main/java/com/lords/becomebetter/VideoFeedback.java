package com.lords.becomebetter;

import java.util.ArrayList;
import java.util.List;

public class VideoFeedback {
    private int feedbackId;
    private int submissionId;
    private int coachId;
    private String coachName;
    private String coachEmail;
    private String feedbackText;
    private String feedbackDate;
    private String annotationData; // JSON string containing annotation information
    private String voiceRecordingPath;
    private List<VoiceRecording> voiceRecordings;
    private int rating; // 1-5 star rating
    private String status; // 'draft', 'completed'

    public VideoFeedback() {
        this.voiceRecordings = new ArrayList<>();
        this.status = "draft";
        this.rating = 0;
    }

    public VideoFeedback(int submissionId, int coachId, String feedbackText) {
        this();
        this.submissionId = submissionId;
        this.coachId = coachId;
        this.feedbackText = feedbackText;
    }

    // Getters and Setters
    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

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

    public String getCoachEmail() {
        return coachEmail;
    }

    public void setCoachEmail(String coachEmail) {
        this.coachEmail = coachEmail;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public String getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(String feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public String getAnnotationData() {
        return annotationData;
    }

    public void setAnnotationData(String annotationData) {
        this.annotationData = annotationData;
    }

    public String getVoiceRecordingPath() {
        return voiceRecordingPath;
    }

    public void setVoiceRecordingPath(String voiceRecordingPath) {
        this.voiceRecordingPath = voiceRecordingPath;
    }

    public List<VoiceRecording> getVoiceRecordings() {
        return voiceRecordings;
    }

    public void setVoiceRecordings(List<VoiceRecording> voiceRecordings) {
        this.voiceRecordings = voiceRecordings;
    }

    public void addVoiceRecording(VoiceRecording recording) {
        this.voiceRecordings.add(recording);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = Math.max(0, Math.min(5, rating)); // Ensure rating is between 0-5
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasAnnotations() {
        return annotationData != null && !annotationData.trim().isEmpty();
    }

    public boolean hasVoiceRecordings() {
        return voiceRecordings != null && !voiceRecordings.isEmpty();
    }

    public boolean hasMainVoiceRecording() {
        return voiceRecordingPath != null && !voiceRecordingPath.trim().isEmpty();
    }

    public int getVoiceRecordingCount() {
        return voiceRecordings != null ? voiceRecordings.size() : 0;
    }

    public boolean isCompleted() {
        return "completed".equals(status);
    }

    public boolean isDraft() {
        return "draft".equals(status);
    }

    public String getFormattedFeedbackDate() {
        if (feedbackDate == null) return "Unknown";
        try {
            String[] parts = feedbackDate.split(" ");
            if (parts.length > 0) {
                return parts[0]; // Return just the date part
            }
        } catch (Exception e) {
            // Handle parsing errors gracefully
        }
        return "Unknown";
    }

    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    public String getFeedbackSummary() {
        StringBuilder summary = new StringBuilder();

        if (feedbackText != null && !feedbackText.trim().isEmpty()) {
            summary.append("Text feedback");
        }

        if (hasAnnotations()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Annotations");
        }

        if (hasVoiceRecordings() || hasMainVoiceRecording()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Voice feedback");
        }

        if (rating > 0) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("Rating: ").append(getRatingStars());
        }

        return summary.length() > 0 ? summary.toString() : "No feedback provided";
    }

    @Override
    public String toString() {
        return "VideoFeedback{" +
                "feedbackId=" + feedbackId +
                ", submissionId=" + submissionId +
                ", coachName='" + coachName + '\'' +
                ", coachEmail='" + coachEmail + '\'' +
                ", status='" + status + '\'' +
                ", rating=" + rating +
                ", hasAnnotations=" + hasAnnotations() +
                ", voiceRecordingsCount=" + getVoiceRecordingCount() +
                '}';
    }
}