package com.ifbaiano.powermap.activity.users;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.appearance.StatusBarUtil;
import com.ifbaiano.powermap.databinding.ActivityRegisterAdminBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.fragment.ModelsFragment;

public class RegisterAdminActivity extends AppCompatActivity {

    ActivityRegisterAdminBinding binding;
    DataBindingFactory bindingFactory;
    Button backButonAdminRegiter,RegisterAdminBtn;
    TextInputEditText nameAdminRegister, emailAdminRegister,passwordAdminRegister, passwordConfirmeAdminRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);
        StatusBarUtil.changeStatusBarColor(this, R.color.white);

        this.findViewsById();
        this.doBinding();
    }

    private void findViewsById(){
        backButonAdminRegiter = findViewById(R.id.backButonAdminRegiter);
        RegisterAdminBtn = findViewById(R.id.RegisterAdminBtn);
        nameAdminRegister  = findViewById(R.id.nameAdminRegister);
        emailAdminRegister  = findViewById(R.id.emailAdminRegister);
        passwordAdminRegister  = findViewById(R.id.passwordAdminRegister);
        passwordConfirmeAdminRegister  = findViewById(R.id.passwordConfirmeAdminRegister);
    }

    private void doBinding(){
        bindingFactory = new DataBindingFactory(this, R.id.frameLayoutAdminRegister);
        binding = ActivityRegisterAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindingFactory.replaceFragment(new ModelsFragment());
        binding.bottomNavigationMenuUserTelaAdminRegister.setOnItemSelectedListener(item -> bindingFactory.bindingMenu(item));
    }

}