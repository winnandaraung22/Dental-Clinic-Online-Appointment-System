package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.graphics.Color;
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

import com.google.android.material.card.MaterialCardView;
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

public class PatientNotificationsFragment extends Fragment {
    private LinearLayout layoutPatientNotifications;
    private TextView txtNoNotifications;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration notificationsListener;

    public PatientNotificationsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_patient_notifications,
                container,
                false
        );

        layoutPatientNotifications =
                view.findViewById(
                        R.id.layoutPatientNotifications
                );

        txtNoNotifications =
                view.findViewById(
                        R.id.txtNoNotifications
                );

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        listenToPatientNotifications();


        return view;
    }

    private void listenToPatientNotifications() {

        FirebaseUser user =
                auth.getCurrentUser();


        if (user == null) {

            showEmptyMessage(
                    "Please log in again."
            );

            return;
        }


        String patientId =
                user.getUid();


        notificationsListener =
                db.collection("notifications")

                        .whereEqualTo(
                                "userId",
                                patientId
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


                                    layoutPatientNotifications
                                            .removeAllViews();


                                    if (querySnapshot.isEmpty()) {

                                        showEmptyMessage(
                                                "No notifications yet."
                                        );

                                        return;
                                    }

                                    txtNoNotifications
                                            .setVisibility(
                                                    View.GONE
                                            );


                                    for (DocumentSnapshot document :
                                            querySnapshot.getDocuments()) {

                                        addNotificationCard(document);
                                    }


                                }
                        );
    }

    private void addNotificationCard(
            DocumentSnapshot document) {


        MaterialCardView card =
                (MaterialCardView) LayoutInflater.from(
                        requireContext()
                ).inflate(
                        R.layout.item_patient_notification,
                        layoutPatientNotifications,
                        false
                );

        TextView txtTitle =
                card.findViewById(
                        R.id.txtNotificationItemTitle
                );


        TextView txtMessage =
                card.findViewById(
                        R.id.txtNotificationItemMessage
                );


        TextView txtDate =
                card.findViewById(
                        R.id.txtNotificationItemDate
                );

        View viewNotificationUnread =
                card.findViewById(
                        R.id.viewNotificationUnread
                );

        String title =
                document.getString(
                        "title"
                );


        String message =
                document.getString("patientMessage");

        if (message == null ||
                message.trim().isEmpty()) {

            message =
                    document.getString("message");
        }

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

            card.setCardBackgroundColor(
                    Color.parseColor("#EFF6FF")
            );

            card.setStrokeColor(
                    Color.parseColor("#BFDBFE")
            );

            txtTitle.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            viewNotificationUnread.setVisibility(
                    View.VISIBLE
            );

        } else {

            card.setAlpha(1.0f);

            card.setCardBackgroundColor(
                    Color.WHITE
            );

            card.setStrokeColor(
                    Color.parseColor("#E2E8F0")
            );

            txtTitle.setTypeface(
                    null,
                    android.graphics.Typeface.NORMAL
            );

            viewNotificationUnread.setVisibility(
                    View.GONE
            );
        }

        card.setOnClickListener(v -> {

            if (Boolean.FALSE.equals(isRead)) {

                markNotificationAsRead(
                        document.getId()
                );
            }

            String appointmentId =
                    document.getString("appointmentId");

            if (appointmentId != null &&
                    !appointmentId.trim().isEmpty()) {

                Intent intent =
                        new Intent(
                                requireContext(),
                                PatientAppointmentDetailActivity.class
                        );

                intent.putExtra(
                        "appointmentId",
                        appointmentId
                );

                startActivity(intent);

            } else {

                Toast.makeText(
                        requireContext(),
                        "Appointment information is unavailable.",
                        Toast.LENGTH_SHORT
                ).show();
            }

        });

        layoutPatientNotifications
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
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            requireContext(),
                            "Unable to update notification",
                            Toast.LENGTH_SHORT
                    ).show();

                });
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

        layoutPatientNotifications
                .removeAllViews();


        txtNoNotifications
                .setText(message);


        txtNoNotifications
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
    public void onDestroyView() {

        super.onDestroyView();


        if (notificationsListener != null) {

            notificationsListener.remove();

            notificationsListener = null;
        }
    }
}