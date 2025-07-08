package com.lords.becomebetter;

/**
 * Interface for communication between AnnotationOverlay and VideoPlayerActivity
 */
public interface VideoPlayerInterface {

    /**
     * Get current video position in milliseconds
     */
    long getCurrentVideoPosition();

    /**
     * Pause the video
     */
    void pauseVideo();

    /**
     * Check if video is ready
     */
    boolean isVideoReady();
}
