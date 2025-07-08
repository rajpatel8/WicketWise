package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView welcomeText, userNameText, userTypeText;
    private Button logoutBtn, viewProfileBtn, editProfileBtn;
    private Button viewStudentsBtn, manageSessionsBtn, findCoachBtn, bookSessionBtn;

    // ADD THESE MISSING STUDENT VIDEO BUTTONS:
    private Button uploadVideoBtn, viewFeedbackBtn;

    private LinearLayout coachActionsLayout, studentActionsLayout;

    private String userType;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseHelper = new DatabaseHelper(this);

        getUserInfo();
        initializeViews();
        setupUserInterface();
        setupClickListeners();

        // Update student dashboard if it's a student
        if ("student".equals(userType)) {
            updateStudentDashboard();
        }
    }

    private void getUserInfo() {
        // Get user information passed from login
        Intent intent = getIntent();
        userType = intent.getStringExtra("userType");
        userEmail = intent.getStringExtra("userEmail");

        // Default values if not provided
        if (userType == null) userType = "student";
        if (userEmail == null) userEmail = "user@example.com";
    }

    private void initializeViews() {
        // Header views
        welcomeText = findViewById(R.id.welcomeText);
        userNameText = findViewById(R.id.userNameText);
        userTypeText = findViewById(R.id.userTypeText);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Action buttons
        viewProfileBtn = findViewById(R.id.viewProfileBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);

        // Coach-specific buttons
        viewStudentsBtn = findViewById(R.id.viewStudentsBtn);
        manageSessionsBtn = findViewById(R.id.manageSessionsBtn);

        // Student-specific buttons
        findCoachBtn = findViewById(R.id.findCoachBtn);
        bookSessionBtn = findViewById(R.id.bookSessionBtn);

        // ADD THESE MISSING STUDENT VIDEO BUTTONS:
        uploadVideoBtn = findViewById(R.id.uploadVideoBtn);
        viewFeedbackBtn = findViewById(R.id.viewFeedbackBtn);

        // Layouts for conditional visibility
        coachActionsLayout = findViewById(R.id.coachActionsLayout);
        studentActionsLayout = findViewById(R.id.studentActionsLayout);
    }

    private void setupUserInterface() {
        // Set welcome message
        String welcomeMessage = "Welcome back, " + capitalizeUserType(userType) + "!";
        welcomeText.setText(welcomeMessage);

        // Set user info
        userNameText.setText(extractNameFromEmail(userEmail));
        userTypeText.setText(capitalizeUserType(userType));

        // Show/hide buttons based on user type
        if ("coach".equals(userType)) {
            coachActionsLayout.setVisibility(View.VISIBLE);
            studentActionsLayout.setVisibility(View.GONE);
        } else {
            coachActionsLayout.setVisibility(View.GONE);
            studentActionsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        // Common actions
        logoutBtn.setOnClickListener(v -> performLogout());
        viewProfileBtn.setOnClickListener(v -> viewProfile());
        editProfileBtn.setOnClickListener(v -> editProfile());

        // Coach-specific actions
        if (viewStudentsBtn != null) {
            viewStudentsBtn.setOnClickListener(v -> viewMyStudents());
        }
        if (manageSessionsBtn != null) {
            manageSessionsBtn.setOnClickListener(v -> viewStudentRequests());
        }

        // Student-specific actions
        if (findCoachBtn != null) {
            findCoachBtn.setOnClickListener(v -> findCoach());
        }
        if (bookSessionBtn != null) {
            bookSessionBtn.setOnClickListener(v -> viewMyRequests());
        }

        // ADD THESE MISSING STUDENT VIDEO CLICK LISTENERS:
        if (uploadVideoBtn != null) {
            uploadVideoBtn.setOnClickListener(v -> uploadVideo());
        }
        if (viewFeedbackBtn != null) {
            viewFeedbackBtn.setOnClickListener(v -> viewMyFeedback());
        }
    }

    // STUDENT VIDEO FUNCTIONALITY
    private void uploadVideo() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Student student = databaseHelper.getStudentByEmail(userEmail);

        if (student != null) {
            // Check if student has assigned coaches
            List<Coach> availableCoaches = databaseHelper.getCoachesForStudent(student.getId());

            if (availableCoaches.isEmpty()) {
                // Show dialog to guide student to find coaches first
                showFindCoachDialog();
                return;
            }

            // Launch enhanced video upload activity
            Intent intent = new Intent(this, EnhancedVideoUploadActivity.class);
            intent.putExtra("studentEmail", userEmail);
            intent.putExtra("studentId", student.getId());
            intent.putExtra("studentName", student.getName());
            startActivityForResult(intent, 300);

        } else {
            Toast.makeText(this, "Student profile not found", Toast.LENGTH_SHORT).show();
        }
    }

