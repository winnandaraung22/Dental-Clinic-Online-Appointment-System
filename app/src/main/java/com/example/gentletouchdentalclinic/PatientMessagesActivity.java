package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PatientMessagesActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RecyclerView recyclerMessages;
    private LinearLayout layoutEmptyMessages;
    private MessageDoctorAdapter adapter;
    private final List<DoctorMessageItem> doctorList =
            new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration appointmentListener;
    private final List<ListenerRegistration> messageListeners =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_patient_messages
        );

        auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(
                R.id.btnBack
        );

        recyclerMessages = findViewById(
                R.id.recyclerMessages
        );

        layoutEmptyMessages = findViewById(
                R.id.layoutEmptyMessages
        );

        recyclerMessages.setLayoutManager(
                new LinearLayoutManager(
                        PatientMessagesActivity.this
                )
        );

        adapter = new MessageDoctorAdapter(
                doctorList,
                doctor -> {

                    Intent intent =
                            new Intent(
                                    PatientMessagesActivity.this,
                                    PatientChatActivity.class
                            );

                    intent.putExtra(
                            "doctorId",
                            doctor.getDoctorId()
                    );

                    intent.putExtra(
                            "doctorName",
                            doctor.getDoctorName()
                    );

                    startActivity(intent);
                }
        );

        recyclerMessages.setAdapter(
                adapter
        );

        btnBack.setOnClickListener(v -> {
            finish();
        });

        loadConfirmedDoctors();
    }
    private String createChatId(
            String patientId,
            String doctorId) {

        if (patientId.compareTo(doctorId) < 0) {

            return patientId + "_" + doctorId;

        } else {

            return doctorId + "_" + patientId;
        }
    }

    private void loadConfirmedDoctors() {

        FirebaseUser currentUser =
                auth.getCurrentUser();

        if (currentUser == null) {

            showEmptyMessages();

            return;
        }
        if (appointmentListener != null) {
            appointmentListener.remove();
            appointmentListener = null;
        }

        String patientId =
                currentUser.getUid();

        doctorList.clear();

        adapter.notifyDataSetChanged();

        appointmentListener =
                db.collection("appointments")

                        .whereEqualTo(
                                "patientId",
                                patientId
                        )

                        .whereEqualTo(
                                "status",
                                "Confirmed"
                        )

                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        Toast.makeText(
                                                PatientMessagesActivity.this,
                                                "Unable to load messages",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        return;
                                    }

                                    if (snapshot == null) {

                                        showEmptyMessages();

                                        return;
                                    }

                                    doctorList.clear();

                                    Set<String> doctorIds =
                                            new HashSet<>();

                                    for (
                                            DocumentSnapshot document
                                            : snapshot.getDocuments()
                                    ) {

                                        String doctorId =
                                                document.getString(
                                                        "doctorId"
                                                );

                                        if (
                                                doctorId == null ||
                                                        doctorId.isEmpty()
                                        ) {
                                            continue;
                                        }

                                        if (
                                                doctorIds.contains(
                                                        doctorId
                                                )
                                        ) {
                                            continue;
                                        }

                                        doctorIds.add(
                                                doctorId
                                        );

                                        loadDoctor(
                                                doctorId
                                        );
                                    }

                                    if (doctorIds.isEmpty()) {

                                        showEmptyMessages();

                                    }

                                }
                        );
    }

    private void loadDoctor(String doctorId) {

        FirebaseUser currentUser =
                auth.getCurrentUser();


        if (currentUser == null) {
            return;
        }

        String patientId =
                currentUser.getUid();

        String chatId =
                createChatId(
                        patientId,
                        doctorId
                );

        db.collection("doctors")
                .document(doctorId)
                .get()
                .addOnSuccessListener(doctorDocument -> {

                    if (!doctorDocument.exists()) {
                        return;
                    }

                    String doctorName =
                            doctorDocument.getString(
                                    "fullName"
                            );

                    if (doctorName == null
                            || doctorName.isEmpty()) {

                        doctorName = "Doctor";

                    } else if (!doctorName.startsWith("Dr.")) {

                        doctorName =
                                "Dr. " + doctorName;
                    }

                    String profileImage =
                            doctorDocument.getString(
                                    "profileImage"
                            );

                    final String finalDoctorName =
                            doctorName;

                    ListenerRegistration messageListener =
                            db.collection("chats")
                                    .document(chatId)
                                    .collection("messages")
                                    .orderBy(
                                            "timestamp",
                                            Query.Direction.DESCENDING
                                    )
                                    .limit(1)
                                    .addSnapshotListener(
                                            (messageSnapshot, error) -> {

                                        String lastMessage =
                                                "Tap to start conversation";

                                        String messageTime =
                                                "";

                                                Timestamp lastMessageTimestamp =
                                                        null;

                                        boolean unread = false;

                                        if (messageSnapshot != null
                                                && !messageSnapshot.isEmpty()) {

                                            DocumentSnapshot messageDocument =
                                                    messageSnapshot
                                                            .getDocuments()
                                                            .get(0);

                                            String message =
                                                    messageDocument.getString(
                                                            "message"
                                                    );

                                            if (message != null
                                                    && !message.isEmpty()) {

                                                lastMessage =
                                                        message;
                                            }

                                            Timestamp timestamp =
                                                    messageDocument
                                                            .getTimestamp(
                                                                    "timestamp"
                                                            );
                                            lastMessageTimestamp =
                                                    timestamp;

                                            if (timestamp != null) {

                                                Date date =
                                                        timestamp.toDate();

                                                SimpleDateFormat formatter =
                                                        new SimpleDateFormat(
                                                                "h:mm a",
                                                                Locale.getDefault()
                                                        );

                                                messageTime =
                                                        formatter.format(
                                                                date
                                                        );
                                            }

                                            String senderId =
                                                    messageDocument.getString(
                                                            "senderId"
                                                    );

                                            Boolean isRead =
                                                    messageDocument.getBoolean(
                                                            "isRead"
                                                    );

                                            unread =
                                                    senderId != null
                                                            && senderId.equals(
                                                            doctorId
                                                    )
                                                            && Boolean.FALSE.equals(
                                                            isRead
                                                    );
                                        }
                                        DoctorMessageItem item =
                                                new DoctorMessageItem(
                                                                doctorId,
                                                                finalDoctorName,
                                                                lastMessage,
                                                                profileImage,
                                                                messageTime,
                                                                lastMessageTimestamp,
                                                                unread
                                                        );

                                        removeExistingDoctor(
                                                doctorId
                                        );

                                        doctorList.add(item);

                                                Collections.sort(
                                                        doctorList,
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

                                        showDoctorList();

                                    }
                            );
                            messageListeners.add(messageListener);

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PatientMessagesActivity.this,
                            "Unable to load doctor information",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
    private void removeExistingDoctor(
            String doctorId) {

        for (int i = doctorList.size() - 1;
             i >= 0;
             i--) {

            if (doctorList
                    .get(i)
                    .getDoctorId()
                    .equals(doctorId)) {

                doctorList.remove(i);
            }
        }
    }

    private void showEmptyMessages() {

        layoutEmptyMessages.setVisibility(
                View.VISIBLE
        );

        recyclerMessages.setVisibility(
                View.GONE
        );
    }


    private void showDoctorList() {

        if (doctorList.isEmpty()) {

            showEmptyMessages();

            return;
        }


        layoutEmptyMessages.setVisibility(
                View.GONE
        );

        recyclerMessages.setVisibility(
                View.VISIBLE
        );
    }


    @Override
    protected void onDestroy() {

        if (appointmentListener != null) {

            appointmentListener.remove();
            appointmentListener = null;
        }

        for (ListenerRegistration listener :
                messageListeners) {

            if (listener != null) {
                listener.remove();
            }
        }

        messageListeners.clear();

        super.onDestroy();
    }
}