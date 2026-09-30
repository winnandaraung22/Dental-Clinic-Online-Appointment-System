package com.example.gentletouchdentalclinic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;


public class LoginActivity extends AppCompatActivity {

    private static final String NOTIFICATION_CHANNEL_ID =
            "appointment_notifications";
    private TextInputEditText etEmail, etPassword, etUsername;
    private MaterialButton btnLogin;
    private TextView txtForgotPassword;
    private CheckBox cbRememberMe;
    private SharedPreferences preferences;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {

                        FirebaseUser user = auth.getCurrentUser();

                        if (isGranted && user != null) {

                            showUnreadPatientNotifications(
                                    user.getUid()
                            );
                        }
                    }
            );

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createNotificationChannel();

        preferences = getSharedPreferences("loginPreference", MODE_PRIVATE);

        boolean remember = preferences.getBoolean("rememberMe", false);


        if(remember){

            FirebaseUser user = auth.getCurrentUser();

            if(user != null){

                checkRememberedUser(user);
                return;
            }

        }

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);

        txtForgotPassword = findViewById(R.id.txtForgot);

        cbRememberMe = findViewById(R.id.cbRememberMe);
        cbRememberMe.setChecked(
                preferences.getBoolean("rememberMe", false)
        );

        btnLogin.setOnClickListener(v -> loginUser());

        txtForgotPassword.setOnClickListener(v -> resetPassword());
    }

    private void checkRememberedUser(FirebaseUser user){

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    if(!document.exists()){

                        auth.signOut();

                        preferences.edit()
                                .clear()
                                .apply();

                        return;
                    }

                    String role = document.getString("role");

                    if("patient".equals(role)){

                        preparePatientNotifications(user.getUid());

                        startActivity(new Intent(
                                LoginActivity.this,
                                PatientDashboardActivity.class
                        ));

                    }
                    else if("doctor".equals(role)){

                        startActivity(new Intent(
                                LoginActivity.this,
                                DoctorDashboardActivity.class
                        ));

                    }
                    else if("admin".equals(role)){

                        startActivity(new Intent(
                                LoginActivity.this,
                                AdminDashboardActivity.class
                        ));

                    }
                    else{

                        auth.signOut();

                        preferences.edit()
                                .clear()
                                .apply();

                        Toast.makeText(
                                this,
                                "Unknown account role.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;

                    }

                    finish();

                });

    }
    private void loginUser(){
        String loginId = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(loginId.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter username, email and password", Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("username", loginId)
                .whereEqualTo("role", "patient")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if(!queryDocumentSnapshots.isEmpty()){
                        checkUserAndLogin(
                                queryDocumentSnapshots.getDocuments().get(0),
                                email,
                                password
                        );


                    } else {

                        db.collection("users")
                                .whereEqualTo("staffAccessCode", loginId)
                                .get()
                                .addOnSuccessListener(staffQuery -> {

                                    if (!staffQuery.isEmpty()) {

                                        checkUserAndLogin(
                                                staffQuery.getDocuments().get(0),
                                                email,
                                                password
                                        );

                                    } else {

                                        Toast.makeText(
                                                this,
                                                "No user found!",
                                                Toast.LENGTH_LONG
                                        ).show();

                                    }

                                });

                    }

                });
    }

    private void checkUserAndLogin(
            com.google.firebase.firestore.DocumentSnapshot document,
            String email,
            String password
    ){

        String firestoreEmail = document.getString("email");

        if(firestoreEmail == null || !email.equalsIgnoreCase(firestoreEmail)){

            Toast.makeText(
                    this,
                    "Email does not match this account.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()){

                        FirebaseUser user = auth.getCurrentUser();

                        if (user == null) {
                            return;
                        }

                        if(cbRememberMe.isChecked()){

                            preferences.edit()
                                    .putBoolean("rememberMe", true)
                                    .apply();

                        }
                        else{

                            preferences.edit()
                                    .putBoolean("rememberMe", false)
                                    .apply();

                        }

                        String role = document.getString("role");

                        if(role == null){

                            auth.signOut();

                            preferences.edit()
                                    .clear()
                                    .apply();

                            Toast.makeText(
                                    this,
                                    "Invalid account information.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        if("patient".equals(role)){

                            Toast.makeText(
                                    this,
                                    "Login successful.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            preparePatientNotifications(user.getUid());

                            startActivity(
                                    new Intent(
                                            LoginActivity.this,
                                            PatientDashboardActivity.class
                                    )
                            );

                            finish();

                        }
                        else if("doctor".equals(role)){

                            Toast.makeText(
                                    this,
                                    "Doctor login successful.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            startActivity(
                                    new Intent(
                                            LoginActivity.this,
                                            DoctorDashboardActivity.class
                                    )
                            );

                            finish();

                        }
                        else if("admin".equals(role)){

                            Toast.makeText(
                                    this,
                                    "Administrator login successful.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            startActivity(
                                    new Intent(
                                            LoginActivity.this,
                                            AdminDashboardActivity.class
                                    )
                            );

                            finish();

                        }

                    }else{

                        Toast.makeText(
                                this,
                                "Incorrect email or password.",
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    private void resetPassword() {

        String email = etEmail.getText().toString().trim();

        if(email.isEmpty()){

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Email Required")
                    .setMessage("Please enter your email address.")
                    .setPositiveButton("OK", null)
                    .show();

            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(query -> {

                    if(query.isEmpty()){

                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Email Not Found")
                                .setMessage("This email is not registered.")
                                .setPositiveButton("OK", null)
                                .show();

                    }else{

                        auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener(unused -> {

                                    new MaterialAlertDialogBuilder(this)
                                            .setTitle("Password Reset")
                                            .setMessage(
                                                    "A password reset link has been sent to your email.\nPlease check email inbox or spam folder!"
                                            )
                                            .setPositiveButton("OK", null)
                                            .show();

                                })
                                .addOnFailureListener(e -> {

                                    new MaterialAlertDialogBuilder(this)
                                            .setTitle("Reset Failed")
                                            .setMessage(e.getMessage())
                                            .setPositiveButton("OK", null)
                                            .show();

                                });

                    }

                });

    }
    private void preparePatientNotifications(String patientId) {

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                );

                return;
            }
        }

        showUnreadPatientNotifications(patientId);
    }
    private void showUnreadPatientNotifications(
            String patientId) {

        db.collection("notifications")
                .whereEqualTo(
                        "userId",
                        patientId
                )
                .whereEqualTo(
                        "isRead",
                        false
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            for (com.google.firebase.firestore.DocumentSnapshot document :
                                    querySnapshot.getDocuments()) {

                                String title =
                                        document.getString("title");

                                String message =
                                        document.getString("message");

                                showLocalNotification(
                                        document.getId(),
                                        title,
                                        message
                                );
                            }
                        }
                );
    }
    private void showLocalNotification(
            String notificationId,
            String title,
            String message) {

        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (notificationManager == null) {
            return;
        }
        Intent notificationIntent =
                new Intent(
                        LoginActivity.this,
                        PatientDashboardActivity.class
                );

        notificationIntent.putExtra(
                "openNotifications",
                true
        );

        notificationIntent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        notificationId.hashCode(),
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        android.app.Notification.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            builder =
                    new android.app.Notification.Builder(
                            this,
                            "appointment_notifications"
                    );

        } else {

            builder =
                    new android.app.Notification.Builder(
                            this
                    );
        }

        builder
                .setSmallIcon(
                        R.drawable.notification
                )
                .setContentTitle(
                        "GentleTouch Dental Clinic"
                )
                .setContentText(
                        safeText(
                                title,
                                "Your appointment has been updated."
                        )
                )
                .setStyle(
                        new android.app.Notification.BigTextStyle()
                                .bigText(
                                        safeText(
                                                message,
                                                "Your appointment has been updated."
                                        )
                                )
                )
                .setPriority(
                        android.app.Notification.PRIORITY_HIGH
                )
                .setContentIntent(
                        pendingIntent
                )
                .setAutoCancel(true);

        int notificationIdNumber =
                notificationId.hashCode();

        notificationManager.notify(
                notificationIdNumber,
                builder.build()
        );
    }
    private void createNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            "Appointment Notifications",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Notifications about appointment approval and cancellation"
            );

            NotificationManager notificationManager =
                    getSystemService(
                            NotificationManager.class
                    );

            if (notificationManager != null) {

                notificationManager.createNotificationChannel(
                        channel
                );
            }
        }
    }
    private String safeText(
            String value,
            String defaultValue) {

        if (value == null ||
                value.trim().isEmpty()) {

            return defaultValue;
        }

        return value;
    }
}
