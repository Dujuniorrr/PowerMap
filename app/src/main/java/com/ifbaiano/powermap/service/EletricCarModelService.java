package com.ifbaiano.powermap.service;

import com.ifbaiano.powermap.dao.contracts.EletricCarModelDao;
import com.ifbaiano.powermap.dao.contracts.StorageDao;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.EletricCarModel;

import java.util.ArrayList;

public class EletricCarModelService {
    private EletricCarModelDao dao;
    private final StorageDao storageDao;
    private final String folderRef = "eletric_car_models";
    public EletricCarModelService(EletricCarModelDao dao, StorageDao storageDao) {
        this.dao = dao;
        this.storageDao = storageDao;
    }

    public boolean add(EletricCarModel carModel, String carId, byte[] imgByte){
       carModel.setPathImg(storageDao.add(imgByte, this.folderRef));
       return this.dao.add(carModel, carId) != null;
    }

    public boolean edit(EletricCarModel carModel, String carId, byte[] imgByte){
        if(imgByte != null){
            storageDao.putImage(imgByte, carModel.getPathImg());
        }
        return this.dao.edit(carModel) != null;
    }

    public boolean remove(EletricCarModel carModel){
        if(this.storageDao.remove(carModel.getPathImg())) return this.dao.remove(carModel);
        return false;
    }
    public ArrayList<EletricCarModel> listAll(String search){
        return this.dao.findAll(search);
    }
    public EletricCarModelDao getDao() {
        return dao;
    }

    public void setDao(EletricCarModelDao dao) {
        this.dao = dao;
    }
}
