package com.ifbaiano.powermap.activity.schedule;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterButton;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MenuActivity;
import com.ifbaiano.powermap.appearance.StatusBarAppearance;
import com.ifbaiano.powermap.dao.sqlite.ScheduleDaoSqlite;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.model.Schedule;
import com.ifbaiano.powermap.schedule.NotificationSchedule;
import com.ifbaiano.powermap.verifier.ScheduleVerifier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddScheduleActivity extends AppCompatActivity {

     String TITLE_NOTIFICATION;
    ImageFilterButton dateButton, timeButton;

    RadioButton checkMonth,checkOnce, checkWeek,checkYearly;
    GridLayout typeSchedule;
    Button btnCancelSchedule, btnAddSchedule;
    TextInputEditText textDecriptionSchhedule;
    TextView  textDate, textTime;

    private long selectedScheduleType = NotificationSchedule.ONE_DAY, selectedWeekday;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        StatusBarAppearance.changeStatusBarColor(this, R.color.black);
        ScheduleVerifier verifier = new ScheduleVerifier(this);

        this.findViewById();

        TITLE_NOTIFICATION = getString(R.string.title_notification);
        StatusBarAppearance.changeStatusBarColor(this, R.color.black);

        dateButton.setOnClickListener(v -> {
            showDatePickerDialog();
        });


        timeButton.setOnClickListener(v -> {
            showTimePickerDialog();
        });

        btnAddSchedule.setOnClickListener(v -> {

            if (verifier.verifySchedule(textDate, textTime, textDecriptionSchhedule,  Long.toString(selectedScheduleType))) {
                String dateString = textDate.getText().toString();
                String timeString = textTime.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                try {
                    Date parsedDate = sdf.parse(dateString + " " + timeString);
                    Schedule schedule = new ScheduleDaoSqlite(this.getApplicationContext()).add(new Schedule(
                             null,
                            parsedDate,
                            textDecriptionSchhedule.getText().toString(),
                            (int) selectedWeekday,
                            (int) selectedScheduleType
                    ), UserFactory.getUserInMemory(this.getApplicationContext()).getId());

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(parsedDate);
                    calendar.add(Calendar.HOUR_OF_DAY, -3);

                   NotificationSchedule notificationSchedule =  new NotificationSchedule(Integer.parseInt(schedule.getId()));
                   notificationSchedule.scheduleRepeatingNotification(
                           this.getApplicationContext(),
                           calendar.get(Calendar.DAY_OF_WEEK),
                           calendar.get(Calendar.HOUR_OF_DAY),
                           calendar.get(Calendar.MINUTE),
                           selectedScheduleType,
                           TITLE_NOTIFICATION,
                           schedule.getDescription()
                   );

                    backIntent();


                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "erro", Toast.LENGTH_SHORT).show();

            }

        });
    }
//Espaço de metodos

    private void backIntent(){
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("fragment", "schedule");
        startActivity(intent);
    }
    private void findViewById(){
        findViewById(R.id.backButonSchedule).setOnClickListener(v -> {
            backIntent();
        });

        findViewById(R.id.btnCancelSchedule).setOnClickListener(v -> {
            backIntent();
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
                this,R.style.TimePickerDialogStyle,
                (view, year1, month1, dayOfMonth1) -> {

                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth1);
                    selectedWeekday = selectedDate.get(Calendar.DAY_OF_WEEK);

                    String selectedDateStr = String.format("%02d/%02d/%d", dayOfMonth1, (month1 + 1), year1);
                    textDate.setText(selectedDateStr);

                },
                year, month, dayOfMonth);
        Calendar tomorrow = Calendar.getInstance();
//        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
//        datePickerDialog.getDatePicker().setMinDate(tomorrow.getTimeInMillis());

        datePickerDialog.show();
    }


    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,R.style.DatePickerDialogStyle,
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
                selectedScheduleType = NotificationSchedule.ONE_DAY;
            } else if (view.getId() == R.id.checkWeek) {
                selectedScheduleType = NotificationSchedule.WEEK;
            } else if (view.getId() == R.id.checkMonth) {
                selectedScheduleType = NotificationSchedule.MONTH;
            } else if (view.getId() == R.id.checkYearly) {
                selectedScheduleType = NotificationSchedule.YEAR;
            }
        }

}



}