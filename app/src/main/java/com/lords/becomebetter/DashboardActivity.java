package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class DashboardActivity extends AppCompatActivity {

    private MaterialButton btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
    }

    private void setupClickListeners() {
        btnBackToLogin.setOnClickListener(v -> {
            // Go back to login screen
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Close this activity
        });
    }
}