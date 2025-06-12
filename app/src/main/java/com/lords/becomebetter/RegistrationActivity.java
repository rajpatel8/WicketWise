package com.lords.becomebetter;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RegistrationActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private DatabaseHelper databaseHelper;
    private RegistrationPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        databaseHelper = new DatabaseHelper(this);

        setupBackButton();
        setupTabs();
    }

    private void setupBackButton() {
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setupTabs() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new RegistrationPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Coach Registration");
                tab.setIcon(R.drawable.ic_coach);
            } else {
                tab.setText("Student Registration");
                tab.setIcon(R.drawable.ic_student);
            }
        }).attach();
    }

    // Called from CoachRegistrationFragment
    public void registerCoach(Coach coach, String coachCode) {
        // Validate inputs
        if (!validateCoachInputs(coach, coachCode)) {
            return;
        }

        // Check if email already exists
        if (databaseHelper.emailExists(coach.getEmail())) {
            showError("Email already exists. Please use a different email.");
            return;
        }

        // Validate coach code
        if (!databaseHelper.validateCoachCode(coachCode)) {
            showError("Invalid coach code. Please contact administrator.");
            return;
        }

        // Register coach
        long result = databaseHelper.addCoach(coach, coachCode);

        if (result != -1) {
            showSuccess("Coach registered successfully!");
            clearCoachForm();
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    // Called from StudentRegistrationFragment
    public void registerStudent(Student student) {
        // Validate inputs
        if (!validateStudentInputs(student)) {
            return;
        }

        // Check if email already exists
        if (databaseHelper.emailExists(student.getEmail())) {
            showError("Email already exists. Please use a different email.");
            return;
        }

        // Register student
        long result = databaseHelper.addStudent(student);

        if (result != -1) {
            showSuccess("Student registered successfully!");
            clearStudentForm();
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    private boolean validateCoachInputs(Coach coach, String coachCode) {
        boolean isValid = true;

        // Validate name
        if (coach.getName() == null || coach.getName().trim().isEmpty()) {
            showError("Please enter your name");
            isValid = false;
        }

        // Validate email
        if (coach.getEmail() == null || coach.getEmail().trim().isEmpty()) {
            showError("Please enter your email");
            isValid = false;
        } else if (!isValidEmail(coach.getEmail())) {
            showError("Please enter a valid email address");
            isValid = false;
        }

        // Validate password
        if (coach.getPassword() == null || coach.getPassword().length() < 6) {
            showError("Password must be at least 6 characters long");
            isValid = false;
        }

        // Validate coach code
        if (coachCode == null || coachCode.trim().isEmpty()) {
            showError("Please enter the coach code");
            isValid = false;
        } else if (coachCode.trim().length() != 6) {
            showError("Coach code must be exactly 6 digits");
            isValid = false;
        }

        // Validate experience years
        if (coach.getExperienceYears() < 0) {
            showError("Experience years cannot be negative");
            isValid = false;
        }

        return isValid;
    }

    private boolean validateStudentInputs(Student student) {
        boolean isValid = true;

        // Validate name
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            showError("Please enter your name");
            isValid = false;
        }

        // Validate email
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            showError("Please enter your email");
            isValid = false;
        } else if (!isValidEmail(student.getEmail())) {
            showError("Please enter a valid email address");
            isValid = false;
        }

        // Validate password
        if (student.getPassword() == null || student.getPassword().length() < 6) {
            showError("Password must be at least 6 characters long");
            isValid = false;
        }

        // Validate age
        if (student.getAge() < 5 || student.getAge() > 100) {
            showError("Please enter a valid age (5-100)");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void clearCoachForm() {
        // Get current fragment and clear form
        int currentItem = viewPager.getCurrentItem();
        if (currentItem == 0) { // Coach tab
            CoachRegistrationFragment fragment = (CoachRegistrationFragment)
                    getSupportFragmentManager().findFragmentByTag("f" + 0);
            if (fragment != null) {
                fragment.clearForm();
            }
        }
    }

    private void clearStudentForm() {
        // Get current fragment and clear form
        int currentItem = viewPager.getCurrentItem();
        if (currentItem == 1) { // Student tab
            StudentRegistrationFragment fragment = (StudentRegistrationFragment)
                    getSupportFragmentManager().findFragmentByTag("f" + 1);
            if (fragment != null) {
                fragment.clearForm();
            }
        }
    }
}