package com.ifbaiano.powermap.verifier;


import android.content.Context;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;

public class ScheduleVerifier extends Verifier {

    public ScheduleVerifier(Context ctx) {
        super(ctx);
    }

    public boolean verifySchedule(TextInputEditText date, TextInputEditText time, TextInputEditText desciption, String radioGroup) {
        boolean isValid = true;

        isValid &= validateField(date, R.string.date_required);
        isValid &= validateField(time, R.string.time_required);
        isValid &= validateField(desciption, R.string.description_required);
        isValid &= validateRadio(radioGroup, R.string.radio_required);

        return isValid;
    }

    public boolean validateRadio(String radioGroup, int errorMessageResId) {
        boolean isValid = true;

        if (radioGroup == null || radioGroup.isEmpty()) {
            isValid = false;
            Toast.makeText(getCtx(), getCtx().getString(errorMessageResId), Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

}


