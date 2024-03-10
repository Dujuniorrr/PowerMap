package com.ifbaiano.powermap.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.appearance.GenericAppearance;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.Schedule;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.schedule.NotificationSchedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private final List<Schedule> schedules;

    private ScheduleAdapter.CheckClickListener checkClickListner;

    public ScheduleAdapter(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);
        holder.bind(schedule);

        holder.checkButton.setOnClickListener( v -> {
            if (checkClickListner != null) {
                checkClickListner.onCheckClick( holder.getLayoutPosition(), holder.card, schedule);
            }
        });

    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView description, date, repetition;
        AppCompatButton checkButton;
        View card;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView;
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            checkButton = itemView.findViewById(R.id.checkButton);
            repetition = itemView.findViewById(R.id.repetition);
        }

        public void bind(Schedule schedule) {
            description.setText(schedule.getDescription());
            Date dateSchedule = schedule.getDate();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateSchedule);

            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            String time = sdfTime.format(dateSchedule);

            if (schedule.getRepetition() == (int) NotificationSchedule.ONE_DAY) {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                date.setText(sdf.format(dateSchedule) + " - " + time);
                repetition.setText(itemView.getContext().getString(R.string.once));

            } else if (schedule.getRepetition() == (int) NotificationSchedule.WEEK) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE - HH:mm", Locale.getDefault());
                date.setText(GenericAppearance.capitalizedText(sdf.format(dateSchedule)));
                repetition.setText(itemView.getContext().getString(R.string.weekly));

            } else if (schedule.getRepetition() == (int) NotificationSchedule.MONTH) {
                SimpleDateFormat sdf = new SimpleDateFormat("d - HH:mm");
                date.setText("Dia " + sdf.format(dateSchedule));
                repetition.setText(itemView.getContext().getString(R.string.monthly));

            } else if (schedule.getRepetition() == (int) NotificationSchedule.YEAR) {
                SimpleDateFormat sdf = new SimpleDateFormat("d/MM - HH:mm");
                date.setText(sdf.format(dateSchedule));
                repetition.setText(itemView.getContext().getString(R.string.yearly));

            }

        }
    }
    public interface CheckClickListener {
        void onCheckClick(int position, View v, Schedule schedule);
    }

    public void setCheckClickListener(ScheduleAdapter.CheckClickListener listener) {
        this.checkClickListner = listener;
    }

}
