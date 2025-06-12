package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SimpleFindCoachActivity extends AppCompatActivity {

    private RecyclerView coachesRecyclerView;
    private LinearLayout noCoachesLayout;
    private ImageButton backBtn;
    private TextView headerTitle;

    private DatabaseHelper databaseHelper;
    private CoachSelectionAdapter coachAdapter;
    private List<Coach> coachesList;
    private String studentEmail;
    private int studentId;
    private Student currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_find_coach);

        databaseHelper = new DatabaseHelper(this);
        studentEmail = getIntent().getStringExtra("studentEmail");
        studentId = getIntent().getIntExtra("studentId", 0);

        initializeViews();
        loadStudentData();
        loadCoaches();
        setupClickListeners();
    }

    private void initializeViews() {
        coachesRecyclerView = findViewById(R.id.coachesRecyclerView);
        noCoachesLayout = findViewById(R.id.noCoachesLayout);
        backBtn = findViewById(R.id.backBtn);
        headerTitle = findViewById(R.id.headerTitle);

        coachesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadStudentData() {
        currentStudent = databaseHelper.getStudentByEmail(studentEmail);
        if (currentStudent == null) {
            showError("Student profile not found");
            finish();
            return;
        }

        // Update header based on current assignment
        if (currentStudent.getCoachId() > 0) {
            String currentCoachName = databaseHelper.getCoachNameById(currentStudent.getCoachId());
            headerTitle.setText("Change Coach");
            Toast.makeText(this, "Currently assigned to: " + currentCoachName, Toast.LENGTH_SHORT).show();
        } else {
            headerTitle.setText("Select Your Coach");
        }
    }

    private void loadCoaches() {
        coachesList = databaseHelper.getAllCoaches();

        if (coachesList.isEmpty()) {
            coachesRecyclerView.setVisibility(View.GONE);
            noCoachesLayout.setVisibility(View.VISIBLE);
        } else {
            coachesRecyclerView.setVisibility(View.VISIBLE);
            noCoachesLayout.setVisibility(View.GONE);

            coachAdapter = new CoachSelectionAdapter(coachesList);
            coachesRecyclerView.setAdapter(coachAdapter);
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void selectCoach(Coach coach) {
        // Check if this is the currently assigned coach
        if (currentStudent.getCoachId() == coach.getId()) {
            showInfo("This coach is already assigned to you!");
            return;
        }

        // Show confirmation dialog
        showAssignmentConfirmationDialog(coach);
    }

    private void showAssignmentConfirmationDialog(Coach coach) {
        String title = "Assign Coach";
        String message = "Do you want to assign " + coach.getName() + " as your coach?\n\n" +
                "Specialization: " + coach.getSpecialization() + "\n" +
                "Experience: " + coach.getExperienceYears() + " years";

        if (currentStudent.getCoachId() > 0) {
            String currentCoachName = databaseHelper.getCoachNameById(currentStudent.getCoachId());
            message = "Do you want to change from " + currentCoachName + " to " + coach.getName() + "?\n\n" +
                    "New Coach Details:\n" +
                    "Specialization: " + coach.getSpecialization() + "\n" +
                    "Experience: " + coach.getExperienceYears() + " years";
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes, Assign", (dialog, which) -> performCoachAssignment(coach))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performCoachAssignment(Coach coach) {
        // Update student's coach assignment
        currentStudent.setCoachId(coach.getId());
        boolean success = databaseHelper.updateStudent(currentStudent);

        if (success) {
            Toast.makeText(this, "Coach " + coach.getName() + " assigned successfully!",
                    Toast.LENGTH_SHORT).show();

            // Return success result
            Intent resultIntent = new Intent();
            resultIntent.putExtra("coachAssigned", true);
            resultIntent.putExtra("coachName", coach.getName());
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            showError("Failed to assign coach. Please try again.");
        }
    }

    private void showRemoveCoachOption() {
        if (currentStudent.getCoachId() == 0) {
            return; // No coach assigned
        }

        String currentCoachName = databaseHelper.getCoachNameById(currentStudent.getCoachId());

        new AlertDialog.Builder(this)
                .setTitle("Remove Coach")
                .setMessage("Do you want to remove " + currentCoachName + " as your coach?")
                .setPositiveButton("Yes, Remove", (dialog, which) -> {
                    currentStudent.setCoachId(0);
                    boolean success = databaseHelper.updateStudent(currentStudent);
                    if (success) {
                        Toast.makeText(this, "Coach removed successfully!", Toast.LENGTH_SHORT).show();
                        loadStudentData(); // Refresh UI
                        coachAdapter.notifyDataSetChanged();
                    } else {
                        showError("Failed to remove coach.");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Coach Selection RecyclerView Adapter
    private class CoachSelectionAdapter extends RecyclerView.Adapter<CoachSelectionAdapter.CoachViewHolder> {

        private List<Coach> coaches;

        public CoachSelectionAdapter(List<Coach> coaches) {
            this.coaches = coaches;
        }

        @NonNull
        @Override
        public CoachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_coach_selection, parent, false);
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

            private TextView nameText, specializationText, experienceText, certificationText;
            private Button selectBtn;
            private ImageView coachIcon, statusIcon;
            private View statusIndicator;

            public CoachViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.coachNameText);
                specializationText = itemView.findViewById(R.id.coachSpecializationText);
                experienceText = itemView.findViewById(R.id.coachExperienceText);
                certificationText = itemView.findViewById(R.id.coachCertificationText);
                selectBtn = itemView.findViewById(R.id.selectCoachBtn);
                coachIcon = itemView.findViewById(R.id.coachIcon);
                statusIcon = itemView.findViewById(R.id.statusIcon);
                statusIndicator = itemView.findViewById(R.id.statusIndicator);
            }

            public void bind(Coach coach) {
                nameText.setText(coach.getName());
                specializationText.setText(coach.getSpecialization());
                experienceText.setText(coach.getExperienceYears() + " years experience");

                // Show certification if available
                if (coach.getCertification() != null && !coach.getCertification().trim().isEmpty()) {
                    certificationText.setText(coach.getCertification());
                    certificationText.setVisibility(View.VISIBLE);
                } else {
                    certificationText.setVisibility(View.GONE);
                }

                // Check if this coach is currently assigned
                boolean isCurrentCoach = currentStudent.getCoachId() == coach.getId();

                if (isCurrentCoach) {
                    selectBtn.setText("Current Coach");
                    selectBtn.setEnabled(false);
                    selectBtn.setBackgroundTintList(getResources().getColorStateList(R.color.text_secondary));
                    statusIcon.setVisibility(View.VISIBLE);
                    statusIndicator.setBackgroundTintList(getResources().getColorStateList(R.color.cricket_green_primary));
                } else {
                    selectBtn.setText("Select Coach");
                    selectBtn.setEnabled(true);
                    selectBtn.setBackgroundTintList(getResources().getColorStateList(R.color.cricket_green_primary));
                    statusIcon.setVisibility(View.GONE);
                    statusIndicator.setBackgroundTintList(getResources().getColorStateList(R.color.divider_color));
                }

                selectBtn.setOnClickListener(v -> {
                    if (!isCurrentCoach) {
                        selectCoach(coach);
                    }
                });
            }
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showInfo(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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