package com.ifbaiano.powermap.model;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Car implements  Serializable{
    @Exclude
    private String id;

    private  String name;

    @Exclude
    private CarModel carModel;

    public Car() {
    }

    public Car(String name) {
        this.name = name;

    }
    public Car(String id, String name, CarModel carModel) {
        this.id = id;
        this.name = name;
        this.carModel = carModel;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}