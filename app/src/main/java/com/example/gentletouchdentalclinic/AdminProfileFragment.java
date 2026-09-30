package com.example.gentletouchdentalclinic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminProfileFragment extends Fragment {
    LinearLayout adminProfileRoot;
    TextInputLayout layoutAdminName;
    TextInputLayout layoutAdminPhone;

    TextInputEditText edtAdminName;
    TextInputEditText edtAdminEmail;
    TextInputEditText edtAdminPhone;
    TextInputEditText edtStaffAccessCode;
    TextInputEditText edtAdminRole;

    MaterialButton btnSaveProfile;
    MaterialButton btnChangePassword;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_admin_profile,
                container,
                false
        );
        adminProfileRoot = view.findViewById(R.id.adminProfileRoot);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        layoutAdminName = view.findViewById(R.id.layoutAdminName);
        layoutAdminPhone = view.findViewById(R.id.layoutAdminPhone);

        edtAdminName = view.findViewById(R.id.edtAdminName);
        edtAdminEmail = view.findViewById(R.id.edtAdminEmail);
        edtAdminPhone = view.findViewById(R.id.edtAdminPhone);
        edtStaffAccessCode = view.findViewById(R.id.edtStaffAccessCode);
        edtAdminRole = view.findViewById(R.id.edtAdminRole);

        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        disableEdit(edtAdminName);

        disableEdit(edtAdminPhone);

        layoutAdminName.setEndIconOnClickListener(v -> {

            enableEdit(edtAdminName);

            btnSaveProfile.setVisibility(
                    View.VISIBLE
            );

        });

        layoutAdminPhone.setEndIconOnClickListener(v -> {

            enableEdit(edtAdminPhone);

            btnSaveProfile.setVisibility(
                    View.VISIBLE
            );

        });

        edtAdminName.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        edtAdminPhone.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        adminProfileRoot.setOnClickListener(v -> {

            disableEdit(edtAdminName);
            disableEdit(edtAdminPhone);
            btnSaveProfile.setVisibility(View.GONE);

        });

        loadAdminProfile();

        btnSaveProfile.setOnClickListener(v -> updateProfile());

        btnChangePassword.setOnClickListener(v -> {
            showChangePasswordDialog();
        });
        return view;

    }
    private void loadAdminProfile() {

        if(currentUser == null)
            return;

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    if(document.exists()) {

                        edtAdminName.setText(document.getString("fullName"));
                        edtAdminEmail.setText(document.getString("email"));
                        edtAdminPhone.setText(document.getString("phone"));
                        edtStaffAccessCode.setText(document.getString("staffAccessCode"));
                        edtAdminRole.setText(document.getString("role"));

                    }

                });

    }
    private void updateProfile() {

        String fullName = edtAdminName.getText().toString().trim();
        String phone = edtAdminPhone.getText().toString().trim();

        if(fullName.isEmpty() || phone.isEmpty()){

            Toast.makeText(
                    requireContext(),
                    "Please fill all fields.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }
        if(!phone.matches("^[0-9]{11}$")){

            edtAdminPhone.setError("Phone number must be exactly 11 digits");
            edtAdminPhone.requestFocus();

            return;
        }

        Map<String,Object> updates = new HashMap<>();

        updates.put("fullName",
                edtAdminName.getText().toString());

        updates.put("phone",
                edtAdminPhone.getText().toString());


        db.collection("users")
                .document(currentUser.getUid())
                .update(updates)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(requireContext(),
                            "Profile updated successfully.",
                            Toast.LENGTH_SHORT).show();

                    edtAdminName.setFocusable(false);
                    edtAdminName.setFocusableInTouchMode(false);

                    edtAdminPhone.setFocusable(false);
                    edtAdminPhone.setFocusableInTouchMode(false);

                    loadAdminProfile();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                e.getMessage(),
                                Toast.LENGTH_SHORT).show());

    }
    private void showChangePasswordDialog(){

        View view =
                LayoutInflater.from(requireContext())
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
                requireContext()
        )
                .setTitle("Change Password")
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

                        })

                .show();

    }
    private void changePassword(
            String currentPassword,
            String newPassword,
            String confirmPassword){

        if(!newPassword.equals(confirmPassword)){

            Toast.makeText(
                    requireContext(),
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }
        FirebaseUser user = auth.getCurrentUser();

        if(user == null)
            return;

        String email =
                user.getEmail();

        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(
                        email,
                        currentPassword
                )

                .addOnSuccessListener(authResult -> {

                    user.updatePassword(newPassword)

                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Password updated successfully",
                                        Toast.LENGTH_SHORT
                                ).show();


                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        requireContext(),
                                        e.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();


                            });



                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            requireContext(),
                            "Current password is incorrect\nChange password process is failed",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
    private void disableEdit(TextInputEditText editText){

        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.clearFocus();

    }
    private void enableEdit(TextInputEditText editText){

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

    }
}
