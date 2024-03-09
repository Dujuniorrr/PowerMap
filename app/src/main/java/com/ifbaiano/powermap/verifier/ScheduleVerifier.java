package com.ifbaiano.powermap.verifier;


import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleVerifier extends Verifier {

    public ScheduleVerifier(Context ctx) {
        super(ctx);
    }

    public boolean verifySchedule(TextView date, TextView time, TextInputEditText desciption, String radioGroup) {
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


    public boolean validateDate(TextView dateText, int errorMessageResId) {
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

            if (selectedDate.before(currentDate.getTime())) {
                dateText.setError(getCtx().getString(errorMessageResId));
                isValid = false;
            }
            else{
                dateText.setError(null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    public boolean validateTime(TextView timeText, int errorMessageResId) {
        boolean isValid = true;

        String timeString = timeText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        try {
            Date selectedTime = sdf.parse(timeString);
            Calendar currentTime = Calendar.getInstance();

            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentTime.get(Calendar.MINUTE);

            Calendar selectedTimeCal = Calendar.getInstance();
            selectedTimeCal.setTime(selectedTime);

            int selectedHour = selectedTimeCal.get(Calendar.HOUR_OF_DAY);
            int selectedMinute = selectedTimeCal.get(Calendar.MINUTE);

            if (selectedHour < currentHour || (selectedHour == currentHour && selectedMinute < currentMinute)) {
                timeText.setError(getCtx().getString(errorMessageResId));
                isValid = false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isValid;
    }

    protected boolean validateField(TextView field, int errorMessageResId) {
        if (TextUtils.isEmpty(field.getText())) {
            field.setError(this.getCtx().getString(errorMessageResId));
            return false;
        }
        return true;
    }
}



