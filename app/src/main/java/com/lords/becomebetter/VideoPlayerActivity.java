package com.lords.becomebetter;

import android.app.AlertDialog;
import java.io.File;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.List;

/**
 * Enhanced VideoPlayerActivity with rebuilt annotation system
 */
public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayerActivity";

    // Intent extras
    public static final String EXTRA_VIDEO_ID = "video_id";
    public static final String EXTRA_COACH_ID = "coach_id";
    public static final String EXTRA_VIEW_ONLY = "view_only";

    // UI Components
    private VideoView videoView;
    private AnnotationOverlay annotationOverlay;
    private SeekBar seekBar;
    private TextView timeDisplay;
    private TextView titleText;
    private TextView studentText;

    // Video controls
    private ImageButton playPauseButton;
    private ImageButton backwardButton;
    private ImageButton forwardButton;
    private Button saveButton;

    // Drawing tools panel
    private LinearLayout drawingToolsPanel;
    private ImageButton penTool;
    private ImageButton highlighterTool;
    private ImageButton arrowTool;
    private ImageButton circleTool;
    private ImageButton rectangleTool;
    private ImageButton colorPicker;
    private ImageButton undoButton;
    private ImageButton redoButton;
    private ImageButton clearButton;

    // Cricket-specific tools
    private LinearLayout cricketToolsPanel;
    private ImageButton fieldLayoutTool;
    private ImageButton playerMarkerTool;
    private ImageButton ballPathTool;
    private ImageButton notesTool;

    // Data
    private DatabaseHelper databaseHelper;
    private int videoId;
    private int coachId;
    private boolean viewOnly = false;
    private boolean isVideoReady = false;
    private boolean isPlaying = false;

    // Video timing
    private Handler updateHandler;
    private Runnable updateRunnable;
    private static final int UPDATE_INTERVAL = 100; // 100ms updates

    // Annotation management
    private boolean annotationMode = false;
    private int currentColor = android.graphics.Color.GREEN;
    private float currentStrokeWidth = 8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Log.d(TAG, "🎬 Creating VideoPlayerActivity with new annotation system");

        // Initialize data
        initializeData();

        // Initialize UI
        initializeViews();

        // Setup video player
        setupVideoPlayer();

        // Setup annotation overlay
        setupAnnotationOverlay();

        // Setup controls
        setupVideoControls();
        setupDrawingTools();
        setupCricketTools();

        // Load video and annotations
        loadVideoAndAnnotations();

        // Start update timer
        startUpdateTimer();
    }

    /**
     * Initialize data from intent and database
     */
    private void initializeData() {
        databaseHelper = new DatabaseHelper(this);

        // Get intent data
        videoId = getIntent().getIntExtra(EXTRA_VIDEO_ID, -1);
        coachId = getIntent().getIntExtra(EXTRA_COACH_ID, -1);
        viewOnly = getIntent().getBooleanExtra(EXTRA_VIEW_ONLY, false);

        Log.d(TAG, "📊 Video ID: " + videoId + ", Coach ID: " + coachId + ", View Only: " + viewOnly);

        if (videoId == -1) {
            Toast.makeText(this, "Error: Invalid video ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    /**
     * Initialize all UI views
     */
    private void initializeViews() {
        // Video components
        videoView = findViewById(R.id.videoView);
        annotationOverlay = findViewById(R.id.annotationOverlay);

        // Video info
        titleText = findViewById(R.id.titleText);
        studentText = findViewById(R.id.studentText);

        // Video controls
        playPauseButton = findViewById(R.id.playPauseButton);
        backwardButton = findViewById(R.id.backwardButton);
        forwardButton = findViewById(R.id.forwardButton);
        seekBar = findViewById(R.id.seekBar);
        timeDisplay = findViewById(R.id.timeDisplay);
        saveButton = findViewById(R.id.saveButton);

        // Drawing tools
        drawingToolsPanel = findViewById(R.id.drawingToolsPanel);
        penTool = findViewById(R.id.penTool);
        highlighterTool = findViewById(R.id.highlighterTool);
        arrowTool = findViewById(R.id.arrowTool);
        circleTool = findViewById(R.id.circleTool);
        rectangleTool = findViewById(R.id.rectangleTool);
        colorPicker = findViewById(R.id.colorPicker);
        undoButton = findViewById(R.id.undoButton);
        redoButton = findViewById(R.id.redoButton);
        clearButton = findViewById(R.id.clearButton);

        // Cricket tools
        cricketToolsPanel = findViewById(R.id.cricketToolsPanel);
        fieldLayoutTool = findViewById(R.id.fieldLayoutTool);
        playerMarkerTool = findViewById(R.id.playerMarkerTool);
        ballPathTool = findViewById(R.id.ballPathTool);
        notesTool = findViewById(R.id.notesTool);

        // Configure view-only mode
        if (viewOnly) {
            drawingToolsPanel.setVisibility(View.GONE);
            cricketToolsPanel.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
        }
    }

    /**
     * Setup video player
     */
//    private void setupVideoPlayer() {
//        videoView.setOnPreparedListener(mediaPlayer -> {
//            isVideoReady = true;
//
//            // Configure media player
//            mediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> {
//                Log.d(TAG, "📹 Video size: " + width + "x" + height);
//            });
//
//            // Setup seek bar
//            seekBar.setMax(videoView.getDuration());
//            updateTimeDisplay();
//
//            Log.d(TAG, "✅ Video prepared and ready");
//        });
//
//        videoView.setOnCompletionListener(mediaPlayer -> {
//            isPlaying = false;
//            updatePlayPauseButton();
//            seekBar.setProgress(seekBar.getMax());
//            Log.d(TAG, "🏁 Video completed");
//        });
//
//        videoView.setOnErrorListener((mediaPlayer, what, extra) -> {
//            Log.e(TAG, "❌ Video error: what=" + what + ", extra=" + extra);
//            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
//            return true;
//        });
//    }

    /**
     * Setup annotation overlay
     */
    private void setupAnnotationOverlay() {
        annotationOverlay.setParentActivity(this);
        annotationOverlay.setDrawingEnabled(!viewOnly);

        // Set default cricket green color
        annotationOverlay.setActiveColor(ContextCompat.getColor(this, R.color.cricket_green_primary));

        Log.d(TAG, "🎨 Annotation overlay configured");
    }

    /**
     * Setup video control buttons
     */
    private void setupVideoControls() {
        playPauseButton.setOnClickListener(v -> togglePlayPause());

        backwardButton.setOnClickListener(v -> {
            if (isVideoReady) {
                int newPosition = Math.max(0, videoView.getCurrentPosition() - 10000); // 10 seconds back
                videoView.seekTo(newPosition);
                seekBar.setProgress(newPosition);
                updateAnnotationOverlay();
            }
        });

        forwardButton.setOnClickListener(v -> {
            if (isVideoReady) {
                int newPosition = Math.min(videoView.getDuration(),
                        videoView.getCurrentPosition() + 10000); // 10 seconds forward
                videoView.seekTo(newPosition);
                seekBar.setProgress(newPosition);
                updateAnnotationOverlay();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isVideoReady) {
                    videoView.seekTo(progress);
                    updateAnnotationOverlay();
                    updateTimeDisplay();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause updates while user is seeking
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume updates
            }
        });

        saveButton.setOnClickListener(v -> saveAnnotations());
    }

    /**
     * Setup drawing tool buttons
     */
    private void setupDrawingTools() {
        if (viewOnly) return;

        penTool.setOnClickListener(v -> {
            annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.PEN);
            highlightActiveTool(penTool);
        });

        highlighterTool.setOnClickListener(v -> {
            annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.HIGHLIGHTER);
            highlightActiveTool(highlighterTool);
        });

        arrowTool.setOnClickListener(v -> {
            annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.ARROW);
            highlightActiveTool(arrowTool);
        });

        circleTool.setOnClickListener(v -> {
            annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.CIRCLE);
            highlightActiveTool(circleTool);
        });

        rectangleTool.setOnClickListener(v -> {
            annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.RECTANGLE);
            highlightActiveTool(rectangleTool);
        });

        colorPicker.setOnClickListener(v -> showColorPicker());

        undoButton.setOnClickListener(v -> {
            annotationOverlay.undo();
            updateUndoRedoButtons();
        });

        redoButton.setOnClickListener(v -> {
            annotationOverlay.redo();
            updateUndoRedoButtons();
        });

        clearButton.setOnClickListener(v -> showClearConfirmation());

        // Set pen as default tool
        highlightActiveTool(penTool);
    }

    /**
     * Setup cricket-specific tool buttons
     */
    private void setupCricketTools() {
        if (viewOnly) return;

        fieldLayoutTool.setOnClickListener(v -> showFieldLayoutOptions());
        playerMarkerTool.setOnClickListener(v -> enablePlayerMarkerMode());
        ballPathTool.setOnClickListener(v -> enableBallPathMode());
        notesTool.setOnClickListener(v -> showNotesDialog());
    }

    private void setupVideoPlayer() {
        if (videoView == null) return;

        videoView.setOnPreparedListener(mediaPlayer -> {
            Log.d(TAG, "✅ Video prepared successfully");
            isVideoReady = true;
            if (seekBar != null) seekBar.setMax(videoView.getDuration());
            updateTimeDisplay();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "❌ Video error: what=" + what + ", extra=" + extra);
            Toast.makeText(this, "Video playback error: " + what, Toast.LENGTH_LONG).show();
            return true;
        });

        videoView.setOnCompletionListener(mp -> {
            Log.d(TAG, "🏁 Video completed");
            isPlaying = false;
            updatePlayPauseButton();
        });
    }

    /**
     * Load video and existing annotations
     */
    private void loadVideoAndAnnotations() {
        Log.d(TAG, "🔍 Loading video with ID: " + videoId);

        try {
            Video video = databaseHelper.getVideoById(videoId);
            if (video == null) {
                Log.e(TAG, "❌ Video not found for ID: " + videoId);
                Toast.makeText(this, "Video not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "✅ Video found: " + video.getVideoTitle());

            // Set video info
            if (titleText != null) {
                titleText.setText(video.getVideoTitle());
            }

            // Get student info
            Student student = databaseHelper.getStudentById(video.getStudentId());
            if (student != null && studentText != null) {
                studentText.setText(student.getName());
            }

            // CRITICAL: Load video file properly
            String videoPath = video.getVideoPath();
            Log.d(TAG, "📁 Video path: " + videoPath);

            if (videoPath != null && !videoPath.isEmpty() && videoView != null) {
                File videoFile = new File(videoPath);
                Log.d(TAG, "📂 Video file exists: " + videoFile.exists() + ", Size: " + videoFile.length());

                if (videoFile.exists() && videoFile.length() > 0) {
                    Uri videoUri = Uri.fromFile(videoFile);
                    Log.d(TAG, "🎬 Setting video URI: " + videoUri);

                    videoView.setVideoURI(videoUri);
                    videoView.requestFocus();

                    // Start video after a short delay
                    videoView.postDelayed(() -> {
                        if (videoView != null) {
                            videoView.start();
                            Log.d(TAG, "▶️ Video started");
                        }
                    }, 500);

                } else {
                    Log.e(TAG, "❌ Video file invalid: exists=" + videoFile.exists() + ", size=" + videoFile.length());
                    Toast.makeText(this, "Video file not found or empty", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "❌ Invalid video path or VideoView is null");
                Toast.makeText(this, "Invalid video path", Toast.LENGTH_SHORT).show();
            }

            // Load annotations
            if (annotationOverlay != null) {
                List<Annotation> annotations = databaseHelper.getAnnotationsByVideoId(videoId);
                annotationOverlay.loadVideoAnnotations(annotations);
                Log.d(TAG, "📝 Loaded " + annotations.size() + " annotations");
            }

        } catch (Exception e) {
            Log.e(TAG, "💥 Error loading video", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start the update timer for video progress
     */
    private void startUpdateTimer() {
        updateHandler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isVideoReady && isPlaying) {
                    updateVideoProgress();
                }
                updateHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
        updateHandler.post(updateRunnable);
    }

    /**
     * Update video progress and annotation overlay
     */
    private void updateVideoProgress() {
        if (isVideoReady) {
            int currentPosition = videoView.getCurrentPosition();
            seekBar.setProgress(currentPosition);
            updateTimeDisplay();
            updateAnnotationOverlay();
            updateUndoRedoButtons();
        }
    }

    /**
     * Update annotation overlay with current video position
     */
    private void updateAnnotationOverlay() {
        if (annotationOverlay != null && isVideoReady) {
            annotationOverlay.updateVideoTimestamp(videoView.getCurrentPosition());
        }
    }

    /**
     * Update time display
     */
    private void updateTimeDisplay() {
        if (isVideoReady) {
            int current = videoView.getCurrentPosition();
            int duration = videoView.getDuration();
            timeDisplay.setText(formatTime(current) + " / " + formatTime(duration));
        }
    }

    /**
     * Format time in mm:ss format
     */
    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Toggle play/pause
     */
    private void togglePlayPause() {
        if (!isVideoReady) return;

        if (isPlaying) {
            videoView.pause();
            isPlaying = false;
        } else {
            videoView.start();
            isPlaying = true;
        }

        updatePlayPauseButton();
    }

    /**
     * Update play/pause button icon
     */
    private void updatePlayPauseButton() {
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.ic_pause);
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play);
        }
    }

    /**
     * Pause video (called by annotation overlay)
     */
    public void pauseVideo() {
        if (isVideoReady && isPlaying) {
            videoView.pause();
            isPlaying = false;
            updatePlayPauseButton();
        }
    }

    /**
     * Get current video position
     */
    public long getCurrentVideoPosition() {
        if (isVideoReady) {
            return videoView.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Highlight active drawing tool
     */
    private void highlightActiveTool(ImageButton activeTool) {
        // Reset all tools
        penTool.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        highlighterTool.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        arrowTool.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        circleTool.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        rectangleTool.setBackgroundColor(android.graphics.Color.TRANSPARENT);

        // Highlight active tool
        activeTool.setBackgroundColor(ContextCompat.getColor(this, R.color.cricket_green_primary));
    }

    /**
     * Show color picker dialog
     */
    private void showColorPicker() {
        String[] colors = {"Green", "White", "Red", "Yellow", "Blue", "Black"};
        int[] colorValues = {
                ContextCompat.getColor(this, R.color.cricket_green_primary),
                android.graphics.Color.WHITE,
                android.graphics.Color.RED,
                android.graphics.Color.YELLOW,
                android.graphics.Color.BLUE,
                android.graphics.Color.BLACK
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Color")
                .setItems(colors, (dialog, which) -> {
                    currentColor = colorValues[which];
                    annotationOverlay.setActiveColor(currentColor);
                    colorPicker.setColorFilter(currentColor);
                });
        builder.create().show();
    }

    /**
     * Update undo/redo button states
     */
    private void updateUndoRedoButtons() {
        if (undoButton != null && redoButton != null) {
            undoButton.setEnabled(annotationOverlay.canUndo());
            redoButton.setEnabled(annotationOverlay.canRedo());
        }
    }

    /**
     * Show clear confirmation dialog
     */
    private void showClearConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Annotations")
                .setMessage("Are you sure you want to clear all annotations from this session?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    annotationOverlay.clearCurrentSession();
                    updateUndoRedoButtons();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Save annotations to database
     */
    private void saveAnnotations() {
        List<AnnotationOverlay.DrawnAnnotation> currentAnnotations =
                annotationOverlay.getCurrentSessionAnnotations();

        if (currentAnnotations.isEmpty()) {
            Toast.makeText(this, "No annotations to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert to database format
        List<Annotation> dbAnnotations = annotationOverlay.convertToDbAnnotations(videoId, coachId);

        // Delete existing annotations for this video
        databaseHelper.deleteAnnotationsByVideoId(videoId);

        // Save new annotations
        int savedCount = 0;
        for (Annotation annotation : dbAnnotations) {
            long result = databaseHelper.addAnnotation(annotation);
            if (result != -1) {
                savedCount++;
            }
        }

        if (savedCount > 0) {
            // Update video status
            databaseHelper.updateVideoStatus(videoId, "annotated");

            Toast.makeText(this, savedCount + " annotations saved successfully!",
                    Toast.LENGTH_SHORT).show();

            Log.d(TAG, "💾 Saved " + savedCount + " annotations");
        } else {
            Toast.makeText(this, "Error saving annotations", Toast.LENGTH_SHORT).show();
        }
    }

    // Cricket-specific tool methods (placeholder implementations)
    private void showFieldLayoutOptions() {
        Toast.makeText(this, "🏏 Field layout tool - Coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void enablePlayerMarkerMode() {
        Toast.makeText(this, "👤 Player marker mode - Coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void enableBallPathMode() {
        Toast.makeText(this, "⚾ Ball path mode - Coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void showNotesDialog() {
        Toast.makeText(this, "💬 Notes dialog - Coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop update timer
        if (updateHandler != null && updateRunnable != null) {
            updateHandler.removeCallbacks(updateRunnable);
        }

        // Close database
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        Log.d(TAG, "🏁 VideoPlayerActivity destroyed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isVideoReady && isPlaying) {
            videoView.pause();
            isPlaying = false;
            updatePlayPauseButton();
        }
    }
}