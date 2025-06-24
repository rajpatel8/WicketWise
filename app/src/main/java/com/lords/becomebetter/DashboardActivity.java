package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView welcomeText, userNameText, userTypeText;
    private Button logoutBtn, viewProfileBtn, editProfileBtn;
    private Button viewStudentsBtn, manageSessionsBtn, findCoachBtn, bookSessionBtn;
    private LinearLayout coachActionsLayout, studentActionsLayout;

    private String userType;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getUserInfo();
        initializeViews();
        setupUserInterface();
        setupClickListeners();
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
            viewStudentsBtn.setOnClickListener(v -> viewMyStudents()); // Updated to use new activity
        }
        if (manageSessionsBtn != null) {
            manageSessionsBtn.setOnClickListener(v -> viewStudentRequests()); // Updated to handle requests
        }

        // Student-specific actions
        if (findCoachBtn != null) {
            findCoachBtn.setOnClickListener(v -> findCoach()); // Already implemented
        }
        if (bookSessionBtn != null) {
            bookSessionBtn.setOnClickListener(v -> viewMyRequests()); // Updated to view request status
        }
    }

    private void performLogout() {
        // Clear any saved session data if you implement it later
        // For now, just return to login screen
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void viewVideos() {
        Intent intent = new Intent(this, VideoListActivity.class);
        intent.putExtra("coachEmail", userEmail);
        startActivity(intent);
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

    private void manageSessions() {
        // For now, redirect to video management
        viewVideos();
    }

    private void viewStudents() {
        // Remove the "coming soon" message and implement the actual functionality
        Intent intent = new Intent(this, ViewStudentsActivity.class);
        intent.putExtra("coachEmail", userEmail);
        startActivity(intent);
    }


//    private void findCoach() {
//        // Navigate to SimpleFindCoachActivity for students
//        Intent intent = new Intent(this, SimpleFindCoachActivity.class);
//        intent.putExtra("studentEmail", userEmail);
//        // We need to get the student ID, but for now we can pass 0
//        intent.putExtra("studentId", 0);
//        startActivity(intent);
//    }

    private void bookSession() {
        showComingSoonMessage("Book Session");
        // TODO: Implement session booking for students
        // Intent intent = new Intent(this, BookSessionActivity.class);
        // intent.putExtra("studentEmail", userEmail);
        // startActivity(intent);
    }

    private void showComingSoonMessage(String feature) {
        Toast.makeText(this, feature + " feature coming in next update! 🚀", Toast.LENGTH_SHORT).show();
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
        // Extract name part before @ symbol
        String namePart = email.split("@")[0];
        // Replace dots and underscores with spaces and capitalize
        return namePart.replace(".", " ").replace("_", " ");
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to login screen accidentally
        // Show confirmation dialog or just ignore
        super.onBackPressed();
        Toast.makeText(this, "Use logout button to exit", Toast.LENGTH_SHORT).show();
    }

    // Updated Coach methods
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

    // Updated Student methods
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

}