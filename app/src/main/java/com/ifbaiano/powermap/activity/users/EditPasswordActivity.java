package com.ifbaiano.powermap.activity.users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.databinding.ActivityEditPasswordBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.fragment.ModelsFragment;

public class EditPasswordActivity extends AppCompatActivity {

    Button backButonProfilePassword, editProfileUserBtn;
    ImageView imageProfilEditPassword;
    TextInputEditText editPasswordText, editPasswordConfirmText;
    ActivityEditPasswordBinding binding;
    DataBindingFactory bindingFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        this.findViewsById();
        this.doBinding();

        backButonProfilePassword.setOnClickListener(v -> {
                    Intent intent = new Intent(EditPasswordActivity.this, ProfileUserActivity.class);
                    startActivity(intent);
                }
        );
    }


    private void findViewsById() {
        backButonProfilePassword = findViewById(R.id.backButonProfilePassword);
        editProfileUserBtn = findViewById(R.id.editProfileUserBtn);
        imageProfilEditPassword = findViewById(R.id.imageProfilEditPassword);
        editPasswordText = findViewById(R.id.editPasswordText);
        editPasswordConfirmText = findViewById(R.id.editPasswordConfirmText);
    }

    private void doBinding(){
        bindingFactory = new DataBindingFactory(this, R.id.frameLayoutUser);
        binding = ActivityEditPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindingFactory.replaceFragment(new ModelsFragment());
        binding.bottomNavigationMenuUser.setOnItemSelectedListener(item -> bindingFactory.bindingMenu(item));
    }

}