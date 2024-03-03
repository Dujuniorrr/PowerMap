package com.ifbaiano.powermap.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.cars.ListCarActivity;
import com.ifbaiano.powermap.activity.users.InitialUsersActivity;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_TIMEOUT = 2000;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        @SuppressLint({"MissingInflateId","LocalSuppress"})
        ImageView img_logo = findViewById(R.id.gifImageLogo);

        Glide.with(this)
                .load(R.drawable.splash_logo).into(img_logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    //Toast.makeText(MainActivity.this, "Usuário já logado", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(MainActivity.this, ListCarActivity.class);
                    startActivity(it);
                    finish();
                } else {

                    Intent intent = new Intent(MainActivity.this, InitialUsersActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIMEOUT);





    }
}