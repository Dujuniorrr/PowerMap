package com.ifbaiano.powermap.activity.cars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.carModels.ListCarModelsActivity;
import com.ifbaiano.powermap.adapter.ModelCarAdapter;
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

import java.util.ArrayList;

public class AddCarActivity extends AppCompatActivity implements ModelCarAdapter.OnClickListener {
    @NonNull ActivityAddCarBinding binding;
    DataBindingFactory bindingFactory;
    AppCompatButton addBtn;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<CarModel> carModels;
    ModelCarAdapter adapter;
    HybridCarModelService hybridCarModelService;
    EletricCarModelService eletricCarModelService;
    CarModel seletedCarModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        this.doBinding();
        this.findViewById();
        this.makeInstances();
        listModels();
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
        restorePreviousClickedItem();

        setClickedItemColors(v);

        seletedCarModel = carModel;
        adapter.setPreviousClickedIndex(position);

    }

    private void restorePreviousClickedItem() {
        int previousClickedIndex = adapter.getPreviousClickedIndex();
        if (previousClickedIndex != -1) {
            View previousClickedView = recyclerView.getChildAt(previousClickedIndex);
            changeTextViewColors(previousClickedView, R.color.black);
            previousClickedView.setBackgroundResource(R.drawable.model_car_item);
        }
    }

    private void setClickedItemColors(View view) {
        view.setBackgroundResource(R.drawable.model_car_selected_item);
        changeTextViewColors(view, R.color.red_custom);
    }

    private void changeTextViewColors(View view, int colorResource) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                changeTextViewColors(viewGroup.getChildAt(i), colorResource);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(getResources().getColor(colorResource));
        }
    }

}