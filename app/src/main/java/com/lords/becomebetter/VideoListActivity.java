package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    private RecyclerView videosRecyclerView;
    private LinearLayout noVideosLayout;
    private ImageButton backBtn;
    private TextView headerTitle, videoCountText;

    private DatabaseHelper databaseHelper;
    private VideoListAdapter videoAdapter;
    private List<Video> videosList;
    private String coachEmail;
    private int coachId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        databaseHelper = new DatabaseHelper(this);
        coachEmail = getIntent().getStringExtra("coachEmail");

        initializeViews();
        loadCoachData();
        loadVideos();
        setupClickListeners();
    }

    private void initializeViews() {
        videosRecyclerView = findViewById(R.id.videosRecyclerView);
        noVideosLayout = findViewById(R.id.noVideosLayout);
        backBtn = findViewById(R.id.backBtn);
        headerTitle = findViewById(R.id.headerTitle);
        videoCountText = findViewById(R.id.videoCountText);

        videosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCoachData() {
        Coach coach = databaseHelper.getCoachByEmail(coachEmail);
        if (coach == null) {
            showError("Coach profile not found");
            finish();
            return;
        }
        coachId = coach.getId();
    }


    private void loadVideos() {
        // Load video submissions instead of regular videos
        List<VideoSubmission> videoSubmissions = databaseHelper.getVideoSubmissionsForCoach(coachId);

        Log.d("VideoList", "📊 Found " + videoSubmissions.size() + " video submissions for coach " + coachId);

        // Convert VideoSubmissions to Videos for the adapter
        videosList = new ArrayList<>();
        for (VideoSubmission submission : videoSubmissions) {
            Video video = new Video();
            video.setVideoId(submission.getSubmissionId()); // CORRECTED: getSubmissionId() instead of getId()
            video.setStudentId(submission.getStudentId());
            video.setCoachId(coachId); // Set the current coach ID
            video.setVideoPath(submission.getVideoPath());
            video.setVideoTitle(submission.getTitle());
            video.setVideoDescription(submission.getDescription());
            // Note: VideoSubmission doesn't have getDuration(), so we'll set a default or handle it differently
            video.setVideoDuration(0); // CORRECTED: Set default duration, will need to get from video file
            video.setUploadDate(submission.getSubmissionDate());
            video.setStatus("submitted");
            // Note: VideoSubmission doesn't have getThumbnailPath(), so we'll set a default
            video.setThumbnailPath(""); // CORRECTED: Set empty thumbnail path

            videosList.add(video);

            Log.d("VideoList", "✅ Added video: " + video.getVideoTitle() + " from student " + submission.getStudentName());
        }

        if (videosList.isEmpty()) {
            videosRecyclerView.setVisibility(View.GONE);
            noVideosLayout.setVisibility(View.VISIBLE);
            videoCountText.setText("No videos uploaded");
        } else {
            videosRecyclerView.setVisibility(View.VISIBLE);
            noVideosLayout.setVisibility(View.GONE);

            String countText = videosList.size() == 1 ?
                    "1 Video" : videosList.size() + " Videos";
            videoCountText.setText(countText);

            videoAdapter = new VideoListAdapter(videosList);
            videosRecyclerView.setAdapter(videoAdapter);
        }
    }


    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    // Video List RecyclerView Adapter
    private class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

        private List<Video> videos;

        public VideoListAdapter(List<Video> videos) {
            this.videos = videos;
        }

        @NonNull
        @Override
        public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video_coach, parent, false);
            return new VideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
            Video video = videos.get(position);
            holder.bind(video);
        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        class VideoViewHolder extends RecyclerView.ViewHolder {

            private TextView titleText, studentNameText, durationText, uploadDateText, statusText;
            private Button reviewBtn, annotateBtn;
            private ImageView videoIcon, statusIcon;
            private View statusIndicator;

            public VideoViewHolder(@NonNull View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.videoTitleText);
                studentNameText = itemView.findViewById(R.id.studentNameText);
                durationText = itemView.findViewById(R.id.videoDurationText);
                uploadDateText = itemView.findViewById(R.id.videoUploadDateText);
                statusText = itemView.findViewById(R.id.videoStatusText);
                reviewBtn = itemView.findViewById(R.id.reviewVideoBtn);
                annotateBtn = itemView.findViewById(R.id.annotateVideoBtn);
                videoIcon = itemView.findViewById(R.id.videoIcon);
                statusIcon = itemView.findViewById(R.id.statusIcon);
                statusIndicator = itemView.findViewById(R.id.statusIndicator);
            }

            public void bind(Video video) {
                titleText.setText(video.getVideoTitle());

                // Get student name for the video
                String studentName = databaseHelper.getStudentNameById(video.getStudentId());
                studentNameText.setText("Student: " + studentName);

                // Format duration
                if (video.getVideoDuration() > 0) {
                    long seconds = video.getVideoDuration() / 1000;
                    long minutes = seconds / 60;
                    seconds = seconds % 60;
                    durationText.setText(String.format("%d:%02d", minutes, seconds));
                } else {
                    durationText.setText("--:--");
                }

                // Format upload date
                uploadDateText.setText("Uploaded: " + formatUploadDate(video.getUploadDate()));

                // Set status based on whether it's been reviewed
                String status = video.getStatus();
                if ("submitted".equals(status)) {
                    statusText.setText("Pending Review");
                    statusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.warning_color));
                    if (statusIndicator != null) {
                        statusIndicator.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.warning_color));
                    }
                } else if ("reviewed".equals(status)) {
                    statusText.setText("Reviewed");
                    statusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.success_color));
                    if (statusIndicator != null) {
                        statusIndicator.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.success_color));
                    }
                } else {
                    statusText.setText("Unknown");
                    statusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                    if (statusIndicator != null) {
                        statusIndicator.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                    }
                }

                // Update button text based on status
                if ("reviewed".equals(status)) {
                    reviewBtn.setText("View Again");
                    annotateBtn.setVisibility(View.VISIBLE);
                    annotateBtn.setText("View Annotations");
                } else {
                    reviewBtn.setText("Review Video");
                    annotateBtn.setVisibility(View.VISIBLE);
                    annotateBtn.setText("Add Feedback");
                }

                // Set click listeners - pass the correct video ID
                reviewBtn.setOnClickListener(v -> {
                    Log.d("VideoList", "🎬 Opening video player for video ID: " + video.getVideoId());
                    Intent intent = new Intent(VideoListActivity.this, VideoPlayerActivity.class);
                    intent.putExtra("videoId", video.getVideoId());
                    intent.putExtra("coachEmail", coachEmail);
                    intent.putExtra("viewOnly", true);
                    intent.putExtra("isSubmission", true); // Add this flag
                    startActivity(intent);
                });

                annotateBtn.setOnClickListener(v -> {
                    Log.d("VideoList", "✏️ Opening video player for annotation, video ID: " + video.getVideoId());
                    Intent intent = new Intent(VideoListActivity.this, VideoPlayerActivity.class);
                    intent.putExtra("videoId", video.getVideoId());
                    intent.putExtra("coachEmail", coachEmail);
                    intent.putExtra("viewOnly", false);
                    intent.putExtra("isSubmission", true); // Add this flag
                    startActivityForResult(intent, 100);
                });
            }

            private String formatUploadDate(String dateString) {
                if (dateString == null) return "Unknown";
                try {
                    String[] parts = dateString.split(" ");
                    if (parts.length > 0) {
                        return parts[0]; // Return just the date part
                    }
                } catch (Exception e) {
                    // Handle parsing errors gracefully
                }
                return "Unknown";
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Video was annotated, reload the list
            loadVideos();
            Toast.makeText(this, "Video annotations saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Return to coach dashboard
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("userType", "coach");
        intent.putExtra("userEmail", coachEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}