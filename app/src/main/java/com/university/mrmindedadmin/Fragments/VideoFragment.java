package com.university.mrmindedadmin.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.university.mrmindedadmin.Adapters.videoListAdapters;
import com.university.mrmindedadmin.Models.playInterface;
import com.university.mrmindedadmin.Models.slideOnclickInterface;
import com.university.mrmindedadmin.Models.videoListModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.FragmentVideoBinding;
import com.university.mrmindedadmin.player.playerActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoFragment extends Fragment implements slideOnclickInterface, playInterface {

    private FragmentVideoBinding binding;
    private videoListAdapters adapters;
    private ArrayList<videoListModel> arrayList;

    private Dialog dialog;
    private TextView updateName, cancelDailog;
    private EditText pdfNameChanged;
    private AlertDialog.Builder alearDailog;
    private String thumbnail;
    private String course_thumbnail_uniqueKeys;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private ActivityResultLauncher<String> getMyVideo;
    private Uri PDF_URI;
    private ProgressDialog progressDialog;
    private Context context;
    private String pdf_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVideoBinding.inflate(inflater, container, false);

        Bundle message = getArguments();
        course_thumbnail_uniqueKeys = message.getString("u");


        alearDailog = new AlertDialog.Builder(getActivity());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving Data");

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.change_pdf_name);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        updateName = dialog.findViewById(R.id.changePdfName);
        cancelDailog = dialog.findViewById(R.id.cancle);
        pdfNameChanged = dialog.findViewById(R.id.pdf_nameChanged);

        cancelDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        alearDailog.setMessage("Do you want to delete this video?");
        alearDailog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();


        binding.pdfListRecyclerview.setLayoutManager(new LinearLayoutManager(container.getContext()));
        binding.pdfListRecyclerview.setHasFixedSize(true);
        arrayList = new ArrayList<>();
        getOurAllVideos();
        adapters = new videoListAdapters(container.getContext(), arrayList, this);
        binding.pdfListRecyclerview.setAdapter(adapters);


        return binding.getRoot();
    }

    private void getOurAllVideos() {
        databaseReference.child("Admin")
                .child(auth.getUid())
                .child("Course")
                .child(auth.getUid())
                .child(course_thumbnail_uniqueKeys)
                .child("videos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            arrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                videoListModel model = dataSnapshot.getValue(videoListModel.class);
                                model.setVideoUniqueKey(dataSnapshot.getKey());

                                binding.pdfListRecyclerview.setVisibility(View.VISIBLE);
                                binding.coursenotfound.setVisibility(View.GONE);
                                arrayList.add(model);
                            }
                            adapters.notifyDataSetChanged();
                        } else {
                            arrayList.clear();
                            binding.coursenotfound.setVisibility(View.VISIBLE);
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
        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("videoName", pdfNameChanged.getText().toString());

                databaseReference.child("Admin")
                        .child(auth.getUid())
                        .child("Course")
                        .child(auth.getUid())
                        .child(course_thumbnail_uniqueKeys)
                        .child("videos")
                        .child(uniqueKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Name updated success", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void deleteClickLister(int position, String uniqueKey) {
        alearDailog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Thanks a lot :) ", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("Admin")
                        .child(auth.getUid())
                        .child("Course")
                        .child(auth.getUid())
                        .child(course_thumbnail_uniqueKeys)
                        .child("videos")
                        .child(uniqueKey).removeValue();

                Toast.makeText(getActivity(), "video Deleted", Toast.LENGTH_SHORT).show();
                arrayList.clear();
                adapters.notifyDataSetChanged();

            }
        });
        alearDailog.show();
    }

    @Override
    public void playVideo(int position, String videoUrl, String uniqueKey) {
        databaseReference.child("Admin")
                .child(auth.getUid())
                .child("Course")
                .child(auth.getUid())
                .child(course_thumbnail_uniqueKeys)
                .child("videos")
                .child(uniqueKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String videoUrl = snapshot.child("videoUrl").getValue(String.class);
                            String videoName = snapshot.child("videoName").getValue(String.class);

                            Intent intent = new Intent(getActivity(), playerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("url", videoUrl);
                            intent.putExtra("name", videoName);

                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "Video not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error is " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}