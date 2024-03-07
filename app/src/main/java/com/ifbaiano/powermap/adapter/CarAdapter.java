package com.ifbaiano.powermap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.dao.media.StorageDaoMedia;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;

import java.util.ArrayList;

public class CarAdapter extends RecyclerView.Adapter {
    ArrayList<Car> cars;
    Context context;
    private DeleteClickListener deleteClickListener;
    private OnClickListener onClickListener;

    private final Boolean selectMode;
    private int previousClickedIndex = -1;


    public CarAdapter(ArrayList<Car> cars, Context context, Boolean selectMode) {
        this.cars = cars;
        this.context = context;
        this.selectMode = selectMode;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car,parent,false);
        return new ViewHolderClass(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClass vhClass = (ViewHolderClass) holder;
        Car car = cars.get(position);
        vhClass.name.setText(car.getName());
        vhClass.year.setText(Integer.toString(car.getCarModel().getYear()));

        if (car.getCarModel() instanceof EletricCarModel) {
            vhClass.energyConsumption.setText(
                    context.getString(R.string.energy_consumption_label, Float.toString(((EletricCarModel) car.getCarModel()).getEnergyConsumption()))
            );
            if (car.getCarModel() instanceof HybridCarModel) {
                vhClass.fuelConsumption.setText(
                        context.getString(
                                R.string.fuel_consumption_label, Float.toString(((HybridCarModel) car.getCarModel()).getFuelConsumption())
                        )
                );
            }
            else{
                vhClass.fuelConsumption.setVisibility(View.GONE);
            }
        }

        if(selectMode){
            //
        }
        else{
            vhClass.deleteButton.setOnClickListener( v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick( vhClass.getLayoutPosition(), vhClass.card, car);
                }
            });
        }

        new StorageDaoMedia(this.context).transformInBitmap(car.getCarModel().getPathImg(), vhClass.imageView, vhClass.progressBar);
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {

        View card;
        TextView name, year, energyConsumption, fuelConsumption;
        ImageView imageView;
        AppCompatButton editButton, deleteButton;

        ProgressBar progressBar;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            card = itemView;
            name = itemView.findViewById(R.id.name);
            year = itemView.findViewById(R.id.year);
            energyConsumption =  itemView.findViewById(R.id.energyConsumption);
            fuelConsumption =  itemView.findViewById(R.id.fuelConsumption);
            imageView = itemView.findViewById(R.id.imageView);
            progressBar = itemView.findViewById(R.id.progressBar);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        @Override
        public void onClick(View v) {}
    }


    public interface DeleteClickListener {
        void onDeleteClick(int position, View v, Car car);
    }
    public interface OnClickListener{
        void onClick(int position, View v, Car car);
    }

    public void setDeleteClickListener(DeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public int getPreviousClickedIndex() {
        return previousClickedIndex;
    }

    public void setPreviousClickedIndex(int previousClickedIndex) {
        this.previousClickedIndex = previousClickedIndex;
    }
}
