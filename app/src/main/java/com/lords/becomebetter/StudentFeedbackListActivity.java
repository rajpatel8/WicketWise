package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentFeedbackListActivity extends AppCompatActivity {

    private RecyclerView feedbackRecyclerView;
    private TextView noFeedbackText;
    private ImageButton backBtn;

    private DatabaseHelper databaseHelper;
    private List<VideoSubmission> submissionsWithFeedback;
    private String studentEmail;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_feedback_list);

        studentEmail = getIntent().getStringExtra("studentEmail");
        databaseHelper = new DatabaseHelper(this);

        // Get student ID
        Student student = databaseHelper.getStudentByEmail(studentEmail);
        if (student != null) {
            studentId = student.getId();
        }

        initializeViews();
        loadFeedback();
        setupClickListeners();
    }

    private void initializeViews() {
        feedbackRecyclerView = findViewById(R.id.feedbackRecyclerView);
        noFeedbackText = findViewById(R.id.noFeedbackText);
        backBtn = findViewById(R.id.backBtn);

        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadFeedback() {
        submissionsWithFeedback = databaseHelper.getSubmissionsWithFeedbackForStudent(studentId);

        if (submissionsWithFeedback.isEmpty()) {
            feedbackRecyclerView.setVisibility(View.GONE);
            noFeedbackText.setVisibility(View.VISIBLE);
        } else {
            feedbackRecyclerView.setVisibility(View.VISIBLE);
            noFeedbackText.setVisibility(View.GONE);

            FeedbackAdapter adapter = new FeedbackAdapter(submissionsWithFeedback);
            feedbackRecyclerView.setAdapter(adapter);
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
    }

    // Adapter for feedback list
    private class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

        private List<VideoSubmission> submissions;

        public FeedbackAdapter(List<VideoSubmission> submissions) {
            this.submissions = submissions;
        }

        @NonNull
        @Override
        public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_student_feedback, parent, false);
            return new FeedbackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
            VideoSubmission submission = submissions.get(position);
            holder.bind(submission);
        }

        @Override
        public int getItemCount() {
            return submissions.size();
        }

        class FeedbackViewHolder extends RecyclerView.ViewHolder {

            private TextView titleText, coachNameText, feedbackDateText, feedbackSnippetText;
            private Button viewFeedbackBtn;

            public FeedbackViewHolder(@NonNull View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.videoTitleText);
                coachNameText = itemView.findViewById(R.id.coachNameText);
                feedbackDateText = itemView.findViewById(R.id.feedbackDateText);
                feedbackSnippetText = itemView.findViewById(R.id.feedbackSnippetText);
                viewFeedbackBtn = itemView.findViewById(R.id.viewFeedbackBtn);
            }

            public void bind(VideoSubmission submission) {
                titleText.setText(submission.getTitle());

                // Get feedback details
                List<VideoFeedback> feedbacks = submission.getFeedbacks();
                if (!feedbacks.isEmpty()) {
                    VideoFeedback latestFeedback = feedbacks.get(0); // Most recent
                    coachNameText.setText("Coach: " + latestFeedback.getCoachName());
                    feedbackDateText.setText("Reviewed: " + formatDate(latestFeedback.getFeedbackDate()));

                    // Show snippet of text feedback
                    String feedbackText = latestFeedback.getFeedbackText();
                    if (feedbackText != null && !feedbackText.isEmpty()) {
                        String snippet = feedbackText.length() > 100 ?
                                feedbackText.substring(0, 100) + "..." : feedbackText;
                        feedbackSnippetText.setText(snippet);
                        feedbackSnippetText.setVisibility(View.VISIBLE);
                    } else {
                        feedbackSnippetText.setVisibility(View.GONE);
                    }
                }

                viewFeedbackBtn.setOnClickListener(v -> {
                    // Open feedback in enhanced video player (view only for student)
                    Intent intent = new Intent(StudentFeedbackListActivity.this, EnhancedVideoPlayerActivity.class);
                    intent.putExtra("submissionId", submission.getSubmissionId());
                    intent.putExtra("studentId", studentId);
                    intent.putExtra("viewOnly", true); // Students can only view, not edit
                    intent.putExtra("showFeedback", true); // Show feedback overlay
                    startActivity(intent);
                });
            }

            private String formatDate(String dateString) {
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
}

// 7. Add method to get submissions with feedback for student

