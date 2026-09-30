package com.example.gentletouchdentalclinic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DoctorPendingAppointmentAdapter
        extends RecyclerView.Adapter<DoctorPendingAppointmentAdapter.ViewHolder> {

    private ArrayList<DoctorPendingAppointment> appointmentList;

    public DoctorPendingAppointmentAdapter(
            ArrayList<DoctorPendingAppointment> appointmentList) {

        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_doctor_pending_appointment,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        DoctorPendingAppointment appointment =
                appointmentList.get(position);

        String patientName =
                appointment.getPatientName();

        if (patientName == null ||
                patientName.trim().isEmpty()) {

            patientName = "Patient Name";
        }

        holder.txtPatientName.setText(
                patientName
        );

        String ageText =
                appointment.getAge() != null
                        ? String.valueOf(
                        appointment.getAge()
                )
                        : "N/A";


        String gender =
                appointment.getGender();

        if (gender == null ||
                gender.trim().isEmpty()) {

            gender = "N/A";
        }

        holder.txtPatientInfo.setText(
                "Age: " +
                        ageText +
                        "  •  " +
                        gender
        );

        String date =
                appointment.getAppointmentDate();

        if (date == null ||
                date.trim().isEmpty()) {

            date = "Not provided";
        }

        holder.txtDate.setText(
                "Date: " + date
        );

        String time =
                appointment.getAppointmentTime();

        if (time == null ||
                time.trim().isEmpty()) {

            time = "Not provided";
        }

        holder.txtTime.setText(
                "Time: " + time
        );

        String type =
                appointment.getAppointmentType();

        if (type == null ||
                type.trim().isEmpty()) {

            type = "Not provided";
        }

        holder.txtType.setText(
                "Type: " + type
        );

        String phone =
                appointment.getPhone();

        if (phone == null ||
                phone.trim().isEmpty()) {

            phone = "Not provided";
        }

        holder.txtPhone.setText(
                "Phone: " + phone
        );

        String reason =
                appointment.getReason();

        if (reason == null ||
                reason.trim().isEmpty()) {

            reason = "Not provided";
        }

        holder.txtReason.setText(
                "Reason: " + reason
        );

        holder.txtStatus.setText(
                "Pending Approval"
        );
    }


    @Override
    public int getItemCount() {

        return appointmentList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtPatientName;
        TextView txtPatientInfo;
        TextView txtDate;
        TextView txtTime;
        TextView txtType;
        TextView txtPhone;
        TextView txtReason;
        TextView txtStatus;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);


            txtPatientName =
                    itemView.findViewById(
                            R.id.txtDoctorPendingPatientName
                    );

            txtPatientInfo =
                    itemView.findViewById(
                            R.id.txtDoctorPendingPatientInfo
                    );

            txtDate =
                    itemView.findViewById(
                            R.id.txtDoctorPendingDate
                    );

            txtTime =
                    itemView.findViewById(
                            R.id.txtDoctorPendingTime
                    );

            txtType =
                    itemView.findViewById(
                            R.id.txtDoctorPendingType
                    );

            txtPhone =
                    itemView.findViewById(
                            R.id.txtDoctorPendingPhone
                    );

            txtReason =
                    itemView.findViewById(
                            R.id.txtDoctorPendingReason
                    );

            txtStatus =
                    itemView.findViewById(
                            R.id.txtDoctorPendingStatus
                    );
        }
    }
}