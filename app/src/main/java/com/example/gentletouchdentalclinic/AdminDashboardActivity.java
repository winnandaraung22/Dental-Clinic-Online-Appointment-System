package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

public class AdminDashboardActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private SharedPreferences preferences;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView txtAppointmentBadge;
    private TextView txtMessageBadge;
    private ListenerRegistration messageUnreadListener;
    private static final String PREF_NAME = "appointmentBadgePreference";
    private static final String KEY_CANCELLATION_VIEWED_AT = "cancellationViewedAt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admindashboard);

        auth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("loginPreference", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.topAppBar);

        MenuItem appointmentItem = navigationView.getMenu().findItem(R.id.nav_appointment);

        if (appointmentItem != null) {

            View actionView = appointmentItem.getActionView();

            if (actionView != null) {

                txtAppointmentBadge = actionView.findViewById(R.id.txtAppointmentBadge);
            }
        }

        if (txtAppointmentBadge != null) {
            txtAppointmentBadge.setVisibility(View.GONE);
        }

        listenForNewAppointments();

        MenuItem messageItem = navigationView.getMenu().findItem(R.id.nav_chat);
        if (messageItem != null) {

            View messageActionView = messageItem.getActionView();

            if (messageActionView != null) {

                txtMessageBadge = messageActionView.findViewById(R.id.txtMessageBadge);
            }
        }

        if (txtMessageBadge != null) {

            txtMessageBadge.setVisibility(View.GONE);
        }

        listenForUnreadAdminMessages();


        if (savedInstanceState == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, new AdminDashboardFragment())
                    .commit();
        }

        navigationView.setCheckedItem(R.id.nav_dashboard);

        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {

            navigationView.setCheckedItem(item.getItemId());
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, new AdminDashboardFragment())
                        .commit();

            } else if (id == R.id.nav_doctors) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, new ManageDoctorsFragment())
                        .commit();

            } else if (id == R.id.nav_schedule) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, new ManageSchedulesFragment())
                        .commit();

            } else if (id == R.id.nav_appointment) {

                markAppointmentsAsViewed();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, new AdminAppointmentsFragment())
                        .commit();

            } else if (id == R.id.nav_chat) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.adminFragmentContainer,
                                new AdminChatFragment()
                        )
                        .commit();

            } else if (id == R.id.nav_patients) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, new AdminPatientsFragment())
                        .commit();

            } else if (id == R.id.nav_services) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, new ManageServicesFragment())
                        .commit();

            } else if (id == R.id.nav_staff) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, new StaffManagementFragment())
                        .commit();

            } else if (id == R.id.nav_profile) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.adminFragmentContainer, new AdminProfileFragment())
                        .commit();

            } else if (id == R.id.nav_logout) {

                logout();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void logout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    auth.signOut();
                    preferences.edit().clear().apply();

                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void listenForNewAppointments() {

        db.collection("appointments")
                .addSnapshotListener((querySnapshot, error) -> {

                    if (error != null || querySnapshot == null) return;

                    int pendingCount = 0;
                    int newCancellationCount = 0;

                    SharedPreferences badgePreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

                    long lastCancellationViewedAt = badgePreferences.getLong(KEY_CANCELLATION_VIEWED_AT, 0);

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        String status = document.getString("status");
                        if (status == null) continue;

                        if (status.equalsIgnoreCase("Pending")) {

                            pendingCount++;

                        } else if (status.equalsIgnoreCase("Cancelled by Patient")) {

                            Timestamp cancelledAt = document.getTimestamp("cancelledAt");

                            if (cancelledAt != null) {

                                long cancelledTime = cancelledAt.toDate().getTime();

                                if (cancelledTime > lastCancellationViewedAt) {

                                    newCancellationCount++;
                                }
                            }
                        }
                    }

                    int totalBadgeCount = pendingCount + newCancellationCount;

                    if (txtAppointmentBadge != null) {

                        if (totalBadgeCount > 0) {

                            txtAppointmentBadge.setText(totalBadgeCount > 99 ? "99+" : String.valueOf(totalBadgeCount));
                            txtAppointmentBadge.setVisibility(View.VISIBLE);

                        } else {

                            txtAppointmentBadge.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void listenForUnreadAdminMessages() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            hideMessageBadge();
            return;
        }

        String adminId = user.getUid();

        if (messageUnreadListener != null) {
            messageUnreadListener.remove();
            messageUnreadListener = null;
        }

        messageUnreadListener = db.collectionGroup("messages")
                .whereEqualTo("receiverId", adminId)
                .whereEqualTo("isRead", false)
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {
                        hideMessageBadge();
                        return;
                    }

                    if (snapshot == null) {
                        hideMessageBadge();
                        return;
                    }

                    int unreadCount = snapshot.size();

                    if (unreadCount <= 0) {

                        hideMessageBadge();

                        return;
                    }

                    if (txtMessageBadge != null) {

                        txtMessageBadge.setText(
                                unreadCount > 99
                                        ? "99+"
                                        : String.valueOf(unreadCount)
                        );

                        txtMessageBadge.setVisibility(
                                View.VISIBLE
                        );
                    }
                });
    }

    private void markAppointmentsAsViewed() {

        if (txtAppointmentBadge != null) {

            txtAppointmentBadge.setText("0");
            txtAppointmentBadge.setVisibility(View.GONE);

        }
    }

    private void hideMessageBadge() {

        if (txtMessageBadge != null) {

            txtMessageBadge.setText("");
            txtMessageBadge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {

        if (messageUnreadListener != null) {

            messageUnreadListener.remove();
            messageUnreadListener = null;
        }
        super.onDestroy();
    }
}