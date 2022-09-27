package com.university.mrmindedadmin.Application.pdf;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


import com.university.mrmindedadmin.Notification.FcmNotificationsSender_abhishek;
import com.university.mrmindedadmin.databinding.ActivityAddPdfBinding;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

public class addPdfActivity extends AppCompatActivity {
    private ActivityAddPdfBinding binding;

    private String uniqueKey;
    private String courseName;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private ActivityResultLauncher<String> getMyVideo;
    private Uri PDF_URI;
    private ProgressDialog progressDialog;

    private FirebaseMessaging messaging;

    private String appName = "Mr. Minded";


    private String pdf_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uniqueKey = getIntent().getStringExtra("uniqueKey");
        courseName = getIntent().getStringExtra("courseName");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving Data");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        messaging.getInstance().subscribeToTopic("all");

        getMyVideo = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @SuppressLint("Range")
            @Override
            public void onActivityResult(Uri result) {
                PDF_URI = result;

                if (PDF_URI.toString().startsWith("content://")) {
                    Cursor cursor = null;
                    cursor = addPdfActivity.this.getContentResolver().query(PDF_URI, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        pdf_name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }

                } else if (PDF_URI.toString().startsWith("file://")) {
                    pdf_name = new File(PDF_URI.toString()).getName();
                }

                binding.pdfName.setText(pdf_name);
            }
        });
        binding.choosePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMyVideo.launch("application/pdf");
            }
        });

        binding.uploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.videoName.getText().toString().equals("")) {
                    binding.videoName.requestFocus();
                    Toast.makeText(addPdfActivity.this, "Pdf name is empty", Toast.LENGTH_SHORT).show();
                } else if (PDF_URI == null) {
                    getMyVideo.launch("application/pdf");
                    Toast.makeText(addPdfActivity.this, "Video Not selected", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    storageReference.child("Pdf").child(auth.getUid() + new Date().getTime()).putFile(PDF_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> VIDEO_TASK = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            VIDEO_TASK.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("pdfUrl", uri.toString());
                                    hashMap.put("clicked", false);
                                    hashMap.put("thumbnail_uniqueKey", uniqueKey);
                                    hashMap.put("pdfName", binding.videoName.getText().toString());

                                    databaseReference.child("Admin")
                                            .child(auth.getUid())
                                            .child("Course")
                                            .child(auth.getUid())
                                            .child(uniqueKey)
                                            .child("pdf")
                                            .push()
                                            .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();

                                                        FcmNotificationsSender_abhishek notificationSender = new FcmNotificationsSender_abhishek("/topics/all", appName, "Course :- " + courseName + "\n" + "Pdf:- " + binding.videoName.getText().toString(), getApplicationContext(), addPdfActivity.this);
                                                        notificationSender.SendNotifications();

                                                        Toast.makeText(addPdfActivity.this, "Pdf Uploaded Success", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            long percentage = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            progressDialog.setMessage(percentage + "%" + " Uploading");
                        }
                    });
                }
            }
        });
    }
}