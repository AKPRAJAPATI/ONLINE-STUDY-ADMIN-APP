package com.university.mrmindedadmin.Application;

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

import com.university.mrmindedadmin.databinding.ActivityUploadVideoCreatePlaylistBinding;
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

public class create_upVi_playlist_Activity extends AppCompatActivity {

    private ActivityUploadVideoCreatePlaylistBinding binding;
   private ActivityResultLauncher<String> getMyResult;
    private Uri IMAGE_VIEW;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    private int price = 0;
    private boolean payment = false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadVideoCreatePlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("create a course");
       progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();;
        storageReference = FirebaseStorage.getInstance().getReference();
        binding.coursePrice.setText(String.valueOf(00));


        binding.chooseVideoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openGallery();
                getMyResult.launch("image/*");
            }
        });
        getMyResult= registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri image) {
                binding.courseThumbnailUV.setImageURI(image);
                IMAGE_VIEW = image;
            }
        });
        binding.uploadThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if (binding.courseName.getText().toString().equals("")){
                     Toast.makeText(create_upVi_playlist_Activity.this, "Course name is empty", Toast.LENGTH_SHORT).show();
                 }else if (IMAGE_VIEW == null){
                     Toast.makeText(create_upVi_playlist_Activity.this, "Image Thumbnail is null", Toast.LENGTH_SHORT).show();
                     Toast.makeText(create_upVi_playlist_Activity.this, "Please select your thumbnail", Toast.LENGTH_LONG).show();
                     getMyResult.launch("image/*");
                 }else{

                     progressDialog.show();

                     storageReference.child("Thumbnails/").child(auth.getUid()+new Date().getTime()).putFile(IMAGE_VIEW).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             Task<Uri> IMAGE_TASK = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                             IMAGE_TASK.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                     String imageuri = uri.toString();
                                     HashMap<String, Object> hashMap = new HashMap<>();
                                     hashMap.put("thumbnail",imageuri);
                                      hashMap.put("launched",false);
                                     hashMap.put("course_name",binding.courseName.getText().toString());
                                     hashMap.put("price", Integer.parseInt(binding.coursePrice.getText().toString()));

                                     databaseReference.child("Admin").child(auth.getUid()).child("Course").child(auth.getUid()).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful())
                                         {
                                             progressDialog.dismiss();

                                             Toast.makeText(create_upVi_playlist_Activity.this, "Course Created Success", Toast.LENGTH_SHORT).show();
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
                     });
                 }
            }
        });

        getOurProfileData();

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
}