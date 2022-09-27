package com.university.mrmindedadmin.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.university.mrmindedadmin.Application.MainActivity;
import com.university.mrmindedadmin.Models.registerModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    /////////// FIREBASE /////////////////
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    /////////// LOCAL VARIABLE //////////
    private Uri IMAGE_URI;
    private String image_uri;
    private int GALLERY_REQUEST_CODE = 100;
    private ProgressDialog progressDialog, progressDialogRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.color_main));
        }


        progressDialogRegister = new ProgressDialog(this);
        progressDialogRegister.setCancelable(false);
        progressDialogRegister.setMessage("Register Account");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving Data");

        ///////////////GET INSTANCE OF OUR DATABASE///////////////
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

///////////////////////////////////////////////MAIN WORK START HERE//////////////////////////////////////////////////
        binding.userProfileRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        binding.loginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        binding.registersumbitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.userNameEdit.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Name is empty", Toast.LENGTH_SHORT).show();
                } else if (binding.userEmailEdit.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                } else if (!binding.userEmailEdit.getText().toString().contains("@gmail.com")) {
                    Toast.makeText(RegisterActivity.this, "Email is wrong", Toast.LENGTH_SHORT).show();
                } else if (binding.userPasswordEdit.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                } else if (binding.userPasswordEdit.getText().toString().length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Password more than 8 character", Toast.LENGTH_SHORT).show();
                } else if (binding.userConfirmPasswordEdit.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Confirm password is empty", Toast.LENGTH_SHORT).show();
                } else if (!binding.userPasswordEdit.getText().toString().equals(binding.userConfirmPasswordEdit.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Password no matches", Toast.LENGTH_SHORT).show();
                } else if (IMAGE_URI == null) {
                    Toast.makeText(RegisterActivity.this, "Please select your Image", Toast.LENGTH_SHORT).show();
                    openGallery();
                } else {
                    progressDialogRegister.show();
                    auth.createUserWithEmailAndPassword(binding.userEmailEdit.getText().toString(), binding.userPasswordEdit.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialogRegister.dismiss();
                                progressDialog.show();
                                saveMyData();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialogRegister.dismiss();
                            Toast.makeText(RegisterActivity.this, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void saveMyData() {
        storageReference.child("Admin").child(auth.getUid()).child("Profile").putFile(IMAGE_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> IMAGE_TASK = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                IMAGE_TASK.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        image_uri = uri.toString();
                        registerModel model = new registerModel
                                (
                                        image_uri,
                                        binding.userNameEdit.getText().toString(),
                                        binding.userEmailEdit.getText().toString(),
                                        binding.userPasswordEdit.getText().toString(),
                                         auth.getUid()
                                );

                        databaseReference.child("Admin").child(auth.getUid()).child("our info").setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ///////////// i will use shayerd Prefrence in our database
                                    Toast.makeText(RegisterActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Error is " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Image uri not get for you", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RegisterActivity.this, "error is "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //////////onpen gallery/////////////
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE || resultCode == RESULT_OK) {
            IMAGE_URI = data.getData();
            binding.userProfileRegister.setImageURI(IMAGE_URI);
        }
    }
    /////////////////close work //////////////////
}