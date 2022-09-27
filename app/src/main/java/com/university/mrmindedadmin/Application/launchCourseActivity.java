package com.university.mrmindedadmin.Application;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.university.mrmindedadmin.Adapters.launchAdapters;
import com.university.mrmindedadmin.Models.courseModel;
import com.university.mrmindedadmin.databinding.ActivityLaunchCourseBinding;

import java.util.ArrayList;

public class launchCourseActivity extends AppCompatActivity {
    private ActivityLaunchCourseBinding binding;
    private ArrayList<courseModel> course_arrayList;
    private launchAdapters adapters;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLaunchCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMain.setHasFixedSize(true);
        course_arrayList = new ArrayList<>();
        getOurCourse();
        adapters = new launchAdapters(getApplicationContext(), course_arrayList);
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
                    binding.recyclerViewMain.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}