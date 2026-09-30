package com.example.gentletouchdentalclinic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class AdminDashboardFragment extends Fragment {
    private TextView txtDoctorCount;
    private TextView txtAppointmentCount;
    private TextView txtPatientCount;
    private TextView txtStaffCount;
    private LineChart patientGrowthChart;
    private FirebaseFirestore db;

    public AdminDashboardFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState){

        View view = inflater.inflate(
                R.layout.fragment_admindashboard,
                container,
                false
        );

        txtDoctorCount = view.findViewById(R.id.txtDoctorCount);
        txtAppointmentCount = view.findViewById(R.id.txtAppointmentCount);
        txtPatientCount = view.findViewById(R.id.txtPatientCount);
        txtStaffCount = view.findViewById(R.id.txtStaffCount);
        patientGrowthChart = view.findViewById(R.id.patientGrowthChart);

        db = FirebaseFirestore.getInstance();

        loadDoctorCount();

        loadPatientCount();

        loadStaffCount();

        loadTodayAppointmentCount();

        loadPatientGrowthData();

        return view;

    }
    private void loadDoctorCount() {

        db.collection("doctors")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int count = queryDocumentSnapshots.size();

                    txtDoctorCount.setText(String.valueOf(count));

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to load doctor count.",
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }
    private void loadPatientCount(){

        db.collection("users")
                .whereEqualTo("role", "patient")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int count = queryDocumentSnapshots.size();

                    txtPatientCount.setText(
                            String.valueOf(count)
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to load patient count.",
                            Toast.LENGTH_SHORT
                    ).show();


                });

    }

    private void loadPatientGrowthData(){

        ArrayList<Entry> entries = new ArrayList<>();

        db.collection("users")
                .whereEqualTo("role","patient")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int[] monthlyPatients = new int[12];

                    for(DocumentSnapshot document : queryDocumentSnapshots){

                        Timestamp timestamp =
                                document.getTimestamp("createdAt");

                        if(timestamp != null){

                            Calendar calendar =
                                    Calendar.getInstance();

                            calendar.setTime(
                                    timestamp.toDate()
                            );

                            int month =
                                    calendar.get(Calendar.MONTH);

                            monthlyPatients[month]++;

                        }

                    }
                    for(int i=0; i<12; i++){

                        entries.add(
                                new Entry(
                                        i,
                                        monthlyPatients[i]
                                )
                        );

                    }

                    showPatientChart(entries);

                });

    }
    private void showPatientChart(ArrayList<Entry> entries){

        LineDataSet dataSet =
                new LineDataSet(
                        entries,
                        "Registered Patients"
                );

        dataSet.setLineWidth(3f);

        dataSet.setCircleRadius(5f);

        LineData lineData = new LineData(dataSet);

        patientGrowthChart.setData(lineData);

        String[] months =
                {
                        "Jan",
                        "Feb",
                        "Mar",
                        "Apr",
                        "May",
                        "Jun",
                        "Jul",
                        "Aug",
                        "Sep",
                        "Oct",
                        "Nov",
                        "Dec"
                };


        XAxis xAxis =
                patientGrowthChart.getXAxis();


        xAxis.setValueFormatter(
                new IndexAxisValueFormatter(months)
        );


        xAxis.setGranularity(1f);

        xAxis.setPosition(
                XAxis.XAxisPosition.BOTTOM
        );

        YAxis yAxis =
                patientGrowthChart.getAxisLeft();


        yAxis.setAxisMinimum(0f);

        yAxis.setGranularity(1f);

        yAxis.setGranularityEnabled(true);

        patientGrowthChart.getAxisRight()
                .setEnabled(false);

        patientGrowthChart.getDescription()
                .setEnabled(false);


        patientGrowthChart.invalidate();

    }
    private void loadStaffCount(){

        db.collection("staff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int count = queryDocumentSnapshots.size();

                    txtStaffCount.setText(
                            String.valueOf(count)
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Failed to load staff count.",
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }
    private void loadTodayAppointmentCount() {

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat(
                        "yyyy-MM-dd",
                        java.util.Locale.ENGLISH
                );

        String today =
                sdf.format(new java.util.Date());

        db.collection("appointments")
                .whereEqualTo("appointmentDate", today)
                .whereEqualTo("status", "Confirmed")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    txtAppointmentCount.setText(
                            String.valueOf(
                                    queryDocumentSnapshots.size()
                            )
                    );

                })
                .addOnFailureListener(e -> {

                    txtAppointmentCount.setText("0");

                    Toast.makeText(
                            requireContext(),
                            "Failed to load today's appointments.",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}
