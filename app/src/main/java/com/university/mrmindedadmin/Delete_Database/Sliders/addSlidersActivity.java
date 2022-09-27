package com.university.mrmindedadmin.Delete_Database.Sliders;

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

import com.university.mrmindedadmin.Models.slidersModel;
import com.university.mrmindedadmin.databinding.ActivityAddSlidersBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class addSlidersActivity extends AppCompatActivity {
    private ActivityAddSlidersBinding binding;
    private ActivityResultLauncher<String> get_ourResult;
    private Uri IMAGE_URI;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSlidersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("upload Sliders");
        progressDialog.setCancelable(false);

        binding.chooseSliderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_ourResult.launch("image/*");
            }
        });
        get_ourResult = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri myUri) {
                binding.sliderImage.setImageURI(myUri);
                IMAGE_URI = myUri;
                binding.chooseSliderImage.setText("Change Image");
            }
        });

        binding.uploadSliders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IMAGE_URI == null)
                {
                    Toast.makeText(addSlidersActivity.this, "Please select any image", Toast.LENGTH_SHORT).show();
                    get_ourResult.launch("image/*");
                }else{
                    progressDialog.show();

                    storageReference.child("Sliders/").child(auth.getUid()+new Date().getTime()).putFile(IMAGE_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> IMAGE_TASK = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            IMAGE_TASK.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String img_uri = uri.toString();
                                    slidersModel obj = new slidersModel();
                                    obj.setSlidersImg(img_uri);
                                    databaseReference.child("Admin").child(auth.getUid()).child("Sliders").push().setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                binding.chooseSliderImage.setText("Choose Image");
                                                Toast.makeText(addSlidersActivity.this, "Sliders Added", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot downloder) {
                            long progress = (100 * downloder.getBytesTransferred()) / downloder.getTotalByteCount();
                            progressDialog.setMessage("Uploading "+progress+"%" +" Completed!");
                        }
                    });
                }
            }
        });


    }
}