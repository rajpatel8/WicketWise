package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "StudentFeedback";

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
        studentId = getIntent().getIntExtra("studentId", 0);
        databaseHelper = new DatabaseHelper(this);

        Log.d(TAG, "StudentFeedbackListActivity started with studentId: " + studentId);

        // Get student ID if not provided
        if (studentId == 0) {
            Student student = databaseHelper.getStudentByEmail(studentEmail);
            if (student != null) {
                studentId = student.getId();
                Log.d(TAG, "Found student ID from email: " + studentId);
            } else {
                Log.e(TAG, "Could not find student with email: " + studentEmail);
            }
        }

        initializeViews();
        loadFeedback();
        setupClickListeners();
    }

    private void initializeViews() {
        feedbackRecyclerView = findViewById(R.id.feedbackRecyclerView);
        noFeedbackText = findViewById(R.id.noFeedbackText);
        backBtn = findViewById(R.id.backBtn);

        if (feedbackRecyclerView != null) {
            feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Log.e(TAG, "feedbackRecyclerView is null!");
        }
    }

    private void loadFeedback() {
        Log.d(TAG, "Loading feedback for student: " + studentId);

        try {
            submissionsWithFeedback = databaseHelper.getSubmissionsWithFeedbackForStudent(studentId);
            Log.d(TAG, "Found " + submissionsWithFeedback.size() + " submissions with feedback");

            if (submissionsWithFeedback.isEmpty()) {
                if (feedbackRecyclerView != null) feedbackRecyclerView.setVisibility(View.GONE);
                if (noFeedbackText != null) noFeedbackText.setVisibility(View.VISIBLE);
            } else {
                if (feedbackRecyclerView != null) feedbackRecyclerView.setVisibility(View.VISIBLE);
                if (noFeedbackText != null) noFeedbackText.setVisibility(View.GONE);

                FeedbackAdapter adapter = new FeedbackAdapter(submissionsWithFeedback);
                if (feedbackRecyclerView != null) {
                    feedbackRecyclerView.setAdapter(adapter);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading feedback", e);
        }
    }

    private void setupClickListeners() {
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFeedback();
    }

    // Simple adapter for feedback list
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
            private TextView videoTitleText, coachNameText, feedbackDateText, feedbackSnippetText;
            private Button viewFeedbackBtn;

            public FeedbackViewHolder(@NonNull View itemView) {
                super(itemView);

                // Find views with null checks
                videoTitleText = itemView.findViewById(R.id.videoTitleText);
                coachNameText = itemView.findViewById(R.id.coachNameText);
                feedbackDateText = itemView.findViewById(R.id.feedbackDateText);
                feedbackSnippetText = itemView.findViewById(R.id.feedbackSnippetText);
                viewFeedbackBtn = itemView.findViewById(R.id.viewFeedbackBtn);

                // Log which views were found
                Log.d(TAG, "FeedbackViewHolder - videoTitleText: " + (videoTitleText != null));
                Log.d(TAG, "FeedbackViewHolder - coachNameText: " + (coachNameText != null));
                Log.d(TAG, "FeedbackViewHolder - feedbackDateText: " + (feedbackDateText != null));
                Log.d(TAG, "FeedbackViewHolder - feedbackSnippetText: " + (feedbackSnippetText != null));
                Log.d(TAG, "FeedbackViewHolder - viewFeedbackBtn: " + (viewFeedbackBtn != null));
            }

            public void bind(VideoSubmission submission) {
                try {
                    // Set video title safely
                    if (videoTitleText != null) {
                        videoTitleText.setText(submission.getTitle());
                    }

                    // Get feedback details
                    List<VideoFeedback> feedbacks = submission.getFeedbacks();
                    if (!feedbacks.isEmpty()) {
                        VideoFeedback latestFeedback = feedbacks.get(0);

                        // Set coach name safely
                        if (coachNameText != null) {
                            String coachName = latestFeedback.getCoachName();
                            if (coachName != null && !coachName.isEmpty()) {
                                coachNameText.setText("Coach: " + coachName);
                            } else {
                                coachNameText.setText("Coach: Unknown");
                            }
                        }

                        // Set feedback date safely
                        if (feedbackDateText != null) {
                            feedbackDateText.setText("Reviewed: " + formatDate(latestFeedback.getFeedbackDate()));
                        }

                        // Show snippet of text feedback safely
                        if (feedbackSnippetText != null) {
                            String feedbackText = latestFeedback.getFeedbackText();
                            if (feedbackText != null && !feedbackText.trim().isEmpty()) {
                                String snippet = feedbackText.length() > 100 ?
                                        feedbackText.substring(0, 100) + "..." : feedbackText;
                                feedbackSnippetText.setText(snippet);
                            } else {
                                feedbackSnippetText.setText("No comments provided");
                            }
                        }

                        // Add rating to snippet if available
                        int rating = latestFeedback.getRating();
                        if (rating > 0 && feedbackSnippetText != null) {
                            String ratingText = "Rating: " + rating + "/5 stars\n";
                            String currentText = feedbackSnippetText.getText().toString();
                            feedbackSnippetText.setText(ratingText + currentText);
                        }

                    } else {
                        // Fallback
                        if (coachNameText != null) coachNameText.setText("Coach: Unknown");
                        if (feedbackDateText != null) feedbackDateText.setText("Date: Unknown");
                        if (feedbackSnippetText != null) feedbackSnippetText.setText("No feedback available");
                    }

                    // Set click listener safely
                    if (viewFeedbackBtn != null) {
                        viewFeedbackBtn.setOnClickListener(v -> {
                            Intent intent = new Intent(StudentFeedbackListActivity.this, VideoPlayerActivity.class);
                            intent.putExtra("submissionId", submission.getSubmissionId());
                            intent.putExtra("studentId", studentId);
                            intent.putExtra("viewOnly", true);
                            intent.putExtra("showFeedback", true);
                            startActivity(intent);
                        });
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error binding feedback item", e);
                }
            }

            private String formatDate(String dateString) {
                if (dateString == null) return "Unknown";
                try {
                    String[] parts = dateString.split(" ");
                    if (parts.length > 0) {
                        return parts[0];
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error formatting date", e);
                }
                return "Unknown";
            }
        }
    }
}