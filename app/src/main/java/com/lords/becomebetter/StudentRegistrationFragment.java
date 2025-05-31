package com.lords.becomebetter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class StudentRegistrationFragment extends Fragment {

    private TextInputLayout nameLayout, emailLayout, passwordLayout, phoneLayout, ageLayout;
    private TextInputEditText nameEdit, emailEdit, passwordEdit, phoneEdit, ageEdit;
    private Spinner skillSpinner;
    private Button registerBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_registration, container, false);

        initializeViews(view);
        setupSpinner();
        setupRegisterButton();

        return view;
    }

    private void initializeViews(View view) {
        // TextInputLayouts
        nameLayout = view.findViewById(R.id.studentNameLayout);
        emailLayout = view.findViewById(R.id.studentEmailLayout);
        passwordLayout = view.findViewById(R.id.studentPasswordLayout);
        phoneLayout = view.findViewById(R.id.studentPhoneLayout);
        ageLayout = view.findViewById(R.id.studentAgeLayout);

        // TextInputEditTexts
        nameEdit = view.findViewById(R.id.studentNameEdit);
        emailEdit = view.findViewById(R.id.studentEmailEdit);
        passwordEdit = view.findViewById(R.id.studentPasswordEdit);
        phoneEdit = view.findViewById(R.id.studentPhoneEdit);
        ageEdit = view.findViewById(R.id.studentAgeEdit);

        // Other views
        skillSpinner = view.findViewById(R.id.studentSkillSpinner);
        registerBtn = view.findViewById(R.id.studentRegisterBtn);
    }

    private void setupSpinner() {
        String[] skillLevels = {
                "Select Skill Level",
                "Beginner (Just starting)",
                "Basic (Some experience)",
                "Intermediate (Regular player)",
                "Advanced (Competitive level)",
                "Expert (Professional level)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                skillLevels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillSpinner.setAdapter(adapter);
    }

    private void setupRegisterButton() {
        registerBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                registerStudent();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Clear previous errors
        clearErrors();

        // Validate name
        String name = nameEdit.getText().toString().trim();
        if (name.isEmpty()) {
            nameLayout.setError("Name is required");
            isValid = false;
        }

        // Validate email
        String email = emailEdit.getText().toString().trim();
        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email");
            isValid = false;
        }

        // Validate password
        String password = passwordEdit.getText().toString();
        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        }

        // Validate age
        String ageStr = ageEdit.getText().toString().trim();
        if (ageStr.isEmpty()) {
            ageLayout.setError("Age is required");
            isValid = false;
        } else {
            try {
                int age = Integer.parseInt(ageStr);
                if (age < 5 || age > 100) {
                    ageLayout.setError("Age must be between 5-100 years");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                ageLayout.setError("Please enter a valid age");
                isValid = false;
            }
        }

        // Validate skill level
        if (skillSpinner.getSelectedItemPosition() == 0) {
            // You might want to show an error message or highlight the spinner
            return false;
        }

        return isValid;
    }

    private void clearErrors() {
        nameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        phoneLayout.setError(null);
        ageLayout.setError(null);
    }

    private void registerStudent() {
        // Get values from form
        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString();
        String phone = phoneEdit.getText().toString().trim();
        int age = Integer.parseInt(ageEdit.getText().toString().trim());
        String skillLevel = skillSpinner.getSelectedItem().toString();

        // Create Student object
        Student student = new Student(name, email, password, phone, age, skillLevel);

        // Call parent activity method to register student
        if (getActivity() instanceof RegistrationActivity) {
            ((RegistrationActivity) getActivity()).registerStudent(student);
        }
    }

    public void clearForm() {
        nameEdit.setText("");
        emailEdit.setText("");
        passwordEdit.setText("");
        phoneEdit.setText("");
        ageEdit.setText("");
        skillSpinner.setSelection(0);
        clearErrors();
    }
}