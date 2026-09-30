package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AdminChatFragment extends Fragment {

    private RecyclerView recyclerAdminChats;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration unreadMessagesListener;
    private AdminChatAdapter adapter;
    private final List<AdminChat> chatList =
            new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_admin_chat,
                container,
                false
        );

        recyclerAdminChats =
                view.findViewById(
                        R.id.recyclerAdminChats
                );

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerAdminChats.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        adapter = new AdminChatAdapter(
                chatList,
                this::openChat
        );

        recyclerAdminChats.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        listenForUnreadAdminChats();
    }
    private void listenForUnreadAdminChats() {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String adminId =
                auth.getCurrentUser().getUid();

        if (unreadMessagesListener != null) {

            unreadMessagesListener.remove();

            unreadMessagesListener = null;
        }

        unreadMessagesListener =
                db.collectionGroup("messages")
                        .whereEqualTo(
                                "receiverId",
                                adminId
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

                                    Set<String> unreadChatIds =
                                            new HashSet<>();

                                    for (
                                            DocumentSnapshot message
                                            : snapshot.getDocuments()
                                    ) {

                                        if (
                                                message
                                                        .getReference()
                                                        .getParent()
                                                        .getParent()
                                                        != null
                                        ) {

                                            String chatId =
                                                    message
                                                            .getReference()
                                                            .getParent()
                                                            .getParent()
                                                            .getId();

                                            unreadChatIds.add(
                                                    chatId
                                            );
                                        }
                                    }

                                    loadDoctors(
                                            unreadChatIds
                                    );
                                }
                        );
    }
    private void loadDoctors(
            Set<String> unreadChatIds) {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    requireContext(),
                    "Admin is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String adminId =
                auth.getCurrentUser().getUid();

        db.collection("chats")
                .whereEqualTo(
                        "adminId",
                        adminId
                )
                .orderBy(
                        "lastMessageTime",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            chatList.clear();

                            for (
                                    DocumentSnapshot document
                                    : querySnapshot.getDocuments()
                            ) {

                                String doctorId =
                                        document.getString(
                                                "doctorId"
                                        );

                                if (doctorId == null
                                        || doctorId.isEmpty()) {

                                    continue;
                                }

                                String chatId =
                                        ChatUtils.getAdminDoctorChatId(
                                                adminId,
                                                doctorId
                                        );

                                String doctorName =
                                        document.getString(
                                                "doctorName"
                                        );

                                String doctorPhoto =
                                        document.getString(
                                                "doctorPhoto"
                                        );

                                String lastMessage =
                                        document.getString(
                                                "lastMessage"
                                        );

                                Timestamp timestamp =
                                        document.getTimestamp(
                                                "lastMessageTime"
                                        );

                                if (
                                        doctorName == null
                                                || doctorName.isEmpty()
                                ) {

                                    doctorName =
                                            "Doctor";
                                }

                                if (
                                        lastMessage == null
                                                || lastMessage.isEmpty()
                                ) {

                                    lastMessage =
                                            "No messages";
                                }

                                String lastMessageTime = "";

                                if (timestamp != null) {

                                    Date date =
                                            timestamp.toDate();

                                    SimpleDateFormat formatter =
                                            new SimpleDateFormat(
                                                    "h:mm a",
                                                    Locale.getDefault()
                                            );

                                    lastMessageTime =
                                            formatter.format(date);
                                }

                                boolean unread =
                                        unreadChatIds.contains(
                                                chatId
                                        );

                                AdminChat adminChat =
                                        new AdminChat(
                                                chatId,
                                                doctorId,
                                                doctorName,
                                                doctorPhoto,
                                                lastMessage,
                                                lastMessageTime,
                                                unread
                                        );

                                chatList.add(
                                        adminChat
                                );
                            }

                            adapter.notifyDataSetChanged();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    requireContext(),
                                    "Failed to load chats: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }
    private void openChat(AdminChat chat) {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    requireContext(),
                    "Admin is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String adminId =
                auth.getCurrentUser().getUid();

        String doctorId =
                chat.getDoctorId();

        if (doctorId == null
                || doctorId.isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Doctor information is missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String chatId =
                ChatUtils.getAdminDoctorChatId(
                        adminId,
                        doctorId
                );

        markChatMessagesAsRead(
                chatId,
                () -> openDoctorChat(
                        chatId,
                        chat
                )
        );
    }
    private void markChatMessagesAsRead(
            String chatId,
            Runnable onComplete) {

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .whereEqualTo(
                        "receiverId",
                        auth.getCurrentUser().getUid()
                )
                .whereEqualTo(
                        "isRead",
                        false
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {

                        if (onComplete != null) {
                            onComplete.run();
                        }

                        return;
                    }

                    com.google.firebase.firestore.WriteBatch batch =
                            db.batch();

                    for (
                            DocumentSnapshot message
                            : snapshot.getDocuments()
                    ) {

                        batch.update(
                                message.getReference(),
                                "isRead",
                                true
                        );
                    }

                    batch.commit()
                            .addOnSuccessListener(
                                    unused -> {

                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    }
                            )
                            .addOnFailureListener(
                                    e -> {

                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    }
                            );
                })
                .addOnFailureListener(
                        e -> {

                            if (onComplete != null) {
                                onComplete.run();
                            }
                        }
                );
    }
    private void openDoctorChat(
            String chatId,
            AdminChat chat) {

        Intent intent =
                new Intent(
                        requireContext(),
                        AdminDoctorChatActivity.class
                );

        intent.putExtra(
                "chatId",
                chatId
        );

        intent.putExtra(
                "doctorId",
                chat.getDoctorId()
        );

        intent.putExtra(
                "doctorName",
                chat.getDoctorName()
        );

        intent.putExtra(
                "doctorPhoto",
                chat.getDoctorPhoto()
        );

        startActivity(intent);
    }

    @Override
    public void onDestroyView() {

        if (unreadMessagesListener != null) {

            unreadMessagesListener.remove();

            unreadMessagesListener = null;
        }

        super.onDestroyView();
    }
}