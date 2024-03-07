package com.ifbaiano.powermap.factory;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ifbaiano.powermap.dao.sqlite.EletricCarModelDaoSqlite;
import com.ifbaiano.powermap.dao.sqlite.HybridCarModelDaoSqlite;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.Schedule;

public class CarFactory {

    public static Car createByCursor(Cursor cursor, Context ctx){
        String id =  cursor.getString(cursor.getColumnIndexOrThrow("id"));

        CarModel eletricModel = new EletricCarModelDaoSqlite(ctx).findByCarId(id);
        CarModel hybridModel = new HybridCarModelDaoSqlite(ctx).findByCarId(id);
        CarModel carModel;

        if(eletricModel != null){
            carModel = eletricModel;
            Log.d("TESTE", eletricModel.getId());

        }
        else{
            carModel = hybridModel;

        }

        return new Car(
                id,
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                carModel
        );
    }

}
