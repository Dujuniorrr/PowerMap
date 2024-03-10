package com.ifbaiano.powermap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.cars.AddCarActivity;
import com.ifbaiano.powermap.activity.users.RegisterAdminActivity;
import com.ifbaiano.powermap.adapter.CarAdapter;
import com.ifbaiano.powermap.adapter.UsersAdapter;
import com.ifbaiano.powermap.appearance.MessageHandlerAppearance;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.dao.sqlite.UserDaoSqlite;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.service.CarService;

import java.util.ArrayList;


public class UsersFragment extends Fragment {


    MessageHandlerAppearance errorTagManager;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<User> users;
    UsersAdapter adapter;
    AppCompatActivity mainActivity;
    View rootView;

    public UsersFragment() {
    }

    public static UsersFragment newInstance() {
        return new UsersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        this.mainActivity = (AppCompatActivity) getActivity();
        this.rootView = inflater.inflate(R.layout.fragment_users, container, false);
        errorTagManager = new MessageHandlerAppearance(rootView.findViewById(R.id.errorMessageRecords));
        this.findViewById();
        this.listUsers(R.id.all);
        return rootView;
    }

    public void listUsers(int type){
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        errorTagManager.clearErrorTag();

        new Thread(() -> {
            if(type == R.id.all){
                this.users = new UserDaoFirebase(getContext()).findAll();
            }
            else if(type == R.id.clients){
                this.users = new UserDaoFirebase(getContext()).findAllClients();
            }
            else if(type == R.id.admins){
                this.users = new UserDaoFirebase(getContext()).findAllAdmins();
            }
            mainActivity.runOnUiThread(() -> {
                if(users.size() > 0){
                    mainActivity.runOnUiThread( this::formatRecycleView);
                }else{
                    progressBar.setVisibility(View.GONE);
                    errorTagManager.showErrorTag(getString(R.string.data_user_error));
                }
            });
        }).start();
    }

    public void formatRecycleView(){
        adapter = new UsersAdapter(this.users);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void findViewById(){
        recyclerView = rootView.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity.getApplicationContext()));
        progressBar = rootView.findViewById(R.id.progressBar);

        rootView.findViewById(R.id.addBtn).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RegisterAdminActivity.class));
        });

        ((RadioGroup) rootView.findViewById(R.id.type)).setOnCheckedChangeListener((group, checkedId) -> {
            listUsers(checkedId);
        });


    }
}