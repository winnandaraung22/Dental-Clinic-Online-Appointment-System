package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDoctorChatActivity extends AppCompatActivity {

    public static final String ARG_CHAT_ID = "chatId";
    public static final String ARG_DOCTOR_ID = "doctorId";
    public static final String ARG_DOCTOR_NAME = "doctorName";
    public static final String ARG_DOCTOR_PHOTO = "doctorPhoto";
    private ImageButton btnBack;
    private ImageView imgDoctor;
    private TextView txtDoctorName;
    private RecyclerView recyclerMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private AdminMessageAdapter adapter;
    private final List<AdminMessage> messageList =
            new ArrayList<>();
    private ListenerRegistration messageListener;
    private String chatId;
    private String doctorId;
    private String doctorName;
    private String doctorPhoto;
    private String adminId;

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_doctor_chat
        );

        Intent intent = getIntent();

        chatId =
                intent.getStringExtra(
                        ARG_CHAT_ID
                );

        doctorId =
                intent.getStringExtra(
                        ARG_DOCTOR_ID
                );

        doctorName =
                intent.getStringExtra(
                        ARG_DOCTOR_NAME
                );

        doctorPhoto =
                intent.getStringExtra(
                        ARG_DOCTOR_PHOTO
                );

        auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {

            adminId = auth.getCurrentUser().getUid();
        }
        if (adminId == null
                || doctorId == null
                || doctorId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Chat information is missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        chatId =
                ChatUtils.getAdminDoctorChatId(
                        adminId,
                        doctorId
                );

        btnBack =
                findViewById(
                        R.id.btnBack
                );

        imgDoctor =
                findViewById(
                        R.id.imgDoctor
                );

        txtDoctorName =
                findViewById(
                        R.id.txtDoctorName
                );

        recyclerMessages =
                findViewById(
                        R.id.recyclerMessages
                );

        etMessage =
                findViewById(
                        R.id.etMessage
                );

        btnSend =
                findViewById(
                        R.id.btnSend
                );

        if (doctorName == null
                || doctorName.isEmpty()) {

            doctorName = "Doctor";
        }

        txtDoctorName.setText(
                doctorName
        );

        loadDoctorPhoto();

        recyclerMessages.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter =
                new AdminMessageAdapter(
                        messageList,
                        adminId
                );

        recyclerMessages.setAdapter(
                adapter
        );

        btnBack.setOnClickListener(
                v -> finish()
        );

        btnSend.setOnClickListener(
                v -> sendMessage()
        );

        listenForMessages();
    }

    private void loadDoctorPhoto() {

        if (doctorPhoto == null
                || doctorPhoto.isEmpty()) {

            imgDoctor.setImageResource(
                    R.drawable.profile
            );

            return;
        }

        try {

            byte[] imageBytes =
                    Base64.decode(
                            doctorPhoto,
                            Base64.DEFAULT
                    );

            Bitmap bitmap =
                    BitmapFactory.decodeByteArray(
                            imageBytes,
                            0,
                            imageBytes.length
                    );

            if (bitmap != null) {

                imgDoctor.setImageBitmap(
                        bitmap
                );

            } else {

                imgDoctor.setImageResource(
                        R.drawable.profile
                );
            }

        } catch (Exception e) {

            imgDoctor.setImageResource(
                    R.drawable.profile
            );
        }
    }

    private void listenForMessages() {

        if (adminId == null
                || doctorId == null
                || doctorId.isEmpty()
                || chatId == null
                || chatId.isEmpty()) {

            return;
        }

        messageListener =
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

                                    messageList.clear();

                                    for (
                                            DocumentSnapshot document :
                                            snapshot.getDocuments()
                                    ) {

                                        String senderId =
                                                document.getString(
                                                        "senderId"
                                                );

                                        String receiverId =
                                                document.getString(
                                                        "receiverId"
                                                );

                                        String message =
                                                document.getString(
                                                        "message"
                                                );

                                        Boolean read =
                                                document.getBoolean(
                                                        "isRead"
                                                );

                                        Timestamp timestamp =
                                                document.getTimestamp(
                                                        "timestamp"
                                                );

                                        if (senderId != null
                                                && receiverId != null
                                                && message != null) {

                                            messageList.add(
                                                    new AdminMessage(
                                                            document.getId(),
                                                            senderId,
                                                            receiverId,
                                                            message,
                                                            read != null && read,
                                                            timestamp
                                                    )
                                            );
                                        }
                                    }

                                    adapter.notifyDataSetChanged();

                                    if (!messageList.isEmpty()) {

                                        recyclerMessages.scrollToPosition(
                                                messageList.size() - 1
                                        );
                                    }

                                    markDoctorMessagesAsRead(
                                            snapshot.getDocuments()
                                    );
                                }
                        );
    }
    private void markDoctorMessagesAsRead(
            List<DocumentSnapshot> documents) {

        if (adminId == null
                || chatId == null) {

            return;
        }

        for (DocumentSnapshot document :
                documents) {

            String senderId =
                    document.getString(
                            "senderId"
                    );

            Boolean isRead =
                    document.getBoolean(
                            "isRead"
                    );

            if (senderId != null
                    && !senderId.equals(adminId)
                    && (isRead == null || !isRead)) {

                db.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .document(document.getId())
                        .update(
                                "isRead",
                                true
                        );
            }
        }
    }
    private void sendMessage() {

        if (adminId == null) {

            Toast.makeText(
                    this,
                    "Admin is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String message =
                etMessage.getText()
                        .toString()
                        .trim();

        if (message.isEmpty()) {
            return;
        }

        if (chatId == null
                || doctorId == null
                || doctorId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Chat information is missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

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
                .addOnSuccessListener(
                        documentReference -> {

                            updateChatLastMessage(
                                    message
                            );

                            etMessage.setText("");
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    this,
                                    "Failed to send message: "
                                            + e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }
    private void updateChatLastMessage(
            String message) {

        if (chatId == null) {
            return;
        }

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
                adminId
        );

        db.collection("chats")
                .document(chatId)
                .update(updates);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (messageListener != null) {

            messageListener.remove();

            messageListener = null;
        }
    }
}