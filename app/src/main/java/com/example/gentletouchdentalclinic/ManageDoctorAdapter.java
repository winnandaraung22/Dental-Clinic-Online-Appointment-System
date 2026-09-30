package com.example.gentletouchdentalclinic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.RatingBar;
import android.widget.LinearLayout;

public class ManageDoctorAdapter extends RecyclerView.Adapter<ManageDoctorAdapter.DoctorViewHolder> {

    private ArrayList<Doctor> doctorList;
    private OnDoctorActionListener listener;

    public interface OnDoctorActionListener{
        void onEditClick(Doctor doctor);
        void onDeleteClick(Doctor doctor);
    }
    public ManageDoctorAdapter(ArrayList<Doctor> doctorList,
                               OnDoctorActionListener listener){

        this.doctorList = doctorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manage_doctor,parent,false);

        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DoctorViewHolder holder,
            int position){

        Doctor doctor = doctorList.get(position);

        holder.txtName.setText(doctor.getFullName());

        holder.txtSpecialization.setText(
                doctor.getSpecialization());

        holder.txtQualification.setText(
                doctor.getQualification());

        holder.txtExperience.setText(
                doctor.getExperience());

        holder.txtEmail.setText(
                doctor.getEmail());

        Double rating = doctor.getRating();

        if (rating != null && rating > 0) {

            holder.layoutDoctorRating.setVisibility(
                    View.VISIBLE
            );

            holder.ratingDoctor.setRating(
                    rating.floatValue()
            );

            holder.txtDoctorRatingValue.setText(
                    String.format(
                            java.util.Locale.ENGLISH,
                            "%.1f",
                            rating
                    )
            );

        }
        else {

            holder.layoutDoctorRating.setVisibility(
                    View.GONE
            );

        }

        holder.btnEdit.setOnClickListener(v ->
                listener.onEditClick(doctor));

        holder.btnDelete.setOnClickListener(v ->
                listener.onDeleteClick(doctor));

        if(doctor.getProfileImage() != null
                && !doctor.getProfileImage().isEmpty()){


            byte[] decodedBytes =
                    Base64.decode(
                            doctor.getProfileImage(),
                            Base64.DEFAULT
                    );


            Bitmap bitmap =
                    BitmapFactory.decodeByteArray(
                            decodedBytes,
                            0,
                            decodedBytes.length
                    );


            holder.imgDoctor.setImageBitmap(bitmap);


        }
        else{

            holder.imgDoctor.setImageResource(
                    R.drawable.doctor_outline
            );

        }
    }

    @Override
    public int getItemCount(){
        return doctorList.size();
    }

    static class DoctorViewHolder
            extends RecyclerView.ViewHolder{

        ImageView imgDoctor;

        TextView txtName;
        TextView txtSpecialization;
        TextView txtQualification;
        TextView txtExperience;
        TextView txtEmail;
        LinearLayout layoutDoctorRating;
        RatingBar ratingDoctor;
        TextView txtDoctorRatingValue;
        MaterialButton btnEdit;
        MaterialButton btnDelete;

        public DoctorViewHolder(@NonNull View itemView){
            super(itemView);

            imgDoctor = itemView.findViewById(R.id.imgDoctor);

            txtName = itemView.findViewById(R.id.txtDoctorName);

            txtSpecialization = itemView.findViewById(R.id.txtSpecialization);

            txtQualification = itemView.findViewById(R.id.txtQualification);

            txtExperience = itemView.findViewById(R.id.txtExperience);

            txtEmail = itemView.findViewById(R.id.txtDoctorEmail);

            layoutDoctorRating =
                    itemView.findViewById(
                            R.id.layoutDoctorRating
                    );

            ratingDoctor =
                    itemView.findViewById(
                            R.id.ratingDoctor
                    );

            txtDoctorRatingValue =
                    itemView.findViewById(
                            R.id.txtDoctorRatingValue
                    );

            btnEdit = itemView.findViewById(R.id.btnEditDoctor);

            btnDelete = itemView.findViewById(R.id.btnDeleteDoctor);
        }
    }
}
