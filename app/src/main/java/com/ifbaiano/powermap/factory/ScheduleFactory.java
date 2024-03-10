package com.ifbaiano.powermap.factory;

import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import com.ifbaiano.powermap.model.Schedule;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class ScheduleFactory {

    public static Schedule createByCursor(Cursor cursor){
        return new Schedule(
                cursor.getString(cursor.getColumnIndexOrThrow("id")),
                castStringToDate(
                        cursor.getString(cursor.getColumnIndexOrThrow("date"))
                ),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getInt(cursor.getColumnIndexOrThrow("dayOfWeek")),
                cursor.getInt(cursor.getColumnIndexOrThrow("repetition"))
        );
    }

    private static Date castStringToDate(String dateString){

        long timestamp = Long.parseLong(dateString);
        Date date = new Date(timestamp);
        return date;
    }
}
