package com.example.gentletouchdentalclinic;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddStaffActivity extends AppCompatActivity {
    MaterialToolbar addStaffToolbar;
    TextInputEditText edtFullName;
    TextInputEditText edtPosition;
    TextInputEditText edtEmail;
    TextInputEditText edtPhone;
    TextInputEditText edtDateOfBirth;
    TextInputEditText edtHireDate;
    TextInputEditText edtAddress;
    RadioGroup radioGender;
    RadioButton radioMale;
    RadioButton radioFemale;
    Button btnSaveStaff;
    Button btnCancel;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        addStaffToolbar = findViewById(R.id.addStaffToolbar);

        addStaffToolbar.setNavigationOnClickListener(v -> {

            finish();

        });

        edtFullName = findViewById(R.id.edtFullName);
        edtPosition = findViewById(R.id.edtPosition);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth);
        edtHireDate = findViewById(R.id.edtHireDate);
        edtAddress = findViewById(R.id.edtAddress);

        radioGender = findViewById(R.id.radioGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        btnSaveStaff = findViewById(R.id.btnSaveStaff);
        btnCancel = findViewById(R.id.btnCancel);

        db = FirebaseFirestore.getInstance();

        edtDateOfBirth.setOnClickListener(v -> {

            showDatePicker(edtDateOfBirth);

        });

        edtHireDate.setOnClickListener(v -> {

            showDatePicker(edtHireDate);

        });

        btnSaveStaff.setOnClickListener(v -> {

            addStaff();

        });

        btnCancel.setOnClickListener(v -> {

            finish();

        });


    }

    private void showDatePicker(TextInputEditText editText){

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {

                            String date =
                                    selectedDay + "/"
                                            + (selectedMonth + 1)
                                            + "/"
                                            + selectedYear;

                            editText.setText(date);

                        },
                        year,
                        month,
                        day
                );

        dialog.show();

    }
    private void addStaff(){

        String fullName =
                edtFullName.getText()
                        .toString()
                        .trim();

        String position =
                edtPosition.getText()
                        .toString()
                        .trim();

        String email =
                edtEmail.getText()
                        .toString()
                        .trim();

        String phone =
                edtPhone.getText()
                        .toString()
                        .trim();

        String dob =
                edtDateOfBirth.getText()
                        .toString()
                        .trim();

        String hireDate =
                edtHireDate.getText()
                        .toString()
                        .trim();

        String address =
                edtAddress.getText()
                        .toString()
                        .trim();

        String gender = "";

        int selectedGender =
                radioGender.getCheckedRadioButtonId();

        if(selectedGender == R.id.radioMale){

            gender = "Male";

        }
        else if(selectedGender == R.id.radioFemale) {

            gender = "Female";

        }

        if(TextUtils.isEmpty(fullName)
                || TextUtils.isEmpty(position)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(gender)
                || TextUtils.isEmpty(dob)
                || TextUtils.isEmpty(hireDate)){

            Toast.makeText(
                    this,
                    "Please fill required fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }

        if(!phone.matches("^09\\d{9}$")){
            edtPhone.setError("Enter a valid 11-digit phone number");
            edtPhone.requestFocus();

            return;
        }

        Map<String,Object> staff =
                new HashMap<>();


        staff.put(
                "fullName",
                fullName
        );

        staff.put(
                "Gender",
                gender
        );

        staff.put(
                "position",
                position
        );

        staff.put(
                "email",
                email
        );

        staff.put(
                "phone",
                phone
        );

        staff.put(
                "dateOfBirth",
                dob
        );

        staff.put(
                "hireDate",
                hireDate
        );

        staff.put(
                "address",
                address
        );

        staff.put(
                "createdAt",
                Timestamp.now()
        );

        db.collection("staff")
                .add(staff)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            this,
                            "Staff Added Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }


}