package com.lords.becomebetter;

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
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_STORAGE_PERMISSION);
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
                    showError("Storage permission is required to select videos");
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
                        processSelectedVideo(selectedVideoUri);
                    }
                    break;
            }
        }
    }

    private void processSelectedVideo(Uri videoUri) {
        try {
            // Copy video to app directory
            videoPath = copyVideoToAppDirectory(videoUri);

            // Get video duration
            videoDuration = getVideoDuration(videoPath);

            // Check duration limit (10 seconds = 10000 milliseconds)
            if (videoDuration > 10000) {
                showError("Video must be 10 seconds or shorter. Current duration: " +
                        formatDuration(videoDuration));
                return;
            }

            // Generate thumbnail
            thumbnailPath = generateThumbnail(videoPath);

            // Setup preview
            setupVideoPreview();

            // Update UI
            updateVideoInfo();
            uploadBtn.setVisibility(Button.VISIBLE);

        } catch (Exception e) {
            showError("Error processing video: " + e.getMessage());
        }
    }

    private String copyVideoToAppDirectory(Uri videoUri) throws IOException {
        File videoDir = new File(getFilesDir(), "videos");
        if (!videoDir.exists()) {
            videoDir.mkdirs();
        }

        String fileName = "video_" + System.currentTimeMillis() + ".mp4";
        File videoFile = new File(videoDir, fileName);

        InputStream inputStream = getContentResolver().openInputStream(videoUri);
        FileOutputStream outputStream = new FileOutputStream(videoFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        inputStream.close();
        outputStream.close();

        return videoFile.getAbsolutePath();
    }

    private long getVideoDuration(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return Long.parseLong(duration);
        } catch (Exception e) {
            return 0;
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                // Ignore
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

                return thumbnailFile.getAbsolutePath();
            }
        } catch (Exception e) {
            // Handle error
        }
        return null;
    }

    private void setupVideoPreview() {
        if (videoPath != null) {
            videoPreview.setVideoPath(videoPath);
            videoPreview.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                videoPreview.start();
            });
        }

        if (thumbnailPath != null) {
            // You can set thumbnail to ImageView if needed
        }
    }

    private void updateVideoInfo() {
        String info = "Duration: " + formatDuration(videoDuration) + "\n" +
                "Status: Ready to upload";
        videoInfoText.setText(info);
    }

    private void uploadVideo() {
        if (!validateInput()) {
            return;
        }

        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();

        // Create video object
        Video video = new Video(studentId, coachId, videoPath, title, description,
                videoDuration, thumbnailPath);

        // Save to database
        long result = databaseHelper.addVideo(video);

        if (result != -1) {
            uploadStatusText.setText("✅ Video uploaded successfully!");
            uploadStatusText.setTextColor(getResources().getColor(R.color.success_color));

            Toast.makeText(this, "Video uploaded! Your coach will review it soon.",
                    Toast.LENGTH_LONG).show();

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Clean up temporary files if needed
        finish();
    }
}