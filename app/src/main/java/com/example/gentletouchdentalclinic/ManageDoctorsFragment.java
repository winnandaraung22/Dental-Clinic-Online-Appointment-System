package com.example.gentletouchdentalclinic;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ManageDoctorsFragment extends Fragment {

    private RecyclerView recyclerDoctors;
    private ArrayList<Doctor> doctorList;
    private ManageDoctorAdapter adapter;
    private FirebaseFirestore db;
    private FloatingActionButton btnAddDoctor;

    public ManageDoctorsFragment(){

    }
    @Override
    public void onResume() {
        super.onResume();
        if(db != null){
            loadDoctors();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){


        View view = inflater.inflate(
                R.layout.fragment_manage_doctors,
                container,
                false
        );
        recyclerDoctors =
                view.findViewById(R.id.recyclerDoctors);

        btnAddDoctor = view.findViewById(R.id.btnAddDoctor);

        db = FirebaseFirestore.getInstance();

        doctorList = new ArrayList<>();

        adapter =
                new ManageDoctorAdapter(
                        doctorList,
                        new ManageDoctorAdapter.OnDoctorActionListener() {


                            @Override
                            public void onEditClick(Doctor doctor) {

                                Intent intent =
                                        new Intent(getActivity(), EditDoctorActivity.class);

                                intent.putExtra("doctorId", doctor.getId());

                                startActivity(intent);

                            }


                            @Override
                            public void onDeleteClick(Doctor doctor) {

                                showDeleteConfirmation(doctor);

                            }

                        });


        recyclerDoctors.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerDoctors.setAdapter(adapter);

        btnAddDoctor.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            requireContext(),
                            AddDoctorActivity.class
                    );

            startActivity(intent);

        });

        return view;

    }

    private void loadDoctors(){

        if(db == null || doctorList == null){
            return;
        }

        db.collection("doctors")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    doctorList.clear();

                    for(DocumentSnapshot document
                            : queryDocumentSnapshots){

                        Doctor doctor =
                                document.toObject(Doctor.class);

                        if(doctor != null){

                            doctor.setId(document.getId());

                            Double rating = document.getDouble("rating");
                            doctor.setRating(rating);

                            doctorList.add(doctor);

                        }
                    }

                    if (adapter != null && isAdded()) {
                        adapter.notifyDataSetChanged();
                    }

                });

    }
    private void showDeleteConfirmation(Doctor doctor){
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())

                .setTitle("Delete Doctor")

                .setMessage(
                        "Are you sure you want to delete "
                                + doctor.getFullName()
                                + "? \n All related schedules will also be deleted."
                )

                .setPositiveButton(
                        "Yes",
                        (dialog, which) -> {

                            deleteDoctor(doctor);

                        }
                )

                .setNegativeButton(
                        "No",
                        (dialog, which) -> {

                            Toast.makeText(
                                    requireContext(),
                                    "Deletion Cancelled!",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                )

                .show();
    }
    private void deleteDoctor(Doctor doctor) {

        String doctorId = doctor.getId();

        if (doctorId == null || doctorId.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Doctor ID is missing.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.collection("doctorSchedules")
                .whereEqualTo(
                        "doctorDocumentID",
                        doctorId
                )
                .get()
                .addOnSuccessListener(scheduleSnapshot -> {

                    for (DocumentSnapshot document :
                            scheduleSnapshot.getDocuments()) {

                        document.getReference().delete();
                    }

                    db.collection("appointments")
                            .whereEqualTo(
                                    "doctorId",
                                    doctorId
                            )
                            .get()
                            .addOnSuccessListener(appointmentSnapshot -> {

                                deleteAppointmentNotifications(
                                        appointmentSnapshot,
                                        doctorId,
                                        () -> deleteDoctorRatings(
                                                doctorId,
                                                () -> deleteDoctorChats(
                                                        doctorId,
                                                        () -> deleteDoctorRecords(
                                                                doctorId
                                                        )
                                                )
                                        )
                                );

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Failed to find appointments: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to delete schedules: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
    private void deleteAppointmentNotifications(
            com.google.firebase.firestore.QuerySnapshot appointmentSnapshot,
            String doctorId,
            Runnable onComplete) {

        if (appointmentSnapshot.isEmpty()) {

            if (onComplete != null) {
                onComplete.run();
            }

            return;
        }

        final int totalAppointments =
                appointmentSnapshot.size();

        final int[] processedAppointments =
                {0};

        for (DocumentSnapshot appointment :
                appointmentSnapshot.getDocuments()) {

            String appointmentId =
                    appointment.getId();

            db.collection("notifications")
                    .whereEqualTo(
                            "appointmentId",
                            appointmentId
                    )
                    .get()
                    .addOnSuccessListener(notificationSnapshot -> {

                        for (DocumentSnapshot notification :
                                notificationSnapshot.getDocuments()) {

                            notification
                                    .getReference()
                                    .delete();
                        }

                        appointment
                                .getReference()
                                .delete()
                                .addOnSuccessListener(unused -> {

                                    processedAppointments[0]++;

                                    if (processedAppointments[0]
                                            >= totalAppointments) {

                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    }

                                })
                                .addOnFailureListener(e -> {

                                    processedAppointments[0]++;

                                    if (processedAppointments[0]
                                            >= totalAppointments) {

                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    }

                                    Toast.makeText(
                                            requireContext(),
                                            "Failed to delete an appointment.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                });

                    })
                    .addOnFailureListener(e -> {

                        processedAppointments[0]++;

                        if (processedAppointments[0]
                                >= totalAppointments) {

                            if (onComplete != null) {
                                onComplete.run();
                            }
                        }

                        Toast.makeText(
                                requireContext(),
                                "Failed to find appointment notifications: "
                                        + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                    });
        }
    }
    private void deleteDoctorRecords(String doctorId) {

        db.collection("doctors")
                .document(doctorId)
                .delete()
                .addOnSuccessListener(unused -> {

                    db.collection("users")
                            .document(doctorId)
                            .delete()
                            .addOnSuccessListener(unused2 -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Doctor and related data deleted successfully",
                                        Toast.LENGTH_LONG
                                ).show();

                                loadDoctors();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Doctor deleted, but user record could not be deleted.",
                                        Toast.LENGTH_LONG
                                ).show();

                                loadDoctors();

                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to delete doctor: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
    private void deleteDoctorChats(
            String doctorId,
            Runnable onComplete) {

        db.collection("chats")
                .whereEqualTo(
                        "doctorId",
                        doctorId
                )
                .get()
                .addOnSuccessListener(chatSnapshot -> {

                    if (chatSnapshot.isEmpty()) {

                        if (onComplete != null) {
                            onComplete.run();
                        }

                        return;
                    }

                    deleteChatDocuments(
                            chatSnapshot.getDocuments(),
                            0,
                            onComplete
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to find doctor chats: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
    private void deleteChatDocuments(
            List<DocumentSnapshot> chatDocuments,
            int index,
            Runnable onComplete) {

        if (index >= chatDocuments.size()) {

            if (onComplete != null) {
                onComplete.run();
            }

            return;
        }

        DocumentSnapshot chatDocument =
                chatDocuments.get(index);

        String chatId =
                chatDocument.getId();

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .get()
                .addOnSuccessListener(messageSnapshot -> {

                    com.google.firebase.firestore.WriteBatch batch =
                            db.batch();

                    for (DocumentSnapshot message :
                            messageSnapshot.getDocuments()) {

                        batch.delete(
                                message.getReference()
                        );
                    }

                    batch.commit()
                            .addOnSuccessListener(unused -> {

                                db.collection("chats")
                                        .document(chatId)
                                        .delete()
                                        .addOnSuccessListener(
                                                unused2 -> {

                                                    deleteChatDocuments(
                                                            chatDocuments,
                                                            index + 1,
                                                            onComplete
                                                    );

                                                }
                                        )
                                        .addOnFailureListener(e -> {

                                            Toast.makeText(
                                                    requireContext(),
                                                    "Failed to delete chat: "
                                                            + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();

                                        });

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Failed to delete chat messages: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to load chat messages: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
    private void deleteDoctorRatings(
            String doctorId,
            Runnable onComplete) {

        db.collection("ratings")
                .whereEqualTo(
                        "doctorId",
                        doctorId
                )
                .get()
                .addOnSuccessListener(ratingSnapshot -> {

                    if (ratingSnapshot.isEmpty()) {

                        if (onComplete != null) {
                            onComplete.run();
                        }

                        return;
                    }

                    com.google.firebase.firestore.WriteBatch batch =
                            db.batch();

                    for (DocumentSnapshot rating :
                            ratingSnapshot.getDocuments()) {

                        batch.delete(
                                rating.getReference()
                        );
                    }

                    batch.commit()
                            .addOnSuccessListener(unused -> {

                                if (onComplete != null) {
                                    onComplete.run();
                                }

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        requireContext(),
                                        "Failed to delete doctor ratings: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to find doctor ratings: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }

}
