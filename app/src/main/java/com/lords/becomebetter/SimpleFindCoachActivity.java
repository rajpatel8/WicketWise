package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class SimpleFindCoachActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private String studentEmail;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a simple layout programmatically
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 50, 50, 50);

        // Add title
        TextView title = new TextView(this);
        title.setText("Available Coaches");
        title.setTextSize(20);
        title.setPadding(0, 0, 0, 30);
        mainLayout.addView(title);

        // Initialize database and get intent data
        databaseHelper = new DatabaseHelper(this);
        studentEmail = getIntent().getStringExtra("studentEmail");
        studentId = getIntent().getIntExtra("studentId", 0);

        // Load and display coaches
        List<Coach> coaches = databaseHelper.getAllCoaches();

        if (coaches.isEmpty()) {
            TextView noCoaches = new TextView(this);
            noCoaches.setText("No coaches available yet.");
            noCoaches.setTextSize(16);
            mainLayout.addView(noCoaches);
        } else {
            for (Coach coach : coaches) {
                // Create a simple coach item
                LinearLayout coachItem = new LinearLayout(this);
                coachItem.setOrientation(LinearLayout.VERTICAL);
                coachItem.setPadding(20, 20, 20, 20);
                coachItem.setBackgroundColor(0xFFE8E8E8);

                // Coach info
                TextView coachName = new TextView(this);
                coachName.setText(coach.getName());
                coachName.setTextSize(18);
                coachName.setTextColor(0xFF2E7D32);

                TextView coachDetails = new TextView(this);
                coachDetails.setText(coach.getSpecialization() + " - " + coach.getExperienceYears() + " years");
                coachDetails.setTextSize(14);
                coachDetails.setTextColor(0xFF666666);

                // Select button
                TextView selectBtn = new TextView(this);
                selectBtn.setText("SELECT THIS COACH");
                selectBtn.setTextSize(16);
                selectBtn.setTextColor(0xFFFFFFFF);
                selectBtn.setBackgroundColor(0xFF2E7D32);
                selectBtn.setPadding(30, 20, 30, 20);
                selectBtn.setOnClickListener(v -> selectCoach(coach));

                coachItem.addView(coachName);
                coachItem.addView(coachDetails);
                coachItem.addView(selectBtn);

                mainLayout.addView(coachItem);

                // Add some spacing
                TextView spacer = new TextView(this);
                spacer.setHeight(30);
                mainLayout.addView(spacer);
            }
        }

        // Back button
        TextView backBtn = new TextView(this);
        backBtn.setText("← BACK TO PROFILE");
        backBtn.setTextSize(16);
        backBtn.setTextColor(0xFF2E7D32);
        backBtn.setPadding(0, 30, 0, 0);
        backBtn.setOnClickListener(v -> onBackPressed());
        mainLayout.addView(backBtn);

        setContentView(mainLayout);
    }

    private void selectCoach(Coach coach) {
        // Update student's coach assignment
        Student student = databaseHelper.getStudentByEmail(studentEmail);
        if (student != null) {
            student.setCoachId(coach.getId());
            boolean success = databaseHelper.updateStudent(student);

            if (success) {
                Toast.makeText(this, "Coach " + coach.getName() + " assigned successfully!",
                        Toast.LENGTH_SHORT).show();
                finish(); // Return to profile
            } else {
                Toast.makeText(this, "Failed to assign coach. Please try again.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Return to student profile
        Intent intent = new Intent(this, StudentProfileActivity.class);
        intent.putExtra("userEmail", studentEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}