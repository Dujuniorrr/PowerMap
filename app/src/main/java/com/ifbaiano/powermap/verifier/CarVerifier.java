package com.ifbaiano.powermap.verifier;

import android.content.Context;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.model.CarModel;

public class CarVerifier extends Verifier {
    public CarVerifier(Context ctx) {
        super(ctx);
    }

    public boolean verifyCar(TextInputEditText name, boolean carModelIsSelected ){
       return validateField(name, R.string.name_error) && validateCarModel(carModelIsSelected, R.string.selected_car_model_error);
    }

    private boolean validateCarModel(boolean carModelIsSelected, int messageId){
      if(!carModelIsSelected){
          Toast.makeText(getCtx(), getCtx().getString(messageId), Toast.LENGTH_SHORT).show();
      }
      return carModelIsSelected;
    }
}
