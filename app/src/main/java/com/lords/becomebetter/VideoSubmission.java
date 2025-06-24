package com.lords.becomebetter;

import java.util.ArrayList;
import java.util.List;

public class VideoSubmission {
    private int submissionId;
    private String title;
    private String description;
    private String videoPath;
    private int studentId;
    private String studentName;
    private String submissionDate;
    private String status; // 'pending', 'reviewed'
    private List<Integer> selectedCoachIds;
    private List<VideoFeedback> feedbacks;

    public VideoSubmission() {
        this.selectedCoachIds = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
        this.status = "pending";
    }

    public VideoSubmission(String title, String description, String videoPath, int studentId) {
        this();
        this.title = title;
        this.description = description;
        this.videoPath = videoPath;
        this.studentId = studentId;
    }

    // Getters and Setters
    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
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

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Integer> getSelectedCoachIds() {
        return selectedCoachIds;
    }

    public void setSelectedCoachIds(List<Integer> selectedCoachIds) {
        this.selectedCoachIds = selectedCoachIds;
    }

    public void addSelectedCoachId(int coachId) {
        if (!this.selectedCoachIds.contains(coachId)) {
            this.selectedCoachIds.add(coachId);
        }
    }

    public void removeSelectedCoachId(int coachId) {
        this.selectedCoachIds.remove(Integer.valueOf(coachId));
    }

    public List<VideoFeedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<VideoFeedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public void addFeedback(VideoFeedback feedback) {
        this.feedbacks.add(feedback);
    }

    public int getFeedbackCount() {
        return feedbacks != null ? feedbacks.size() : 0;
    }

    public boolean hasFeedbackFromCoach(int coachId) {
        if (feedbacks == null) return false;
        return feedbacks.stream().anyMatch(f -> f.getCoachId() == coachId);
    }

    public VideoFeedback getFeedbackFromCoach(int coachId) {
        if (feedbacks == null) return null;
        return feedbacks.stream()
                .filter(f -> f.getCoachId() == coachId)
                .findFirst()
                .orElse(null);
    }

    public boolean isPending() {
        return "pending".equals(status);
    }

    public boolean isReviewed() {
        return "reviewed".equals(status);
    }

    public String getFormattedSubmissionDate() {
        if (submissionDate == null) return "Unknown";
        try {
            String[] parts = submissionDate.split(" ");
            if (parts.length > 0) {
                return parts[0]; // Return just the date part
            }
        } catch (Exception e) {
            // Handle parsing errors gracefully
        }
        return "Unknown";
    }

    // Additional utility methods that might be needed
    public String getShortSubmissionDate() {
        if (submissionDate == null) return "N/A";
        try {
            String[] parts = submissionDate.split(" ");
            if (parts.length > 0) {
                String datePart = parts[0]; // "2025-06-24"
                String[] dateParts = datePart.split("-");
                if (dateParts.length == 3) {
                    return dateParts[1] + "/" + dateParts[2]; // "06/24"
                }
            }
        } catch (Exception e) {
            // Handle parsing errors gracefully
        }
        return "N/A";
    }

    public String getTimeAgo() {
        if (submissionDate == null) return "Unknown";
        try {
            // This is a simple implementation - you could enhance it
            return "Recently"; // For now, just return "Recently"
        } catch (Exception e) {
            return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "VideoSubmission{" +
                "submissionId=" + submissionId +
                ", title='" + title + '\'' +
                ", studentId=" + studentId +
                ", status='" + status + '\'' +
                ", selectedCoachesCount=" + selectedCoachIds.size() +
                ", feedbacksCount=" + getFeedbackCount() +
                '}';
    }
}