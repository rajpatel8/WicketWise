package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditCoachProfileActivity extends AppCompatActivity {

    private TextInputLayout nameLayout, phoneLayout, experienceLayout, certificationLayout;
    private TextInputEditText nameEdit, phoneEdit, experienceEdit, certificationEdit;
    private Spinner specializationSpinner;
    private Button saveBtn, cancelBtn;
    private ImageButton backBtn;

    private DatabaseHelper databaseHelper;
    private Coach currentCoach;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_coach_profile);

        databaseHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("userEmail");

        initializeViews();
        setupSpinner();
        loadCurrentData();
        setupClickListeners();
    }

    private void initializeViews() {
        nameLayout = findViewById(R.id.editCoachNameLayout);
        phoneLayout = findViewById(R.id.editCoachPhoneLayout);
        experienceLayout = findViewById(R.id.editCoachExperienceLayout);
        certificationLayout = findViewById(R.id.editCoachCertificationLayout);

        nameEdit = findViewById(R.id.editCoachNameEdit);
        phoneEdit = findViewById(R.id.editCoachPhoneEdit);
        experienceEdit = findViewById(R.id.editCoachExperienceEdit);
        certificationEdit = findViewById(R.id.editCoachCertificationEdit);

        specializationSpinner = findViewById(R.id.editCoachSpecializationSpinner);
        saveBtn = findViewById(R.id.saveCoachBtn);
        cancelBtn = findViewById(R.id.cancelCoachBtn);
        backBtn = findViewById(R.id.backBtn);
    }

    private void setupSpinner() {
        String[] specializations = {
                "Batting Coach",
                "Bowling Coach",
                "Wicket Keeping Coach",
                "Fielding Coach",
                "All-Rounder Coach",
                "Fitness & Conditioning",
                "Mental Performance Coach",
                "Youth Development",
                "Professional Cricket"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, specializations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specializationSpinner.setAdapter(adapter);
    }

    private void loadCurrentData() {
        currentCoach = databaseHelper.getCoachByEmail(userEmail);
        if (currentCoach == null) {
            showError("Coach profile not found");
            finish();
            return;
        }

        // Populate fields
        nameEdit.setText(currentCoach.getName());
        phoneEdit.setText(currentCoach.getPhone());
        experienceEdit.setText(String.valueOf(currentCoach.getExperienceYears()));
        certificationEdit.setText(currentCoach.getCertification());

        // Set spinner selection
        String specialization = currentCoach.getSpecialization();
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) specializationSpinner.getAdapter();
        int position = adapter.getPosition(specialization);
        if (position >= 0) {
            specializationSpinner.setSelection(position);
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
        cancelBtn.setOnClickListener(v -> onBackPressed());
        saveBtn.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        if (!validateInputs()) {
            return;
        }

        // Update coach object
        currentCoach.setName(nameEdit.getText().toString().trim());
        currentCoach.setPhone(phoneEdit.getText().toString().trim());
        currentCoach.setExperienceYears(Integer.parseInt(experienceEdit.getText().toString().trim()));
        currentCoach.setSpecialization(specializationSpinner.getSelectedItem().toString());
        currentCoach.setCertification(certificationEdit.getText().toString().trim());

        // Save to database
        boolean success = databaseHelper.updateCoach(currentCoach);

        if (success) {
            setResult(RESULT_OK);
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            showError("Failed to update profile. Please try again.");
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Clear previous errors
        nameLayout.setError(null);
        experienceLayout.setError(null);

        // Validate name
        String name = nameEdit.getText().toString().trim();
        if (name.isEmpty()) {
            nameLayout.setError("Name is required");
            isValid = false;
        }

        // Validate experience
        String experienceStr = experienceEdit.getText().toString().trim();
        if (experienceStr.isEmpty()) {
            experienceLayout.setError("Experience is required");
            isValid = false;
        } else {
            try {
                int experience = Integer.parseInt(experienceStr);
                if (experience < 0 || experience > 50) {
                    experienceLayout.setError("Experience must be between 0-50 years");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                experienceLayout.setError("Please enter a valid number");
                isValid = false;
            }
        }

        return isValid;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}