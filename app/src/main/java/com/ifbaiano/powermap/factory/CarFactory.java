package com.ifbaiano.powermap.factory;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.ifbaiano.powermap.dao.sqlite.EletricCarModelDaoSqlite;
import com.ifbaiano.powermap.dao.sqlite.HybridCarModelDaoSqlite;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.Schedule;
import com.ifbaiano.powermap.model.User;

public class CarFactory {

    public static Car createByCursor(Cursor cursor, Context ctx){
        String id =  cursor.getString(cursor.getColumnIndexOrThrow("id"));

        CarModel eletricModel = new EletricCarModelDaoSqlite(ctx).findByCarId(id);
        CarModel hybridModel = new HybridCarModelDaoSqlite(ctx).findByCarId(id);
        CarModel carModel;

        if(eletricModel != null){
            carModel = eletricModel;
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

    public static boolean saveCarInMemory(Car car, Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences("power_map_memory", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putString("idCar", car.getId());
        edit.putString("nameCar", car.getName());
        edit.apply();
        return true;
    }

    public static Car getCarInMemory(Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences("power_map_memory", Context.MODE_PRIVATE);

        return new Car(
                preferences.getString("idCar", null),
                preferences.getString("nameCar", null),
                null
        );
    }

    public static boolean clearUserInMemory(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences("power_map_memory", ctx.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();

        edit.remove("idCar");
        edit.remove("nameCar");
        edit.apply();

        return true;
    }
}
