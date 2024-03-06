package com.ifbaiano.powermap.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    @Exclude
    private String id;

    private String name, email, password, imgPath;
    private boolean isAdmin;

    @Exclude
    private ArrayList<Schedule> schedules;

    @Exclude
    private ArrayList<Car> cars;

    public User(){}

    public User( String name, String email, String password, boolean isAdmin) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
    public User(String id, String name, String email, String password, String imgPath, boolean isAdmin, ArrayList<Schedule> schedules, ArrayList<Car> cars) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.imgPath = imgPath;
        this.isAdmin = isAdmin;
        this.schedules = schedules;
        this.cars = cars;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImgpath() {
        return this.imgPath;
    }

    public void setImgpath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public void setCars(ArrayList<Car> cars) {
        this.cars = cars;
    }
}
