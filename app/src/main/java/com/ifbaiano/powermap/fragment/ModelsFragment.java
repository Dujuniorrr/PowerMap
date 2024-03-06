package com.ifbaiano.powermap.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MainActivity;
import com.ifbaiano.powermap.activity.MenuActivity;
import com.ifbaiano.powermap.activity.carModels.AddCarModelActivity;
import com.ifbaiano.powermap.activity.carModels.EditCarModelActivity;
import com.ifbaiano.powermap.adapter.ModelCarAdapter;
import com.ifbaiano.powermap.dao.firebase.EletricCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.HybridCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.EletricCarModelService;
import com.ifbaiano.powermap.service.HybridCarModelService;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModelsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModelsFragment extends Fragment  implements ModelCarAdapter.EditClickListener, ModelCarAdapter.DeleteClickListener {


    LayoutInflater inflater;
    AppCompatButton addBtn;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<CarModel> carModels;
    ModelCarAdapter adapter;
    HybridCarModelService hybridCarModelService;
    EletricCarModelService eletricCarModelService;
    AppCompatActivity mainActivity;
    View rootView;

    public ModelsFragment() {
        // Required empty public constructor
    }


    public static ModelsFragment newInstance() {
        return new ModelsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mainActivity =  (AppCompatActivity) getActivity();
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_models, container, false);
        this.makeInstances();
        this.findViewById();
        addBtn.setOnClickListener(v -> {
            startActivity(new Intent(mainActivity.getApplicationContext(), AddCarModelActivity.class));
        });

        listModels();
        return rootView;
    }

    private void findViewById(){
        recyclerView = rootView.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity.getApplicationContext()));
        progressBar = rootView.findViewById(R.id.progressBar);
        addBtn = rootView.findViewById(R.id.addBtn);
    }

    private void makeInstances(){
        hybridCarModelService = new HybridCarModelService(
                new HybridCarModelDaoFirebase(mainActivity.getApplicationContext()),
                new StorageDaoFirebase());

        eletricCarModelService = new EletricCarModelService(
                new EletricCarModelDaoFirebase(mainActivity.getApplicationContext()),
                new StorageDaoFirebase());
    }

    private void listModels(){

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            setModelList();

            mainActivity.runOnUiThread( this::formatRecycleView);
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
        adapter = new ModelCarAdapter(carModels, mainActivity.getApplicationContext(), false);
        adapter.setEditClickListener(ModelsFragment.this);
        adapter.setDeleteClickListener(ModelsFragment.this);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEditClick(View v, CarModel carModel) {
        Intent intent = new Intent(mainActivity, EditCarModelActivity.class);
        intent.putExtra("carModel", carModel);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position, View v, CarModel carModel) {
        new Thread(() -> {
            if ((carModel instanceof HybridCarModel && hybridCarModelService.remove((HybridCarModel) carModel))
                    || (carModel instanceof EletricCarModel && eletricCarModelService.remove((EletricCarModel) carModel))) {
                mainActivity.runOnUiThread(() -> {
                    carModels.remove(position);
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