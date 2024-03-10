package com.ifbaiano.powermap.verifier;


import android.content.Context;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        boolean isValid = false;
        String dateString = dateText.getText().toString().trim();

        // Verifica se a data está no formato dd/MM/yyyy
        Pattern pattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
        Matcher matcher = pattern.matcher(dateString);
        if (matcher.matches()) {
            // Extrai o dia, mês e ano da data
            String[] dateParts = dateString.split("/");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            // Verifica se o dia está dentro do intervalo válido (01-31)
            if (day >= 1 && day <= 31) {
                // Verifica se o mês está dentro do intervalo válido (01-12)
                if (month >= 1 && month <= 12) {
                    // Verifica se o ano é um valor razoável (ajuste conforme necessário)
                    if (year >= 1900 && year <= Calendar.getInstance().get(Calendar.YEAR)) {
                        // Verifica o mês de fevereiro
                        if (month == 2) {
                            // Se for fevereiro, o dia máximo é 28 (considerando anos não bissextos)
                            if (day >= 1 && day <= 28) {
                                isValid = true;
                            } else {
                                dateText.setError(getCtx().getString(errorMessageResId));
                            }
                        } else {
                            isValid = true;
                        }
                    } else {
                        dateText.setError(getCtx().getString(errorMessageResId));
                    }
                } else {
                    dateText.setError(getCtx().getString(errorMessageResId));
                }
            } else {
                dateText.setError(getCtx().getString(errorMessageResId));
            }
        } else {
            dateText.setError(getCtx().getString(errorMessageResId));
        }

        return isValid;
    }



    }



