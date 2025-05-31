package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FindCoachActivity extends AppCompatActivity {

    private RecyclerView coachesRecyclerView;
    private TextView noCoachesText;
    private ImageButton backBtn;  // Changed from Button to ImageButton

    private DatabaseHelper databaseHelper;
    private CoachAdapter coachAdapter;
    private List<Coach> coachesList;
    private String studentEmail;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_coach);

        databaseHelper = new DatabaseHelper(this);
        studentEmail = getIntent().getStringExtra("studentEmail");
        studentId = getIntent().getIntExtra("studentId", 0);

        initializeViews();
        loadCoaches();
        setupClickListeners();
    }

    private void initializeViews() {
        coachesRecyclerView = findViewById(R.id.coachesRecyclerView);
        noCoachesText = findViewById(R.id.noCoachesText);
        backBtn = findViewById(R.id.backBtn);

        coachesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCoaches() {
        coachesList = databaseHelper.getAllCoaches();

        if (coachesList.isEmpty()) {
            coachesRecyclerView.setVisibility(View.GONE);
            noCoachesText.setVisibility(View.VISIBLE);
        } else {
            coachesRecyclerView.setVisibility(View.VISIBLE);
            noCoachesText.setVisibility(View.GONE);

            coachAdapter = new CoachAdapter(coachesList);
            coachesRecyclerView.setAdapter(coachAdapter);
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
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

    // Coach RecyclerView Adapter
    private class CoachAdapter extends RecyclerView.Adapter<CoachAdapter.CoachViewHolder> {

        private List<Coach> coaches;

        public CoachAdapter(List<Coach> coaches) {
            this.coaches = coaches;
        }

        @NonNull
        @Override
        public CoachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_coach, parent, false);
            return new CoachViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CoachViewHolder holder, int position) {
            Coach coach = coaches.get(position);
            holder.bind(coach);
        }

        @Override
        public int getItemCount() {
            return coaches.size();
        }

        class CoachViewHolder extends RecyclerView.ViewHolder {

            private TextView nameText, specializationText, experienceText;
            private Button selectBtn;
            private ImageView coachIcon;

            public CoachViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.coachNameText);
                specializationText = itemView.findViewById(R.id.coachSpecializationText);
                experienceText = itemView.findViewById(R.id.coachExperienceText);
                selectBtn = itemView.findViewById(R.id.selectCoachBtn);
                coachIcon = itemView.findViewById(R.id.coachIcon);
            }

            public void bind(Coach coach) {
                nameText.setText(coach.getName());
                specializationText.setText(coach.getSpecialization());
                experienceText.setText(coach.getExperienceYears() + " years experience");

                selectBtn.setOnClickListener(v -> selectCoach(coach));
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