package com.example.gentletouchdentalclinic;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminPatientsFragment extends Fragment {
    private TextInputEditText edtPatientSearch;
    private LinearLayout layoutAdminPatients;
    private HorizontalScrollView patientTableScroll;
    private FirebaseFirestore db;
    private final List<DocumentSnapshot> patientAppointments =
            new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_admin_patients,
                container,
                false
        );

        edtPatientSearch =
                view.findViewById(
                        R.id.edtPatientSearch
                );

        layoutAdminPatients =
                view.findViewById(
                        R.id.layoutAdminPatients
                );

        patientTableScroll = view.findViewById(R.id.patientTableScroll);

        db = FirebaseFirestore.getInstance();

        loadPatients();

        edtPatientSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterPatients(
                                s.toString().trim()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        return view;
    }
    private void loadPatients() {

        db.collection("appointments")
                .whereEqualTo("status", "Confirmed")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    patientAppointments.clear();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        patientAppointments.add(document);
                    }

                    displayPatients(patientAppointments);

                })
                .addOnFailureListener(e -> {

                    patientAppointments.clear();

                    layoutAdminPatients.removeAllViews();

                    View emptyRow =
                            LayoutInflater.from(
                                    requireContext()
                            ).inflate(
                                    R.layout.item_admin_patient_empty,
                                    layoutAdminPatients,
                                    false
                            );

                    TextView message =
                            emptyRow.findViewById(
                                    R.id.txtNoPatientsInTable
                            );

                    message.setText(
                            "Failed to load Patients"
                    );

                    layoutAdminPatients.addView(
                            emptyRow
                    );

                    Toast.makeText(
                            requireContext(),
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
    private void displayPatients(
            List<DocumentSnapshot> documents) {

        layoutAdminPatients.removeAllViews();

        if (documents.isEmpty()) {

            View emptyRow =
                    LayoutInflater.from(
                            requireContext()
                    ).inflate(
                            R.layout.item_admin_patient_empty,
                            layoutAdminPatients,
                            false
                    );

            layoutAdminPatients.addView(
                    emptyRow
            );

            return;
        }

        for (DocumentSnapshot document :
                documents) {

            addPatientRow(document);
        }
    }
    private void addPatientRow(
            DocumentSnapshot document) {

        View row =
                LayoutInflater.from(
                        requireContext()
                ).inflate(
                        R.layout.item_admin_patient,
                        layoutAdminPatients,
                        false
                );

        TextView txtPatientName =
                row.findViewById(
                        R.id.txtAdminPatientName
                );

        TextView txtDoctor =
                row.findViewById(
                        R.id.txtAdminPatientDoctor
                );

        TextView txtDate =
                row.findViewById(
                        R.id.txtAdminPatientDate
                );

        TextView txtTime =
                row.findViewById(
                        R.id.txtAdminPatientTime
                );

        TextView txtType =
                row.findViewById(
                        R.id.txtAdminPatientType
                );

        String patientName =
                document.getString(
                        "patientName"
                );

        String doctorName =
                document.getString(
                        "doctorName"
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

        txtPatientName.setText(
                safeText(
                        patientName,
                        "Patient"
                )
        );

        txtDoctor.setText(
                safeText(
                        doctorName,
                        "Doctor"
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
                safeText(
                        appointmentType,
                        "N/A"
                )
        );

        row.setOnClickListener(v -> {

            Toast.makeText(
                    requireContext(),
                    "Patient: " +
                            safeText(
                                    patientName,
                                    "Patient"
                            ),
                    Toast.LENGTH_SHORT
            ).show();

        });

        layoutAdminPatients.addView(row);
    }
    private void filterPatients(
            String searchText) {

        if (searchText.isEmpty()) {

            displayPatients(
                    patientAppointments
            );

            return;
        }

        List<DocumentSnapshot> filteredList =
                new ArrayList<>();

        String search =
                searchText.toLowerCase();

        for (DocumentSnapshot document :
                patientAppointments) {

            String patientName =
                    document.getString(
                            "patientName"
                    );

            String doctorName =
                    document.getString(
                            "doctorName"
                    );

            String phone =
                    document.getString(
                            "phone"
                    );

            String appointmentDate =
                    document.getString(
                            "appointmentDate"
                    );

            boolean patientMatches =
                    patientName != null &&
                            patientName
                                    .toLowerCase()
                                    .contains(search);

            boolean doctorMatches =
                    doctorName != null &&
                            doctorName
                                    .toLowerCase()
                                    .contains(search);

            boolean phoneMatches =
                    phone != null &&
                            phone.contains(search);

            boolean dateMatches =
                    appointmentDate != null &&
                            appointmentDate
                                    .contains(search);

            if (patientMatches ||
                    doctorMatches ||
                    phoneMatches ||
                    dateMatches) {

                filteredList.add(
                        document
                );
            }
        }

        displayPatients(
                filteredList
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
