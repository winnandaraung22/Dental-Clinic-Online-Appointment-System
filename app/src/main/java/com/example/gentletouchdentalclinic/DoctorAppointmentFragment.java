package com.example.gentletouchdentalclinic;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DoctorAppointmentFragment extends Fragment {

    private LinearLayout layoutDoctorAppointments;
    private TextView txtNoDoctorAppointments;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration appointmentsListener;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_doctor_appointments,
                container,
                false
        );

        layoutDoctorAppointments =
                view.findViewById(
                        R.id.layoutDoctorAppointments
                );

        txtNoDoctorAppointments =
                view.findViewById(
                        R.id.txtNoDoctorAppointments
                );

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        listenToDoctorAppointments();

        return view;
    }
    private void listenToDoctorAppointments() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            showEmptyMessage(
                    "Please log in again."
            );

            return;
        }

        String doctorUID = user.getUid();

        appointmentsListener =
                db.collection("appointments")

                        .whereEqualTo(
                                "doctorId",
                                doctorUID
                        )

                        .whereEqualTo(
                                "status",
                                "Confirmed"
                        )

                        .orderBy(
                                "approvedAt",
                                Query.Direction.DESCENDING
                        )

                        .addSnapshotListener(
                                (querySnapshot, error) -> {

                                    if (error != null) {

                                        showEmptyMessage(
                                                "Failed to load appointments"
                                        );

                                        Toast.makeText(
                                                requireContext(),
                                                error.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();

                                        return;
                                    }

                                    if (querySnapshot == null) {
                                        return;
                                    }

                                    layoutDoctorAppointments
                                            .removeAllViews();

                                    if (querySnapshot.isEmpty()) {

                                        showEmptyMessage(
                                                "You have no confirmed appointments."
                                        );

                                        return;
                                    }

                                    txtNoDoctorAppointments
                                            .setVisibility(
                                                    View.GONE
                                            );


                                    for (
                                            DocumentSnapshot document :
                                            querySnapshot.getDocuments()
                                    ) {

                                        addAppointmentRow(
                                                document
                                        );
                                    }

                                }
                        );
    }
    private void addAppointmentRow(
            DocumentSnapshot document) {

        View row =
                LayoutInflater.from(
                        requireContext()
                ).inflate(
                        R.layout.item_doctor_appointment,
                        layoutDoctorAppointments,
                        false
                );

        TextView txtPatientName =
                row.findViewById(
                        R.id.txtDoctorAppointmentPatientName
                );

        TextView txtPatientInfo =
                row.findViewById(
                        R.id.txtDoctorAppointmentPatientInfo
                );

        TextView txtStatus =
                row.findViewById(
                        R.id.txtDoctorAppointmentStatus
                );

        TextView txtDate =
                row.findViewById(
                        R.id.txtDoctorAppointmentDate
                );

        TextView txtTime =
                row.findViewById(
                        R.id.txtDoctorAppointmentTime
                );

        TextView txtType =
                row.findViewById(
                        R.id.txtDoctorAppointmentType
                );

        TextView txtPhone =
                row.findViewById(
                        R.id.txtDoctorAppointmentPhone
                );

        TextView txtReason =
                row.findViewById(
                        R.id.txtDoctorAppointmentReason
                );

        String patientName =
                document.getString(
                        "patientName"
                );

        Long ageValue =
                document.getLong("age");

        String age =
                ageValue != null
                        ? String.valueOf(ageValue)
                        : null;

        String gender =
                document.getString(
                        "gender"
                );

        String date =
                document.getString(
                        "appointmentDate"
                );

        String time =
                document.getString(
                        "appointmentTime"
                );

        String appointmentType =
                document.getString(
                        "appointmentType"
                );

        String phone =
                document.getString(
                        "phone"
                );

        String reason =
                document.getString(
                        "reason"
                );

        String status =
                document.getString(
                        "status"
                );

        if (isExpired(date)) {

            txtStatus.setText("Expired");
            txtStatus.setTextColor(Color.RED);

        } else {

            txtStatus.setText(
                    safeText(
                            status,
                            "Confirmed"
                    )
            );
            txtStatus.setTextColor(Color.parseColor("#B45309"));
        }

        txtPatientName.setText(
                safeText(
                        patientName,
                        "Patient"
                )
        );

        String patientInfo =
                buildPatientInfo(
                        age,
                        gender
                );

        txtPatientInfo.setText(
                patientInfo
        );

        txtStatus.setText(
                safeText(
                        status,
                        "Confirmed"
                )
        );

        txtDate.setText(
                safeText(
                        date,
                        "N/A"
                )
        );

        txtTime.setText(
                safeText(
                        time,
                        "N/A"
                )
        );

        txtType.setText(
                "Type: " +
                        safeText(
                                appointmentType,
                                "N/A"
                        )
        );

        txtPhone.setText(
                "Phone: " +
                        safeText(
                                phone,
                                "N/A"
                        )
        );

        txtReason.setText(
                "Reason: " +
                        safeText(
                                reason,
                                "N/A"
                        )
        );

        layoutDoctorAppointments
                .addView(row);
    }

    private String buildPatientInfo(
            String age,
            String gender) {

        boolean hasAge =
                age != null &&
                        !age.trim().isEmpty();

        boolean hasGender =
                gender != null &&
                        !gender.trim().isEmpty();


        if (hasAge && hasGender) {

            return "Age: " +
                    age +
                    " • " +
                    gender;
        }


        if (hasAge) {

            return "Age: " +
                    age;
        }


        if (hasGender) {

            return gender;
        }


        return "Patient information unavailable";
    }

    private void showEmptyMessage(
            String message) {

        layoutDoctorAppointments
                .removeAllViews();


        txtNoDoctorAppointments
                .setText(message);


        txtNoDoctorAppointments
                .setVisibility(
                        View.VISIBLE
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

    @Override
    public void onDestroyView() {

        super.onDestroyView();


        if (appointmentsListener != null) {

            appointmentsListener.remove();

            appointmentsListener = null;
        }
    }
}