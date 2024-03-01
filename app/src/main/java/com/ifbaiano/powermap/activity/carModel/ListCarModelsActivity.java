package com.ifbaiano.powermap.activity.carModel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.adapter.ModelCarAdapter;
import com.ifbaiano.powermap.dao.firebase.EletricCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.HybridCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.fragment.ModelsFragment;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.databinding.ActivityListCarModelsBinding;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.EletricCarModelService;
import com.ifbaiano.powermap.service.HybridCarModelService;

import java.util.ArrayList;

public class ListCarModelsActivity extends AppCompatActivity  implements ModelCarAdapter.EditClickListener, ModelCarAdapter.DeleteClickListener {

    EditText searchModel;
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

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);
        addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AddCarModelActivity.class));
        });
        searchModel = findViewById(R.id.searchModel);

        findViewById(R.id.searchBtn).setOnClickListener(v -> {
            Log.d("PRINT MODEL", searchModel.getText().toString());
            String searchText = searchModel.getText().toString();
            if(searchText.isEmpty()){
                listModels(null);
            }
            listModels(searchText);
        });

        listModels(null);
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

    private void listModels(String search){
        Log.d("LIST MODELS", "entrou aqq");
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            carModels = new ArrayList<>();
            ArrayList<EletricCarModel> eletricCarModels = this.eletricCarModelService.listAll(search);

            ArrayList<HybridCarModel> hybridModels = this.hybridCarModelService.listAll(search);

            if(eletricCarModels != null){
                carModels.addAll(eletricCarModels);
            }

            if(hybridModels != null){
                carModels.addAll(hybridModels);
            }

            runOnUiThread(() -> {
                adapter = new ModelCarAdapter(carModels, getApplicationContext());
                adapter.setEditClickListener(ListCarModelsActivity.this);
                adapter.setDeleteClickListener(ListCarModelsActivity.this);
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
            });

        }).start();
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