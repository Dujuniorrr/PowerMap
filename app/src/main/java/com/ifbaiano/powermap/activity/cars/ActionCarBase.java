package com.ifbaiano.powermap.activity.cars;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MenuActivity;
import com.ifbaiano.powermap.adapter.ModelCarAdapter;
import com.ifbaiano.powermap.appearance.CarModelAppearence;
import com.ifbaiano.powermap.dao.firebase.EletricCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.HybridCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.EletricCarModelService;
import com.ifbaiano.powermap.service.HybridCarModelService;

import java.util.ArrayList;

abstract public class ActionCarBase extends AppCompatActivity implements ModelCarAdapter.OnClickListener {

    TextInputEditText name;
    AppCompatButton  submitForm;
    ProgressBar progressBar, progressBarSubmit;
    RecyclerView recyclerView;
    ArrayList<CarModel> carModels;
    ModelCarAdapter adapter;
    HybridCarModelService hybridCarModelService;
    EletricCarModelService eletricCarModelService;
    CarModel selectedCarModel = new CarModel();
    CarModelAppearence carModelAppearence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_car);
        this.findViewById();
        this.makeInstances();

        this.listModels();

        submitForm.setOnClickListener(v -> {
            submitForm();
        });
    }

    abstract void submitForm();

    private void makeInstances(){
        hybridCarModelService = new HybridCarModelService(
                new HybridCarModelDaoFirebase(getApplicationContext()),
                new StorageDaoFirebase());

        eletricCarModelService = new EletricCarModelService(
                new EletricCarModelDaoFirebase(getApplicationContext()),
                new StorageDaoFirebase());
    }

    private void findViewById(){
        findViewById(R.id.backButon).setOnClickListener(v -> {
            startActivity(new Intent(this, MenuActivity.class));
        });

        name = findViewById(R.id.name);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);
        submitForm = findViewById(R.id.submitForm);
        progressBarSubmit = findViewById(R.id.progressBarSubmit);
    }

    public void listModels(){

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            setModelList();

            runOnUiThread( this::formatRecycleView);
        }).start();
    }


    public void setModelList(){
        carModels = new ArrayList<>();
        ArrayList<EletricCarModel> eletricCarModels = this.eletricCarModelService.listAll();
        ArrayList<HybridCarModel> hybridModels = this.hybridCarModelService.listAll();

        if(eletricCarModels != null){
            carModels.addAll(eletricCarModels);
        }

        if(hybridModels != null){
            carModels.addAll(hybridModels);
        }
    }

    public void formatRecycleView(){
        adapter = new ModelCarAdapter(carModels, getApplicationContext(), true);
        adapter.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        carModelAppearence = new CarModelAppearence(
                adapter,
                recyclerView,
                getApplicationContext()
        );
    }

    @Override
    public void onClick(int position, View v, CarModel carModel) {
        carModelAppearence.restorePreviousClickedItem();

        carModelAppearence.setClickedItemColors(v);

        selectedCarModel = carModel;
        adapter.setPreviousClickedIndex(position);

    }

}
