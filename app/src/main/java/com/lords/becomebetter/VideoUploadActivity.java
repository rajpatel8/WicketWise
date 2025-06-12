package com.lords.becomebetter;

import android.os.Build;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
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

public class VideoUploadActivity extends AppCompatActivity {

    private static final String TAG = "VideoUploadActivity";
    private static final int REQUEST_VIDEO_CAPTURE = 1001;
    private static final int REQUEST_VIDEO_GALLERY = 1002;
    private static final int REQUEST_CAMERA_PERMISSION = 1003;
    private static final int REQUEST_STORAGE_PERMISSION = 1004;

    private VideoView videoPreview;
    private ImageView thumbnailPreview;
    private TextView videoInfoText, uploadStatusText;
    private TextInputLayout titleLayout, descriptionLayout;
    private TextInputEditText titleEdit, descriptionEdit;
    private Button selectVideoBtn, recordVideoBtn, uploadBtn;
    private ImageButton backBtn;

    private DatabaseHelper databaseHelper;
    private String studentEmail;
    private int studentId;
    private int coachId;
    private Uri selectedVideoUri;
    private String videoPath;
    private String thumbnailPath;
    private long videoDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        databaseHelper = new DatabaseHelper(this);
        studentEmail = getIntent().getStringExtra("studentEmail");

        Log.d(TAG, "VideoUploadActivity created for student: " + studentEmail);

