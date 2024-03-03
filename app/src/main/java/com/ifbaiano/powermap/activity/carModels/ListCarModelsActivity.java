package com.ifbaiano.powermap.activity.carModels;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.adapter.ModelCarAdapter;
import com.ifbaiano.powermap.dao.firebase.EletricCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.HybridCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.databinding.ActivityListCarModelsBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.fragment.ModelsFragment;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.EletricCarModelService;
import com.ifbaiano.powermap.service.HybridCarModelService;

import java.util.ArrayList;

public class ListCarModelsActivity extends AppCompatActivity  implements ModelCarAdapter.EditClickListener, ModelCarAdapter.DeleteClickListener {

    ActivityListCarModelsBinding binding;
    AppCompatButton addBtn;
    ProgressBar progressBar;
    DataBindingFactory bindingFactory;
    RecyclerView recyclerView;
    ArrayList<CarModel> carModels;
    ModelCarAdapter adapter;
    HybridCarModelService hybridCarModelService;
    EletricCarModelService eletricCarModelService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_car_models);

        this.doBinding();
        this.makeInstances();
        this.findViewById();

        addBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AddCarModelActivity.class));
        });

        listModels();
    }

    private void findViewById(){
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);
        addBtn = findViewById(R.id.addBtn);
    }

    private void makeInstances(){
        hybridCarModelService = new HybridCarModelService(
                new HybridCarModelDaoFirebase(getApplicationContext()),
                new StorageDaoFirebase());

        eletricCarModelService = new EletricCarModelService(
                new EletricCarModelDaoFirebase(getApplicationContext()),
                new StorageDaoFirebase());
    }

    private void doBinding(){
        bindingFactory = new DataBindingFactory(this, R.id.frameLayoutAdminlist);
        binding = ActivityListCarModelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindingFactory.replaceFragment(new ModelsFragment());
        binding.bottomNavigationMenuAdminlist.setOnItemSelectedListener( item -> bindingFactory.bindingMenu(item));
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

        adapter = new ModelCarAdapter(carModels, getApplicationContext(), false);
        adapter.setEditClickListener(ListCarModelsActivity.this);
        adapter.setDeleteClickListener(ListCarModelsActivity.this);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEditClick(View v, CarModel carModel) {
        Intent intent = new Intent(this, EditCarModelActivity.class);
        intent.putExtra("carModel", carModel);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position, View v, CarModel carModel) {
        new Thread(() -> {
            if ((carModel instanceof HybridCarModel && hybridCarModelService.remove((HybridCarModel) carModel))
                    || (carModel instanceof EletricCarModel && eletricCarModelService.remove((EletricCarModel) carModel))) {
            runOnUiThread(() -> {
                    carModels.remove(position);
                    adapter.notifyItemRemoved(position);
                });
            }
            else{
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.error_data, Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}