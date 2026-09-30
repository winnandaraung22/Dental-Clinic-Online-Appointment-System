package com.example.gentletouchdentalclinic;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class EditStaffActivity extends AppCompatActivity {
    MaterialToolbar editStaffToolbar;
    EditText edtFullName;
    RadioGroup radioGender;
    RadioButton radioMale, radioFemale;
    EditText edtPosition;
    EditText edtEmail;
    EditText edtPhone;
    EditText edtDateOfBirth;
    EditText edtHireDate;
    EditText edtAddress;
    Button btnUpdateStaff;
    Button btnCancel;
    FirebaseFirestore db;
    String staffId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_staff);

        editStaffToolbar =
                findViewById(
                        R.id.editStaffToolbar
                );

        editStaffToolbar.setNavigationOnClickListener(v -> {

            finish();

        });

        staffId =
                getIntent()
                        .getStringExtra("staffId");


        db = FirebaseFirestore.getInstance();

        edtFullName = findViewById(R.id.edtEditFullName);

        radioGender = findViewById(R.id.radioEditGender);
        radioMale = findViewById(R.id.radioEditMale);
        radioFemale = findViewById(R.id.radioEditFemale);

        edtPosition = findViewById(R.id.edtEditPosition);
        edtEmail = findViewById(R.id.edtEditEmail);
        edtPhone = findViewById(R.id.edtEditPhone);
        edtDateOfBirth = findViewById(R.id.edtEditDOB);
        edtHireDate = findViewById(R.id.edtEditHireDate);
        edtAddress = findViewById(R.id.edtEditAddress);

        btnUpdateStaff = findViewById(R.id.btnUpdateStaff);
        btnCancel = findViewById(R.id.btnCancelEdit);

        edtDateOfBirth.setOnClickListener(v ->
                showDatePicker(edtDateOfBirth)
        );

        edtHireDate.setOnClickListener(v ->
                showDatePicker(edtHireDate)
        );

        loadStaff();

        btnUpdateStaff.setOnClickListener(v -> {

            updateStaff();

        });

        btnCancel.setOnClickListener(v -> {

            finish();

        });


    }
    private void loadStaff(){

        db.collection("staff")
                .document(staffId)
                .get()
                .addOnSuccessListener(document -> {

                    if(document.exists()){

                        edtFullName.setText(
                                document.getString("fullName")
                        );

                        String gender =
                                document.getString("Gender");

                        if(gender != null){

                            if(gender.equalsIgnoreCase("Male")){

                                radioMale.setChecked(true);

                            }
                            else if(gender.equalsIgnoreCase("Female")){

                                radioFemale.setChecked(true);

                            }

                        }

                        edtPosition.setText(
                                document.getString("position")
                        );

                        edtEmail.setText(
                                document.getString("email")
                        );

                        edtPhone.setText(
                                document.getString("phone")
                        );

                        edtDateOfBirth.setText(
                                document.getString("dateOfBirth")
                        );

                        edtHireDate.setText(
                                document.getString("hireDate")
                        );

                        edtAddress.setText(
                                document.getString("address")
                        );


                    }


                });


    }

    private void updateStaff(){

        int selectedGender =
                radioGender.getCheckedRadioButtonId();

        if(selectedGender == -1){

            Toast.makeText(
                    this,
                    "Please select gender",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }

        String gender;

        if(selectedGender == R.id.radioEditMale){

            gender = "Male";

        }
        else{

            gender = "Female";

        }

        Map<String,Object> staff =
                new HashMap<>();

        staff.put(
                "fullName",
                edtFullName.getText().toString().trim()
        );

        staff.put(
                "Gender",
                gender
        );

        staff.put(
                "position",
                edtPosition.getText().toString().trim()
        );

        staff.put(
                "email",
                edtEmail.getText().toString().trim()
        );

        staff.put(
                "phone",
                edtPhone.getText().toString().trim()
        );

        staff.put(
                "dateOfBirth",
                edtDateOfBirth.getText().toString()
        );

        staff.put(
                "hireDate",
                edtHireDate.getText().toString()
        );

        staff.put(
                "address",
                edtAddress.getText().toString().trim()
        );

        db.collection("staff")
                .document(staffId)
                .update(staff)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Staff updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();


                });


    }
    private void showDatePicker(EditText editText){

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, day) -> {

                            String date =
                                    year + "-" +
                                            (month + 1) + "-" +
                                            day;

                            editText.setText(date);

                        },

                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)

                );

        dialog.show();

    }

}