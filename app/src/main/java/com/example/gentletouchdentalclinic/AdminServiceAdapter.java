package com.example.gentletouchdentalclinic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;


public class AdminServiceAdapter
        extends RecyclerView.Adapter<AdminServiceAdapter.ServiceViewHolder>{

    private ArrayList<Service> serviceList;
    private Context context;
    private OnServiceActionListener listener;

    public interface OnServiceActionListener{
        void onEdit(Service service);
        void onDelete(Service service);
    }
    public AdminServiceAdapter(
            Context context,
            ArrayList<Service> serviceList,
            OnServiceActionListener listener){

        this.context = context;

        this.serviceList = serviceList;

        this.listener = listener;

    }
    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType){

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_admin_service,
                                parent,
                                false
                        );

        return new ServiceViewHolder(view);

    }
    @Override
    public void onBindViewHolder(
            @NonNull ServiceViewHolder holder,
            int position){

        Service service =
                serviceList.get(position);

        holder.txtName.setText(
                service.getServiceName()
        );

        holder.txtDescription.setText(
                service.getDescription()
        );

        holder.txtPrice.setText(
                service.getPrice() + " MMK"
        );

        holder.txtDuration.setText(
                service.getDuration()
        );

        int imageResource =
                context.getResources()
                        .getIdentifier(
                                service.getImageName(),
                                "drawable",
                                context.getPackageName()
                        );

        holder.imgService.setImageResource(
                imageResource
        );

        holder.btnEdit.setOnClickListener(v -> {

            listener.onEdit(service);

        });

        holder.btnDelete.setOnClickListener(v -> {

            listener.onDelete(service);

        });


    }
    @Override
    public int getItemCount(){

        return serviceList.size();

    }
    public static class ServiceViewHolder
            extends RecyclerView.ViewHolder{


        ImageView imgService;
        TextView txtName;
        TextView txtDescription;
        TextView txtPrice;
        TextView txtDuration;
        MaterialButton btnEdit;
        MaterialButton btnDelete;
        public ServiceViewHolder(
                @NonNull View itemView){

            super(itemView);

            imgService =
                    itemView.findViewById(
                            R.id.imgAdminService
                    );
            txtName =
                    itemView.findViewById(
                            R.id.txtAdminServiceName
                    );
            txtDescription =
                    itemView.findViewById(
                            R.id.txtAdminServiceDescription
                    );
            txtPrice =
                    itemView.findViewById(
                            R.id.txtAdminServicePrice
                    );
            txtDuration =
                    itemView.findViewById(
                            R.id.txtAdminServiceDuration
                    );
            btnEdit =
                    itemView.findViewById(
                            R.id.btnEditService
                    );
            btnDelete =
                    itemView.findViewById(
                            R.id.btnDeleteService
                    );
        }

    }

}