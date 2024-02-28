package com.ifbaiano.powermap.verifier;

import android.content.Context;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;

public class RegisterUserVerifier extends Verifier {


    public RegisterUserVerifier(Context ctx) {
        super(ctx);
    }



    public boolean verifyRegisterUser(TextInputEditText name, TextInputEditText email, TextInputEditText password, TextInputEditText passwordConfirm ) {
        boolean isValid = true;

        isValid = validateField(name, R.string.name_error_register);
        isValid &= validateField(email, R.string.email_error);
        isValid &= validateField(password, R.string.password_error);
        isValid &= validateField(passwordConfirm, R.string.password_confirm_error);

        return isValid;
    }
}
