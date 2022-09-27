package com.university.mrmindedadmin.Delete_Database.Sliders;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.university.mrmindedadmin.Delete_Database.Adapters.slidersAdapters;
import com.university.mrmindedadmin.Models.slideOnclickInterface;
import com.university.mrmindedadmin.Models.slidersModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.ActivityDeleteSlidersBinding;
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

public class deleteSlidersActivity extends AppCompatActivity implements slideOnclickInterface {
    private ActivityDeleteSlidersBinding binding;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private LinearLayoutManager mLinearLayoutManager;
    private ProgressDialog progressDialog;

    private Dialog dialog;
    private Uri IMAGE_URI;

    private ArrayList<slidersModel> arrayList;
    private slidersAdapters updateSlideAdapters;

    private TextView chooseImage, updateImages;
    private ImageView imageView;

    private ActivityResultLauncher<String> getOurImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteSlidersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), addSlidersActivity.class));
            }
        });

        dialog = new Dialog(deleteSlidersActivity.this);
        dialog.setContentView(R.layout.update_sliders);

        chooseImage =   dialog.findViewById(R.id.chooseImageBtn);
        updateImages = dialog.findViewById(R.id.updateBtn);
        imageView = dialog.findViewById(R.id.imageAk);

        getOurImages = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri ImageUri) {
                imageView.setImageURI(ImageUri);
                IMAGE_URI = ImageUri;
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Slide");
        progressDialog.setCancelable(false);

        binding.updateSlidesRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.updateSlidesRecyclerview.setHasFixedSize(true);
        arrayList = new ArrayList<>();
        updateSlideAdapters = new slidersAdapters(getApplicationContext(), arrayList, this);
        getOurAllSliders();
        binding.updateSlidesRecyclerview.setAdapter(updateSlideAdapters);





    }

    private void getOurAllSliders() {
        databaseReference.child("Admin").child(auth.getUid()).child("Sliders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    arrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        slidersModel model = dataSnapshot.getValue(slidersModel.class);
                        model.setUniqueKey(dataSnapshot.getKey());
                        arrayList.add(model);
                    }
                    updateSlideAdapters.notifyDataSetChanged();
                } else {
                    Toast.makeText(deleteSlidersActivity.this, "Sliders not found yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void updateClickLister(int position, String uniqueKey) {
        dialog.show();
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOurImages.launch("image/*");
                Toast.makeText(deleteSlidersActivity.this, "Image chooses", Toast.LENGTH_SHORT).show();
            }
        });
        updateImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IMAGE_URI == null)
                {
                    Toast.makeText(deleteSlidersActivity.this, "Please select any image", Toast.LENGTH_SHORT).show();
                    getOurImages.launch("image/*");
                }else{
                    progressDialog.show();

                    storageReference.child("Sliders/").child(auth.getUid()+new Date().getTime()).putFile(IMAGE_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> IMAGE_TASK = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            IMAGE_TASK.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String , Object> hashMap = new HashMap<>();
                                    hashMap.put("slidersImg",uri.toString());
                                    databaseReference.child("Admin").child(auth.getUid()).child("Sliders").child(uniqueKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                dialog.dismiss();

                                                Toast.makeText(deleteSlidersActivity.this, "Sliders Updated", Toast.LENGTH_SHORT).show();
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

    @Override
    public void deleteClickLister(int position, String uniqueKey) {
        databaseReference.child("Admin").child(auth.getUid()).child("Sliders").child(uniqueKey).removeValue();
        arrayList.clear();
        updateSlideAdapters.notifyDataSetChanged();
        Toast.makeText(this, "Sliders Deleted Success", Toast.LENGTH_SHORT).show();
    }
}