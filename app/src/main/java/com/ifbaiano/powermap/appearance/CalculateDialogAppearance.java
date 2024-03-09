package com.ifbaiano.powermap.appearance;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.factory.CarFactory;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;
import com.ifbaiano.powermap.service.LocationService;

import org.checkerframework.common.value.qual.DoubleVal;

import java.io.IOException;

public class CalculateDialogAppearance {

    EditText kmByHour, currentEnergy, currentFuel;
    TextView message, distanceMessage, energyMessage, fuelMessage;
    RelativeLayout resultsContainer;
    private Dialog dialog = null;
    private final Context ctx;
    private LocationService locationService;
    private Car car = null;

    private double distanceToNearestStation;
    private double energyConsumptionUntilLocation;
    private double fuelConsumptionUntilLocation;
    public CalculateDialogAppearance(Context ctx, LocationService locationService) {
        this.ctx = ctx;
        this.locationService = locationService;
        if(CarFactory.getCarInMemory(this.ctx).getId() != null){
            this.car = new CarDaoSqlite(this.ctx).findOne(CarFactory.getCarInMemory(this.ctx).getId());
        }
    }

    public void createCalculateDialog() {
       if(car != null){
           dialog = new Dialog(this.ctx);

           configDialog();
           findViewsById();
           setCarAttributes();

           dialog.show();
       }
       else{
           Toast.makeText(ctx, this.ctx.getString(R.string.selected_car), Toast.LENGTH_SHORT).show();
       }
    }

    private void configDialog(){
        dialog.setContentView(R.layout.calculate_consumption_dialog);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.findViewById(R.id.submitForm).setOnClickListener(v -> dialog.findViewById(R.id.resultsContainer).setVisibility(View.VISIBLE));
        window.setWindowAnimations(android.R.style.Animation_Toast);

    }

    private void findViewsById(){
        kmByHour = dialog.findViewById(R.id.kmByHour);
        currentEnergy = dialog.findViewById(R.id.eletricConsumption);
        currentFuel = dialog.findViewById(R.id.fuelConsumption);
        message = dialog.findViewById(R.id.message);
        distanceMessage = dialog.findViewById(R.id.locationValue);
        energyMessage = dialog.findViewById(R.id.energyValue);
        fuelMessage = dialog.findViewById(R.id.fuelValue);
        resultsContainer = dialog.findViewById(R.id.resultsContainer);

        dialog.findViewById(R.id.submitForm).setOnClickListener(v -> {
            showCalculate();
        });
    }

    @SuppressLint("DefaultLocale")
    private void showCalculate(){

        if(canReachNearestStation()){
            message.setText(R.string.possible);
        }
        else{
            message.setText(R.string.not_possible);
        }

        distanceMessage.setText(
                ctx.getResources().getString(R.string.until_station, String.format("%.2f", distanceToNearestStation))
        );

        energyMessage.setText(
                ctx.getResources().getString(R.string.energy_station, String.format("%.2f", energyConsumptionUntilLocation))
        );

        fuelMessage.setText(
                ctx.getResources().getString(R.string.fuel_station, String.format("%.2f", fuelConsumptionUntilLocation))
        );

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,  // Largura
                RelativeLayout.LayoutParams.WRAP_CONTENT   // Altura
        );

        layoutParams.addRule(RelativeLayout.BELOW, R.id.lineView);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        resultsContainer.setLayoutParams(layoutParams);

        resultsContainer.setVisibility(View.VISIBLE);
    }

    private void setCarAttributes(){
       try{
           ((ImageView)  dialog.findViewById(R.id.imageView)).setImageBitmap(
                   MediaStore.Images.Media.getBitmap(
                           this.ctx.getContentResolver() , Uri.parse(car.getCarModel().getPathImg()))
           );
           ((TextView)  dialog.findViewById(R.id.name)).setText(car.getName());

       }catch (IOException e){
           Toast.makeText(ctx, ctx.getString(R.string.image_error_not_found), Toast.LENGTH_SHORT).show();
       }
    }
    public boolean canReachNearestStation() {
        distanceToNearestStation = locationService.getPlaceService().getDirectionService().totalDistance;

        float kmPerHour = Float.parseFloat( kmByHour.getText().toString());

        float energyConsumption = 0;
        float fuelConsumption = 0;
        if (car.getCarModel() instanceof EletricCarModel) {
            energyConsumption = ((EletricCarModel) car.getCarModel()).getEnergyConsumption();
            if (car.getCarModel() instanceof HybridCarModel) {
                fuelConsumption = ((HybridCarModel) car.getCarModel()).getFuelConsumption();
            }
        }

        double neededValue = calculateEnergyNeeded(distanceToNearestStation, energyConsumption, fuelConsumption, kmPerHour);

        float currentEnergy = Float.parseFloat(this.currentEnergy.getText().toString());
        float currentFuel = Float.parseFloat(this.currentFuel.getText().toString());

        if(car.getCarModel() instanceof HybridCarModel) {
            return currentEnergy + currentFuel >= neededValue;
        }
        else if (car.getCarModel() instanceof EletricCarModel) {
            return currentEnergy >= neededValue;
        }

        return false;
    }



    private double calculateEnergyNeeded(double distanceToNearestStation, float energyConsumption, float fuelConsumption, float kmPerHour) {
        //tempo = distancia / velocidade
        double timeToReachStation = distanceToNearestStation / kmPerHour;

        this.energyConsumptionUntilLocation = energyConsumption * timeToReachStation;
        if (car.getCarModel() instanceof HybridCarModel) {
            this.fuelConsumptionUntilLocation = fuelConsumption * timeToReachStation;
        }

        return this.energyConsumptionUntilLocation + this.fuelConsumptionUntilLocation;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }


}

