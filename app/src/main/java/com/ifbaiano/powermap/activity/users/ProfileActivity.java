package com.ifbaiano.powermap.activity.users;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.databinding.ActivityProfileBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.fragment.ProfileFragment;


public class ProfileActivity extends AppCompatActivity {
    DataBindingFactory bindingFactory;
    ActivityProfileBinding binding;
    Button editPasswordProfileBtn, editProfileBtn;
    TextInputEditText  editProfileUser, editEmailUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editPasswordProfileBtn = findViewById(R.id.editPasswordProfileBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        editProfileUser = findViewById(R.id.editProfileUser);
        editEmailUser = findViewById(R.id.editEmailUser);
        this.doBinding();

    }

    private void doBinding(){
        bindingFactory = new DataBindingFactory(this, R.id.frameLayoutUserProfile);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindingFactory.replaceFragment(new ProfileFragment());
        binding.bottomNavigationMenuProfile.setOnItemSelectedListener(item -> bindingFactory.bindingMenu(item));

    }
}