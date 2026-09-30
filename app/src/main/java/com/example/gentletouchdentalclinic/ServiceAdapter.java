package com.example.gentletouchdentalclinic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private ArrayList<Service> serviceList;
    private OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onClick(Service service);

    }

    public ServiceAdapter(
            ArrayList<Service> serviceList,
            OnServiceClickListener listener
    ){

        this.serviceList = serviceList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position){
        Service service = serviceList.get(position);


        holder.txtService.setText(
                service.getServiceName()
        );


        int imageResource =
                holder.itemView.getContext()
                        .getResources()
                        .getIdentifier(
                                service.getImageName(),
                                "drawable",
                                holder.itemView.getContext()
                                        .getPackageName()
                        );


        holder.imgService.setImageResource(
                imageResource
        );

        holder.itemView.setOnClickListener(v -> {

            if(listener != null){

                listener.onClick(service);

            }

        });
    }

    @Override
    public int getItemCount(){
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder{
        ImageView imgService;
        TextView txtService;

        public ServiceViewHolder(@NonNull View itemView){
            super(itemView);

            imgService = itemView.findViewById(R.id.imgService);
            txtService = itemView.findViewById(R.id.txtService);
        }
    }
}
