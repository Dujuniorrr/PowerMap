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
import com.ifbaiano.powermap.activity.carModels.AddCarModelActivity;
import com.ifbaiano.powermap.activity.carModels.EditCarModelActivity;
import com.ifbaiano.powermap.activity.cars.AddCarActivity;
import com.ifbaiano.powermap.activity.cars.EditCarActivity;
import com.ifbaiano.powermap.adapter.CarAdapter;
import com.ifbaiano.powermap.adapter.ModelCarAdapter;
import com.ifbaiano.powermap.dao.firebase.EletricCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.HybridCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.dao.media.StorageDaoMedia;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.CarService;
import com.ifbaiano.powermap.service.EletricCarModelService;
import com.ifbaiano.powermap.service.HybridCarModelService;

import java.util.ArrayList;


public class CarFragment extends Fragment  implements CarAdapter.EditClickListener, CarAdapter.DeleteClickListener {

    LayoutInflater inflater;
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

    @Override
    public void onEditClick(View v, Car car) {
        Intent intent = new Intent(mainActivity, EditCarActivity.class);
        intent.putExtra("car", car);
        startActivity(intent);
    }

    public void formatRecycleView(){
        adapter = new CarAdapter(cars, mainActivity.getApplicationContext(), false);
        adapter.setEditClickListener(CarFragment.this);
        adapter.setDeleteClickListener(CarFragment.this);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeleteClick(int position, View v, Car car) {
        new Thread(() -> {
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