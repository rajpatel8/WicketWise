package com.lords.becomebetter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnhancedVideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "EnhancedVideoPlayer";
    private static final int REQUEST_RECORD_AUDIO = 1001;

    // UI Components
    private VideoView videoView;
    private View annotationOverlay;
    private ImageButton playPauseBtn, backBtn, fullscreenBtn;
    private Button recordVoiceBtn, saveAnnotationBtn, saveFeedbackBtn;
    private SeekBar videoSeekBar, zoomSeekBar;
    private TextView currentTimeText, totalTimeText, zoomLevelText, voiceRecordingStatus;
    private TextView videoTitleText, studentNameText, submissionDateText;
    private LinearLayout voiceRecordingsLayout, controlsLayout;
    private TextInputEditText feedbackTextEdit;

    // Zoom and gesture handling
    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;
    private float scaleFactor = 1.0f;
    private float translateX = 0f, translateY = 0f;
    private PointF lastPan = new PointF();
    private Matrix videoMatrix;

    // Voice recording
    private MediaRecorder mediaRecorder;
    private boolean isRecordingVoice = false;
    private String currentVoiceRecordingPath;
    private List<VoiceRecording> voiceRecordings;

    // Video and feedback data
    private int submissionId;
    private int coachId;
    private String coachEmail;
    private VideoSubmission videoSubmission;
    private VideoFeedback currentFeedback;
    private List<Annotation> currentAnnotations;
    private boolean isViewOnly;

    // Video playback
    private Handler updateHandler;
    private boolean isPlaying = false;
    private int videoDuration = 0;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_video_player);

        // Get intent data - support both coach and student access
        submissionId = getIntent().getIntExtra("submissionId", 0);
        coachId = getIntent().getIntExtra("coachId", 0);
        coachEmail = getIntent().getStringExtra("coachEmail");
        int studentId = getIntent().getIntExtra("studentId", 0);
        isViewOnly = getIntent().getBooleanExtra("viewOnly", false);
        boolean showFeedback = getIntent().getBooleanExtra("showFeedback", false);

        // Initialize data structures
        voiceRecordings = new ArrayList<>();
        currentAnnotations = new ArrayList<>();
        updateHandler = new Handler();

        initializeViews();
        setupGestureDetectors();
        loadVideoSubmission();
        setupVideoPlayer();
        setupVoiceRecording();
        setupZoomControls();
        setupClickListeners();

        // Handle different user types
        if (studentId > 0) {
            // Student viewing their feedback
            setupStudentView(studentId, showFeedback);
        } else if (coachId > 0) {
            // Coach providing/viewing feedback
            setupCoachView();
        }

        if (isViewOnly) {
            hideEditingControls();
        }
    }
    private void setupCoachView() {
        // Coach can edit and provide feedback
        // This is the existing functionality
    }

    private void showFeedbackOverlay() {
        // Create a semi-transparent overlay showing feedback info
        if (currentFeedback != null) {
            // Update feedback text display
            if (feedbackTextEdit != null && currentFeedback.getFeedbackText() != null) {
                feedbackTextEdit.setText(currentFeedback.getFeedbackText());
            }

            // Show voice recordings with play buttons
            updateVoiceRecordingsDisplay();

            // Show coach info
            if (currentFeedback.getCoachName() != null) {
                // You can add a TextView to show coach name
                TextView coachInfoText = findViewById(R.id.coachInfoText);
                if (coachInfoText != null) {
                    coachInfoText.setText("Feedback from: " + currentFeedback.getCoachName());
                    coachInfoText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // Add method to play voice recordings for students
    private void playVoiceRecording(VoiceRecording recording) {
        try {
            // Seek video to the timestamp of the voice recording
            if (videoView != null && recording.getVideoTimestamp() > 0) {
                videoView.seekTo(recording.getVideoTimestamp());
            }

            // Play the voice recording
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(recording.getRecordingPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                Toast.makeText(this, "Voice feedback completed", Toast.LENGTH_SHORT).show();
            });

            Toast.makeText(this, "Playing voice feedback at " + formatTime(recording.getVideoTimestamp()),
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error playing voice recording", e);
            Toast.makeText(this, "Error playing voice feedback", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupStudentView(int studentId, boolean showFeedback) {
        // Student can only view, not edit
        isViewOnly = true;

        if (showFeedback) {
            // Load and display all feedback for this submission
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            List<VideoFeedback> feedbacks = databaseHelper.getVideoFeedbacks(submissionId);

            if (!feedbacks.isEmpty()) {
                currentFeedback = feedbacks.get(0); // Show most recent feedback
                loadExistingFeedback();
                showFeedbackOverlay();
            }
        }

        // Hide coach-specific controls
        if (recordVoiceBtn != null) recordVoiceBtn.setVisibility(View.GONE);
        if (saveAnnotationBtn != null) saveAnnotationBtn.setVisibility(View.GONE);
        if (saveFeedbackBtn != null) saveFeedbackBtn.setVisibility(View.GONE);
        if (feedbackTextEdit != null) feedbackTextEdit.setEnabled(false);
    }

    private void initializeViews() {
        // Video components
        videoView = findViewById(R.id.videoView);
        annotationOverlay = findViewById(R.id.annotationOverlay);

        // Control buttons
        playPauseBtn = findViewById(R.id.playPauseBtn);
        backBtn = findViewById(R.id.backBtn);
        fullscreenBtn = findViewById(R.id.fullscreenBtn);
        recordVoiceBtn = findViewById(R.id.recordVoiceBtn);
        saveAnnotationBtn = findViewById(R.id.saveAnnotationBtn);
        saveFeedbackBtn = findViewById(R.id.saveFeedbackBtn);

        // Seek bars
        videoSeekBar = findViewById(R.id.videoSeekBar);
        zoomSeekBar = findViewById(R.id.zoomSeekBar);

        // Text views
        currentTimeText = findViewById(R.id.currentTimeText);
        totalTimeText = findViewById(R.id.totalTimeText);
        zoomLevelText = findViewById(R.id.zoomLevelText);
        voiceRecordingStatus = findViewById(R.id.voiceRecordingStatus);
        videoTitleText = findViewById(R.id.videoTitleText);
        studentNameText = findViewById(R.id.studentNameText);
        submissionDateText = findViewById(R.id.submissionDateText);

        // Layouts
        voiceRecordingsLayout = findViewById(R.id.voiceRecordingsLayout);
        controlsLayout = findViewById(R.id.controlsLayout);

        // Feedback input
        feedbackTextEdit = findViewById(R.id.feedbackTextEdit);

        // Initial states
        voiceRecordingStatus.setVisibility(View.GONE);
        zoomLevelText.setText("1.0x");
    }

    private void setupGestureDetectors() {
        scaleDetector = new ScaleGestureDetector(this, new ScaleListener());
        gestureDetector = new GestureDetector(this, new GestureListener());

        videoView.setOnTouchListener((v, event) -> {
            scaleDetector.onTouchEvent(event);
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void loadVideoSubmission() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        videoSubmission = databaseHelper.getVideoSubmissionById(submissionId);

        if (videoSubmission == null) {
            Toast.makeText(this, "Video submission not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Update UI with submission details
        videoTitleText.setText(videoSubmission.getTitle());
        studentNameText.setText("By: " + videoSubmission.getStudentName());
        submissionDateText.setText("Submitted: " + videoSubmission.getFormattedSubmissionDate());

        // Load existing feedback if coach is viewing
        if (coachId > 0) {
            currentFeedback = databaseHelper.getVideoFeedback(submissionId, coachId);
            if (currentFeedback != null) {
                loadExistingFeedback();
            } else {
                // Create new feedback object
                currentFeedback = new VideoFeedback(submissionId, coachId, "");
            }
        }
    }

    private void loadExistingFeedback() {
        if (currentFeedback.getFeedbackText() != null) {
            feedbackTextEdit.setText(currentFeedback.getFeedbackText());
        }

        // Load voice recordings
        voiceRecordings = currentFeedback.getVoiceRecordings();
        updateVoiceRecordingsDisplay();

        // Load annotations (implement as needed)
        // currentAnnotations = loadAnnotationsFromJson(currentFeedback.getAnnotationData());
    }

    private void setupVideoPlayer() {
        try {
            Uri videoUri = Uri.parse(videoSubmission.getVideoPath());
            videoView.setVideoURI(videoUri);

            videoView.setOnPreparedListener(mp -> {
                videoDuration = mp.getDuration();
                totalTimeText.setText(formatTime(videoDuration));
                videoSeekBar.setMax(videoDuration);
                mp.setLooping(false);
            });

            videoView.setOnCompletionListener(mp -> {
                isPlaying = false;
                playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                updateHandler.removeCallbacks(updateRunnable);
            });

            videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Video error: " + what + ", " + extra);
                Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
                return true;
            });

            // Setup seek bar
            videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        videoView.seekTo(progress);
                        currentPosition = progress;
                        currentTimeText.setText(formatTime(progress));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

        } catch (Exception e) {
            Log.e(TAG, "Error setting up video player", e);
            Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupZoomControls() {
        zoomSeekBar.setMax(300); // 100% to 400% zoom
        zoomSeekBar.setProgress(100); // Start at 100%

        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float newScaleFactor = progress / 100.0f;
                    if (newScaleFactor < 1.0f) newScaleFactor = 1.0f;
                    if (newScaleFactor > 4.0f) newScaleFactor = 4.0f;

                    scaleFactor = newScaleFactor;
                    applyTransformation();
                    zoomLevelText.setText(String.format("%.1fx", scaleFactor));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupVoiceRecording() {
        recordVoiceBtn.setOnClickListener(v -> {
            if (isRecordingVoice) {
                stopVoiceRecording();
            } else {
                startVoiceRecording();
            }
        });
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());

        playPauseBtn.setOnClickListener(v -> togglePlayPause());

        fullscreenBtn.setOnClickListener(v -> toggleFullscreen());

        saveAnnotationBtn.setOnClickListener(v -> saveAnnotations());

        saveFeedbackBtn.setOnClickListener(v -> saveFeedback());
    }

    private void hideEditingControls() {
        recordVoiceBtn.setVisibility(View.GONE);
        saveAnnotationBtn.setVisibility(View.GONE);
        saveFeedbackBtn.setVisibility(View.GONE);
        feedbackTextEdit.setEnabled(false);
    }

    private void togglePlayPause() {
        if (isPlaying) {
            videoView.pause();
            isPlaying = false;
            playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
            updateHandler.removeCallbacks(updateRunnable);
        } else {
            videoView.start();
            isPlaying = true;
            playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
            updateHandler.post(updateRunnable);
        }
    }

    private void toggleFullscreen() {
        // Implement fullscreen toggle
        // This would typically involve hiding system UI and adjusting layout
    }

    private void startVoiceRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
            return;
        }

        try {
            // Pause video during recording
            if (isPlaying) {
                togglePlayPause();
            }

            String fileName = "voice_feedback_" + System.currentTimeMillis() + ".3gp";
            currentVoiceRecordingPath = new File(getExternalFilesDir("voice_recordings"), fileName).getAbsolutePath();

            // Ensure directory exists
            new File(currentVoiceRecordingPath).getParentFile().mkdirs();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(currentVoiceRecordingPath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecordingVoice = true;
            recordVoiceBtn.setText("Stop Recording");
            recordVoiceBtn.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            voiceRecordingStatus.setVisibility(View.VISIBLE);
            voiceRecordingStatus.setText("🎤 Recording voice feedback at " + formatTime(currentPosition));

        } catch (IOException e) {
            Log.e(TAG, "Failed to start recording", e);
            Toast.makeText(this, "Failed to start voice recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopVoiceRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                isRecordingVoice = false;
                recordVoiceBtn.setText("Record Voice");
                recordVoiceBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));

                // Create voice recording object
                VoiceRecording recording = new VoiceRecording();
                recording.setRecordingPath(currentVoiceRecordingPath);
                recording.setVideoTimestamp(currentPosition);
                recording.setTitle("Voice note at " + formatTime(currentPosition));

                voiceRecordings.add(recording);
                updateVoiceRecordingsDisplay();

                voiceRecordingStatus.setText("✅ Voice feedback recorded successfully!");

                // Hide status after 3 seconds
                updateHandler.postDelayed(() ->
                        voiceRecordingStatus.setVisibility(View.GONE), 3000);

            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to stop recording", e);
                Toast.makeText(this, "Failed to stop recording", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateVoiceRecordingsDisplay() {
        voiceRecordingsLayout.removeAllViews();

        for (VoiceRecording recording : voiceRecordings) {
            View recordingView = getLayoutInflater().inflate(R.layout.item_voice_recording,
                    voiceRecordingsLayout, false);

            TextView titleText = recordingView.findViewById(R.id.recordingTitleText);
            TextView timestampText = recordingView.findViewById(R.id.recordingTimestampText);
            Button playBtn = recordingView.findViewById(R.id.playRecordingBtn);
            Button deleteBtn = recordingView.findViewById(R.id.deleteRecordingBtn);

            titleText.setText(recording.getDisplayTitle());
            timestampText.setText("At: " + recording.getFormattedVideoTimestamp());

            playBtn.setOnClickListener(v -> playVoiceRecording(recording));
            deleteBtn.setOnClickListener(v -> deleteVoiceRecording(recording));

            voiceRecordingsLayout.addView(recordingView);
        }
    }

    private void deleteVoiceRecording(VoiceRecording recording) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Voice Recording")
                .setMessage("Are you sure you want to delete this voice recording?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete file
                    new File(recording.getRecordingPath()).delete();
                    // Remove from list
                    voiceRecordings.remove(recording);
                    updateVoiceRecordingsDisplay();
                    Toast.makeText(this, "Voice recording deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveAnnotations() {
        // Implement annotation saving
        // This would save the current annotations to the database
        Toast.makeText(this, "Annotations saved", Toast.LENGTH_SHORT).show();
    }

    private void saveFeedback() {
        String feedbackText = feedbackTextEdit.getText().toString().trim();

        if (feedbackText.isEmpty() && voiceRecordings.isEmpty()) {
            Toast.makeText(this, "Please provide some feedback before saving", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update feedback object
        currentFeedback.setFeedbackText(feedbackText);
        currentFeedback.setVoiceRecordings(voiceRecordings);
        currentFeedback.setStatus("completed");

        // Save to database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        boolean success = databaseHelper.saveVideoFeedback(currentFeedback);

        if (success) {
            Toast.makeText(this, "Feedback saved successfully!", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to save feedback", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyTransformation() {
        ViewGroup.LayoutParams params = videoView.getLayoutParams();

        // Calculate new dimensions
        int originalWidth = videoView.getWidth();
        int originalHeight = videoView.getHeight();

        params.width = (int)(originalWidth * scaleFactor);
        params.height = (int)(originalHeight * scaleFactor);

        videoView.setLayoutParams(params);
        videoView.setTranslationX(translateX);
        videoView.setTranslationY(translateY);
    }

    // Gesture listeners
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 4.0f));

            applyTransformation();
            zoomSeekBar.setProgress((int)(scaleFactor * 100));
            zoomLevelText.setText(String.format("%.1fx", scaleFactor));

            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (scaleFactor > 1.0f) {
                translateX -= distanceX;
                translateY -= distanceY;
                applyTransformation();
            }
            return true;
        }
    }

    // Update runnable for video progress
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying && videoView != null) {
                currentPosition = videoView.getCurrentPosition();
                videoSeekBar.setProgress(currentPosition);
                currentTimeText.setText(formatTime(currentPosition));
                updateHandler.postDelayed(this, 1000);
            }
        }
    };

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecording();
            } else {
                Toast.makeText(this, "Audio permission required for voice feedback",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlaying) {
            togglePlayPause();
        }
        updateHandler.removeCallbacks(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        updateHandler.removeCallbacks(updateRunnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Return to appropriate screen based on user type
        Intent intent;
        if (coachEmail != null) {
            intent = new Intent(this, VideoListActivity.class);
            intent.putExtra("coachEmail", coachEmail);
        } else {
            intent = new Intent(this, StudentProfileActivity.class);
            intent.putExtra("userEmail", videoSubmission.getStudentName());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}