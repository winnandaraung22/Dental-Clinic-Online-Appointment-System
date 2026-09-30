package com.example.gentletouchdentalclinic;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AppointmentActivity extends AppCompatActivity {
    ImageButton btnAppointmentBack;
    TextInputEditText edtAppointmentDate;
    TextInputEditText edtAppointmentTime;
    AutoCompleteTextView autoAppointmentType;
    TextView txtAppointmentDoctorName;
    TextView txtAppointmentSpecialization;
    TextInputEditText edtAppointmentAge;
    TextInputEditText edtAppointmentPhone;
    TextInputEditText edtAppointmentAddress;
    TextInputEditText edtAppointmentReason;
    AutoCompleteTextView autoAppointmentGender;
    MaterialButton btnRequestAppointment;
    TextInputLayout layoutAppointmentDate;
    TextInputLayout layoutAppointmentTime;
    TextInputLayout layoutAppointmentType;
    TextInputLayout layoutAppointmentAge;
    TextInputLayout layoutAppointmentGender;
    TextInputLayout layoutAppointmentPhone;
    TextInputLayout layoutAppointmentAddress;
    TextInputLayout layoutAppointmentReason;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private String doctorId;
    private String doctorName = "";
    private String specialization = "";
    private String patientId;
    private String patientName = "";
    private String selectedDate = "";
    private String selectedDay = "";
    private String selectedTime = "";
    private ArrayList<String> doctorDutyDays = new ArrayList<>();
    private boolean doctorScheduleLoaded = false;
    private ArrayList<String> availableTimeSlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appointment);

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        btnAppointmentBack = findViewById(R.id.btnAppointmentBack);

        txtAppointmentDoctorName = findViewById(R.id.txtAppointmentDoctorName);

        txtAppointmentSpecialization = findViewById(R.id.txtAppointmentSpecialization);

        edtAppointmentDate = findViewById(R.id.edtAppointmentDate);

        edtAppointmentTime = findViewById(R.id.edtAppointmentTime);

        autoAppointmentType = findViewById(R.id.autoAppointmentType);

        edtAppointmentAge = findViewById(R.id.edtAppointmentAge);

        autoAppointmentGender = findViewById(R.id.autoAppointmentGender);

        edtAppointmentPhone = findViewById(R.id.edtAppointmentPhone);

        edtAppointmentAddress = findViewById(R.id.edtAppointmentAddress);

        edtAppointmentReason = findViewById(R.id.edtAppointmentReason);

        btnRequestAppointment = findViewById(R.id.btnRequestAppointment);

        layoutAppointmentDate = findViewById(R.id.layoutAppointmentDate);

        layoutAppointmentTime = findViewById(R.id.layoutAppointmentTime);

        layoutAppointmentType = findViewById(R.id.layoutAppointmentType);

        layoutAppointmentAge = findViewById(R.id.layoutAppointmentAge);

        layoutAppointmentGender = findViewById(R.id.layoutAppointmentGender);

        layoutAppointmentPhone = findViewById(R.id.layoutAppointmentPhone);

        layoutAppointmentAddress = findViewById(R.id.layoutAppointmentAddress);

        layoutAppointmentReason = findViewById(R.id.layoutAppointmentReason);

        btnAppointmentBack.setOnClickListener(v -> {

            finish();
            overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
            );

        });

        doctorId = getIntent().getStringExtra("doctorId");

        if (doctorId == null ||
                doctorId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Doctor information not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        patientId = auth.getCurrentUser().getUid();

        setupAppointmentTypeDropdown();

        setupGenderDropdown();

        edtAppointmentDate.setOnClickListener(v -> {

            if (!doctorScheduleLoaded) {

                Toast.makeText(
                        this,
                        "Please wait, loading doctor's schedule...",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (doctorDutyDays.isEmpty()) {

                Toast.makeText(
                        this,
                        "This doctor has no available working days",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            showDatePicker();
        });


        layoutAppointmentDate.setEndIconOnClickListener(v -> {

            if (!doctorScheduleLoaded) {

                Toast.makeText(
                        this,
                        "Please wait, loading doctor's schedule...",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (doctorDutyDays.isEmpty()) {

                Toast.makeText(
                        this,
                        "This doctor has no available working days",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            showDatePicker();
        });

        edtAppointmentTime.setOnClickListener(v -> {

            if (selectedDate.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please select appointment date first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            showTimePicker();

        });


        layoutAppointmentTime.setEndIconOnClickListener(v -> {

            if (selectedDate.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please select appointment date first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            showTimePicker();

        });

        loadDoctor();

        loadPatient();

        loadDoctorDutyDays();

        btnRequestAppointment.setOnClickListener(v -> {

            requestAppointment();

        });

    }

    private void setupAppointmentTypeDropdown() {

        String[] appointmentTypes = {

                "New Consultation",
                "Follow-up Visit",
                "Dental Check-up",
                "Emergency Visit",
                "Treatment Appointment",
                "Others"

        };


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        appointmentTypes
                );

        autoAppointmentType.setAdapter(adapter);

        autoAppointmentType.setOnItemClickListener(
                (parent, view, position, id) -> {

                    layoutAppointmentType.setError(null);

                }
        );

    }
    private void setupGenderDropdown() {

        String[] genders = {

                "Male",
                "Female",
                "Other"

        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        genders
                );

        autoAppointmentGender.setAdapter(adapter);

        autoAppointmentGender.setOnItemClickListener(
                (parent, view, position, id) -> {

                    layoutAppointmentGender.setError(null);

                }
        );
    }
    private void loadDoctor() {

        db.collection("doctors")
                .document(doctorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                this,
                                "Doctor not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Doctor doctor =
                            documentSnapshot.toObject(
                                    Doctor.class
                            );

                    if (doctor != null) {

                        doctorName = doctor.getFullName();

                        specialization = doctor.getSpecialization();

                        txtAppointmentDoctorName.setText(doctorName);

                        txtAppointmentSpecialization.setText(specialization);

                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load doctor information",
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }
    private void loadPatient() {

        db.collection("users")
                .document(patientId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        return;
                    }

                    patientName =
                            documentSnapshot.getString(
                                    "fullName"
                            );

                    String phone =
                            documentSnapshot.getString(
                                    "phone"
                            );

                    if (phone != null) {

                        edtAppointmentPhone.setText(
                                phone
                        );

                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load patient information",
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }
    private void showDatePicker() {

        View calendarView =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.dialog_appointment_calendar,
                                null
                        );


        MaterialCalendarView calendar =
                calendarView.findViewById(
                        R.id.appointmentCalendar
                );

        calendar.state().edit()
                .setMinimumDate(
                        CalendarDay.today()
                )
                .commit();

        calendar.addDecorator(
                new DoctorDutyDayDecorator(
                        doctorDutyDays
                )
        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(calendarView)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .create();

        calendar.setOnDateChangedListener(
                (widget, date, selected) -> {

                    if (!selected) {
                        return;
                    }

                    Calendar selectedCalendar =
                            Calendar.getInstance();

                    selectedCalendar.set(
                            date.getYear(),
                            date.getMonth() - 1,
                            date.getDay()
                    );

                    String dayName =
                            new java.text.SimpleDateFormat(
                                    "EEEE",
                                    Locale.ENGLISH
                            ).format(
                                    selectedCalendar.getTime()
                            );

                    if (!doctorDutyDays.contains(dayName)) {

                        Toast.makeText(
                                this,
                                "Doctor is not available on " + dayName,
                                Toast.LENGTH_SHORT
                        ).show();

                        calendar.clearSelection();

                        return;
                    }

                    String checkingDate =
                            String.format(
                                    Locale.getDefault(),
                                    "%04d-%02d-%02d",
                                    date.getYear(),
                                    date.getMonth(),
                                    date.getDay()
                            );

                    db.collection("appointments")
                            .whereEqualTo("patientId", patientId)
                            .whereEqualTo("doctorId", doctorId)
                            .whereEqualTo("appointmentDate", checkingDate)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {

                                boolean alreadyBooked = false;

                                for (DocumentSnapshot document :
                                        querySnapshot.getDocuments()) {

                                    String status =
                                            document.getString("status");

                                    if ("Pending".equalsIgnoreCase(status)
                                            || "Approved".equalsIgnoreCase(status)
                                            || "Confirmed".equalsIgnoreCase(status)) {

                                        alreadyBooked = true;
                                        break;
                                    }
                                }

                                if (alreadyBooked) {

                                    Toast.makeText(
                                            this,
                                            "You already have an appointment " +
                                                    "with this doctor on " +
                                                    checkingDate,
                                            Toast.LENGTH_LONG
                                    ).show();

                                    calendar.clearSelection();

                                    return;
                                }

                                selectedDate = checkingDate;

                                selectedDay = dayName;

                                String displayDate =
                                        new java.text.SimpleDateFormat(
                                                "dd MMM yyyy",
                                                Locale.getDefault()
                                        ).format(
                                                selectedCalendar.getTime()
                                        );

                                edtAppointmentDate.setText(
                                        displayDate
                                );

                                layoutAppointmentDate.setError(null);

                                dialog.dismiss();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        "Failed to check existing appointments",
                                        Toast.LENGTH_SHORT
                                ).show();

                                calendar.clearSelection();

                            });

                }
        );

        dialog.show();
    }
    private void showTimePicker() {

        if (selectedDay == null || selectedDay.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select appointment date first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.collection("doctorSchedules")
                .whereEqualTo(
                        "doctorDocumentID",
                        doctorId
                )
                .whereEqualTo(
                        "dayOfWeek",
                        selectedDay
                )
                .whereEqualTo(
                        "status",
                        "AVAILABLE"
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    availableTimeSlots.clear();

                    if (querySnapshot.isEmpty()) {

                        Toast.makeText(
                                this,
                                "No schedule available for this day",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    DocumentSnapshot schedule =
                            querySnapshot.getDocuments().get(0);

                    String startTime =
                            schedule.getString("startTime");

                    String endTime =
                            schedule.getString("endTime");

                    if (startTime == null ||
                            endTime == null ||
                            startTime.isEmpty() ||
                            endTime.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Doctor schedule time is not available",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    generateTimeSlots(
                            startTime,
                            endTime
                    );

                    loadBookedTimeSlots();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load doctor's schedule",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
    private boolean validateAppointment() {

        layoutAppointmentDate.setError(null);
        layoutAppointmentTime.setError(null);
        layoutAppointmentType.setError(null);
        layoutAppointmentAge.setError(null);
        layoutAppointmentGender.setError(null);
        layoutAppointmentPhone.setError(null);
        layoutAppointmentAddress.setError(null);
        layoutAppointmentReason.setError(null);

        boolean valid = true;

        if (selectedDate.isEmpty()) {

            layoutAppointmentDate.setError(
                    "Please select an appointment date"
            );

            valid = false;

        }

        if (selectedTime.isEmpty()) {

            layoutAppointmentTime.setError(
                    "Please select an appointment time"
            );

            valid = false;

        }

        String appointmentType =
                autoAppointmentType
                        .getText()
                        .toString()
                        .trim();

        if (appointmentType.isEmpty()) {

            layoutAppointmentType.setError(
                    "Please select an appointment type"
            );

            valid = false;

        }

        String ageText =
                edtAppointmentAge
                        .getText()
                        .toString()
                        .trim();


        if (ageText.isEmpty()) {

            layoutAppointmentAge.setError(
                    "Age is required"
            );

            valid = false;

        }

        String gender =
                autoAppointmentGender
                        .getText()
                        .toString()
                        .trim();


        if (gender.isEmpty()) {

            layoutAppointmentGender.setError(
                    "Please select gender"
            );

            valid = false;

        }

        String phone =
                edtAppointmentPhone
                        .getText()
                        .toString()
                        .trim();

        if (phone.isEmpty()) {

            layoutAppointmentPhone.setError(
                    "Phone number is required"
            );

            valid = false;

        }
        else if (!phone.matches("[0-9]{11}")) {

            layoutAppointmentPhone.setError(
                    "Phone number must contain exactly 11 digits"
            );

            valid = false;

        }

        String address =
                edtAppointmentAddress
                        .getText()
                        .toString()
                        .trim();


        if (address.isEmpty()) {

            layoutAppointmentAddress.setError(
                    "Address is required"
            );

            valid = false;

        }

        String reason =
                edtAppointmentReason
                        .getText()
                        .toString()
                        .trim();

        return valid;

    }

    private void requestAppointment() {


        if (!validateAppointment()) {

            Toast.makeText(
                    this,
                    "Please complete all required fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }

        if (isTimePast(selectedTime)) {

            layoutAppointmentTime.setError(
                    "This appointment time has already passed. Please select another time."
            );

            Toast.makeText(
                    this,
                    "This appointment time is no longer available.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String ageText =
                edtAppointmentAge
                        .getText()
                        .toString()
                        .trim();

        int age;

        try {

            age = Integer.parseInt(ageText);

        }
        catch (NumberFormatException e) {

            layoutAppointmentAge.setError(
                    "Please enter a valid age"
            );

            return;

        }

        String gender =
                autoAppointmentGender
                        .getText()
                        .toString()
                        .trim();

        String phone =
                edtAppointmentPhone
                        .getText()
                        .toString()
                        .trim();

        String address =
                edtAppointmentAddress
                        .getText()
                        .toString()
                        .trim();

        String reason =
                edtAppointmentReason
                        .getText()
                        .toString()
                        .trim();

        String appointmentType =
                autoAppointmentType
                        .getText()
                        .toString()
                        .trim();


        Map<String, Object> appointment =
                new HashMap<>();

        appointment.put(
                "patientId",
                patientId
        );
        appointment.put(
                "patientName",
                patientName
        );
        appointment.put(
                "age",
                age
        );
        appointment.put(
                "gender",
                gender
        );
        appointment.put(
                "phone",
                phone
        );
        appointment.put(
                "address",
                address
        );
        appointment.put(
                "reason",
                reason
        );
        appointment.put(
                "doctorId",
                doctorId
        );
        appointment.put(
                "doctorName",
                doctorName
        );
        appointment.put(
                "specialization",
                specialization
        );
        appointment.put(
                "appointmentDate",
                selectedDate
        );
        appointment.put(
                "appointmentDay",
                selectedDay
        );
        appointment.put(
                "appointmentTime",
                selectedTime
        );
        appointment.put(
                "appointmentType",
                appointmentType
        );
        appointment.put(
                "status",
                "Pending"
        );
        appointment.put(
                "createdAt",
                Timestamp.now()
        );

        btnRequestAppointment.setEnabled(false);

        db.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            this,
                            "Appointment request submitted",
                            Toast.LENGTH_LONG
                    ).show();


                    Intent intent =
                            new Intent(
                                    AppointmentActivity.this,
                                    PatientDashboardActivity.class
                            );

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                    );

                    startActivity(intent);

                    finish();

                })
                .addOnFailureListener(e -> {

                    btnRequestAppointment.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Failed to request appointment",
                            Toast.LENGTH_LONG
                    ).show();

                });

    }
    private void loadDoctorDutyDays() {

        doctorScheduleLoaded = false;

        db.collection("doctorSchedules")
                .whereEqualTo("doctorDocumentID", doctorId)
                .whereEqualTo("status", "AVAILABLE")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    doctorDutyDays.clear();

                    for (DocumentSnapshot document : querySnapshot) {

                        String dayOfWeek =
                                document.getString("dayOfWeek");

                        if (dayOfWeek != null &&
                                !dayOfWeek.trim().isEmpty()) {

                            doctorDutyDays.add(
                                    dayOfWeek.trim()
                            );
                        }
                    }

                    doctorScheduleLoaded = true;

                })
                .addOnFailureListener(e -> {

                    doctorScheduleLoaded = false;

                    Toast.makeText(
                            this,
                            "Failed to load doctor's schedule",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
    private static class DoctorDutyDayDecorator
            implements DayViewDecorator {

        private final ArrayList<String> dutyDays;
        DoctorDutyDayDecorator(
                ArrayList<String> dutyDays) {

            this.dutyDays =
                    new ArrayList<>(dutyDays);

        }
        @Override
        public boolean shouldDecorate(
                CalendarDay day) {

            Calendar calendar = Calendar.getInstance();

            calendar.set(
                    day.getYear(),
                    day.getMonth() - 1,
                    day.getDay()
            );

            String dayName =
                    new java.text.SimpleDateFormat(
                            "EEEE",
                            Locale.ENGLISH
                    ).format(
                            calendar.getTime()
                    );

            return dutyDays.contains(
                    dayName
            );

        }
        @Override
        public void decorate(
                DayViewFacade view) {

            view.addSpan(
                    new android.text.style.ForegroundColorSpan(
                            Color.rgb(
                                    37,
                                    99,
                                    235
                            )
                    )
            );

        }

    }
    private void generateTimeSlots(
            String startTime,
            String endTime) {

        try {

            java.text.SimpleDateFormat inputFormat =
                    new java.text.SimpleDateFormat(
                            "hh:mm a",
                            Locale.ENGLISH
                    );

            Calendar start = Calendar.getInstance();

            Calendar end = Calendar.getInstance();

            start.setTime(
                    inputFormat.parse(startTime)
            );

            end.setTime(
                    inputFormat.parse(endTime)
            );

            while (
                    start.before(end)
            ) {

                String slot =
                        inputFormat.format(
                                start.getTime()
                        );

                availableTimeSlots.add(slot);

                start.add(
                        Calendar.MINUTE,
                        30
                );

            }

        }
        catch (Exception e) {

            Toast.makeText(
                    this,
                    "Invalid doctor's schedule time",
                    Toast.LENGTH_SHORT
            ).show();

        }
    }
    private void showTimeSlotDialog() {

        if (availableTimeSlots.isEmpty()) {

            Toast.makeText(
                    this,
                    "No available time slots",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String[] slots =
                availableTimeSlots.toArray(
                        new String[0]
                );

        new AlertDialog.Builder(this)
                .setTitle("Select Appointment Time")
                .setItems(
                        slots,
                        (dialog, which) -> {

                            String selectedSlot =
                                    availableTimeSlots.get(which);

                            if (isTimePast(selectedSlot)) {

                                layoutAppointmentTime.setError(
                                        "This appointment time has already passed. Please select another time."
                                );

                                Toast.makeText(
                                        this,
                                        "This time is unavailable because it has already passed.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            selectedTime = selectedSlot;

                            edtAppointmentTime.setText(
                                    selectedTime
                            );

                            layoutAppointmentTime.setError(
                                    null
                            );

                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }
    private void loadBookedTimeSlots() {

        db.collection("appointments")
                .whereEqualTo(
                        "doctorId",
                        doctorId
                )
                .whereEqualTo(
                        "appointmentDate",
                        selectedDate
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    ArrayList<String> bookedTimeSlots =
                            new ArrayList<>();


                    for (DocumentSnapshot document :
                            querySnapshot) {

                        String status =
                                document.getString("status");

                        String appointmentTime =
                                document.getString(
                                        "appointmentTime"
                                );

                        if (appointmentTime == null ||
                                appointmentTime.isEmpty()) {

                            continue;
                        }

                        if ("Pending".equalsIgnoreCase(status) ||
                                "Approved".equalsIgnoreCase(status) ||
                                "Confirmed".equalsIgnoreCase(status)) {

                            bookedTimeSlots.add(
                                    appointmentTime
                            );
                        }
                    }

                    availableTimeSlots.removeAll(
                            bookedTimeSlots
                    );

                    if (availableTimeSlots.isEmpty()) {

                        Toast.makeText(
                                this,
                                "No available time slots for this date",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    showTimeSlotDialog();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to check booked appointment times",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
    private boolean isTimePast(String time) {

        if (selectedDate == null ||
                selectedDate.isEmpty() ||
                time == null ||
                time.isEmpty()) {

            return false;
        }

        try {

            java.text.SimpleDateFormat dateTimeFormat =
                    new java.text.SimpleDateFormat(
                            "yyyy-MM-dd hh:mm a",
                            Locale.ENGLISH
                    );

            String selectedDateTime =
                    selectedDate + " " + time;

            java.util.Date appointmentDateTime =
                    dateTimeFormat.parse(
                            selectedDateTime
                    );

            java.util.Date now =
                    new java.util.Date();

            return appointmentDateTime != null &&
                    appointmentDateTime.before(now);

        }
        catch (Exception e) {

            return false;
        }
    }

}