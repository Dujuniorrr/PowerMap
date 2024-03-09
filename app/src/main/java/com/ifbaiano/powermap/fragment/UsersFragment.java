package com.ifbaiano.powermap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.cars.AddCarActivity;
import com.ifbaiano.powermap.adapter.CarAdapter;
import com.ifbaiano.powermap.adapter.UsersAdapter;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.dao.sqlite.UserDaoSqlite;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.service.CarService;

import java.util.ArrayList;


public class UsersFragment extends Fragment {


    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<User> user;
    UsersAdapter adapter;
    CarService carService;
    AppCompatActivity mainActivity;
    View rootView;


    public UsersFragment() {
        // Required empty public constructor
    }

    public static UsersFragment newInstance() {
        return new UsersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        this.user = new UserDaoSqlite(getContext()).findAllClients();
        this.mainActivity = (AppCompatActivity) getActivity();
        this.rootView = inflater.inflate(R.layout.fragment_users, container, false);
        /*
        this.findViewById();
        this.makeInstances();
        this.formatRecycleView();
        this.listCars();
        btnReturn.setOnClickListener(v -> {
            startActivity(new Intent(mainActivity.getApplicationContext(), AddCarActivity.class));
        }); */
        return rootView;

    }

    private void findViewById(){
        recyclerView = rootView.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity.getApplicationContext()));
        progressBar = rootView.findViewById(R.id.progressBar);
        btnReturn = rootView.findViewById(R.id.addBtn);
    }
}