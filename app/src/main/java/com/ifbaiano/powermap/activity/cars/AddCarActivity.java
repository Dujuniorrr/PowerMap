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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MenuActivity;
import com.ifbaiano.powermap.adapter.ModelCarAdapter;
import com.ifbaiano.powermap.appearance.CarModelAppearence;
import com.ifbaiano.powermap.dao.firebase.EletricCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.HybridCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.dao.media.StorageDaoMedia;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.dao.sqlite.EletricCarModelDaoSqlite;
import com.ifbaiano.powermap.dao.sqlite.HybridCarModelDaoSqlite;
import com.ifbaiano.powermap.databinding.ActivityAddCarBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.fragment.CarFragment;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.CarService;
import com.ifbaiano.powermap.service.EletricCarModelService;
import com.ifbaiano.powermap.service.HybridCarModelService;
import com.ifbaiano.powermap.verifier.CarVerifier;

import java.util.ArrayList;
import java.util.Objects;

public class AddCarActivity extends AppCompatActivity implements ModelCarAdapter.OnClickListener {

    TextInputEditText name;
    AppCompatButton addBtn;
    ProgressBar progressBar;
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
        listModels();
        findViewById(R.id.submitForm).setOnClickListener(v -> {
            submitForm();
        });
    }

    private void submitForm(){
        boolean isValid = new CarVerifier(getApplicationContext()).verifyCar(name, selectedCarModel.getId() != null);
        new Thread(() -> {
            if(isValid){
               try{
                   Car car = new CarService(new CarDaoSqlite(getApplicationContext())).add(
                           new Car(Objects.requireNonNull(name.getText()).toString()),
                           UserFactory.getUserInMemory(getApplicationContext()).getId()
                   );

                   byte[] imageBytes = new StorageDaoFirebase().getImageAsByteArray(selectedCarModel.getPathImg());
                   StorageDaoMedia storageDaoMedia = new StorageDaoMedia(getApplicationContext());

                   if(selectedCarModel instanceof HybridCarModel){
                       new HybridCarModelService(
                               new HybridCarModelDaoSqlite(getApplicationContext()),
                               storageDaoMedia).add((HybridCarModel) selectedCarModel, car.getId(), imageBytes );
                   }
                   else if(selectedCarModel instanceof EletricCarModel){
                       new EletricCarModelService(
                               new EletricCarModelDaoSqlite(getApplicationContext()),
                               storageDaoMedia).add((EletricCarModel) selectedCarModel, car.getId(), imageBytes );
                   }
                   startActivity(new Intent(this, MenuActivity.class));
               }
               catch (Exception e){
                   runOnUiThread(() -> {
                       Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.error_data), Toast.LENGTH_SHORT).show();
                   });
               }
            }
        }).start();
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
            startActivity(new Intent(this, MenuActivity.class));
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



    @Override
    public void onClick(int position, View v, CarModel carModel) {
        carModelAppearence.restorePreviousClickedItem();

        carModelAppearence.setClickedItemColors(v);

        selectedCarModel = carModel;
        adapter.setPreviousClickedIndex(position);

    }


}