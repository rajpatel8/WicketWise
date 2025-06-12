package com.lords.becomebetter;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
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
    private boolean isDrawing = false;
    private Path currentPath = new Path();
    private Paint drawPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        databaseHelper = new DatabaseHelper(this);

        // Get intent data
        videoId = getIntent().getIntExtra("videoId", 0);
        coachEmail = getIntent().getStringExtra("coachEmail");
        viewOnly = getIntent().getBooleanExtra("viewOnly", false);

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
        }
    }

    private void loadVideoData() {
        currentVideo = databaseHelper.getVideoById(videoId);
        if (currentVideo == null) {
            showError("Video not found");
            finish();
            return;
        }

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
    }

    private void setupVideoPlayer() {
        File videoFile = new File(currentVideo.getVideoPath());
        if (!videoFile.exists()) {
            showError("Video file not found");
            finish();
            return;
        }

        Uri videoUri = Uri.fromFile(videoFile);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {
            isVideoReady = true;
            int duration = mp.getDuration();

            // Set up seek bar
            videoSeekBar.setMax(duration);
            totalTimeText.setText(formatTime(duration));

            // Start time updates
            startTimeUpdates();

            // Auto-pause initially
            mp.pause();
            isPlaying = false;
            updatePlayPauseButton();
        });

        videoView.setOnCompletionListener(mp -> {
            isPlaying = false;
            updatePlayPauseButton();
            stopTimeUpdates();
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
    }

    private void setupAnnotationSystem() {
        // Initialize paint for drawing
        drawPaint = new Paint();
        drawPaint.setColor(Color.RED);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(8f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        // Set up annotation overlay
        annotationOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (viewOnly) return false;

                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDrawing = true;
                        currentPath.moveTo(x, y);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (isDrawing) {
                            currentPath.lineTo(x, y);
                            annotationOverlay.invalidate();
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (isDrawing) {
                            isDrawing = false;
                            // Save the current drawing as an annotation
                            saveCurrentDrawing();
                            currentPath.reset();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());

        playPauseBtn.setOnClickListener(v -> {
            if (isVideoReady) {
                if (isPlaying) {
                    videoView.pause();
                    isPlaying = false;
                } else {
                    videoView.start();
                    isPlaying = true;
                }
                updatePlayPauseButton();
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
    }

    private void startTimeUpdates() {
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isVideoReady && videoView != null) {
                    int currentPosition = videoView.getCurrentPosition();
                    videoSeekBar.setProgress(currentPosition);
                    currentTimeText.setText(formatTime(currentPosition));
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

    private void saveCurrentDrawing() {
        if (currentPath.isEmpty()) return;

        long currentTime = videoView.getCurrentPosition();

        // Convert path to string data (simplified - you might want to use a more sophisticated format)
        String pathData = pathToString(currentPath);

        // Create annotation
        Annotation annotation = new Annotation(
                videoId,
                coachId,
                currentTime,
                Annotation.TYPE_DRAWING,
                pathData,
                0, 0 // x, y not used for drawing annotations
        );

        annotations.add(annotation);

        // Add to overlay for display
        annotationOverlay.addAnnotation(annotation);

        Toast.makeText(this, "Annotation added at " + formatTime((int)currentTime), Toast.LENGTH_SHORT).show();
    }

    private void saveAllAnnotations() {
        // Delete existing annotations for this video
        // (You'll need to add this method to DatabaseHelper)

        // Save all current annotations
        for (Annotation annotation : annotations) {
            long result = databaseHelper.addAnnotation(annotation);
            if (result == -1) {
                showError("Failed to save some annotations");
                return;
            }
        }

        // Update video status
        databaseHelper.updateVideoStatus(videoId, "annotated");

        Toast.makeText(this, "All annotations saved successfully!", Toast.LENGTH_LONG).show();

        setResult(RESULT_OK);
        finish();
    }

    private void clearAllAnnotations() {
        annotations.clear();
        annotationOverlay.clearAnnotations();
        currentPath.reset();
        annotationOverlay.invalidate();

        Toast.makeText(this, "Annotations cleared", Toast.LENGTH_SHORT).show();
    }

    private void loadAnnotations() {
        // Load existing annotations from database
        // (You'll need to add this method to DatabaseHelper)
        List<Annotation> existingAnnotations = databaseHelper.getAnnotationsByVideoId(videoId);

        for (Annotation annotation : existingAnnotations) {
            annotations.add(annotation);
            annotationOverlay.addAnnotation(annotation);
        }
    }

    private String pathToString(Path path) {
        // Simple implementation - you might want to use a more sophisticated format
        // For now, just return a placeholder
        return "drawing_path_" + System.currentTimeMillis();
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimeUpdates();
    }

    @Override
    public void onBackPressed() {
        if (!viewOnly && !annotations.isEmpty()) {
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