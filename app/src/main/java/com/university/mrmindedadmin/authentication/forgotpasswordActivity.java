package com.university.mrmindedadmin.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;

import com.university.mrmindedadmin.databinding.ActivityForgotpasswordBinding;


public class forgotpasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    ActivityForgotpasswordBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotpasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Email");
        progressDialog.setCancelable(false);
        binding.registersumbitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performForgetPassword();
            }
        });
    }

    public void performForgetPassword() {
        if (binding.userEmailEdit.getText().toString().equals("")) {
            binding.userEmailEdit.requestFocus();
            binding.userEmailEdit.setError("Enter your email like this abc@gmail.com");
            Toast.makeText(forgotpasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
        } else if (!binding.userEmailEdit.getText().toString().contains("@gmail.com")) {
            binding.userEmailEdit.requestFocus();
            binding.userEmailEdit.setError("Email is wrong");
            Toast.makeText(forgotpasswordActivity.this, "Email is wrong", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.show();
         auth.sendPasswordResetEmail(binding.userEmailEdit.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){
                     Toast.makeText(forgotpasswordActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(getApplicationContext(),forgotpasswordActivity.class);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     startActivity(intent);
                     progressDialog.dismiss();
                 }else{
                     progressDialog.dismiss();
                     Toast.makeText(forgotpasswordActivity.this, "Error is "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                 }
             }
         });
        }
    }
}