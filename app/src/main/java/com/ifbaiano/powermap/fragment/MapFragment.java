package com.ifbaiano.powermap.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
<<<<<<<<< Temporary merge branch 1
=========
import android.os.AsyncTask;
>>>>>>>>> Temporary merge branch 2
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.appearance.CalculateDialogAppearance;
import com.ifbaiano.powermap.service.LocationService;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CalculateDialogAppearance calculateDialogAppearance;
    LocationService locationService;
    private AppCompatActivity mainActivity;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public MapFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (AppCompatActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        calculateDialogAppearance = new CalculateDialogAppearance(requireContext(), locationService);

        view.findViewById(R.id.calculate).setOnClickListener(v -> {
            calculateDialogAppearance.createCalculateDialog();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Verificar permissões
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
         locationService = new LocationService(mainActivity, mMap);
         locationService.getCurrentLocation();
         calculateDialogAppearance.setLocationService(locationService);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            } else {
                Toast.makeText(requireContext(), mainActivity.getString(R.string.error_permission_location), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
