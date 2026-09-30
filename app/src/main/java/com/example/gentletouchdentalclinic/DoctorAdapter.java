package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.android.material.button.MaterialButton;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    private ArrayList<Doctor> doctorList;
    public DoctorAdapter(ArrayList<Doctor> doctorList){

        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featured_doctor,parent,false);

        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DoctorViewHolder holder,
            int position){

        Doctor doctor = doctorList.get(position);

        holder.txtName.setText(
                doctor.getFullName()
        );

        holder.txtSpeciality.setText(
                doctor.getSpecialization()
                        + "\n"
                        + doctor.getExperience()
        );

        if (doctor.getProfileImage() != null
                && !doctor.getProfileImage().isEmpty()) {

            byte[] decodedBytes =
                    android.util.Base64.decode(
                            doctor.getProfileImage(),
                            android.util.Base64.DEFAULT
                    );

            android.graphics.Bitmap bitmap =
                    android.graphics.BitmapFactory.decodeByteArray(
                            decodedBytes,
                            0,
                            decodedBytes.length
                    );

            holder.imgDoctor.setImageBitmap(bitmap);

        } else {

            holder.imgDoctor.setImageResource(
                    R.drawable.doctor_outline
            );

        }
        holder.btnViewProfile.setOnClickListener(v -> {


            Intent intent =
                    new Intent(
                            v.getContext(),
                            DoctorProfileActivity.class
                    );


            intent.putExtra(
                    "doctorId",
                    doctor.getId()
            );


            v.getContext().startActivity(intent);


        });

    }


    @Override
    public int getItemCount(){

        return doctorList.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder{

        TextView txtName;
        TextView txtSpeciality;
        ImageView imgDoctor;
        MaterialButton btnViewProfile;

        public DoctorViewHolder(@NonNull View itemView){
            super(itemView);

            txtName = itemView.findViewById(R.id.txtDoctorName);
            txtSpeciality = itemView.findViewById(R.id.txtDoctorSpeciality);
            imgDoctor = itemView.findViewById(R.id.imgDoctorPhoto);
            btnViewProfile = itemView.findViewById(R.id.btnViewDoctor);
        }
    }
}
