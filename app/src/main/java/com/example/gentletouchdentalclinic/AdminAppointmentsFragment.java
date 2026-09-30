package com.example.gentletouchdentalclinic;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AdminAppointmentsFragment extends Fragment {
    private FirebaseFirestore db;
    private TextView btnPendingRequests;
    private TextView btnConfirmedAppointments;
    private LinearLayout pendingContent;
    private LinearLayout confirmedContent;
    private LinearLayout layoutPendingAppointments;
    private LinearLayout layoutConfirmedAppointments;
    private TextView txtPendingEmpty;
    private TextView txtConfirmedEmpty;
    private TextView btnCancellationAppointments;
    private TextView txtPendingBadge;
    private TextView txtConfirmedBadge;
    private TextView txtCancellationBadge;
    private LinearLayout cancellationContent;
    private LinearLayout layoutCancellationAppointments;
    private TextView txtCancellationEmpty;
    private MaterialButton btnCancelSelectedAppointments;
    private LinearLayout emergencyActionBar;
    private boolean emergencySelectionMode = false;
    private boolean pendingBadgeCleared = false;
    private boolean confirmedBadgeCleared = false;
    private ArrayList<String> selectedAppointmentIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_admin_appointment,
                container,
                false
        );

        db = FirebaseFirestore.getInstance();

        btnPendingRequests =
                view.findViewById(
                        R.id.btnPendingRequests
                );

        btnConfirmedAppointments =
                view.findViewById(
                        R.id.btnConfirmedAppointments
                );

        btnCancellationAppointments =
                view.findViewById(
                        R.id.btnCancellationAppointments
                );
        txtPendingBadge =
                view.findViewById(
                        R.id.txtPendingBadge
                );

        txtConfirmedBadge =
                view.findViewById(
                        R.id.txtConfirmedBadge
                );

        txtCancellationBadge =
                view.findViewById(
                        R.id.txtCancellationBadge
                );

        pendingContent =
                view.findViewById(
                        R.id.pendingContent
                );

        confirmedContent =
                view.findViewById(
                        R.id.confirmedContent
                );

        cancellationContent =
                view.findViewById(
                        R.id.cancellationContent
                );

        layoutPendingAppointments =
                view.findViewById(
                        R.id.layoutPendingAppointments
                );

        layoutConfirmedAppointments =
                view.findViewById(
                        R.id.layoutConfirmedAppointments
                );

        layoutCancellationAppointments =
                view.findViewById(
                        R.id.layoutCancellationAppointments
                );

        txtPendingEmpty =
                view.findViewById(
                        R.id.txtPendingEmpty
                );

        txtConfirmedEmpty =
                view.findViewById(
                        R.id.txtConfirmedEmpty
                );

        emergencyActionBar =
                view.findViewById(
                        R.id.emergencyActionBar
                );

        txtCancellationEmpty =
                view.findViewById(
                        R.id.txtCancellationEmpty
                );

        btnCancelSelectedAppointments =
                view.findViewById(
                        R.id.btnCancelSelectedAppointments
                );

        btnCancelSelectedAppointments.setOnClickListener(v -> {

            if (selectedAppointmentIds.isEmpty()) {

                Toast.makeText(
                        requireContext(),
                        "Please select at least one appointment.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            showEmergencyCancellationDialog();

        });

        showPendingContent();

        btnPendingRequests.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        btnConfirmedAppointments.setTypeface(
                null,
                android.graphics.Typeface.NORMAL
        );

        btnCancellationAppointments.setTypeface(
                null,
                android.graphics.Typeface.NORMAL
        );

        btnPendingRequests.setOnClickListener(v -> {

            showPendingContent();

            btnPendingRequests.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            btnConfirmedAppointments.setTypeface(
                    null,
                    android.graphics.Typeface.NORMAL
            );

            btnCancellationAppointments.setTypeface(
                    null,
                    android.graphics.Typeface.NORMAL
            );

        });
        btnConfirmedAppointments.setOnClickListener(v -> {

            showConfirmedContent();

            btnPendingRequests.setTypeface(
                    null,
                    android.graphics.Typeface.NORMAL
            );

            btnConfirmedAppointments.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            btnCancellationAppointments.setTypeface(
                    null,
                    android.graphics.Typeface.NORMAL
            );

        });
        btnCancellationAppointments.setOnClickListener(v -> {

            showCancellationContent();

            btnPendingRequests.setTypeface(
                    null,
                    android.graphics.Typeface.NORMAL
            );

            btnConfirmedAppointments.setTypeface(
                    null,
                    android.graphics.Typeface.NORMAL
            );

            btnCancellationAppointments.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

        });
        loadAppointments();

        return view;

    }
    private void showPendingContent() {

        pendingContent.setVisibility(View.VISIBLE);
        confirmedContent.setVisibility(View.GONE);
        cancellationContent.setVisibility(View.GONE);

        pendingBadgeCleared = true;

        if (txtPendingBadge != null) {
            txtPendingBadge.setVisibility(View.GONE);
        }

        emergencySelectionMode = false;
        selectedAppointmentIds.clear();

        emergencyActionBar.setVisibility(View.GONE);

        hideAllConfirmedCheckboxes();
    }
    private void showConfirmedContent() {

        pendingContent.setVisibility(View.GONE);
        confirmedContent.setVisibility(View.VISIBLE);
        cancellationContent.setVisibility(View.GONE);

        confirmedBadgeCleared = true;

        if (txtConfirmedBadge != null) {
            txtConfirmedBadge.setVisibility(View.GONE);
        }

        requireContext()
                .getSharedPreferences(
                        "appointmentBadgePreference",
                        android.content.Context.MODE_PRIVATE
                )
                .edit()
                .putLong(
                        "confirmedViewedAt",
                        System.currentTimeMillis()
                )
                .apply();

        emergencySelectionMode = false;

        selectedAppointmentIds.clear();

        emergencyActionBar.setVisibility(View.GONE);

        hideAllConfirmedCheckboxes();
    }
    private void showCancellationContent() {

        pendingContent.setVisibility(View.GONE);
        confirmedContent.setVisibility(View.GONE);
        cancellationContent.setVisibility(View.VISIBLE);

        if (txtCancellationBadge != null) {
            txtCancellationBadge.setVisibility(View.GONE);
        }

        requireContext()
                .getSharedPreferences(
                        "appointmentBadgePreference",
                        android.content.Context.MODE_PRIVATE
                )
                .edit()
                .putLong(
                        "cancellationViewedAt",
                        System.currentTimeMillis()
                )
                .apply();

        emergencySelectionMode = false;

        selectedAppointmentIds.clear();

        emergencyActionBar.setVisibility(View.GONE);

        hideAllConfirmedCheckboxes();
    }
    private void loadAppointments() {

        db.collection("appointments")
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    layoutPendingAppointments
                            .removeAllViews();

                    layoutConfirmedAppointments
                            .removeAllViews();

                    layoutCancellationAppointments
                            .removeAllViews();

                    int pendingCount = 0;
                    int confirmedCount = 0;
                    int cancellationCount = 0;

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String status =
                                document.getString(
                                        "status"
                                );

                        if (status == null) {
                            continue;
                        }

                        if (status.equalsIgnoreCase("Pending")) {

                            addPendingAppointment(
                                    document
                            );

                            pendingCount++;

                        }

                        else if (status.equalsIgnoreCase("Confirmed")) {

                            addConfirmedAppointment(
                                    document
                            );

                            confirmedCount++;

                        }

                        else if (
                                status.equalsIgnoreCase("Cancelled") ||
                                        status.equalsIgnoreCase("Cancelled by Patient")
                        ) {

                            addCancellationAppointment(
                                    document
                            );

                            cancellationCount++;

                        }
                    }
                    updateBadge(
                            txtPendingBadge,
                            pendingCount
                    );

                    int newConfirmedCount =
                            getNewConfirmedCount(
                                    querySnapshot
                            );

                    updateBadge(
                            txtConfirmedBadge,
                            newConfirmedCount
                    );

                    int newCancellationCount =
                            getNewCancellationCount(
                                    querySnapshot
                            );

                    updateBadge(
                            txtCancellationBadge,
                            newCancellationCount
                    );

                    if (pendingCount == 0) {

                        txtPendingEmpty.setVisibility(
                                View.VISIBLE
                        );

                    }
                    else {

                        txtPendingEmpty.setVisibility(
                                View.GONE
                        );

                    }

                    if (confirmedCount == 0) {

                        txtConfirmedEmpty.setVisibility(
                                View.VISIBLE
                        );

                    }
                    else {

                        txtConfirmedEmpty.setVisibility(
                                View.GONE
                        );

                    }

                    if (cancellationCount == 0) {

                        txtCancellationEmpty.setVisibility(
                                View.VISIBLE
                        );

                    }
                    else {

                        txtCancellationEmpty.setVisibility(
                                View.GONE
                        );

                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to load appointments",
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }
    private int getNewConfirmedCount(
            com.google.firebase.firestore.QuerySnapshot querySnapshot) {

        long lastViewedAt =
                requireContext()
                        .getSharedPreferences(
                                "appointmentBadgePreference",
                                android.content.Context.MODE_PRIVATE
                        )
                        .getLong(
                                "confirmedViewedAt",
                                0
                        );

        int count = 0;

        for (DocumentSnapshot document :
                querySnapshot.getDocuments()) {

            String status =
                    document.getString("status");

            if (status == null ||
                    !status.equalsIgnoreCase("Confirmed")) {
                continue;
            }

            Timestamp approvedAt =
                    document.getTimestamp("approvedAt");

            if (approvedAt != null &&
                    approvedAt.toDate().getTime() > lastViewedAt) {

                count++;
            }
        }

        return count;
    }
    private int getNewCancellationCount(
            com.google.firebase.firestore.QuerySnapshot querySnapshot) {

        long lastViewedAt =
                requireContext()
                        .getSharedPreferences(
                                "appointmentBadgePreference",
                                android.content.Context.MODE_PRIVATE
                        )
                        .getLong(
                                "cancellationViewedAt",
                                0
                        );

        int count = 0;

        for (DocumentSnapshot document :
                querySnapshot.getDocuments()) {

            String status =
                    document.getString("status");

            if (status == null) {
                continue;
            }

            if (status.equalsIgnoreCase(
                    "Cancelled by Patient"
            ) ||
                    status.equalsIgnoreCase(
                            "Cancelled"
                    )) {

                Timestamp cancelledAt =
                        document.getTimestamp(
                                "cancelledAt"
                        );

                if (cancelledAt != null &&
                        cancelledAt.toDate().getTime()
                                > lastViewedAt) {

                    count++;
                }
            }
        }

        return count;
    }
    private void updateBadge(
            TextView badge,
            int count) {

        if (badge == null) {
            return;
        }

        if (count > 0) {

            badge.setText(
                    count > 99
                            ? "99+"
                            : String.valueOf(count)
            );

            badge.setVisibility(
                    View.VISIBLE
            );

        }
        else {

            badge.setVisibility(
                    View.GONE
            );
        }
    }
    private void addCancellationAppointment(
            DocumentSnapshot document) {

        View card =
                LayoutInflater.from(
                        requireContext()
                ).inflate(
                        R.layout.item_admin_cancelled_appointment,
                        layoutCancellationAppointments,
                        false
                );

        TextView txtPatientName =
                card.findViewById(
                        R.id.txtCancelledPatientName
                );

        TextView txtStatus =
                card.findViewById(
                        R.id.txtCancelledStatus
                );

        TextView txtPatientInfo =
                card.findViewById(
                        R.id.txtCancelledPatientInfo
                );

        TextView txtDoctorName =
                card.findViewById(
                        R.id.txtCancelledDoctorName
                );

        TextView txtSpecialization =
                card.findViewById(
                        R.id.txtCancelledSpecialization
                );

        TextView txtDate =
                card.findViewById(
                        R.id.txtCancelledDate
                );

        TextView txtTime =
                card.findViewById(
                        R.id.txtCancelledTime
                );

        TextView txtType =
                card.findViewById(
                        R.id.txtCancelledType
                );

        TextView txtPhone =
                card.findViewById(
                        R.id.txtCancelledPhone
                );

        TextView txtReason =
                card.findViewById(
                        R.id.txtCancellationReason
                );

        TextView txtCancelledAt =
                card.findViewById(
                        R.id.txtCancelledAt
                );


        String patientName =
                document.getString(
                        "patientName"
                );

        String doctorName =
                document.getString(
                        "doctorName"
                );

        String specialization =
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
                        "cancellationReason"
                );

        String status =
                document.getString(
                        "status"
                );

        Long age =
                document.getLong(
                        "age"
                );

        String gender =
                document.getString(
                        "gender"
                );

        Timestamp cancelledAt =
                document.getTimestamp(
                        "cancelledAt"
                );


        txtPatientName.setText(
                safeText(
                        patientName,
                        "Patient Name"
                )
        );


        txtPatientInfo.setText(
                "Age: " +
                        (age != null ? age : "N/A") +
                        "  •  " +
                        safeText(
                                gender,
                                "N/A"
                        )
        );


        txtDoctorName.setText(
                safeText(
                        doctorName,
                        "Doctor Name"
                )
        );


        txtSpecialization.setText(
                safeText(
                        specialization,
                        "Specialization"
                )
        );


        txtDate.setText(
                "Date: " +
                        safeText(
                                date,
                                "Not provided"
                        )
        );


        txtTime.setText(
                "Time: " +
                        safeText(
                                time,
                                "Not provided"
                        )
        );


        txtType.setText(
                "Type: " +
                        safeText(
                                appointmentType,
                                "Not provided"
                        )
        );


        txtPhone.setText(
                "Phone: " +
                        safeText(
                                phone,
                                "Not provided"
                        )
        );

        txtStatus.setText(
                safeText(
                        status,
                        "Cancelled"
                )
        );

        if (status != null &&
                status.equalsIgnoreCase(
                        "Cancelled by Patient"
                )) {

            txtStatus.setTextColor(
                    Color.RED
            );

        } else {

            txtStatus.setTextColor(
                    Color.parseColor(
                            "#B45309"
                    )
            );
        }

        txtReason.setText(
                "Reason: " +
                        safeText(
                                reason,
                                "Not provided"
                        )
        );

        if (cancelledAt != null) {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.ENGLISH
                    );

            txtCancelledAt.setText(
                    "Cancelled at: " +
                            sdf.format(
                                    cancelledAt.toDate()
                            )
            );

        } else {

            txtCancelledAt.setText(
                    "Cancelled at: Not available"
            );
        }

        layoutCancellationAppointments.addView(
                card
        );
    }
    private void addPendingAppointment(
            DocumentSnapshot document) {

        View card =
                LayoutInflater.from(
                        requireContext()
                ).inflate(
                        R.layout.item_admin_pending_appointment,
                        layoutPendingAppointments,
                        false
                );

        TextView txtPatientName =
                card.findViewById(
                        R.id.txtPendingPatientName
                );

        TextView txtPatientInfo =
                card.findViewById(
                        R.id.txtPendingPatientInfo
                );

        TextView txtDoctorName =
                card.findViewById(
                        R.id.txtPendingDoctorName
                );

        TextView txtSpecialization =
                card.findViewById(
                        R.id.txtPendingSpecialization
                );

        TextView txtDate =
                card.findViewById(
                        R.id.txtPendingDate
                );

        TextView txtTime =
                card.findViewById(
                        R.id.txtPendingTime
                );

        TextView txtType =
                card.findViewById(
                        R.id.txtPendingType
                );

        TextView txtPhone =
                card.findViewById(
                        R.id.txtPendingPhone
                );

        TextView txtReason =
                card.findViewById(
                        R.id.txtPendingReason
                );

        MaterialButton btnCancel =
                card.findViewById(
                        R.id.btnCancelPending
                );

        MaterialButton btnApprove =
                card.findViewById(
                        R.id.btnApprovePending
                );

        String patientName =
                document.getString(
                        "patientName"
                );

        String doctorName =
                document.getString(
                        "doctorName"
                );

        String specialization =
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

        Long age =
                document.getLong(
                        "age"
                );

        String gender =
                document.getString(
                        "gender"
                );

        txtPatientName.setText(
                safeText(
                        patientName,
                        "Patient Name"
                )
        );

        String patientInfo =
                "Age: " +
                        (age != null ? age : "N/A") +
                        "  •  " +
                        safeText(gender, "N/A");


        txtPatientInfo.setText(
                patientInfo
        );

        txtDoctorName.setText(
                        safeText(
                                doctorName,
                                "Doctor Name"
                        )
        );

        txtSpecialization.setText(
                safeText(
                        specialization,
                        "Specialization"
                )
        );

        txtDate.setText(
                "Date: " +
                        safeText(
                                date,
                                "Not provided"
                        )
        );

        txtTime.setText(
                "Time: " +
                        safeText(
                                time,
                                "Not provided"
                        )
        );

        txtType.setText(
                "Type: " +
                        safeText(
                                appointmentType,
                                "Not provided"
                        )
        );

        txtPhone.setText(
                "Phone: " +
                        safeText(
                                phone,
                                "Not provided"
                        )
        );

        if (reason == null ||
                reason.trim().isEmpty()) {

            txtReason.setText(
                    "Reason: Not provided"
            );

        }
        else {

            txtReason.setText(
                    "Reason: " +
                            reason
            );

        }

        btnApprove.setOnClickListener(v -> {

            updateAppointmentStatus(
                    document.getId(),
                    "Confirmed"
            );

        });

        btnCancel.setOnClickListener(v -> {

            showCancellationDialog(
                    document.getId(),
                    document.getString("patientId")
            );

        });

        layoutPendingAppointments.addView(
                card
        );

    }
    private void addConfirmedAppointment(
            DocumentSnapshot document) {

        View card =
                LayoutInflater.from(
                        requireContext()
                ).inflate(
                        R.layout.item_admin_confirmed_appointment,
                        layoutConfirmedAppointments,
                        false
                );

        CheckBox checkConfirmedAppointment =
                card.findViewById(
                        R.id.checkConfirmedAppointment
                );

        boolean expired = isExpiredDate(
                document.getString("appointmentDate")
        );

        if (emergencySelectionMode && !expired) {

            checkConfirmedAppointment.setVisibility(
                    View.VISIBLE
            );

        } else {

            checkConfirmedAppointment.setVisibility(
                    View.GONE
            );

            checkConfirmedAppointment.setChecked(false);
        }
        checkConfirmedAppointment.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    String appointmentId =
                            document.getId();

                    if (isChecked) {

                        if (!selectedAppointmentIds.contains(
                                appointmentId)) {

                            selectedAppointmentIds.add(
                                    appointmentId
                            );
                        }

                    } else {

                        selectedAppointmentIds.remove(
                                appointmentId
                        );
                    }

                    updateCancelSelectedButton();

                }
        );
        card.setOnClickListener(v -> {

            String date =
                    document.getString(
                            "appointmentDate"
                    );

            if (isExpiredDate(date)) {

                Toast.makeText(
                        requireContext(),
                        "Expired appointments cannot be selected.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!emergencySelectionMode) {

                emergencySelectionMode = true;

                showAllConfirmedCheckboxes();

                updateCancelSelectedButton();
            }

        });

        TextView txtPatientName =
                card.findViewById(
                        R.id.txtConfirmedPatientName
                );


        TextView txtStatus =
                card.findViewById(
                        R.id.txtConfirmedStatus
                );


        TextView txtPatientInfo =
                card.findViewById(
                        R.id.txtConfirmedPatientInfo
                );


        TextView txtDoctorName =
                card.findViewById(
                        R.id.txtConfirmedDoctorName
                );


        TextView txtSpecialization =
                card.findViewById(
                        R.id.txtConfirmedSpecialization
                );


        TextView txtDate =
                card.findViewById(
                        R.id.txtConfirmedDate
                );


        TextView txtTime =
                card.findViewById(
                        R.id.txtConfirmedTime
                );


        TextView txtType =
                card.findViewById(
                        R.id.txtConfirmedType
                );


        TextView txtPhone =
                card.findViewById(
                        R.id.txtConfirmedPhone
                );


        TextView txtReason =
                card.findViewById(
                        R.id.txtConfirmedReason
                );

        String patientName =
                document.getString(
                        "patientName"
                );

        String doctorName =
                document.getString(
                        "doctorName"
                );

        String specialization =
                document.getString(
                        "specialization"
                );

        String date =
                document.getString(
                        "appointmentDate"
                );

        card.setTag(date);

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

        Long age =
                document.getLong(
                        "age"
                );

        String gender =
                document.getString(
                        "gender"
                );

        txtPatientName.setText(
                safeText(
                        patientName,
                        "Patient Name"
                )
        );

        txtPatientInfo.setText(
                "Age: " +
                        (age != null ? age : "N/A") +
                        "  •  " +
                        safeText(gender, "N/A")
        );

        if (isExpiredDate(date)) {

            txtStatus.setText("Expired");
            txtStatus.setTextColor(Color.RED);

        } else {

            txtStatus.setText("Confirmed");
            txtStatus.setTextColor(Color.parseColor("#B45309"));
        }

        txtDoctorName.setText(
                        safeText(
                                doctorName,
                                "Doctor Name"
                        )
        );

        txtSpecialization.setText(
                safeText(
                        specialization,
                        "Specialization"
                )
        );

        txtDate.setText(
                "Date: " +
                        safeText(
                                date,
                                "Not provided"
                        )
        );

        txtTime.setText(
                "Time: " +
                        safeText(
                                time,
                                "Not provided"
                        )
        );

        txtType.setText(
                "Type: " +
                        safeText(
                                appointmentType,
                                "Not provided"
                        )
        );

        txtPhone.setText(
                "Phone: " +
                        safeText(
                                phone,
                                "Not provided"
                        )
        );

        if (reason == null ||
                reason.trim().isEmpty()) {

            txtReason.setText(
                    "Reason: Not provided"
            );

        }
        else {

            txtReason.setText(
                    "Reason: " +
                            reason
            );

        }

        layoutConfirmedAppointments.addView(
                card
        );

    }
    private void showAllConfirmedCheckboxes() {

        int childCount =
                layoutConfirmedAppointments.getChildCount();

        for (int i = 0; i < childCount; i++) {

            View card =
                    layoutConfirmedAppointments.getChildAt(i);

            CheckBox checkbox =
                    card.findViewById(
                            R.id.checkConfirmedAppointment
                    );

            if (checkbox == null) {
                continue;
            }

            String date =
                    (String) card.getTag();

            if (isExpiredDate(date)) {

                checkbox.setChecked(false);

                checkbox.setVisibility(
                        View.GONE
                );

            }

            else {

                checkbox.setVisibility(
                        View.VISIBLE
                );
            }
        }
    }
    private void updateCancelSelectedButton() {

        if (!emergencySelectionMode ||
                selectedAppointmentIds.isEmpty()) {

            emergencyActionBar.setVisibility(
                    View.GONE
            );

            return;
        }

        emergencyActionBar.setVisibility(
                View.VISIBLE
        );

        int count =
                selectedAppointmentIds.size();

        btnCancelSelectedAppointments.setText(
                "Cancel " +
                        count +
                        " Selected Appointment" +
                        (count > 1 ? "s" : "")
        );
    }
    private void showEmergencyCancellationDialog() {

        View dialogView =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.dialog_emergency_cancellation,
                                null
                        );

        RadioGroup radioGroup =
                dialogView.findViewById(
                        R.id.radioEmergencyCancellationReason
                );

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(
                        requireContext()
                )
                        .setTitle(
                                "Emergency Cancellation"
                        )
                        .setView(dialogView)
                        .setNegativeButton(
                                "Back",
                                null
                        )
                        .setPositiveButton(
                                "Cancel Selected Appointments",
                                null
                        )
                        .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(
                    androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                int selectedId =
                        radioGroup.getCheckedRadioButtonId();

                if (selectedId == -1) {

                    Toast.makeText(
                            requireContext(),
                            "Please select a cancellation reason",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                RadioButton selectedRadio =
                        dialogView.findViewById(
                                selectedId
                        );

                String reason =
                        selectedRadio
                                .getText()
                                .toString();

                dialog.dismiss();

                cancelSelectedAppointments(
                        reason
                );

            });

        });

        dialog.show();
    }
    private void hideAllConfirmedCheckboxes() {

        int childCount =
                layoutConfirmedAppointments.getChildCount();

        for (int i = 0; i < childCount; i++) {

            View card =
                    layoutConfirmedAppointments.getChildAt(i);

            CheckBox checkbox =
                    card.findViewById(
                            R.id.checkConfirmedAppointment
                    );

            if (checkbox != null) {

                checkbox.setChecked(false);

                checkbox.setVisibility(
                        View.GONE
                );
            }
        }
    }
    private void cancelSelectedAppointments(
            String reason) {

        if (selectedAppointmentIds.isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "No appointments selected.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int total =
                selectedAppointmentIds.size();

        final int[] completed =
                {0};

        for (String appointmentId :
                new ArrayList<>(
                        selectedAppointmentIds)) {

            db.collection("appointments")
                    .document(appointmentId)
                    .get()
                    .addOnSuccessListener(document -> {

                        if (!document.exists()) {

                            completed[0]++;

                            checkEmergencyCancellationComplete(
                                    completed[0],
                                    total
                            );

                            return;
                        }

                        String patientId =
                                document.getString(
                                        "patientId"
                                );

                        Map<String, Object> updates =
                                new HashMap<>();

                        updates.put(
                                "status",
                                "Cancelled"
                        );

                        updates.put(
                                "cancelledBy",
                                "admin"
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

                                    if (patientId != null &&
                                            !patientId.trim().isEmpty()) {

                                        createEmergencyCancellationNotification(
                                                appointmentId,
                                                patientId,
                                                reason
                                        );
                                    }

                                    completed[0]++;

                                    checkEmergencyCancellationComplete(
                                            completed[0],
                                            total
                                    );

                                })
                                .addOnFailureListener(e -> {

                                    completed[0]++;

                                    checkEmergencyCancellationComplete(
                                            completed[0],
                                            total
                                    );

                                    Toast.makeText(
                                            requireContext(),
                                            "Failed to cancel one appointment.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                });

                    })
                    .addOnFailureListener(e -> {

                        completed[0]++;

                        checkEmergencyCancellationComplete(
                                completed[0],
                                total
                        );

                    });
        }
    }
    private void checkEmergencyCancellationComplete(
            int completed,
            int total) {

        if (completed >= total) {

            Toast.makeText(
                    requireContext(),
                    "Selected appointments cancelled successfully.",
                    Toast.LENGTH_LONG
            ).show();

            emergencySelectionMode = false;

            selectedAppointmentIds.clear();

            emergencyActionBar.setVisibility(
                    View.GONE
            );

            loadAppointments();
        }
    }
    private void createEmergencyCancellationNotification(
            String appointmentId,
            String patientId,
            String reason) {
        db.collection("appointments")
                .document(appointmentId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }

                    String date =
                            document.getString("appointmentDate");

                    String time =
                            document.getString("appointmentTime");

                    String doctorName =
                            document.getString("doctorName");

                    String message =
                            "Your appointment on "
                                    + safeText(date, "the scheduled date")
                                    + " at "
                                    + safeText(time, "the scheduled time")
                                    + " with "
                                    + safeText(doctorName, "your doctor")
                                    + " has been cancelled by the clinic due to an emergency. Reason: "
                                    + safeText(reason, "Not provided")
                                    + ".";

                    Map<String, Object> notification =
                            new HashMap<>();

                    notification.put(
                            "userId",
                            patientId
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
                            message
                    );

                    notification.put(
                            "type",
                            "emergency_appointment_cancelled"
                    );

                    notification.put(
                            "createdAt",
                            Timestamp.now()
                    );

                    notification.put(
                            "isRead",
                            false
                    );

                    db.collection("notifications")
                            .add(notification)
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Appointment cancelled, but notification could not be created.",
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to get appointment details",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
    private void updateAppointmentStatus(
            String appointmentId,
            String newStatus) {

        if (newStatus.equals("Confirmed")) {

            db.collection("appointments")
                    .document(appointmentId)
                    .get()
                    .addOnSuccessListener(document -> {

                        if (!document.exists()) {

                            Toast.makeText(
                                    requireContext(),
                                    "Appointment not found",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        String patientId =
                                document.getString("patientId");

                        Map<String, Object> updates =
                                new HashMap<>();

                        updates.put(
                                "status",
                                "Confirmed"
                        );

                        updates.put(
                                "approvedAt",
                                Timestamp.now()
                        );

                        db.collection("appointments")
                                .document(appointmentId)
                                .update(updates)
                                .addOnSuccessListener(unused -> {

                                    createApprovalNotification(
                                            appointmentId,
                                            patientId
                                    );

                                    String appointmentDate =
                                            document.getString(
                                                    "appointmentDate"
                                            );

                                    String appointmentTime =
                                            document.getString(
                                                    "appointmentTime"
                                            );

                                    String doctorName =
                                            document.getString(
                                                    "doctorName"
                                            );

                                    if (appointmentDate != null &&
                                            appointmentTime != null &&
                                            doctorName != null) {

                                        AppointmentReminderScheduler
                                                .scheduleReminder(
                                                        requireContext(),
                                                        appointmentId,
                                                        appointmentDate,
                                                        appointmentTime,
                                                        doctorName
                                                );
                                    }

                                    Toast.makeText(
                                            requireContext(),
                                            "Appointment approved",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    loadAppointments();

                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(
                                            requireContext(),
                                            "Failed to approve appointment",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                });

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                requireContext(),
                                "Failed to find appointment",
                                Toast.LENGTH_SHORT
                        ).show();

                    });
        }

        else if (newStatus.equals("Cancelled")) {

            db.collection("appointments")
                    .document(appointmentId)
                    .update(
                            "status",
                            "Cancelled"
                    )
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                requireContext(),
                                "Appointment cancelled",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadAppointments();

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                requireContext(),
                                "Failed to cancel appointment",
                                Toast.LENGTH_SHORT
                        ).show();

                    });
        }
    }
    private void createApprovalNotification(
            String appointmentId,
            String patientId) {
        if (patientId == null ||
                patientId.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Patient ID is missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.collection("appointments")
                .document(appointmentId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }

                    String date =
                            document.getString("appointmentDate");

                    String time =
                            document.getString("appointmentTime");

                    String doctorName =
                            document.getString("doctorName");

                    String patientMessage =
                            "Your appointment on "
                                    + safeText(date, "the scheduled date")
                                    + " at "
                                    + safeText(time, "the scheduled time")
                                    + " with "
                                    + safeText(doctorName, "your doctor")
                                    + " has been approved.";

                    Map<String, Object> notification =
                            new HashMap<>();

                    notification.put(
                            "userId",
                            patientId
                    );

                    notification.put(
                            "appointmentId",
                            appointmentId
                    );

                    notification.put(
                            "title",
                            "Appointment Approved"
                    );

                    notification.put(
                            "patientMessage",
                            patientMessage
                    );

                    notification.put(
                            "type",
                            "appointment_approved"
                    );

                    notification.put(
                            "createdAt",
                            Timestamp.now()
                    );

                    notification.put(
                            "isRead",
                            false
                    );

                    db.collection("notifications")
                            .add(notification)
                            .addOnSuccessListener(documentReference -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Notification was sent to patient",
                                        Toast.LENGTH_SHORT
                                ).show();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Appointment approved, but notification failed",
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to get appointment details",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    private boolean isExpiredDate(String dateString) {

        if (dateString == null || dateString.trim().isEmpty()) {
            return false;
        }

        try {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.ENGLISH
                    );

            sdf.setLenient(false);

            Date appointmentDate =
                    sdf.parse(dateString);

            Date today =
                    sdf.parse(
                            new SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.ENGLISH
                            ).format(new Date())
                    );

            return appointmentDate.before(today);

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
    private void showCancellationDialog(
            String appointmentId,
            String patientId) {

        View dialogView =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.dialog_cancel_appointment,
                                null
                        );

        RadioGroup radioGroup =
                dialogView.findViewById(
                        R.id.radioCancellationReason
                );

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(
                        requireContext()
                )
                        .setTitle("Cancel Appointment")
                        .setView(dialogView)
                        .setNegativeButton(
                                "Back",
                                null
                        )
                        .setPositiveButton(
                                "Cancel Appointment",
                                null
                        )
                        .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(
                    androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                int selectedId =
                        radioGroup.getCheckedRadioButtonId();

                if (selectedId == -1) {

                    Toast.makeText(
                            requireContext(),
                            "Please select a cancellation reason",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                RadioButton selectedRadio =
                        dialogView.findViewById(
                                selectedId
                        );

                String reason =
                        selectedRadio
                                .getText()
                                .toString();

                cancelAppointment(
                        appointmentId,
                        patientId,
                        reason
                );

                dialog.dismiss();

            });

        });

        dialog.show();
    }
    private void cancelAppointment(
            String appointmentId,
            String patientId,
            String reason) {

        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "status",
                "Cancelled"
        );

        updates.put(
                "cancelledBy",
                "admin"
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
                            reason
                    );

                    Toast.makeText(
                            requireContext(),
                            "Appointment cancelled",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadAppointments();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to cancel appointment",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
    private void createCancellationNotification(
            String appointmentId,
            String patientId,
            String reason) {
        db.collection("appointments")
                .document(appointmentId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }

                    String date =
                            document.getString("appointmentDate");

                    String time =
                            document.getString("appointmentTime");

                    String doctorName =
                            document.getString("doctorName");

                    String message =
                            "Your appointment on "
                                    + safeText(date, "the scheduled date")
                                    + " at "
                                    + safeText(time, "the scheduled time")
                                    + " with "
                                    + safeText(doctorName, "your doctor")
                                    + " has been cancelled. Reason: "
                                    + safeText(reason, "Not provided")
                                    + ".";

                    Map<String, Object> notification =
                            new HashMap<>();

                    notification.put(
                            "userId",
                            patientId
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
                            "message",
                            message
                    );

                    notification.put(
                            "type",
                            "appointment_cancelled"
                    );

                    notification.put(
                            "createdAt",
                            Timestamp.now()
                    );

                    notification.put(
                            "isRead",
                            false
                    );

                    db.collection("notifications")
                            .add(notification)
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Appointment cancelled, but notification could not be created",
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to get appointment details",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
}