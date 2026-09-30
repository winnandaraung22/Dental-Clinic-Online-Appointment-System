package com.example.gentletouchdentalclinic;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;


public class PatientDashboardActivity extends AppCompatActivity {
    LinearLayout navHome, navDoctors, navNotification, navMenu;
    ImageView imgHome, imgDoctor, imgNotification, imgMenu;
    TextView txtHome, txtDoctor, txtNotification, txtMenu, txtTitle;
    MaterialCardView menuPopup, bottomNavigationCard;
    LinearLayout menuLogout, menuAppointment, menuProfile;
    View blurOverlay;
    ImageButton btnSearch, btnBackSearch, btnMessage;
    SearchView searchView;
    RelativeLayout normalToolbar, searchToolbar;
    TextView txtNotificationBadge, txtMessageBadge;
    private int currentTab = 0;
    private ListenerRegistration notificationBadgeListener, messageUnreadListener;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private final BroadcastReceiver messageReadReceiver =
            new BroadcastReceiver() {

                @Override
                public void onReceive(
                        Context context,
                        Intent intent) {

                    if (PatientChatActivity.ACTION_MESSAGE_READ
                            .equals(intent.getAction())) {

                        hideMessageBadge();

                        refreshMessageBadge();
                    }
                }
            };

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientdashboard);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.TIRAMISU) {

            if(checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                android.Manifest.permission.POST_NOTIFICATIONS
                        },
                        100
                );
            }
        }

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if(savedInstanceState == null){

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.patientFragmentContainer,
                            new PatientHomeFragment()
                    )
                    .commit();

        }

        navHome = findViewById(R.id.navHome);
        navDoctors = findViewById(R.id.navDoctors);
        navNotification = findViewById(R.id.navNotification);
        navMenu = findViewById(R.id.navMenu);

        imgHome = findViewById(R.id.imgHome);
        imgDoctor = findViewById(R.id.imgDoctor);
        imgNotification = findViewById(R.id.imgNotification);
        imgMenu = findViewById(R.id.imgMenu);

        txtHome = findViewById(R.id.txtHome);
        txtDoctor = findViewById(R.id.txtDoctor);
        txtNotification = findViewById(R.id.txtNotification);
        txtMenu = findViewById(R.id.txtMenu);
        txtTitle = findViewById(R.id.txtTitle);

        menuPopup = findViewById(R.id.menuPopup);
        menuLogout = findViewById(R.id.menuLogout);
        menuAppointment = findViewById(R.id.menuAppointment);
        menuProfile = findViewById(R.id.menuProfile);

        normalToolbar = findViewById(R.id.normalToolbar);
        searchToolbar = findViewById(R.id.searchToolbar);

        searchView = findViewById(R.id.searchView);

        btnSearch = findViewById(R.id.btnSearch);
        btnBackSearch = findViewById(R.id.btnBackSearch);
        btnMessage = findViewById(R.id.btnMessage);
        txtMessageBadge = findViewById(R.id.txtMessageBadge);

        txtMessageBadge.setVisibility(View.GONE);

        bottomNavigationCard = findViewById(R.id.bottomNavigationCard);

        searchToolbar.setVisibility(View.GONE);
        normalToolbar.setVisibility(View.VISIBLE);

        blurOverlay = findViewById(R.id.blurOverlay);
        txtNotificationBadge = findViewById(R.id.txtNotificationBadge);

        listenForUnreadNotifications();
        listenForUnreadMessages();

        if (getIntent().getBooleanExtra(
                "openNotifications",
                false
        )) {

            openNotificationsPage();

        } else {

            selectNavigation(
                    navHome,
                    imgHome,
                    txtHome
            );
        }

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {


                        SearchFragment fragment =
                                (SearchFragment)
                                        getSupportFragmentManager()
                                                .findFragmentByTag("SEARCH");

                        if(fragment != null){

                            fragment.searchData(query);

                        }

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        SearchFragment fragment =
                                (SearchFragment)
                                        getSupportFragmentManager()
                                                .findFragmentByTag("SEARCH");

                        if(fragment != null){

                            fragment.searchData(newText);

                        }

                        return true;
                    }

                });

        navHome.setOnClickListener(v -> {

            currentTab = 0;

            blurOverlay.setVisibility(View.GONE);
            menuPopup.setVisibility(View.GONE);

            selectNavigation(navHome, imgHome, txtHome);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.patientFragmentContainer,
                            new PatientHomeFragment()
                    )
                    .commit();

            setTopBarTitle(
                    "Find Your Specialist"
            );
        });

        navDoctors.setOnClickListener(v -> {
            currentTab = 1;

            blurOverlay.setVisibility(View.GONE);
            menuPopup.setVisibility(View.GONE);

            selectNavigation(navDoctors, imgDoctor, txtDoctor);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.patientFragmentContainer,
                            new DoctorsFragment()
                    )
                    .commit();
        });

        navNotification.setOnClickListener(v -> {
            currentTab = 2;

            blurOverlay.setVisibility(View.GONE);
            menuPopup.setVisibility(View.GONE);

            selectNavigation(navNotification, imgNotification, txtNotification);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.patientFragmentContainer,
                            new PatientNotificationsFragment()
                    )
                    .commit();
            setTopBarTitle(
                    "Notifications"
            );
        });

        navMenu.setOnClickListener(v -> {
            selectNavigation(navMenu, imgMenu, txtMenu);
            blurOverlay.setVisibility(View.VISIBLE);
            menuPopup.setVisibility(View.VISIBLE);
        });

        menuProfile.setOnClickListener(v -> {

            blurOverlay.setVisibility(View.GONE);
            menuPopup.setVisibility(View.GONE);

            Intent intent = new Intent(
                    PatientDashboardActivity.this,
                    PatientProfileActivity.class
            );

            startActivity(intent);
        });

        blurOverlay.setOnClickListener(v -> {
            blurOverlay.setVisibility(View.GONE);
            menuPopup.setVisibility(View.GONE);
            if(currentTab == 0){

                selectNavigation(navHome, imgHome, txtHome);

            }
            else if(currentTab == 1){

                selectNavigation(navDoctors, imgDoctor, txtDoctor);

            }
            else if(currentTab == 2){

                selectNavigation(navNotification, imgNotification, txtNotification);

            }

        });

        btnSearch.setOnClickListener(v -> {

            openSearchMode();
        });

        btnBackSearch.setOnClickListener(v -> {

            closeSearchMode();

        });
        btnMessage.setOnClickListener(v -> {

            blurOverlay.setVisibility(View.GONE);
            menuPopup.setVisibility(View.GONE);

            hideMessageBadge();

            markPatientMessagesAsRead(() -> {

                Intent intent = new Intent(
                        PatientDashboardActivity.this,
                        PatientMessagesActivity.class
                );

                startActivity(intent);

            });
        });

        menuAppointment.setOnClickListener(v -> {
            blurOverlay.setVisibility(View.GONE);
            menuPopup.setVisibility(View.GONE);

            Intent intent =
                    new Intent(
                            PatientDashboardActivity.this,
                            PatientAppointmentsActivity.class
                    );

            startActivity(intent);
        });


        menuLogout.setOnClickListener(v -> {

            new AlertDialog.Builder(PatientDashboardActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        blurOverlay.setVisibility(View.GONE);
                        menuPopup.setVisibility(View.GONE);
                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(
                                PatientDashboardActivity.this,
                                LoginActivity.class
                        );

                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);

                        finish();

                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        });


    }
    private void markPatientMessagesAsRead(Runnable onComplete) {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            if (onComplete != null) {
                onComplete.run();
            }

            return;
        }

        String patientId = user.getUid();

        db.collectionGroup("messages")
                .whereEqualTo("receiverId", patientId)
                .whereEqualTo("isRead", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        hideMessageBadge();

                        if (onComplete != null) {
                            onComplete.run();
                        }

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

                    batch.commit()
                            .addOnSuccessListener(unused -> {

                                hideMessageBadge();

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
    public void setTopBarTitle(String title){

        txtTitle.setText(title);

    }
    public void hideMessageBadge() {

        if (txtMessageBadge != null) {

            txtMessageBadge.setVisibility(View.GONE);

        }
    }
    private void selectNavigation(LinearLayout selectedItem,
                                  ImageView selectedIcon,
                                  TextView selectedText){

        txtHome.setVisibility(View.GONE);
        txtDoctor.setVisibility(View.GONE);
        txtNotification.setVisibility(View.GONE);
        txtMenu.setVisibility(View.GONE);

        navHome.setBackgroundResource(0);
        navDoctors.setBackgroundResource(0);
        navNotification.setBackgroundResource(0);
        navMenu.setBackgroundResource(0);

        imgHome.setColorFilter(Color.BLACK);
        imgDoctor.setColorFilter(Color.BLACK);
        imgNotification.setColorFilter(Color.BLACK);
        imgMenu.setColorFilter(Color.BLACK);

        selectedItem.setBackgroundResource(
                R.drawable.nav_selected_background
        );

        selectedText.setVisibility(View.VISIBLE);

        selectedIcon.setColorFilter(Color.WHITE);
    }
    private void openSearchMode() {

        normalToolbar.setVisibility(View.GONE);
        searchToolbar.setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.patientFragmentContainer,
                        new SearchFragment(),
                        "SEARCH"
                )
                .addToBackStack("SEARCH_MODE")
                .commit();

        bottomNavigationCard.setVisibility(View.GONE);

    }
    private void closeSearchMode() {

        searchView.setQuery("", false);
        searchView.clearFocus();

        searchToolbar.setVisibility(View.GONE);
        normalToolbar.setVisibility(View.VISIBLE);

        bottomNavigationCard.setVisibility(View.VISIBLE);


        getSupportFragmentManager()
                .popBackStack(
                        "SEARCH_MODE",
                        androidx.fragment.app.FragmentManager
                                .POP_BACK_STACK_INCLUSIVE
                );

        currentTab = 0;

        selectNavigation(
                navHome,
                imgHome,
                txtHome
        );

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.patientFragmentContainer,
                        new PatientHomeFragment()
                )
                .commit();

        setTopBarTitle(
                "Find Your Specialist"
        );
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        if (intent.getBooleanExtra("openNotifications", false)) {

            blurOverlay.setVisibility(View.GONE);
            menuPopup.setVisibility(View.GONE);

            currentTab = 2;

            selectNavigation(
                    navNotification,
                    imgNotification,
                    txtNotification
            );

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.patientFragmentContainer,
                            new PatientNotificationsFragment()
                    )
                    .commit();

            txtTitle.setText("Notifications");

            return;
        }

        if (intent.getBooleanExtra("openHome", false)) {

            blurOverlay.setVisibility(View.GONE);
            menuPopup.setVisibility(View.GONE);

            currentTab = 0;

            selectNavigation(
                    navHome,
                    imgHome,
                    txtHome
            );

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.patientFragmentContainer,
                            new PatientHomeFragment()
                    )
                    .commit();

            txtTitle.setText("Find Your Specialist");
        }
    }
    private void openNotificationsPage() {

        currentTab = 2;

        selectNavigation(
                navNotification,
                imgNotification,
                txtNotification
        );

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.patientFragmentContainer,
                        new PatientNotificationsFragment()
                )
                .commit();

        txtTitle.setText("Notifications");
    }
    private void listenForUnreadNotifications() {

        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        String patientId = user.getUid();

        notificationBadgeListener =
                FirebaseFirestore.getInstance()
                        .collection("notifications")
                        .whereEqualTo("userId", patientId)
                        .whereEqualTo("isRead", false)
                        .addSnapshotListener((snapshot, error) -> {

                            if (error != null || snapshot == null) {
                                return;
                            }

                            int unreadCount =
                                    snapshot.size();

                            if (unreadCount > 0) {

                                txtNotificationBadge
                                        .setText(
                                                unreadCount > 9
                                                        ? "9+"
                                                        : String.valueOf(unreadCount)
                                        );

                                txtNotificationBadge
                                        .setVisibility(View.VISIBLE);

                            } else {

                                txtNotificationBadge
                                        .setVisibility(View.GONE);
                            }

                        });
    }
    private void listenForUnreadMessages() {

        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }

        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            hideMessageBadge();

            return;
        }

        String patientId = user.getUid();

        if (messageUnreadListener != null) {

            messageUnreadListener.remove();
            messageUnreadListener = null;
        }

        messageUnreadListener =
                db.collectionGroup("messages")
                        .whereEqualTo("receiverId", patientId)
                        .whereEqualTo("isRead", false)
                        .addSnapshotListener((snapshot, error) -> {

                            if (error != null) {

                                return;
                            }

                            if (snapshot == null) {

                                return;
                            }

                            int unreadCount =
                                    snapshot.size();

                            if (unreadCount > 0) {

                                txtMessageBadge.setText(
                                        unreadCount > 99
                                                ? "99+"
                                                : String.valueOf(unreadCount)
                                );

                                txtMessageBadge.setVisibility(
                                        View.VISIBLE
                                );

                            } else {

                                hideMessageBadge();
                            }

                        });
    }
    public void openDoctorsFromSearch(
            String searchType,
            String searchValue) {

        searchToolbar.setVisibility(View.GONE);
        normalToolbar.setVisibility(View.VISIBLE);

        bottomNavigationCard.setVisibility(View.VISIBLE);

        searchView.setQuery("", false);
        searchView.clearFocus();

        getSupportFragmentManager()
                .popBackStack(
                        "SEARCH_MODE",
                        androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                );

        DoctorsFragment doctorsFragment =
                new DoctorsFragment();

        Bundle bundle =
                new Bundle();

        bundle.putString(
                "searchType",
                searchType
        );

        bundle.putString(
                "searchValue",
                searchValue
        );

        doctorsFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.patientFragmentContainer,
                        doctorsFragment
                )
                .commit();

        currentTab = 1;

        selectNavigation(
                navDoctors,
                imgDoctor,
                txtDoctor
        );

        setTopBarTitle(
                "Our Dentists"
        );
    }
    @Override
    protected void onDestroy() {

        if (notificationBadgeListener != null) {
            notificationBadgeListener.remove();
            notificationBadgeListener = null;
        }

        if (messageUnreadListener != null) {
            messageUnreadListener.remove();
            messageUnreadListener = null;
        }

        super.onDestroy();
    }
    @Override
    protected void onResume() {

        super.onResume();

    }
    private void refreshMessageBadge() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            hideMessageBadge();

            return;
        }

        String patientId = user.getUid();

        db.collectionGroup("messages")
                .whereEqualTo("receiverId", patientId)
                .whereEqualTo("isRead", false)
                .get()
                .addOnSuccessListener(snapshot -> {

                    int unreadCount = snapshot.size();

                    if (unreadCount == 0) {

                        hideMessageBadge();

                    } else {

                        txtMessageBadge.setText(
                                unreadCount > 99
                                        ? "99+"
                                        : String.valueOf(unreadCount)
                        );

                        txtMessageBadge.setVisibility(
                                View.VISIBLE
                        );
                    }

                })
                .addOnFailureListener(e -> {
                    hideMessageBadge();
                });
    }
    @Override
    protected void onStart() {

        super.onStart();

        IntentFilter filter =
                new IntentFilter(
                        PatientChatActivity.ACTION_MESSAGE_READ
                );

        registerReceiver(
                messageReadReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
        );
    }
    @Override
    protected void onStop() {

        super.onStop();

        unregisterReceiver(
                messageReadReceiver
        );
    }
    public void openAppointmentFromSearch(
            String appointmentId) {

        Intent intent =
                new Intent(
                        this,
                        PatientAppointmentDetailActivity.class
                );

        intent.putExtra(
                "appointmentId",
                appointmentId
        );

        startActivity(intent);
    }
}
