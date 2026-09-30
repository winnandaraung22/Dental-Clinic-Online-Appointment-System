package com.example.gentletouchdentalclinic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PatientAppointmentDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbarAppointmentDetail;
    private MaterialButton btnCancelAppointment;
    private TextView txtAppointmentStatus;
    private TextView txtAppointmentDoctor;
    private TextView txtAppointmentSpecialization;
    private TextView txtAppointmentDate;
    private TextView txtAppointmentDay;
    private TextView txtAppointmentTime;
    private TextView txtAppointmentType;
    private TextView txtAppointmentPhone;
    private TextView txtAppointmentAddress;
    private TextView txtAppointmentReason;
    private FirebaseFirestore db;
    private String appointmentId;
    private Date appointmentDateTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_patient_appointment_detail
        );

        db = FirebaseFirestore.getInstance();

        appointmentId =
                getIntent().getStringExtra(
                        "appointmentId"
                );

        if (appointmentId == null ||
                appointmentId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Appointment not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        toolbarAppointmentDetail =
                findViewById(
                        R.id.toolbarAppointmentDetail
                );

        btnCancelAppointment =
                findViewById(
                        R.id.btnCancelAppointment
                );

        txtAppointmentStatus =
                findViewById(
                        R.id.txtAppointmentStatus
                );

        txtAppointmentDoctor =
                findViewById(
                        R.id.txtAppointmentDoctor
                );

        txtAppointmentSpecialization =
                findViewById(
                        R.id.txtAppointmentSpecialization
                );

        txtAppointmentDate =
                findViewById(
                        R.id.txtAppointmentDate
                );

        txtAppointmentDay =
                findViewById(
                        R.id.txtAppointmentDay
                );

        txtAppointmentTime =
                findViewById(
                        R.id.txtAppointmentTime
                );

        txtAppointmentType =
                findViewById(
                        R.id.txtAppointmentType
                );

        txtAppointmentPhone =
                findViewById(
                        R.id.txtAppointmentPhone
                );

        txtAppointmentAddress =
                findViewById(
                        R.id.txtAppointmentAddress
                );

        txtAppointmentReason =
                findViewById(
                        R.id.txtAppointmentReason
                );

        toolbarAppointmentDetail.setNavigationOnClickListener(
                v -> finish()
        );

        btnCancelAppointment.setOnClickListener(
                v -> showCancellationReasons()
        );

        loadAppointment();
    }

    private void loadAppointment() {

        db.collection("appointments")
                .document(appointmentId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!document.exists()) {

                                Toast.makeText(
                                        this,
                                        "Appointment not found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();

                                return;
                            }

                            displayAppointment(
                                    document
                            );

                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    this,
                                    "Failed to load appointment",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                );
    }

    private void displayAppointment(
            DocumentSnapshot document) {


        String status =
                document.getString("status");

        String doctorName =
                document.getString("doctorName");

        String specialization =
                document.getString("specialization");

        String appointmentDate =
                document.getString("appointmentDate");

        String appointmentDay =
                document.getString("appointmentDay");

        String appointmentTime =
                document.getString("appointmentTime");

        String appointmentType =
                document.getString("appointmentType");

        String phone =
                document.getString("phone");

        String address =
                document.getString("address");

        String reason =
                document.getString("reason");


        txtAppointmentStatus.setText(
                safeText(
                        status,
                        "Pending"
                )
        );

        txtAppointmentDoctor.setText(
                        safeText(
                                doctorName,
                                "Doctor Name"
                        )
        );

        txtAppointmentSpecialization.setText(
                safeText(
                        specialization,
                        "Specialization"
                )
        );

        txtAppointmentDate.setText(
                safeText(
                        appointmentDate,
                        "Date not available"
                )
        );

        txtAppointmentDay.setText(
                safeText(
                        appointmentDay,
                        "Day not available"
                )
        );

        txtAppointmentTime.setText(
                safeText(
                        appointmentTime,
                        "Time not available"
                )
        );

        txtAppointmentType.setText(
                safeText(
                        appointmentType,
                        "Not provided"
                )
        );

        txtAppointmentPhone.setText(
                safeText(
                        phone,
                        "Not provided"
                )
        );

        txtAppointmentAddress.setText(
                safeText(
                        address,
                        "Not provided"
                )
        );

        txtAppointmentReason.setText(
                safeText(
                        reason,
                        "Not provided"
                )
        );

        appointmentDateTime =
                parseAppointmentDateTime(
                        appointmentDate,
                        appointmentTime
                );

        checkCancellationEligibility(
                status
        );
    }

    private void checkCancellationEligibility(
            String status) {

        if (status == null ||
                (!status.equalsIgnoreCase("Pending")
                        &&
                        !status.equalsIgnoreCase("Confirmed"))) {

            btnCancelAppointment.setEnabled(false);

            btnCancelAppointment.setVisibility(
                    View.GONE
            );

            return;
        }

        if (appointmentDateTime == null) {

            btnCancelAppointment.setEnabled(false);

            return;
        }

        long currentTime =
                System.currentTimeMillis();

        long appointmentTimeMillis =
                appointmentDateTime.getTime();

        long twoHours =
                2 * 60 * 60 * 1000L;

        long cancellationDeadline =
                appointmentTimeMillis -
                        twoHours;

        if (currentTime <
                cancellationDeadline) {

            btnCancelAppointment.setEnabled(true);

        }
        else {

            btnCancelAppointment.setEnabled(false);

            btnCancelAppointment.setText(
                    "Cancellation Period Ended"
            );
        }
    }

    private void showCancellationReasons() {


        if (appointmentDateTime == null) {

            Toast.makeText(
                    this,
                    "Unable to check cancellation time",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        long currentTime =
                System.currentTimeMillis();


        long cancellationDeadline =
                appointmentDateTime.getTime()
                        -
                        (2 * 60 * 60 * 1000L);

        if (currentTime >=
                cancellationDeadline) {

            Toast.makeText(
                    this,
                    "Cancellation is only allowed more than 2 hours before the appointment.",
                    Toast.LENGTH_LONG
            ).show();

            btnCancelAppointment.setEnabled(false);

            btnCancelAppointment.setText(
                    "Cancellation Period Ended"
            );

            return;
        }

        String[] reasons = {

                "Time no longer convenient",

                "Change of plans",

                "Found another appointment",

                "Feeling better / no longer need appointment",

                "Transportation problem",

                "Personal reason"

        };

        new AlertDialog.Builder(this)
                .setTitle("Why are you cancelling?")
                .setSingleChoiceItems(
                        reasons,
                        -1,
                        (dialog, which) -> {

                            String selectedReason =
                                    reasons[which];

                            dialog.dismiss();

                            confirmCancellation(
                                    selectedReason
                            );

                        }
                )
                .setNegativeButton(
                        "Keep Appointment",
                        null
                )
                .show();
    }

    private void confirmCancellation(
            String cancellationReason) {


        new AlertDialog.Builder(this)
                .setTitle("Cancel Appointment?")
                .setMessage(
                        "Are you sure you want to cancel this appointment?"
                )
                .setNegativeButton(
                        "No",
                        null
                )
                .setPositiveButton(
                        "Yes",
                        (dialog, which) -> {

                            cancelAppointment(
                                    cancellationReason
                            );

                        }
                )
                .show();
    }

    private void cancelAppointment(String reason) {

        if (appointmentId == null ||
                appointmentId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Appointment ID is missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.collection("appointments")
                .document(appointmentId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        Toast.makeText(
                                this,
                                "Appointment not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    String patientId =
                            document.getString("patientId");

                    String doctorId =
                            document.getString("doctorId");

                    if (patientId == null ||
                            patientId.trim().isEmpty()) {

                        Toast.makeText(
                                this,
                                "Patient ID is missing",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if (doctorId == null ||
                            doctorId.trim().isEmpty()) {

                        Toast.makeText(
                                this,
                                "Doctor ID is missing",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Map<String, Object> updates =
                            new HashMap<>();

                    updates.put(
                            "status",
                            "Cancelled by Patient"
                    );

                    updates.put(
                            "cancelledBy",
                            "patient"
                    );

                    updates.put(
                            "cancellationReason",
                            reason
                    );

                    updates.put(
                            "cancelledAt",
                            Timestamp.now()
                    );

                    db.collection("appointments")
                            .document(appointmentId)
                            .update(updates)
                            .addOnSuccessListener(unused -> {

                                createCancellationNotification(
                                        appointmentId,
                                        patientId,
                                        doctorId,
                                        reason
                                );

                                Toast.makeText(
                                        this,
                                        "Appointment cancelled successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadAppointment();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        "Failed to cancel appointment: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to find appointment: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
    private void createCancellationNotification(
            String appointmentId,
            String patientId,
            String doctorId,
            String reason) {

        db.collection("appointments")
                .document(appointmentId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }

                    String patientName =
                            document.getString("patientName");

                    String doctorName =
                            document.getString("doctorName");

                    String appointmentDate =
                            document.getString("appointmentDate");

                    String appointmentTime =
                            document.getString("appointmentTime");

                    String patientMessage =
                            "You cancelled your appointment on "
                                    + safeText(
                                    appointmentDate,
                                    "the scheduled date"
                            )
                                    + " at "
                                    + safeText(
                                    appointmentTime,
                                    "the scheduled time"
                            )
                                    + " with "
                                    + safeText(
                                    doctorName,
                                    "your doctor"
                            )
                                    + ".";

                    String doctorMessage =
                            safeText(
                                    patientName,
                                    "A patient"
                            )
                                    + " cancelled the appointment on "
                                    + safeText(
                                    appointmentDate,
                                    "the scheduled date"
                            )
                                    + " at "
                                    + safeText(
                                    appointmentTime,
                                    "the scheduled time"
                            )
                                    + ". Reason: "
                                    + safeText(
                                    reason,
                                    "Not provided"
                            )
                                    + ".";

                    Map<String, Object> notification =
                            new HashMap<>();


                    notification.put(
                            "userId",
                            patientId
                    );

                    notification.put(
                            "doctorId",
                            doctorId
                    );

                    notification.put(
                            "appointmentId",
                            appointmentId
                    );

                    notification.put(
                            "title",
                            "Appointment Cancelled"
                    );

                    notification.put(
                            "patientMessage",
                            patientMessage
                    );

                    notification.put(
                            "doctorMessage",
                            doctorMessage
                    );

                    notification.put(
                            "type",
                            "appointment_cancelled_by_patient"
                    );

                    notification.put(
                            "createdAt",
                            Timestamp.now()
                    );

                    notification.put(
                            "isRead",
                            false
                    );

                    notification.put(
                            "doctorIsRead",
                            false
                    );

                    String notificationId =
                            "cancelled_" + appointmentId;

                    db.collection("notifications")
                            .document(notificationId)
                            .set(notification)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        this,
                                        "Cancellation notification was sent.",
                                        Toast.LENGTH_SHORT
                                ).show();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        "Appointment cancelled, but notification failed",
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to get appointment details",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    private Date parseAppointmentDateTime(
            String date,
            String time) {

        if (date == null || time == null) {
            return null;
        }

        String dateTimeString =
                date.trim() + " " + time.trim();

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "yyyy-MM-dd hh:mm a",
                        Locale.getDefault()
                );

        format.setLenient(false);

        try {

            return format.parse(
                    dateTimeString
            );

        } catch (ParseException e) {

            e.printStackTrace();

            return null;
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
}