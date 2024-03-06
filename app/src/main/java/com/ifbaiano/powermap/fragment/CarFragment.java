package com.ifbaiano.powermap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.cars.AddCarActivity;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.dao.sqlite.EletricCarModelDaoSqlite;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.EletricCarModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    public CarFragment() {
    }

    public static CarFragment newInstance() {
        return new CarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ArrayList<EletricCarModel> models = new EletricCarModelDaoSqlite(getActivity()).findAll();
        ArrayList<Car> cars = new CarDaoSqlite(getActivity()).findAll();
        if(models != null){
            for(EletricCarModel model: models){
                Log.d("CAR TEST", model.getName());
            }
        }

        if(cars != null){
            for(Car car: cars){
                Log.d("CAR TEST", car.getName());
                Log.d("CAR TEST", car.getCarModel().getId());
                Log.d("CAR TEST", car.getCarModel().getPathImg());
            }
        }
        View viewRoot = inflater.inflate(R.layout.fragment_car, container, false);
        viewRoot.findViewById(R.id.text).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddCarActivity.class));
        });
        return viewRoot;
    }
}