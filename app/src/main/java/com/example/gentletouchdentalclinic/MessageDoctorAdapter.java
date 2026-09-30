package com.example.gentletouchdentalclinic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageDoctorAdapter
        extends RecyclerView.Adapter<MessageDoctorAdapter.DoctorViewHolder> {

    private final List<DoctorMessageItem> doctorList;
    private final OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onDoctorClick(DoctorMessageItem doctor);
    }

    public MessageDoctorAdapter(
            List<DoctorMessageItem> doctorList,
            OnDoctorClickListener listener) {

        this.doctorList = doctorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_message_doctor,
                        parent,
                        false
                );

        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DoctorViewHolder holder,
            int position) {

        DoctorMessageItem doctor =
                doctorList.get(position);

        holder.txtDoctorName.setText(
                doctor.getDoctorName()
        );

        holder.txtLastMessage.setText(
                doctor.getLastMessage()
        );

        holder.txtMessageTime.setText(
                doctor.getMessageTime()
        );

        if (doctor.isUnread()) {

            holder.txtDoctorName.setTypeface(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
            );

            holder.txtLastMessage.setTypeface(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
            );

            holder.txtMessageTime.setTypeface(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
            );

            holder.txtUnreadBadge.setVisibility(
                    View.VISIBLE
            );

        } else {

            holder.txtDoctorName.setTypeface(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.NORMAL
            );

            holder.txtLastMessage.setTypeface(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.NORMAL
            );

            holder.txtMessageTime.setTypeface(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.NORMAL
            );

            holder.txtUnreadBadge.setVisibility(
                    View.GONE
            );
        }

        String profileImage =
                doctor.getProfileImage();

        if (profileImage != null
                && !profileImage.isEmpty()) {

            try {

                byte[] imageBytes =
                        Base64.decode(
                                profileImage,
                                Base64.DEFAULT
                        );

                Bitmap bitmap =
                        BitmapFactory.decodeByteArray(
                                imageBytes,
                                0,
                                imageBytes.length
                        );

                if (bitmap != null) {

                    holder.imgDoctor.setImageBitmap(
                            bitmap
                    );

                } else {

                    holder.imgDoctor.setImageResource(
                            R.drawable.doctor
                    );
                }

            } catch (IllegalArgumentException e) {

                holder.imgDoctor.setImageResource(
                        R.drawable.doctor
                );
            }

        } else {

            holder.imgDoctor.setImageResource(
                    R.drawable.doctor
            );
        }

        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {

                listener.onDoctorClick(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {

        return doctorList.size();
    }

    static class DoctorViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgDoctor;
        TextView txtDoctorName;
        TextView txtLastMessage;
        TextView txtMessageTime;
        TextView txtUnreadBadge;

        public DoctorViewHolder(
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

            txtUnreadBadge =
                    itemView.findViewById(
                            R.id.txtUnreadBadge
                    );
        }
    }
}