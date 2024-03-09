package com.ifbaiano.powermap.activity.schedule;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterButton;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MenuActivity;
import com.ifbaiano.powermap.appearance.StatusBarAppearance;
import com.ifbaiano.powermap.verifier.ScheduleVerifier;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {

    ImageFilterButton dateButton, timeButton;
    TextInputEditText textDate, textTime;
    RadioButton checkMonth,checkOnce, checkWeek,checkYearly;
    GridLayout typeSchedule;
    Button btnCancelSchedule, btnAddSchedule;
    TextInputEditText textDecriptionSchhedule;

    private String selectedScheduleType;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        StatusBarAppearance.changeStatusBarColor(this, R.color.black);
        ScheduleVerifier verifier = new ScheduleVerifier(this);

        this.findViewById();

        final String TITLE_NOTIFICATION = getString(R.string.title_notification);

        dateButton.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        timeButton.setOnClickListener(v -> {
            showTimePickerDialog();
        });


        btnAddSchedule.setOnClickListener(v -> {
            if (verifier.verifySchedule(textDate, textTime, textDecriptionSchhedule,  selectedScheduleType)) {
                Toast.makeText(this, "verificação feita", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Prencha todos os campos", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void findViewById(){
        findViewById(R.id.backButonSchedule).setOnClickListener(v -> {
            startActivity(new Intent(this, MenuActivity.class));
        });

        findViewById(R.id.btnCancelSchedule).setOnClickListener(v -> {
            startActivity(new Intent(this, MenuActivity.class));
        });

        textDate = findViewById(R.id.textDate);
        textTime = findViewById(R.id.textTime);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        textDecriptionSchhedule= findViewById(R.id.textDecriptionSchhedule);

        checkOnce = findViewById(R.id.checkOnce);
        checkWeek = findViewById(R.id.checkWeek);
        checkMonth = findViewById(R.id.checkMonth);
        checkYearly = findViewById(R.id.checkYearly);

        checkOnce.setOnClickListener(this::onRadioButtonClicked);
        checkWeek.setOnClickListener(this::onRadioButtonClicked);
        checkMonth.setOnClickListener(this::onRadioButtonClicked);
        checkYearly.setOnClickListener(this::onRadioButtonClicked);
        typeSchedule = findViewById(R.id.typeSchedule);

        btnCancelSchedule = findViewById(R.id.btnCancelSchedule);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                // Aplicando o estilo personalizado (opcional)
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Pegando o dia da semana
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);
                        // Formatando a data e o dia da semana
                        String weekday = "";
                        switch (dayOfWeek) {
                            case Calendar.SUNDAY:
                                weekday = "Domingo";
                                break;
                            case Calendar.MONDAY:
                                weekday = "Segunda-feira";
                                break;
                            case Calendar.TUESDAY:
                                weekday = "Terça-feira";
                                break;
                            case Calendar.WEDNESDAY:
                                weekday = "Quarta-feira";
                                break;
                            case Calendar.THURSDAY:
                                weekday = "Quinta-feira";
                                break;
                            case Calendar.FRIDAY:
                                weekday = "Sexta-feira";
                                break;
                            case Calendar.SATURDAY:
                                weekday = "Sábado";
                                break;
                        }
                        String selectedDateStr = String.format("%02d/%02d/%d", dayOfMonth, (month + 1), year);
                        textDate.setText(selectedDateStr);
                        String selectedWeekday = weekday;
                        Toast.makeText(AddScheduleActivity.this, selectedWeekday, Toast.LENGTH_SHORT).show();
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }


    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                 // Aplicando o estilo personalizado
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                        String selectedTime = formatter.format(LocalTime.of(hourOfDay, minute));
                        textTime.setText(selectedTime);
                    }
                },
                hourOfDay, minute, true);

        timePickerDialog.show();
    }


    public void onRadioButtonClicked(View view) {
        // Checar qual RadioButton foi clicado
        boolean checked = ((RadioButton) view).isChecked();

        // Desmarcar todos os outros RadioButtons
        checkOnce.setChecked(false);
        checkWeek.setChecked(false);
        checkMonth.setChecked(false);
        checkYearly.setChecked(false);

        // Mudar o estado do RadioButton clicado para "true"
        ((RadioButton) view).setChecked(checked);

        if (checked) {
            // Defina a variável selectedScheduleType com base no RadioButton clicado
            if (view.getId() == R.id.checkOnce) {
                selectedScheduleType = "Once";
            } else if (view.getId() == R.id.checkWeek) {
                selectedScheduleType = "Weekly";
            } else if (view.getId() == R.id.checkMonth) {
                selectedScheduleType = "Monthly";
            } else if (view.getId() == R.id.checkYearly) {
                selectedScheduleType = "Yearly";
            }
        }
}



}