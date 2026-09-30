package com.example.gentletouchdentalclinic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PatientHomeFragment extends Fragment {
    RecyclerView recyclerAvailableDoctors;
    ArrayList<DoctorSchedule> availableDoctorList;
    AvailableDoctorAdapter availableDoctorAdapter;
    FirebaseFirestore db;
    ViewPager2 viewPagerDoctors;
    ArrayList<Doctor> doctorList;
    DoctorAdapter doctorAdapter;
    RecyclerView recyclerServices;
    ArrayList<Service> serviceList;
    ServiceAdapter serviceAdapter;
    private LinearLayout availableDoctorsContainer;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState){

        View view = inflater.inflate(
                R.layout.fragment_patient_home,
                container,
                false);

        ((PatientDashboardActivity)
                requireActivity())
                .setTopBarTitle(
                        "Find Your Specialist"
                );


        viewPagerDoctors = view.findViewById(R.id.viewPagerDoctors);

        doctorList = new ArrayList<>();

        doctorAdapter = new DoctorAdapter(doctorList);

        viewPagerDoctors.setAdapter(doctorAdapter);

        loadDoctors();

        viewPagerDoctors.setClipToPadding(false);
        viewPagerDoctors.setClipChildren(false);
        viewPagerDoctors.setOffscreenPageLimit(3);

        viewPagerDoctors.post(() -> {

            RecyclerView recyclerView =
                    (RecyclerView) viewPagerDoctors.getChildAt(0);


            if(recyclerView != null){

                recyclerView.setPadding(
                        60,
                        0,
                        60,
                        0
                );

                recyclerView.setClipToPadding(false);

            }

        });

        CompositePageTransformer transformer =
                new CompositePageTransformer();

        transformer.addTransformer(new MarginPageTransformer(30));

        transformer.addTransformer((page, position) -> {

            float scale = 0.9f + (1 - Math.abs(position)) * 0.1f;

            page.setScaleY(scale);

        });

        viewPagerDoctors.setPageTransformer(transformer);

        recyclerServices = view.findViewById(R.id.recyclerServices);

        serviceList = new ArrayList<>();

        serviceAdapter =
                new ServiceAdapter(
                        serviceList,
                        service -> {


                            showServiceDetails(service);


                        }
                );

        recyclerServices.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        recyclerServices.setAdapter(
                serviceAdapter
        );

        db = FirebaseFirestore.getInstance();

        loadServices();

        ((PatientDashboardActivity)
                requireActivity())
                .setTopBarTitle("Find Your Specialist");

        availableDoctorsContainer =
                view.findViewById(
                        R.id.availableDoctorsContainer
                );

        recyclerAvailableDoctors =
                view.findViewById(
                        R.id.recyclerAvailableDoctors);

        availableDoctorList =
                new ArrayList<>();

        availableDoctorAdapter =
                new AvailableDoctorAdapter(
                        availableDoctorList);

        recyclerAvailableDoctors.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerAvailableDoctors.setAdapter(
                availableDoctorAdapter
        );

        loadAvailableDoctors();

        return view;

    }
    private void loadServices() {

        db.collection("services")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!isAdded() || getView() == null) {
                        return;
                    }

                    serviceList.clear();

                    for (DocumentSnapshot document :
                            queryDocumentSnapshots) {

                        Service service =
                                document.toObject(
                                        Service.class
                                );

                        if (service != null) {

                            service.setId(
                                    document.getId()
                            );

                            serviceList.add(service);
                        }
                    }

                    serviceAdapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    if (!isAdded() || getView() == null) {
                        return;
                    }

                    Toast.makeText(
                            requireContext(),
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    private void loadAvailableDoctors() {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "EEEE",
                        Locale.getDefault()
                );

        String today =
                sdf.format(
                        calendar.getTime()
                ).trim();

        db.collection("doctorSchedules")
                .whereEqualTo(
                        "dayOfWeek",
                        today
                )
                .whereEqualTo(
                        "status",
                        "AVAILABLE"
                )
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!isAdded() || getView() == null) {
                        return;
                    }

                    availableDoctorList.clear();

                    for (DocumentSnapshot document :
                            queryDocumentSnapshots) {

                        DoctorSchedule schedule =
                                document.toObject(
                                        DoctorSchedule.class
                                );

                        if (schedule != null) {

                            availableDoctorList.add(
                                    schedule
                            );
                        }
                    }

                    if (availableDoctorList.isEmpty()) {

                        recyclerAvailableDoctors.setVisibility(
                                View.GONE
                        );

                        View emptyView =
                                LayoutInflater.from(
                                        requireContext()
                                ).inflate(
                                        R.layout.item_available_doctor_empty,
                                        availableDoctorsContainer,
                                        false
                                );

                        availableDoctorsContainer.removeAllViews();

                        availableDoctorsContainer.addView(
                                emptyView
                        );

                    }

                    else {

                        availableDoctorsContainer.removeAllViews();

                        availableDoctorsContainer.addView(
                                recyclerAvailableDoctors
                        );

                        recyclerAvailableDoctors.setVisibility(
                                View.VISIBLE
                        );

                        availableDoctorAdapter
                                .notifyDataSetChanged();
                    }

                })
                .addOnFailureListener(e -> {

                    if (!isAdded() || getView() == null) {
                        return;
                    }

                    Toast.makeText(
                            requireContext(),
                            "Failed to load available doctors",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
    private void loadDoctors() {

        FirebaseFirestore db =
                FirebaseFirestore.getInstance();

        db.collection("doctors")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!isAdded() || getView() == null) {
                        return;
                    }

                    doctorList.clear();

                    for (DocumentSnapshot document :
                            queryDocumentSnapshots) {

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

                    doctorAdapter.notifyDataSetChanged();

                });
    }
    private void showServiceDetails(Service service){

        View view =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.dialog_service_details,
                                null
                        );


        TextView txtName =
                view.findViewById(
                        R.id.txtServiceDetailName
                );

        TextView txtDescription =
                view.findViewById(
                        R.id.txtServiceDetailDescription
                );

        TextView txtDuration =
                view.findViewById(
                        R.id.txtServiceDetailDuration
                );

        ImageView imgService =
                view.findViewById(
                        R.id.imgServiceDetail
                );

        MaterialButton btnClose =
                view.findViewById(
                        R.id.btnCloseServiceDetail
                );

        txtName.setText(
                service.getServiceName()
        );

        txtDescription.setText(
                service.getDescription()
        );

        txtDuration.setText(
                service.getDuration()
        );

        int imageResource =
                requireContext()
                        .getResources()
                        .getIdentifier(
                                service.getImageName(),
                                "drawable",
                                requireContext()
                                        .getPackageName()
                        );

        if(imageResource != 0){

            imgService.setImageResource(
                    imageResource
            );

        }

        AlertDialog dialog =
                new AlertDialog.Builder(
                        requireContext()
                )
                        .setView(view)
                        .create();

        dialog.getWindow();

        btnClose.setOnClickListener(v ->
                dialog.dismiss()
        );

        dialog.setOnShowListener(d -> {

            if(dialog.getWindow() != null){

                dialog.getWindow()
                        .setBackgroundDrawableResource(
                                android.R.color.transparent
                        );

                dialog.getWindow()
                        .setDimAmount(0.35f);

            }

        });

        dialog.show();

        if(dialog.getWindow() != null){

            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            dialog.getWindow().setDimAmount(0.35f);

            dialog.getWindow().setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.90),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

    }
}
