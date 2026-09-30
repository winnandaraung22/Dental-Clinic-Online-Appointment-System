package com.example.gentletouchdentalclinic;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText etName, etEmail, etPhone, etUsername;
    TextInputEditText etPassword, etConfirmPassword;
    TextView txtLogin;

    MaterialButton btnCreateAccount;

    FirebaseAuth auth;
    FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        txtLogin = findViewById(R.id.txtLogin);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        });

        btnCreateAccount.setOnClickListener(v -> {
            registerPatient();
        });
    }
    private void registerPatient(){
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if(name.isEmpty() || username.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || password.isEmpty()){

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if(!username.matches("[a-zA-Z0-9_]+")){

            Toast.makeText(
                    this,
                    "Username can contain only letters, numbers and underscore",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()){

            Toast.makeText(
                    this,
                    "Invalid email format",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if(password.length() < 6){

            Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if(!phone.matches("\\d{11}")){

            Toast.makeText(
                    this,
                    "Phone number must be exactly 11 digits",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if(confirmPassword.isEmpty()){
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(confirmPassword)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(query -> {

                    if(!query.isEmpty()){

                        Toast.makeText(
                                this,
                                "Username already exists",
                                Toast.LENGTH_SHORT
                        ).show();

                    }
                    else{

                        createPatientAccount(
                                name,
                                username,
                                email,
                                phone,
                                password
                        );

                    }

                });
    }
    private void createPatientAccount(
            String name,
            String username,
            String email,
            String phone,
            String password
    ){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()){

                        FirebaseUser user = auth.getCurrentUser();
                        String uid = user.getUid();

                        Map<String,Object> patient = new HashMap<>();

                        patient.put("fullName", name);
                        patient.put("username", username);
                        patient.put("email", email);
                        patient.put("phone", phone);
                        patient.put("role", "patient");
                        patient.put(
                                "createdAt",
                                com.google.firebase.Timestamp.now()
                        );


                        db.collection("users")
                                .document(uid)
                                .set(patient)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            this,
                                            "Account Created Successfully",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    etName.setText("");
                                    etUsername.setText("");
                                    etEmail.setText("");
                                    etPhone.setText("");
                                    etPassword.setText("");
                                    etConfirmPassword.setText("");

                                    FirebaseAuth.getInstance().signOut();

                                    Intent intent = new Intent(
                                            RegisterActivity.this,
                                            LoginActivity.class
                                    );

                                    startActivity(intent);
                                    finish();

                                }).addOnFailureListener(e -> {
                                    Toast.makeText(
                                            this,
                                            "Failed to save user data: " + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                });

                    }
                    else{

                        Toast.makeText(
                                this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });
    }
}
