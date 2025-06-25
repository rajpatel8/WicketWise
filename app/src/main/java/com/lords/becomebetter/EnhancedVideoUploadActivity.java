package com.lords.becomebetter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnhancedVideoUploadActivity extends AppCompatActivity {

    private static final String TAG = "EnhancedVideoUpload";
    private static final int REQUEST_VIDEO_CAPTURE = 1001;
    private static final int REQUEST_VIDEO_GALLERY = 1002;
    private static final int REQUEST_CAMERA_PERMISSION = 1003;
    private static final int REQUEST_STORAGE_PERMISSION = 1004;

    // UI Components
    private VideoView videoPreview;
    private ImageView thumbnailPreview;
    private TextView videoInfoText, uploadStatusText, selectedCoachesText, noCoachesText;
    private TextInputLayout titleLayout, descriptionLayout;
    private TextInputEditText titleEdit, descriptionEdit;
    private Button selectVideoBtn, recordVideoBtn, selectCoachesBtn, uploadBtn;
    private ImageButton backBtn;

    // Data
    private DatabaseHelper databaseHelper;
    private String studentEmail;
    private int studentId;
    private Uri selectedVideoUri;
    private String videoPath;
    private String thumbnailPath;
    private long videoDuration;
    private List<Coach> availableCoaches;
    private List<Integer> selectedCoachIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_video_upload);

        databaseHelper = new DatabaseHelper(this);

        // ADD THESE TEST LINES:
        android.util.Log.d("VIDEO_UPLOAD", "🧪 Testing coach functionality...");
        List<Coach> testCoaches = databaseHelper.getCoachesForStudent(1);
        android.util.Log.d("VIDEO_UPLOAD", "📊 Test result: " + testCoaches.size() + " coaches found");

        studentEmail = getIntent().getStringExtra("studentEmail");
        selectedCoachIds = new ArrayList<>();
        availableCoaches = new ArrayList<>();

        Log.d(TAG, "EnhancedVideoUploadActivity created for student: " + studentEmail);

        initializeViews();
        loadStudentData();
        loadAvailableCoaches();  // This should now work!
        setupClickListeners();
    }

    private void initializeViews() {
        // Video components
        videoPreview = findViewById(R.id.videoPreview);
        thumbnailPreview = findViewById(R.id.thumbnailPreview);
        videoInfoText = findViewById(R.id.videoInfoText);
        uploadStatusText = findViewById(R.id.uploadStatusText);

        // Input components
        titleLayout = findViewById(R.id.videoTitleLayout);
        descriptionLayout = findViewById(R.id.videoDescriptionLayout);
        titleEdit = findViewById(R.id.videoTitleEdit);
        descriptionEdit = findViewById(R.id.videoDescriptionEdit);

        // Coach selection components
        selectedCoachesText = findViewById(R.id.selectedCoachesText);
        noCoachesText = findViewById(R.id.noCoachesText);

        // Buttons
        selectVideoBtn = findViewById(R.id.selectVideoBtn);
        recordVideoBtn = findViewById(R.id.recordVideoBtn);
        selectCoachesBtn = findViewById(R.id.selectCoachesBtn);
        uploadBtn = findViewById(R.id.uploadVideoBtn);
        backBtn = findViewById(R.id.backBtn);

        // Initially hide upload button and set default states
        uploadBtn.setVisibility(View.GONE);
        uploadStatusText.setVisibility(View.GONE);
        selectedCoachesText.setText("No coaches selected");
        selectCoachesBtn.setEnabled(false);
    }

    private void loadStudentData() {
        Student student = databaseHelper.getStudentByEmail(studentEmail);
        if (student == null) {
            showError("Student profile not found");
            finish();
            return;
        }

        studentId = student.getId();
        Log.d(TAG, "Student loaded: ID=" + studentId + ", Name=" + student.getName());
    }

    private void loadAvailableCoaches() {
        // Get coaches who have accepted this student
        availableCoaches = databaseHelper.getCoachesForStudent(studentId);

        Log.d(TAG, "Available coaches for student: " + availableCoaches.size());

        if (availableCoaches.isEmpty()) {
            noCoachesText.setVisibility(View.VISIBLE);
            selectCoachesBtn.setEnabled(false);
            selectCoachesBtn.setText("No Coaches Available");
        } else {
            noCoachesText.setVisibility(View.GONE);
            selectCoachesBtn.setEnabled(true);
            selectCoachesBtn.setText("Select Coaches (" + availableCoaches.size() + " available)");
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());

        selectVideoBtn.setOnClickListener(v -> selectVideoFromGallery());
        recordVideoBtn.setOnClickListener(v -> recordNewVideo());
        selectCoachesBtn.setOnClickListener(v -> showCoachSelectionDialog());
        uploadBtn.setOnClickListener(v -> uploadVideoWithFeedbackRequest());
    }

    private void selectVideoFromGallery() {
        if (checkStoragePermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_VIDEO_GALLERY);
        }
    }

    private void recordNewVideo() {
        if (checkCameraPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 300); // 5 minutes max
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // High quality
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
            } else {
                Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showCoachSelectionDialog() {
        android.util.Log.d("SIMPLE_DIALOG", "🎯 Opening SIMPLE coach dialog...");

        if (availableCoaches.isEmpty()) {
            Toast.makeText(this, "No coaches available!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a simple list of coach names
        List<String> coachNamesList = new ArrayList<>();
        for (Coach coach : availableCoaches) {
            coachNamesList.add(coach.getName() + " - " + coach.getSpecialization());
        }

        // Convert to array
        String[] coachArray = coachNamesList.toArray(new String[0]);

        android.util.Log.d("SIMPLE_DIALOG", "📝 Coach array: " + Arrays.toString(coachArray));

        // Create simple AlertDialog with single choice items
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Coach");
        builder.setItems(coachArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clear previous selections and add the selected coach
                selectedCoachIds.clear();
                selectedCoachIds.add(availableCoaches.get(which).getId());

                android.util.Log.d("SIMPLE_DIALOG", "✅ Selected coach: " + availableCoaches.get(which).getName());

                updateSelectedCoachesDisplay();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();

        android.util.Log.d("SIMPLE_DIALOG", "🚀 Simple dialog shown");
    }

    private void updateSelectedCoachesDisplay() {
        if (selectedCoachIds.isEmpty()) {
            selectedCoachesText.setText("No coaches selected");
            updateUploadButtonState();
        } else {
            StringBuilder text = new StringBuilder("Selected coaches: ");
            for (int i = 0; i < selectedCoachIds.size(); i++) {
                Coach coach = getCoachById(selectedCoachIds.get(i));
                if (coach != null) {
                    text.append(coach.getName());
                    if (i < selectedCoachIds.size() - 1) text.append(", ");
                }
            }
            selectedCoachesText.setText(text.toString());
            updateUploadButtonState();
        }
    }

    private Coach getCoachById(int coachId) {
        return availableCoaches.stream()
                .filter(coach -> coach.getId() == coachId)
                .findFirst()
                .orElse(null);
    }

    private void updateUploadButtonState() {
        boolean canUpload = selectedVideoUri != null && !selectedCoachIds.isEmpty();
        uploadBtn.setVisibility(canUpload ? View.VISIBLE : View.GONE);
        uploadBtn.setEnabled(canUpload);
    }

    private void uploadVideoWithFeedbackRequest() {
        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            titleEdit.setError("Please enter a title");
            titleEdit.requestFocus();
            return;
        }

        if (selectedVideoUri == null) {
            Toast.makeText(this, "Please select a video first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCoachIds.isEmpty()) {
            Toast.makeText(this, "Please select at least one coach", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show uploading status
        uploadStatusText.setVisibility(View.VISIBLE);
        uploadStatusText.setText("Uploading video...");
        uploadBtn.setEnabled(false);

        // Copy video to app storage and create submission
        try {
            String copiedVideoPath = copyVideoToAppStorage();
            if (copiedVideoPath != null) {
                createVideoSubmission(title, description, copiedVideoPath);
            } else {
                showUploadError("Failed to save video");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error uploading video", e);
            showUploadError("Error uploading video: " + e.getMessage());
        }
    }

    private String copyVideoToAppStorage() {
        try {
            String fileName = "video_" + System.currentTimeMillis() + ".mp4";
            File videoFile = new File(getExternalFilesDir("videos"), fileName);

            // Create directory if it doesn't exist
            videoFile.getParentFile().mkdirs();

            // Copy video from URI to app storage
            InputStream inputStream = getContentResolver().openInputStream(selectedVideoUri);
            FileOutputStream outputStream = new FileOutputStream(videoFile);

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return videoFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error copying video", e);
            return null;
        }
    }

    private void createVideoSubmission(String title, String description, String videoPath) {
        // Create video submission object
        VideoSubmission submission = new VideoSubmission();
        submission.setTitle(title);
        submission.setDescription(description);
        submission.setVideoPath(videoPath);
        submission.setStudentId(studentId);
        submission.setSelectedCoachIds(selectedCoachIds);

        // Save to database
        long submissionId = databaseHelper.createVideoSubmission(submission);

        if (submissionId > 0) {
            uploadStatusText.setText("Video uploaded successfully!");

            String message = "Video submitted successfully!\n" +
                    selectedCoachIds.size() + " coach(es) will be notified for feedback.";

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Return success result
            setResult(RESULT_OK);
            finish();
        } else {
            showUploadError("Failed to save video submission to database");
        }
    }

    private void showUploadError(String message) {
        uploadStatusText.setText("Upload failed: " + message);
        uploadBtn.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_VIDEO_CAPTURE:
                case REQUEST_VIDEO_GALLERY:
                    selectedVideoUri = data.getData();
                    if (selectedVideoUri != null) {
                        setupVideoPreview();
                        extractVideoInfo();
                        updateUploadButtonState();
                    }
                    break;
            }
        }
    }

    private void setupVideoPreview() {
        try {
            videoPreview.setVideoURI(selectedVideoUri);
            videoPreview.setVisibility(View.VISIBLE);
            thumbnailPreview.setVisibility(View.GONE);

            // Create and show thumbnail
            generateVideoThumbnail();

        } catch (Exception e) {
            Log.e(TAG, "Error setting up video preview", e);
            Toast.makeText(this, "Error loading video preview", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateVideoThumbnail() {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, selectedVideoUri);

            Bitmap thumbnail = retriever.getFrameAtTime(1000000); // Get frame at 1 second
            if (thumbnail != null) {
                thumbnailPreview.setImageBitmap(thumbnail);
                thumbnailPreview.setVisibility(View.VISIBLE);
                videoPreview.setVisibility(View.GONE);
            }

            retriever.release();
        } catch (Exception e) {
            Log.e(TAG, "Error generating thumbnail", e);
        }
    }

    private void extractVideoInfo() {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, selectedVideoUri);

            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

            if (duration != null) {
                videoDuration = Long.parseLong(duration);
                String formattedDuration = formatDuration(videoDuration);

                String fileName = getFileName(selectedVideoUri);
                String videoInfo = String.format("File: %s\nDuration: %s\nResolution: %sx%s",
                        fileName, formattedDuration, width, height);

                videoInfoText.setText(videoInfo);
                videoInfoText.setVisibility(View.VISIBLE);
            }

            retriever.release();
        } catch (Exception e) {
            Log.e(TAG, "Error extracting video info", e);
            videoInfoText.setText("Video selected");
            videoInfoText.setVisibility(View.VISIBLE);
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "video.mp4";
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file name", e);
        }
        return fileName;
    }

    private String formatDuration(long durationMs) {
        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return String.format("0:%02d", seconds);
        }
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return false;
        }
        return true;
    }

    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordNewVideo();
                } else {
                    Toast.makeText(this, "Camera permission required to record video",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectVideoFromGallery();
                } else {
                    Toast.makeText(this, "Storage permission required to select video",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Return to student profile
        Intent intent = new Intent(this, StudentProfileActivity.class);
        intent.putExtra("userEmail", studentEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}