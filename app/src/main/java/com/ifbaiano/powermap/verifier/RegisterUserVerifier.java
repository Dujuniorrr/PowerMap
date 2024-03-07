package com.ifbaiano.powermap.verifier;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.service.CryptographyPasswordService;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterUserVerifier extends Verifier {

    private final String EMAIL_VALID =  "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    private final String SYMBOLS_VALID = "[^a-zA-Z0-9]";
    private final String LETTERS_VALID = ".*[a-zA-Z].*";
    private final String NUMBERS_VALID = ".*\\d.*";
    private final String HAS_SPACE = "\\s+";

    public RegisterUserVerifier(Context ctx) {
        super(ctx);
    }

    public boolean verifyRegisterUser(TextInputEditText name, TextInputEditText email, TextInputEditText password, TextInputEditText passwordConfirm, boolean emailAlreadyExists) {
        boolean isValid = true;

        isValid &= validateField(name, R.string.name_error_register);
        isValid &= validateField(email, R.string.email_error);
        isValid &= validateField(password, R.string.password_error);
        isValid &= validateField(passwordConfirm, R.string.password_confirm_error);
        isValid &= isValidName(name, R.string.name_invalid_error);
        isValid &= isValidEmail(email, R.string.email_invalid_error);
        isValid &= isValidPassword(password);
        isValid &= passwordsMismatch(password, passwordConfirm, R.string.password_mismatch_error);
        isValid &= emailExists(email, emailAlreadyExists, R.string.email_already_registered_error);

        return isValid;
    }

    public boolean verifyEditUser(TextInputEditText name, TextInputEditText email, boolean emailAlreadyExists) {
        boolean isValid = true;

        isValid &= validateField(name, R.string.name_error_register);
        isValid &= isValidName(name, R.string.name_invalid_error);
        isValid &= isValidEmail(email, R.string.email_invalid_error);

        // Check if the provided email is the same as the one stored in SharedPreferences
        String storedEmail = UserFactory.getUserInMemory(getCtx()).getEmail();
        if (!email.getText().toString().equals(storedEmail)) {
            // If the provided email is different, call emailExists method
            isValid &= emailExists(email, emailAlreadyExists, R.string.email_already_registered_error);
        }

        return isValid;
    }

    public boolean verifyPasswordEdit(TextInputEditText passworOld, TextInputEditText passwordNew) {
        boolean isValid = true;

        isValid &= validateField(passworOld, R.string.password_error);
        isValid &= validateField(passwordNew, R.string.password_error);
        isValid &= isValidPassword(passwordNew);
        isValid &= oldPasswordMatches(passworOld, R.string.password_mismatch_error);

        return isValid;
    }


    public boolean verifyPasswordLogin( TextInputEditText email, TextInputEditText password) {
        boolean isValid = true;

        isValid &= validateField(email, R.string.email_error);
        isValid &= validateField(password, R.string.password_error);
        isValid &= isValidEmail(email, R.string.email_invalid_error);
        return isValid;
    }


    private boolean isValidName(TextInputEditText name, int messageId) {
        String nameText = Objects.requireNonNull(name.getText()).toString();
        String[] nameParts = nameText.split(HAS_SPACE);

        if(nameParts.length < 2){
            name.setError(this.getCtx().getString(messageId));
            return false;
        }

        return true;
    }

    private boolean passwordsMismatch(TextInputEditText password, TextInputEditText passwordConfirm, int messageId){
        String passwordText = Objects.requireNonNull(password.getText()).toString();
        String passwordConfirmText = Objects.requireNonNull(passwordConfirm.getText()).toString();
        if (!TextUtils.equals(passwordText, passwordConfirmText)) {
            passwordConfirm.setError(getCtx().getString(messageId));
            return false;
        } else{
            return true;
        }
    }

    private boolean oldPasswordMatches(TextInputEditText passwordOld, int messageId){

        String passwordShared = UserFactory.getUserInMemory(getCtx()).getPassword();
        String passwordText = Objects.requireNonNull(passwordOld.getText()).toString();

        String passwordCryp = CryptographyPasswordService.encryptPassword(passwordText.toString());

        if (!passwordCryp.equals(passwordShared)) {
            passwordOld.setError(getCtx().getString(messageId));
            return false;
        } else{

            return true;

        }

    }


    private boolean isValidPassword(TextInputEditText password){
        return this.validatePasswordLength(password, R.string.password_length_error) &&
                this.validatePasswordLettersAndNumbers(password, R.string.password_alpha_numeric_error)&&
                this.validatePasswordSymbols(password, R.string.password_symbol_error);
    }

    private boolean validatePasswordLettersAndNumbers(TextInputEditText password, int messageId){
        String passwordText = Objects.requireNonNull(password.getText()).toString();
        if (!passwordText.matches(LETTERS_VALID) || !passwordText.matches(NUMBERS_VALID)) {
            password.setError(this.getCtx().getString(messageId));
            return false;
        }
        return true;
    }

    private boolean validatePasswordSymbols(TextInputEditText password, int messageId){
        String passwordText = Objects.requireNonNull(password.getText()).toString();

        Pattern pattern = Pattern.compile(SYMBOLS_VALID);
        Matcher matcher = pattern.matcher(passwordText);
        if (!matcher.find()) {
            password.setError(this.getCtx().getString(messageId));
           return false;
        }
        return true;
    }

    private boolean validatePasswordLength(TextInputEditText password, int messageId){
        String passwordText = Objects.requireNonNull(password.getText()).toString();
        if (passwordText.length() < 8) {
            password.setError(this.getCtx().getString(messageId));
            return false;
        }
        return true;
    }

    private boolean isValidEmail(TextInputEditText email, int messageId) {
        String emailText = Objects.requireNonNull(email.getText()).toString();

        // Expressão regular para validar o formato do e-mail
        Pattern pattern = Pattern.compile(EMAIL_VALID);
        if(!pattern.matcher(emailText).matches()){
            email.setError(this.getCtx().getString(messageId));
            return false;
        }

        return true;
    }


    private boolean emailExists(TextInputEditText email, boolean alreadyExists, int messageId) {
         if(alreadyExists){
             email.setError(this.getCtx().getString(messageId));
         }

        return !alreadyExists;
    }
}