//    private void viewMyFeedback() {
//        Intent intent = new Intent(this, StudentFeedbackListActivity.class);
//        intent.putExtra("studentEmail", userEmail);
//        startActivity(intent);
//    }

    private void viewMyFeedback() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Student student = databaseHelper.getStudentByEmail(userEmail);

        if (student != null) {
            Intent intent = new Intent(this, StudentFeedbackListActivity.class);
            intent.putExtra("studentEmail", userEmail);
            intent.putExtra("studentId", student.getId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Student profile not found", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateStudentDashboard() {
        if ("student".equals(userType)) {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            Student student = databaseHelper.getStudentByEmail(userEmail);

            if (student != null) {
                // Get feedback count
                int feedbackCount = databaseHelper.getFeedbackCountForStudent(student.getId());

                // Update UI to show feedback count
                if (viewFeedbackBtn != null && feedbackCount > 0) {
                    viewFeedbackBtn.setText("Feedback (" + feedbackCount + ")");
                }

                // Get pending uploads count
                int pendingUploads = databaseHelper.getPendingSubmissionsCount(student.getId());

                // Show dashboard stats
                updateStudentStats(feedbackCount, pendingUploads);
            }
        }
    }

    private void updateStudentStats(int feedbackCount, int pendingUploads) {
        // Find stats text views
        TextView feedbackCountText = findViewById(R.id.feedbackCountText);
        TextView uploadsCountText = findViewById(R.id.uploadsCountText);

        if (feedbackCountText != null) {
            feedbackCountText.setText(String.valueOf(feedbackCount));
        }

        if (uploadsCountText != null) {
            uploadsCountText.setText(String.valueOf(pendingUploads));
        }
    }

    // COACH FUNCTIONALITY
    private void viewMyStudents() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Coach coach = databaseHelper.getCoachByEmail(userEmail);

        if (coach != null) {
            Intent intent = new Intent(this, CoachStudentsActivity.class);
            intent.putExtra("coachEmail", userEmail);
            intent.putExtra("coachId", coach.getId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Coach profile not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewStudentRequests() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Coach coach = databaseHelper.getCoachByEmail(userEmail);

        if (coach != null) {
            Intent intent = new Intent(this, CoachRequestsActivity.class);
            intent.putExtra("coachEmail", userEmail);
            intent.putExtra("coachId", coach.getId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Coach profile not found", Toast.LENGTH_SHORT).show();
        }
    }

    // STUDENT FUNCTIONALITY
    private void findCoach() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Student student = databaseHelper.getStudentByEmail(userEmail);

        if (student != null) {
            Intent intent = new Intent(this, FindCoachActivity.class);
            intent.putExtra("studentEmail", userEmail);
            intent.putExtra("studentId", student.getId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Student profile not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewMyRequests() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Student student = databaseHelper.getStudentByEmail(userEmail);

        if (student != null) {
            Intent intent = new Intent(this, StudentRequestsActivity.class);
            intent.putExtra("studentEmail", userEmail);
            intent.putExtra("studentId", student.getId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Student profile not found", Toast.LENGTH_SHORT).show();
        }
    }

    // HELPER METHODS
    private void showFindCoachDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("No Coaches Available")
                .setMessage("You need to connect with coaches before uploading videos for feedback. Would you like to find coaches now?")
                .setPositiveButton("Find Coaches", (dialog, which) -> {
                    // Navigate to find coach activity
                    Intent intent = new Intent(this, SimpleFindCoachActivity.class);
                    intent.putExtra("studentEmail", userEmail);
                    DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    Student student = databaseHelper.getStudentByEmail(userEmail);
                    if (student != null) {
                        intent.putExtra("studentId", student.getId());
                    }
                    startActivity(intent);
                })
                .setNegativeButton("Later", null)
                .show();
    }

    private void performLogout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void viewProfile() {
        if ("coach".equals(userType)) {
            Intent intent = new Intent(this, CoachProfileActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, StudentProfileActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        }
    }

    private void editProfile() {
        if ("coach".equals(userType)) {
            Intent intent = new Intent(this, EditCoachProfileActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, EditStudentProfileActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        }
    }

    private String capitalizeUserType(String type) {
        if (type == null || type.isEmpty()) {
            return "User";
        }
        return type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
    }

    private String extractNameFromEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "User";
        }
        String namePart = email.split("@")[0];
        return namePart.replace(".", " ").replace("_", " ");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Use logout button to exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 300 && resultCode == RESULT_OK) {
            // Video upload was successful
            Toast.makeText(this, "🎥 Video uploaded successfully! Your coaches will review it soon.",
                    Toast.LENGTH_LONG).show();

            // Refresh student dashboard
            if ("student".equals(userType)) {
                updateStudentDashboard();
            }

            // Show additional success feedback
            showVideoUploadSuccessSnackbar();
        }
    }

    private void showVideoUploadSuccessSnackbar() {
        try {
            com.google.android.material.snackbar.Snackbar snackbar =
                    com.google.android.material.snackbar.Snackbar.make(
                            findViewById(android.R.id.content),
                            "Video uploaded! Coaches will provide feedback soon.",
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                    );

            snackbar.setAction("View Profile", v -> {
                Intent intent = new Intent(this, StudentProfileActivity.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            });

            snackbar.show();
        } catch (Exception e) {
            Toast.makeText(this, "Check your profile to see uploaded videos", Toast.LENGTH_SHORT).show();
        }
    }
}