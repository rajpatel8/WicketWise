package com.lords.becomebetter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

    private LinearLayout voiceRecordingsLayout;
    private ScrollView controlsLayout;

    // UI Components
    private VideoView videoView;
    private View annotationOverlay;
    private ImageButton playPauseBtn, backBtn, fullscreenBtn;
    private Button recordVoiceBtn, saveAnnotationBtn, saveFeedbackBtn;
    private SeekBar videoSeekBar;
    private TextView currentTimeText, totalTimeText, voiceRecordingStatus;
    private TextView videoTitleText, studentNameText, submissionDateText;
    private TextInputEditText feedbackTextEdit;

    // Removed zoom-related variables
    // private ScaleGestureDetector scaleDetector;
    // private GestureDetector gestureDetector;
    // private float scaleFactor = 1.0f;
    // private float translateX = 0f, translateY = 0f;
    // private PointF lastPan = new PointF();
    // private Matrix videoMatrix;
    // private SeekBar zoomSeekBar;
    // private TextView zoomLevelText;

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
    private Runnable updateRunnable;

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
        loadVideoSubmission();
        setupVideoPlayer();
        setupVoiceRecording();
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

    private void initializeViews() {
        videoView = findViewById(R.id.videoView);
        annotationOverlay = findViewById(R.id.annotationOverlay);
        playPauseBtn = findViewById(R.id.playPauseBtn);
        backBtn = findViewById(R.id.backBtn);
        fullscreenBtn = findViewById(R.id.fullscreenBtn);
        recordVoiceBtn = findViewById(R.id.recordVoiceBtn);
        saveAnnotationBtn = findViewById(R.id.saveAnnotationBtn);
        saveFeedbackBtn = findViewById(R.id.saveFeedbackBtn);
        videoSeekBar = findViewById(R.id.videoSeekBar);
        currentTimeText = findViewById(R.id.currentTimeText);
        totalTimeText = findViewById(R.id.totalTimeText);
        voiceRecordingStatus = findViewById(R.id.voiceRecordingStatus);
        videoTitleText = findViewById(R.id.videoTitleText);
        studentNameText = findViewById(R.id.studentNameText);
        submissionDateText = findViewById(R.id.submissionDateText);
        feedbackTextEdit = findViewById(R.id.feedbackTextEdit);
        voiceRecordingsLayout = findViewById(R.id.voiceRecordingsLayout);
        controlsLayout = findViewById(R.id.controlsLayout);

        // Hide voice recording status initially
        voiceRecordingStatus.setVisibility(View.GONE);

        // Initialize update runnable for video progress
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPlaying && videoView != null) {
                    try {
                        currentPosition = videoView.getCurrentPosition();
                        videoSeekBar.setProgress(currentPosition);
                        currentTimeText.setText(formatTime(currentPosition));
                        updateHandler.postDelayed(this, 100);
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating video progress", e);
                    }
                }
            }
        };
    }

    private void setupCoachView() {
        // Coach can edit and provide feedback
        // This is the existing functionality
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
    }

    private void setupVideoPlayer() {
        try {
            // Validate video file first
            File videoFile = new File(videoSubmission.getVideoPath());
            if (!videoFile.exists()) {
                Toast.makeText(this, "Video file not found", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            Log.d(TAG, "Setting up video: " + videoSubmission.getVideoPath());

            Uri videoUri = Uri.fromFile(videoFile);
            videoView.setVideoURI(videoUri);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "Video prepared successfully");
                    videoDuration = mp.getDuration();
                    totalTimeText.setText(formatTime(videoDuration));
                    videoSeekBar.setMax(videoDuration);
                    mp.setLooping(false);

                    // Enable play button
                    playPauseBtn.setEnabled(true);
                    Toast.makeText(EnhancedVideoPlayerActivity.this, "Video ready to play", Toast.LENGTH_SHORT).show();
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                    updateHandler.removeCallbacks(updateRunnable);
                }
            });

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e(TAG, "Video error: " + what + ", " + extra);
                    String errorMsg = "Error playing video";
                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                            errorMsg = "Unknown video error";
                            break;
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            errorMsg = "Media server died";
                            break;
                    }
                    Toast.makeText(EnhancedVideoPlayerActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            // Setup seek bar
            videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && videoView != null) {
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

            // Disable play button until video is prepared
            playPauseBtn.setEnabled(false);

        } catch (Exception e) {
            Log.e(TAG, "Error setting up video player", e);
            Toast.makeText(this, "Error loading video: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Removed setupZoomControls() and setupGestureDetectors() methods

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

        if (fullscreenBtn != null) {
            fullscreenBtn.setOnClickListener(v -> toggleFullscreen());
        }

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
        try {
            if (videoView == null) {
                Toast.makeText(this, "Video not ready", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isPlaying) {
                videoView.pause();
                isPlaying = false;
                playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                updateHandler.removeCallbacks(updateRunnable);
                Log.d(TAG, "Video paused");
            } else {
                videoView.start();
                isPlaying = true;
                playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                updateHandler.post(updateRunnable);
                Log.d(TAG, "Video started");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error toggling play/pause", e);
            Toast.makeText(this, "Error controlling video playback", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFullscreen() {
        // Simple fullscreen toggle - hide/show controls
        if (controlsLayout.getVisibility() == View.VISIBLE) {
            controlsLayout.setVisibility(View.GONE);
        } else {
            controlsLayout.setVisibility(View.VISIBLE);
        }
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

            // Prepare recording
            currentVoiceRecordingPath = getExternalFilesDir(null) + "/voice_" + System.currentTimeMillis() + ".3gp";

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
            voiceRecordingStatus.setText("Recording voice feedback...");

            Toast.makeText(this, "Voice recording started", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e(TAG, "Error starting voice recording", e);
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopVoiceRecording() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }

            isRecordingVoice = false;
            recordVoiceBtn.setText("Record Voice");
            recordVoiceBtn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.cricket_green_primary)));
            voiceRecordingStatus.setVisibility(View.GONE);

            // Create voice recording object
            VoiceRecording recording = new VoiceRecording();
            recording.setRecordingPath(currentVoiceRecordingPath);
            recording.setVideoTimestamp(currentPosition);
            recording.setDuration(0); // You can calculate duration if needed

            voiceRecordings.add(recording);
            updateVoiceRecordingsDisplay();

            Toast.makeText(this, "Voice recording saved", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error stopping voice recording", e);
            Toast.makeText(this, "Failed to stop recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateVoiceRecordingsDisplay() {
        voiceRecordingsLayout.removeAllViews();

        for (int i = 0; i < voiceRecordings.size(); i++) {
            VoiceRecording recording = voiceRecordings.get(i);
            View recordingView = getLayoutInflater().inflate(R.layout.item_voice_recording, voiceRecordingsLayout, false);

            TextView titleText = recordingView.findViewById(R.id.recordingTitleText);
            TextView timestampText = recordingView.findViewById(R.id.recordingTimestampText);
            Button playBtn = recordingView.findViewById(R.id.playRecordingBtn);
            Button deleteBtn = recordingView.findViewById(R.id.deleteRecordingBtn);

            titleText.setText("Voice note " + (i + 1));
            timestampText.setText("At: " + formatTime(recording.getVideoTimestamp()));

            playBtn.setOnClickListener(v -> playVoiceRecording(recording));
            deleteBtn.setOnClickListener(v -> deleteVoiceRecording(recording));

            voiceRecordingsLayout.addView(recordingView);
        }
    }

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

    private void setupStudentView(int studentId, boolean showFeedback) {
        // Student can only view, not edit
        isViewOnly = true;

        if (showFeedback) {
            showFeedbackOverlay();
        }
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

            // Show coach info in title or as toast since we don't have a dedicated TextView
            if (currentFeedback.getCoachName() != null) {
                Toast.makeText(this, "Feedback from: " + currentFeedback.getCoachName(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // Removed applyTransformation() and gesture listener classes

    private String formatTime(int milliseconds) {
        int totalSeconds = milliseconds / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecording();
            } else {
                Toast.makeText(this, "Audio recording permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup
        if (mediaRecorder != null) {
            try {
                mediaRecorder.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing media recorder", e);
            }
        }

        if (updateHandler != null) {
            updateHandler.removeCallbacks(updateRunnable);
        }
    }
}