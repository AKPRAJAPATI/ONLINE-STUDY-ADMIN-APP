package com.university.mrmindedadmin.Application;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import com.university.mrmindedadmin.Adapters.MyTabAdapter;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.ActivityDetailBinding;
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
import java.util.Date;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String thumbnail;
    private String uniquekey;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private ActivityResultLauncher<String> getOurImage;
    Uri IMAGE_URI;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Wait a sec");
        getOurProfileData();

        thumbnail = getIntent().getStringExtra("imageThumbnail");
        uniquekey = getIntent().getStringExtra("uniqueKey");
        //Toast.makeText(this, ""+uniquekey, Toast.LENGTH_SHORT).show();
        Picasso.get().load(thumbnail).into(binding.courseThumbnail);


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Videos"));
        tabLayout.addTab(tabLayout.newTab().setText("Pdf"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        Bundle bundle = new Bundle();
        bundle.putString("u", uniquekey);


        final MyTabAdapter adapter = new MyTabAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount(), bundle);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.changeThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    openGallery();
            }
        });
    }

    private void openGallery() {
        Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
        openGallery.setType("image/*");
        startActivityForResult(openGallery,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 || resultCode==RESULT_OK){
            IMAGE_URI = data.getData();
            binding.courseThumbnail.setImageURI(IMAGE_URI);

            progressDialog.show();
            storageReference.child("Thumbnails/").child(auth.getUid()+new Date().getTime()).putFile(IMAGE_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> imagetask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    imagetask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("thumbnail", uri.toString());
                            databaseReference.child("Admin").child(auth.getUid()).child("Course").child(auth.getUid()).child(uniquekey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(DetailActivity.this, "Thumbnail updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    long progress = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploading "+progress+"%");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailActivity.this, "error is "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void getOurProfileData() {
        databaseReference.child("Admin").child(auth.getUid()).child("our info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String profile = snapshot.child("profile_image").getValue(String.class);
                    binding.userNameLogo.setText(name);
                    Picasso.get().load(profile).into(binding.userProfileAppLogo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error is " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}