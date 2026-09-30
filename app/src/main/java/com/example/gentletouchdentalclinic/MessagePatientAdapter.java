package com.example.gentletouchdentalclinic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagePatientAdapter
        extends RecyclerView.Adapter<MessagePatientAdapter.PatientViewHolder> {

    private final List<PatientMessageItem> patientList;
    private final OnPatientClickListener listener;
    public interface OnPatientClickListener {
        void onPatientClick(PatientMessageItem patient);
    }
    public MessagePatientAdapter(
            List<PatientMessageItem> patientList,
            OnPatientClickListener listener) {

        this.patientList = patientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_doctor_chat,
                        parent,
                        false
                );

        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PatientViewHolder holder,
            int position) {

        PatientMessageItem patient =
                patientList.get(position);

        boolean unread = patient.isUnread();

        if (unread) {

            holder.txtPatientName.setTypeface(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
            );

            holder.txtLastMessage.setTypeface(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
            );

        } else {

            holder.txtPatientName.setTypeface(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.NORMAL
            );

            holder.txtLastMessage.setTypeface(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.NORMAL
            );
        }

        holder.txtPatientName.setText(
                patient.getPatientName()
        );

        holder.txtLastMessage.setText(
                patient.getLastMessage()
        );

        holder.txtMessageTime.setText(
                patient.getMessageTime()
        );

        holder.imgPatient.setImageResource(
                R.drawable.users_profile
        );

        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {

                listener.onPatientClick(
                        patient
                );
            }
        });
    }

    @Override
    public int getItemCount() {

        return patientList.size();
    }

    static class PatientViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgPatient;
        TextView txtPatientName;
        TextView txtLastMessage;
        TextView txtMessageTime;

        public PatientViewHolder(
                @NonNull View itemView) {

            super(itemView);

            imgPatient =
                    itemView.findViewById(
                            R.id.imgPatient
                    );

            txtPatientName =
                    itemView.findViewById(
                            R.id.txtPatientName
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