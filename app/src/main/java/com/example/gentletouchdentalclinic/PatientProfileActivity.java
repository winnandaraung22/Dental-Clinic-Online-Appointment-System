package com.example.gentletouchdentalclinic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PatientProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbarPatientProfile;

    private TextInputLayout layoutPatientUsername;
    private TextInputLayout layoutPatientName;
    private TextInputLayout layoutPatientPhone;

    private TextInputEditText edtPatientUsername;
    private TextInputEditText edtPatientName;
    private TextInputEditText edtPatientPhone;
    private TextInputEditText edtPatientEmail;

    private TextView txtPatientName;

    private MaterialButton btnSavePatientProfile;
    private MaterialButton btnChangePatientPassword;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_patient_profile
        );


        auth = FirebaseAuth.getInstance();

        currentUser = auth.getCurrentUser();

        db = FirebaseFirestore.getInstance();


        toolbarPatientProfile =
                findViewById(
                        R.id.toolbarPatientProfile
                );

        toolbarPatientProfile.setNavigationOnClickListener(v -> {

            Intent intent = new Intent(
                    PatientProfileActivity.this,
                    PatientDashboardActivity.class
            );

            intent.putExtra("openHome", true);

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);

            finish();

        });

        layoutPatientUsername =
                findViewById(
                        R.id.layoutPatientUsername
                );

        layoutPatientName =
                findViewById(
                        R.id.layoutPatientName
                );

        layoutPatientPhone =
                findViewById(
                        R.id.layoutPatientPhone
                );


        edtPatientUsername =
                findViewById(
                        R.id.edtPatientUsername
                );

        edtPatientName =
                findViewById(
                        R.id.edtPatientName
                );

        edtPatientPhone =
                findViewById(
                        R.id.edtPatientPhone
                );

        edtPatientEmail =
                findViewById(
                        R.id.edtPatientEmail
                );


        txtPatientName =
                findViewById(
                        R.id.txtPatientName
                );


        btnSavePatientProfile =
                findViewById(
                        R.id.btnSavePatientProfile
                );

        btnChangePatientPassword =
                findViewById(
                        R.id.btnChangePatientPassword
                );


        btnSavePatientProfile.setVisibility(
                View.GONE
        );


        disableEdit(
                edtPatientUsername
        );

        disableEdit(
                edtPatientName
        );

        disableEdit(
                edtPatientPhone
        );

        edtPatientEmail.setEnabled(false);


        layoutPatientUsername.setEndIconOnClickListener(
                v -> enableEdit(
                        edtPatientUsername
                )
        );


        layoutPatientName.setEndIconOnClickListener(
                v -> enableEdit(
                        edtPatientName
                )
        );


        layoutPatientPhone.setEndIconOnClickListener(
                v -> enableEdit(
                        edtPatientPhone
                )
        );


        btnSavePatientProfile.setOnClickListener(
                v -> updatePatientProfile()
        );


        btnChangePatientPassword.setOnClickListener(
                v -> showChangePasswordDialog()
        );


        loadPatientProfile();
    }

    private void enableEdit(
            TextInputEditText editText) {

        editText.setFocusable(true);

        editText.setFocusableInTouchMode(true);

        editText.requestFocus();

        editText.setCursorVisible(true);

        btnSavePatientProfile.setVisibility(
                View.VISIBLE
        );

        InputMethodManager imm =
                (InputMethodManager)
                        getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );

        imm.showSoftInput(
                editText,
                InputMethodManager.SHOW_IMPLICIT
        );
    }

    private void loadPatientProfile() {

        if (currentUser == null) {
            return;
        }

        db.collection("users")
                .document(
                        currentUser.getUid()
                )
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (document.exists()) {

                                String name =
                                        document.getString(
                                                "fullName"
                                        );

                                edtPatientUsername.setText(
                                        document.getString(
                                                "username"
                                        )
                                );

                                edtPatientName.setText(
                                        name
                                );

                                txtPatientName.setText(
                                        safeText(
                                                name,
                                                "Patient"
                                        )
                                );

                                edtPatientPhone.setText(
                                        document.getString(
                                                "phone"
                                        )
                                );

                                edtPatientEmail.setText(
                                        document.getString(
                                                "email"
                                        )
                                );
                            }

                        }
                );
    }

    private void updatePatientProfile() {

        if (currentUser == null) {
            return;
        }


        String username =
                edtPatientUsername
                        .getText()
                        .toString()
                        .trim();


        String phone =
                edtPatientPhone
                        .getText()
                        .toString()
                        .trim();


        String fullName =
                edtPatientName
                        .getText()
                        .toString()
                        .trim();


        if (username.isEmpty()
                || fullName.isEmpty()) {

            Toast.makeText(
                    this,
                    "Username and Full Name cannot be empty",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (!phone.matches(
                "^[0-9]{11}$")) {

            edtPatientPhone.setError(
                    "Phone number must be exactly 11 digits"
            );

            edtPatientPhone.requestFocus();

            return;
        }


        if (!username.matches(
                "[a-zA-Z0-9_]+")) {

            Toast.makeText(
                    this,
                    "Username can contain only letters, numbers and underscore",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        Map<String, Object> updates =
                new HashMap<>();


        updates.put(
                "username",
                username
        );


        updates.put(
                "fullName",
                fullName
        );


        updates.put(
                "phone",
                phone
        );


        db.collection("users")
                .document(
                        currentUser.getUid()
                )
                .update(updates)
                .addOnSuccessListener(
                        unused -> {

                            Toast.makeText(
                                    this,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                            ).show();


                            disableEdit(
                                    edtPatientUsername
                            );

                            disableEdit(
                                    edtPatientName
                            );

                            disableEdit(
                                    edtPatientPhone
                            );


                            btnSavePatientProfile
                                    .setVisibility(
                                            View.GONE
                                    );


                            View focus =
                                    getCurrentFocus();


                            if (focus != null) {

                                InputMethodManager imm =
                                        (InputMethodManager)
                                                getSystemService(
                                                        Context.INPUT_METHOD_SERVICE
                                                );


                                imm.hideSoftInputFromWindow(
                                        focus.getWindowToken(),
                                        0
                                );
                            }


                            loadPatientProfile();

                        }
                )
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void disableEdit(
            TextInputEditText editText) {

        editText.clearFocus();

        editText.setFocusable(false);

        editText.setFocusableInTouchMode(false);

        editText.setCursorVisible(false);
    }

    private void showChangePasswordDialog() {

        View view =
                LayoutInflater
                        .from(this)
                        .inflate(
                                R.layout.dialog_change_password,
                                null
                        );


        TextInputEditText edtCurrentPassword =
                view.findViewById(
                        R.id.edtCurrentPassword
                );


        TextInputEditText edtNewPassword =
                view.findViewById(
                        R.id.edtNewPassword
                );


        TextInputEditText edtConfirmPassword =
                view.findViewById(
                        R.id.edtConfirmPassword
                );


        new androidx.appcompat.app.AlertDialog.Builder(
                this
        )
                .setTitle(
                        "Change Password"
                )
                .setView(view)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Update",
                        (dialog, which) -> {

                            String currentPassword =
                                    edtCurrentPassword
                                            .getText()
                                            .toString();


                            String newPassword =
                                    edtNewPassword
                                            .getText()
                                            .toString();


                            String confirmPassword =
                                    edtConfirmPassword
                                            .getText()
                                            .toString();


                            changePassword(
                                    currentPassword,
                                    newPassword,
                                    confirmPassword
                            );

                        }
                )
                .show();
    }

    private void changePassword(
            String currentPassword,
            String newPassword,
            String confirmPassword) {

        if (!newPassword.equals(
                confirmPassword)) {

            Toast.makeText(
                    this,
                    "Passwords do not match\nChange password process is failed.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        FirebaseUser user =
                auth.getCurrentUser();


        if (user == null) {
            return;
        }


        String email =
                user.getEmail();


        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(
                        email,
                        currentPassword
                )
                .addOnSuccessListener(
                        authResult -> {

                            user.updatePassword(
                                            newPassword
                                    )
                                    .addOnSuccessListener(
                                            unused ->
                                                    Toast.makeText(
                                                            this,
                                                            "Password updated successfully",
                                                            Toast.LENGTH_SHORT
                                                    ).show()
                                    )
                                    .addOnFailureListener(
                                            e ->
                                                    Toast.makeText(
                                                            this,
                                                            e.getMessage(),
                                                            Toast.LENGTH_SHORT
                                                    ).show()
                                    );

                        }
                )
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Current password is incorrect\nChange password process is failed",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private String safeText(
            String value,
            String defaultValue) {

        if (value == null ||
                value.trim().isEmpty()) {

            return defaultValue;
        }

        return value;
    }
}