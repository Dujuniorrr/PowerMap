package com.ifbaiano.powermap.activity.users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MenuActivity;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
import com.ifbaiano.powermap.dao.sqlite.UserDaoSqlite;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.service.CryptographyPasswordService;
import com.ifbaiano.powermap.service.UserService;
import com.ifbaiano.powermap.verifier.RegisterUserVerifier;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    Button backButonRegister, registerUserBtn;
    RegisterUserVerifier verifier;
    TextView textAccountLogin;

    TextInputEditText nameUserRegister, emailUserRegister,passwordUserRegister, passwordUserRegisterConfirme;

    UserDaoFirebase userDaoFirebase;
    UserDaoSqlite userDaoSqlite;
    UserService userRegisterService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.findViewsById();
        this.makeInstances();
//        userRegisterService.setDao(userDaoSqlite);
        
        verifier =  new RegisterUserVerifier(getApplicationContext());

        registerUserBtn.setOnClickListener(v -> { submitForm(); });

        backButonRegister.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterActivity.this, InitialUsersActivity.class);
                startActivity(intent);
            }
        );


        textAccountLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

    }

    private void findViewsById(){
        backButonRegister = findViewById(R.id.backButonUserRegiter);
        registerUserBtn = findViewById(R.id.RegisterUserBtn);
        nameUserRegister  = findViewById(R.id.nameUserRegister);
        emailUserRegister  = findViewById(R.id.emailUserRegister);
        passwordUserRegister  = findViewById(R.id.passwordUserRegister);
        passwordUserRegisterConfirme  = findViewById(R.id.passwordUserRegisterConfirme);
        textAccountLogin = findViewById(R.id.textAccountLogin);
    }
    private void makeInstances(){
        userDaoFirebase = new UserDaoFirebase(this);
        userDaoSqlite = new UserDaoSqlite(this);
        userRegisterService  = new UserService(userDaoFirebase);
    }

    private void submitForm() {
        new Thread(() -> {
            boolean emailAlreadyExists = checkEmailExists();

            if (verifyFormValidity(emailAlreadyExists)) {
                String passwordCryp = CryptographyPasswordService.encryptPassword(passwordUserRegister.getText().toString().trim());
                User newUser = new User(
                        Objects.requireNonNull(nameUserRegister.getText()).toString().trim(),
                        Objects.requireNonNull(emailUserRegister.getText()).toString().trim(),
                        passwordCryp,
                        false
                );
                User newUserSqlite = new User(
                        Objects.requireNonNull(nameUserRegister.getText()).toString().trim(),
                        Objects.requireNonNull(emailUserRegister.getText()).toString().trim(),
                        passwordCryp,
                        false
                );

                User userAddedFirebase = userRegisterService.add(newUser);
                userRegisterService.setDao(new UserDaoSqlite(getApplicationContext()));
                User userAddedSqlite = userRegisterService.add(newUserSqlite);
                Log.d("ID FIREBASE", userAddedFirebase.getId());

                executeAfterRegistration(userAddedFirebase != null&& userAddedSqlite != null, userAddedSqlite, userAddedFirebase);
            }
        }).start();
    }

    private boolean checkEmailExists() {
        String emailText = Objects.requireNonNull(emailUserRegister.getText()).toString().trim();
        return userRegisterService.findByEmail(emailText) != null;
    }

    private void executeAfterRegistration(boolean isUserRegisteredFirebase, User user, User userF) {
        runOnUiThread(() -> {
            if (isUserRegisteredFirebase) {
                // Se bem-sucedido, vai para a tela de listar carros
                UserFactory.saveUserInMemoryFirebase(userF, getApplicationContext());
                UserFactory.saveUserInMemory(user, getApplicationContext());
                Log.d("USER SHARED", UserFactory.getUserInMemory(getApplicationContext()).getPassword());

                Intent intent= new Intent(RegisterActivity.this, MenuActivity.class);
                startActivity(intent);
            } else {
                // Se não for bem-sucedido
                Toast.makeText(RegisterActivity.this, getString(R.string.error_register), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private boolean verifyFormValidity(boolean emailAlreadyExists) {
        boolean[] verifyValid = {false};
        Object lock = new Object();

        runOnUiThread(() -> {

            verifyValid[0] = verifier.verifyRegisterUser(nameUserRegister, emailUserRegister, passwordUserRegister, passwordUserRegisterConfirme, emailAlreadyExists);
            synchronized (lock) {
                lock.notify();
            }
        });

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return verifyValid[0];
    }

}