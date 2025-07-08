package com.lords.becomebetter;

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

    public VideoFeedback() {}

    // Getters and setters
    public int getSubmissionId() { return submissionId; }
    public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }

    public int getCoachId() { return coachId; }
    public void setCoachId(int coachId) { this.coachId = coachId; }

    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }

    public String getFeedbackDate() { return feedbackDate; }
    public void setFeedbackDate(String feedbackDate) { this.feedbackDate = feedbackDate; }

    public String getAnnotationData() { return annotationData; }
    public void setAnnotationData(String annotationData) { this.annotationData = annotationData; }

    public String getVoiceRecordingPath() { return voiceRecordingPath; }
    public void setVoiceRecordingPath(String voiceRecordingPath) { this.voiceRecordingPath = voiceRecordingPath; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}