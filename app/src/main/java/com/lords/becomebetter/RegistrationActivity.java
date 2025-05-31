package com.lords.becomebetter;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrationActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private DatabaseHelper databaseHelper;

    // Coach registration views
    private TextInputLayout coachNameLayout, coachEmailLayout, coachPasswordLayout,
            coachPhoneLayout, coachCodeLayout, coachExperienceLayout, coachCertificationLayout;
    private TextInputEditText coachNameEdit, coachEmailEdit, coachPasswordEdit,
            coachPhoneEdit, coachCodeEdit, coachExperienceEdit, coachCertificationEdit;
    private Spinner coachSpecializationSpinner;
    private Button coachRegisterBtn;

    // Student registration views
    private TextInputLayout studentNameLayout, studentEmailLayout, studentPasswordLayout,
            studentPhoneLayout, studentAgeLayout;
    private TextInputEditText studentNameEdit, studentEmailEdit, studentPasswordEdit,
            studentPhoneEdit, studentAgeEdit;
    private Spinner studentSkillSpinner;
    private Button studentRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        databaseHelper = new DatabaseHelper(this);

        setupBackButton();
        setupTabs();
        setupCoachRegistration();
        setupStudentRegistration();
    }

    private void setupBackButton() {
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setupTabs() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        RegistrationPagerAdapter adapter = new RegistrationPagerAdapter(this);
        viewPager.setAdapter(adapter);

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

    private void setupCoachRegistration() {
        // Initialize coach views (these will be in the fragment)
        // This is a simplified version - in reality, these would be in CoachRegistrationFragment
    }

    private void setupStudentRegistration() {
        // Initialize student views (these will be in the fragment)
        // This is a simplified version - in reality, these would be in StudentRegistrationFragment
    }

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
        // Clear all coach form fields
        // This would be implemented in the actual fragments
    }

    private void clearStudentForm() {
        // Clear all student form fields
        // This would be implemented in the actual fragments
    }
}