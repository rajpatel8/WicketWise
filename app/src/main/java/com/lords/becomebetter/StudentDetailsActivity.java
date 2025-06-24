package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDetailsActivity extends AppCompatActivity {

    private TextView studentNameText, studentEmailText, studentAgeText,
            studentSkillText, studentPhoneText, joinDateText;
    private ImageButton backBtn;
    private Button viewVideosBtn, sendMessageBtn;

    private DatabaseHelper databaseHelper;
    private Student student;
    private String coachEmail;
    private int coachId, studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        databaseHelper = new DatabaseHelper(this);

        // Get intent data
        studentId = getIntent().getIntExtra("studentId", 0);
        coachEmail = getIntent().getStringExtra("coachEmail");
        coachId = getIntent().getIntExtra("coachId", 0);

        initializeViews();
        loadStudentData();
        setupClickListeners();
    }

    private void initializeViews() {
        studentNameText = findViewById(R.id.studentNameText);
        studentEmailText = findViewById(R.id.studentEmailText);
        studentAgeText = findViewById(R.id.studentAgeText);
        studentSkillText = findViewById(R.id.studentSkillText);
        studentPhoneText = findViewById(R.id.studentPhoneText);
        joinDateText = findViewById(R.id.joinDateText);
        backBtn = findViewById(R.id.backBtn);
        viewVideosBtn = findViewById(R.id.viewVideosBtn);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
    }

    private void loadStudentData() {
        student = databaseHelper.getStudentById(studentId);

        if (student != null) {
            studentNameText.setText(student.getName());
            studentEmailText.setText(student.getEmail());
            studentAgeText.setText("Age: " + student.getAge());
            studentSkillText.setText("Skill Level: " + student.getSkillLevel());
            studentPhoneText.setText("Phone: " + (student.getPhone() != null ? student.getPhone() : "Not provided"));
            joinDateText.setText("Joined: " + formatDate(student.getCreatedAt()));
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());

        viewVideosBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, VideoListActivity.class);
            intent.putExtra("coachEmail", coachEmail);
            intent.putExtra("studentId", studentId);
            intent.putExtra("studentName", student.getName());
            startActivity(intent);
        });

        sendMessageBtn.setOnClickListener(v -> {
            // TODO: Implement messaging feature
            // For now, just show a placeholder
            android.widget.Toast.makeText(this, "Messaging feature coming soon!",
                    android.widget.Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}