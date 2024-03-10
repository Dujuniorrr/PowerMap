package com.ifbaiano.powermap.verifier;


import android.content.Context;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleVerifier extends Verifier {

    public ScheduleVerifier(Context ctx) {
        super(ctx);
    }

    public boolean verifySchedule(TextInputEditText date, TextInputEditText time, TextInputEditText desciption, String radioGroup) {
        boolean isValid = true;

        isValid &= validateFieldText(date, R.string.date_required);
        isValid &= validateDate(date, R.string.date_future);//data acima da atual
        isValid &= validateDateStandard(date, R.string.date_standard);// data no fotmato correto
        isValid &= validateTimeStandard(time, R.string.time_standard);// time no fotmato correto
        isValid &= validateFieldText(time, R.string.time_required);
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


    public boolean validateDate(TextInputEditText dateText, int errorMessageResId) {
        boolean isValid = true;

        String dateString = dateText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date selectedDate = sdf.parse(dateString);
            Calendar currentDate = Calendar.getInstance();
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.MILLISECOND, 0);

            currentDate.add(Calendar.DAY_OF_MONTH, 1);

            if (selectedDate.before(currentDate.getTime())) {
                dateText.setError(getCtx().getString(errorMessageResId));
                isValid = false;
            } else {
                dateText.setError(null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isValid;
    }



    public boolean validateTimeStandard(TextInputEditText timeText, int errorMessageResId) {
        boolean isValid = true;

        String timeString = timeText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        try {
            sdf.parse(timeString);
        } catch (ParseException e) {
            timeText.setError(getCtx().getString(errorMessageResId));
            isValid = false;
        }

        return isValid;
    }


    public boolean validateDateStandard(TextInputEditText dateText, int errorMessageResId) {
        Context context = dateText.getContext();
        String dateFormat = "dd/MM/yyyy";
        boolean isValid = true;

        String inputDate = dateText.getText().toString().trim();

        if (!inputDate.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            sdf.setLenient(false);

            try {
                sdf.parse(inputDate);
            } catch (ParseException e) {
                isValid = false;
            }

            if (!isValid) {
                dateText.setError(context.getString(errorMessageResId));
            }
        }

        return isValid;
    }



}



