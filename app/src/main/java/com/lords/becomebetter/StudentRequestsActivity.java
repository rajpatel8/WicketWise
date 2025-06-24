package com.lords.becomebetter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentRequestsActivity extends AppCompatActivity {

    private RecyclerView requestsRecyclerView;
    private TextView noRequestsText, titleText;
    private ImageButton backBtn;

    private DatabaseHelper databaseHelper;
    private RequestAdapter requestAdapter;
    private List<CoachRequest> requestsList;
    private String studentEmail;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_requests);

        databaseHelper = new DatabaseHelper(this);
        studentEmail = getIntent().getStringExtra("studentEmail");
        studentId = getIntent().getIntExtra("studentId", 0);

        initializeViews();
        loadRequests();
        setupClickListeners();
    }

    private void initializeViews() {
        requestsRecyclerView = findViewById(R.id.requestsRecyclerView);
        noRequestsText = findViewById(R.id.noRequestsText);
        backBtn = findViewById(R.id.backBtn);
        titleText = findViewById(R.id.titleText);

        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (titleText != null) {
            titleText.setText("My Coach Requests");
        }
    }

    private void loadRequests() {
        requestsList = databaseHelper.getRequestsForStudent(studentId);

        if (requestsList.isEmpty()) {
            requestsRecyclerView.setVisibility(View.GONE);
            noRequestsText.setVisibility(View.VISIBLE);
            noRequestsText.setText("You haven't sent any coach requests yet.");
        } else {
            requestsRecyclerView.setVisibility(View.VISIBLE);
            noRequestsText.setVisibility(View.GONE);

            requestAdapter = new RequestAdapter(requestsList);
            requestsRecyclerView.setAdapter(requestAdapter);
        }
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    // Request RecyclerView Adapter
    private class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

        private List<CoachRequest> requests;

        public RequestAdapter(List<CoachRequest> requests) {
            this.requests = requests;
        }

        @NonNull
        @Override
        public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_student_request, parent, false);
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

            private TextView coachNameText, statusText, requestDateText, messageText, responseText;
            private CardView statusCard;
            private View statusIndicator;

            public RequestViewHolder(@NonNull View itemView) {
                super(itemView);
                coachNameText = itemView.findViewById(R.id.coachNameText);
                statusText = itemView.findViewById(R.id.statusText);
                requestDateText = itemView.findViewById(R.id.requestDateText);
                messageText = itemView.findViewById(R.id.messageText);
                responseText = itemView.findViewById(R.id.responseText);
                statusCard = itemView.findViewById(R.id.statusCard);
                statusIndicator = itemView.findViewById(R.id.statusIndicator);
            }

            public void bind(CoachRequest request) {
                coachNameText.setText("Coach: " + request.getCoachName());
                statusText.setText(request.getStatus().toUpperCase());
                requestDateText.setText("Requested: " + request.getFormattedRequestDate());
                messageText.setText("Your message: \"" + request.getMessage() + "\"");

                // Set status color
                int statusColor = Color.parseColor(request.getStatusColor());
                statusText.setTextColor(statusColor);
                statusIndicator.setBackgroundColor(statusColor);

                // Show response if available
                if (request.getResponseMessage() != null && !request.getResponseMessage().trim().isEmpty()) {
                    responseText.setVisibility(View.VISIBLE);
                    if (request.isAccepted()) {
                        responseText.setText("✓ " + request.getResponseMessage());
                        responseText.setTextColor(Color.parseColor("#66BB6A"));
                    } else if (request.isRejected()) {
                        responseText.setText("✗ " + request.getResponseMessage());
                        responseText.setTextColor(Color.parseColor("#EF5350"));
                    }
                } else {
                    responseText.setVisibility(View.GONE);
                }

                // Set card background based on status
                if (request.isPending()) {
                    statusCard.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
                } else if (request.isAccepted()) {
                    statusCard.setCardBackgroundColor(Color.parseColor("#E8F5E8"));
                } else if (request.isRejected()) {
                    statusCard.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}