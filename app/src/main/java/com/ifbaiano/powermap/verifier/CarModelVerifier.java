package com.ifbaiano.powermap.verifier;

import android.content.Context;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;

import java.time.Year;
import java.util.Objects;


public class CarModelVerifier extends Verifier {

    public CarModelVerifier(Context ctx) {
        super(ctx);
    }

    public boolean verifyCarModel(TextInputEditText name, TextInputEditText year, TextInputEditText energyConsumption, AppCompatButton imgBtn, boolean hasImg) {
        boolean isValid = true;

        isValid = validateField(name, R.string.name_error);
        isValid &= validateField(year, R.string.year_error);
        isValid &= validateYear(year, R.string.wrong_year_error);
        isValid &= validateMinYear(year, R.string.wrong_year_error);
        isValid &= validateMinConsumption(energyConsumption, R.string.energy_consumption_error );
        isValid &= validateField(energyConsumption, R.string.energy_consumption_error);
        isValid &= validateImage(hasImg, imgBtn, R.string.image_error);

        return isValid;
    }

    public boolean verifyCarModel(TextInputEditText name, TextInputEditText year, TextInputEditText energyConsumption,  AppCompatButton imgBtn, Boolean hasImg, TextInputEditText fuelEnergyConsumption) {
        boolean isValid = true;
        isValid = this.verifyCarModel(
                name, year, energyConsumption, imgBtn, hasImg
        );
        isValid &= validateMinConsumption(fuelEnergyConsumption, R.string.energy_consumption_error );
        isValid &= validateField(fuelEnergyConsumption, R.string.fuel_consumption_error);
        return isValid;
    }

    public boolean validateYear(TextInputEditText year, int errorMessageResId) {
        if (!year.getText().toString().isEmpty()) {
            int inputYear = Integer.valueOf(year.getText().toString());
            int currentYear = Year.now().getValue();
            if (inputYear > currentYear) {
                year.setError(this.getCtx().getString(errorMessageResId));
                return false;
            }
        }
        return true;
    }
    public boolean validateMinYear(TextInputEditText year, int errorMessageResId) {
        if (!year.getText().toString().isEmpty()) {
            int inputYear = Integer.valueOf(year.getText().toString());
            if (inputYear < 1900) {
                year.setError(this.getCtx().getString(errorMessageResId));
                return false;
            }
        }
        return true;
    }

    public boolean validateMinConsumption(TextInputEditText year, int errorMessageResId) {
        if (!year.getText().toString().isEmpty()) {
            float value = Float.valueOf(year.getText().toString());
            if (value < 0.1) {
                year.setError(this.getCtx().getString(errorMessageResId));
                return false;
            }
        }
        return true;
    }


    protected boolean validateImage(boolean field, AppCompatButton imgBtn, int errorMessageResId){
        if (!field){
            imgBtn.setBackgroundResource(R.drawable.button_submit_image_red);
//            Toast.makeText(ctx, this.getCtx().getString(errorMessageResId), Toast.LENGTH_SHORT).show();
        }
        else{
            imgBtn.setBackgroundResource(R.drawable.button_submit_image);
        }

        return field;
    }

}
