package com.ifbaiano.powermap.activity.cars;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.dao.sqlite.EletricCarModelDaoSqlite;
import com.ifbaiano.powermap.databinding.ActivityListCarBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.fragment.CarFragment;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.EletricCarModel;

import java.util.ArrayList;

public class ListCarActivity extends AppCompatActivity {

    ActivityListCarBinding binding;
    DataBindingFactory bindingFactory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_car);
        this.doBinding();

        ArrayList<EletricCarModel> models = new EletricCarModelDaoSqlite(getApplicationContext()).findAll();
        ArrayList<Car> cars = new CarDaoSqlite(getApplicationContext()).findAll();
        if(models != null){
            for(EletricCarModel model: models){
                Log.d("CAR TEST", model.getName());
            }
        }

        if(cars != null){
            for(Car car: cars){
                Log.d("CAR TEST", car.getName());
                Log.d("CAR TEST", car.getCarModel().getId());
                Log.d("CAR TEST", car.getCarModel().getPathImg());
            }
        }
    }

    private void doBinding(){
        bindingFactory = new DataBindingFactory(this, R.id.frameLayoutUser);
        binding = ActivityListCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindingFactory.replaceFragment(new CarFragment());
        binding.bottomNavigationMenuUser.setOnItemSelectedListener(item -> bindingFactory.bindingMenu(item));
    }

}