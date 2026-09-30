package com.example.gentletouchdentalclinic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchResultAdapter
        extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    ArrayList<SearchResult> results;
    public SearchResultAdapter(
            ArrayList<SearchResult> results) {

        this.results = results;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_search_result,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        SearchResult result =
                results.get(position);

        holder.txtResult.setText(
                result.getMatchedText()
        );

        holder.txtType.setText(
                result.getMatchedType()
        );

        holder.itemView.setOnClickListener(v -> {

            PatientDashboardActivity activity =
                    (PatientDashboardActivity) v.getContext();


            if (result.getMatchedType()
                    .startsWith("Appointment")) {

                activity.openAppointmentFromSearch(
                        result.getAppointmentId()
                );

            }
            else {

                activity.openDoctorsFromSearch(
                        result.getMatchedType(),
                        result.getMatchedText()
                );
            }

        });
    }

    @Override
    public int getItemCount() {

        return results.size();

    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {
        TextView txtResult;
        TextView txtType;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtResult =
                    itemView.findViewById(
                            R.id.txtSearchResult
                    );

            txtType =
                    itemView.findViewById(
                            R.id.txtSearchType
                    );
        }
    }
}