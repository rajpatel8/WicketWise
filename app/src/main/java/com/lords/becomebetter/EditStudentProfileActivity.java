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

public class EditStudentProfileActivity extends AppCompatActivity {

    private TextInputLayout nameLayout, phoneLayout, ageLayout;
    private TextInputEditText nameEdit, phoneEdit, ageEdit;
    private Spinner skillLevelSpinner;
    private Button saveBtn, cancelBtn;
    private ImageButton backBtn;

    private DatabaseHelper databaseHelper;
    private Student currentStudent;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student_profile);

        databaseHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("userEmail");

        initializeViews();
        setupSpinner();
        loadCurrentData();
        setupClickListeners();
    }

    private void initializeViews() {
        nameLayout = findViewById(R.id.editStudentNameLayout);
        phoneLayout = findViewById(R.id.editStudentPhoneLayout);
        ageLayout = findViewById(R.id.editStudentAgeLayout);

        nameEdit = findViewById(R.id.editStudentNameEdit);
        phoneEdit = findViewById(R.id.editStudentPhoneEdit);
        ageEdit = findViewById(R.id.editStudentAgeEdit);

        skillLevelSpinner = findViewById(R.id.editStudentSkillSpinner);
        saveBtn = findViewById(R.id.saveStudentBtn);
        cancelBtn = findViewById(R.id.cancelStudentBtn);
        backBtn = findViewById(R.id.backBtn);
    }

    private void setupSpinner() {
        String[] skillLevels = {
                "Beginner (Just starting)",
                "Basic (Some experience)",
                "Intermediate (Regular player)",
                "Advanced (Competitive level)",
                "Expert (Professional level)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, skillLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillLevelSpinner.setAdapter(adapter);
    }

    private void loadCurrentData() {
        currentStudent = databaseHelper.getStudentByEmail(userEmail);
        if (currentStudent == null) {
            showError("Student profile not found");
            finish();
            return;
        }

        // Populate fields
        nameEdit.setText(currentStudent.getName());
        phoneEdit.setText(currentStudent.getPhone());
        ageEdit.setText(String.valueOf(currentStudent.getAge()));

        // Set spinner selection
        String skillLevel = currentStudent.getSkillLevel();
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) skillLevelSpinner.getAdapter();
        int position = adapter.getPosition(skillLevel);
        if (position >= 0) {
            skillLevelSpinner.setSelection(position);
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

        // Update student object
        currentStudent.setName(nameEdit.getText().toString().trim());
        currentStudent.setPhone(phoneEdit.getText().toString().trim());
        currentStudent.setAge(Integer.parseInt(ageEdit.getText().toString().trim()));
        currentStudent.setSkillLevel(skillLevelSpinner.getSelectedItem().toString());

        // Save to database
        boolean success = databaseHelper.updateStudent(currentStudent);

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
        ageLayout.setError(null);

        // Validate name
        String name = nameEdit.getText().toString().trim();
        if (name.isEmpty()) {
            nameLayout.setError("Name is required");
            isValid = false;
        }

        // Validate age
        String ageStr = ageEdit.getText().toString().trim();
        if (ageStr.isEmpty()) {
            ageLayout.setError("Age is required");
            isValid = false;
        } else {
            try {
                int age = Integer.parseInt(ageStr);
                if (age < 5 || age > 100) {
                    ageLayout.setError("Age must be between 5-100 years");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                ageLayout.setError("Please enter a valid age");
                isValid = false;
            }
        }

        return isValid;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Return to student profile
        Intent intent = new Intent(this, StudentProfileActivity.class);
        intent.putExtra("userEmail", userEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}