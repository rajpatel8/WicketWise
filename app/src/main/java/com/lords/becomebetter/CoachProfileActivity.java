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

public class CoachProfileActivity extends AppCompatActivity {

    private TextView nameText, emailText, phoneText, experienceText,
            specializationText, certificationText, joinedDateText;
    private Button editProfileBtn;
    private ImageButton backBtn;  // Changed from Button to ImageButton
    private ImageView profileIcon;

    private DatabaseHelper databaseHelper;
    private Coach currentCoach;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_profile);

        databaseHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("userEmail");

        initializeViews();
        loadCoachProfile();
        setupClickListeners();
    }

    private void initializeViews() {
        // Profile info views
        nameText = findViewById(R.id.coachNameText);
        emailText = findViewById(R.id.coachEmailText);
        phoneText = findViewById(R.id.coachPhoneText);
        experienceText = findViewById(R.id.coachExperienceText);
        specializationText = findViewById(R.id.coachSpecializationText);
        certificationText = findViewById(R.id.coachCertificationText);
        joinedDateText = findViewById(R.id.coachJoinedDateText);

        // Buttons and icons
        editProfileBtn = findViewById(R.id.editProfileBtn);
        backBtn = findViewById(R.id.backBtn);  // This is an ImageButton
        profileIcon = findViewById(R.id.profileIcon);
    }

    private void loadCoachProfile() {
        if (userEmail == null || userEmail.isEmpty()) {
            showError("User email not found");
            finish();
            return;
        }

        currentCoach = databaseHelper.getCoachByEmail(userEmail);

        if (currentCoach == null) {
            showError("Coach profile not found");
            finish();
            return;
        }

        populateProfileData();
    }

    private void populateProfileData() {
        nameText.setText(currentCoach.getName());
        emailText.setText(currentCoach.getEmail());

        // Handle phone (optional field)
        String phone = currentCoach.getPhone();
        if (phone == null || phone.trim().isEmpty()) {
            phoneText.setText("Not provided");
            phoneText.setTextColor(getResources().getColor(R.color.text_secondary));
        } else {
            phoneText.setText(phone);
            phoneText.setTextColor(getResources().getColor(R.color.text_primary));
        }

        // Experience
        experienceText.setText(currentCoach.getExperienceYears() + " years");

        // Specialization
        specializationText.setText(currentCoach.getSpecialization());

        // Certification (optional field)
        String certification = currentCoach.getCertification();
        if (certification == null || certification.trim().isEmpty()) {
            certificationText.setText("No certifications listed");
            certificationText.setTextColor(getResources().getColor(R.color.text_secondary));
        } else {
            certificationText.setText(certification);
            certificationText.setTextColor(getResources().getColor(R.color.text_primary));
        }

        // Joined date
        String joinedDate = formatJoinedDate(currentCoach.getCreatedAt());
        joinedDateText.setText("Member since " + joinedDate);
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());

        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditCoachProfileActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivityForResult(intent, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Profile was updated, reload the data
            loadCoachProfile();
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        }
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
        intent.putExtra("userType", "coach");
        intent.putExtra("userEmail", userEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}