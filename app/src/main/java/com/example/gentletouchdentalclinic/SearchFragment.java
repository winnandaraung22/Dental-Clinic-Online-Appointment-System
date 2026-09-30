package com.example.gentletouchdentalclinic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    RecyclerView recyclerSearchResult;
    ArrayList<SearchResult> searchResults;
    SearchResultAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private int searchRequestId = 0;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_search,
                container,
                false
        );
        recyclerSearchResult =
                view.findViewById(
                        R.id.recyclerSearchResult
                );

        searchResults =
                new ArrayList<>();

        recyclerSearchResult.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        adapter =
                new SearchResultAdapter(
                        searchResults
                );

        recyclerSearchResult.setAdapter(
                adapter
        );

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        return view;
    }
    public void searchData(String keyword) {

        searchRequestId++;

        int currentRequestId = searchRequestId;

        searchResults.clear();
        adapter.notifyDataSetChanged();

        if (keyword == null ||
                keyword.trim().isEmpty()) {

            return;
        }

        String searchKeyword =
                keyword.toLowerCase().trim();

        db.collection("doctors")
                .get()
                .addOnSuccessListener(query -> {

                    if (currentRequestId != searchRequestId) {
                        return;
                    }

                    for (DocumentSnapshot doc : query) {

                        String doctorName =
                                doc.getString("fullName");

                        String qualification =
                                doc.getString("qualification");

                        String specialization =
                                doc.getString("specialization");

                        String doctorId =
                                doc.getId();


                        if (doctorName != null &&
                                doctorName
                                        .toLowerCase()
                                        .contains(searchKeyword)) {

                            searchResults.add(
                                    new SearchResult(
                                            doctorId,
                                            doctorName,
                                            doctorName,
                                            "Doctor"
                                    )
                            );
                        }


                        if (qualification != null &&
                                qualification
                                        .toLowerCase()
                                        .contains(searchKeyword)) {

                            searchResults.add(
                                    new SearchResult(
                                            doctorId,
                                            doctorName,
                                            qualification,
                                            "Qualification"
                                    )
                            );
                        }


                        if (specialization != null &&
                                specialization
                                        .toLowerCase()
                                        .contains(searchKeyword)) {

                            searchResults.add(
                                    new SearchResult(
                                            doctorId,
                                            doctorName,
                                            specialization,
                                            "Specialization"
                                    )
                            );
                        }
                    }

                    searchAppointments(
                            searchKeyword,
                            currentRequestId
                    );

                })
                .addOnFailureListener(e -> {

                    searchAppointments(
                            searchKeyword,
                            currentRequestId
                    );

                });
    }
    private void searchAppointments(
            String searchKeyword,
            int currentRequestId) {

        if (auth.getCurrentUser() == null) {
            adapter.notifyDataSetChanged();
            return;
        }

        String patientId =
                auth.getCurrentUser().getUid();


        db.collection("appointments")
                .whereEqualTo(
                        "patientId",
                        patientId
                )
                .get()
                .addOnSuccessListener(query -> {

                    if (currentRequestId != searchRequestId) {
                        return;
                    }


                    for (DocumentSnapshot doc : query) {

                        String appointmentId =
                                doc.getId();

                        String doctorName =
                                doc.getString("doctorName");

                        String appointmentDate =
                                doc.getString(
                                        "appointmentDate"
                                );

                        String appointmentTime =
                                doc.getString(
                                        "appointmentTime"
                                );

                        String status =
                                doc.getString("status");

                        if (appointmentDate != null &&
                                appointmentDate
                                        .toLowerCase()
                                        .contains(searchKeyword)) {

                            searchResults.add(
                                    new SearchResult(
                                            appointmentId,
                                            doctorName,
                                            appointmentDate,
                                            "Appointment Date",
                                            appointmentDate,
                                            appointmentTime,
                                            status
                                    )
                            );
                        }

                        if (appointmentTime != null &&
                                appointmentTime
                                        .toLowerCase()
                                        .contains(searchKeyword)) {

                            searchResults.add(
                                    new SearchResult(
                                            appointmentId,
                                            doctorName,
                                            appointmentTime,
                                            "Appointment Time",
                                            appointmentDate,
                                            appointmentTime,
                                            status
                                    )
                            );
                        }

                        if (status != null &&
                                status
                                        .toLowerCase()
                                        .contains(searchKeyword)) {

                            searchResults.add(
                                    new SearchResult(
                                            appointmentId,
                                            doctorName,
                                            status,
                                            "Appointment Status",
                                            appointmentDate,
                                            appointmentTime,
                                            status
                                    )
                            );
                        }
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    adapter.notifyDataSetChanged();

                });
    }
}