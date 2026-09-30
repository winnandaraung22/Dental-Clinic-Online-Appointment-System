package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class DoctorListAdapter
        extends RecyclerView.Adapter<DoctorListAdapter.DoctorViewHolder>{
    private ArrayList<Doctor> doctorList;
    public DoctorListAdapter(ArrayList<Doctor> doctorList){

        this.doctorList = doctorList;

    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_doctor_list,
                                parent,
                                false
                        );

        return new DoctorViewHolder(view);

    }
    @Override
    public void onBindViewHolder(
            @NonNull DoctorViewHolder holder,
            int position){

        Doctor doctor =
                doctorList.get(position);

        holder.txtDoctorName.setText(
                doctor.getFullName()
        );

        holder.txtSpecialization.setText(
                doctor.getSpecialization()
        );

        holder.txtQualification.setText(
                doctor.getQualification()
        );

        holder.txtExperience.setText(
                doctor.getExperience()
        );

        double rating =
                doctor.getRating();

        holder.ratingDoctor.setRating(
                (float) rating
        );

        holder.txtRating.setText(
                String.format(
                        "%.1f",
                        rating
                )
        );

        if(doctor.getProfileImage()!=null
                && !doctor.getProfileImage().isEmpty()){


            byte[] bytes =
                    Base64.decode(
                            doctor.getProfileImage(),
                            Base64.DEFAULT
                    );

            Bitmap bitmap =
                    BitmapFactory.decodeByteArray(
                            bytes,
                            0,
                            bytes.length
                    );

            holder.imgDoctor.setImageBitmap(bitmap);

        }
        else{

            holder.imgDoctor.setImageResource(
                    R.drawable.doctor_outline
            );


        }

        holder.btnBookAppointment
                .setOnClickListener(v -> {

                    Intent intent = new Intent(
                            v.getContext(),
                            AppointmentActivity.class
                    );

                    intent.putExtra(
                            "doctorId",
                            doctor.getId()
                    );

                    intent.putExtra(
                            "doctorName",
                            doctor.getFullName()
                    );

                    intent.putExtra(
                            "specialization",
                            doctor.getSpecialization()
                    );

                    v.getContext().startActivity(intent);

                });
    }
    @Override
    public int getItemCount(){

        return doctorList.size();

    }
    public static class DoctorViewHolder
            extends RecyclerView.ViewHolder{

        ImageView imgDoctor;
        TextView txtDoctorName;
        TextView txtSpecialization;
        TextView txtQualification;
        TextView txtExperience;
        TextView txtRating;
        RatingBar ratingDoctor;
        MaterialButton btnBookAppointment;

        public DoctorViewHolder(
                @NonNull View itemView){

            super(itemView);

            imgDoctor =
                    itemView.findViewById(
                            R.id.imgDoctor
                    );

            txtDoctorName =
                    itemView.findViewById(
                            R.id.txtDoctorName
                    );

            txtSpecialization =
                    itemView.findViewById(
                            R.id.txtSpecialization
                    );

            txtQualification =
                    itemView.findViewById(
                            R.id.txtQualification
                    );

            txtExperience =
                    itemView.findViewById(
                            R.id.txtExperience
                    );

            ratingDoctor =
                    itemView.findViewById(
                            R.id.ratingDoctor
                    );

            txtRating =
                    itemView.findViewById(
                            R.id.txtRating
                    );

            btnBookAppointment =
                    itemView.findViewById(
                            R.id.btnBookAppointment
                    );

        }

    }

}