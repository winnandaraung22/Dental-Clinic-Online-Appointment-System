package com.example.gentletouchdentalclinic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class DoctorChatFragment extends Fragment {

    private ImageButton btnChatBack;
    private ImageView imgClinicProfile;
    private TextView txtClinicName;
    private TextView txtClinicStatus;
    private RecyclerView recyclerMessages;
    private TextInputEditText etMessage;
    private ImageButton btnSend;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private static final String CLINIC_ADMIN_UID =
            "UACW4aAjCBX8GTdZ0R3b2xbWqgi2";
    private String doctorId;
    private String chatId;
    private DoctorMessageAdapter messageAdapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_doctor_chat,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState
        );

        btnChatBack =
                view.findViewById(
                        R.id.btnChatBack
                );

        imgClinicProfile =
                view.findViewById(
                        R.id.imgClinicProfile
                );

        txtClinicName =
                view.findViewById(
                        R.id.txtClinicName
                );

        txtClinicStatus =
                view.findViewById(
                        R.id.txtClinicStatus
                );

        recyclerMessages =
                view.findViewById(
                        R.id.recyclerMessages
                );

        etMessage =
                view.findViewById(
                        R.id.etMessage
                );

        btnSend =
                view.findViewById(
                        R.id.btnSend
                );

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    requireContext(),
                    "Doctor is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        doctorId = auth.getCurrentUser().getUid();

        chatId =
                ChatUtils.getAdminDoctorChatId(
                        CLINIC_ADMIN_UID,
                        doctorId
                );

        setupChatHeader();

        setupRecyclerView();

        listenForMessages();

        btnSend.setOnClickListener(
                v -> sendMessage()
        );

        btnChatBack.setOnClickListener(
                v -> {

                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack();

                }
        );
    }

    private void setupChatHeader() {

        txtClinicName.setText(
                "GentleTouch Dental Clinic"
        );

        txtClinicStatus.setText(
                "Clinic Administrator"
        );

        imgClinicProfile.setImageResource(
                R.drawable.logo
        );
    }

    private void setupRecyclerView() {

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        requireContext()
                );

        layoutManager.setStackFromEnd(
                true
        );

        recyclerMessages.setLayoutManager(
                layoutManager
        );


        messageAdapter =
                new DoctorMessageAdapter(
                        requireContext(),
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
                                        requireContext(),
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
                                    DocumentSnapshot document
                                    : snapshot.getDocuments()
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
                                        readValue != null
                                                && readValue;


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
                                    messageAdapter
                                            .getItemCount()
                                            > 0
                            ) {

                                recyclerMessages.scrollToPosition(
                                        messageAdapter
                                                .getItemCount()
                                                - 1
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
                etMessage
                        .getText()
                        .toString()
                        .trim();

        if (text.isEmpty()) {

            return;
        }

        if (doctorId == null) {

            Toast.makeText(
                    requireContext(),
                    "Doctor account not found",
                    Toast.LENGTH_SHORT
            ).show();

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
                        e -> {

                            Toast.makeText(
                                    requireContext(),
                                    "Failed to send message: "
                                            + e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
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
                DocumentSnapshot document
                : snapshot.getDocuments()
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

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        recyclerMessages.setAdapter(null);

    }
}