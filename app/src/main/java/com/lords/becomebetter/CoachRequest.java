package com.lords.becomebetter;

public class CoachRequest {
    private int requestId;
    private int studentId;
    private int coachId;
    private String studentName;
    private String studentEmail;
    private String coachName;
    private String coachEmail;
    private String status; // 'pending', 'accepted', 'rejected'
    private String message; // Student's message to coach
    private String responseMessage; // Coach's response message
    private String requestDate;
    private String responseDate;

    // Request status constants
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";

    // Default constructor
    public CoachRequest() {}

    // Constructor for new request
    public CoachRequest(int studentId, int coachId, String message) {
        this.studentId = studentId;
        this.coachId = coachId;
        this.message = message;
        this.status = STATUS_PENDING;
    }

    // Full constructor
    public CoachRequest(int requestId, int studentId, int coachId, String studentName,
                        String studentEmail, String coachName, String coachEmail,
                        String status, String message, String responseMessage,
                        String requestDate, String responseDate) {
        this.requestId = requestId;
        this.studentId = studentId;
        this.coachId = coachId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.coachName = coachName;
        this.coachEmail = coachEmail;
        this.status = status;
        this.message = message;
        this.responseMessage = responseMessage;
        this.requestDate = requestDate;
        this.responseDate = responseDate;
    }

    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }

    // Helper methods
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    public boolean isAccepted() {
        return STATUS_ACCEPTED.equals(status);
    }

    public boolean isRejected() {
        return STATUS_REJECTED.equals(status);
    }

    public String getFormattedRequestDate() {
        if (requestDate == null) return "Unknown";
        try {
            // Simple date formatting - you can enhance this
            String[] parts = requestDate.split(" ");
            if (parts.length > 0) {
                return parts[0]; // Return just the date part
            }
        } catch (Exception e) {
            // Handle parsing errors gracefully
        }
        return "Unknown";
    }

    public String getStatusColor() {
        switch (status) {
            case STATUS_PENDING:
                return "#FFA726"; // Orange
            case STATUS_ACCEPTED:
                return "#66BB6A"; // Green
            case STATUS_REJECTED:
                return "#EF5350"; // Red
            default:
                return "#9E9E9E"; // Gray
        }
    }

    @Override
    public String toString() {
        return "CoachRequest{" +
                "requestId=" + requestId +
                ", studentId=" + studentId +
                ", coachId=" + coachId +
                ", studentName='" + studentName + '\'' +
                ", coachName='" + coachName + '\'' +
                ", status='" + status + '\'' +
                ", requestDate='" + requestDate + '\'' +
                '}';
    }
}