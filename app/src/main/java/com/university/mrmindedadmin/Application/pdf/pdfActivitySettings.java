package com.university.mrmindedadmin.Application.pdf;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.university.mrmindedadmin.Adapters.add_Pdf_Adapters;
import com.university.mrmindedadmin.Models.courseModel;
import com.university.mrmindedadmin.databinding.ActivityPdfSettingsBinding;

import java.util.ArrayList;

public class pdfActivitySettings extends AppCompatActivity {
    private ActivityPdfSettingsBinding binding;
    private ArrayList<courseModel> course_arrayList;
    private add_Pdf_Adapters adapters;

    private String uniqueKey;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private ActivityResultLauncher<String> getMyVideo;
    private Uri VIDEO_URI;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uniqueKey = getIntent().getStringExtra("uniqueKey");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving Data");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();



        binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMain.setHasFixedSize(true);
        course_arrayList = new ArrayList<>();
        getOurCourse();
        adapters = new add_Pdf_Adapters(getApplicationContext(), course_arrayList);
        binding.recyclerViewMain.setAdapter(adapters);

    }
    private void getOurCourse() {
        databaseReference.child("Admin").child(auth.getUid()).child("Course").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    course_arrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        courseModel model = dataSnapshot.getValue(courseModel.class);
                        model.setUniquekey(dataSnapshot.getKey());

                        binding.recyclerViewMain.setVisibility(View.VISIBLE);
                        binding.coursenotfound.setVisibility(View.GONE);
                        course_arrayList.add(model);
                    }
                    adapters.notifyDataSetChanged();
                } else {
                    course_arrayList.clear();
                    binding.coursenotfound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}