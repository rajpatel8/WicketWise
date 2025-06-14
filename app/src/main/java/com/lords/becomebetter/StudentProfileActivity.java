package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentProfileActivity extends AppCompatActivity {

    private TextView nameText, emailText, phoneText, ageText,
            skillLevelText, coachText, joinedDateText;
    private Button editProfileBtn, findCoachBtn;
    private ImageButton backBtn;  // Changed from Button to ImageButton
    private ImageView profileIcon;

    private Button uploadVideoBtn;

    private DatabaseHelper databaseHelper;
    private Student currentStudent;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        databaseHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("userEmail");

        initializeViews();
        loadStudentProfile();
        setupClickListeners();
    }

    private void initializeViews() {
        // Profile info views
        uploadVideoBtn = findViewById(R.id.uploadVideoBtn);
        nameText = findViewById(R.id.studentNameText);
        emailText = findViewById(R.id.studentEmailText);
        phoneText = findViewById(R.id.studentPhoneText);
        ageText = findViewById(R.id.studentAgeText);
        skillLevelText = findViewById(R.id.studentSkillLevelText);
        coachText = findViewById(R.id.studentCoachText);
        joinedDateText = findViewById(R.id.studentJoinedDateText);

        // Buttons and icons
        editProfileBtn = findViewById(R.id.editProfileBtn);
        findCoachBtn = findViewById(R.id.findCoachBtn);
        backBtn = findViewById(R.id.backBtn);
        profileIcon = findViewById(R.id.profileIcon);

        // DEBUG: Check if uploadVideoBtn is found
        if (uploadVideoBtn == null) {
            Toast.makeText(this, "ERROR: Upload video button not found in layout!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "SUCCESS: Upload video button found!", Toast.LENGTH_SHORT).show();
            // Force visibility for debugging
            uploadVideoBtn.setVisibility(View.VISIBLE);
        }
    }


    private void loadStudentProfile() {
        if (userEmail == null || userEmail.isEmpty()) {
            showError("User email not found");
            finish();
            return;
        }

        currentStudent = databaseHelper.getStudentByEmail(userEmail);

        if (currentStudent == null) {
            showError("Student profile not found");
            finish();
            return;
        }

        populateProfileData();
    }

    private void populateProfileData() {
        nameText.setText(currentStudent.getName());
        emailText.setText(currentStudent.getEmail());

        // Handle phone (optional field)
        String phone = currentStudent.getPhone();
        if (phone == null || phone.trim().isEmpty()) {
            phoneText.setText("Not provided");
            phoneText.setTextColor(getResources().getColor(R.color.text_secondary));
        } else {
            phoneText.setText(phone);
            phoneText.setTextColor(getResources().getColor(R.color.text_primary));
        }

        // Age
        ageText.setText(currentStudent.getAge() + " years old");

        // Skill Level
        skillLevelText.setText(currentStudent.getSkillLevel());

        // Coach assignment
        String coachName = databaseHelper.getCoachNameById(currentStudent.getCoachId());
        coachText.setText(coachName);

        // Show/hide buttons based on coach assignment
        if (currentStudent.getCoachId() == 0) {
            findCoachBtn.setVisibility(View.VISIBLE);
            uploadVideoBtn.setVisibility(View.GONE); // Hide upload button when no coach
            coachText.setTextColor(getResources().getColor(R.color.text_secondary));
        } else {
            findCoachBtn.setVisibility(View.GONE);
            uploadVideoBtn.setVisibility(View.VISIBLE); // Show upload button when coach assigned
            coachText.setTextColor(getResources().getColor(R.color.text_primary));
        }

        // Joined date
        String joinedDate = formatJoinedDate(currentStudent.getCreatedAt());
        joinedDateText.setText("Member since " + joinedDate);
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());

        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditStudentProfileActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivityForResult(intent, 100);
        });

        uploadVideoBtn.setOnClickListener(v -> {
            if (currentStudent.getCoachId() == 0) {
                Toast.makeText(this, "Please assign a coach first before uploading videos",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(this, VideoUploadActivity.class);
            intent.putExtra("studentEmail", userEmail);
            startActivityForResult(intent, 200);
        });

        findCoachBtn.setOnClickListener(v -> {
            // Add debug logging
            Toast.makeText(this, "Button clicked! Opening coach selection...", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, SimpleFindCoachActivity.class);
            intent.putExtra("studentEmail", userEmail);
            intent.putExtra("studentId", currentStudent.getId());

            try {
                startActivity(intent);
                Toast.makeText(this, "Activity started successfully!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Profile was updated, reload the data
            loadStudentProfile();
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            // Video was uploaded successfully
            Toast.makeText(this, "Video uploaded successfully! Your coach will review it soon.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile in case coach assignment changed
        loadStudentProfile();
    }

    private String formatJoinedDate(String dateString) {
        if (dateString == null) return "Unknown";

        try {
            // Simple date formatting - you can enhance this
            // Input format: "2025-01-15 10:30:45"
            String[] parts = dateString.split(" ");
            if (parts.length > 0) {
                return parts[0]; // Return just the date part
            }
        } catch (Exception e) {
            // Handle parsing errors gracefully
        }

        return "Unknown";
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Return to dashboard
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("userType", "student");
        intent.putExtra("userEmail", userEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



}