package com.ifbaiano.powermap.service;

import com.ifbaiano.powermap.dao.contracts.CarDao;
import com.ifbaiano.powermap.dao.contracts.EletricCarModelDao;
import com.ifbaiano.powermap.dao.contracts.StorageDao;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.EletricCarModel;

public class CarService {
    private CarDao dao;
    public CarService(CarDao dao) {
        this.dao = dao;
    }
    public Car add(Car car, String userId){
        return this.dao.add(car, userId);
    }


}