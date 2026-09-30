package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class EditDoctorActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private ImageView imgDoctorProfile;
    private TextInputEditText edtDoctorStaffCode;
    private TextInputEditText edtDoctorName;
    private TextInputEditText edtDoctorEmail;
    private TextInputEditText edtDoctorPhone;
    private TextInputEditText edtSpecialization;
    private TextInputEditText edtQualification;
    private TextInputEditText edtExperience;
    private MaterialButton btnUpdateDoctor;
    private FirebaseFirestore db;
    private String doctorId;
    private String profileImage = "";
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor);

        toolbar = findViewById(R.id.editDoctorToolbar);

        imgDoctorProfile = findViewById(R.id.imgDoctorProfile);

        edtDoctorStaffCode = findViewById(R.id.edtDoctorStaffCode);
        edtDoctorName = findViewById(R.id.edtDoctorName);
        edtDoctorEmail = findViewById(R.id.edtDoctorEmail);
        edtDoctorPhone = findViewById(R.id.edtDoctorPhone);
        edtSpecialization = findViewById(R.id.edtSpecialization);
        edtQualification = findViewById(R.id.edtQualification);
        edtExperience = findViewById(R.id.edtExperience);

        btnUpdateDoctor = findViewById(R.id.btnUpdateDoctor);

        db = FirebaseFirestore.getInstance();

        doctorId = getIntent().getStringExtra("doctorId");

        toolbar.setNavigationOnClickListener(v -> finish());

        imagePickerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {

                            if(uri != null){

                                selectedImageUri = uri;

                                imgDoctorProfile.setImageURI(uri);

                            }

                        });

        imgDoctorProfile.setOnClickListener(v -> {

            imagePickerLauncher.launch("image/*");

        });

        if(doctorId != null){

            loadDoctorInformation();

        }

        btnUpdateDoctor.setOnClickListener(v -> {

            updateDoctor();

        });

    }

    private void loadDoctorInformation(){

        db.collection("doctors")
                .document(doctorId)
                .get()
                .addOnSuccessListener(document -> {

                    if(document.exists()){

                        Doctor doctor =
                                document.toObject(Doctor.class);

                        if(doctor != null){

                            edtDoctorStaffCode.setText(
                                    doctor.getStaffCode()
                            );

                            edtDoctorName.setText(
                                    doctor.getFullName());

                            edtDoctorEmail.setText(
                                    doctor.getEmail());

                            edtDoctorPhone.setText(
                                    doctor.getPhone());

                            edtSpecialization.setText(
                                    doctor.getSpecialization());

                            edtQualification.setText(
                                    doctor.getQualification());

                            edtExperience.setText(
                                    doctor.getExperience());

                            profileImage =
                                    doctor.getProfileImage();

                            if(profileImage != null
                                    && !profileImage.isEmpty()) {


                                byte[] decodedBytes =
                                        Base64.decode(
                                                profileImage,
                                                Base64.DEFAULT
                                        );

                                Bitmap bitmap =
                                        BitmapFactory.decodeByteArray(
                                                decodedBytes,
                                                0,
                                                decodedBytes.length
                                        );


                                imgDoctorProfile.setImageBitmap(bitmap);
                            }

                        }

                    }

                })
                .addOnFailureListener(e ->

                        Toast.makeText(
                                this,
                                "Failed to load doctor information.",
                                Toast.LENGTH_SHORT
                        ).show());

    }

    private void updateDoctor(){

        String fullName = edtDoctorName.getText().toString().trim();
        String email = edtDoctorEmail.getText().toString().trim();
        String phone = edtDoctorPhone.getText().toString().trim();
        String specialization = edtSpecialization.getText().toString().trim();
        String qualification = edtQualification.getText().toString().trim();
        String experience = edtExperience.getText().toString().trim();

        if(fullName.isEmpty()
                || email.isEmpty()
                || phone.isEmpty()
                || specialization.isEmpty()
                || qualification.isEmpty()
                || experience.isEmpty()){

            Toast.makeText(
                    this,
                    "Please fill all fields.",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }
        if(!phone.matches("^[0-9]{11}$")){

            edtDoctorPhone.setError(
                    "Phone number must be 11 digits"
            );

            return;

        }
        if(selectedImageUri != null){

            convertImageToBase64(selectedImageUri);

        }

        Map<String,Object> doctorMap =
                new HashMap<>();

        doctorMap.put("fullName", fullName);
        doctorMap.put("email", email);
        doctorMap.put("phone", phone);
        doctorMap.put("specialization", specialization);
        doctorMap.put("qualification", qualification);
        doctorMap.put("experience", experience);
        doctorMap.put("profileImage", profileImage);

        db.collection("doctors")
                .document(doctorId)
                .update(doctorMap)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Doctor information updated successfully.",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Update failed.",
                            Toast.LENGTH_SHORT
                    ).show();


                });
    }

    private void convertImageToBase64(Uri uri){

        try {

            Bitmap bitmap =
                    BitmapFactory.decodeStream(
                            getContentResolver()
                                    .openInputStream(uri)
                    );
            bitmap =
                    Bitmap.createScaledBitmap(
                            bitmap,
                            500,
                            500,
                            true
                    );

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    50,
                    outputStream
            );

            byte[] imageBytes =
                    outputStream.toByteArray();

            profileImage =
                    Base64.encodeToString(
                            imageBytes,
                            Base64.DEFAULT
                    );

        }
        catch(Exception e){

            Toast.makeText(
                    this,
                    "Image conversion failed.",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }
}
