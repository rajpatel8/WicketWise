package com.lords.becomebetter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
//import android:view.View;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayerActivity";

    private VideoView videoView;
    private AnnotationOverlay annotationOverlay;
    private FrameLayout videoContainer;
    private ImageButton backBtn, playPauseBtn;
    private SeekBar videoSeekBar;
    private TextView currentTimeText, totalTimeText, videoTitleText, studentNameText;
    private Button saveAnnotationsBtn, clearAnnotationsBtn;

    private DatabaseHelper databaseHelper;
    private Video currentVideo;
    private String coachEmail;
    private int coachId;
    private int videoId;
    private boolean viewOnly;
    private boolean isPlaying = false;
    private boolean isVideoReady = false;

    private Handler timeHandler = new Handler();
    private Runnable timeUpdateRunnable;

    // Annotation data
    private List<Annotation> annotations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        databaseHelper = new DatabaseHelper(this);

        // Get intent data
        videoId = getIntent().getIntExtra("videoId", 0);
        coachEmail = getIntent().getStringExtra("coachEmail");
        viewOnly = getIntent().getBooleanExtra("viewOnly", false);

        Log.d(TAG, "VideoPlayerActivity created - VideoID: " + videoId + ", ViewOnly: " + viewOnly);

        initializeViews();
        loadVideoData();
        setupVideoPlayer();
        setupAnnotationSystem();
        setupClickListeners();
    }

    private void initializeViews() {
        videoView = findViewById(R.id.videoView);
        videoContainer = findViewById(R.id.videoContainer);
        annotationOverlay = findViewById(R.id.annotationOverlay);
        backBtn = findViewById(R.id.backBtn);
        playPauseBtn = findViewById(R.id.playPauseBtn);
        videoSeekBar = findViewById(R.id.videoSeekBar);
        currentTimeText = findViewById(R.id.currentTimeText);
        totalTimeText = findViewById(R.id.totalTimeText);
        videoTitleText = findViewById(R.id.videoTitleText);
        studentNameText = findViewById(R.id.studentNameText);
        saveAnnotationsBtn = findViewById(R.id.saveAnnotationsBtn);
        clearAnnotationsBtn = findViewById(R.id.clearAnnotationsBtn);

        // Hide annotation controls if view only
        if (viewOnly) {
            saveAnnotationsBtn.setVisibility(View.GONE);
            clearAnnotationsBtn.setVisibility(View.GONE);
            if (annotationOverlay != null) {
                annotationOverlay.setDrawingEnabled(false);
            }
        } else {
            if (annotationOverlay != null) {
                annotationOverlay.setDrawingEnabled(true);
            }
        }

        Log.d(TAG, "Views initialized");
    }

    private void loadVideoData() {
        currentVideo = databaseHelper.getVideoById(videoId);
        if (currentVideo == null) {
            showError("Video not found");
            finish();
            return;
        }

        // Debug video information
        VideoDebugHelper.debugVideo(this, currentVideo);

        // Get coach data
        Coach coach = databaseHelper.getCoachByEmail(coachEmail);
        if (coach == null) {
            showError("Coach profile not found");
            finish();
            return;
        }
        coachId = coach.getId();

        // Set video info
        videoTitleText.setText(currentVideo.getVideoTitle());
        String studentName = databaseHelper.getStudentNameById(currentVideo.getStudentId());
        studentNameText.setText("Student: " + studentName);

        // Load existing annotations
        loadAnnotations();

        Log.d(TAG, "Video data loaded - Title: " + currentVideo.getVideoTitle());
        Log.d(TAG, "Video path: " + currentVideo.getVideoPath());
    }

    private void setupVideoPlayer() {
        File videoFile = new File(currentVideo.getVideoPath());
        Log.d(TAG, "Video file path: " + videoFile.getAbsolutePath());
        Log.d(TAG, "Video file exists: " + videoFile.exists());
        Log.d(TAG, "Video file length: " + videoFile.length());

        if (!videoFile.exists()) {
            showError("Video file not found at: " + videoFile.getAbsolutePath());
            finish();
            return;
        }

        if (videoFile.length() == 0) {
            showError("Video file is empty");
            finish();
            return;
        }

        try {
            Uri videoUri = Uri.fromFile(videoFile);
            Log.d(TAG, "Video URI: " + videoUri.toString());

            videoView.setVideoURI(videoUri);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isVideoReady = true;
                    int duration = mp.getDuration();

                    Log.d(TAG, "Video prepared successfully - Duration: " + duration + "ms");

                    // Set up seek bar
                    videoSeekBar.setMax(duration);
                    totalTimeText.setText(formatTime(duration));

                    // Start time updates
                    startTimeUpdates();

                    // Set video to fit properly
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

                    // DON'T auto-pause - let it stay ready to play
                    // The video will be in "prepared" state, ready to start when user clicks play
                    isPlaying = false;
                    updatePlayPauseButton();

                    Toast.makeText(VideoPlayerActivity.this, "Video ready - Duration: " + formatTime(duration), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Video is ready to play");
                }
            });

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e(TAG, "MediaPlayer Error: what=" + what + ", extra=" + extra);
                    String errorMsg = "Video playback error: ";

                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                            errorMsg += "Unknown error";
                            break;
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            errorMsg += "Media server died";
                            break;
                        default:
                            errorMsg += "Error code " + what;
                    }

                    showError(errorMsg + " (Extra: " + extra + ")");
                    return true; // Handled the error
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    updatePlayPauseButton();
                    stopTimeUpdates();
                    Log.d(TAG, "Video playback completed");
                }
            });

            // Seek bar listener
            videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && isVideoReady) {
                        videoView.seekTo(progress);
                        currentTimeText.setText(formatTime(progress));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

        } catch (Exception e) {
            Log.e(TAG, "Exception setting up video player", e);
            showError("Failed to setup video player: " + e.getMessage());
            finish();
        }

        Log.d(TAG, "Video player setup complete");
    }

    private void setupAnnotationSystem() {
        if (annotationOverlay != null) {
            // Set annotation overlay properties
            annotationOverlay.setDrawingColor(getResources().getColor(R.color.error_color));
            annotationOverlay.setDrawingWidth(8f);

            // Enable clicking and focusing
            annotationOverlay.setClickable(true);
            annotationOverlay.setFocusable(true);
        }

        Log.d(TAG, "Annotation system setup complete. Drawing enabled: " + !viewOnly);
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());

        playPauseBtn.setOnClickListener(v -> {
            if (isVideoReady) {
                try {
                    if (isPlaying) {
                        videoView.pause();
                        isPlaying = false;
                        Log.d(TAG, "Video paused");
                    } else {
                        videoView.start();
                        isPlaying = true;
                        Log.d(TAG, "Video started");
                    }
                    updatePlayPauseButton();
                } catch (Exception e) {
                    Log.e(TAG, "Error controlling video playback", e);
                    showError("Error controlling video playback: " + e.getMessage());
                }
            } else {
                Toast.makeText(this, "Video not ready yet", Toast.LENGTH_SHORT).show();
            }
        });

        saveAnnotationsBtn.setOnClickListener(v -> saveAllAnnotations());

        clearAnnotationsBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Clear Annotations")
                    .setMessage("Are you sure you want to clear all annotations for this video?")
                    .setPositiveButton("Yes, Clear", (dialog, which) -> clearAllAnnotations())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        Log.d(TAG, "Click listeners setup complete");
    }

    private void startTimeUpdates() {
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isVideoReady && videoView != null) {
                    try {
                        int currentPosition = videoView.getCurrentPosition();
                        videoSeekBar.setProgress(currentPosition);
                        currentTimeText.setText(formatTime(currentPosition));
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating time", e);
                    }
                }
                timeHandler.postDelayed(this, 100);
            }
        };
        timeHandler.post(timeUpdateRunnable);
    }

    private void stopTimeUpdates() {
        if (timeUpdateRunnable != null) {
            timeHandler.removeCallbacks(timeUpdateRunnable);
        }
    }

    private void updatePlayPauseButton() {
        if (isPlaying) {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
        } else {
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    private void saveAllAnnotations() {
        if (annotationOverlay == null) {
            Toast.makeText(this, "Annotation system not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current annotations from overlay
        List<AnnotationOverlay.AnnotationDrawing> currentDrawings = annotationOverlay.getAllAnnotations();

        if (currentDrawings.isEmpty()) {
            Toast.makeText(this, "No annotations to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete existing annotations for this video first
        databaseHelper.deleteAnnotationsByVideoId(videoId);

        // Save all current annotations
        int savedCount = 0;
        for (AnnotationOverlay.AnnotationDrawing drawing : currentDrawings) {
            long currentTime = isVideoReady ? videoView.getCurrentPosition() : 0;

            // Create annotation from drawing
            Annotation annotation = new Annotation(
                    videoId,
                    coachId,
                    currentTime,
                    Annotation.TYPE_DRAWING,
                    "drawing_data_" + System.currentTimeMillis(), // You can enhance this
                    0, 0 // x, y not used for drawing annotations
            );

            long result = databaseHelper.addAnnotation(annotation);
            if (result != -1) {
                savedCount++;
            }
        }

        if (savedCount > 0) {
            // Update video status
            databaseHelper.updateVideoStatus(videoId, "annotated");
            Toast.makeText(this, savedCount + " annotations saved successfully!", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
        } else {
            showError("Failed to save annotations");
        }

        Log.d(TAG, "Saved " + savedCount + " annotations");
    }

    private void clearAllAnnotations() {
        if (annotationOverlay != null) {
            annotationOverlay.clearAnnotations();
            Toast.makeText(this, "Annotations cleared", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Annotations cleared");
        }
    }

    private void loadAnnotations() {
        // Load existing annotations from database
        List<Annotation> existingAnnotations = databaseHelper.getAnnotationsByVideoId(videoId);

        for (Annotation annotation : existingAnnotations) {
            annotations.add(annotation);
            if (annotationOverlay != null) {
                annotationOverlay.addAnnotation(annotation);
            }
        }

        Log.d(TAG, "Loaded " + existingAnnotations.size() + " existing annotations");
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Error: " + message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimeUpdates();
        Log.d(TAG, "VideoPlayerActivity destroyed");
    }

    @Override
    public void onBackPressed() {
        if (!viewOnly && annotationOverlay != null && annotationOverlay.getAllAnnotations().size() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Unsaved Annotations")
                    .setMessage("You have unsaved annotations. Do you want to save them before leaving?")
                    .setPositiveButton("Save & Exit", (dialog, which) -> saveAllAnnotations())
                    .setNegativeButton("Exit Without Saving", (dialog, which) -> {
                        super.onBackPressed();
                        finish();
                    })
                    .setNeutralButton("Cancel", null)
                    .show();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}