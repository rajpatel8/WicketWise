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

public class CoachRegistrationFragment extends Fragment {

    private TextInputLayout nameLayout, emailLayout, passwordLayout, phoneLayout,
            codeLayout, experienceLayout, certificationLayout;
    private TextInputEditText nameEdit, emailEdit, passwordEdit, phoneEdit,
            codeEdit, experienceEdit, certificationEdit;
    private Spinner specializationSpinner;
    private Button registerBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coach_registration, container, false);

        initializeViews(view);
        setupSpinner();
        setupRegisterButton();

        return view;
    }

    private void initializeViews(View view) {
        // TextInputLayouts
        nameLayout = view.findViewById(R.id.coachNameLayout);
        emailLayout = view.findViewById(R.id.coachEmailLayout);
        passwordLayout = view.findViewById(R.id.coachPasswordLayout);
        phoneLayout = view.findViewById(R.id.coachPhoneLayout);
        codeLayout = view.findViewById(R.id.coachCodeLayout);
        experienceLayout = view.findViewById(R.id.coachExperienceLayout);
        certificationLayout = view.findViewById(R.id.coachCertificationLayout);

        // TextInputEditTexts
        nameEdit = view.findViewById(R.id.coachNameEdit);
        emailEdit = view.findViewById(R.id.coachEmailEdit);
        passwordEdit = view.findViewById(R.id.coachPasswordEdit);
        phoneEdit = view.findViewById(R.id.coachPhoneEdit);
        codeEdit = view.findViewById(R.id.coachCodeEdit);
        experienceEdit = view.findViewById(R.id.coachExperienceEdit);
        certificationEdit = view.findViewById(R.id.coachCertificationEdit);

        // Other views
        specializationSpinner = view.findViewById(R.id.coachSpecializationSpinner);
        registerBtn = view.findViewById(R.id.coachRegisterBtn);
    }

    private void setupSpinner() {
        String[] specializations = {
                "Select Specialization",
                "Batting Coach",
                "Bowling Coach",
                "Wicket Keeping Coach",
                "Fielding Coach",
                "All-Rounder Coach",
                "Fitness & Conditioning",
                "Mental Performance Coach",
                "Youth Development",
                "Professional Cricket"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                specializations
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specializationSpinner.setAdapter(adapter);
    }

    private void setupRegisterButton() {
        registerBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                registerCoach();
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

        // Validate coach code
        String code = codeEdit.getText().toString().trim();
        if (code.isEmpty()) {
            codeLayout.setError("Coach code is required");
            isValid = false;
        } else if (code.length() != 6) {
            codeLayout.setError("Coach code must be exactly 6 digits");
            isValid = false;
        } else if (!code.matches("\\d{6}")) {
            codeLayout.setError("Coach code must contain only numbers");
            isValid = false;
        }

        // Validate experience
        String experienceStr = experienceEdit.getText().toString().trim();
        if (experienceStr.isEmpty()) {
            experienceLayout.setError("Experience years is required");
            isValid = false;
        } else {
            try {
                int experience = Integer.parseInt(experienceStr);
                if (experience < 0 || experience > 50) {
                    experienceLayout.setError("Experience must be between 0-50 years");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                experienceLayout.setError("Please enter a valid number");
                isValid = false;
            }
        }

        // Validate specialization
        if (specializationSpinner.getSelectedItemPosition() == 0) {
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
        codeLayout.setError(null);
        experienceLayout.setError(null);
        certificationLayout.setError(null);
    }

    private void registerCoach() {
        // Get values from form
        String name = nameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString();
        String phone = phoneEdit.getText().toString().trim();
        String coachCode = codeEdit.getText().toString().trim();
        int experience = Integer.parseInt(experienceEdit.getText().toString().trim());
        String specialization = specializationSpinner.getSelectedItem().toString();
        String certification = certificationEdit.getText().toString().trim();

        // Create Coach object
        Coach coach = new Coach(name, email, password, phone, experience, specialization, certification);

        // Call parent activity method to register coach
        if (getActivity() instanceof RegistrationActivity) {
            ((RegistrationActivity) getActivity()).registerCoach(coach, coachCode);
        }
    }

    public void clearForm() {
        nameEdit.setText("");
        emailEdit.setText("");
        passwordEdit.setText("");
        phoneEdit.setText("");
        codeEdit.setText("");
        experienceEdit.setText("");
        certificationEdit.setText("");
        specializationSpinner.setSelection(0);
        clearErrors();
    }
}