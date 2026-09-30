package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoctorMessageFragment extends Fragment {
    private static final String CLINIC_ADMIN_UID =
            "UACW4aAjCBX8GTdZ0R3b2xbWqgi2";
    public static final String TYPE_PATIENT = "patient";
    public static final String TYPE_ADMIN = "admin";
    private RecyclerView recyclerDoctorMessages;
    private LinearLayout layoutDoctorEmptyMessages;
    private MessagePatientAdapter adapter;
    private final List<PatientMessageItem> patientList =
            new ArrayList<>();
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ListenerRegistration chatsListener;
    private final List<ListenerRegistration> messageListeners =
            new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_doctor_message,
                container,
                false
        );

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerDoctorMessages =
                view.findViewById(
                        R.id.recyclerDoctorMessages
                );

        layoutDoctorEmptyMessages =
                view.findViewById(
                        R.id.layoutDoctorEmptyMessages
                );

        recyclerDoctorMessages.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        adapter = new MessagePatientAdapter(
                patientList,
                patient -> {

                    if (auth.getCurrentUser() == null) {
                        return;
                    }

                    if (TYPE_ADMIN.equals(
                            patient.getMessageType())) {

                        openAdminDoctorChat();

                    } else {

                        openPatientDoctorChat(patient);
                    }
                }
        );

        recyclerDoctorMessages.setAdapter(adapter);

        loadDoctorMessages();

        return view;
    }
    private void openAdminDoctorChat() {

        Intent intent =
                new Intent(
                        requireContext(),
                        DoctorAdminChatActivity.class
                );

        intent.putExtra(
                "adminId",
                CLINIC_ADMIN_UID
        );

        intent.putExtra(
                "adminName",
                "GentleTouch Dental Clinic"
        );

        startActivity(intent);
    }
    private void openPatientDoctorChat(
            PatientMessageItem patient) {

        Intent intent =
                new Intent(
                        requireContext(),
                        DoctorChatActivity.class
                );

        intent.putExtra(
                "patientId",
                patient.getPatientId()
        );

        intent.putExtra(
                "patientName",
                patient.getPatientName()
        );

        startActivity(intent);
    }

    private void loadDoctorMessages() {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            showNoMessages();
            return;
        }

        String doctorId = currentUser.getUid();

        chatsListener = db.collection("chats")
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {

                        Toast.makeText(
                                requireContext(),
                                "Unable to load messages: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                        showNoMessages();
                        return;
                    }

                    if (snapshot == null || snapshot.isEmpty()) {

                        showNoMessages();
                        return;
                    }

                    patientList.clear();

                    boolean[] foundChat = {false};

                    for (DocumentSnapshot chatDocument
                            : snapshot.getDocuments()) {

                        String chatId =
                                chatDocument.getId();

                        String adminChatId =
                                ChatUtils.getAdminDoctorChatId(
                                        CLINIC_ADMIN_UID,
                                        doctorId
                                );

                        if (chatId.equals(adminChatId)) {

                            foundChat[0] = true;

                            loadLatestAdminMessage(
                                    chatId,
                                    doctorId
                            );

                            continue;
                        }

                        String patientId =
                                getPatientIdFromChatId(
                                        chatId,
                                        doctorId
                                );

                        if (patientId == null) {
                            continue;
                        }

                        foundChat[0] = true;

                        loadLatestMessage(
                                chatId,
                                patientId,
                                doctorId
                        );
                    }

                    if (!foundChat[0]) {
                        showNoMessages();
                    }
                });
    }
    private void loadLatestAdminMessage(
            String chatId,
            String doctorId) {

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy(
                        "timestamp",
                        com.google.firebase.firestore.Query.Direction.DESCENDING
                )
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
                        return;
                    }

                    DocumentSnapshot messageDocument =
                            querySnapshot
                                    .getDocuments()
                                    .get(0);

                    String lastMessage =
                            messageDocument.getString(
                                    "message"
                            );

                    Timestamp timestamp =
                            messageDocument.getTimestamp(
                                    "timestamp"
                            );

                    String messageTime = "";

                    if (timestamp != null) {

                        Date date =
                                timestamp.toDate();

                        SimpleDateFormat formatter =
                                new SimpleDateFormat(
                                        "h:mm a",
                                        Locale.getDefault()
                                );

                        messageTime =
                                formatter.format(date);
                    }

                    Boolean isRead =
                            messageDocument.getBoolean(
                                    "isRead"
                            );

                    String senderId =
                            messageDocument.getString(
                                    "senderId"
                            );

                    boolean unread =
                            CLINIC_ADMIN_UID.equals(
                                    senderId
                            )
                                    && Boolean.FALSE.equals(
                                    isRead
                            );

                    PatientMessageItem item =
                            new PatientMessageItem(
                                    CLINIC_ADMIN_UID,
                                    "GentleTouch Dental Clinic",
                                    lastMessage != null
                                            ? lastMessage
                                            : "No messages",
                                    "",
                                    messageTime,
                                    timestamp,
                                    unread
                            );

                    item.setMessageType(
                            TYPE_ADMIN
                    );

                    removeExistingPatient(
                            CLINIC_ADMIN_UID
                    );

                    patientList.add(item);

                    Collections.sort(
                            patientList,
                            (item1, item2) -> {

                                Timestamp time1 =
                                        item1.getLastMessageTimestamp();

                                Timestamp time2 =
                                        item2.getLastMessageTimestamp();

                                if (time1 == null &&
                                        time2 == null) {
                                    return 0;
                                }

                                if (time1 == null) {
                                    return 1;
                                }

                                if (time2 == null) {
                                    return -1;
                                }

                                return time2.compareTo(time1);
                            }
                    );

                    adapter.notifyDataSetChanged();

                    showPatientList();
                });
    }
    private String getPatientIdFromChatId(
            String chatId,
            String doctorId) {

        if (chatId == null || chatId.isEmpty()) {
            return null;
        }

        String[] parts =
                chatId.split("_");

        if (parts.length != 2) {
            return null;
        }

        String firstId = parts[0];
        String secondId = parts[1];

        if (firstId.equals(doctorId)) {
            return secondId;
        }

        if (secondId.equals(doctorId)) {
            return firstId;
        }

        return null;
    }

    private void loadLatestMessage(
            String chatId,
            String patientId,
            String doctorId) {

        ListenerRegistration messageListener =
                db.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .orderBy(
                                "timestamp",
                                com.google.firebase.firestore.Query.Direction.DESCENDING
                        )
                        .limit(1)
                        .addSnapshotListener((querySnapshot, error) -> {

                            if (error != null || querySnapshot == null) {
                                return;
                            }

                            if (querySnapshot.isEmpty()) {
                                return;
                            }

                            DocumentSnapshot messageDocument =
                                    querySnapshot.getDocuments().get(0);

                            String lastMessage =
                                    messageDocument.getString("message");

                            String senderId =
                                    messageDocument.getString("senderId");

                            Boolean isRead =
                                    messageDocument.getBoolean("isRead");

                            boolean unread =
                                    senderId != null
                                            && senderId.equals(patientId)
                                            && Boolean.FALSE.equals(isRead);

                            Timestamp timestamp =
                                    messageDocument.getTimestamp("timestamp");

                            String messageTime = "";

                            if (timestamp != null) {

                                Date date = timestamp.toDate();

                                SimpleDateFormat formatter =
                                        new SimpleDateFormat(
                                                "h:mm a",
                                                Locale.getDefault()
                                        );

                                messageTime =
                                        formatter.format(date);
                            }

                            loadPatient(
                                    patientId,
                                    lastMessage,
                                    messageTime,
                                    timestamp,
                                    unread
                            );
                        });
        messageListeners.add(messageListener);
    }

    private void loadPatient(
            String patientId,
            String lastMessage,
            String messageTime,
            Timestamp lastMessageTimestamp,
            boolean unread) {

        db.collection("users")
                .document(patientId)
                .get()
                .addOnSuccessListener(
                        documentSnapshot -> {

                            if (
                                    !documentSnapshot.exists()
                            ) {
                                return;
                            }

                            String patientName =
                                    documentSnapshot.getString(
                                            "fullName"
                                    );

                            if (
                                    patientName == null ||
                                            patientName.isEmpty()
                            ) {

                                patientName =
                                        "Patient";
                            }

                            PatientMessageItem item =
                                    new PatientMessageItem(
                                            patientId,
                                            patientName,
                                            lastMessage != null
                                                    ? lastMessage
                                                    : "No messages",
                                            "",
                                            messageTime,
                                            lastMessageTimestamp,
                                            unread
                                    );

                            item.setMessageType(
                                    TYPE_PATIENT
                            );

                            removeExistingPatient(
                                    patientId
                            );

                            patientList.add(item);

                            Collections.sort(
                                    patientList,
                                    (item1, item2) -> {

                                        Timestamp time1 =
                                                item1.getLastMessageTimestamp();

                                        Timestamp time2 =
                                                item2.getLastMessageTimestamp();

                                        if (time1 == null && time2 == null) {
                                            return 0;
                                        }

                                        if (time1 == null) {
                                            return 1;
                                        }

                                        if (time2 == null) {
                                            return -1;
                                        }

                                        return time2.compareTo(time1);
                                    }
                            );

                            adapter.notifyDataSetChanged();

                            showPatientList();
                        }
                );
    }

    private void removeExistingPatient(
            String patientId) {

        for (int i = patientList.size() - 1;
             i >= 0;
             i--) {

            if (
                    patientList
                            .get(i)
                            .getPatientId()
                            .equals(patientId)
            ) {

                patientList.remove(i);
            }
        }
    }

    private void showNoMessages() {

        if (layoutDoctorEmptyMessages != null) {

            layoutDoctorEmptyMessages.setVisibility(
                    View.VISIBLE
            );
        }

        if (recyclerDoctorMessages != null) {

            recyclerDoctorMessages.setVisibility(
                    View.GONE
            );
        }
    }

    private void showPatientList() {

        if (patientList.isEmpty()) {

            showNoMessages();

            return;
        }

        if (layoutDoctorEmptyMessages != null) {

            layoutDoctorEmptyMessages.setVisibility(
                    View.GONE
            );
        }

        if (recyclerDoctorMessages != null) {

            recyclerDoctorMessages.setVisibility(
                    View.VISIBLE
            );
        }
    }

    @Override
    public void onDestroyView() {

        if (chatsListener != null) {

            chatsListener.remove();
            chatsListener = null;
        }

        for (ListenerRegistration listener :
                messageListeners) {

            if (listener != null) {
                listener.remove();
            }
        }

        messageListeners.clear();

        super.onDestroyView();
    }
}