package com.lords.becomebetter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import java.io.File;

public class VideoDebugHelper {

    private static final String TAG = "VideoDebugHelper";

    public static void debugVideo(Context context, Video video) {
        Log.d(TAG, "=== VIDEO DEBUG INFORMATION ===");
        Log.d(TAG, "Video ID: " + video.getVideoId());
        Log.d(TAG, "Video Title: " + video.getVideoTitle());
        Log.d(TAG, "Video Path: " + video.getVideoPath());
        Log.d(TAG, "Video Duration (DB): " + video.getVideoDuration() + " ms");
        Log.d(TAG, "Video Status: " + video.getStatus());

        File videoFile = new File(video.getVideoPath());
        Log.d(TAG, "File exists: " + videoFile.exists());
        Log.d(TAG, "File size: " + videoFile.length() + " bytes");
        Log.d(TAG, "File path: " + videoFile.getAbsolutePath());
        Log.d(TAG, "File readable: " + videoFile.canRead());

        if (videoFile.exists()) {
            // Get actual video metadata
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(video.getVideoPath());

                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                String mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                String hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);

                Log.d(TAG, "Actual Duration: " + duration + " ms");
                Log.d(TAG, "Video Dimensions: " + width + "x" + height);
                Log.d(TAG, "Bitrate: " + bitrate);
                Log.d(TAG, "MIME Type: " + mimeType);
                Log.d(TAG, "Has Video: " + hasVideo);
                Log.d(TAG, "Has Audio: " + hasAudio);

            } catch (Exception e) {
                Log.e(TAG, "Error reading video metadata", e);
            } finally {
                try {
                    retriever.release();
                } catch (Exception e) {
                    Log.e(TAG, "Error releasing MediaMetadataRetriever", e);
                }
            }
        }

        Log.d(TAG, "=== END VIDEO DEBUG ===");
    }

    public static void debugVideoDirectory(Context context) {
        Log.d(TAG, "=== VIDEO DIRECTORY DEBUG ===");

        File videoDir = new File(context.getFilesDir(), "videos");
        Log.d(TAG, "Video directory path: " + videoDir.getAbsolutePath());
        Log.d(TAG, "Video directory exists: " + videoDir.exists());

        if (videoDir.exists()) {
            File[] files = videoDir.listFiles();
            if (files != null) {
                Log.d(TAG, "Number of video files: " + files.length);
                for (File file : files) {
                    Log.d(TAG, "File: " + file.getName() + " - Size: " + file.length() + " bytes");
                }
            } else {
                Log.d(TAG, "Video directory is empty or cannot be read");
            }
        }

        Log.d(TAG, "=== END VIDEO DIRECTORY DEBUG ===");
    }
}