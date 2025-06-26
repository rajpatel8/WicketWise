// Add this to your CoachRequestsActivity.java or create if it doesn't exist

package com.lords.becomebetter;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CoachRequestsActivity extends AppCompatActivity {

    private static final String TAG = "CoachRequests";

    private RecyclerView requestsRecyclerView;
    private TextView noRequestsText, headerText;
    private ImageButton backBtn;

    private DatabaseHelper databaseHelper;
    private CoachRequestsAdapter adapter;
    private String coachEmail;
    private int coachId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_requests);

        Log.d(TAG, "🚀 CoachRequestsActivity started");

        // Get coach email from intent
        coachEmail = getIntent().getStringExtra("coachEmail");
        if (coachEmail == null || coachEmail.isEmpty()) {
            Log.e(TAG, "❌ No coach email provided");
            showError("Coach information missing");
            finish();
            return;
        }

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Get coach ID
        Coach coach = databaseHelper.getCoachByEmail(coachEmail);
        if (coach == null) {
            Log.e(TAG, "❌ Coach not found: " + coachEmail);
            showError("Coach profile not found");
            finish();
            return;
        }
        coachId = coach.getId();

        // Initialize views
        initializeViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load requests
        loadRequests();

        Log.d(TAG, "✅ CoachRequestsActivity setup completed");
    }

    private void initializeViews() {
        // Find views
        requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        noRequestsText = findViewById(R.id.noRequestsText);
        headerText = findViewById(R.id.headerText);
        backBtn = findViewById(R.id.backBtn);

        // Setup header
        if (headerText != null) {
            headerText.setText("Student Requests");
            headerText.setTextSize(20); // Larger header text
        }

        // Setup back button
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> onBackPressed());
        }

        // Setup no requests message
        if (noRequestsText != null) {
            noRequestsText.setText("No student requests yet.\n\nStudents who want you as their coach will appear here.");
            noRequestsText.setTextSize(16); // Larger text for better readability
        }

        Log.d(TAG, "✅ Views initialized");
    }

    private void setupRecyclerView() {
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create adapter with larger text sizes
        adapter = new CoachRequestsAdapter(this, coachId, new CoachRequestsAdapter.OnRequestActionListener() {
            @Override
            public void onAcceptRequest(CoachRequest request) {
                showAcceptDialog(request);
            }

            @Override
            public void onRejectRequest(CoachRequest request) {
                showRejectDialog(request);
            }
        });

        requestsRecyclerView.setAdapter(adapter);

        Log.d(TAG, "✅ RecyclerView setup completed");
    }

    private void loadRequests() {
        Log.d(TAG, "📋 Loading requests for coach ID: " + coachId);

        try {
            // Get all requests for this coach
            List<CoachRequest> requests = databaseHelper.getAllRequestsForCoach(coachId);

            Log.d(TAG, "📋 Found " + requests.size() + " requests");

            if (requests.isEmpty()) {
                // Show no requests message
                requestsRecyclerView.setVisibility(View.GONE);
                noRequestsText.setVisibility(View.VISIBLE);
            } else {
                // Show requests
                requestsRecyclerView.setVisibility(View.VISIBLE);
                noRequestsText.setVisibility(View.GONE);

                // Update adapter
                adapter.updateRequests(requests);
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error loading requests", e);
            showError("Error loading student requests");
        }
    }

    private void showAcceptDialog(CoachRequest request) {
        // Get student info
        Student student = databaseHelper.getStudentById(request.getStudentId());
        if (student == null) {
            showError("Student information not found");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Accept Student Request")
                .setMessage("Accept " + student.getName() + " as your student?\n\n" +
                        "Message: " + request.getMessage())
                .setPositiveButton("Accept", (dialog, which) -> {
                    acceptRequest(request, student);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRejectDialog(CoachRequest request) {
        // Get student info
        Student student = databaseHelper.getStudentById(request.getStudentId());
        if (student == null) {
            showError("Student information not found");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Reject Student Request")
                .setMessage("Reject " + student.getName() + "'s request?\n\n" +
                        "This action cannot be undone.")
                .setPositiveButton("Reject", (dialog, which) -> {
                    rejectRequest(request);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void acceptRequest(CoachRequest request, Student student) {
        try {
            // Update request status
            boolean success = databaseHelper.updateCoachRequestStatus(
                    request.getRequestId(),
                    CoachRequest.STATUS_ACCEPTED,
                    "Welcome to my coaching program!"
            );

            if (success) {
                // Assign student to coach
                student.setCoachId(coachId);
                databaseHelper.updateStudent(student);

                Toast.makeText(this, student.getName() + " is now your student!",
                        Toast.LENGTH_LONG).show();

                // Reload requests
                loadRequests();

                Log.d(TAG, "✅ Request accepted for student: " + student.getName());
            } else {
                showError("Failed to accept request");
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error accepting request", e);
            showError("Error accepting student request");
        }
    }

    private void rejectRequest(CoachRequest request) {
        try {
            boolean success = databaseHelper.updateCoachRequestStatus(
                    request.getRequestId(),
                    CoachRequest.STATUS_REJECTED,
                    "Thank you for your interest, but I cannot take on new students at this time."
            );

            if (success) {
                Toast.makeText(this, "Request rejected", Toast.LENGTH_SHORT).show();

                // Reload requests
                loadRequests();

                Log.d(TAG, "✅ Request rejected");
            } else {
                showError("Failed to reject request");
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error rejecting request", e);
            showError("Error rejecting student request");
        }
    }

    private void showError(String message) {
        Log.e(TAG, "🚨 Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        Log.d(TAG, "🧹 CoachRequestsActivity destroyed");
    }
}