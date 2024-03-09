package com.ifbaiano.powermap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.cars.AddCarActivity;
import com.ifbaiano.powermap.adapter.CarAdapter;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.factory.CarFactory;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.service.CarService;
import java.util.ArrayList;


public class CarFragment extends Fragment  implements  CarAdapter.DeleteClickListener {

    AppCompatButton addBtn;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<Car> cars;
    CarAdapter adapter;
    CarService carService;
    AppCompatActivity mainActivity;
    View rootView;


    public CarFragment() {
    }

    public static CarFragment newInstance() {
        return new CarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.cars = new CarDaoSqlite(getActivity()).findAll();
        this.mainActivity = (AppCompatActivity) getActivity();
        this.rootView = inflater.inflate(R.layout.fragment_car, container, false);

        this.findViewById();
        this.makeInstances();
        this.formatRecycleView();
        this.listCars();
        addBtn.setOnClickListener(v -> {
            startActivity(new Intent(mainActivity.getApplicationContext(), AddCarActivity.class));
        });
        return rootView;
    }

    private void listCars(){

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            cars = carService.findAll();
            mainActivity.runOnUiThread( this::formatRecycleView);
        }).start();
    }

    private void findViewById(){
        recyclerView = rootView.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity.getApplicationContext()));
        progressBar = rootView.findViewById(R.id.progressBar);
        addBtn = rootView.findViewById(R.id.addBtn);
    }

    private void makeInstances(){
        carService = new CarService(new CarDaoSqlite(mainActivity.getApplicationContext()));
    }

    public void formatRecycleView(){
        adapter = new CarAdapter(cars, mainActivity.getApplicationContext(), false);
        adapter.setDeleteClickListener(CarFragment.this);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeleteClick(int position, View v, Car car) {
        new Thread(() -> {
            Car carInMemory = CarFactory.getCarInMemory(mainActivity);
            if(carInMemory.getId() != null && carInMemory.getId().equals(car.getId())){
                CarFactory.clearUserInMemory(mainActivity);
            }
            if ( carService.remove(car)){
                mainActivity.runOnUiThread(() -> {
                    cars.remove(position);
                    adapter.notifyItemRemoved(position);
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