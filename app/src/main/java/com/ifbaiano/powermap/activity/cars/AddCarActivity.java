package com.ifbaiano.powermap.activity.cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.carModels.ListCarModelsActivity;
import com.ifbaiano.powermap.adapter.ModelCarAdapter;
import com.ifbaiano.powermap.appearance.CarModelAppearence;
import com.ifbaiano.powermap.dao.firebase.EletricCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.HybridCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.databinding.ActivityAddCarBinding;
import com.ifbaiano.powermap.databinding.ActivityListCarBinding;
import com.ifbaiano.powermap.databinding.ActivityListCarModelsBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.fragment.CarFragment;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.EletricCarModelService;
import com.ifbaiano.powermap.service.HybridCarModelService;
import com.ifbaiano.powermap.verifier.CarModelVerifier;
import com.ifbaiano.powermap.verifier.CarVerifier;

import java.util.ArrayList;

public class AddCarActivity extends AppCompatActivity implements ModelCarAdapter.OnClickListener {

    TextInputEditText name;
    @NonNull ActivityAddCarBinding binding;
    DataBindingFactory bindingFactory;
    AppCompatButton addBtn;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<CarModel> carModels;
    ModelCarAdapter adapter;
    HybridCarModelService hybridCarModelService;
    EletricCarModelService eletricCarModelService;
    CarModel seletedCarModel = new CarModel();
    CarModelAppearence carModelAppearence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        this.doBinding();
        this.findViewById();
        this.makeInstances();
        listModels();
        findViewById(R.id.submitForm).setOnClickListener(v -> {
            submitForm();
        });
    }

    private void submitForm(){
        if(new CarVerifier(getApplicationContext()).verifyCar(name, seletedCarModel.getId() != null)){
            // salvar carro com user
            // salvar imagem do modelo localmente
            // salvar modelo com carro
        }
    }

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
            startActivity(new Intent(this, ListCarActivity.class));
        });

        name = findViewById(R.id.name);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);
        addBtn = findViewById(R.id.addBtn);
    }

    private void listModels(){

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
        adapter.setOnClickListener(AddCarActivity.this);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        carModelAppearence = new CarModelAppearence(
                adapter,
                recyclerView,
                getApplicationContext()
        );
    }

    private void doBinding(){
        bindingFactory = new DataBindingFactory(this, R.id.frameLayoutUser);
        binding = ActivityAddCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindingFactory.replaceFragment(new CarFragment());
        binding.bottomNavigationMenuUser.setOnItemSelectedListener(item -> bindingFactory.bindingMenu(item));
    }

    @Override
    public void onClick(int position, View v, CarModel carModel) {
        carModelAppearence.restorePreviousClickedItem();

        carModelAppearence.setClickedItemColors(v);

        seletedCarModel = carModel;
        adapter.setPreviousClickedIndex(position);

    }


}