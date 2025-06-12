package com.lords.becomebetter;

public class Annotation {
    private int annotationId;
    private int videoId;
    private int coachId;
    private long timestamp; // Video timestamp in milliseconds
    private String annotationType; // text, drawing, voice
    private String annotationData; // JSON data or text content
    private float xPosition;
    private float yPosition;
    private String createdAt;

    // Annotation types constants
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_DRAWING = "drawing";
    public static final String TYPE_VOICE = "voice";

    // Default constructor
    public Annotation() {}

    // Constructor for new annotation
    public Annotation(int videoId, int coachId, long timestamp, String annotationType,
                      String annotationData, float xPosition, float yPosition) {
        this.videoId = videoId;
        this.coachId = coachId;
        this.timestamp = timestamp;
        this.annotationType = annotationType;
        this.annotationData = annotationData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    // Full constructor
    public Annotation(int annotationId, int videoId, int coachId, long timestamp,
                      String annotationType, String annotationData, float xPosition,
                      float yPosition, String createdAt) {
        this.annotationId = annotationId;
        this.videoId = videoId;
        this.coachId = coachId;
        this.timestamp = timestamp;
        this.annotationType = annotationType;
        this.annotationData = annotationData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(int annotationId) {
        this.annotationId = annotationId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getCoachId() {
        return coachId;
    }

    public void setCoachId(int coachId) {
        this.coachId = coachId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public String getAnnotationData() {
        return annotationData;
    }

    public void setAnnotationData(String annotationData) {
        this.annotationData = annotationData;
    }

    public float getXPosition() {
        return xPosition;
    }

    public void setXPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getYPosition() {
        return yPosition;
    }

    public void setYPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public String getFormattedTimestamp() {
        long seconds = timestamp / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public boolean isTextAnnotation() {
        return TYPE_TEXT.equals(annotationType);
    }

    public boolean isDrawingAnnotation() {
        return TYPE_DRAWING.equals(annotationType);
    }

    public boolean isVoiceAnnotation() {
        return TYPE_VOICE.equals(annotationType);
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "annotationId=" + annotationId +
                ", videoId=" + videoId +
                ", coachId=" + coachId +
                ", timestamp=" + timestamp +
                ", annotationType='" + annotationType + '\'' +
                ", annotationData='" + annotationData + '\'' +
                '}';
    }
}