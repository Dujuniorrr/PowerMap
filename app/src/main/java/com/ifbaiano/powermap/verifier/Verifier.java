package com.ifbaiano.powermap.verifier;

import android.content.Context;
import android.text.TextUtils;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;

import java.time.Year;
import java.util.Objects;

public class Verifier {
    private Context ctx;

    public Verifier(Context ctx) {
        this.ctx = ctx;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    protected boolean validateField(TextInputEditText field, int errorMessageResId) {
        if (TextUtils.isEmpty(field.getText())) {
            field.setError(this.getCtx().getString(errorMessageResId));
            return false;
        }
        return true;
    }


}
