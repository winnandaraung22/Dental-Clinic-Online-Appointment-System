package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PatientAppointmentsActivity extends AppCompatActivity {

    private LinearLayout layoutPatientAppointments;
    private TextView txtNoAppointments;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    MaterialToolbar toolbarPatientAppointments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_patient_appointments
        );

        layoutPatientAppointments =
                findViewById(
                        R.id.layoutPatientAppointments
                );

        txtNoAppointments =
                findViewById(
                        R.id.txtNoAppointments
                );

        toolbarPatientAppointments = findViewById(R.id.toolbarPatientAppointments);

        toolbarPatientAppointments.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(
                    PatientAppointmentsActivity.this,
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

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadPatientAppointments();
    }

    private void loadPatientAppointments() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        String patientId =
                user.getUid();

        db.collection("appointments")
                .whereEqualTo(
                        "patientId",
                        patientId
                )
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    layoutPatientAppointments
                            .removeAllViews();

                    if (querySnapshot.isEmpty()) {

                        txtNoAppointments.setVisibility(
                                View.VISIBLE
                        );

                        return;
                    }

                    txtNoAppointments.setVisibility(
                            View.GONE
                    );

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        addAppointmentCard(
                                document
                        );
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load appointments",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    private void addAppointmentCard(
            DocumentSnapshot document) {

        View card =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.item_patient_appointment,
                                layoutPatientAppointments,
                                false
                        );

        TextView txtDoctor =
                card.findViewById(
                        R.id.txtPatientAppointmentDoctor
                );

        TextView txtSpecialization =
                card.findViewById(
                        R.id.txtPatientAppointmentSpecialization
                );

        TextView txtDate =
                card.findViewById(
                        R.id.txtPatientAppointmentDate
                );

        TextView txtTime =
                card.findViewById(
                        R.id.txtPatientAppointmentTime
                );

        TextView txtStatus =
                card.findViewById(
                        R.id.txtPatientAppointmentStatus
                );

        String doctorName =
                document.getString(
                        "doctorName"
                );

        String doctorSpecialization =
                document.getString(
                        "specialization"
                );

        String date =
                document.getString(
                        "appointmentDate"
                );

        String time =
                document.getString(
                        "appointmentTime"
                );

        String status =
                document.getString(
                        "status"
                );

        if ("Confirmed".equalsIgnoreCase(status)
                && date != null
                && time != null) {

            AppointmentReminderScheduler.scheduleReminder(
                    this,
                    document.getId(),
                    date,
                    time,
                    doctorName
            );
        }

        txtDoctor.setText(
                        safeText(
                                doctorName,
                                "Doctor Name"
                        )
        );

        txtSpecialization.setText(
                safeText(
                        doctorSpecialization,
                        "Specialization not available"
                )
        );

        txtDate.setText(
                safeText(
                        date,
                        "Date not available"
                )
        );

        txtTime.setText(
                safeText(
                        time,
                        "Time not available"
                )
        );

        if (isExpired(date)) {

            txtStatus.setText("Expired");
            txtStatus.setTextColor(Color.RED);

        } else {

            txtStatus.setText(
                    safeText(
                            status,
                            "Pending"
                    )
            );
            txtStatus.setTextColor(Color.parseColor("#B45309"));
        }

        card.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            PatientAppointmentsActivity.this,
                            PatientAppointmentDetailActivity.class
                    );

            intent.putExtra(
                    "appointmentId",
                    document.getId()
            );

            startActivity(intent);

        });

        layoutPatientAppointments.addView(
                card
        );
    }
    private boolean isExpired(String dateString) {

        try {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.ENGLISH
                    );

            sdf.setLenient(false);

            Date appointmentDate =
                    sdf.parse(dateString);

            Calendar today =
                    Calendar.getInstance();

            today.set(
                    Calendar.HOUR_OF_DAY,
                    0
            );
            today.set(
                    Calendar.MINUTE,
                    0
            );
            today.set(
                    Calendar.SECOND,
                    0
            );
            today.set(
                    Calendar.MILLISECOND,
                    0
            );

            return appointmentDate.before(
                    today.getTime()
            );

        } catch (Exception e) {

            return false;
        }
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

    @Override
    protected void onResume() {
        super.onResume();

        loadPatientAppointments();
    }
}