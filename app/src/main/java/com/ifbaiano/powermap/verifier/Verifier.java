package com.ifbaiano.powermap.verifier;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

abstract public class Verifier {
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


    protected boolean validateFieldText(TextView field, int errorMessageResId) {
        if (TextUtils.isEmpty(field.getText())) {
            field.setError(this.getCtx().getString(errorMessageResId));
            return false;
        }
        return true;
    }



}
