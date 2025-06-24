package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CoachStudentsActivity extends AppCompatActivity {

    private RecyclerView studentsRecyclerView;
    private TextView noStudentsText, titleText, studentCountText;
    private ImageButton backBtn;

    private DatabaseHelper databaseHelper;
    private StudentAdapter studentAdapter;
    private List<Student> studentsList;
    private String coachEmail;
    private int coachId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_students);

        databaseHelper = new DatabaseHelper(this);
        coachEmail = getIntent().getStringExtra("coachEmail");
        coachId = getIntent().getIntExtra("coachId", 0);

        initializeViews();
        loadStudents();
        setupClickListeners();
        updateUI();
    }

    private void initializeViews() {
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        noStudentsText = findViewById(R.id.noStudentsText);
        backBtn = findViewById(R.id.backBtn);
        titleText = findViewById(R.id.titleText);
        studentCountText = findViewById(R.id.studentCountText);

        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (titleText != null) {
            titleText.setText("My Students");
        }
    }

    private void updateUI() {
        int studentCount = studentsList != null ? studentsList.size() : 0;
        if (studentCountText != null) {
            if (studentCount > 0) {
                studentCountText.setVisibility(View.VISIBLE);
                studentCountText.setText(studentCount + " student" + (studentCount > 1 ? "s" : "") + " assigned");
            } else {
                studentCountText.setVisibility(View.GONE);
            }
        }
    }

    private void loadStudents() {
        studentsList = databaseHelper.getStudentsForCoach(coachId);

        if (studentsList.isEmpty()) {
            studentsRecyclerView.setVisibility(View.GONE);
            noStudentsText.setVisibility(View.VISIBLE);
            noStudentsText.setText("No students assigned yet.\nStudents will appear here once you accept their requests.");
        } else {
            studentsRecyclerView.setVisibility(View.VISIBLE);
            noStudentsText.setVisibility(View.GONE);

            studentAdapter = new StudentAdapter(studentsList);
            studentsRecyclerView.setAdapter(studentAdapter);
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void viewStudentProfile(Student student) {
        // Navigate to student profile/details
        Intent intent = new Intent(this, StudentDetailsActivity.class);
        intent.putExtra("studentId", student.getId());
        intent.putExtra("studentEmail", student.getEmail());
        intent.putExtra("coachEmail", coachEmail);
        intent.putExtra("coachId", coachId);
        startActivity(intent);
    }

    private void viewStudentVideos(Student student) {
        // Navigate to student's videos
        Intent intent = new Intent(this, VideoListActivity.class);
        intent.putExtra("coachEmail", coachEmail);
        intent.putExtra("studentId", student.getId());
        intent.putExtra("studentName", student.getName());
        startActivity(intent);
    }

    // Student RecyclerView Adapter
    private class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

        private List<Student> students;

        public StudentAdapter(List<Student> students) {
            this.students = students;
        }

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_coach_student, parent, false);
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

            private TextView studentNameText, skillLevelText, ageText, joinDateText;
            private ImageView studentIcon;
            private CardView studentCard;

            public StudentViewHolder(@NonNull View itemView) {
                super(itemView);
                studentNameText = itemView.findViewById(R.id.studentNameText);
                skillLevelText = itemView.findViewById(R.id.skillLevelText);
                ageText = itemView.findViewById(R.id.ageText);
                joinDateText = itemView.findViewById(R.id.joinDateText);
                studentIcon = itemView.findViewById(R.id.studentIcon);
                studentCard = itemView.findViewById(R.id.studentCard);
            }

            public void bind(Student student) {
                studentNameText.setText(student.getName());
                skillLevelText.setText("Skill: " + student.getSkillLevel());
                ageText.setText("Age: " + student.getAge());

                if (student.getCreatedAt() != null) {
                    joinDateText.setText("Joined: " + formatDate(student.getCreatedAt()));
                } else {
                    joinDateText.setText("Joined: Recently");
                }

                // Set click listeners
                studentCard.setOnClickListener(v -> viewStudentProfile(student));

                // Long click for videos
                studentCard.setOnLongClickListener(v -> {
                    viewStudentVideos(student);
                    return true;
                });

                // Set student icon based on skill level
                if (student.getSkillLevel() != null) {
                    switch (student.getSkillLevel().toLowerCase()) {
                        case "beginner":
                            studentIcon.setImageResource(R.drawable.ic_person);
                            studentIcon.setColorFilter(getResources().getColor(R.color.warning_color));
                            break;
                        case "intermediate":
                            studentIcon.setImageResource(R.drawable.ic_person);
                            studentIcon.setColorFilter(getResources().getColor(R.color.cricket_green_primary));
                            break;
                        case "advanced":
                            studentIcon.setImageResource(R.drawable.ic_person);
                            studentIcon.setColorFilter(getResources().getColor(R.color.success_color));
                            break;
                        default:
                            studentIcon.setImageResource(R.drawable.ic_person);
                            studentIcon.setColorFilter(getResources().getColor(R.color.text_secondary));
                            break;
                    }
                } else {
                    studentIcon.setImageResource(R.drawable.ic_person);
                    studentIcon.setColorFilter(getResources().getColor(R.color.text_secondary));
                }
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from other activities
        loadStudents();
        updateUI();
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