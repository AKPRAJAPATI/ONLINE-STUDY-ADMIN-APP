package com.university.mrmindedadmin.Application.video;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
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

import com.university.mrmindedadmin.Adapters.addVideo_Adapters;
import com.university.mrmindedadmin.Models.courseModel;
import com.university.mrmindedadmin.Notification.FcmNotificationsSender_abhishek;
import com.university.mrmindedadmin.databinding.ActivityAddVideoBinding;
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
import java.util.Date;
import java.util.HashMap;

public class addVideoAct extends AppCompatActivity {

    private ActivityAddVideoBinding binding;
    private ArrayList<courseModel> course_arrayList;
    private addVideo_Adapters adapters;

    private String uniqueKey;
    private String courseName;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private ActivityResultLauncher<String> getMyVideo;
    private Uri VIDEO_URI;
    private ProgressDialog progressDialog;
    private boolean payment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uniqueKey = getIntent().getStringExtra("uniqueKey");
        courseName = getIntent().getStringExtra("courseName");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving Data");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        getMyVideo = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri filepath) {
                VIDEO_URI = filepath;

                binding.addVideoView.setVideoURI(filepath);
                binding.addVideoView.start();

                binding.uploadVideoAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (VIDEO_URI == null) {
                            getMyVideo.launch("video/*");
                            Toast.makeText(addVideoAct.this, "Video Not selected", Toast.LENGTH_SHORT).show();
                        } else if (binding.videoName.getText().toString().equals("")){
                            binding.videoName.requestFocus();
                            Toast.makeText(addVideoAct.this, "Please take your video name", Toast.LENGTH_SHORT).show();
                        }else {
                            progressDialog.show();
                            storageReference.child("Videos").child(auth.getUid() + new Date().getTime()).putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> VIDEO_TASK = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    VIDEO_TASK.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("videoUrl", uri.toString());
                                            hashMap.put("clicked",false);
                                            hashMap.put("videoName", binding.videoName.getText().toString());

                                            databaseReference.child("Admin")
                                                    .child(auth.getUid())
                                                    .child("Course")
                                                    .child(auth.getUid())
                                                    .child(uniqueKey)
                                                    .child("videos")
                                                    .push()
                                                    .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                String appName = "Mr. Minded";

                                                                FcmNotificationsSender_abhishek notificationSender = new FcmNotificationsSender_abhishek("/topics/all",appName,"Course :- "+courseName+"\n"+"Video :- "+binding.videoName.getText().toString(),getApplicationContext(),addVideoAct.this);
                                                                notificationSender.SendNotifications();
                                                                progressDialog.dismiss();
                                                                Toast.makeText(addVideoAct.this, "Video Uploaded Success", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    long progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                    progressDialog.setMessage(progress + " % " + " Uploading");
                                }
                            });
                        }
                    }
                });
            }
        });
        binding.chooseVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMyVideo.launch("video/*");
            }
        });

    }
}

