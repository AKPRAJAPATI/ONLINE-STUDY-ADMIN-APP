package com.university.mrmindedadmin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.university.mrmindedadmin.Application.MainActivity;
import com.university.mrmindedadmin.authentication.RegisterActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartingActivity extends AppCompatActivity {

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.circleImageView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        }, 3000);


    }
}