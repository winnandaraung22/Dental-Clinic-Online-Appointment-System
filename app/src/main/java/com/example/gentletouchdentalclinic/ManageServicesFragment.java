package com.example.gentletouchdentalclinic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageServicesFragment extends Fragment {
    RecyclerView recyclerServicesAdmin;
    ArrayList<Service> serviceList;
    AdminServiceAdapter adapter;
    FirebaseFirestore db;
    MaterialButton btnAddService;

    private String[] serviceImages = {

            "teeth_cleaning",
            "braces",
            "implant",
            "whitening",
            "root_canal",
            "dental_filling",
            "tooth_extraction",
            "dental_checkup"

    };
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState){


        View view =
                inflater.inflate(
                        R.layout.fragment_manage_services,
                        container,
                        false
                );


        recyclerServicesAdmin =
                view.findViewById(
                        R.id.recyclerServicesAdmin
                );


        btnAddService =
                view.findViewById(
                        R.id.btnAddService
                );


        db = FirebaseFirestore.getInstance();

        serviceList = new ArrayList<>();

        adapter =
                new AdminServiceAdapter(
                        requireContext(),
                        serviceList,

                        new AdminServiceAdapter.OnServiceActionListener(){

                            @Override
                            public void onEdit(Service service){

                                editService(service);

                            }

                            @Override
                            public void onDelete(Service service){

                                deleteService(service);

                            }

                        }
                );

        recyclerServicesAdmin.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerServicesAdmin.setAdapter(adapter);

        loadServices();

        btnAddService.setOnClickListener(v -> {

            showAddServiceDialog();

        });

        return view;

    }
    private void loadServices(){


        db.collection("services")
                .get()
                .addOnSuccessListener(snapshot -> {

                    serviceList.clear();

                    for(DocumentSnapshot document : snapshot){

                        Service service =
                                document.toObject(Service.class);

                        if(service != null){

                            service.setId(
                                    document.getId()
                            );

                            serviceList.add(service);

                        }

                    }

                    adapter.notifyDataSetChanged();

                });

    }
    private void showAddServiceDialog() {

        View view =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.dialog_add_edit_service,
                                null
                        );

        TextInputEditText edtName =
                view.findViewById(R.id.edtServiceName);

        TextInputEditText edtDescription =
                view.findViewById(R.id.edtServiceDescription);

        TextInputEditText edtPrice =
                view.findViewById(R.id.edtServicePrice);

        TextInputEditText edtDuration =
                view.findViewById(R.id.edtServiceDuration);

        AutoCompleteTextView autoImage =
                view.findViewById(R.id.autoServiceImage);

        ArrayAdapter<String> imageAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        serviceImages
                );

        autoImage.setAdapter(imageAdapter);

        AlertDialog dialog =
                new AlertDialog.Builder(requireContext())
                        .setTitle("Add Service")
                        .setView(view)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Save",
                                null
                        )
                        .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String name =
                        edtName.getText()
                                .toString()
                                .trim();

                String description =
                        edtDescription.getText()
                                .toString()
                                .trim();

                String price =
                        edtPrice.getText()
                                .toString()
                                .trim();

                String duration =
                        edtDuration.getText()
                                .toString()
                                .trim();

                String imageName =
                        autoImage.getText()
                                .toString()
                                .trim();

                if(name.isEmpty()
                        || description.isEmpty()
                        || price.isEmpty()
                        || duration.isEmpty()
                        || imageName.isEmpty()){

                    Toast.makeText(
                            requireContext(),
                            "All fields are required",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                if(!price.matches("[0-9]+")){

                    Toast.makeText(
                            requireContext(),
                            "Price must contain numbers only",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                db.collection("services")
                        .whereEqualTo(
                                "serviceName",
                                name
                        )
                        .get()
                        .addOnSuccessListener(
                                queryDocumentSnapshots -> {


                                    if(!queryDocumentSnapshots.isEmpty()){

                                        Toast.makeText(
                                                requireContext(),
                                                "This service already exists",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        return;
                                    }

                                    HashMap<String,Object> service =
                                            new HashMap<>();

                                    service.put(
                                            "serviceName",
                                            name
                                    );

                                    service.put(
                                            "description",
                                            description
                                    );

                                    service.put(
                                            "price",
                                            price
                                    );

                                    service.put(
                                            "duration",
                                            duration
                                    );

                                    service.put(
                                            "imageName",
                                            imageName
                                    );

                                    db.collection("services")
                                            .add(service)
                                            .addOnSuccessListener(
                                                    documentReference -> {

                                                        Toast.makeText(
                                                                requireContext(),
                                                                "Service added successfully",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                        dialog.dismiss();

                                                        loadServices();

                                                    }
                                            )
                                            .addOnFailureListener(
                                                    e -> {

                                                        Toast.makeText(
                                                                requireContext(),
                                                                "Failed to add service",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                    }
                                            );

                                }
                        )
                        .addOnFailureListener(
                                e -> {

                                    Toast.makeText(
                                            requireContext(),
                                            "Unable to check service name",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                }
                        );

            });

        });


        dialog.show();
    }
    private void editService(Service service){

        View view =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.dialog_add_edit_service,
                                null
                        );

        TextInputEditText edtName =
                view.findViewById(R.id.edtServiceName);


        TextInputEditText edtDescription =
                view.findViewById(R.id.edtServiceDescription);

        TextInputEditText edtPrice =
                view.findViewById(R.id.edtServicePrice);

        TextInputEditText edtDuration =
                view.findViewById(R.id.edtServiceDuration);

        AutoCompleteTextView autoImage =
                view.findViewById(R.id.autoServiceImage);

        edtName.setText(service.getServiceName());

        edtDescription.setText(service.getDescription());

        edtPrice.setText(service.getPrice());

        edtDuration.setText(service.getDuration());

        ArrayAdapter<String> imageAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        serviceImages
                );

        autoImage.setAdapter(imageAdapter);

        autoImage.setText(
                service.getImageName(),
                false
        );

        new AlertDialog.Builder(requireContext())

                .setTitle("Edit Service")

                .setView(view)

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Update",
                        (dialog, which) -> {

                            String name =
                                    edtName.getText()
                                            .toString()
                                            .trim();

                            String description =
                                    edtDescription.getText()
                                            .toString()
                                            .trim();

                            String price =
                                    edtPrice.getText()
                                            .toString()
                                            .trim();

                            String duration =
                                    edtDuration.getText()
                                            .toString()
                                            .trim();

                            String image =
                                    autoImage.getText()
                                            .toString()
                                            .trim();

                            if(name.isEmpty()
                                    || description.isEmpty()
                                    || price.isEmpty()
                                    || duration.isEmpty()
                                    || image.isEmpty()){


                                Toast.makeText(
                                        requireContext(),
                                        "Please fill all fields",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;

                            }

                            checkDuplicateServiceName(
                                    name,
                                    service.getId(),
                                    () -> {

                                        HashMap<String,Object> update =
                                                new HashMap<>();

                                        update.put(
                                                "serviceName",
                                                name
                                        );

                                        update.put(
                                                "description",
                                                description
                                        );

                                        update.put(
                                                "price",
                                                price
                                        );

                                        update.put(
                                                "duration",
                                                duration
                                        );

                                        update.put(
                                                "imageName",
                                                image
                                        );

                                        db.collection("services")
                                                .document(service.getId())
                                                .update(update)
                                                .addOnSuccessListener(unused -> {

                                                    Toast.makeText(
                                                            requireContext(),
                                                            "Service Updated",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                    loadServices();

                                                });

                                    });

                        })
                .show();

    }
    private void deleteService(Service service){

        new AlertDialog.Builder(requireContext())

                .setTitle("Delete Service")

                .setMessage(
                        "Are you sure you want to delete "
                                + service.getServiceName()
                                + "?"
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            db.collection("services")
                                    .document(
                                            service.getId()
                                    )
                                    .delete()
                                    .addOnSuccessListener(unused -> {

                                        Toast.makeText(
                                                requireContext(),
                                                "Service deleted",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        loadServices();

                                    });

                        })

                .show();

    }
    private void checkDuplicateServiceName(
            String serviceName,
            String currentServiceId,
            Runnable onSuccess
    ){

        db.collection("services")
                .whereEqualTo(
                        "serviceName",
                        serviceName
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    boolean duplicate = false;

                    for(DocumentSnapshot document : snapshot){

                        if(currentServiceId == null){

                            duplicate = true;
                            break;

                        }

                        if(!document.getId().equals(currentServiceId)){

                            duplicate = true;
                            break;

                        }

                    }

                    if(duplicate){

                        Toast.makeText(
                                requireContext(),
                                "Service name already exists",
                                Toast.LENGTH_SHORT
                        ).show();


                    }else{

                        onSuccess.run();

                    }


                });


    }
}
