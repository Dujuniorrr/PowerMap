package com.ifbaiano.powermap.verifier;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import com.ifbaiano.powermap.R;

public class CalculateConsumptionVerifier extends  Verifier{

    public CalculateConsumptionVerifier(Context ctx) {
        super(ctx);
    }

    public boolean verifyFields(EditText distance, EditText currentEnergy){
        boolean isValid = true;
        isValid = validateField(distance, R.string.distance_error);
        isValid &= validateField(currentEnergy, R.string.energy_error);

        return isValid;
    }

    public boolean verifyFields(EditText distance, EditText currentEnergy, EditText currentFuel) {
        boolean isValid;
        isValid = this.verifyFields(
                distance, currentEnergy);
        isValid &= validateField(currentFuel, R.string.fuel_error);
        return isValid;
    }

    protected boolean validateField(EditText field, int errorMessageResId) {
        if (TextUtils.isEmpty(field.getText())) {
            field.setError(this.getCtx().getString(errorMessageResId));
            return false;
        }
        return true;
    }

}
