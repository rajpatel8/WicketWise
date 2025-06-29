package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class FindCoachActivity extends AppCompatActivity {

    private RecyclerView coachesRecyclerView;
    private LinearLayout noCoachesLayout;
    private ImageButton backBtn;

    private DatabaseHelper databaseHelper;
    private CoachAdapter coachAdapter;
    private List<Coach> coachesList;
    private String studentEmail;
    private int studentId;
    private Student currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_coach);

        databaseHelper = new DatabaseHelper(this);
        studentEmail = getIntent().getStringExtra("studentEmail");
        studentId = getIntent().getIntExtra("studentId", 0);

        // Get current student info
        currentStudent = databaseHelper.getStudentByEmail(studentEmail);

        initializeViews();
        loadCoaches();
        setupClickListeners();
    }

    private void initializeViews() {
        coachesRecyclerView = findViewById(R.id.coachesRecyclerView);
        noCoachesLayout = findViewById(R.id.noCoachesLayout);
        backBtn = findViewById(R.id.backBtn);

        coachesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCoaches() {
        coachesList = databaseHelper.getAllCoaches();

        if (coachesList.isEmpty()) {
            coachesRecyclerView.setVisibility(View.GONE);
            noCoachesLayout.setVisibility(View.VISIBLE);
        } else {
            coachesRecyclerView.setVisibility(View.VISIBLE);
            noCoachesLayout.setVisibility(View.GONE);

            coachAdapter = new CoachAdapter(coachesList);
            coachesRecyclerView.setAdapter(coachAdapter);
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void requestCoach(Coach coach) {
        // Check if student already has this coach
        if (currentStudent != null && currentStudent.getCoachId() == coach.getId()) {
            Toast.makeText(this, coach.getName() + " is already your coach!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if there's already a pending request
        if (databaseHelper.hasExistingPendingRequest(studentId, coach.getId())) {
            Toast.makeText(this, "You already have a pending request to " + coach.getName(),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        showRequestDialog(coach);
    }

    private void showRequestDialog(Coach coach) {
        // Create a simple EditText for the message
        EditText messageEdit = new EditText(this);
        messageEdit.setHint("Tell " + coach.getName() + " why you'd like them as your coach...");
        messageEdit.setLines(3);
        messageEdit.setMaxLines(5);

        // Create AlertDialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Request Coach")
                .setMessage("Send request to " + coach.getName())
                .setView(messageEdit)
                .setPositiveButton("Send Request", (dialog, which) -> {
                    String message = messageEdit.getText().toString().trim();
                    if (message.isEmpty()) {
                        Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendCoachRequest(coach, message);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendCoachRequest(Coach coach, String message) {
// DEBUG MSG
//        Toast.makeText(this, "Request sent to " + coach.getName() + "! (Feature coming soon)",
//                Toast.LENGTH_SHORT).show();

        long result = databaseHelper.sendCoachRequest(studentId, coach.getId(), message);

        if (result > 0) {
            Toast.makeText(this, "Request sent to " + coach.getName() + "!", Toast.LENGTH_SHORT).show();
            // Refresh adapter to update button states
            if (coachAdapter != null) {
                coachAdapter.notifyDataSetChanged();
            }
        } else if (result == -2) {
            Toast.makeText(this, "You already have a pending request to " + coach.getName(),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to send request. Please try again.", Toast.LENGTH_LONG).show();
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
            private Button requestBtn;
            private ImageView coachIcon;

            public CoachViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.coachNameText);
                specializationText = itemView.findViewById(R.id.coachSpecializationText);
                experienceText = itemView.findViewById(R.id.coachExperienceText);
                requestBtn = itemView.findViewById(R.id.selectCoachBtn);
                coachIcon = itemView.findViewById(R.id.coachIcon);
            }

            public void bind(Coach coach) {
                nameText.setText(coach.getName());
                specializationText.setText(coach.getSpecialization());
                experienceText.setText(coach.getExperienceYears() + " years experience");

                // Check current relationship status
                updateButtonState(coach);

                requestBtn.setOnClickListener(v -> requestCoach(coach));
            }

            private void updateButtonState(Coach coach) {
                // Check if this coach is already the student's coach
                if (currentStudent != null && currentStudent.getCoachId() == coach.getId()) {
                    requestBtn.setText("Your Coach");
                    requestBtn.setEnabled(false);
                    requestBtn.setBackgroundTintList(getResources().getColorStateList(R.color.success_color));
                    return;
                }

//                 Check if there's a pending request
                if (databaseHelper.hasExistingPendingRequest(studentId, coach.getId())) {
                    requestBtn.setText("Request Pending");
                    requestBtn.setEnabled(false);
                    requestBtn.setBackgroundTintList(getResources().getColorStateList(R.color.warning_color));
                    return;
                }

                // Default state - can send request
                requestBtn.setText("Request Coach");
                requestBtn.setEnabled(true);
                requestBtn.setBackgroundTintList(getResources().getColorStateList(R.color.cricket_green_primary));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from other activities
        currentStudent = databaseHelper.getStudentByEmail(studentEmail);
        if (coachAdapter != null) {
            coachAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Return to dashboard instead of student profile
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("userEmail", studentEmail);
        intent.putExtra("userType", "student"); // Add user type for dashboard
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish(); // Close this activity
    }
}