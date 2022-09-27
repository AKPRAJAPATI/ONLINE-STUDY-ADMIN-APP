package com.university.mrmindedadmin.Application;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.university.mrmindedadmin.Adapters.deleteCourseAdapters;
import com.university.mrmindedadmin.Models.courseModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.ActivityDeletecourseBinding;
import com.university.mrmindedadmin.deleteClickedEvent;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;

public class deletecourseActivity extends AppCompatActivity implements deleteClickedEvent {
    private ActivityDeletecourseBinding binding;
    private ArrayList<courseModel> course_arrayList;
    private deleteCourseAdapters adapters;
    private AlertDialog.Builder alearDailog;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private EditText courseName;
    private EditText coursePrice;
    private Button updatebtn;
    private Button closebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeletecourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        alearDailog = new AlertDialog.Builder(deletecourseActivity.this);

        progressDialog = new ProgressDialog(deletecourseActivity.this);
        progressDialog.setMessage("wait a sec");
        progressDialog.setCancelable(false);


        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMain.setHasFixedSize(true);
        course_arrayList = new ArrayList<>();
        getOurCourse();
        adapters = new deleteCourseAdapters(getApplicationContext(), course_arrayList, this);
        binding.recyclerViewMain.setAdapter(adapters);


        dialog = new Dialog(deletecourseActivity.this);
        dialog.setContentView(R.layout.rename_course);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);


        courseName = dialog.findViewById(R.id.courseNamee);
        coursePrice = dialog.findViewById(R.id.coursePriceDeletee);
        updatebtn = dialog.findViewById(R.id.updateCoursee);
        closebtn = dialog.findViewById(R.id.canclee);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void getOurCourse() {
        databaseReference.child("Admin").child(auth.getUid()).child("Course").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    course_arrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        courseModel model = dataSnapshot.getValue(courseModel.class);
                        model.setUniquekey(dataSnapshot.getKey());
                        binding.recyclerViewMain.setVisibility(View.VISIBLE);
                        //  binding.coursenotfound.setVisibility(View.GONE);
                        course_arrayList.add(model);
                    }
                    adapters.notifyDataSetChanged();
                } else {
                    course_arrayList.clear();
                    //binding.coursenotfound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void checkItem(String uniqueKey, String thumbnailKey) {
        alearDailog.setMessage("Do you want to delete this course?");
        alearDailog.setCancelable(true);

        alearDailog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(deletecourseActivity.this, "Thanks a lot :) ", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("Admin")
                        .child(auth.getUid())
                        .child("Course")
                        .child(auth.getUid())
                        .child(uniqueKey).removeValue();
                course_arrayList.clear();
                adapters.notifyDataSetChanged();


            }
        });
        alearDailog.show();
    }

    @Override
    public void updateItem(String uniqueKey, String thumbnailKey) {
        dialog.show();
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courname = courseName.getText().toString();
                String coursePri = coursePrice.getText().toString();

                if (courname.equals("")) {
                    courseName.requestFocus();
                    Toast.makeText(deletecourseActivity.this, "Enter course name", Toast.LENGTH_SHORT).show();
                } else if (coursePri.equals("")) {
                    coursePrice.requestFocus();
                    Toast.makeText(deletecourseActivity.this, "Enter course price", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("course_name", courname);
                    hashMap.put("price", Integer.parseInt(coursePri));
                    databaseReference.child("Admin").child(auth.getUid()).child("Course").child(auth.getUid()).child(uniqueKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(deletecourseActivity.this, "Course Update", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }


}