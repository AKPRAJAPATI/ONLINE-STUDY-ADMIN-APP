package com.university.mrmindedadmin.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.university.mrmindedadmin.Adapters.pdfAdapters;
import com.university.mrmindedadmin.Application.pdf.pdfViewActivity;
import com.university.mrmindedadmin.Models.pdfInterface;
import com.university.mrmindedadmin.Models.pdfModel;
import com.university.mrmindedadmin.Models.slideOnclickInterface;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.FragmentPdfBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;


public class PdfFragment extends Fragment implements slideOnclickInterface, pdfInterface {
    private FragmentPdfBinding binding;
    private pdfAdapters adapters;
    private ArrayList<pdfModel> arrayList;

    private Dialog dialog;
    private TextView updateName, cancelDailog;
    private EditText pdfNameChanged;
    private AlertDialog.Builder alearDailog;

    private String course_thumbnail_uniqueKeys;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private ActivityResultLauncher<String> getMyVideo;
    private Uri PDF_URI;
    private ProgressDialog progressDialog;

    private  String pdf_name;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
       binding = FragmentPdfBinding.inflate(inflater,container,false);

        Bundle message = getArguments();
        course_thumbnail_uniqueKeys = message.getString("u"); //this is course unique key

        alearDailog = new AlertDialog.Builder(getActivity());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving Data");

        //////////////////////////////// DIALOG////////////////////////////
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.change_pdf_name);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        updateName = dialog.findViewById(R.id.changePdfName);
        cancelDailog = dialog.findViewById(R.id.cancle);
        pdfNameChanged = dialog.findViewById(R.id.pdf_nameChanged);

        cancelDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ///////////////////////////////////////END DIALOG////////////////////////////////////


        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();


        binding.pdfListRecyclerview.setLayoutManager(new LinearLayoutManager(container.getContext()));
        binding.pdfListRecyclerview.setHasFixedSize(true);
        arrayList = new ArrayList<>();
         getOurPdf();
        adapters = new pdfAdapters(container.getContext(),arrayList,this);
        binding.pdfListRecyclerview.setAdapter(adapters);

        return binding.getRoot();
    }

    private void getOurPdf() {
        databaseReference.child("Admin").child(auth.getUid()).child("Course").child(auth.getUid()).child(course_thumbnail_uniqueKeys).child("pdf").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    arrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        pdfModel model = dataSnapshot.getValue(pdfModel.class);
                        model.setPdfUniqueKey(dataSnapshot.getKey());




                        binding.pdfListRecyclerview.setVisibility(View.VISIBLE);
                        binding.coursenotfound.setVisibility(View.GONE);
                        arrayList.add(model);
                    }
                    adapters.notifyDataSetChanged();
                } else {
                    arrayList.clear();
                  binding.coursenotfound.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void updateClickLister(int position, String uniqueKey) {
        dialog.show();

        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("pdfName",pdfNameChanged.getText().toString());

                databaseReference.child("Admin")
                        .child(auth.getUid())
                        .child("Course")
                        .child(auth.getUid())
                        .child(course_thumbnail_uniqueKeys)
                        .child("pdf")
                        .child(uniqueKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Name updated success", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

    @Override
    public void deleteClickLister(int position, String uniqueKey) {
        alearDailog.setMessage("Do you want to delete this pdf?");
        alearDailog.setCancelable(true);

        alearDailog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Thanks a lot :) ", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("Admin")
                        .child(auth.getUid())
                        .child("Course")
                        .child(auth.getUid())
                        .child(course_thumbnail_uniqueKeys)
                        .child("pdf")
                        .child(uniqueKey).removeValue();

                Toast.makeText(getActivity(), "pdf Deleted", Toast.LENGTH_SHORT).show();
                arrayList.clear();
                adapters.notifyDataSetChanged();

            }
        });
        alearDailog.show();
    }

    @Override
    public void showpdf(int position, String videoUrl, String uniqueKey) {
        databaseReference.child("Admin")
                .child(auth.getUid())
                .child("Course")
                .child(auth.getUid())
                .child(course_thumbnail_uniqueKeys)
                .child("pdf")
                .child(uniqueKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String videoUrl = snapshot.child("pdfUrl").getValue(String.class);
                            Intent intent = new Intent(getActivity(), pdfViewActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("url",videoUrl);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getContext(), "Video not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error is "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}