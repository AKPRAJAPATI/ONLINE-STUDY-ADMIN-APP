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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.university.mrmindedadmin.Application.MainActivity;
import com.university.mrmindedadmin.databinding.ActivityLoginBinding;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Login Account");

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),forgotpasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        binding.registersumbitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.userEmailEdit.getText().toString().equals("")) {
                    binding.userEmailEdit.requestFocus();
                    binding.userEmailEdit.setError("Enter your email like this abc@gmail.com");
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else if (!binding.userEmailEdit.getText().toString().contains("@gmail.com")) {
                    binding.userEmailEdit.requestFocus();
                    binding.userEmailEdit.setError("Email is wrong");
                    Toast.makeText(LoginActivity.this, "Email is wrong", Toast.LENGTH_SHORT).show();
                } else if (binding.userPasswordEdit.getText().toString().equals("")) {
                    binding.userPasswordEdit.requestFocus();
                    binding.userPasswordEdit.setError("Enter your password");
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                } else if (binding.userPasswordEdit.getText().toString().length() < 8) {
                    binding.userPasswordEdit.requestFocus();
                    binding.userPasswordEdit.setError("Password more than 8 character");
                    Toast.makeText(LoginActivity.this, "Password more than 8 character", Toast.LENGTH_SHORT).show();
                } else {
                    LoginAccount(binding.userEmailEdit.getText().toString(),binding.userPasswordEdit.getText().toString());
                }
            }
        });
        binding.registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void LoginAccount(String email, String password) {
        progressDialog.show();
    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
            }else{
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Account not find", Toast.LENGTH_SHORT).show();
            }
        }
    });
    }
}