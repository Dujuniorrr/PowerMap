package com.ifbaiano.powermap.service;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.dao.sqlite.CarDaoSqlite;
import com.ifbaiano.powermap.factory.CarFactory;
import com.ifbaiano.powermap.model.Car;
import com.ifbaiano.powermap.model.CarModel;

import java.io.IOException;

public class LocationService {

    private final String URL_API = "https://places.googleapis.com/v1/places:searchNearby";

    private PlaceService placeService;
    private final FusedLocationProviderClient fusedLocationClient;
    private final GoogleMap mMap;
    private Marker mMarker;
    private final AppCompatActivity mainActivity;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public LocationService(AppCompatActivity mainActivity, GoogleMap mMap) {
        this.mainActivity = mainActivity;
        this.mMap = mMap;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity);
    }

    public void getCurrentLocation() {
        if (hasLocationPermission()) {
            getLastLocationAndSetMarker();
            startLocationUpdates();
        } else {
            requestLocationPermission();
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocationAndSetMarker() {
        if(hasLocationPermission()){
            fusedLocationClient.getLastLocation().addOnSuccessListener(mainActivity, location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (mMap != null) {
                        if (mMarker == null) {
                            putMarker(latLng);
                        } else {
                            mMarker.setPosition(latLng);
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));

                        CarModel carModel = null;
                        if(CarFactory.getCarInMemory(mainActivity).getId() != null) carModel = new CarDaoSqlite(mainActivity).findOne(CarFactory.getCarInMemory(mainActivity).getId()).getCarModel();
                        placeService = new PlaceService(carModel, latLng, mainActivity, mMap);
                        placeService.execute(URL_API);
                    }
                } else {
                    Toast.makeText(mainActivity, mainActivity.getString(R.string.error_location), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // 5 segundos

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (mMarker != null) {
                        mMarker.setPosition(latLng);
                    } else {
                        putMarker(latLng);
                    }
                }
            }
        };

        if(hasLocationPermission()){
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }


    private void putMarker(LatLng userLocation) {
        BitmapDescriptor icon;
        Car carInMemory = CarFactory.getCarInMemory(mainActivity.getApplicationContext());
        if(carInMemory.getId() == null){
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = mainActivity.getResources().getDrawable(R.drawable.img_byddolphin01);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 150, 100, false);
            icon = BitmapDescriptorFactory.fromBitmap(smallMarker);
        }
        else{
            Car car = new CarDaoSqlite(mainActivity).findOne(carInMemory.getId());
            try {
                Bitmap bitmap =   MediaStore.Images.Media.getBitmap(
                        mainActivity.getContentResolver() , Uri.parse(car.getCarModel().getPathImg()));
                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 150, 100, false);

                icon = BitmapDescriptorFactory.fromBitmap(
                        smallMarker
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }

        mMarker = mMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title(mainActivity.getString(R.string.your_location))
                .icon(icon));
    }


    public PlaceService getPlaceService() {
        return placeService;
    }

}