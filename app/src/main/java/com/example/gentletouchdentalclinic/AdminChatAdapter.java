package com.example.gentletouchdentalclinic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminChatAdapter
        extends RecyclerView.Adapter<AdminChatAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(AdminChat chat);
    }

    private final List<AdminChat> chatList;
    private final OnChatClickListener listener;

    public AdminChatAdapter(
            List<AdminChat> chatList,
            OnChatClickListener listener) {

        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_message_doctor,
                        parent,
                        false
                );

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ChatViewHolder holder,
            int position) {

        AdminChat chat = chatList.get(position);

        holder.txtDoctorName.setText(
                chat.getDoctorName()
        );

        String lastMessage = chat.getLastMessage();

        if (lastMessage == null || lastMessage.isEmpty()) {

            holder.txtLastMessage.setText(
                    "Tap to start conversation"
            );

        } else {

            holder.txtLastMessage.setText(
                    lastMessage
            );
        }

        if (chat.getLastMessageTime() != null
                && !chat.getLastMessageTime().isEmpty()) {

            holder.txtMessageTime.setText(
                    chat.getLastMessageTime()
            );

        } else {

            holder.txtMessageTime.setText("");
        }

        if (chat.isUnread()) {

            holder.txtDoctorName.setTypeface(
                    holder.txtDoctorName.getTypeface(),
                    Typeface.BOLD
            );

            holder.txtLastMessage.setTypeface(
                    holder.txtLastMessage.getTypeface(),
                    Typeface.BOLD
            );

            holder.txtMessageTime.setTypeface(
                    holder.txtMessageTime.getTypeface(),
                    Typeface.BOLD
            );

        } else {

            holder.txtDoctorName.setTypeface(
                    holder.txtDoctorName.getTypeface(),
                    Typeface.NORMAL
            );

            holder.txtLastMessage.setTypeface(
                    holder.txtLastMessage.getTypeface(),
                    Typeface.NORMAL
            );

            holder.txtMessageTime.setTypeface(
                    holder.txtMessageTime.getTypeface(),
                    Typeface.NORMAL
            );
        }

        if (chat.getDoctorPhoto() != null
                && !chat.getDoctorPhoto().isEmpty()) {

            try {

                byte[] imageBytes = Base64.decode(
                        chat.getDoctorPhoto(),
                        Base64.DEFAULT
                );

                Bitmap bitmap =
                        BitmapFactory.decodeByteArray(
                                imageBytes,
                                0,
                                imageBytes.length
                        );

                if (bitmap != null) {

                    holder.imgDoctor.setImageBitmap(bitmap);

                } else {

                    holder.imgDoctor.setImageResource(
                            R.drawable.profile
                    );
                }

            } catch (Exception e) {

                holder.imgDoctor.setImageResource(
                        R.drawable.profile
                );
            }

        } else {

            holder.imgDoctor.setImageResource(
                    R.drawable.profile
            );
        }

        holder.itemView.setOnClickListener(
                v -> listener.onChatClick(chat)
        );
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgDoctor;
        TextView txtDoctorName;
        TextView txtLastMessage;
        TextView txtMessageTime;

        ChatViewHolder(
                @NonNull View itemView) {

            super(itemView);

            imgDoctor =
                    itemView.findViewById(
                            R.id.imgDoctor
                    );

            txtDoctorName =
                    itemView.findViewById(
                            R.id.txtDoctorName
                    );

            txtLastMessage =
                    itemView.findViewById(
                            R.id.txtLastMessage
                    );

            txtMessageTime =
                    itemView.findViewById(
                            R.id.txtMessageTime
                    );
        }
    }
}