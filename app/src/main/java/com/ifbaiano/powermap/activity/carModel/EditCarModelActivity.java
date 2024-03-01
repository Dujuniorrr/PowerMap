package com.ifbaiano.powermap.activity.carModel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.EletricCarModelService;
import com.ifbaiano.powermap.service.HybridCarModelService;

import java.util.Objects;

public class EditCarModelActivity extends ActionCarModelBase {
    CarModel carModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.putValuesInEditText();
        this.putImage();

        submitFormBtn.setHint(R.string.edit);
        submitFormBtn.setOnClickListener(v -> {
            submitForm();
        });
    }
    private void putImage(){
        new StorageDaoFirebase().transformInBitmap(carModel.getPathImg(), imageView, null);
    }

    @SuppressLint("SetTextI18n")
    private void putValuesInEditText(){
        carModel = (CarModel) getIntent().getSerializableExtra("carModel");
        assert carModel != null;
        name.setText(carModel.getName());
        year.setText(Integer.toString(carModel.getYear()));
        energyConsumption.setText(Float.toString(((EletricCarModel) carModel).getEnergyConsumption()));
        if(carModel instanceof  HybridCarModel) fuelConsumption.setText(Float.toString(((HybridCarModel) carModel).getFuelConsumption()));
        if(!(carModel instanceof HybridCarModel)){
            RadioButton eletricRadioButton = findViewById(R.id.eletric);
            eletricRadioButton.setChecked(true);
            onRadioButtonClicked(eletricRadioButton);
        }
        this.configRadioButtons();
    }

    private void configRadioButtons(){
        findViewById(R.id.eletric).setEnabled(false);
        findViewById(R.id.hybrid).setEnabled(false);
    }
    private void submitForm() {

        progressBar.setVisibility(View.VISIBLE);
        submitFormBtn.setVisibility(View.GONE);

        boolean verifyValid = false;

        if (type == R.id.eletric) {
            verifyValid = verifier.verifyCarModel(name, year, energyConsumption, submitImgBtn,   true);
        } else if (type == R.id.hybrid) {
            verifyValid = verifier.verifyCarModel(name, year, energyConsumption, submitImgBtn,   true, fuelConsumption);
        }

        if (verifyValid) {
            new Thread(() -> {
                final boolean[] success = {false};
                if (type == R.id.eletric) {
                    success[0] = new EletricCarModelService(eletricCarModelDao, storageDao).edit(
                            new EletricCarModel(
                                    carModel.getId(),
                                    Objects.requireNonNull(name.getText()).toString(),
                                    Integer.valueOf(Objects.requireNonNull(year.getText()).toString()),
                                    carModel.getPathImg(),
                                    Float.parseFloat(Objects.requireNonNull(energyConsumption.getText()).toString())
                            ),
                            null,
                            bitmapCustomFactory.getByteArray()
                    );
                } else if (type == R.id.hybrid) {
                    success[0] = new HybridCarModelService(hybridCarModelDao, storageDao).edit(
                            new HybridCarModel(
                                    carModel.getId(),
                                    Objects.requireNonNull(name.getText()).toString(),
                                    Integer.valueOf(Objects.requireNonNull(year.getText()).toString()),
                                    carModel.getPathImg(),
                                    Float.parseFloat(Objects.requireNonNull(energyConsumption.getText()).toString()),
                                    Float.parseFloat(Objects.requireNonNull(fuelConsumption.getText()).toString())
                            ),
                            null,
                            bitmapCustomFactory.getByteArray()
                    );
                }

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    submitFormBtn.setVisibility(View.VISIBLE);

                    if (success[0]) {
                        backActivity();
                    } else {
                        Toast.makeText(this, R.string.error_data, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        } else {
            progressBar.setVisibility(View.GONE);
            submitFormBtn.setVisibility(View.VISIBLE);
        }
    }


}