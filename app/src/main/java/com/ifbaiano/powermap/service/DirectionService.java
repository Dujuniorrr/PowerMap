package com.ifbaiano.powermap.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.ifbaiano.powermap.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DirectionService extends AsyncTask<LatLng, Void, List<LatLng>> {
    private final LatLng moreCloserStation;
    private GeoApiContext geoApiContext;
    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    private final GoogleMap mMap;
    public double totalDistance;


    public DirectionService(LatLng moreCloserStation, Context ctx, GoogleMap mMap) {
        this.moreCloserStation = moreCloserStation;
        this.ctx = ctx;
        this.mMap = mMap;
    }

    @Override
    protected List<LatLng> doInBackground(LatLng... params) {
        DirectionsResult directionsResult = null;
        try {
            geoApiContext = new GeoApiContext();
            geoApiContext.setApiKey(ctx.getString(R.string.maps_key));

            directionsResult = requestDirections(params[0], params[1]);
        } catch (InterruptedException | IOException |
                 com.google.maps.errors.ApiException e) {
            e.printStackTrace();
        }

        if (directionsResult != null) {
            return decodePath(directionsResult);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<LatLng> path) {
        super.onPostExecute(path);
        if (path != null) {
            drawPolyline(path);
        }
        animateCamera(moreCloserStation);
    }

    private DirectionsResult requestDirections(LatLng origin, LatLng destination) throws InterruptedException, IOException, com.google.maps.errors.ApiException {
        return DirectionsApi.newRequest(geoApiContext)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .await();
    }

    private List<LatLng> decodePath(DirectionsResult directionsResult) {
        DirectionsRoute route = directionsResult.routes[0];
        List<com.google.maps.model.LatLng> decodedPath = route.overviewPolyline.decodePath();

        List<LatLng> path = new ArrayList<>();
        for (com.google.maps.model.LatLng point : decodedPath) {
            path.add(new LatLng(point.lat, point.lng));
        }

        DirectionsLeg[] legs = route.legs;
        double totalDistance = 0;

        for (DirectionsLeg leg : legs) {
            totalDistance += leg.distance.inMeters;
        }

        this.totalDistance = totalDistance / 1000;

        return path;
    }

    private void drawPolyline(List<LatLng> path) {
        mMap.addPolyline(new PolylineOptions()
                .addAll(path)
                .width(6)
                .color(Color.RED));
    }

    private void animateCamera(LatLng position) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(20)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}