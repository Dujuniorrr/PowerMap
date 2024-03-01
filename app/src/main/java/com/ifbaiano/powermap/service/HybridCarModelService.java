package com.ifbaiano.powermap.service;

import com.ifbaiano.powermap.dao.contracts.HybridCarModelDao;
import com.ifbaiano.powermap.dao.contracts.StorageDao;
import com.ifbaiano.powermap.model.EletricCarModel;
import com.ifbaiano.powermap.model.HybridCarModel;

import java.util.ArrayList;

public class HybridCarModelService {
    private HybridCarModelDao dao;
    private final StorageDao storageDao;
    private final String folderRef = "hybrid_car_models";
    public HybridCarModelService(HybridCarModelDao dao, StorageDao storageDao) {
        this.dao = dao;
        this.storageDao = storageDao;
    }

    public boolean add(HybridCarModel carModel, String carId, byte[] imgByte){
        carModel.setPathImg(storageDao.add(imgByte, this.folderRef));
        return this.dao.add(carModel, carId) != null;
    }

    public boolean edit(HybridCarModel carModel, String carId, byte[] imgByte){
        if(imgByte != null){
            storageDao.putImage(imgByte, carModel.getPathImg());
        }
        return this.dao.edit(carModel) != null;
    }
    public boolean remove(HybridCarModel carModel){
        if(this.storageDao.remove(carModel.getPathImg())) return this.dao.remove(carModel);
        return false;
    }
    public ArrayList<HybridCarModel> listAll(String search){
        return this.dao.findAll(search);
    }

    public HybridCarModelDao getDao() {
        return dao;
    }

    public void setDao(HybridCarModelDao dao) {
        this.dao = dao;
    }
}
