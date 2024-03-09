package com.ifbaiano.powermap.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
import android.util.Log;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.model.CarModel;
import com.ifbaiano.powermap.model.HybridCarModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlaceService extends AsyncTask<String, Void, String> {
    private DirectionService directionService;
    private CarModel carModel;
    private String ELETRIC_TYPE = "electric_vehicle_charging_station";
    private String FUEL_TYPE = "gas_station";
    private final LatLng userLocation;
    private LatLng moreCloserStation;

    private final GoogleMap mMap;
    @SuppressLint("StaticFieldLeak")
    private final Context ctx;

    public PlaceService(CarModel carModel, LatLng userLocation, Context ctx, GoogleMap mMap) {
        this.carModel = carModel;
        this.userLocation = userLocation;
        this.mMap = mMap;
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length < 1) {
            return null;
        }

        OkHttpClient client = new OkHttpClient();
        String jsonResponse = null;

        try {
            String jsonBody = generateJsonBody(userLocation);
            Request request = buildRequest(params[0], jsonBody);

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null ) {
                jsonResponse = response.body().string();
            } else {
                Log.e("PlacesTask", "Failed to fetch nearby EV charging stations");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }

    private void markPlaces(JSONArray placesArray) {
        try {
            for (int i = 0; i < placesArray.length(); i++) {
                JSONObject placeObject = placesArray.getJSONObject(i);
                String name = placeObject.getJSONObject("displayName").getString("text");
                LatLng latLng = extractLatLng(placeObject);

                addMarkerToMap(name, latLng);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("REQUEST PLACES API", result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray placesArray = jsonObject.getJSONArray("places");

             markPlaces(placesArray);

            if (placesArray.length() > 0) {
                moreCloserStation = extractLatLng(placesArray.getJSONObject(0));
                this.directionService = new DirectionService(moreCloserStation, ctx, mMap);
                this.directionService.execute(userLocation, moreCloserStation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateJsonBody(LatLng userLocation) {
        String includedTypes = "\""+ ELETRIC_TYPE + "\"";

        if(carModel != null && carModel instanceof HybridCarModel){
            includedTypes += ",\""+ FUEL_TYPE +"\"";
        }

        return "{\"" +
                "rankPreference\": \"DISTANCE\"," +
                " \"includedTypes\": ["+ includedTypes +"]," +
                "\"maxResultCount\": 10," +
                " \"locationRestriction\": " +
                "{\"circle\": " +
                "{\"center\": " +
                "{\"latitude\": " + userLocation.latitude + "," +
                " \"longitude\": " + userLocation.longitude + "}, " +
                "\"radius\": 50000}}}";
    }

    private Request buildRequest(String url, String jsonBody) {

        return new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Goog-Api-Key", ctx.getString(R.string.maps_key))
                .addHeader("X-Goog-FieldMask", "places.displayName,places.location,places.types")
                .post(RequestBody.create(MediaType.parse("application/json"), jsonBody))
                .build();
    }

    private LatLng extractLatLng(JSONObject placeObject) throws JSONException {
        JSONObject location = placeObject.getJSONObject("location");
        double lat = location.getDouble("latitude");
        double lng = location.getDouble("longitude");
        return new LatLng(lat, lng);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void addMarkerToMap(String name, LatLng latLng) {
         mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }


    public LatLng getUserLocation() {
        return userLocation;
    }

    public DirectionService getDirectionService() {
        return directionService;
    }


}
