package com.ifbaiano.powermap.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.ifbaiano.powermap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlaceService extends AsyncTask<String, Void, String> {

    private LatLng userLocation;
    private final GoogleMap mMap;
    private Context ctx;

    public PlaceService(LatLng userLocation, Context ctx, GoogleMap mMap) {
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
            //electric_vehicle_charging_station
            String jsonBody = "{\"includedTypes\": [\"electric_vehicle_charging_station\"], \"maxResultCount\": 10, \"locationRestriction\": {\"circle\": {\"center\": {\"latitude\": " + this.userLocation.latitude + ", \"longitude\": " + this.userLocation.longitude + "}, \"radius\": 50000}}}";

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);
            Request request = new Request.Builder()
                    .url(params[0]) // A URL é passada como o primeiro parâmetro
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Goog-Api-Key", ctx.getString(R.string.maps_key))
                    .addHeader("X-Goog-FieldMask", "places.addressComponents,places.location")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                jsonResponse = response.body().string();
            } else {
                Log.e("PlacesTask", "Failed to fetch nearby EV charging stations");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray placesArray = jsonObject.getJSONArray("places");

            for (int i = 0; i < placesArray.length(); i++) {
                JSONObject placeObject = placesArray.getJSONObject(i);
                String name = placeObject.getJSONArray("addressComponents").getJSONObject(0).getString("longText");

                JSONObject location = placeObject.getJSONObject("location");
                double lat = location.getDouble("latitude");
                double lng = location.getDouble("longitude");

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

            if (placesArray.length() > 0) {
                JSONObject location = placesArray.getJSONObject(0).getJSONObject("location");
                double lat = location.getDouble("latitude");
                double lng = location.getDouble("longitude");
                LatLng moreCloserStation = new LatLng(lat, lng);

                new GetDirectionsTask(moreCloserStation).execute(userLocation, moreCloserStation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetDirectionsTask extends AsyncTask<LatLng, Void, List<LatLng>> {
        LatLng moreCloserStation;

        public GetDirectionsTask( LatLng moreCloserStation) {
            this.moreCloserStation = moreCloserStation;
        }

        @Override
        protected List<LatLng> doInBackground(LatLng... params) {
            DirectionsResult directionsResult = null;
            try {
                GeoApiContext geoApiContext = new GeoApiContext();
                geoApiContext.setApiKey(ctx.getString(R.string.maps_key));

                directionsResult = DirectionsApi.newRequest(geoApiContext)
                        .origin(new com.google.maps.model.LatLng(params[0].latitude, params[0].longitude))
                        .destination(new com.google.maps.model.LatLng(params[1].latitude, params[1].longitude))
                        .await();
            } catch (InterruptedException | IOException |
                     com.google.maps.errors.ApiException e) {
                e.printStackTrace();
            }

            if (directionsResult != null) {
                DirectionsRoute route = directionsResult.routes[0];
                List<com.google.maps.model.LatLng> decodedPath = route.overviewPolyline.decodePath();

                List<LatLng> path = new ArrayList<>();
                for (com.google.maps.model.LatLng point : decodedPath) {
                    path.add(new LatLng(point.lat, point.lng));
                }
                return path;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<LatLng> path) {
            super.onPostExecute(path);
            if (path != null) {
                mMap.addPolyline(new PolylineOptions()
                        .addAll(path)
                        .width(6)
                        .color(Color.RED));


            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(moreCloserStation)// Define o novo centro do mapa
                    .zoom(9) // Define o nível de zoom desejado
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }


}