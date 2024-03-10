package com.ifbaiano.powermap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.schedule.AddScheduleActivity;
import com.ifbaiano.powermap.activity.users.RegisterAdminActivity;
import com.ifbaiano.powermap.adapter.ScheduleAdapter;
import com.ifbaiano.powermap.adapter.UsersAdapter;
import com.ifbaiano.powermap.appearance.MessageHandlerAppearance;
import com.ifbaiano.powermap.appearance.StatusBarAppearance;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
import com.ifbaiano.powermap.dao.sqlite.ScheduleDaoSqlite;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.model.Schedule;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.schedule.NotificationReceiver;
import com.ifbaiano.powermap.schedule.NotificationSchedule;

import java.util.ArrayList;


public class ScheduleFragment extends Fragment implements  ScheduleAdapter.CheckClickListener{

    ProgressBar progressBar;
    MessageHandlerAppearance errorTagManager;
    FloatingActionButton floatingActionbtn;
    AppCompatActivity mainActivity;
    View rootView;
    RecyclerView recyclerView;
    ArrayList<Schedule> schedules;
    ScheduleAdapter adapter;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance(String param1, String param2) {
        return new ScheduleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mainActivity =  (AppCompatActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        StatusBarAppearance.changeStatusBarColor(mainActivity, R.color.sub_background_form);
        errorTagManager = new MessageHandlerAppearance(rootView.findViewById(R.id.errorMessageRecords));
        this.findViewsById();
        this.listSchedules();
        return rootView;
    }

    public void listSchedules(){
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        errorTagManager.clearErrorTag();

        new Thread(() -> {
            schedules = new ScheduleDaoSqlite(mainActivity.getApplicationContext()).findByUserId(UserFactory.getUserInMemory(mainActivity).getId());
            mainActivity.runOnUiThread(() -> {
                if(schedules.size() > 0){
                    formatRecycleView();
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    errorTagManager.showErrorTag(mainActivity.getString(R.string.data_schedule_error));
                }

            });
        }).start();
    }

    public void formatRecycleView(){
        adapter = new ScheduleAdapter(this.schedules);
        adapter.setCheckClickListener(this);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void findViewsById(){
        floatingActionbtn =  rootView.findViewById(R.id.floatingActionbtn);
        recyclerView = rootView.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity.getApplicationContext()));
        progressBar = rootView.findViewById(R.id.progressBar);

        floatingActionbtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onCheckClick(int position, View v, Schedule schedule) {
        new Thread(() -> {
            boolean success = new ScheduleDaoSqlite(mainActivity).remove(schedule);
            NotificationSchedule.cancelScheduledNotification(mainActivity, Integer.parseInt(schedule.getId()));

            if(success){
                mainActivity.runOnUiThread(() -> {
                    schedules.remove(position);
                    adapter.notifyItemRemoved(position);
                    if(schedules.size() < 1){
                        errorTagManager.showErrorTag(mainActivity.getString(R.string.data_schedule_error));
                    }
                });
            }
            else{
                mainActivity.runOnUiThread(() -> {
                    Toast.makeText(mainActivity, R.string.error_data, Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}