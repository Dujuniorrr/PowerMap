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
import com.ifbaiano.powermap.adapter.contract.AdapterCustom;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;

import java.util.ArrayList;

public class ModelCarAdapter extends RecyclerView.Adapter implements AdapterCustom {
    ArrayList<CarModel>  carModels;
    Context context;
    private EditClickListener editClickListener;
    private DeleteClickListener deleteClickListener;
    private OnClickListener onClickListener;

    private final Boolean selectMode;
    private int previousClickedIndex = -1;


    public ModelCarAdapter(ArrayList<CarModel> carModels, Context context, Boolean selectMode) {
        this.carModels = carModels;
        this.context = context;
        this.selectMode = selectMode;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_model_car,parent,false);
        return new ViewHolderClass(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClass vhClass = (ViewHolderClass) holder;
        CarModel carModel = carModels.get(position);
        vhClass.name.setText(carModel.getName());
        vhClass.year.setText(Integer.toString(carModel.getYear()));

        if (carModel instanceof EletricCarModel) {
            vhClass.energyConsumption.setText(
                    context.getString(R.string.energy_consumption_label, Float.toString(((EletricCarModel) carModel).getEnergyConsumption()))
            );
            if (carModel instanceof HybridCarModel) {
                vhClass.fuelConsumption.setText(
                        context.getString(
                                R.string.fuel_consumption_label, Float.toString(((HybridCarModel) carModel).getFuelConsumption())
                        )
                );
            }
            else{
                vhClass.fuelConsumption.setVisibility(View.GONE);
            }
        }

        if(selectMode){
            vhClass.editButton.setVisibility(View.GONE);
            vhClass.deleteButton.setVisibility(View.GONE);
            vhClass.card.setOnClickListener( v -> {
                if (onClickListener != null) {
                    onClickListener.onClick(vhClass.getLayoutPosition(), vhClass.card, carModel);
                }
            });
        }
        else{
            vhClass.editButton.setOnClickListener( v -> {
                if (editClickListener != null) {
                    editClickListener.onEditClick( vhClass.card, carModel);
                }
            });
            vhClass.deleteButton.setOnClickListener( v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick( vhClass.getLayoutPosition(), vhClass.card, carModel);
                }
            });
        }

         new StorageDaoFirebase().transformInBitmap(carModel.getPathImg(), vhClass.imageView, vhClass.progressBar);
    }

    @Override
    public int getItemCount() {
        return carModels.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder  {

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
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

    }

    public interface EditClickListener {
        void onEditClick(View v, CarModel carModel);
    }

    public interface DeleteClickListener {
        void onDeleteClick(int position, View v, CarModel carModel);
    }
    public interface OnClickListener{
        void onClick(int position, View v, CarModel carModel);
    }

    public void setEditClickListener(EditClickListener listener) {
        this.editClickListener = listener;
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
