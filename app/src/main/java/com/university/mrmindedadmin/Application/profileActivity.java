package com.university.mrmindedadmin.Application;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.ActivityProfileBinding;
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
import java.util.HashMap;

public class profileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private ActivityResultLauncher<String> getMyPhoto;
    private Uri IMAGE_URI;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.color_main));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.color_main));
        }


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating...");
        binding.userProfileRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMyPhoto.launch("image/*");
            }
        });

        getMyPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri imageUri) {
                binding.userProfileRegister.setImageURI(imageUri);
                progressDialog.show();
                storageReference.child("Admin").child(auth.getUid()).child("Profile").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> IMAGE_TASK = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        IMAGE_TASK.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String image_uri = uri.toString();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("profile_image",image_uri);


                                databaseReference.child("Admin").child(auth.getUid()).child("our info").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ///////////// i will use shayerd Prefrence in our database
                                            Toast.makeText(profileActivity.this, "Image updated", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(profileActivity.this, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profileActivity.this, "Image uri not get for you", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                       // long progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        double progress = ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploading " + progress + " %");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profileActivity.this, "error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        getOurProfileData();
        getOurAuthId_IN_APP();

        binding.registersumbitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.userNameEdit.getText().toString().equals("")) {
                    Toast.makeText(profileActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name", binding.userNameEdit.getText().toString());

                    databaseReference.child("Admin").child(auth.getUid()).child("our info").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ///////////// i will use shayerd Prefrence in our database
                                Toast.makeText(profileActivity.this, "Name updated Success", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(profileActivity.this, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        binding.tapToCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String myid = binding.myAuthId.getText().toString();
                ClipData clipData = ClipData.newPlainText("Copied",myid);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(profileActivity.this, "Copied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOurAuthId_IN_APP() {
    binding.myAuthId.setSelected(true);
    binding.myAuthId.setText(auth.getUid());
    binding.myAuthId.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            String myid = binding.myAuthId.getText().toString();
            ClipData clipData = ClipData.newPlainText("Copied",myid);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(profileActivity.this, "Copied", Toast.LENGTH_SHORT).show();
        }
    });
    }

    private void getOurProfileData() {
        databaseReference.child("Admin").child(auth.getUid()).child("our info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String profile = snapshot.child("profile_image").getValue(String.class);
                    binding.userNameEdit.setText(name);
                    Picasso.get().load(profile).into(binding.userProfileRegister);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profileActivity.this, "Error is " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}