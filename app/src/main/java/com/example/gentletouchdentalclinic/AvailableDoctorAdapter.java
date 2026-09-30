package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AvailableDoctorAdapter
        extends RecyclerView.Adapter<AvailableDoctorAdapter.ViewHolder>{

    private ArrayList<DoctorSchedule> doctorList;
    public AvailableDoctorAdapter(
            ArrayList<DoctorSchedule> doctorList){

        this.doctorList = doctorList;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){

        View view =
                LayoutInflater.from(
                                parent.getContext())
                        .inflate(
                                R.layout.item_available_doctor,
                                parent,
                                false
                        );

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position){

        DoctorSchedule doctor =
                doctorList.get(position);

        holder.txtDoctorName
                .setText(
                        doctor.getDoctorName()
                );

        holder.txtSpecialization
                .setText(
                        "Specialization: " + doctor.getSpecialization()
                );

        holder.txtDutyTime
                .setText(
                        doctor.getStartTime()
                                + " - "
                                + doctor.getEndTime()
                );

        holder.btnBookAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(
                    v.getContext(),
                    AppointmentActivity.class
            );

            intent.putExtra(
                    "doctorId",
                    doctor.getDoctorDocumentID()
            );

            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount(){

        return doctorList.size();

    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder{

        TextView txtDoctorName;
        TextView txtDutyTime;
        TextView txtSpecialization;
        MaterialButton btnBookAppointment;

        public ViewHolder(
                @NonNull View itemView){

            super(itemView);

            txtSpecialization = itemView.findViewById(R.id.txtSpecialization);

            btnBookAppointment = itemView.findViewById(R.id.btnBookAppointment);

            txtDoctorName =
                    itemView.findViewById(
                            R.id.txtDoctorName
                    );

            txtDutyTime =
                    itemView.findViewById(
                            R.id.txtDutyTime
                    );

        }

    }

}
