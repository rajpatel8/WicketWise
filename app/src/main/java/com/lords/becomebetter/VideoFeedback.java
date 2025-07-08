package com.lords.becomebetter;

import java.util.ArrayList;
import java.util.List;

public class VideoFeedback {
    private int feedbackId;
    private int submissionId;
    private int coachId;
    private String feedbackText;
    private String feedbackDate;
    private String annotationData;
    private String voiceRecordingPath;
    private int rating;
    private String status;

    // Additional properties needed by the application
    private String coachName;
    private String coachEmail;
    private List<VoiceRecording> voiceRecordings;

    // Default constructor
    public VideoFeedback() {
        this.voiceRecordings = new ArrayList<>();
    }

    // Constructor used in EnhancedVideoPlayerActivity
    public VideoFeedback(int submissionId, int coachId, String feedbackText) {
        this();
        this.submissionId = submissionId;
        this.coachId = coachId;
        this.feedbackText = feedbackText;
    }

    // Full constructor
    public VideoFeedback(int feedbackId, int submissionId, int coachId, String feedbackText,
                         String feedbackDate, String annotationData, String voiceRecordingPath,
                         int rating, String status) {
        this();
        this.feedbackId = feedbackId;
        this.submissionId = submissionId;
        this.coachId = coachId;
        this.feedbackText = feedbackText;
        this.feedbackDate = feedbackDate;
        this.annotationData = annotationData;
        this.voiceRecordingPath = voiceRecordingPath;
        this.rating = rating;
        this.status = status;
    }

    // Getters and setters for existing fields
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getters and setters for additional fields
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

    public List<VoiceRecording> getVoiceRecordings() {
        if (voiceRecordings == null) {
            voiceRecordings = new ArrayList<>();
        }
        return voiceRecordings;
    }

    public void setVoiceRecordings(List<VoiceRecording> voiceRecordings) {
        this.voiceRecordings = voiceRecordings;
    }

    // Utility methods
    public void addVoiceRecording(VoiceRecording recording) {
        if (voiceRecordings == null) {
            voiceRecordings = new ArrayList<>();
        }
        voiceRecordings.add(recording);
    }

    public void removeVoiceRecording(VoiceRecording recording) {
        if (voiceRecordings != null) {
            voiceRecordings.remove(recording);
        }
    }

    public boolean hasVoiceRecordings() {
        return voiceRecordings != null && !voiceRecordings.isEmpty();
    }
}