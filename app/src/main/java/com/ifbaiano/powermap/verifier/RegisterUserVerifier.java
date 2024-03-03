package com.ifbaiano.powermap.verifier;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterUserVerifier {
    private Context ctx;
    private UserDaoFirebase userDaoFirebase;


    public RegisterUserVerifier(Context ctx) {
        this.ctx = ctx;
        this.userDaoFirebase = userDaoFirebase;
    }



    protected boolean validateFieldRegister(TextInputEditText field, int errorMessageResId) {
        if (TextUtils.isEmpty(field.getText())) {
            field.setError(ctx.getString(errorMessageResId));
            return false;
        }
        return true;
    }

    public boolean verifyRegisterUser(TextInputEditText name, TextInputEditText email, TextInputEditText password, TextInputEditText passwordConfirm) {
        boolean isValid = true;

        isValid &= validateFieldRegister(name, R.string.name_error_register);
        isValid &= validateFieldRegister(email, R.string.email_error);
        isValid &= validateFieldRegister(password, R.string.password_error);
        isValid &= validateFieldRegister(passwordConfirm, R.string.password_confirm_error);

        String nameText = name.getText().toString();

        // Verifica se o nome contém pelo menos o primeiro e o segundo nome
        if (!isValidName(nameText)) {
            name.setError(ctx.getString(R.string.name_invalid_error));
            isValid = false;
        }


        String emailText = email.getText().toString();

        /*
        if (emailExists(emailText)) {
            email.setError(ctx.getString(R.string.email_already_registered_error));
            Log.d("Firebase", "email erro " + emailText);
            return false;
        }*/

        // Verifica se o e-mail tem um formato válido
        if (!isValidEmail(emailText)) {
            email.setError(ctx.getString(R.string.email_invalid_error));
            isValid = false;
        }

        String passwordText = password.getText().toString();

        // Verifica se a senha tem pelo menos 8 caracteres
        if (passwordText.length() < 8) {
            password.setError(ctx.getString(R.string.password_length_error));
            isValid = false;
        }

        // Verifica se a senha contém letras e números
        if (!passwordText.matches(".*[a-zA-Z].*") || !passwordText.matches(".*\\d.*")) {
            password.setError(ctx.getString(R.string.password_alpha_numeric_error));
            isValid = false;
        }

        // Verifica se a senha contém símbolos
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(passwordText);
        if (!matcher.find()) {
            password.setError(ctx.getString(R.string.password_symbol_error));
            isValid = false;
        }

        // Verifica se as senhas coincidem
        if (!TextUtils.equals(passwordText, passwordConfirm.getText().toString())) {
            passwordConfirm.setError(ctx.getString(R.string.password_mismatch_error));
            isValid = false;
        }

        return isValid;
    }


    private boolean isValidName(String name) {
        // Divide o nome em partes
        String[] nameParts = name.split("\\s+");
        // Verifica se há pelo menos duas partes (primeiro e último nome)
        return nameParts.length >= 2;
    }

    private boolean isValidEmail(String email) {
        // Expressão regular para validar o formato do e-mail
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean emailExists(String email) {
        return userDaoFirebase.findAll()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }


}
