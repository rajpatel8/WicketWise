package com.lords.becomebetter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

    // UI Components
    private VideoView videoView;
    private AnnotationOverlay annotationOverlay;
    private FrameLayout videoContainer;
    private ImageButton backBtn, playPauseBtn;
    private SeekBar videoSeekBar;
    private TextView currentTimeText, totalTimeText, videoTitleText, studentNameText;
    private Button saveAnnotationsBtn, clearAnnotationsBtn;

    // Data & State
    private DatabaseHelper databaseHelper;
    private Video currentVideo;
    private VideoSubmission videoSubmission;
    private String coachEmail;
    private int coachId;
    private int videoId;
    private boolean viewOnly;
    private boolean isSubmission;

    // Video State
    private boolean isPlaying = false;
    private boolean isVideoReady = false;
    private int videoDuration = 0;

    // Time Updates
    private Handler timeHandler = new Handler(Looper.getMainLooper());
    private Runnable timeUpdateRunnable;

    // Annotations
    private List<Annotation> annotations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Log.d(TAG, "🚀 VideoPlayerActivity onCreate started");

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Get intent data
        extractIntentData();

        // Initialize UI
        initializeViews();

        // Load video data
        loadVideoData();

        // Setup video player
        setupVideoPlayer();

        // Setup annotations
        setupAnnotationSystem();

        // Setup click listeners
        setupClickListeners();

        // Setup seek bar
        setupSeekBar();

        Log.d(TAG, "✅ VideoPlayerActivity onCreate completed");
    }

    private void extractIntentData() {
        videoId = getIntent().getIntExtra("videoId", 0);
        coachEmail = getIntent().getStringExtra("coachEmail");
        viewOnly = getIntent().getBooleanExtra("viewOnly", false);
        isSubmission = getIntent().getBooleanExtra("isSubmission", false);

        Log.d(TAG, "📋 Intent Data - VideoID: " + videoId + ", ViewOnly: " + viewOnly + ", IsSubmission: " + isSubmission);

        if (videoId == 0) {
            Log.e(TAG, "❌ Invalid video ID received");
            showError("Invalid video ID");
            finish();
            return;
        }

        if (coachEmail == null || coachEmail.isEmpty()) {
            Log.e(TAG, "❌ Coach email not provided");
            showError("Coach information missing");
            finish();
            return;
        }
    }

    private void initializeViews() {
        Log.d(TAG, "🎨 Initializing views...");

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

        // Configure annotation controls based on view mode
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

        // Initialize time displays
        currentTimeText.setText("00:00");
        totalTimeText.setText("00:00");

        Log.d(TAG, "✅ Views initialized successfully");
    }

    private void loadVideoData() {
        Log.d(TAG, "📂 Loading video data...");

        if (isSubmission) {
            loadVideoSubmissionData();
        } else {
            loadRegularVideoData();
        }

        // Get coach data
        Coach coach = databaseHelper.getCoachByEmail(coachEmail);
        if (coach == null) {
            Log.e(TAG, "❌ Coach not found: " + coachEmail);
            showError("Coach profile not found");
            finish();
            return;
        }
        coachId = coach.getId();
        Log.d(TAG, "✅ Coach found - ID: " + coachId);

        // Load annotations
        loadAnnotations();
    }

    private void loadVideoSubmissionData() {
        Log.d(TAG, "🔍 Loading video submission with ID: " + videoId);

        videoSubmission = databaseHelper.getVideoSubmissionById(videoId);
        if (videoSubmission == null) {
            Log.e(TAG, "❌ Video submission not found: " + videoId);
            showError("Video submission not found");
            finish();
            return;
        }

        Log.d(TAG, "✅ Video submission found: " + videoSubmission.getTitle());

        // Create Video object for compatibility
        currentVideo = new Video();
        currentVideo.setVideoId(videoSubmission.getSubmissionId());
        currentVideo.setStudentId(videoSubmission.getStudentId());
        currentVideo.setVideoPath(videoSubmission.getVideoPath());
        currentVideo.setVideoTitle(videoSubmission.getTitle());
        currentVideo.setVideoDescription(videoSubmission.getDescription());
        currentVideo.setUploadDate(videoSubmission.getSubmissionDate());
        currentVideo.setStatus("submitted");

        // Update UI
        videoTitleText.setText(videoSubmission.getTitle());
        studentNameText.setText("Student: " + videoSubmission.getStudentName());

        Log.d(TAG, "📂 Video path: " + videoSubmission.getVideoPath());
    }

    private void loadRegularVideoData() {
        Log.d(TAG, "🔍 Loading regular video with ID: " + videoId);

        currentVideo = databaseHelper.getVideoById(videoId);
        if (currentVideo == null) {
            Log.e(TAG, "❌ Video not found: " + videoId);
            showError("Video not found");
            finish();
            return;
        }

        Log.d(TAG, "✅ Regular video found: " + currentVideo.getVideoTitle());

        // Update UI
        videoTitleText.setText(currentVideo.getVideoTitle());
        String studentName = databaseHelper.getStudentNameById(currentVideo.getStudentId());
        studentNameText.setText("Student: " + studentName);

        Log.d(TAG, "📂 Video path: " + currentVideo.getVideoPath());
    }

    private void setupVideoPlayer() {
        Log.d(TAG, "🎬 Setting up video player...");

        try {
            // Get video path
            String videoPath = currentVideo.getVideoPath();
            if (videoPath == null || videoPath.isEmpty()) {
                Log.e(TAG, "❌ Video path is null or empty");
                showError("Video path not available");
                finish();
                return;
            }

            // Validate file
            if (!validateVideoFile(videoPath)) {
                Log.e(TAG, "❌ Video file validation failed");
                showError("Invalid video file");
                finish();
                return;
            }

            // Create URI
            File videoFile = new File(videoPath);
            Uri videoUri = Uri.fromFile(videoFile);
            Log.d(TAG, "🎬 Video URI: " + videoUri);

            // Reset VideoView
            resetVideoView();

            // Setup listeners
            setupVideoListeners();

            // Set video URI
            videoView.setVideoURI(videoUri);
            videoView.requestFocus();

            Log.d(TAG, "✅ Video player setup completed");

        } catch (Exception e) {
            Log.e(TAG, "💥 Error setting up video player", e);
            showError("Failed to setup video player: " + e.getMessage());
            finish();
        }
    }

    private boolean validateVideoFile(String videoPath) {
        try {
            File file = new File(videoPath);

            Log.d(TAG, "🔍 Validating video file: " + file.getName());
            Log.d(TAG, "📁 File exists: " + file.exists());
            Log.d(TAG, "📁 File readable: " + file.canRead());
            Log.d(TAG, "📁 File size: " + file.length() + " bytes");

            if (!file.exists()) {
                Log.e(TAG, "❌ File does not exist");
                return false;
            }

            if (!file.canRead()) {
                Log.e(TAG, "❌ File is not readable");
                return false;
            }

            if (file.length() == 0) {
                Log.e(TAG, "❌ File is empty");
                return false;
            }

            // Check file extension
            String fileName = file.getName().toLowerCase();
            String[] validExtensions = {".mp4", ".3gp", ".mov", ".avi", ".mkv"};
            boolean validExtension = false;

            for (String ext : validExtensions) {
                if (fileName.endsWith(ext)) {
                    validExtension = true;
                    break;
                }
            }

            if (!validExtension) {
                Log.w(TAG, "⚠️ Unknown video format: " + fileName);
            }

            Log.d(TAG, "✅ Video file validation passed");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error validating video file", e);
            return false;
        }
    }

    private void resetVideoView() {
        try {
            videoView.stopPlayback();
            videoView.suspend();
            videoView.resume();
            isVideoReady = false;
            isPlaying = false;
        } catch (Exception e) {
            Log.w(TAG, "Warning during VideoView reset", e);
        }
    }

    private void setupVideoListeners() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "✅ Video prepared successfully!");

                isVideoReady = true;
                videoDuration = mp.getDuration();

                // Configure MediaPlayer
                mp.setLooping(false);
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

                // Update UI
                totalTimeText.setText(formatTime(videoDuration));
                videoSeekBar.setMax(videoDuration);
                updatePlayPauseButton();

                Log.d(TAG, "📺 Video duration: " + videoDuration + "ms");
                Log.d(TAG, "📺 Video dimensions: " + mp.getVideoWidth() + "x" + mp.getVideoHeight());

                Toast.makeText(VideoPlayerActivity.this,
                        "Video ready! Duration: " + formatTime(videoDuration),
                        Toast.LENGTH_SHORT).show();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "❌ MediaPlayer Error - What: " + what + ", Extra: " + extra);

                String errorMessage = getErrorMessage(what);

                runOnUiThread(() -> {
                    showError(errorMessage + " (Code: " + what + "-" + extra + ")");
                    retryVideoSetup();
                });

                return true;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "🏁 Video playback completed");
                isPlaying = false;
                updatePlayPauseButton();
                stopTimeUpdates();

                // Reset to beginning
                videoView.seekTo(0);
                currentTimeText.setText("00:00");
                videoSeekBar.setProgress(0);
            }
        });
    }

    private String getErrorMessage(int what) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                return "Unknown video error";
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                return "Media server died";
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                return "Video format not supported";
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                return "Video loading timed out";
            default:
                return "Video playback error";
        }
    }

    private void retryVideoSetup() {
        Log.d(TAG, "🔄 Retrying video setup in 2 seconds...");
        timeHandler.postDelayed(() -> {
            try {
                setupVideoPlayer();
            } catch (Exception e) {
                Log.e(TAG, "❌ Retry failed", e);
                showError("Video setup retry failed");
            }
        }, 2000);
    }

    private void setupClickListeners() {
        Log.d(TAG, "🖱️ Setting up click listeners...");

        backBtn.setOnClickListener(v -> onBackPressed());

        playPauseBtn.setOnClickListener(v -> {
            if (!isVideoReady) {
                Toast.makeText(this, "Video not ready yet", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                if (isPlaying) {
                    pauseVideo();
                } else {
                    startVideo();
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error controlling playback", e);
                showError("Error controlling video playback");
            }
        });

        saveAnnotationsBtn.setOnClickListener(v -> saveAllAnnotations());

        clearAnnotationsBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Clear Annotations")
                    .setMessage("Are you sure you want to clear all annotations?")
                    .setPositiveButton("Yes, Clear", (dialog, which) -> clearAllAnnotations())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        Log.d(TAG, "✅ Click listeners setup completed");
    }

    private void setupSeekBar() {
        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isVideoReady) {
                    try {
                        videoView.seekTo(progress);
                        currentTimeText.setText(formatTime(progress));
                        Log.d(TAG, "🎯 Seeked to: " + formatTime(progress));
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error seeking video", e);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopTimeUpdates();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isPlaying) {
                    startTimeUpdates();
                }
            }
        });
    }

    private void startVideo() {
        if (!isVideoReady) {
            Log.w(TAG, "⚠️ Attempted to start video before ready");
            return;
        }

        try {
            videoView.start();
            isPlaying = true;
            updatePlayPauseButton();
            startTimeUpdates();
            Log.d(TAG, "▶️ Video started");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting video", e);
            showError("Failed to start video");
        }
    }

    private void pauseVideo() {
        if (!isVideoReady) {
            Log.w(TAG, "⚠️ Attempted to pause video before ready");
            return;
        }

        try {
            if (videoView.isPlaying()) {
                videoView.pause();
                isPlaying = false;
                updatePlayPauseButton();
                stopTimeUpdates();
                Log.d(TAG, "⏸️ Video paused");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error pausing video", e);
        }
    }

    private void startTimeUpdates() {
        stopTimeUpdates(); // Stop any existing updates

        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isVideoReady && videoView != null && isPlaying) {
                    try {
                        int currentPosition = videoView.getCurrentPosition();
                        videoSeekBar.setProgress(currentPosition);
                        currentTimeText.setText(formatTime(currentPosition));

                        // Update annotation overlay
                        if (annotationOverlay != null) {
                            annotationOverlay.updateVideoPosition(currentPosition);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error updating time", e);
                    }
                }

                if (isPlaying) {
                    timeHandler.postDelayed(this, 100);
                }
            }
        };
        timeHandler.post(timeUpdateRunnable);
    }

    private void stopTimeUpdates() {
        if (timeUpdateRunnable != null) {
            timeHandler.removeCallbacks(timeUpdateRunnable);
            timeUpdateRunnable = null;
        }
    }

    private void updatePlayPauseButton() {
        if (isPlaying) {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
        } else {
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    private void setupAnnotationSystem() {
        if (annotationOverlay != null) {
            annotationOverlay.setDrawingColor(getResources().getColor(R.color.error_color));
            annotationOverlay.setDrawingWidth(8f);
            annotationOverlay.setParentActivity(this);
            annotationOverlay.setClickable(true);
            annotationOverlay.setFocusable(true);

            Log.d(TAG, "✅ Annotation system setup. Drawing enabled: " + !viewOnly);
        }
    }

    public long getCurrentVideoPosition() {
        if (isVideoReady && videoView != null) {
            return videoView.getCurrentPosition();
        }
        return 0;
    }

    private void loadAnnotations() {
        List<Annotation> existingAnnotations = databaseHelper.getAnnotationsByVideoId(videoId);

        annotations.clear();
        for (Annotation annotation : existingAnnotations) {
            annotations.add(annotation);
            if (annotationOverlay != null) {
                annotationOverlay.addAnnotation(annotation);
            }
        }

        Log.d(TAG, "📝 Loaded " + existingAnnotations.size() + " annotations");
    }

    private void saveAllAnnotations() {
        if (annotationOverlay == null) {
            Toast.makeText(this, "Annotation system not available", Toast.LENGTH_SHORT).show();
            return;
        }

        List<AnnotationOverlay.AnnotationDrawing> currentDrawings = annotationOverlay.getAllAnnotations();

        if (currentDrawings.isEmpty()) {
            Toast.makeText(this, "No annotations to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete existing annotations
        databaseHelper.deleteAnnotationsByVideoId(videoId);

        // Save new annotations
        int savedCount = 0;
        for (AnnotationOverlay.AnnotationDrawing drawing : currentDrawings) {
            Annotation annotation = new Annotation(
                    videoId,
                    coachId,
                    drawing.timestamp,
                    Annotation.TYPE_DRAWING,
                    drawing.pathData,
                    0, 0
            );

            long result = databaseHelper.addAnnotation(annotation);
            if (result != -1) {
                savedCount++;
            }
        }

        if (savedCount > 0) {
            databaseHelper.updateVideoStatus(videoId, "annotated");
            Toast.makeText(this, savedCount + " annotations saved!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "💾 Saved " + savedCount + " annotations");
        } else {
            showError("Failed to save annotations");
        }
    }

    private void clearAllAnnotations() {
        if (annotationOverlay != null) {
            annotationOverlay.clearAnnotations();
            Toast.makeText(this, "Annotations cleared", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "🧹 Annotations cleared");
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void showError(String message) {
        Log.e(TAG, "🚨 Error: " + message);

        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            new AlertDialog.Builder(this)
                    .setTitle("Video Error")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Retry", (dialog, which) -> setupVideoPlayer())
                    .show();
        });
    }

    @Override
    public void onBackPressed() {
        if (!viewOnly && annotationOverlay != null && !annotationOverlay.getAllAnnotations().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Unsaved Annotations")
                    .setMessage("You have unsaved annotations. Save them before leaving?")
                    .setPositiveButton("Save & Exit", (dialog, which) -> {
                        saveAllAnnotations();
                        timeHandler.postDelayed(() -> {
                            setResult(RESULT_OK);
                            finish();
                        }, 100);
                    })
                    .setNegativeButton("Exit Without Saving", (dialog, which) -> {
                        super.onBackPressed();
                        finish();
                    })
                    .setNeutralButton("Cancel", null)
                    .setCancelable(false)
                    .show();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isVideoReady && isPlaying) {
            startVideo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isVideoReady && isPlaying) {
            pauseVideo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clean up video resources
        if (videoView != null) {
            videoView.stopPlayback();
        }

        // Stop time updates
        stopTimeUpdates();

        // Close database
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        Log.d(TAG, "🧹 VideoPlayerActivity destroyed and cleaned up");
    }
}