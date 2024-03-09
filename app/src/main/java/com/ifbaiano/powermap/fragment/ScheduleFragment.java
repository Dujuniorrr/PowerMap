package com.ifbaiano.powermap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.users.AddScheduleActivity;
import com.ifbaiano.powermap.appearance.StatusBarAppearance;


public class ScheduleFragment extends Fragment {

    FloatingActionButton floatingActionbtn;
    AppCompatActivity mainActivity;
    View rootView;




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
        // Inflate the layout for this fragment
        this.mainActivity =  (AppCompatActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        StatusBarAppearance.changeStatusBarColor(mainActivity, R.color.sub_background_form);


        this.findViewsById();

        floatingActionbtn.setOnClickListener(v -> {
            // Redirect to MainActivity
            Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    private void findViewsById() {
       floatingActionbtn =  rootView.findViewById(R.id.floatingActionbtn);

    }
}