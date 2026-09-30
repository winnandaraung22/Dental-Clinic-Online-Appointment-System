package com.example.gentletouchdentalclinic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DoctorsFragment extends Fragment {
    RecyclerView recyclerDoctors;
    ArrayList<Doctor> doctorList;
    DoctorListAdapter adapter;
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState){

        View view =
                inflater.inflate(
                        R.layout.fragment_doctors,
                        container,
                        false
                );

        ((PatientDashboardActivity)
                requireActivity())
                .setTopBarTitle(
                        "Our Dentists"
                );

        recyclerDoctors =
                view.findViewById(
                        R.id.recyclerDoctors
                );

        recyclerDoctors.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        doctorList =
                new ArrayList<>();

        adapter =
                new DoctorListAdapter(
                        doctorList
                );

        recyclerDoctors.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadDoctors();

        return view;

    }
    private void loadDoctors() {

        String searchType = null;
        String searchValue = null;

        Bundle arguments = getArguments();

        if (arguments != null) {

            searchType =
                    arguments.getString("searchType");

            searchValue =
                    arguments.getString("searchValue");
        }


        if (searchType != null &&
                searchValue != null &&
                !searchValue.trim().isEmpty()) {

            if ("Specialization".equals(searchType)) {

                loadDoctorsBySpecialization(
                        searchValue
                );

            } else if ("Qualification".equals(searchType)) {

                loadDoctorsByQualification(
                        searchValue
                );

            } else {

                loadAllDoctors();
            }

        } else {

            loadAllDoctors();
        }
    }
    private void loadAllDoctors() {

        db.collection("doctors")
                .get()
                .addOnSuccessListener(snapshot -> {

                    doctorList.clear();

                    for (DocumentSnapshot document :
                            snapshot) {

                        Doctor doctor =
                                document.toObject(
                                        Doctor.class
                                );

                        if (doctor != null) {

                            doctor.setId(
                                    document.getId()
                            );

                            doctorList.add(doctor);
                        }
                    }

                    adapter.notifyDataSetChanged();

                });
    }
    private void loadDoctorsBySpecialization(
            String specialization) {

        db.collection("doctors")
                .whereEqualTo(
                        "specialization",
                        specialization
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    doctorList.clear();

                    for (DocumentSnapshot document :
                            snapshot) {

                        Doctor doctor =
                                document.toObject(
                                        Doctor.class
                                );

                        if (doctor != null) {

                            doctor.setId(
                                    document.getId()
                            );

                            doctorList.add(doctor);
                        }
                    }

                    adapter.notifyDataSetChanged();

                });
    }
    private void loadDoctorsByQualification(
            String qualification) {

        db.collection("doctors")
                .whereEqualTo(
                        "qualification",
                        qualification
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    doctorList.clear();

                    for (DocumentSnapshot document :
                            snapshot) {

                        Doctor doctor =
                                document.toObject(
                                        Doctor.class
                                );

                        if (doctor != null) {

                            doctor.setId(
                                    document.getId()
                            );

                            doctorList.add(doctor);
                        }
                    }

                    adapter.notifyDataSetChanged();

                });
    }
}