        initializeViews();
        loadStudentData();
        setupClickListeners();
    }

    private void initializeViews() {
        videoPreview = findViewById(R.id.videoPreview);
        thumbnailPreview = findViewById(R.id.thumbnailPreview);
        videoInfoText = findViewById(R.id.videoInfoText);
        uploadStatusText = findViewById(R.id.uploadStatusText);

        titleLayout = findViewById(R.id.videoTitleLayout);
        descriptionLayout = findViewById(R.id.videoDescriptionLayout);
        titleEdit = findViewById(R.id.videoTitleEdit);
        descriptionEdit = findViewById(R.id.videoDescriptionEdit);

        selectVideoBtn = findViewById(R.id.selectVideoBtn);
        recordVideoBtn = findViewById(R.id.recordVideoBtn);
        uploadBtn = findViewById(R.id.uploadVideoBtn);
        backBtn = findViewById(R.id.backBtn);

        // Initially hide upload button
        uploadBtn.setVisibility(Button.GONE);
        uploadStatusText.setVisibility(TextView.GONE);
    }

    private void loadStudentData() {
        Student student = databaseHelper.getStudentByEmail(studentEmail);
        if (student == null) {
            showError("Student profile not found");
            finish();
            return;
        }

        studentId = student.getId();
        coachId = student.getCoachId();

        Log.d(TAG, "Student data loaded - ID: " + studentId + ", Coach ID: " + coachId);

        if (coachId == 0) {
            showError("Please assign a coach first before uploading videos");
            finish();
            return;
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
        selectVideoBtn.setOnClickListener(v -> selectVideoFromGallery());
        recordVideoBtn.setOnClickListener(v -> recordVideo());
        uploadBtn.setOnClickListener(v -> uploadVideo());
    }

    private void selectVideoFromGallery() {
        if (checkStoragePermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_VIDEO_GALLERY);
        } else {
            requestStoragePermission();
        }
    }

    private void recordVideo() {
        if (checkCameraPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10); // 10 seconds limit
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // High quality
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        } else {
            requestCameraPermission();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkStoragePermission() {
        // Check different permissions based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                REQUEST_CAMERA_PERMISSION);
    }

    private void requestStoragePermission() {
        String[] permissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            permissions = new String[]{
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordVideo();
                } else {
                    showError("Camera permission is required to record videos");
                }
                break;

            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectVideoFromGallery();
                } else {
                    showError("Storage permission is required to select videos from gallery");
                }
                break;
        }
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
                        Log.d(TAG, "Video selected: " + selectedVideoUri.toString());
                        processSelectedVideo(selectedVideoUri);
                    } else {
                        showError("Failed to get video URI");
                    }
                    break;
            }
        } else {
            Log.d(TAG, "Activity result not OK or data is null");
        }
    }

    private void processSelectedVideo(Uri videoUri) {
        try {
            Log.d(TAG, "Processing video: " + videoUri.toString());

            // Copy video to app directory
            videoPath = copyVideoToAppDirectory(videoUri);
            Log.d(TAG, "Video copied to: " + videoPath);

            // Verify the copied file
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                showError("Failed to copy video file");
                return;
            }

            Log.d(TAG, "Video file size: " + videoFile.length() + " bytes");

            // Get video duration
            videoDuration = getVideoDuration(videoPath);
            Log.d(TAG, "Video duration: " + videoDuration + " ms");

            // Check duration limit (10 seconds = 10000 milliseconds)
            if (videoDuration > 10000) {
                showError("Video must be 10 seconds or shorter. Current duration: " +
                        formatDuration(videoDuration));
                return;
            }

            // Generate thumbnail
            thumbnailPath = generateThumbnail(videoPath);
            Log.d(TAG, "Thumbnail generated: " + thumbnailPath);

            // Setup preview
            setupVideoPreview();

            // Update UI
            updateVideoInfo();
            uploadBtn.setVisibility(Button.VISIBLE);

            Toast.makeText(this, "Video processed successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error processing video", e);
            showError("Error processing video: " + e.getMessage());
        }
    }

    private String copyVideoToAppDirectory(Uri videoUri) throws IOException {
        File videoDir = new File(getFilesDir(), "videos");
        if (!videoDir.exists()) {
            boolean created = videoDir.mkdirs();
            Log.d(TAG, "Video directory created: " + created);
        }

        String fileName = "video_" + System.currentTimeMillis() + ".mp4";
        File videoFile = new File(videoDir, fileName);

        Log.d(TAG, "Copying video to: " + videoFile.getAbsolutePath());

        InputStream inputStream = getContentResolver().openInputStream(videoUri);
        if (inputStream == null) {
            throw new IOException("Cannot open input stream for video URI");
        }

        FileOutputStream outputStream = new FileOutputStream(videoFile);

        byte[] buffer = new byte[1024 * 8]; // 8KB buffer
        int length;
        long totalBytes = 0;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
            totalBytes += length;
        }

        inputStream.close();
        outputStream.close();

        Log.d(TAG, "Video copied successfully. Total bytes: " + totalBytes);

        return videoFile.getAbsolutePath();
    }

    private long getVideoDuration(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duration != null) {
                return Long.parseLong(duration);
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting video duration", e);
            return 0;
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing MediaMetadataRetriever", e);
            }
        }
    }

    private String generateThumbnail(String videoPath) {
        try {
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoPath,
                    MediaStore.Images.Thumbnails.MINI_KIND);

            if (thumbnail != null) {
                File thumbnailDir = new File(getFilesDir(), "thumbnails");
                if (!thumbnailDir.exists()) {
                    thumbnailDir.mkdirs();
                }

                String fileName = "thumb_" + System.currentTimeMillis() + ".jpg";
                File thumbnailFile = new File(thumbnailDir, fileName);

                FileOutputStream out = new FileOutputStream(thumbnailFile);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.close();

                Log.d(TAG, "Thumbnail saved: " + thumbnailFile.getAbsolutePath());
                return thumbnailFile.getAbsolutePath();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error generating thumbnail", e);
        }
        return null;
    }

    private void setupVideoPreview() {
        if (videoPath != null) {
            try {
                videoPreview.setVideoPath(videoPath);
                videoPreview.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    videoPreview.start();
                    Log.d(TAG, "Video preview started");
                });

                videoPreview.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "Video preview error: " + what + ", " + extra);
                    return false;
                });
            } catch (Exception e) {
                Log.e(TAG, "Error setting up video preview", e);
            }
        }
    }

    private void updateVideoInfo() {
        String info = "Duration: " + formatDuration(videoDuration) + "\n" +
                "File size: " + getFileSize(videoPath) + "\n" +
                "Status: Ready to upload";
        videoInfoText.setText(info);
    }

    private String getFileSize(String filePath) {
        try {
            File file = new File(filePath);
            long bytes = file.length();
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
            return (bytes / (1024 * 1024)) + " MB";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void uploadVideo() {
        if (!validateInput()) {
            return;
        }

        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();

        Log.d(TAG, "Uploading video - Title: " + title + ", Duration: " + videoDuration);

        // Create video object
        Video video = new Video(studentId, coachId, videoPath, title, description,
                videoDuration, thumbnailPath);

        // Save to database
        long result = databaseHelper.addVideo(video);

        if (result != -1) {
            uploadStatusText.setVisibility(TextView.VISIBLE);
            uploadStatusText.setText("✅ Video uploaded successfully!");
            uploadStatusText.setTextColor(getResources().getColor(R.color.success_color));

            Toast.makeText(this, "Video uploaded! Your coach will review it soon.",
                    Toast.LENGTH_LONG).show();

            Log.d(TAG, "Video uploaded successfully with ID: " + result);

            // Return to previous screen after delay
            uploadStatusText.postDelayed(() -> {
                setResult(RESULT_OK);
                finish();
            }, 2000);

        } else {
            showError("Failed to upload video. Please try again.");
        }
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (videoPath == null) {
            showError("Please select or record a video first");
            isValid = false;
        }

        String title = titleEdit.getText().toString().trim();
        if (title.isEmpty()) {
            titleLayout.setError("Title is required");
            isValid = false;
        } else {
            titleLayout.setError(null);
        }

        return isValid;
    }

    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Error: " + message);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Clean up temporary files if needed
        finish();
    }
}