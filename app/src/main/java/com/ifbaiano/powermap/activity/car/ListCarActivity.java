package com.ifbaiano.powermap.activity.car;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.databinding.ActivityListCarBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.fragment.CarFragment;

public class ListCarActivity extends AppCompatActivity {

    ActivityListCarBinding binding;
    DataBindingFactory bindingFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_car);
        this.doBinding();
    }

    private void doBinding(){
        bindingFactory = new DataBindingFactory(this, R.id.frameLayoutUser);
        binding = ActivityListCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindingFactory.replaceFragment(new CarFragment());
        binding.bottomNavigationMenuUser.setOnItemSelectedListener(item -> bindingFactory.bindingMenu(item));
    }



}