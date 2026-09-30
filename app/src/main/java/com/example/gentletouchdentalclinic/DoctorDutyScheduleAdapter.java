package com.example.gentletouchdentalclinic;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class DoctorDutyScheduleAdapter
        extends RecyclerView.Adapter<DoctorDutyScheduleAdapter.ScheduleViewHolder> {

    private ArrayList<DoctorSchedule> scheduleList;
    public DoctorDutyScheduleAdapter(
            ArrayList<DoctorSchedule> scheduleList) {

        this.scheduleList = scheduleList;

    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {


        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(
                R.layout.item_doctor_schedule,
                parent,
                false
        );

        return new ScheduleViewHolder(view);

    }

    @Override
    public void onBindViewHolder(
            @NonNull ScheduleViewHolder holder,
            int position) {

        DoctorSchedule schedule =
                scheduleList.get(position);

        holder.txtScheduleDay.setText(
                schedule.getDayOfWeek()
        );

        if(schedule.getStatus().equalsIgnoreCase("OFF")){

            holder.txtScheduleTime.setText("OFF");

        }
        else{

            holder.txtScheduleTime.setText(
                    schedule.getStartTime()
                            + " - "
                            + schedule.getEndTime()
            );

        }

        holder.txtScheduleSpecialization.setText(
                schedule.getSpecialization()
        );

        holder.txtScheduleStatus.setText(
                schedule.getStatus()
        );


    }
    @Override
    public int getItemCount() {

        return scheduleList.size();

    }
    public static class ScheduleViewHolder
            extends RecyclerView.ViewHolder {
        TextView txtScheduleDay;
        TextView txtScheduleTime;
        TextView txtScheduleSpecialization;
        TextView txtScheduleStatus;
        public ScheduleViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtScheduleDay =
                    itemView.findViewById(
                            R.id.txtScheduleDay
                    );

            txtScheduleTime =
                    itemView.findViewById(
                            R.id.txtScheduleTime
                    );

            txtScheduleSpecialization =
                    itemView.findViewById(
                            R.id.txtScheduleSpecialization
                    );

            txtScheduleStatus =
                    itemView.findViewById(
                            R.id.txtScheduleStatus
                    );
        }

    }

}