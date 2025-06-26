package com.lords.becomebetter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CoachRequestsActivity extends AppCompatActivity {

    private RecyclerView requestsRecyclerView;
    private TextView noRequestsText, titleText, pendingCountText;
    private ImageButton backBtn;
    private Button viewAllBtn, viewPendingBtn;

    private DatabaseHelper databaseHelper;
    private RequestAdapter requestAdapter;
    private List<CoachRequest> requestsList;
    private String coachEmail;
    private int coachId;
    private boolean showingPendingOnly = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_requests);

        databaseHelper = new DatabaseHelper(this);
        coachEmail = getIntent().getStringExtra("coachEmail");
        coachId = getIntent().getIntExtra("coachId", 0);

        initializeViews();
        loadRequests();
        setupClickListeners();
        updateUI();
    }

    private void initializeViews() {
        requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        noRequestsText = findViewById(R.id.noRequestsText);
        backBtn = findViewById(R.id.backBtn);
        titleText = findViewById(R.id.titleText);
        pendingCountText = findViewById(R.id.pendingCountText);
        viewAllBtn = findViewById(R.id.viewAllBtn);
        viewPendingBtn = findViewById(R.id.viewPendingBtn);

        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (titleText != null) {
            titleText.setText("Student Requests");
        }
    }

    private void updateUI() {
        int pendingCount = databaseHelper.getPendingRequestCount(coachId);
        if (pendingCountText != null) {
            if (pendingCount > 0) {
                pendingCountText.setVisibility(View.VISIBLE);
                pendingCountText.setText(pendingCount + " pending request" + (pendingCount > 1 ? "s" : ""));
            } else {
                pendingCountText.setVisibility(View.GONE);
            }
        }

        // Update button states
        if (showingPendingOnly) {
            viewPendingBtn.setBackgroundTintList(getResources().getColorStateList(R.color.cricket_green_primary));
            viewAllBtn.setBackgroundTintList(getResources().getColorStateList(R.color.text_secondary));
        } else {
            viewAllBtn.setBackgroundTintList(getResources().getColorStateList(R.color.cricket_green_primary));
            viewPendingBtn.setBackgroundTintList(getResources().getColorStateList(R.color.text_secondary));
        }
    }

    private void loadRequests() {
        if (showingPendingOnly) {
            requestsList = databaseHelper.getPendingRequestsForCoach(coachId);
            noRequestsText.setText("No pending requests.");
        } else {
            requestsList = databaseHelper.getAllRequestsForCoach(coachId);
            noRequestsText.setText("No requests received yet.");
        }

        if (requestsList.isEmpty()) {
            requestsRecyclerView.setVisibility(View.GONE);
            noRequestsText.setVisibility(View.VISIBLE);
        } else {
            requestsRecyclerView.setVisibility(View.VISIBLE);
            noRequestsText.setVisibility(View.GONE);

            requestAdapter = new RequestAdapter(requestsList);
            requestsRecyclerView.setAdapter(requestAdapter);
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());

        viewPendingBtn.setOnClickListener(v -> {
            showingPendingOnly = true;
            loadRequests();
            updateUI();
        });

        viewAllBtn.setOnClickListener(v -> {
            showingPendingOnly = false;
            loadRequests();
            updateUI();
        });
    }

    private void acceptRequest(CoachRequest request) {
        showResponseDialog(request, true);
    }

    private void rejectRequest(CoachRequest request) {
        showResponseDialog(request, false);
    }

    private void showResponseDialog(CoachRequest request, boolean isAccepting) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_coach_response);
        dialog.setCancelable(true);

        TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView studentNameText = dialog.findViewById(R.id.studentNameText);
        TextView studentMessageText = dialog.findViewById(R.id.studentMessageText);
        EditText responseEdit = dialog.findViewById(R.id.responseMessageEdit);
        Button confirmBtn = dialog.findViewById(R.id.confirmResponseBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelResponseBtn);

        String action = isAccepting ? "Accept" : "Reject";
        titleText.setText(action + " Student Request");
        studentNameText.setText("Student: " + request.getStudentName());
        studentMessageText.setText("\"" + request.getMessage() + "\"");
        responseEdit.setHint("Write a " + (isAccepting ? "welcome" : "rejection") + " message...");
        confirmBtn.setText(action + " Request");

        if (isAccepting) {
            confirmBtn.setBackgroundTintList(getResources().getColorStateList(R.color.success_color));
        } else {
            confirmBtn.setBackgroundTintList(getResources().getColorStateList(R.color.error_color));
        }

        confirmBtn.setOnClickListener(v -> {
            String responseMessage = responseEdit.getText().toString().trim();
            if (responseMessage.isEmpty()) {
                responseEdit.setError("Please enter a response message");
                return;
            }

            respondToRequest(request, isAccepting, responseMessage);
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void respondToRequest(CoachRequest request, boolean isAccepting, String responseMessage) {
        String status = isAccepting ? CoachRequest.STATUS_ACCEPTED : CoachRequest.STATUS_REJECTED;

        // Fixed: Use respondToCoachRequest instead of updateCoachRequestStatus
        boolean success = databaseHelper.respondToCoachRequest(request.getRequestId(), status, responseMessage);

        if (success) {
            String action = isAccepting ? "accepted" : "rejected";
            Toast.makeText(this, "Request " + action + " successfully!", Toast.LENGTH_SHORT).show();
            loadRequests(); // Refresh the list
            updateUI();
        } else {
            Toast.makeText(this, "Failed to respond to request. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    // Fixed: Renamed from CoachRequestsAdapter to RequestAdapter (inner class)
    private class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

        private List<CoachRequest> requests;

        public RequestAdapter(List<CoachRequest> requests) {
            this.requests = requests;
        }

        @NonNull
        @Override
        public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_coach_request, parent, false);
            return new RequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
            CoachRequest request = requests.get(position);
            holder.bind(request);
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        class RequestViewHolder extends RecyclerView.ViewHolder {

            private TextView studentNameText, statusText, requestDateText, messageText, responseText;
            private Button acceptBtn, rejectBtn;
            private View statusIndicator;

            public RequestViewHolder(@NonNull View itemView) {
                super(itemView);
                studentNameText = itemView.findViewById(R.id.studentNameText);
                statusText = itemView.findViewById(R.id.statusText);
                requestDateText = itemView.findViewById(R.id.requestDateText);
                messageText = itemView.findViewById(R.id.messageText);
                responseText = itemView.findViewById(R.id.responseText);
                acceptBtn = itemView.findViewById(R.id.acceptBtn);
                rejectBtn = itemView.findViewById(R.id.rejectBtn);
                statusIndicator = itemView.findViewById(R.id.statusIndicator);
            }

            public void bind(CoachRequest request) {
                studentNameText.setText(request.getStudentName());
                statusText.setText(request.getStatus().toUpperCase());
                requestDateText.setText("Requested: " + request.getFormattedRequestDate());
                messageText.setText("\"" + request.getMessage() + "\"");

                // Set status color
                int statusColor = Color.parseColor(request.getStatusColor());
                statusText.setTextColor(statusColor);
//                statusIndicator.setBackgroundColor(statusColor);

                // Show/hide action buttons based on status
                if (request.isPending()) {
                    acceptBtn.setVisibility(View.VISIBLE);
                    rejectBtn.setVisibility(View.VISIBLE);
                    responseText.setVisibility(View.GONE);

                    acceptBtn.setOnClickListener(v -> acceptRequest(request));
                    rejectBtn.setOnClickListener(v -> rejectRequest(request));
                } else {
                    acceptBtn.setVisibility(View.GONE);
                    rejectBtn.setVisibility(View.GONE);
                    responseText.setVisibility(View.VISIBLE);
                    responseText.setText("Your response: \"" + request.getResponseMessage() + "\"");
                }
            }
        }
    }
}