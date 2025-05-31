package com.lords.becomebetter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText emailEdit, passwordEdit;
    private Button loginBtn, registerBtn;
    private TextView forgotPasswordText;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
    }

    private void setupClickListeners() {
        loginBtn.setOnClickListener(v -> performLogin());
        registerBtn.setOnClickListener(v -> navigateToRegistration());
        forgotPasswordText.setOnClickListener(v -> handleForgotPassword());
    }

    private void performLogin() {
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString();

        // Clear previous errors
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Authenticate user
        String userType = databaseHelper.authenticateUser(email, password);

        if (userType != null) {
            // Login successful
            Toast.makeText(this, "Login successful! Welcome " + userType, Toast.LENGTH_SHORT).show();

            // Navigate to appropriate dashboard based on user type
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("userType", userType);
            intent.putExtra("userEmail", email);
            startActivity(intent);

            // Optional: finish this activity so user can't go back to login
            // finish();
        } else {
            // Login failed
            showError("Invalid email or password. Please try again.");
        }
    }

    private void navigateToRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void handleForgotPassword() {
        // TODO: Implement forgot password functionality
        Toast.makeText(this, "Forgot password feature coming soon!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        // Validate email
        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate password
        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear form when returning to login screen
        clearForm();
    }

    private void clearForm() {
        emailEdit.setText("");
        passwordEdit.setText("");
        emailLayout.setError(null);
        passwordLayout.setError(null);
    }
}