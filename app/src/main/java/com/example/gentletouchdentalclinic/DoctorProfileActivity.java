package com.example.gentletouchdentalclinic;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;

public class DoctorProfileActivity extends AppCompatActivity {
    TextView txtDoctorName, txtSpecialization, txtRating, txtQualification, txtExperience;
    RatingBar ratingDoctor;
    ImageView imgDoctorProfile, imgBack;
    FirebaseFirestore db;
    MaterialButton btnBookAppointment;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_doctor_profile
        );

        txtDoctorName = findViewById(R.id.txtDoctorName);
        txtSpecialization = findViewById(R.id.txtSpecialization);
        txtQualification = findViewById(R.id.txtQualification);
        txtExperience = findViewById(R.id.txtExperience);
        txtRating = findViewById(R.id.txtRating);
        ratingDoctor = findViewById(R.id.ratingDoctor);
        imgDoctorProfile = findViewById(R.id.imgDoctorProfile);
        imgBack = findViewById(R.id.imgBack);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);

        btnBookAppointment.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DoctorProfileActivity.this,
                    AppointmentActivity.class
            );

            intent.putExtra(
                    "doctorId",
                    doctorId
            );

            startActivity(intent);

        });

        imgBack.setOnClickListener(v -> {
            finish();

            overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );
        });

        db = FirebaseFirestore.getInstance();

        doctorId = getIntent().getStringExtra("doctorId");

        loadDoctorProfile(doctorId);

        ratingDoctor.setOnRatingBarChangeListener(
                (ratingBar, rating, fromUser) -> {

                    if(fromUser){

                        showRatingDialog(rating);

                    }

                });

    }
    private void loadDoctorProfile(String doctorId){


        db.collection("doctors")
                .document(doctorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {


                    Doctor doctor =
                            documentSnapshot.toObject(
                                    Doctor.class
                            );

                    if(doctor != null){

                        txtDoctorName.setText(
                                doctor.getFullName()
                        );

                        txtSpecialization.setText(
                                doctor.getSpecialization()
                        );

                        txtQualification.setText(
                                doctor.getQualification()
                        );

                        txtExperience.setText(
                                doctor.getExperience()
                        );

                        double rating = doctor.getRating();

                        ratingDoctor.setRating(
                                (float) rating
                        );

                        txtRating.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "%.1f",
                                        rating
                                )
                        );

                        if(doctor.getProfileImage() != null
                                && !doctor.getProfileImage().isEmpty()){


                            byte[] decodedBytes =
                                    Base64.decode(
                                            doctor.getProfileImage(),
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
                        else {

                            imgDoctorProfile.setImageResource(
                                    R.drawable.doctor_outline
                            );

                        }

                    }


                });


    }
    private void showRatingDialog(float rating){

        new AlertDialog.Builder(this)
                .setTitle("Submit Rating")
                .setMessage("Give this doctor " + rating + " stars?")
                .setPositiveButton("Submit", (dialog, which) -> {

                    submitRating(rating);

                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                    loadDoctorProfile(doctorId);

                })
                .show();
    }
    private void submitRating(float rating){

        String patientId =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();


        db.collection("ratings")
                .whereEqualTo(
                        "doctorId",
                        doctorId
                )
                .whereEqualTo(
                        "patientId",
                        patientId
                )
                .get()
                .addOnSuccessListener(query -> {


                    HashMap<String,Object> data =
                            new HashMap<>();


                    data.put(
                            "doctorId",
                            doctorId
                    );

                    data.put(
                            "patientId",
                            patientId
                    );

                    data.put(
                            "rating",
                            rating
                    );

                    data.put(
                            "createdAt",
                            com.google.firebase.Timestamp.now()
                    );

                    if(query.isEmpty()){

                        db.collection("ratings")
                                .add(data)
                                .addOnSuccessListener(documentReference -> {

                                    Toast.makeText(
                                            this,
                                            "Thank you for your rating",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    ratingDoctor.setIsIndicator(true);
                                    updateDoctorRating();

                                });


                    }
                    else {

                        DocumentSnapshot document =
                                query.getDocuments()
                                        .get(0);

                        db.collection("ratings")
                                .document(document.getId())
                                .update(
                                        "rating",
                                        rating
                                )
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            this,
                                            "Your rating has been updated",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    updateDoctorRating();

                                });

                    }

                });

    }
    private void updateDoctorRating(){

        db.collection("ratings")
                .whereEqualTo(
                        "doctorId",
                        doctorId
                )
                .get()
                .addOnSuccessListener(query -> {

                    double total = 0;

                    int count = query.size();

                    for(DocumentSnapshot doc:
                            query){

                        total += doc.getDouble("rating");

                    }

                    if(count == 0){
                        return;
                    }

                    double average =
                            total / count;

                    db.collection("doctors")
                            .document(doctorId)
                            .update(
                                    "rating",
                                    average
                            );

                    ratingDoctor.setRating(
                            (float) average
                    );

                    txtRating.setText(
                            String.format(
                                    "%.1f",
                                    average
                            )
                    );


                });

    }
}
