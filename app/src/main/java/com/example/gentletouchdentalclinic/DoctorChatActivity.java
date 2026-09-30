package com.example.gentletouchdentalclinic;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorChatActivity extends AppCompatActivity {

    private ArrayList<ChatMessage> messageList;
    private ChatMessageAdapter chatAdapter;
    private ImageButton btnBack;
    private ImageButton btnSend;
    private ImageView imgPatient;
    private TextView txtPatientName;
    private EditText etMessage;
    private RecyclerView recyclerMessages;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String doctorId;
    private String patientId;
    private String patientName;
    private ListenerRegistration messagesListener;

    private String createChatId(
            String patientId,
            String doctorId) {

        return ChatUtils.getAdminDoctorChatId(
                patientId,
                doctorId
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_doctor_chat
        );

        btnBack = findViewById(
                R.id.btnBack
        );

        btnSend = findViewById(
                R.id.btnSend
        );

        imgPatient = findViewById(
                R.id.imgPatient
        );

        txtPatientName = findViewById(
                R.id.txtPatientName
        );

        etMessage = findViewById(
                R.id.etMessage
        );

        recyclerMessages = findViewById(
                R.id.recyclerDoctorChatMessages
        );

        auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Doctor login required.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        doctorId = user.getUid();

        patientId = getIntent().getStringExtra(
                "patientId"
        );

        patientName = getIntent().getStringExtra(
                "patientName"
        );

        if (patientId == null
                || patientId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Patient information not found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        if (patientName != null
                && !patientName.isEmpty()) {

            txtPatientName.setText(
                    patientName
            );

        } else {

            txtPatientName.setText(
                    "Patient"
            );
        }

        imgPatient.setImageResource(
                R.drawable.users_profile
        );

        messageList = new ArrayList<>();

        chatAdapter =
                new ChatMessageAdapter(
                        messageList,
                        doctorId
                );

        recyclerMessages.setAdapter(
                chatAdapter
        );

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(
                true
        );

        recyclerMessages.setLayoutManager(
                layoutManager
        );

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        markMessagesAsRead();

                        finish();
                    }
                }
        );

        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(
                v -> sendMessage()
        );

        listenForMessages();
    }

    private void sendMessage() {

        String messageText =
                etMessage.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(messageText)) {

            return;
        }

        String chatId = createChatId(
                patientId,
                doctorId
        );

        String receiverId = patientId;

        Map<String, Object> message =
                new HashMap<>();

        message.put(
                "senderId",
                doctorId
        );

        message.put(
                "receiverId",
                receiverId
        );

        message.put(
                "message",
                messageText
        );

        message.put(
                "timestamp",
                Timestamp.now()
        );

        message.put(
                "isRead",
                false
        );

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)

                .addOnSuccessListener(
                        documentReference -> {

                            etMessage.setText("");

                        }
                )

                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    DoctorChatActivity.this,
                                    "Failed to send message: "
                                            + e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                );
    }

    private void listenForMessages() {

        String chatId = createChatId(
                patientId,
                doctorId
        );

        messagesListener =
                db.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .orderBy(
                                "timestamp"
                        )
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        Toast.makeText(
                                                DoctorChatActivity.this,
                                                "Unable to load messages.",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        return;
                                    }

                                    if (snapshot == null) {

                                        return;
                                    }

                                    messageList.clear();

                                    for (
                                            DocumentSnapshot document
                                            : snapshot.getDocuments()
                                    ) {

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

                                }
                        );
    }
    private void markMessagesAsRead() {

        if (doctorId == null) {
            return;
        }

        if (patientId == null || patientId.isEmpty()) {
            return;
        }

        String chatId = createChatId(
                patientId,
                doctorId
        );

        String receiverId = doctorId;

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .whereEqualTo(
                        "receiverId",
                        receiverId
                )
                .whereEqualTo(
                        "isRead",
                        false
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
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

                    batch.commit();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            DoctorChatActivity.this,
                            "Unable to mark messages as read.",
                            Toast.LENGTH_SHORT
                    ).show();

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