package com.ifbaiano.powermap.appearance;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.adapter.CarAdapter;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.factory.CarFactory;
import com.ifbaiano.powermap.fragment.CarFragment;
import com.ifbaiano.powermap.fragment.MapFragment;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.service.CarService;
import com.ifbaiano.powermap.service.LocationService;
import com.ifbaiano.powermap.verifier.CalculateConsumptionVerifier;

import java.util.ArrayList;

public class SelectCurrentCarDialogAppearance implements  CarAdapter.OnClickListener {
    MapFragment mapFragment;

    MessageHandlerAppearance errorTagManager;
    CalculateDialogAppearance calculateDialogAppearance;
    Car car = null;
    CarModelAppearence carModelAppearence;
    private final Context ctx;
    ArrayList<Car> cars;
    CarAdapter adapter;
    CarService carService;
    CalculateConsumptionVerifier verifier;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    private Dialog dialog = null;

    AppCompatActivity mainActivity;
    public SelectCurrentCarDialogAppearance(Context ctx, AppCompatActivity mainActivity, MapFragment mapFragment,
    CalculateDialogAppearance calculateDialogAppearance) {
        this.mainActivity = mainActivity;
        this.ctx = ctx;
        this.mapFragment = mapFragment;
        this.calculateDialogAppearance = calculateDialogAppearance;
        this.verifier = new CalculateConsumptionVerifier(this.ctx);

    }

    public void createSelectDialog() {
        this.makeInstances();

        if(carService.findAll().size() > 0){
            dialog = new Dialog(this.ctx);

            errorTagManager = new MessageHandlerAppearance(dialog.findViewById(R.id.errorMessageRecords));

            this.configDialog();
            this.findViewById();
            this.listCars();

            dialog.show();
        }
        else{
            Toast.makeText(ctx, ctx.getResources().getString(R.string.data_car_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void findViewById(){
        dialog.findViewById(R.id.submitForm).setOnClickListener(v -> {
            if(car != null){
                CarFactory.saveCarInMemory(car, mainActivity);
                mapFragment.mMap.clear();
                calculateDialogAppearance.putCar();
                mapFragment.getCurrentLocation();
                dialog.dismiss();
            }
            else{
                Toast.makeText(ctx, mainActivity.getString(R.string.select_car), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView = dialog.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity.getApplicationContext()));
        progressBar = dialog.findViewById(R.id.progressBar);
    }


    private void makeInstances(){
        carService = new CarService(new CarDaoSqlite(mainActivity.getApplicationContext()));
    }

    private void listCars(){

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            cars = carService.findAll();
            if(cars.size() > 0){
                mainActivity.runOnUiThread( this::formatRecycleView);
                errorTagManager.clearErrorTag();
            }
            else{
                progressBar.setVisibility(View.GONE);
                errorTagManager.showErrorTag(dialog.getContext().getString(R.string.data_car_error));
            }
        }).start();
    }

    public void formatRecycleView(){
        adapter = new CarAdapter(cars, mainActivity.getApplicationContext(), true);
        adapter.setOnClickListener(SelectCurrentCarDialogAppearance.this);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        carModelAppearence = new CarModelAppearence(
                adapter,
                recyclerView,
                mainActivity.getApplicationContext()
        );
    }

    private void configDialog(){
        dialog.setContentView(R.layout.select_current_car_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.findViewById(R.id.submitForm).setOnClickListener(v -> dialog.findViewById(R.id.resultsContainer).setVisibility(View.VISIBLE));
        window.setWindowAnimations(android.R.style.Animation_Toast);

    }

    @Override
    public void onClick(int position, View v, Car car) {
        carModelAppearence.restorePreviousClickedItem();

        carModelAppearence.setClickedItemColors(v);

        this.car = car;

        adapter.setPreviousClickedIndex(position);
    }
}
