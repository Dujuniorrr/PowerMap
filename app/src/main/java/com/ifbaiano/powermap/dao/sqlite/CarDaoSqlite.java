package com.ifbaiano.powermap.dao.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ifbaiano.powermap.connection.SqliteConnection;
import com.ifbaiano.powermap.dao.contracts.CarDao;
import com.ifbaiano.powermap.factory.CarFactory;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.CarModel;

import java.util.ArrayList;

public class CarDaoSqlite implements CarDao {

    private final SqliteConnection conn;
    private final Context ctx;
    private SQLiteDatabase db;
    private final String TABLE_NAME = "cars";
    private final String FIND_ONE_QUERY = "SELECT * FROM "+ this.TABLE_NAME +" WHERE id = ?";
    private final String FIND_ALL_QUERY =  "SELECT * FROM "+ this.TABLE_NAME;
    private final String FIND_BY_CLIENT_QUERY =  "SELECT * FROM "+ this.TABLE_NAME + " WHERE users_id = ?";


    public CarDaoSqlite(Context ctx) {
        this.conn = new SqliteConnection(ctx);
        this.ctx = ctx;
    }

    @Override
    public Car add(Car car, String userId) {
        this.db = this.conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", car.getName());
        values.put("users_id", userId);

        long id = db.insert(this.TABLE_NAME, null, values);
        car.setId(Long.toString(id));

        return  (id > 0) ? car :  null;
    }

    @Override
    public Car edit(Car car) {
        this.db = this.conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", car.getName());

        return (this.db.update(this.TABLE_NAME, values, "id = ?",  new String[]{ car.getId() }) > 0) ? car : null;

    }

    @Override
    public Boolean remove(Car car) {
        this.db = this.conn.getWritableDatabase();
        return db.delete(this.TABLE_NAME, "id = ?", new String[]{ car.getId()}) > 0;
    }

    @Override
    public Car findOne(String id) {
        this.db = this.conn.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(this.FIND_ONE_QUERY, new String[]{id});

        if(cursor.moveToFirst()){
            return CarFactory.createByCursor(cursor, this.ctx);
        }

        return null;
    }

    @Override
    public ArrayList<Car> findAll() {
        this.db = this.conn.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = this.db.rawQuery(this.FIND_ALL_QUERY, null);
        return this.makeCarList(cursor);
    }

    @Override
    public ArrayList<Car> findByUserId(String id) {
        this.db = this.conn.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor =  this.db.rawQuery(this.FIND_BY_CLIENT_QUERY, new String[]{ id });
        return this.makeCarList(cursor);
    }

    public ArrayList<Car> makeCarList(Cursor cursor){
        ArrayList<Car> carList = new ArrayList<>();
        while(cursor.moveToNext()) {
            carList.add(CarFactory.createByCursor(cursor, this.ctx));
        }
        return carList;
    }
}
