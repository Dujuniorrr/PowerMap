package com.ifbaiano.powermap.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.users.InitialUsersActivity;
import com.ifbaiano.powermap.appearance.GenericAppearance;
import com.ifbaiano.powermap.appearance.StatusBarAppearance;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.verifier.LoginVerifier;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_TIMEOUT = 2000;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarAppearance.changeStatusBarColor(this, R.color.white);
        LoginVerifier loginVerifier = new LoginVerifier(this);

        @SuppressLint({"MissingInflateId","LocalSuppress"})
        ImageView img_logo = findViewById(R.id.gifImageLogo);

        Glide.with(this)
                .load(R.drawable.splash_logo).into(img_logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    Toast.makeText(MainActivity.this, getString(R.string.welcome)+" "+ currentUser.getDisplayName()+ " !", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Se não estiver logado com o Google, verifica usando SharedPreferences
                    if (loginVerifier.isUserLogged()) {
                        String name = UserFactory.getUserInMemory(getApplicationContext()).getName();
                        String nameUp = GenericAppearance.capitalizedText(name);

                        Toast.makeText(MainActivity.this, getString(R.string.welcome)+" "+ nameUp + " !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, InitialUsersActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }
        }, SPLASH_TIMEOUT);


    }
}