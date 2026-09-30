package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class DoctorDashboardActivity extends AppCompatActivity {

    LinearLayout navDoctorHome;
    LinearLayout navDoctorAppointment;
    LinearLayout navDoctorMessage;
    LinearLayout navDoctorProfile;

    ImageView imgDoctorHome;
    ImageView imgDoctorAppointment;
    ImageView imgDoctorMessage;
    ImageView imgDoctorProfile;

    TextView txtDoctorHome;
    TextView txtDoctorAppointment;
    TextView txtDoctorMessage;
    TextView txtDoctorProfile;

    TextView txtDoctorName;
    FirebaseAuth auth;
    FirebaseFirestore db;
    MaterialCardView doctorBottomNavigation;
    private ImageView btnDoctorNotification;
    private TextView txtDoctorNotificationBadge;
    private ListenerRegistration notificationListener;
    private TextView txtDoctorMessageBadge;
    private ListenerRegistration messageUnreadListener;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doctor_dashboard);

        txtDoctorName = findViewById(R.id.txtDoctorName);
        auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        loadDoctorProfile();

        navDoctorHome = findViewById(R.id.navDoctorHome);
        navDoctorAppointment = findViewById(R.id.navDoctorAppointment);
        navDoctorMessage = findViewById(R.id.navDoctorMessage);
        navDoctorProfile = findViewById(R.id.navDoctorProfile);

        imgDoctorHome = findViewById(R.id.imgDoctorHome);
        imgDoctorAppointment = findViewById(R.id.imgDoctorAppointment);
        imgDoctorMessage = findViewById(R.id.imgDoctorMessage);
        imgDoctorProfile = findViewById(R.id.imgDoctorProfile);

        txtDoctorHome = findViewById(R.id.txtDoctorHome);
        txtDoctorAppointment = findViewById(R.id.txtDoctorAppointment);
        txtDoctorMessage = findViewById(R.id.txtDoctorMessage);
        txtDoctorProfile = findViewById(R.id.txtDoctorProfile);
        doctorBottomNavigation = findViewById(R.id.doctorBottomNavigation);

        btnDoctorNotification = findViewById(R.id.btnDoctorNotification);
        txtDoctorNotificationBadge = findViewById(R.id.txtDoctorNotificationBadge);

        txtDoctorNotificationBadge.setVisibility(View.GONE);

        txtDoctorMessageBadge = findViewById(R.id.txtDoctorMessageBadge);

        txtDoctorMessageBadge.setVisibility(View.GONE);

        listenForUnreadMessages();

        if(savedInstanceState == null){

            loadFragment(new DoctorHomeFragment());

        }

        selectNavigation(
                navDoctorHome,
                imgDoctorHome,
                txtDoctorHome
        );

        navDoctorHome.setOnClickListener(v -> {

            currentTab = 0;

            loadFragment(
                    new DoctorHomeFragment()
            );

            selectNavigation(
                    navDoctorHome,
                    imgDoctorHome,
                    txtDoctorHome
            );

        });

        navDoctorAppointment.setOnClickListener(v -> {

            currentTab = 1;

            loadFragment(
                    new DoctorAppointmentFragment()
            );

            selectNavigation(
                    navDoctorAppointment,
                    imgDoctorAppointment,
                    txtDoctorAppointment
            );

        });

        navDoctorMessage.setOnClickListener(v -> {

            currentTab = 2;

            hideDoctorMessageBadge();

            markDoctorMessagesAsRead(() -> {

                loadFragment(
                        new DoctorMessageFragment()
                );

                selectNavigation(
                        navDoctorMessage,
                        imgDoctorMessage,
                        txtDoctorMessage
                );

            });

        });

        navDoctorProfile.setOnClickListener(v -> {

            currentTab = 3;

            loadFragment(
                    new DoctorProfileFragment()
            );

            selectNavigation(
                    navDoctorProfile,
                    imgDoctorProfile,
                    txtDoctorProfile
            );

        });

        btnDoctorNotification.setOnClickListener(v -> {

            markDoctorNotificationsAsRead();

            Intent intent =
                    new Intent(
                            DoctorDashboardActivity.this,
                            DoctorNotificationsActivity.class
                    );

            startActivity(intent);
        });

    }
    private void markDoctorMessagesAsRead(Runnable onComplete) {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            if (onComplete != null) {
                onComplete.run();
            }

            return;
        }

        String doctorId = user.getUid();

        db.collectionGroup("messages")
                .whereEqualTo(
                        "receiverId",
                        doctorId
                )
                .whereEqualTo(
                        "isRead",
                        false
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        hideDoctorMessageBadge();

                        if (onComplete != null) {
                            onComplete.run();
                        }

                        return;
                    }

                    com.google.firebase.firestore.WriteBatch batch = db.batch();

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

                                hideDoctorMessageBadge();

                                if (onComplete != null) {
                                    onComplete.run();
                                }

                            })
                            .addOnFailureListener(e -> {

                                if (onComplete != null) {
                                    onComplete.run();
                                }

                            });

                })
                .addOnFailureListener(e -> {

                    if (onComplete != null) {
                        onComplete.run();
                    }

                });
    }
    private void loadDoctorProfile(){

        FirebaseUser user = auth.getCurrentUser();

        if(user == null){
            return;
        }

        String uid = user.getUid();

        db.collection("doctors")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if(document.exists()){

                        String doctorName =
                                document.getString("fullName");

                        if(doctorName != null){

                            txtDoctorName.setText(
                                    doctorName
                            );

                        }

                    }


                });

    }
    private void loadFragment(Fragment fragment){


        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.doctorFragmentContainer,
                        fragment
                )
                .commit();


    }
    private void selectNavigation(
            LinearLayout selectedItem,
            ImageView selectedIcon,
            TextView selectedText
    ){

        txtDoctorHome.setVisibility(View.GONE);

        txtDoctorAppointment.setVisibility(View.GONE);

        txtDoctorMessage.setVisibility(View.GONE);

        txtDoctorProfile.setVisibility(View.GONE);

        navDoctorHome.setBackgroundResource(0);

        navDoctorAppointment.setBackgroundResource(0);

        navDoctorMessage.setBackgroundResource(0);

        navDoctorProfile.setBackgroundResource(0);

        imgDoctorHome.setColorFilter(Color.BLACK);

        imgDoctorAppointment.setColorFilter(Color.BLACK);

        imgDoctorMessage.setColorFilter(Color.BLACK);

        imgDoctorProfile.setColorFilter(Color.BLACK);

        selectedItem.setBackgroundResource(
                R.drawable.nav_selected_background
        );

        selectedText.setVisibility(View.VISIBLE);

        selectedIcon.setColorFilter(
                Color.WHITE
        );

    }
    private void listenForDoctorNotifications() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            return;
        }

        String doctorId = user.getUid();

        if (notificationListener != null) {
            notificationListener.remove();
        }

        notificationListener = db.collection("notifications")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("doctorIsRead", false)
                .addSnapshotListener((querySnapshot, error) -> {

                    if (error != null || querySnapshot == null) {
                        return;
                    }

                    int unreadCount = querySnapshot.size();

                    if (unreadCount > 0) {

                        txtDoctorNotificationBadge.setText(
                                String.valueOf(unreadCount)
                        );

                        txtDoctorNotificationBadge.setVisibility(
                                View.VISIBLE
                        );

                    } else {

                        txtDoctorNotificationBadge.setVisibility(
                                View.GONE
                        );
                    }
                });
    }
    private void listenForUnreadMessages() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {
            return;
        }

        String doctorId =
                user.getUid();

        if (messageUnreadListener != null) {

            messageUnreadListener.remove();
            messageUnreadListener = null;
        }

        messageUnreadListener =
                db.collectionGroup("messages")
                        .whereEqualTo(
                                "receiverId",
                                doctorId
                        )
                        .whereEqualTo(
                                "isRead",
                                false
                        )
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        return;
                                    }

                                    if (snapshot == null) {

                                        return;
                                    }

                                    int unreadCount =
                                            snapshot.size();

                                    if (unreadCount == 0) {

                                        hideDoctorMessageBadge();

                                    } else {

                                        txtDoctorMessageBadge
                                                .setText(
                                                        unreadCount > 99
                                                                ? "99+"
                                                                : String.valueOf(
                                                                unreadCount
                                                        )
                                                );

                                        txtDoctorMessageBadge
                                                .setVisibility(
                                                        View.VISIBLE
                                                );
                                    }
                                });
    }

    private void markDoctorNotificationsAsRead() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {
            return;
        }

        String doctorId =
                user.getUid();

        db.collection("notifications")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("doctorIsRead", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        document.getReference()
                                .update("doctorIsRead", true);
                    }

                    if (txtDoctorNotificationBadge != null) {

                        txtDoctorNotificationBadge
                                .setVisibility(View.GONE);
                    }
                });
    }
    public void hideDoctorBottomNavigation(boolean hide){

        if(hide){
            doctorBottomNavigation.setVisibility(View.GONE);
        }
        else{
            doctorBottomNavigation.setVisibility(View.VISIBLE);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

        listenForDoctorNotifications();


    }
    @Override
    protected void onDestroy() {

        if (notificationListener != null) {
            notificationListener.remove();
            notificationListener = null;
        }

        if (messageUnreadListener != null) {
            messageUnreadListener.remove();
            messageUnreadListener = null;
        }

        super.onDestroy();
    }
    public void hideDoctorMessageBadge() {

        if (txtDoctorMessageBadge != null) {

            txtDoctorMessageBadge.setText("");

            txtDoctorMessageBadge.setVisibility(
                    View.GONE
            );
        }
    }

}