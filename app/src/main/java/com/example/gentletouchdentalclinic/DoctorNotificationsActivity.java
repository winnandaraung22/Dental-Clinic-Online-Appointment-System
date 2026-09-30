package com.example.gentletouchdentalclinic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DoctorNotificationsActivity extends AppCompatActivity {

    private MaterialToolbar toolbarDoctorNotifications;
    private LinearLayout layoutDoctorNotifications;
    private TextView txtNoDoctorNotifications;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration notificationsListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_doctor_notifications
        );

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        toolbarDoctorNotifications =
                findViewById(
                        R.id.toolbarDoctorNotifications
                );

        layoutDoctorNotifications =
                findViewById(
                        R.id.layoutDoctorNotifications
                );

        txtNoDoctorNotifications =
                findViewById(
                        R.id.txtNoDoctorNotifications
                );

        toolbarDoctorNotifications.setNavigationOnClickListener(
                v -> finish()
        );

        listenToDoctorNotifications();
    }

    private void listenToDoctorNotifications() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            showEmptyMessage(
                    "Please log in again."
            );

            return;
        }

        String doctorId =
                user.getUid();

        notificationsListener =
                db.collection("notifications")
                        .whereEqualTo(
                                "doctorId",
                                doctorId
                        )
                        .orderBy(
                                "createdAt",
                                Query.Direction.DESCENDING
                        )
                        .addSnapshotListener(
                                (querySnapshot, error) -> {

                                    if (error != null) {

                                        return;
                                    }

                                    if (querySnapshot == null) {
                                        return;
                                    }

                                    layoutDoctorNotifications
                                            .removeViews(
                                                    1,
                                                    Math.max(
                                                            0,
                                                            layoutDoctorNotifications
                                                                    .getChildCount() - 1
                                                    )
                                            );

                                    if (querySnapshot.isEmpty()) {

                                        showEmptyMessage(
                                                "No notifications yet."
                                        );

                                        return;
                                    }

                                    txtNoDoctorNotifications
                                            .setVisibility(
                                                    View.GONE
                                            );

                                    for (
                                            DocumentSnapshot document :
                                            querySnapshot.getDocuments()
                                    ) {

                                        addNotificationCard(
                                                document
                                        );
                                    }
                                }
                        );
    }

    private void addNotificationCard(
            DocumentSnapshot document) {

        View card =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.item_doctor_notification,
                                layoutDoctorNotifications,
                                false
                        );

        TextView txtTitle =
                card.findViewById(
                        R.id.txtDoctorNotificationTitle
                );

        TextView txtMessage =
                card.findViewById(
                        R.id.txtDoctorNotificationMessage
                );

        TextView txtDate =
                card.findViewById(
                        R.id.txtDoctorNotificationItemDate
                );

        String title =
                document.getString(
                        "title"
                );

        String message =
                document.getString(
                        "doctorMessage"
                );

        Timestamp createdAt =
                document.getTimestamp(
                        "createdAt"
                );

        Boolean isRead =
                document.getBoolean(
                        "isRead"
                );

        txtTitle.setText(
                safeText(
                        title,
                        "Notification"
                )
        );

        txtMessage.setText(
                safeText(
                        message,
                        "No message available."
                )
        );

        if (createdAt != null) {

            txtDate.setText(
                    formatNotificationTime(
                            createdAt.toDate()
                    )
            );

        } else {

            txtDate.setText(
                    "Date unavailable"
            );
        }

        if (Boolean.FALSE.equals(isRead)) {

            card.setAlpha(1.0f);

        } else {

            card.setAlpha(0.75f);
        }

        card.setOnClickListener(
                v -> markNotificationAsRead(
                        document.getId()
                )
        );

        layoutDoctorNotifications
                .addView(card);
    }

    private void markNotificationAsRead(
            String notificationId) {

        db.collection("notifications")
                .document(notificationId)
                .update(
                        "isRead",
                        true
                )
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Unable to update notification",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private String formatNotificationTime(
            Date date) {

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        Locale.ENGLISH
                );

        return sdf.format(date);
    }

    private void showEmptyMessage(
            String message) {

        txtNoDoctorNotifications
                .setText(message);

        txtNoDoctorNotifications
                .setVisibility(
                        View.VISIBLE
                );
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

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (notificationsListener != null) {

            notificationsListener.remove();

            notificationsListener = null;
        }
    }
}