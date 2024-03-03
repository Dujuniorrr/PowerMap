package com.ifbaiano.powermap.activity.carModel;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.EletricCarModelService;
import com.ifbaiano.powermap.service.HybridCarModelService;
import java.util.Objects;

public class AddCarModelActivity extends ActionCarModelBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        submitFormBtn.setOnClickListener(v ->  submitForm());
    }

    public void submitForm() {

        progressBar.setVisibility(View.VISIBLE);
        submitFormBtn.setVisibility(View.GONE);

        boolean verifyValid = false;

        if (type == R.id.eletric) {
            verifyValid = verifier.verifyCarModel(name, year, energyConsumption, submitImgBtn,   bitmapCustomFactory.getByteArray() != null);
        } else if (type == R.id.hybrid) {
            verifyValid = verifier.verifyCarModel(name, year, energyConsumption, submitImgBtn,   bitmapCustomFactory.getByteArray() != null, fuelConsumption);
        }

        if (verifyValid) {
            new Thread(() -> {
                final boolean[] success = {false};
                if (type == R.id.eletric) {
                    success[0] = new EletricCarModelService(eletricCarModelDao, storageDao).add(
                            new EletricCarModel(
                                    null,
                                    Objects.requireNonNull(name.getText()).toString(),
                                    Integer.valueOf(Objects.requireNonNull(year.getText()).toString()),
                                    "",
                                    Float.parseFloat(Objects.requireNonNull(energyConsumption.getText()).toString())
                            ),
                            null,
                            bitmapCustomFactory.getByteArray()
                    );
                } else if (type == R.id.hybrid) {
                    success[0] = new HybridCarModelService(hybridCarModelDao, storageDao).add(
                            new HybridCarModel(
                                    null,
                                    Objects.requireNonNull(name.getText()).toString(),
                                    Integer.valueOf(Objects.requireNonNull(year.getText()).toString()),
                                    "",
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
                        Toast.makeText(AddCarModelActivity.this, R.string.error_data, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        } else {
            progressBar.setVisibility(View.GONE);
            submitFormBtn.setVisibility(View.VISIBLE);
        }
    }

}