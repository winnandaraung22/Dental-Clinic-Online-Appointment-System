package com.example.gentletouchdentalclinic;

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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class DoctorHomeFragment extends Fragment {

    RecyclerView recyclerDoctorSchedule;
    ArrayList<DoctorSchedule> scheduleList;
    DoctorDutyScheduleAdapter scheduleAdapter;
    RecyclerView recyclerDoctorPending;
    ArrayList<DoctorPendingAppointment> pendingAppointmentList;
    DoctorPendingAppointmentAdapter pendingAppointmentAdapter;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private TextView txtTodayAppointmentCount;
    private TextView txtUpcomingCount;
    private ListenerRegistration pendingAppointmentsListener;
    private TextView txtNoPendingRequests;

    public DoctorHomeFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_doctor_home,
                container,
                false
        );

        recyclerDoctorSchedule =
                view.findViewById(
                        R.id.recyclerDoctorSchedule
                );

        scheduleList = new ArrayList<>();

        scheduleAdapter =
                new DoctorDutyScheduleAdapter(
                        scheduleList
                );

        recyclerDoctorSchedule.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerDoctorSchedule.setAdapter(
                scheduleAdapter
        );

        recyclerDoctorPending =
                view.findViewById(
                        R.id.recyclerDoctorPending
                );

        pendingAppointmentList =
                new ArrayList<>();

        pendingAppointmentAdapter =
                new DoctorPendingAppointmentAdapter(
                        pendingAppointmentList
                );

        recyclerDoctorPending.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerDoctorPending.setAdapter(
                pendingAppointmentAdapter
        );

        txtNoPendingRequests =
                view.findViewById(
                        R.id.txtNoPendingRequests
                );

        txtTodayAppointmentCount =
                view.findViewById(
                        R.id.txtTodayAppointmentCount
                );

        txtUpcomingCount =
                view.findViewById(
                        R.id.txtUpcomingCount
                );


        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();


        loadDoctorSchedule();

        listenToDoctorPendingAppointments();

        loadDoctorAppointmentCounts();

        return view;

    }
    private void loadDoctorSchedule(){


        FirebaseUser user =
                auth.getCurrentUser();

        if(user == null){
            return;
        }

        String doctorUID =
                user.getUid();

        db.collection("doctorSchedules")
                .whereEqualTo(
                        "doctorDocumentID",
                        doctorUID
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    scheduleList.clear();

                    for(DocumentSnapshot document :
                            querySnapshot){

                        DoctorSchedule schedule =
                                document.toObject(
                                        DoctorSchedule.class
                                );

                        if(schedule != null){

                            schedule.setId(
                                    document.getId()
                            );

                            String status = schedule.getStatus();
                            if(status != null && status.equalsIgnoreCase("AVAILABLE")) {
                                scheduleList.add(
                                        schedule
                                );
                            }

                        }

                    }

                    scheduleAdapter.notifyDataSetChanged();

                });

    }
    private void listenToDoctorPendingAppointments() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            return;
        }

        String doctorUID = user.getUid();

        pendingAppointmentsListener =
                db.collection("appointments")
                        .whereEqualTo(
                                "doctorId",
                                doctorUID
                        )
                        .whereEqualTo(
                                "status",
                                "Pending"
                        )
                        .orderBy(
                                "createdAt",
                                Query.Direction.DESCENDING
                        )
                        .addSnapshotListener(
                                (querySnapshot, error) -> {

                                    if (error != null) {

                                        Toast.makeText(
                                                requireContext(),
                                                "Failed: " +
                                                        error.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();

                                        return;
                                    }

                                    if (querySnapshot == null) {
                                        return;
                                    }

                                    pendingAppointmentList.clear();

                                    for (DocumentSnapshot document :
                                            querySnapshot.getDocuments()) {

                                        DoctorPendingAppointment appointment =
                                                document.toObject(
                                                        DoctorPendingAppointment.class
                                                );

                                        if (appointment != null) {

                                            appointment.setId(
                                                    document.getId()
                                            );

                                            pendingAppointmentList.add(
                                                    appointment
                                            );
                                        }
                                    }

                                    if (pendingAppointmentList.isEmpty()) {

                                        recyclerDoctorPending.setVisibility(
                                                View.GONE
                                        );

                                        txtNoPendingRequests.setVisibility(
                                                View.VISIBLE
                                        );

                                    } else {

                                        txtNoPendingRequests.setVisibility(
                                                View.GONE
                                        );

                                        recyclerDoctorPending.setVisibility(
                                                View.VISIBLE
                                        );
                                    }

                                    pendingAppointmentAdapter.notifyDataSetChanged();
                                }
                        );
    }
    private void loadDoctorAppointmentCounts() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {
            return;
        }

        String doctorUID =
                user.getUid();

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.ENGLISH
                );

        String today =
                sdf.format(
                        Calendar.getInstance().getTime()
                );

        db.collection("appointments")
                .whereEqualTo(
                        "doctorId",
                        doctorUID
                )
                .whereEqualTo(
                        "status",
                        "Confirmed"
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    int todayCount = 0;
                    int upcomingCount = 0;

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String appointmentDate =
                                document.getString(
                                        "appointmentDate"
                                );

                        if (appointmentDate == null) {
                            continue;
                        }

                        appointmentDate =
                                appointmentDate.trim();

                        if (appointmentDate.equals(today)) {

                            todayCount++;

                        } else if (
                                appointmentDate.compareTo(today) > 0
                        ) {

                            upcomingCount++;
                        }
                    }

                    txtTodayAppointmentCount.setText(
                            String.valueOf(todayCount)
                    );

                    txtUpcomingCount.setText(
                            String.valueOf(upcomingCount)
                    );

                })
                .addOnFailureListener(e -> {

                    txtTodayAppointmentCount.setText("0");

                    txtUpcomingCount.setText("0");

                    Toast.makeText(
                            requireContext(),
                            "Failed to load appointment counts: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
    @Override
    public void onDestroyView() {

        super.onDestroyView();

        if (pendingAppointmentsListener != null) {

            pendingAppointmentsListener.remove();

            pendingAppointmentsListener = null;
        }
    }

}