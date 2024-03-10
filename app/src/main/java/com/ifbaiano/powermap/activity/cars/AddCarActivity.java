package com.ifbaiano.powermap.activity.cars;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MenuActivity;

import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.dao.media.StorageDaoMedia;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.dao.sqlite.EletricCarModelDaoSqlite;
import com.ifbaiano.powermap.dao.sqlite.HybridCarModelDaoSqlite;

import com.ifbaiano.powermap.factory.CarFactory;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.CarService;
import com.ifbaiano.powermap.service.EletricCarModelService;
import com.ifbaiano.powermap.service.HybridCarModelService;
import com.ifbaiano.powermap.verifier.CarVerifier;

import java.util.Objects;

public class AddCarActivity extends ActionCarBase{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void submitForm(){

        errorTagManager.clearErrorTag();
        submitForm.setVisibility(View.GONE);
        progressBarSubmit.setVisibility(View.VISIBLE);
        boolean isValid = new CarVerifier(getApplicationContext()).verifyCar(name, selectedCarModel.getId() != null);
        new Thread(() -> {
            if(isValid){
                try{
                    Car car = new CarService(new CarDaoSqlite(getApplicationContext())).add(
                            new Car(Objects.requireNonNull(name.getText()).toString()),
                            UserFactory.getUserInMemory(getApplicationContext()).getId()
                    );

                    byte[] imageBytes = new StorageDaoFirebase().getImageAsByteArray(selectedCarModel.getPathImg());
                    StorageDaoMedia storageDaoMedia = new StorageDaoMedia(getApplicationContext());

                    if(selectedCarModel instanceof HybridCarModel){
                        new HybridCarModelService(
                                new HybridCarModelDaoSqlite(getApplicationContext()),
                                storageDaoMedia).add((HybridCarModel) selectedCarModel, car.getId(), imageBytes );
                    }
                    else if(selectedCarModel instanceof EletricCarModel){
                        new EletricCarModelService(
                                new EletricCarModelDaoSqlite(getApplicationContext()),
                                storageDaoMedia).add((EletricCarModel) selectedCarModel, car.getId(), imageBytes );
                    }

                    if(CarFactory.getCarInMemory(getApplicationContext()).getId() == null){
                        CarFactory.saveCarInMemory(car, getApplicationContext());
                    }

                    startActivity(new Intent(this, MenuActivity.class));
                }
                catch (Exception e){
                    runOnUiThread(() -> {
                        submitForm.setVisibility(View.VISIBLE);
                        progressBarSubmit.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.error_data), Toast.LENGTH_SHORT).show();
                    });
                }
            }
            else{
                runOnUiThread(() -> {
                    submitForm.setVisibility(View.VISIBLE);
                    progressBarSubmit.setVisibility(View.GONE);
                });
            }
        }).start();
    }

}