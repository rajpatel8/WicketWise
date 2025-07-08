package com.lords.becomebetter;

import android.app.AlertDialog;
import java.io.File;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.List;

// ADD THESE MISSING IMPORTS:
import android.widget.EditText;
import android.widget.RatingBar;
import android.graphics.PointF;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private ImageButton frameBackwardButton;
    private ImageButton frameForwardButton;
    private static final int FRAME_DURATION_MS = 33;



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
        setupDrawingToolsWithScroll() ;
        setupCricketTools();

        // Load video and annotations
        loadVideoAndAnnotations();

        // Start update timer
//        startUpdateTimer();

        startVideoStateMonitor();
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

    private void highlightActiveToolWithScroll(ImageButton activeTool) {
        // Reset all drawing tools to normal state
        ImageButton[] drawingTools = {penTool, highlighterTool, arrowTool, circleTool, rectangleTool, colorPicker, undoButton, redoButton, clearButton};

        for (ImageButton tool : drawingTools) {
            if (tool != null) {
                tool.setSelected(false);
                tool.setAlpha(0.7f);
            }
        }

        // Highlight the active tool
        if (activeTool != null) {
            activeTool.setSelected(true);
            activeTool.setAlpha(1.0f);

            // Add animation
            activeTool.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        activeTool.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();
                    })
                    .start();

            // AUTO-SCROLL to the active tool
            scrollToActiveTool(activeTool);
        }
    }

    private void setupDrawingToolsWithScroll() {
        if (viewOnly) return;

        // Initialize scrollable toolbar
        initializeScrollableToolbar();

        // Set click listeners with auto-scroll
        penTool.setOnClickListener(v -> {
            annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.PEN);
            highlightActiveToolWithScroll(penTool);
            Log.d(TAG, "🖊️ Pen tool selected");
        });

        highlighterTool.setOnClickListener(v -> {
            annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.HIGHLIGHTER);
            highlightActiveToolWithScroll(highlighterTool);
            Log.d(TAG, "🖍️ Highlighter tool selected");
        });

        arrowTool.setOnClickListener(v -> {
            annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.ARROW);
            highlightActiveToolWithScroll(arrowTool);
            Log.d(TAG, "➡️ Arrow tool selected");
        });

        circleTool.setOnClickListener(v -> {
            annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.CIRCLE);
            highlightActiveToolWithScroll(circleTool);
            Log.d(TAG, "⭕ Circle tool selected");
        });

        rectangleTool.setOnClickListener(v -> {
            annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.RECTANGLE);
            highlightActiveToolWithScroll(rectangleTool);
            Log.d(TAG, "⬛ Rectangle tool selected");
        });

        colorPicker.setOnClickListener(v -> {
            showColorPicker();
            highlightActiveToolWithScroll(colorPicker);
            Log.d(TAG, "🎨 Color picker opened");
        });

        undoButton.setOnClickListener(v -> {
            annotationOverlay.undo();
            updateUndoRedoButtons();
            highlightActiveToolWithScroll(undoButton);
            Log.d(TAG, "↶ Undo action");
        });

        redoButton.setOnClickListener(v -> {
            annotationOverlay.redo();
            updateUndoRedoButtons();
            highlightActiveToolWithScroll(redoButton);
            Log.d(TAG, "↷ Redo action");
        });

        clearButton.setOnClickListener(v -> {
            showClearConfirmation();
            highlightActiveToolWithScroll(clearButton);
            Log.d(TAG, "🗑️ Clear confirmation dialog");
        });

        // Set pen as default tool and scroll to it
        highlightActiveToolWithScroll(penTool);
        annotationOverlay.setActiveTool(AnnotationOverlay.DrawingTool.PEN);
    }

    private void scrollToActiveTool(ImageButton activeTool) {
        HorizontalScrollView scrollView = findViewById(R.id.drawingToolsScrollView);

        if (scrollView != null && activeTool != null) {
            scrollView.post(() -> {
                int toolX = activeTool.getLeft();
                int toolWidth = activeTool.getWidth();
                int scrollViewWidth = scrollView.getWidth();

                // Calculate scroll position to center the tool
                int scrollX = toolX - (scrollViewWidth / 2) + (toolWidth / 2);

                // Ensure scroll position is within bounds
                scrollX = Math.max(0, scrollX);

                // Smooth scroll to the tool
                scrollView.smoothScrollTo(scrollX, 0);
            });
        }
    }


    private void initializeScrollableToolbar() {
        HorizontalScrollView scrollView = findViewById(R.id.drawingToolsScrollView);

        if (scrollView != null) {
            // Enable smooth scrolling
            scrollView.setSmoothScrollingEnabled(true);

            // Enable scroll bar fading
            scrollView.setScrollBarFadeDuration(3000);
            scrollView.setScrollbarFadingEnabled(true);

            Log.d(TAG, "📜 Scrollable toolbar initialized");
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

        frameBackwardButton = findViewById(R.id.frameBackwardButton);
        frameForwardButton = findViewById(R.id.frameForwardButton);

        saveButton = findViewById(R.id.saveButton);

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
        playPauseButton.setOnClickListener(v -> {
            Log.d(TAG, "🎯 Play button clicked - Current state: isPlaying = " + isPlaying);
            togglePlayPause();
        });
        // Existing 10-second controls
        backwardButton.setOnClickListener(v -> {
            if (isVideoReady) {
                int newPosition = Math.max(0, videoView.getCurrentPosition() - 10000);
                videoView.seekTo(newPosition);
                seekBar.setProgress(newPosition);
                updateAnnotationOverlay();
                Log.d(TAG, "⏪ 10s backward");
            }
        });

        forwardButton.setOnClickListener(v -> {
            if (isVideoReady) {
                int newPosition = Math.min(videoView.getDuration(),
                        videoView.getCurrentPosition() + 10000);
                videoView.seekTo(newPosition);
                seekBar.setProgress(newPosition);
                updateAnnotationOverlay();
                Log.d(TAG, "⏩ 10s forward");
            }
        });

        if (frameBackwardButton != null) {
            frameBackwardButton.setOnClickListener(v -> {
                Log.d(TAG, "🎯 Frame backward clicked");
                if (isVideoReady) {
                    seekOneFrameBackward();
                }
            });
        }

        if (frameForwardButton != null) {
            frameForwardButton.setOnClickListener(v -> {
                Log.d(TAG, "🎯 Frame forward clicked");
                if (isVideoReady) {
                    seekOneFrameForward();
                }
            });
        }

        // Seek bar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isVideoReady) {
                    videoView.seekTo(progress);
                    updateAnnotationOverlay();
                    updateTimeDisplay();
                    Log.d(TAG, "🎯 Manual seek to: " + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        saveButton.setOnClickListener(v -> saveAnnotations());
    }


    /**
     * Setup drawing tool buttons
     */

    private void seekOneFrameBackward() {
        if (!isVideoReady) {
            Log.w(TAG, "Video not ready for frame control");
            return;
        }

        // Pause video for precise control
        if (isPlaying) {
            videoView.pause();
            isPlaying = false;
            updatePlayPauseButton(); // IMPORTANT: Update button state

            // Stop update timer
            if (updateHandler != null && updateRunnable != null) {
                updateHandler.removeCallbacks(updateRunnable);
            }
        }

        int currentPos = videoView.getCurrentPosition();
        int newPosition = Math.max(0, currentPos - FRAME_DURATION_MS);

        videoView.seekTo(newPosition);
        seekBar.setProgress(newPosition);
        updateAnnotationOverlay();
        updateTimeDisplay();

        Log.d(TAG, "⏪ Frame backward: " + currentPos + " -> " + newPosition);
    }

    /**
     * Seek one frame forward
     */
    private void seekOneFrameForward() {
        if (!isVideoReady) {
            Log.w(TAG, "Video not ready for frame control");
            return;
        }

        // Pause video for precise control
        if (isPlaying) {
            videoView.pause();
            isPlaying = false;
            updatePlayPauseButton(); // IMPORTANT: Update button state

            // Stop update timer
            if (updateHandler != null && updateRunnable != null) {
                updateHandler.removeCallbacks(updateRunnable);
            }
        }

        int currentPos = videoView.getCurrentPosition();
        int duration = videoView.getDuration();
        int newPosition = Math.min(duration, currentPos + FRAME_DURATION_MS);

        videoView.seekTo(newPosition);
        seekBar.setProgress(newPosition);
        updateAnnotationOverlay();
        updateTimeDisplay();

        Log.d(TAG, "⏩ Frame forward: " + currentPos + " -> " + newPosition);
    }


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

            // Setup seek bar with video duration
            if (seekBar != null) {
                seekBar.setMax(videoView.getDuration());
                Log.d(TAG, "📊 Seek bar max set to: " + videoView.getDuration());
            }

            updateTimeDisplay();

            // IMPORTANT: Prepare the timer
            if (updateHandler == null) {
                updateHandler = new Handler();
            }

            updateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isVideoReady && isPlaying) {
                        updateVideoProgress();
                        Log.d(TAG, "🔄 Progress updated: " + videoView.getCurrentPosition());
                    } else {
                        Log.d(TAG, "⏸️ Timer tick skipped - isVideoReady: " + isVideoReady + ", isPlaying: " + isPlaying);
                    }

                    // Schedule next update
                    if (updateHandler != null) {
                        updateHandler.postDelayed(this, UPDATE_INTERVAL);
                    }
                }
            };

            // DON'T auto-start the video - wait for user to click play
            Log.d(TAG, "⏰ Update timer prepared (waiting for user to click play)");
        });

        // ADD: Listen for when video actually starts playing
        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                Log.d(TAG, "🎬 Video rendering started - checking play state");

                // If video started but we don't know about it, update our state
                if (videoView.isPlaying() && !isPlaying) {
                    isPlaying = true;
                    updatePlayPauseButton();

                    // Start the timer if it's not running
                    if (updateHandler != null && updateRunnable != null) {
                        updateHandler.removeCallbacks(updateRunnable);
                        updateHandler.post(updateRunnable);
                        Log.d(TAG, "⏰ Timer started due to auto-play detection");
                    }
                }
            }
            return false;
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

            // Stop update timer when video completes
            if (updateHandler != null && updateRunnable != null) {
                updateHandler.removeCallbacks(updateRunnable);
            }
        });
    }

    private void startVideoStateMonitor() {
        Handler stateHandler = new Handler();
        Runnable stateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isVideoReady && videoView != null) {
                    boolean videoIsPlaying = videoView.isPlaying();

                    if (videoIsPlaying != isPlaying) {
                        Log.d(TAG, "🔄 Video state mismatch detected - videoView.isPlaying(): " + videoIsPlaying + ", our isPlaying: " + isPlaying);

                        // Sync our state with actual video state
                        isPlaying = videoIsPlaying;
                        updatePlayPauseButton();

                        if (isPlaying) {
                            // Start timer if video is playing
                            if (updateHandler != null && updateRunnable != null) {
                                updateHandler.removeCallbacks(updateRunnable);
                                updateHandler.post(updateRunnable);
                                Log.d(TAG, "⏰ Timer started due to state sync");
                            }
                        } else {
                            // Stop timer if video is paused
                            if (updateHandler != null && updateRunnable != null) {
                                updateHandler.removeCallbacks(updateRunnable);
                                Log.d(TAG, "⏰ Timer stopped due to state sync");
                            }
                        }
                    }
                }

                // Check again in 1 second
                stateHandler.postDelayed(this, 1000);
            }
        };

        // Start monitoring after a short delay
        stateHandler.postDelayed(stateRunnable, 2000);
        Log.d(TAG, "🔍 Started video state monitoring");
    }

    /**
     * Load video and existing annotations
     */
    private void loadVideoAndAnnotations() {
        Log.d(TAG, "🔍 Loading video with ID: " + videoId);

        try {
            // FIXED: Use getVideoSubmissionById instead of getVideoById
            VideoSubmission videoSubmission = databaseHelper.getVideoSubmissionById(videoId);
            if (videoSubmission == null) {
                Log.e(TAG, "❌ Video submission not found for ID: " + videoId);
                Toast.makeText(this, "Video submission not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "✅ Video submission found: " + videoSubmission.getTitle());

            // Set video info
            if (titleText != null) {
                titleText.setText(videoSubmission.getTitle());
            }

            // Get student info
            Student student = databaseHelper.getStudentById(videoSubmission.getStudentId());
            if (student != null && studentText != null) {
                studentText.setText(student.getName());
            }

            // CRITICAL: Load video file properly
            String videoPath = videoSubmission.getVideoPath();
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

            // Load annotations - FIXED: Use submission ID for annotations
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
        if (updateHandler == null) {
            updateHandler = new Handler();
        }

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "⏰ Timer tick - isVideoReady: " + isVideoReady + ", isPlaying: " + isPlaying);

                if (isVideoReady && isPlaying) {
                    updateVideoProgress();
                    Log.d(TAG, "🔄 Progress updated: " + videoView.getCurrentPosition());
                } else {
                    Log.d(TAG, "⏸️ Timer tick skipped - not ready or not playing");
                }

                // Schedule next update
                if (updateHandler != null) {
                    updateHandler.postDelayed(this, UPDATE_INTERVAL);
                }
            }
        };

        // Start the timer
        updateHandler.post(updateRunnable);
        Log.d(TAG, "⏰ Update timer started with initial state - isVideoReady: " + isVideoReady + ", isPlaying: " + isPlaying);
    }


    /**
     * Update video progress and annotation overlay
     */
    private void updateVideoProgress() {
        if (isVideoReady && seekBar != null && timeDisplay != null) {
            int currentPosition = videoView.getCurrentPosition();

            // Update seek bar progress
            seekBar.setProgress(currentPosition);

            // Update time display
            updateTimeDisplay();

            // Update annotation overlay
            updateAnnotationOverlay();

            // Update undo/redo buttons
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
        if (isVideoReady && timeDisplay != null) {
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
        if (!isVideoReady) {
            Log.w(TAG, "Video not ready");
            return;
        }

        if (isPlaying) {
            videoView.pause();
            isPlaying = false;
            Log.d(TAG, "⏸️ Video paused, isPlaying = " + isPlaying);

            // Stop update timer when paused
            if (updateHandler != null && updateRunnable != null) {
                updateHandler.removeCallbacks(updateRunnable);
                Log.d(TAG, "⏰ Timer stopped on pause");
            }
        } else {
            videoView.start();
            isPlaying = true;  // CRITICAL: Set this BEFORE starting timer
            Log.d(TAG, "▶️ Video started, isPlaying = " + isPlaying);

            // CRITICAL: Restart timer immediately when playing starts
            if (updateHandler != null && updateRunnable != null) {
                updateHandler.removeCallbacks(updateRunnable);
                updateHandler.post(updateRunnable);
                Log.d(TAG, "⏰ Timer restarted on play");
            }
        }

        updatePlayPauseButton();
    }

    /**
     * Update play/pause button icon
     */
    private void updatePlayPauseButton() {
        if (playPauseButton != null) {
            if (isPlaying) {
                playPauseButton.setImageResource(R.drawable.ic_pause);
                Log.d(TAG, "🔄 Button updated to PAUSE icon");
            } else {
                playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                Log.d(TAG, "🔄 Button updated to PLAY icon");
            }
        } else {
            Log.e(TAG, "❌ playPauseButton is null!");
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

        // Show save options dialog
        showSaveOptionsDialog();
    }

    private void showSaveOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Annotations")
                .setMessage("Choose how to save your annotations:")
                .setPositiveButton("Save & Send to Student", (dialog, which) -> {
                    saveAndSendAnnotations();
                })
                .setNeutralButton("Save Only", (dialog, which) -> {
                    saveAnnotationsOnly();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveAnnotationsOnly() {
        List<AnnotationOverlay.DrawnAnnotation> currentAnnotations =
                annotationOverlay.getCurrentSessionAnnotations();

        // Convert to database format with timestamps
        List<Annotation> dbAnnotations = convertToTimestampedAnnotations(currentAnnotations);

        // Delete existing annotations for this video by this coach
        databaseHelper.deleteAnnotationsByVideoAndCoach(videoId, coachId);

        // Save new annotations
        int savedCount = 0;
        for (Annotation annotation : dbAnnotations) {
            long result = databaseHelper.addAnnotation(annotation);
            if (result != -1) {
                savedCount++;
            }
        }

        if (savedCount > 0) {
            Toast.makeText(this, savedCount + " annotations saved locally!",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "💾 Saved " + savedCount + " annotations locally");
        } else {
            Toast.makeText(this, "Error saving annotations", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save and send annotations to student with feedback
     */
    private void saveAndSendAnnotations() {
        // First save annotations
        saveAnnotationsOnly();

        // Then show feedback dialog
        showFeedbackDialog();
    }

    /**
     * Show dialog to get coach's feedback comments
     */
    private void showFeedbackDialog() {
        // Create custom dialog for feedback
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate custom layout for feedback
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_coach_feedback, null);

        EditText feedbackEdit = dialogView.findViewById(R.id.feedbackEdit);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        TextView timestampText = dialogView.findViewById(R.id.timestampText);

        // Show current video timestamp
        timestampText.setText("Feedback for annotations at: " + formatTime((int)getCurrentVideoPosition()));

        builder.setView(dialogView)
                .setTitle("Send Feedback to Student")
                .setPositiveButton("Send", (dialog, which) -> {
                    String feedbackText = feedbackEdit.getText().toString().trim();
                    float rating = ratingBar.getRating();

                    if (feedbackText.isEmpty()) {
                        Toast.makeText(this, "Please enter feedback comments", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sendFeedbackToStudent(feedbackText, rating);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Send feedback to student using database
     */
    private void sendFeedbackToStudent(String feedbackText, float rating) {
        try {
            // Create video feedback object
            VideoFeedback feedback = new VideoFeedback();
            feedback.setSubmissionId(videoId);
            feedback.setCoachId(coachId);
            feedback.setFeedbackText(feedbackText);
            feedback.setRating((int)rating);
            feedback.setStatus("sent");
            feedback.setFeedbackDate(getCurrentTimestamp());

            // Add annotation data
            List<AnnotationOverlay.DrawnAnnotation> annotations = annotationOverlay.getCurrentSessionAnnotations();
            String annotationData = serializeAnnotations(annotations);
            feedback.setAnnotationData(annotationData);

            // Save feedback to database
            long result = databaseHelper.addVideoFeedback(feedback);

            if (result != -1) {
                // Update video submission status
                updateVideoSubmissionStatus("reviewed");

                Toast.makeText(this, "✅ Feedback sent to student successfully!",
                        Toast.LENGTH_LONG).show();

                Log.d(TAG, "📤 Feedback sent - Rating: " + rating + ", Text: " + feedbackText);

                // Clear current annotations after sending
                annotationOverlay.clearCurrentSession();

                // Optionally finish activity or show confirmation
                showFeedbackSentConfirmation();
            } else {
                Toast.makeText(this, "❌ Error sending feedback", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error sending feedback", e);
            Toast.makeText(this, "Error sending feedback: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Convert current annotations to database format with timestamps
     */
    private List<Annotation> convertToTimestampedAnnotations(List<AnnotationOverlay.DrawnAnnotation> drawnAnnotations) {
        List<Annotation> dbAnnotations = new ArrayList<>();

        for (AnnotationOverlay.DrawnAnnotation drawn : drawnAnnotations) {
            for (PointF point : drawn.points) {
                Annotation annotation = new Annotation();
                annotation.setVideoId(videoId);
                annotation.setCoachId(coachId);
                annotation.setTimestamp(drawn.timestamp); // CRITICAL: Use annotation's timestamp
                annotation.setAnnotationType(drawn.tool.name());
                annotation.setAnnotationData(drawn.serializedPath);
                annotation.setXPosition(point.x);
                annotation.setYPosition(point.y);
                annotation.setCreatedAt(getCurrentTimestamp());

                dbAnnotations.add(annotation);
            }
        }

        return dbAnnotations;
    }

    /**
     * Serialize annotations for storage
     */
    private String serializeAnnotations(List<AnnotationOverlay.DrawnAnnotation> annotations) {
        StringBuilder sb = new StringBuilder();
        for (AnnotationOverlay.DrawnAnnotation annotation : annotations) {
            sb.append("timestamp:").append(annotation.timestamp).append(";");
            sb.append("tool:").append(annotation.tool.name()).append(";");
            sb.append("points:").append(annotation.serializedPath).append("|");
        }
        return sb.toString();
    }

    /**
     * Update video submission status
     */
    private void updateVideoSubmissionStatus(String status) {
        try {
            databaseHelper.updateVideoSubmissionStatus(videoId, status);
            Log.d(TAG, "📊 Video submission status updated to: " + status);
        } catch (Exception e) {
            Log.e(TAG, "Error updating video status", e);
        }
    }

    /**
     * Show confirmation that feedback was sent
     */
    private void showFeedbackSentConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Feedback Sent! ✅")
                .setMessage("Your annotations and feedback have been sent to the student. They will receive a notification to review your coaching tips.")
                .setPositiveButton("Continue Coaching", null)
                .setNeutralButton("Return to Videos", (dialog, which) -> {
                    finish(); // Return to video list
                })
                .show();
    }

    /**
     * Get current timestamp
     */
    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
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
            Log.d(TAG, "⏰ Update timer stopped");
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