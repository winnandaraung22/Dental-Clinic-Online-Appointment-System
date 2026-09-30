package com.example.gentletouchdentalclinic;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class DoctorAdminChatActivity extends AppCompatActivity {

    private ImageButton btnChatBack;
    private ImageView imgClinicProfile;
    private TextView txtClinicName;
    private RecyclerView recyclerMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private LinearLayout chatHeader;

    private static final String CLINIC_ADMIN_UID =
            "UACW4aAjCBX8GTdZ0R3b2xbWqgi2";

    private String doctorId;
    private String chatId;
    private DoctorMessageAdapter messageAdapter;

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_doctor_chat
        );

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Doctor is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        doctorId =
                auth.getCurrentUser().getUid();

        chatId =
                ChatUtils.getAdminDoctorChatId(
                        CLINIC_ADMIN_UID,
                        doctorId
                );

        btnChatBack =
                findViewById(R.id.btnBack);

        chatHeader =
                findViewById(R.id.chatHeader);

        imgClinicProfile =
                findViewById(R.id.imgDoctor);

        txtClinicName =
                findViewById(R.id.txtDoctorName);

        recyclerMessages =
                findViewById(R.id.recyclerMessages);

        etMessage =
                findViewById(R.id.etMessage);

        btnSend =
                findViewById(R.id.btnSend);

        setupChatHeader();

        setupRecyclerView();

        listenForMessages();

        btnSend.setOnClickListener(
                v -> sendMessage()
        );

        btnChatBack.setOnClickListener(
                v -> finish()
        );
    }

    private void setupChatHeader() {

        txtClinicName.setText(
                "Clinic Administrator"
        );

        imgClinicProfile.setImageResource(
                R.drawable.users_profile
        );

        chatHeader.setBackgroundResource(
                R.drawable.splash_gradient
        );

    }

    private void setupRecyclerView() {

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);

        recyclerMessages.setLayoutManager(
                layoutManager
        );

        messageAdapter =
                new DoctorMessageAdapter(
                        this,
                        doctorId
                );

        recyclerMessages.setAdapter(
                messageAdapter
        );
    }

    private void listenForMessages() {

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy(
                        "timestamp",
                        Query.Direction.ASCENDING
                )
                .addSnapshotListener(
                        (snapshot, error) -> {

                            if (error != null) {

                                Toast.makeText(
                                        this,
                                        "Failed to load messages: "
                                                + error.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            if (snapshot == null) {
                                return;
                            }

                            messageAdapter.clearMessages();

                            for (
                                    DocumentSnapshot document :
                                    snapshot.getDocuments()
                            ) {

                                String message =
                                        document.getString(
                                                "message"
                                        );

                                String senderId =
                                        document.getString(
                                                "senderId"
                                        );

                                String receiverId =
                                        document.getString(
                                                "receiverId"
                                        );

                                Timestamp timestamp =
                                        document.getTimestamp(
                                                "timestamp"
                                        );

                                Boolean readValue =
                                        document.getBoolean(
                                                "isRead"
                                        );

                                boolean isRead =
                                        Boolean.TRUE.equals(
                                                readValue
                                        );

                                if (
                                        message != null
                                                && senderId != null
                                ) {

                                    messageAdapter.addMessage(
                                            document.getId(),
                                            message,
                                            senderId,
                                            receiverId,
                                            timestamp,
                                            isRead
                                    );
                                }
                            }

                            messageAdapter.notifyDataSetChanged();

                            if (
                                    messageAdapter.getItemCount()
                                            > 0
                            ) {

                                recyclerMessages.scrollToPosition(
                                        messageAdapter.getItemCount() - 1
                                );
                            }

                            markAdminMessagesAsRead(
                                    snapshot
                            );
                        }
                );
    }

    private void sendMessage() {

        String text =
                etMessage.getText()
                        .toString()
                        .trim();

        if (text.isEmpty()) {
            return;
        }

        Map<String, Object> messageData =
                new HashMap<>();

        messageData.put(
                "senderId",
                doctorId
        );

        messageData.put(
                "receiverId",
                CLINIC_ADMIN_UID
        );

        messageData.put(
                "message",
                text
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
                .addOnSuccessListener(
                        documentReference -> {

                            etMessage.setText("");

                            updateLastMessage(
                                    text
                            );
                        }
                )
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to send message: "
                                        + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void updateLastMessage(
            String message) {

        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "lastMessage",
                message
        );

        updates.put(
                "lastMessageTime",
                Timestamp.now()
        );

        updates.put(
                "lastSenderId",
                doctorId
        );

        db.collection("chats")
                .document(chatId)
                .update(updates);
    }

    private void markAdminMessagesAsRead(
            com.google.firebase.firestore.QuerySnapshot snapshot) {

        for (
                DocumentSnapshot document :
                snapshot.getDocuments()
        ) {

            String senderId =
                    document.getString(
                            "senderId"
                    );

            Boolean isRead =
                    document.getBoolean(
                            "isRead"
                    );

            if (
                    CLINIC_ADMIN_UID.equals(senderId)
                            && !Boolean.TRUE.equals(isRead)
            ) {

                document.getReference()
                        .update(
                                "isRead",
                                true
                        );
            }
        }
    }
}