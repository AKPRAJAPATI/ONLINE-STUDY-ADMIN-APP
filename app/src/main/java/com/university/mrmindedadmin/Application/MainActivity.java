package com.university.mrmindedadmin.Application;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import com.university.mrmindedadmin.Adapters.mainAdapters;
import com.university.mrmindedadmin.Models.courseModel;
import com.university.mrmindedadmin.authentication.RegisterActivity;
import com.university.mrmindedadmin.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private List<SlideModel> arrayList;
    private ArrayList<courseModel> course_arrayList;
    private mainAdapters adapters;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseMessaging.getInstance().subscribeToTopic("all");
         getOurProfileData();

        binding.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserDetailActivity.class));
            }
        });

        arrayList = new ArrayList<>();
        getOurSliders(arrayList);


        binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMain.setHasFixedSize(true);
        course_arrayList = new ArrayList<>();
        getOurCourse();
        adapters = new mainAdapters(getApplicationContext(), course_arrayList);
        binding.recyclerViewMain.setAdapter(adapters);

        }

    private void getOurSliders(List<SlideModel> arrayList) {
        databaseReference.child("Admin").child(auth.getUid()).child("Sliders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              arrayList.clear();
              for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                  String image_data = dataSnapshot.child("slidersImg").getValue(String.class);
                  arrayList.add(new SlideModel(image_data , ScaleTypes.CENTER_CROP));
                  binding.imageSlider.setImageList(arrayList);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getOurProfileData() {
        databaseReference.child("Admin").child(auth.getUid()).child("our info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name  = snapshot.child("name").getValue(String.class);
                    String profile = snapshot.child("profile_image").getValue(String.class);
                    binding.userNameLogo.setText(name);
                    Picasso.get().load(profile).into(binding.userProfileAppLogo);

                    binding.shimmerLayout.stopShimmer();
                    binding.mainLayout.setVisibility(View.VISIBLE);
                    binding.shimmerLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error is "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                        binding.mainLayout.setVisibility(View.VISIBLE);
                        binding.coursenotfound.setVisibility(View.GONE);
                        binding.shimmerLayout.setVisibility(View.GONE);
                        course_arrayList.add(model);

                    }
                    adapters.notifyDataSetChanged();
                } else {
                    course_arrayList.clear();
                    binding.shimmerLayout.setVisibility(View.GONE);
                    binding.mainLayout.setVisibility(View.VISIBLE);
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