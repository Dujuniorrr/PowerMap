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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        sdf.setLenient(false); // Impede que a SimpleDateFormat aceite horas e minutos inválidos.

        try {
            Date parsedTime = sdf.parse(timeString);
            if (parsedTime != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedTime);

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                if (hour > 23 || minute > 59) {
                    isValid = false;
                    timeText.setError(getCtx().getString(errorMessageResId));
                }
            }
        } catch (ParseException e) {
            isValid = false;
            timeText.setError(getCtx().getString(errorMessageResId));
        }

        return isValid;
    }



    public boolean validateDateStandard(TextInputEditText dateText, int errorMessageResId) {
        String dateString = dateText.getText().toString().trim();

        if (!dateString.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            dateText.setError(getCtx().getString(errorMessageResId));
            return false;
        }

        String[] dateParts = dateString.split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);

        if ((day < 1 || day > 31) || (month < 1 || month > 12)) {
            dateText.setError(getCtx().getString(errorMessageResId));
            return false;
        }

        if (month == 2 && (day < 1 || day > 28)) {
            dateText.setError(getCtx().getString(errorMessageResId));
            return false;
        }

        return true;
    }



}



