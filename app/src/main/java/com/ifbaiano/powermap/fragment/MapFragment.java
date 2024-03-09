package com.ifbaiano.powermap.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.appearance.CalculateDialogAppearance;
import com.ifbaiano.powermap.appearance.SelectCurrentCarDialogAppearance;
import com.ifbaiano.powermap.service.DirectionService;
import com.ifbaiano.powermap.service.LocationService;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public GoogleMap mMap;
    AppCompatButton calculate, changeCar;
    private CalculateDialogAppearance calculateDialogAppearance;
    private SelectCurrentCarDialogAppearance selectCurrentCarDialogAppearance;

    LocationService locationService;
    private AppCompatActivity mainActivity;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public MapFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (AppCompatActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        this.findViewsById(view);

        calculateDialogAppearance = new CalculateDialogAppearance(requireContext(), locationService);

        selectCurrentCarDialogAppearance = new SelectCurrentCarDialogAppearance( requireContext(), mainActivity, this, calculateDialogAppearance);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    public void findViewsById(View view){
        calculate = view.findViewById(R.id.calculate);
        changeCar =  view.findViewById(R.id.changeCar);

        calculate.setOnClickListener(v -> {
            calculateDialogAppearance.createCalculateDialog();
        });

       changeCar.setOnClickListener(v -> {
            selectCurrentCarDialogAppearance.createSelectDialog();
        });

    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

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

    public void getCurrentLocation() {
         locationService = new LocationService(mainActivity, mMap);
         locationService.getCurrentLocation();
         calculateDialogAppearance.setLocationService(locationService);
         calculate.setVisibility(View.VISIBLE);
         changeCar.setVisibility(View.VISIBLE);
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
