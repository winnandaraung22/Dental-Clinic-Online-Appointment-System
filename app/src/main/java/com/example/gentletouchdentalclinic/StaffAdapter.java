package com.example.gentletouchdentalclinic;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class StaffAdapter
        extends RecyclerView.Adapter<StaffAdapter.ViewHolder>{

    ArrayList<Staff> staffList;

    public StaffAdapter(ArrayList<Staff> staffList){

        this.staffList = staffList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){


        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_staff,
                                parent,
                                false
                        );

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position){


        Staff staff =
                staffList.get(position);


        holder.txtName.setText(
                staff.getFullName()
        );

        holder.txtGender.setText(
                staff.getGender()
        );

        holder.txtPosition.setText(
                staff.getPosition()
        );

        holder.txtEmail.setText(
                staff.getEmail()
        );

        holder.txtPhone.setText(
                staff.getPhone()
        );

        holder.txtDOB.setText(
                staff.getDateOfBirth()
        );

        holder.txtHireDate.setText(
                staff.getHireDate()
        );

        holder.txtAddress.setText(
                staff.getAddress()
        );
        holder.btnStaffMenu.setOnClickListener(v -> {


            PopupMenu popup =
                    new PopupMenu(
                            v.getContext(),
                            holder.btnStaffMenu
                    );


            popup.getMenu()
                    .add("Edit");


            popup.getMenu()
                    .add("Delete");


            popup.setOnMenuItemClickListener(item -> {


                if(item.getTitle()
                        .equals("Edit")){


                    Intent intent =
                            new Intent(
                                    v.getContext(),
                                    EditStaffActivity.class
                            );


                    intent.putExtra(
                            "staffId",
                            staff.getId()
                    );


                    v.getContext()
                            .startActivity(intent);


                }


                else if(item.getTitle()
                        .equals("Delete")){


                    showDeleteConfirmation(
                            staff,
                            v
                    );


                }

                return true;

            });

            popup.show();


        });

    }

    @Override
    public int getItemCount(){

        return staffList.size();

    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder{

        TextView txtName, txtGender, txtPosition, txtEmail;
         TextView txtPhone, txtDOB, txtHireDate, txtAddress;
        ImageButton btnStaffMenu;

        public ViewHolder(@NonNull View itemView){

            super(itemView);

            btnStaffMenu =
                    itemView.findViewById(
                            R.id.btnStaffMenu
                    );

            txtName =
                    itemView.findViewById(
                            R.id.txtStaffName
                    );


            txtGender =
                    itemView.findViewById(
                            R.id.txtGender
                    );


            txtPosition =
                    itemView.findViewById(
                            R.id.txtPosition
                    );


            txtEmail =
                    itemView.findViewById(
                            R.id.txtEmail
                    );


            txtPhone =
                    itemView.findViewById(
                            R.id.txtPhone
                    );


            txtDOB =
                    itemView.findViewById(
                            R.id.txtDOB
                    );


            txtHireDate =
                    itemView.findViewById(
                            R.id.txtHireDate
                    );


            txtAddress =
                    itemView.findViewById(
                            R.id.txtAddress
                    );

        }

    }
    private void showDeleteConfirmation(
            Staff staff,
            View view){


        new AlertDialog.Builder(
                view.getContext()
        )
                .setTitle("Delete Staff")
                .setMessage(
                        "Are you sure you want to delete "
                                + staff.getFullName()
                                + "?"
                )

                .setPositiveButton(
                        "Yes",
                        (dialog, which) -> {


                            FirebaseFirestore db =
                                    FirebaseFirestore.getInstance();


                            db.collection("staff")
                                    .document(staff.getId())
                                    .delete()

                                    .addOnSuccessListener(unused -> {


                                        int position =
                                                staffList.indexOf(staff);


                                        if(position != -1){

                                            staffList.remove(position);

                                            notifyItemRemoved(position);

                                        }


                                        Toast.makeText(
                                                view.getContext(),
                                                "Staff deleted successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();


                                    })


                                    .addOnFailureListener(e -> {


                                        Toast.makeText(
                                                view.getContext(),
                                                "Delete failed",
                                                Toast.LENGTH_SHORT
                                        ).show();


                                    });


                        }
                )


                .setNegativeButton(
                        "Cancel",
                        null
                )


                .show();


    }
    public void updateStaffList(
            ArrayList<Staff> newList
    ){

        staffList = newList;

        notifyDataSetChanged();

    }

}