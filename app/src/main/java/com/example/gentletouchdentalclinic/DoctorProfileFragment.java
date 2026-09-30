package com.example.gentletouchdentalclinic;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class DoctorProfileFragment extends Fragment {
    ShapeableImageView imgDoctorProfile;
    ImageView btnEditDoctorImage;
    TextView txtDoctorProfileName;
    TextView txtDoctorSpecialization;
    TextView txtDoctorStaffAccessCode;
    TextView txtDoctorQualification;
    TextView txtDoctorExperience;
    TextView txtDoctorEmail;
    TextView txtDoctorPhone;
    EditText edtDoctorPhone;
    ImageView btnEditDoctorPhone;
    MaterialButton btnSaveDoctorChanges;
    ImageView btnChangeDoctorPassword;
    MaterialButton btnDoctorLogout;
    boolean isEditingPhone = false;
    FirebaseFirestore db;
    FirebaseAuth auth;
    ActivityResultLauncher<String> imagePickerLauncher;
    Bitmap selectedBitmap;
    String doctorDocumentID;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {


        View view = inflater.inflate(
                R.layout.fragment_doctor_profile,
                container,
                false
        );


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        imagePickerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {

                            if(uri != null){

                                try{

                                    selectedBitmap =
                                            MediaStore.Images.Media.getBitmap(
                                                    requireActivity()
                                                            .getContentResolver(),
                                                    uri
                                            );

                                    imgDoctorProfile.setImageBitmap(
                                            selectedBitmap
                                    );

                                    btnSaveDoctorChanges.setVisibility(
                                            View.VISIBLE
                                    );

                                }
                                catch(IOException e){

                                    e.printStackTrace();

                                }

                            }

                        }
                );

        imgDoctorProfile = view.findViewById(R.id.imgDoctorProfile);

        btnEditDoctorImage =
                view.findViewById(
                        R.id.btnEditDoctorImage
                );

        txtDoctorProfileName =
                view.findViewById(
                        R.id.txtDoctorProfileName
                );

        txtDoctorSpecialization =
                view.findViewById(
                        R.id.txtDoctorSpecialization
                );

        txtDoctorStaffAccessCode =
                view.findViewById(
                        R.id.txtDoctorStaffAccessCode
                );

        txtDoctorQualification =
                view.findViewById(
                        R.id.txtDoctorQualification
                );


        txtDoctorExperience =
                view.findViewById(
                        R.id.txtDoctorExperience
                );

        txtDoctorEmail =
                view.findViewById(
                        R.id.txtDoctorEmail
                );

        txtDoctorPhone =
                view.findViewById(
                        R.id.txtDoctorPhone
                );

        edtDoctorPhone =
                view.findViewById(
                        R.id.edtDoctorPhone
                );

        btnEditDoctorPhone =
                view.findViewById(
                        R.id.btnEditDoctorPhone
                );

        btnSaveDoctorChanges =
                view.findViewById(
                        R.id.btnSaveDoctorChanges
                );

        btnChangeDoctorPassword =
                view.findViewById(
                        R.id.btnChangeDoctorPassword
                );

        btnDoctorLogout =
                view.findViewById(
                        R.id.btnDoctorLogout
                );


        loadDoctorProfile();

        view.setOnTouchListener((v, event) -> {


            if(isEditingPhone){

                edtDoctorPhone.clearFocus();


                cancelPhoneEdit();


            }


            return false;

        });

        btnEditDoctorPhone.setOnClickListener(v -> {


            isEditingPhone = true;


            txtDoctorPhone.setVisibility(View.GONE);


            edtDoctorPhone.setVisibility(View.VISIBLE);


            btnSaveDoctorChanges.setVisibility(
                    View.VISIBLE
            );


            edtDoctorPhone.requestFocus();


            edtDoctorPhone.setSelection(
                    edtDoctorPhone.length()
            );


            hideBottomNavigation(true);


        });

        btnChangeDoctorPassword.setOnClickListener(v -> {


            showChangePasswordDialog();


        });

        btnSaveDoctorChanges.setOnClickListener(v -> {


            String phone = edtDoctorPhone.getText().toString().trim();

            if(!phone.matches("^[0-9]{11}$")){


                edtDoctorPhone.setError(
                        "Phone number must be 11 digits"
                );


                return;

            }

            if(doctorDocumentID == null){


                Toast.makeText(
                        requireContext(),
                        "Doctor profile not loaded",
                        Toast.LENGTH_SHORT
                ).show();


                return;

            }

            HashMap<String,Object> updates =
                    new HashMap<>();

            updates.put(
                    "phone",
                    phone
            );

            if(selectedBitmap != null){

                updates.put(
                        "profileImage",
                        bitmapToBase64(selectedBitmap)
                );

            }

            db.collection("doctors")
                    .document(doctorDocumentID)
                    .update(updates)
                    .addOnSuccessListener(unused -> {

                        txtDoctorPhone.setText(phone);

                        cancelPhoneEdit();

                        selectedBitmap = null;

                        hideBottomNavigation(false);

                        Toast.makeText(
                                requireContext(),
                                "Profile Updated",
                                Toast.LENGTH_SHORT
                        ).show();

                    })
                    .addOnFailureListener(e -> {
                        hideBottomNavigation(false);


                        Toast.makeText(
                                requireContext(),
                                "Update failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    });


        });

        btnEditDoctorImage.setOnClickListener(v -> {

            imagePickerLauncher.launch("image/*");

        });

        btnDoctorLogout.setOnClickListener(v -> {


            auth.signOut();


            Toast.makeText(
                    requireContext(),
                    "Logged out",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent =
                    new Intent(
                            requireActivity(),
                            LoginActivity.class
                    );


            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );


            startActivity(intent);

            requireActivity().finish();


        });

        return view;

    }

    private void loadDoctorProfile(){


        FirebaseUser user =
                auth.getCurrentUser();


        if(user == null){
            return;
        }


        String uid =
                user.getUid();

        db.collection("doctors")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if(document.exists()){

                        doctorDocumentID =
                                document.getId();


                        txtDoctorProfileName.setText(
                                document.getString("fullName")
                        );

                        txtDoctorSpecialization.setText(
                                document.getString("specialization")
                        );

                        txtDoctorStaffAccessCode.setText(
                                "Staff Access Code: "
                                        + document.getString("staffAccessCode")
                        );

                        txtDoctorQualification.setText(
                                "Qualification: "
                                        + document.getString("qualification")
                        );

                        txtDoctorExperience.setText(
                                "Experience: "
                                        + document.getString("experience")
                        );

                        txtDoctorEmail.setText(
                                document.getString("email")
                        );

                        String phone =
                                document.getString("phone");

                        txtDoctorPhone.setText(phone);

                        edtDoctorPhone.setText(phone);

                        String image =
                                document.getString("profileImage");


                        loadDoctorImage(image);

                    }


                });


    }
    private void loadDoctorImage(String base64Image){


        if(base64Image != null &&
                !base64Image.isEmpty()){

            try {

                byte[] decodedBytes =
                        Base64.decode(
                                base64Image,
                                Base64.DEFAULT
                        );

                Bitmap bitmap =
                        BitmapFactory.decodeByteArray(
                                decodedBytes,
                                0,
                                decodedBytes.length
                        );

                imgDoctorProfile.setImageBitmap(
                        bitmap
                );

            }
            catch(Exception e){

                e.printStackTrace();

                imgDoctorProfile.setImageResource(
                        R.drawable.profile
                );

            }

        }

    }
    private String bitmapToBase64(Bitmap bitmap){

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                70,
                outputStream
        );

        byte[] imageBytes =
                outputStream.toByteArray();

        return Base64.encodeToString(
                imageBytes,
                Base64.DEFAULT
        );

    }

    private void showChangePasswordDialog(){


        View view =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.dialog_change_password,
                                null
                        );

        EditText etCurrentPassword =
                view.findViewById(
                        R.id.edtCurrentPassword
                );

        EditText etNewPassword =
                view.findViewById(
                        R.id.edtNewPassword
                );

        EditText etConfirmPassword =
                view.findViewById(
                        R.id.edtConfirmPassword
                );

        AlertDialog dialog =
                new AlertDialog.Builder(requireContext())

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
                                null
                        )

                        .create();


        dialog.setOnShowListener(d -> {

            dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    )
                    .setOnClickListener(v -> {

                        String currentPassword =
                                etCurrentPassword
                                        .getText()
                                        .toString()
                                        .trim();

                        String newPassword =
                                etNewPassword
                                        .getText()
                                        .toString()
                                        .trim();


                        String confirmPassword =
                                etConfirmPassword
                                        .getText()
                                        .toString()
                                        .trim();

                        if(currentPassword.isEmpty()
                                || newPassword.isEmpty()
                                || confirmPassword.isEmpty()){

                            Toast.makeText(
                                    requireContext(),
                                    "Please fill all fields",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;

                        }

                        if(!newPassword.equals(confirmPassword)){

                            Toast.makeText(
                                    requireContext(),
                                    "Passwords do not match",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;

                        }

                        changeDoctorPassword(
                                currentPassword,
                                newPassword,
                                dialog
                        );

                    });
        });

        dialog.show();

    }
    private void changeDoctorPassword(
            String currentPassword,
            String newPassword,
            AlertDialog dialog){

        FirebaseUser user = auth.getCurrentUser();

        if(user == null){
            return;
        }

        String email =
                user.getEmail();

        AuthCredential credential =
                EmailAuthProvider.getCredential(
                        email,
                        currentPassword
                );

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {

                    user.updatePassword(
                                    newPassword
                            )
                            .addOnSuccessListener(unused2 -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Password Updated Successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                dialog.dismiss();

                            });

                })
                .addOnFailureListener(e -> {


                    Toast.makeText(
                            requireContext(),
                            "Current password is incorrect",
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }
    private void hideBottomNavigation(boolean hide){


        DoctorDashboardActivity activity =
                (DoctorDashboardActivity) requireActivity();


        activity.hideDoctorBottomNavigation(hide);


    }
    private void cancelPhoneEdit(){


        isEditingPhone = false;

        txtDoctorPhone.setVisibility(
                View.VISIBLE
        );

        edtDoctorPhone.setVisibility(
                View.GONE
        );

        btnSaveDoctorChanges.setVisibility(
                View.GONE
        );

        hideBottomNavigation(false);

    }

}