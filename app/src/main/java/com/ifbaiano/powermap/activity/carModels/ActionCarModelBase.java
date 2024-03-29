package com.ifbaiano.powermap.activity.carModels;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MenuActivity;
import com.ifbaiano.powermap.appearance.StatusBarAppearance;
import com.ifbaiano.powermap.dao.contracts.EletricCarModelDao;
import com.ifbaiano.powermap.dao.contracts.HybridCarModelDao;
import com.ifbaiano.powermap.dao.contracts.StorageDao;
import com.ifbaiano.powermap.dao.firebase.EletricCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.HybridCarModelDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.factory.BitmapCustomFactory;
import com.ifbaiano.powermap.verifier.CarModelVerifier;

abstract public class ActionCarModelBase extends AppCompatActivity {

    TextInputEditText name, year, fuelConsumption, energyConsumption;
    ProgressBar progressBar;
    ImageView imageView;
    AppCompatButton submitImgBtn, backButton, submitFormBtn;
    CarModelVerifier verifier;
    EletricCarModelDao eletricCarModelDao;
    HybridCarModelDao hybridCarModelDao;
    StorageDao storageDao;
    int type;
    byte[] byteArray = null;
    BitmapCustomFactory bitmapCustomFactory;
    RadioButton radioButton;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result ->  bitmapCustomFactory.onResult(result, true)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_car_model);
        StatusBarAppearance.changeStatusBarColor(this, R.color.sub_background_form);
        type = R.id.hybrid;

        this.syncViewObjects();
        this.makeInstances();

        submitImgBtn.setOnClickListener(v -> submitImage());
        backButton.setOnClickListener(v -> backActivity());
    }

    abstract public void submitForm();

    public boolean verifyCarModelValidity(boolean hasImg) {
        boolean verifyValid = false;

        if (type == R.id.eletric) {
            verifyValid = verifier.verifyCarModel(name, year, energyConsumption, submitImgBtn, hasImg);
        } else if (type == R.id.hybrid) {
            verifyValid = verifier.verifyCarModel(name, year, energyConsumption, submitImgBtn, hasImg, fuelConsumption);
        }

        return verifyValid;
    }

    public void onRadioButtonClicked(@NonNull View view) {
        radioButton =  ((RadioButton) view);
        boolean checked = radioButton.isChecked();
        this.type = view.getId();

        if(view.getId() == R.id.eletric && checked){
            fuelConsumption.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) submitFormBtn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_TOP, R.id.energyConsumption);
            submitFormBtn.setLayoutParams(params);
        }
        else if(view.getId() == R.id.hybrid && checked) {
            fuelConsumption.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) submitFormBtn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_TOP, R.id.fuelConsumption);
            submitFormBtn.setLayoutParams(params);
        }

    }

    private void makeInstances(){
        eletricCarModelDao = new EletricCarModelDaoFirebase(getApplicationContext());
        hybridCarModelDao = new HybridCarModelDaoFirebase(getApplicationContext());
        storageDao = new StorageDaoFirebase();
        verifier =  new CarModelVerifier(getApplicationContext());
        bitmapCustomFactory = new BitmapCustomFactory(
                this, byteArray, imageView, submitImgBtn
        );
    }

    private void syncViewObjects(){
        name = findViewById(R.id.name);
        year = findViewById(R.id.year);
        energyConsumption = findViewById(R.id.energyConsumption);
        fuelConsumption = findViewById(R.id.fuelConsumption);
        submitImgBtn = findViewById(R.id.submitImage);
        backButton = findViewById(R.id.backButon);
        submitFormBtn = findViewById(R.id.submitForm);
        imageView = findViewById(R.id.imageView);
        progressBar  = findViewById(R.id.progressBar);
    }

    private void submitImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    protected void backActivity(){
        startActivity(new Intent(this, MenuActivity.class));
    }


}