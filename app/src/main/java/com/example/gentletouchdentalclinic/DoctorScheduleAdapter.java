package com.example.gentletouchdentalclinic;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;


public class DoctorScheduleAdapter extends RecyclerView.Adapter<DoctorScheduleAdapter.ScheduleViewHolder>{
    private ArrayList<DoctorScheduleGroup> doctorList;
    private OnEditClickListener editClickListener;
    public interface OnEditClickListener{
        void onEditClick(DoctorScheduleGroup group);
    }
    public DoctorScheduleAdapter(ArrayList<DoctorScheduleGroup> doctorList,
                                 OnEditClickListener editClickListener){

        this.doctorList = doctorList;
        this.editClickListener = editClickListener;
    }
    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){


        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_doctor_schedules,
                        parent,
                        false
                );

        return new ScheduleViewHolder(view);

    }

    @Override
    public void onBindViewHolder(
            @NonNull ScheduleViewHolder holder,
            int position){

        DoctorScheduleGroup group =
                doctorList.get(position);

        holder.txtMondayTime.setText("OFF");
        holder.txtTuesdayTime.setText("OFF");
        holder.txtWednesdayTime.setText("OFF");
        holder.txtThursdayTime.setText("OFF");
        holder.txtFridayTime.setText("OFF");
        holder.txtSaturdayTime.setText("OFF");

        holder.txtDoctorName
                .setText(group.getDoctorName());

        for(DoctorSchedule schedule :
                group.getSchedules()){

            String time;

            if(schedule.getStatus().equals("OFF")){

                time = "OFF";

            }
            else{

                time =
                        schedule.getStartTime()
                                + " - "
                                + schedule.getEndTime();

            }

            switch(schedule.getDayOfWeek()){

                case "Monday":
                    holder.txtMondayTime.setText(time);
                    break;

                case "Tuesday":
                    holder.txtTuesdayTime.setText(time);
                    break;

                case "Wednesday":
                    holder.txtWednesdayTime.setText(time);
                    break;

                case "Thursday":
                    holder.txtThursdayTime.setText(time);
                    break;

                case "Friday":
                    holder.txtFridayTime.setText(time);
                    break;

                case "Saturday":
                    holder.txtSaturdayTime.setText(time);
                    break;

            }

        }
        holder.btnEditSchedule.setOnClickListener(v -> {
            if(editClickListener != null){

                editClickListener.onEditClick(group);

            }
        });

    }

    @Override
    public int getItemCount(){

        return doctorList.size();

    }

    public static class ScheduleViewHolder
            extends RecyclerView.ViewHolder{

        TextView txtDoctorName;
        TextView txtMondayTime;
        TextView txtTuesdayTime;
        TextView txtWednesdayTime;
        TextView txtThursdayTime;
        TextView txtFridayTime;
        TextView txtSaturdayTime;
        MaterialButton btnEditSchedule;

        public ScheduleViewHolder(
                @NonNull View itemView){

            super(itemView);

            txtDoctorName =
                    itemView.findViewById(
                            R.id.txtDoctorName);

            txtMondayTime =
                    itemView.findViewById(
                            R.id.txtMondayTime);

            txtTuesdayTime =
                    itemView.findViewById(
                            R.id.txtTuesdayTime);

            txtWednesdayTime =
                    itemView.findViewById(
                            R.id.txtWednesdayTime);

            txtThursdayTime =
                    itemView.findViewById(
                            R.id.txtThursdayTime);

            txtFridayTime =
                    itemView.findViewById(
                            R.id.txtFridayTime);

            txtSaturdayTime =
                    itemView.findViewById(
                            R.id.txtSaturdayTime);

            btnEditSchedule =
                    itemView.findViewById(
                            R.id.btnEditSchedule);

        }

    }


}