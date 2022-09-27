package com.university.mrmindedadmin.Application;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.storage.StorageReference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.university.mrmindedadmin.Settings.SettingsActivity;
import com.university.mrmindedadmin.authentication.RegisterActivity;
import com.university.mrmindedadmin.databinding.ActivityUserDetailBinding;
import com.squareup.picasso.Picasso;

public class UserDetailActivity extends AppCompatActivity {
    private ActivityUserDetailBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private AlertDialog.Builder alearDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getOurProfileData();

        alearDailog = new AlertDialog.Builder(UserDetailActivity.this);

        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });
        binding.gotoSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });

        binding.myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_to_profileActivity();
            }
        });
        binding.uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_to_uploadVideo();
            }
        });

        binding.gotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_to_profileActivity();
            }
        });
        binding.gotoUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go_to_uploadVideo();

            }
        });

        alearDailog.setMessage("Do you want to logout?");
        alearDailog.setCancelable(false);

        alearDailog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(UserDetailActivity.this, "Thanks a lot :) ", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                auth.signOut();
                startActivity(new Intent(UserDetailActivity.this, RegisterActivity.class));
                finish();
            }
        });
        binding.gotoLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alearDailog.show();
            }
        });
        binding.logoutSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             alearDailog.show();
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error is "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void go_to_uploadVideo() {
        Intent intent = new Intent(getApplicationContext(), create_upVi_playlist_Activity.class);
        startActivity(intent);
    }

    private void go_to_profileActivity() {
        Intent intent = new Intent(getApplicationContext(),profileActivity.class);
        startActivity(intent);
    }
}