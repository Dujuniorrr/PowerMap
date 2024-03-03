package com.ifbaiano.powermap.activity.users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.car.ListCarActivity;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
import com.ifbaiano.powermap.dao.sqlite.UserDaoSqlite;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.service.UserService;
import com.ifbaiano.powermap.verifier.RegisterUserVerifier;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterActivity extends AppCompatActivity {

    Button backButonRegister, registerUserBtn;
    RegisterUserVerifier verifier;

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
                Intent intent;
                intent = new Intent(RegisterActivity.this, InitialUsersActivity.class);
                startActivity(intent);
            }
        );
    }
    
    private void findViewsById(){
        backButonRegister = findViewById(R.id.backButonUserRegiter);
        registerUserBtn = findViewById(R.id.RegisterUserBtn);
        nameUserRegister  = findViewById(R.id.nameUserRegister);
        emailUserRegister  = findViewById(R.id.emailUserRegister);
        passwordUserRegister  = findViewById(R.id.passwordUserRegister);
        passwordUserRegisterConfirme  = findViewById(R.id.passwordUserRegisterConfirme);
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
                User newUser = new User(
                        Objects.requireNonNull(nameUserRegister.getText()).toString().trim(),
                        Objects.requireNonNull(emailUserRegister.getText()).toString().trim(),
                        Objects.requireNonNull(passwordUserRegister.getText()).toString().trim(),
                        false
                );

                boolean isUserRegisteredFirebase = userRegisterService.add(newUser);

                executeAfterRegistration(isUserRegisteredFirebase);
            }
        }).start();
    }

    private boolean checkEmailExists() {
        String emailText = Objects.requireNonNull(emailUserRegister.getText()).toString().trim();
        return userRegisterService.findByEmail(emailText);
    }

    private void executeAfterRegistration(boolean isUserRegisteredFirebase) {
        runOnUiThread(() -> {
            if (isUserRegisteredFirebase) {
                // Se bem-sucedido, vai para a tela de listar carros
                Intent intent= new Intent(RegisterActivity.this, ListCarActivity.class);
                startActivity(intent);
            } else {
                // Se não for bem-sucedido, exibe uma mensagem de erro
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