package com.example.gentletouchdentalclinic;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddDoctorActivity extends AppCompatActivity {
    private static final String CLINIC_ADMIN_UID =
            "UACW4aAjCBX8GTdZ0R3b2xbWqgi2";
    private MaterialToolbar toolbar;
    private ImageView imgDoctor;
    private MaterialButton btnSelectImage;
    private MaterialButton btnSaveDoctor;
    private TextInputEditText etStaffCode;
    private TextInputEditText etDoctorName;
    private TextInputEditText etDoctorEmail;
    private TextInputEditText etTemporaryPassword;
    private TextInputEditText etDoctorPhone;
    private TextInputEditText etSpecialization;
    private TextInputEditText etQualification;
    private TextInputEditText etExperience;
    private FirebaseFirestore db;
    private FirebaseAuth secondaryAuth;
    private String profileImage = "";
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        toolbar = findViewById(R.id.addDoctorToolbar);
        imgDoctor = findViewById(R.id.imgDoctor);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSaveDoctor = findViewById(R.id.btnSaveDoctor);

        etStaffCode = findViewById(R.id.etStaffCode);
        etDoctorName = findViewById(R.id.etDoctorName);
        etDoctorEmail = findViewById(R.id.etDoctorEmail);
        etTemporaryPassword = findViewById(R.id.etTemporaryPassword);
        etDoctorPhone = findViewById(R.id.etDoctorPhone);
        etSpecialization = findViewById(R.id.etSpecialization);
        etQualification = findViewById(R.id.etQualification);
        etExperience = findViewById(R.id.etExperience);

        db = FirebaseFirestore.getInstance();
        secondaryAuth = getSecondaryAuth();

        toolbar.setNavigationOnClickListener(v -> finish());

        imagePickerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {

                            if (uri != null) {

                                imgDoctor.setImageURI(uri);

                                convertImageToBase64(uri);

                            }

                        });


        imgDoctor.setOnClickListener(v -> {

            imagePickerLauncher.launch("image/*");

        });

        btnSelectImage.setOnClickListener(v -> {

            imagePickerLauncher.launch("image/*");

        });

        btnSaveDoctor.setOnClickListener(v -> {

            saveDoctor();

        });
    }
    private void saveDoctor(){

        String staffCode =
                etStaffCode.getText().toString().trim();

        String name =
                etDoctorName.getText().toString().trim();

        String email =
                etDoctorEmail.getText().toString().trim();

        String password =
                etTemporaryPassword.getText().toString().trim();

        String phone =
                etDoctorPhone.getText().toString().trim();

        String specialization =
                etSpecialization.getText().toString().trim();

        String qualification =
                etQualification.getText().toString().trim();

        String experience =
                etExperience.getText().toString().trim();

        if(staffCode.isEmpty()
                || name.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || phone.isEmpty()
                || specialization.isEmpty()
                || qualification.isEmpty()
                || experience.isEmpty()){

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }
        if(!phone.matches("[0-9]+")){

            Toast.makeText(
                    this,
                    "Phone number must contain only digits",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }

        if(phone.length() < 11){

            Toast.makeText(
                    this,
                    "Phone number must be at least 11 digits",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }
        if(password.length() < 6){

            Toast.makeText(
                    this,
                    "Temporary password must be at least 6 characters",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }
        checkStaffCodeExists(
                staffCode,
                name,
                email,
                password,
                phone,
                specialization,
                qualification,
                experience
        );
    }
    private void checkStaffCodeExists(
            String staffCode,
            String name,
            String email,
            String password,
            String phone,
            String specialization,
            String qualification,
            String experience
    ){

        db.collection("users")
                .whereEqualTo("staffAccessCode", staffCode)
                .get()
                .addOnSuccessListener(userSnapshot -> {

                    if(!userSnapshot.isEmpty()){

                        Toast.makeText(
                                this,
                                "Staff Access Code already exists",
                                Toast.LENGTH_SHORT
                        ).show();

                    }
                    else{

                        db.collection("doctors")
                                .whereEqualTo("staffCode", staffCode)
                                .get()
                                .addOnSuccessListener(doctorSnapshot -> {

                                    if(!doctorSnapshot.isEmpty()){

                                        Toast.makeText(
                                                this,
                                                "Staff Code already exists",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                    }
                                    else{

                                        db.collection("users")
                                                .whereEqualTo("email", email)
                                                .get()
                                                .addOnSuccessListener(emailSnapshot -> {

                                                    if(!emailSnapshot.isEmpty()){

                                                        Toast.makeText(
                                                                this,
                                                                "Email already exists",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                    }
                                                    else{

                                                        createDoctorAccount(
                                                                staffCode,
                                                                name,
                                                                email,
                                                                password,
                                                                phone,
                                                                specialization,
                                                                qualification,
                                                                experience
                                                        );
                                                    }
                                                });
                                    }
                                });
                    }

                });

    }
    private void createDoctorAccount(
            String staffCode,
            String name,
            String email,
            String password,
            String phone,
            String specialization,
            String qualification,
            String experience){

        secondaryAuth.createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnSuccessListener(authResult -> {

                    FirebaseUser firebaseUser =
                            secondaryAuth.getCurrentUser();

                    if(firebaseUser != null){

                        String uid = firebaseUser.getUid();

                        createUserCollection(
                                uid,
                                staffCode,
                                name,
                                email,
                                phone,
                                specialization,
                                qualification,
                                experience
                        );

                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Account creation failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                });

    }
    private void createUserCollection(
            String uid,
            String staffCode,
            String name,
            String email,
            String phone,
            String specialization,
            String qualification,
            String experience){

        Map<String,Object> user =
                new HashMap<>();

        user.put("fullName", name);
        user.put("email", email);
        user.put("phone", phone);
        user.put("staffAccessCode", staffCode);
        user.put("role", "doctor");

        db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(unused -> {

                    createDoctorCollection(
                            uid,
                            staffCode,
                            name,
                            email,
                            phone,
                            specialization,
                            qualification,
                            experience
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "User creation failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                });


    }
    private void createDoctorCollection(
            String uid,
            String staffCode,
            String name,
            String email,
            String phone,
            String specialization,
            String qualification,
            String experience){

        Map<String,Object> doctor =
                new HashMap<>();

        doctor.put("staffAccessCode", staffCode);
        doctor.put("fullName", name);
        doctor.put("email", email);
        doctor.put("phone", phone);
        doctor.put("specialization", specialization);
        doctor.put("qualification", qualification);
        doctor.put("experience", experience);
        doctor.put("profileImage", profileImage);
        doctor.put("role", "doctor");
        doctor.put("rating", 0.0);

        db.collection("doctors")
                .document(uid)
                .set(doctor)
                .addOnSuccessListener(unused -> {

                    createDoctorAdminChat(
                            uid,
                            name,
                            profileImage
                    );

                    secondaryAuth.signOut();

                    Toast.makeText(
                            this,
                            "Doctor account created successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Doctor creation failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();


                });


    }
    private FirebaseAuth getSecondaryAuth(){

        FirebaseApp secondaryApp;

        try {

            secondaryApp = FirebaseApp.getInstance("Secondary");

        }
        catch(IllegalStateException e){

            FirebaseOptions options =
                    FirebaseApp.getInstance()
                            .getOptions();

            secondaryApp =
                    FirebaseApp.initializeApp(
                            getApplicationContext(),
                            options,
                            "Secondary"
                    );

        }

        return FirebaseAuth.getInstance(
                secondaryApp
        );

    }
    private void convertImageToBase64(Uri uri){


        try {

            Bitmap bitmap =
                    BitmapFactory.decodeStream(
                            getContentResolver()
                                    .openInputStream(uri)
                    );

            bitmap =
                    Bitmap.createScaledBitmap(
                            bitmap,
                            500,
                            500,
                            true
                    );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    50,
                    outputStream
            );

            byte[] imageBytes =
                    outputStream.toByteArray();

            profileImage =
                    Base64.encodeToString(
                            imageBytes,
                            Base64.DEFAULT
                    );

        }
        catch(Exception e){

            Toast.makeText(
                    this,
                    "Image conversion failed",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }
    private void createDoctorAdminChat(
            String doctorId,
            String doctorName,
            String doctorPhoto) {

        String adminId = CLINIC_ADMIN_UID;

        String chatId =
                ChatUtils.getAdminDoctorChatId(
                        adminId,
                        doctorId
                );

        Map<String, Object> chat =
                new HashMap<>();

        chat.put(
                "adminId",
                adminId
        );

        chat.put(
                "doctorId",
                doctorId
        );

        chat.put(
                "doctorName",
                doctorName
        );

        chat.put(
                "doctorPhoto",
                doctorPhoto
        );

        chat.put(
                "participantIds",
                java.util.Arrays.asList(
                        adminId,
                        doctorId
                )
        );

        chat.put(
                "lastMessage",
                "Welcome to GentleTouch Dental Clinic, "
                        + doctorName
        );

        chat.put(
                "lastMessageTime",
                Timestamp.now()
        );

        chat.put(
                "lastSenderId",
                adminId
        );

        db.collection("chats")
                .document(chatId)
                .set(chat)
                .addOnSuccessListener(unused -> {

                    createWelcomeMessage(
                            chatId,
                            adminId,
                            doctorId,
                            doctorName
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to create doctor chat: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
    private void createWelcomeMessage(
            String chatId,
            String adminId,
            String doctorId,
            String doctorName) {

        String message =
                "Welcome to GentleTouch Dental Clinic,  "
                        + doctorName;

        Map<String, Object> messageData =
                new HashMap<>();

        messageData.put(
                "senderId",
                adminId
        );

        messageData.put(
                "receiverId",
                doctorId
        );

        messageData.put(
                "message",
                message
        );

        messageData.put(
                "isRead",
                false
        );

        messageData.put(
                "timestamp",
                Timestamp.now()
        );

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageData)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Doctor chat created successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Chat created, but welcome message failed",
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}
