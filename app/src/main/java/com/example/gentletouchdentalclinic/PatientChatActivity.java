package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PatientChatActivity extends AppCompatActivity {
    public static final String ACTION_MESSAGE_READ =
            "com.example.gentletouchdentalclinic.MESSAGE_READ";
    private ArrayList<ChatMessage> messageList;
    private ChatMessageAdapter chatAdapter;
    private ImageButton btnBack;
    private ImageButton btnSend;
    private TextView txtDoctorName;
    private ImageView imgDoctor;
    private EditText etMessage;
    private RecyclerView recyclerMessages;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String patientId;
    private String doctorId;
    private String doctorName;
    private ListenerRegistration messagesListener;

    private String createChatId(String patientId, String doctorId) {

        if (patientId.compareTo(doctorId) < 0) {
            return patientId + "_" + doctorId;
        } else {
            return doctorId + "_" + patientId;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_chat);

        btnBack = findViewById(R.id.btnBack);
        btnSend = findViewById(R.id.btnSend);
        imgDoctor = findViewById(R.id.imgDoctor);
        txtDoctorName = findViewById(R.id.txtDoctorName);
        etMessage = findViewById(R.id.etMessage);

        recyclerMessages = findViewById(R.id.recyclerChatMessages);
        messageList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            finish();
            return;
        }

        patientId = user.getUid();

        chatAdapter =
                new ChatMessageAdapter(
                        messageList,
                        patientId
                );

        recyclerMessages.setAdapter(chatAdapter);

        doctorId = getIntent().getStringExtra("doctorId");
        doctorName = getIntent().getStringExtra("doctorName");

        if (doctorId == null || doctorId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Doctor information not found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }
        loadDoctorProfile();
        markDoctorMessagesAsRead(() -> {});

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);

        recyclerMessages.setLayoutManager(layoutManager);

        btnBack.setOnClickListener(v -> {

            markDoctorMessagesAsRead(() -> {

                finish();
            });
        });

        btnSend.setOnClickListener(v -> sendMessage());

        listenForMessages();
    }

    private void sendMessage() {

        String messageText = etMessage.getText()
                .toString()
                .trim();

        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        String chatId = createChatId(patientId, doctorId);

        Map<String, Object> chatData = new HashMap<>();

        chatData.put("patientId", patientId);
        chatData.put("doctorId", doctorId);
        chatData.put("lastMessage", messageText);
        chatData.put("lastMessageTime", Timestamp.now());

        db.collection("chats")
                .document(chatId)
                .set(
                        chatData,
                        com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(unused -> {

                    Map<String, Object> message =
                            new HashMap<>();

                    message.put("senderId", patientId);
                    message.put("receiverId", doctorId);
                    message.put("message", messageText);
                    message.put("timestamp", Timestamp.now());
                    message.put("isRead", false);

                    db.collection("chats")
                            .document(chatId)
                            .collection("messages")
                            .add(message)
                            .addOnSuccessListener(documentReference -> {

                                etMessage.setText("");

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        PatientChatActivity.this,
                                        "Failed to send message: "
                                                + e.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PatientChatActivity.this,
                            "Failed to create chat: "
                                    + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
    private void markDoctorMessagesAsRead(Runnable onComplete) {

        String chatId = createChatId(
                patientId,
                doctorId
        );

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .whereEqualTo("senderId", doctorId)
                .whereEqualTo("receiverId", patientId)
                .whereEqualTo("isRead", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
                        onComplete.run();
                        return;
                    }

                    com.google.firebase.firestore.WriteBatch batch =
                            db.batch();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        batch.update(
                                document.getReference(),
                                "isRead",
                                true
                        );
                    }

                    batch.commit()
                            .addOnSuccessListener(unused -> {

                                sendBroadcast(
                                        new Intent(
                                                ACTION_MESSAGE_READ
                                        )
                                );

                                onComplete.run();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        PatientChatActivity.this,
                                        "Unable to mark messages as read.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                onComplete.run();
                            });
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PatientChatActivity.this,
                            "Unable to load unread messages.",
                            Toast.LENGTH_SHORT
                    ).show();

                    onComplete.run();
                });
    }

    private void listenForMessages() {

        String chatId =
                createChatId(patientId, doctorId);

        messagesListener =
                db.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .orderBy("timestamp")
                        .addSnapshotListener((snapshot, error) -> {

                            if (error != null) {

                                Toast.makeText(
                                        PatientChatActivity.this,
                                        "Unable to load messages.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            if (snapshot == null) {
                                return;
                            }

                            messageList.clear();

                            for (DocumentSnapshot document :
                                    snapshot.getDocuments()) {

                                ChatMessage chatMessage =
                                        document.toObject(
                                                ChatMessage.class
                                        );

                                if (chatMessage != null) {

                                    messageList.add(
                                            chatMessage
                                    );
                                }
                            }

                            chatAdapter.notifyDataSetChanged();

                            if (!messageList.isEmpty()) {

                                recyclerMessages.scrollToPosition(
                                        messageList.size() - 1
                                );
                            }
                        });
    }
    private void loadDoctorProfile() {

        db.collection("doctors")
                .document(doctorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        txtDoctorName.setText("Dr. Doctor");

                        imgDoctor.setImageResource(
                                R.drawable.doctor
                        );

                        return;
                    }

                    String fullName =
                            documentSnapshot.getString("fullName");

                    if (fullName != null
                            && !fullName.isEmpty()) {

                        if (fullName.startsWith("Dr. ")) {

                            txtDoctorName.setText(fullName);

                        } else {

                            txtDoctorName.setText(
                                    "Dr. " + fullName
                            );
                        }

                    } else {

                        txtDoctorName.setText("Dr. Doctor");
                    }

                    String profileImage =
                            documentSnapshot.getString(
                                    "profileImage"
                            );

                    if (profileImage != null
                            && !profileImage.isEmpty()) {

                        try {

                            byte[] imageBytes =
                                    Base64.decode(
                                            profileImage,
                                            Base64.DEFAULT
                                    );

                            Bitmap bitmap =
                                    BitmapFactory.decodeByteArray(
                                            imageBytes,
                                            0,
                                            imageBytes.length
                                    );

                            if (bitmap != null) {

                                imgDoctor.setImageBitmap(bitmap);

                            } else {

                                imgDoctor.setImageResource(
                                        R.drawable.doctor
                                );
                            }

                        } catch (IllegalArgumentException e) {

                            imgDoctor.setImageResource(
                                    R.drawable.doctor
                            );
                        }

                    } else {

                        imgDoctor.setImageResource(
                                R.drawable.doctor
                        );
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PatientChatActivity.this,
                            "Unable to load doctor profile.",
                            Toast.LENGTH_SHORT
                    ).show();

                    imgDoctor.setImageResource(
                            R.drawable.doctor
                    );
                });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (messagesListener != null) {

            messagesListener.remove();
            messagesListener = null;
        }
    }
}