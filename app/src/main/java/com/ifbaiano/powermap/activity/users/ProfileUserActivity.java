package com.ifbaiano.powermap.activity.users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.car.ListCarActivity;
import com.ifbaiano.powermap.databinding.ActivityProfileUserBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.fragment.CarFragment;

public class ProfileUserActivity extends AppCompatActivity {

    Button submitEditImageProfile,editProfileUserBtn, logouProfile;
    ImageView imageEditProfil;
    TextInputEditText nameEditProfile, emailEditProfile, passwordEditProfile;
    ActivityProfileUserBinding binding;
    DataBindingFactory bindingFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        this.doBinding();
        this.findViewsById();

        logouProfile.setOnClickListener(v -> {
                Intent intent;
                intent = new Intent(ProfileUserActivity.this, ListCarActivity.class);
                startActivity(intent);
         });
    }

    private void findViewsById(){
        submitEditImageProfile = findViewById(R.id.submitEditImageProfile);
        editProfileUserBtn = findViewById(R.id.editProfileUserBtn);
        logouProfile = findViewById(R.id.logouProfile);
        imageEditProfil = findViewById(R.id.imageEditProfil);
        nameEditProfile = findViewById(R.id.nameEditProfile);
        emailEditProfile = findViewById(R.id.emailEditProfile);
        passwordEditProfile = findViewById(R.id.passwordEditProfile);

    }


    private void doBinding(){
        bindingFactory = new DataBindingFactory(this, R.id.frameLayoutUser);
        binding = ActivityProfileUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindingFactory.replaceFragment(new CarFragment());
        binding.bottomNavigationMenuUser.setOnItemSelectedListener(item -> bindingFactory.bindingMenu(item));
    }


}