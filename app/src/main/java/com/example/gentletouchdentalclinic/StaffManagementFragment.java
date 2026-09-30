package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class StaffManagementFragment extends Fragment {
    RecyclerView recyclerStaff;
    ArrayList<Staff> staffList;
    ArrayList<Staff> fullStaffList;
    StaffAdapter adapter;
    EditText edtSearchStaff;
    FirebaseFirestore db;
    FloatingActionButton btnAddStaff;
    private ListenerRegistration staffListener;

    public StaffManagementFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState){


        View view =
                inflater.inflate(
                        R.layout.fragment_staff_management,
                        container,
                        false
                );

        edtSearchStaff =
                view.findViewById(
                        R.id.edtSearchStaff
                );

        recyclerStaff =
                view.findViewById(
                        R.id.recyclerStaff
                );

        btnAddStaff =
                view.findViewById(R.id.btnAddStaff);


        btnAddStaff.setOnClickListener(v -> {


            Intent intent =
                    new Intent(
                            requireContext(),
                            AddStaffActivity.class
                    );


            startActivity(intent);


        });


        recyclerStaff.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );


        staffList =
                new ArrayList<>();

        fullStaffList =
                new ArrayList<>();

        adapter =
                new StaffAdapter(
                        staffList
                );


        recyclerStaff.setAdapter(adapter);

        edtSearchStaff.addTextChangedListener(
                new TextWatcher() {


                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after){

                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count){


                        searchStaff(
                                s.toString()
                        );


                    }


                    @Override
                    public void afterTextChanged(
                            Editable s){

                    }


                }
        );

        db = FirebaseFirestore.getInstance();


        loadStaff();



        return view;

    }
    private void loadStaff(){

        if (db == null) {
            return;
        }

        if (staffListener != null) {
            staffListener.remove();
        }

        staffListener =
                db.collection("staff")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {
                                        return;
                                    }

                                    if (snapshot == null) {
                                        return;
                                    }

                                    staffList.clear();
                                    fullStaffList.clear();

                                    for (
                                            DocumentSnapshot document :
                                            snapshot.getDocuments()
                                    ) {

                                        Staff staff =
                                                document.toObject(
                                                        Staff.class
                                                );

                                        if (staff != null) {

                                            staff.setId(
                                                    document.getId()
                                            );

                                            staffList.add(
                                                    staff
                                            );

                                            fullStaffList.add(
                                                    staff
                                            );
                                        }
                                    }
                                    adapter.notifyDataSetChanged();

                                    if (edtSearchStaff != null) {

                                        searchStaff(
                                                edtSearchStaff
                                                        .getText()
                                                        .toString()
                                        );
                                    }
                                }
                        );
    }
    private void searchStaff(String keyword){


        ArrayList<Staff> filteredList =
                new ArrayList<>();

        keyword = keyword.toLowerCase();

        for(Staff staff : fullStaffList){


            if(
                    (staff.getFullName() != null &&
                            staff.getFullName()
                                    .toLowerCase()
                                    .contains(keyword))

                            ||

                            (staff.getEmail() != null &&
                                    staff.getEmail()
                                            .toLowerCase()
                                            .contains(keyword))

                            ||

                            (staff.getPhone() != null &&
                                    staff.getPhone()
                                            .contains(keyword))

                            ||

                            (staff.getPosition() != null &&
                                    staff.getPosition()
                                            .toLowerCase()
                                            .contains(keyword))
            ){

                filteredList.add(staff);

            }


        }

        adapter.updateStaffList(
                filteredList
        );

    }
    @Override
    public void onDestroyView() {

        if (staffListener != null) {

            staffListener.remove();
            staffListener = null;
        }

        super.onDestroyView();
    }

}
