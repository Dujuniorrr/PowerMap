package com.ifbaiano.powermap.activity.schedule;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.TextView;
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
    TextView textDate, textTime;
    RadioButton checkMonth,checkOnce, checkWeek,checkYearly;
    GridLayout typeSchedule;
    Button btnCancelSchedule, btnAddSchedule;
    TextInputEditText textDecriptionSchhedule;

    private String selectedScheduleType;

    private  String selectedWeekday;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        StatusBarAppearance.changeStatusBarColor(this, R.color.black);
        ScheduleVerifier verifier = new ScheduleVerifier(this);

        this.findViewById();

        final String TITLE_NOTIFICATION = getString(R.string.title_notification);
        StatusBarAppearance.changeStatusBarColor(this, R.color.black);

        dateButton.setOnClickListener(v -> {
            showDatePickerDialog();
        });


        timeButton.setOnClickListener(v -> {
            showTimePickerDialog();
        });


        btnAddSchedule.setOnClickListener(v -> {
            if (verifier.verifySchedule(textDate, textTime, textDecriptionSchhedule,  selectedScheduleType)) {
                String date = textDate.getText().toString();
                String time = textTime.getText().toString();
                String description = textDecriptionSchhedule.getText().toString();
                String weekday =  selectedWeekday.toString();
                String repetition = selectedScheduleType.toString();
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
                (view, year1, month1, dayOfMonth1) -> {

                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth1);
                    int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);

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
                    String selectedDateStr = String.format("%02d/%02d/%d", dayOfMonth1, (month1 + 1), year1);
                    textDate.setText(selectedDateStr);
                    selectedWeekday = weekday;
                    Toast.makeText(AddScheduleActivity.this, selectedWeekday, Toast.LENGTH_SHORT).show();
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
                (view, hourOfDay1, minute1) -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    String selectedTime = formatter.format(LocalTime.of(hourOfDay1, minute1));
                    textTime.setText(selectedTime);
                },
                hourOfDay, minute, true);

        timePickerDialog.show();
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        checkOnce.setChecked(false);
        checkWeek.setChecked(false);
        checkMonth.setChecked(false);
        checkYearly.setChecked(false);

        ((RadioButton) view).setChecked(checked);

        if (checked) {
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