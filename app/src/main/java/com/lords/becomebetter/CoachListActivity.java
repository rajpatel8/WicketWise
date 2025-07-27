package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CoachListActivity extends AppCompatActivity {

    private RecyclerView coachesRecyclerView;
    private ImageView backBtn;
    private TextView titleText;

    private DatabaseHelper databaseHelper;
    private CoachesAdapter coachesAdapter;
    private String studentEmail;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_list);

        initializeViews();
        getIntentData();
        setupRecyclerView();
        loadCoaches();
        setupClickListeners();
    }

    private void initializeViews() {
        coachesRecyclerView = findViewById(R.id.coachesRecyclerView);
        backBtn = findViewById(R.id.backBtn);
        titleText = findViewById(R.id.titleText);

        databaseHelper = new DatabaseHelper(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        studentEmail = intent.getStringExtra("studentEmail");

        // Get student ID
        Student student = databaseHelper.getStudentByEmail(studentEmail);
        studentId = student != null ? student.getId() : -1;
    }

    private void setupRecyclerView() {
        coachesAdapter = new CoachesAdapter(coach -> {
            // Open chat with selected coach
            Intent chatIntent = new Intent(this, ChatActivity.class);
            chatIntent.putExtra("currentUserEmail", studentEmail);
            chatIntent.putExtra("currentUserType", "student");
            chatIntent.putExtra("otherUserId", coach.getId());
            chatIntent.putExtra("otherUserType", "coach");
            chatIntent.putExtra("otherUserName", coach.getName());
            startActivity(chatIntent);
        });

        coachesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        coachesRecyclerView.setAdapter(coachesAdapter);
    }

    private void loadCoaches() {
        List<Coach> coaches = databaseHelper.getAllCoaches();
        coachesAdapter.updateCoaches(coaches);

        titleText.setText("Choose Coach to Message (" + coaches.size() + ")");
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}