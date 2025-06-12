package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ViewStudentsActivity extends AppCompatActivity {

    private RecyclerView studentsRecyclerView;
    private LinearLayout noStudentsLayout;
    private ImageButton backBtn;
    private TextView headerTitle, studentCountText;

    private DatabaseHelper databaseHelper;
    private StudentListAdapter studentAdapter;
    private List<Student> studentsList;
    private String coachEmail;
    private int coachId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_students);

        databaseHelper = new DatabaseHelper(this);
        coachEmail = getIntent().getStringExtra("coachEmail");

        initializeViews();
        loadCoachData();
        loadStudents();
        setupClickListeners();
    }

    private void initializeViews() {
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        noStudentsLayout = findViewById(R.id.noStudentsLayout);
        backBtn = findViewById(R.id.backBtn);
        headerTitle = findViewById(R.id.headerTitle);
        studentCountText = findViewById(R.id.studentCountText);

        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCoachData() {
        Coach coach = databaseHelper.getCoachByEmail(coachEmail);
        if (coach == null) {
            showError("Coach profile not found");
            finish();
            return;
        }
        coachId = coach.getId();
    }

    private void loadStudents() {
        studentsList = databaseHelper.getStudentsByCoachId(coachId);

        if (studentsList.isEmpty()) {
            studentsRecyclerView.setVisibility(View.GONE);
            noStudentsLayout.setVisibility(View.VISIBLE);
            studentCountText.setText("No students assigned");
        } else {
            studentsRecyclerView.setVisibility(View.VISIBLE);
            noStudentsLayout.setVisibility(View.GONE);

            String countText = studentsList.size() == 1 ?
                    "1 Student" : studentsList.size() + " Students";
            studentCountText.setText(countText);

            studentAdapter = new StudentListAdapter(studentsList);
            studentsRecyclerView.setAdapter(studentAdapter);
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    // Student List RecyclerView Adapter
    private class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentViewHolder> {

        private List<Student> students;

        public StudentListAdapter(List<Student> students) {
            this.students = students;
        }

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_student_view, parent, false);
            return new StudentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
            Student student = students.get(position);
            holder.bind(student);
        }

        @Override
        public int getItemCount() {
            return students.size();
        }

        class StudentViewHolder extends RecyclerView.ViewHolder {

            private TextView nameText, ageText, skillLevelText, emailText, phoneText, joinedDateText;
            private ImageView studentIcon;
            private View skillLevelIndicator;

            public StudentViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.studentNameText);
                ageText = itemView.findViewById(R.id.studentAgeText);
                skillLevelText = itemView.findViewById(R.id.studentSkillLevelText);
                emailText = itemView.findViewById(R.id.studentEmailText);
                phoneText = itemView.findViewById(R.id.studentPhoneText);
                joinedDateText = itemView.findViewById(R.id.studentJoinedDateText);
                studentIcon = itemView.findViewById(R.id.studentIcon);
                skillLevelIndicator = itemView.findViewById(R.id.skillLevelIndicator);
            }

            public void bind(Student student) {
                nameText.setText(student.getName());
                ageText.setText(student.getAge() + " years old");
                skillLevelText.setText(student.getSkillLevel());
                emailText.setText(student.getEmail());

                // Handle phone (optional field)
                if (student.getPhone() != null && !student.getPhone().trim().isEmpty()) {
                    phoneText.setText(student.getPhone());
                    phoneText.setVisibility(View.VISIBLE);
                } else {
                    phoneText.setVisibility(View.GONE);
                }

                // Format joined date
                if (student.getCreatedAt() != null) {
                    String joinedDate = formatDate(student.getCreatedAt());
                    joinedDateText.setText("Joined: " + joinedDate);
                } else {
                    joinedDateText.setText("Joined: Unknown");
                }

                // Set skill level indicator color
                setSkillLevelIndicator(student.getSkillLevel());

                // Click to view details (future enhancement)
                itemView.setOnClickListener(v -> {
                    Toast.makeText(ViewStudentsActivity.this,
                            "Student details view coming soon!", Toast.LENGTH_SHORT).show();
                });
            }

            private void setSkillLevelIndicator(String skillLevel) {
                int color;
                if (skillLevel.contains("Beginner")) {
                    color = getResources().getColor(R.color.error_color);
                } else if (skillLevel.contains("Basic")) {
                    color = getResources().getColor(R.color.warning_color);
                } else if (skillLevel.contains("Intermediate")) {
                    color = getResources().getColor(R.color.cricket_green_secondary);
                } else if (skillLevel.contains("Advanced")) {
                    color = getResources().getColor(R.color.cricket_green_primary);
                } else { // Expert
                    color = getResources().getColor(R.color.cricket_green_dark);
                }
                skillLevelIndicator.setBackgroundColor(color);
            }

            private String formatDate(String dateString) {
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

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Return to coach dashboard
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("userType", "coach");
        intent.putExtra("userEmail", coachEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}