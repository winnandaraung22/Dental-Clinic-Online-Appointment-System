package com.example.gentletouchdentalclinic;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ManageSchedulesFragment extends Fragment {

    RecyclerView recyclerDoctorSchedules;
    ArrayList<DoctorScheduleGroup> doctorList;
    DoctorScheduleAdapter adapter;
    FirebaseFirestore db;
    private AutoCompleteTextView autoDoctor;
    private ArrayList<String> doctorNames;
    private HashMap<String, Doctor> doctorMap;
    private String selectDoctorID, selectDoctorName, selectStaffCode, selectSpecialization;
    private MaterialButton btnSelectDay, btnStartTime, btnEndTime, btnAddSchedule;
    private String selectedDay, startTime, endTime;


    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState){

        View view = inflater.inflate(
                R.layout.fragment_manage_schedules,
                container,
                false);

        autoDoctor = view.findViewById(R.id.autoDoctor);
        btnSelectDay = view.findViewById(R.id.btnSelectDay);
        btnStartTime = view.findViewById(R.id.btnStartTime);
        btnEndTime = view.findViewById(R.id.btnEndTime);
        btnAddSchedule = view.findViewById(R.id.btnAddSchedule);

        doctorNames = new ArrayList<>();
        doctorMap = new HashMap<>();

        recyclerDoctorSchedules = view.findViewById(R.id.recyclerDoctorSchedules);

        doctorList = new ArrayList<>();

        adapter = new DoctorScheduleAdapter(doctorList,
                group -> {
                    openEditScheduleDialog(group);
                });

        recyclerDoctorSchedules.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerDoctorSchedules.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadDoctors();

        loadDoctorSchedules();

        autoDoctor.setOnItemClickListener((parent, view1, position, id) -> {

            String doctorName =
                    parent.getItemAtPosition(position).toString();

            Doctor doctor =
                    doctorMap.get(doctorName);

            if (doctor != null) {

                selectDoctorID = doctor.getId();

                selectDoctorName = doctor.getFullName();

                selectStaffCode = doctor.getStaffCode();

                selectSpecialization = doctor.getSpecialization();

            }

        });

        btnSelectDay.setOnClickListener(v -> {

            String[] days = {
                    "Monday",
                    "Tuesday",
                    "Wednesday",
                    "Thursday",
                    "Friday",
                    "Saturday"
            };


            new AlertDialog.Builder(requireContext())
                    .setTitle("Select Day")
                    .setItems(days, (dialog, which) -> {

                        selectedDay = days[which];

                        btnSelectDay.setText(selectedDay);

                    })
                    .show();

        });

        btnStartTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int hour =
                    calendar.get(Calendar.HOUR_OF_DAY);

            int minute =
                    calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker =
                    new TimePickerDialog(

                            getContext(),

                            (timePicker1, hourOfDay, minuteSelected) -> {

                                Calendar selectedTime =
                                        Calendar.getInstance();

                                selectedTime.set(
                                        Calendar.HOUR_OF_DAY,
                                        hourOfDay
                                );

                                selectedTime.set(
                                        Calendar.MINUTE,
                                        minuteSelected
                                );

                                SimpleDateFormat sdf =
                                        new SimpleDateFormat(
                                                "hh:mm a",
                                                Locale.getDefault()
                                        );

                                startTime =
                                        sdf.format(
                                                selectedTime.getTime()
                                        );

                                btnStartTime.setText(startTime);

                            },

                            hour,

                            minute,

                            false

                    );

            timePicker.show();


        });

        btnEndTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int hour =
                    calendar.get(Calendar.HOUR_OF_DAY);

            int minute =
                    calendar.get(Calendar.MINUTE);


            TimePickerDialog timePicker =
                    new TimePickerDialog(

                            getContext(),

                            (timePicker1, hourOfDay, minuteSelected) -> {

                                Calendar selectedTime =
                                        Calendar.getInstance();

                                selectedTime.set(
                                        Calendar.HOUR_OF_DAY,
                                        hourOfDay
                                );

                                selectedTime.set(
                                        Calendar.MINUTE,
                                        minuteSelected
                                );

                                SimpleDateFormat sdf =
                                        new SimpleDateFormat(
                                                "hh:mm a",
                                                Locale.getDefault()
                                        );

                                endTime =
                                        sdf.format(
                                                selectedTime.getTime()
                                        );

                                btnEndTime.setText(endTime);

                            },

                            hour,

                            minute,

                            false

                    );

            timePicker.show();


        });

        btnAddSchedule.setOnClickListener(v -> {

            if(selectDoctorID == null){

                Toast.makeText(
                        requireContext(),
                        "Please select doctor",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            if(selectedDay == null){

                Toast.makeText(
                        requireContext(),
                        "Please select day",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            HashMap<String,Object> schedule =
                    new HashMap<>();

            schedule.put(
                    "doctorDocumentID",
                    selectDoctorID
            );

            schedule.put(
                    "doctorName",
                    selectDoctorName
            );

            schedule.put(
                    "staffCode",
                    selectStaffCode
            );

            schedule.put(
                    "dayOfWeek",
                    selectedDay
            );

            schedule.put(
                    "startTime",
                    startTime
            );

            schedule.put(
                    "endTime",
                    endTime
            );

            schedule.put(
                    "status",
                    "AVAILABLE"
            );

            schedule.put(
                    "specialization",
                    selectSpecialization
            );


            db.collection("doctorSchedules")
                    .whereEqualTo("doctorDocumentID", selectDoctorID)
                    .whereEqualTo("dayOfWeek", selectedDay)
                    .get()
                    .addOnSuccessListener(query -> {

                        if (!query.isEmpty()) {

                            // Schedule already exists → update it

                            String docId =
                                    query.getDocuments()
                                            .get(0)
                                            .getId();

                            db.collection("doctorSchedules")
                                    .document(docId)
                                    .update(schedule)
                                    .addOnSuccessListener(unused -> {

                                        Toast.makeText(
                                                requireContext(),
                                                "Schedule Updated Successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        resetScheduleForm();

                                        loadDoctorSchedules();

                                    });

                        } else {

                            db.collection("doctorSchedules")
                                    .add(schedule)
                                    .addOnSuccessListener(documentReference -> {

                                        Toast.makeText(
                                                requireContext(),
                                                "Schedule Added Successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        resetScheduleForm();

                                        loadDoctorSchedules();

                                    });
                        }

                    });

        });

        return view;

    }
    private void loadDoctorSchedules(){


        db.collection("doctorSchedules")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    doctorList.clear();

                    HashMap<String, ArrayList<DoctorSchedule>> grouped =
                            new HashMap<>();

                    for(DocumentSnapshot document :
                            queryDocumentSnapshots){


                        DoctorSchedule schedule =
                                document.toObject(
                                        DoctorSchedule.class
                                );

                        if(schedule != null){

                            schedule.setId(
                                    document.getId()
                            );

                            String doctorID =
                                    schedule.getDoctorDocumentID();

                            if(!grouped.containsKey(doctorID)){

                                grouped.put(
                                        doctorID,
                                        new ArrayList<>()
                                );

                            }

                            grouped.get(doctorID)
                                    .add(schedule);

                        }


                    }

                    for(String doctorID :
                            grouped.keySet()){

                        ArrayList<DoctorSchedule> schedules =
                                grouped.get(doctorID);

                        String doctorName =
                                schedules.get(0)
                                        .getDoctorName();

                        DoctorScheduleGroup group =
                                new DoctorScheduleGroup(
                                        doctorName,
                                        schedules
                                );

                        doctorList.add(group);

                    }

                    adapter.notifyDataSetChanged();

                });


    }
    private void loadDoctors() {

        db.collection("doctors")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    doctorNames.clear();
                    doctorMap.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {

                        Doctor doctor = document.toObject(Doctor.class);

                        if (doctor != null) {

                            doctor.setId(document.getId());

                            doctorNames.add(doctor.getFullName());

                            doctorMap.put(
                                    doctor.getFullName(),
                                    doctor
                            );
                        }
                    }

                    android.widget.ArrayAdapter<String> arrayAdapter =
                            new android.widget.ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_dropdown_item_1line,
                                    doctorNames
                            );

                    autoDoctor.setAdapter(arrayAdapter);

                });

    }
    private void resetScheduleForm(){

        autoDoctor.setText("");

        selectDoctorID = null;
        selectDoctorName = null;
        selectStaffCode = null;

        btnSelectDay.setText("Select Day");

        btnStartTime.setText("Start Time");

        btnEndTime.setText("End Time");

        selectedDay = "";
        startTime = "";
        endTime = "";

    }
    private void openEditScheduleDialog(DoctorScheduleGroup group){
        ArrayList<DoctorSchedule> schedules =
                group.getSchedules();

        String[] days = new String[schedules.size()];


        for(int i = 0; i < schedules.size(); i++){

            days[i] =
                    schedules.get(i)
                            .getDayOfWeek();

        }

        new AlertDialog.Builder(requireContext())
                .setTitle(
                        "Select Schedule to Edit"
                )
                .setItems(
                        days,
                        (dialog, which) -> {

                            DoctorSchedule selectedSchedule =
                                    schedules.get(which);

                            showEditScheduleDialog(
                                    selectedSchedule
                            );


                        })
                .show();
    }
    private void showEditScheduleDialog(
            DoctorSchedule schedule){

        View view =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.dialog_edit_schedule,
                                null
                        );

        MaterialButton btnStart =
                view.findViewById(
                        R.id.btnEditStartTime
                );

        MaterialButton btnEnd =
                view.findViewById(
                        R.id.btnEditEndTime
                );

        MaterialButton btnStatus =
                view.findViewById(R.id.btnEditStatus);


        String[] newStartTime = {
                schedule.getStartTime()
        };


        String[] newEndTime = {
                schedule.getEndTime()
        };

        String[] newStatus = { schedule.getStatus() };

        btnStatus.setText(newStatus[0]);

        btnStart.setText(
                schedule.getStartTime()
        );


        btnEnd.setText(
                schedule.getEndTime()
        );

        btnStart.setOnClickListener(v -> {


            Calendar calendar =
                    Calendar.getInstance();


            TimePickerDialog timePicker =
                    new TimePickerDialog(

                            requireContext(),

                            (timePicker1, hourOfDay, minute) -> {


                                Calendar selectedTime =
                                        Calendar.getInstance();


                                selectedTime.set(
                                        Calendar.HOUR_OF_DAY,
                                        hourOfDay
                                );


                                selectedTime.set(
                                        Calendar.MINUTE,
                                        minute
                                );


                                SimpleDateFormat sdf =
                                        new SimpleDateFormat(
                                                "hh:mm a",
                                                Locale.getDefault()
                                        );


                                newStartTime[0] =
                                        sdf.format(
                                                selectedTime.getTime()
                                        );


                                btnStart.setText(
                                        newStartTime[0]
                                );


                            },

                            calendar.get(Calendar.HOUR_OF_DAY),

                            calendar.get(Calendar.MINUTE),

                            false

                    );


            timePicker.show();


        });

        btnEnd.setOnClickListener(v -> {


            Calendar calendar =
                    Calendar.getInstance();


            TimePickerDialog timePicker =
                    new TimePickerDialog(

                            requireContext(),

                            (timePicker1, hourOfDay, minute) -> {


                                Calendar selectedTime =
                                        Calendar.getInstance();


                                selectedTime.set(
                                        Calendar.HOUR_OF_DAY,
                                        hourOfDay
                                );

                                selectedTime.set(
                                        Calendar.MINUTE,
                                        minute
                                );

                                SimpleDateFormat sdf =
                                        new SimpleDateFormat(
                                                "hh:mm a",
                                                Locale.getDefault()
                                        );


                                newEndTime[0] =
                                        sdf.format(
                                                selectedTime.getTime()
                                        );


                                btnEnd.setText(
                                        newEndTime[0]
                                );


                            },


                            calendar.get(Calendar.HOUR_OF_DAY),

                            calendar.get(Calendar.MINUTE),

                            false

                    );


            timePicker.show();


        });
        btnStatus.setOnClickListener(v -> {

            String[] options = {"AVAILABLE", "OFF"};

            new AlertDialog.Builder(requireContext())
                    .setTitle("Select Status")
                    .setItems(options, (dialog, which) -> {

                        newStatus[0] = options[which];
                        btnStatus.setText(newStatus[0]);

                    })
                    .show();

        });

        AlertDialog dialog =
                new AlertDialog.Builder(requireContext())
                        .setTitle(
                                "Edit "
                                        + schedule.getDayOfWeek()
                        )
                        .setView(view)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Save",
                                null
                        )
                        .create();



        dialog.setOnShowListener(d -> {


            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {



                HashMap<String,Object> update =
                        new HashMap<>();


                update.put(
                        "startTime",
                        newStartTime[0]
                );


                update.put(
                        "endTime",
                        newEndTime[0]
                );

                update.put("status", newStatus[0]);

                db.collection(
                                "doctorSchedules"
                        )
                        .document(
                                schedule.getId()
                        )
                        .update(update)
                        .addOnSuccessListener(unused -> {


                            Toast.makeText(
                                    requireContext(),
                                    "Schedule Updated",
                                    Toast.LENGTH_SHORT
                            ).show();


                            loadDoctorSchedules();

                            dialog.dismiss();

                        });


            });

        });

        dialog.show();

    }

}
