package com.ifbaiano.powermap.activity.users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MenuActivity;
import com.ifbaiano.powermap.appearance.StatusBarAppearance;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
import com.ifbaiano.powermap.dao.sqlite.UserDaoSqlite;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.fragment.ProfileFragment;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.service.CryptographyPasswordService;
import com.ifbaiano.powermap.service.UserService;
import com.ifbaiano.powermap.verifier.RegisterUserVerifier;

import java.util.Objects;

public class RegisterAdminActivity extends AppCompatActivity {


    Button backButonAdminRegiter, registerAdminBtn;
    TextInputEditText nameAdminRegister, emailAdminRegister,passwordAdminRegister, passwordConfirmeAdminRegister;

    RegisterUserVerifier verifier;
    UserDaoFirebase userDaoFirebase;
    UserDaoSqlite userDaoSqlite;
    UserService userRegisterService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);
        StatusBarAppearance.changeStatusBarColor(this, R.color.white);

        this.findViewsById();
        this.makeInstances();

        verifier =  new RegisterUserVerifier(getApplicationContext());


        registerAdminBtn.setOnClickListener(v -> {
            User user = UserFactory.getUserInMemory(getApplicationContext());

            if(user.isAdmin()==true){
                this.submitForm();
            } else{
                Toast.makeText(this, getString(R.string.error_admin_register), Toast.LENGTH_SHORT).show();
            }

        });



    }

    private void findViewsById(){

        findViewById(R.id.backButonAdminRegiter).setOnClickListener(v -> {
            startActivity(new Intent(this, MenuActivity.class));
        });

        backButonAdminRegiter = findViewById(R.id.backButonAdminRegiter);
        registerAdminBtn = findViewById(R.id.registerAdminBtn);
        nameAdminRegister  = findViewById(R.id.nameAdminRegister);
        emailAdminRegister  = findViewById(R.id.emailAdminRegister);
        passwordAdminRegister  = findViewById(R.id.passwordAdminRegister);
        passwordConfirmeAdminRegister  = findViewById(R.id.passwordConfirmeAdminRegister);
    }

    private void makeInstances(){
        userDaoFirebase = new UserDaoFirebase(this);
        userDaoSqlite = new UserDaoSqlite(this);
        userRegisterService  = new UserService(userDaoFirebase);
    }


    private void submitForm() {
        new Thread(() -> {
            boolean emailAlreadyExists = checkEmailExists();
            //não está chamando o submit
            Log.d("Submit", "submit foi chamdo ");

            if (verifyFormValidity(emailAlreadyExists)) {
                String passwordCryp = CryptographyPasswordService.encryptPassword(passwordAdminRegister.getText().toString().trim());
                User newUser = new User(
                        Objects.requireNonNull(nameAdminRegister.getText()).toString().trim(),
                        Objects.requireNonNull(emailAdminRegister.getText()).toString().trim(),
                        passwordCryp,
                        true
                );

                Log.d("Register admin", "usario salvo: "+newUser.getPassword());
                Log.d("Register admin nome", "usario salvo: "+newUser.getName());



                User userAddedFirebase = userRegisterService.add(newUser);
                userRegisterService.setDao(new UserDaoSqlite(getApplicationContext()));
                User userAddedSqlite = userRegisterService.add(newUser);

                executeAfterRegistration(userAddedFirebase != null&& userAddedSqlite != null, userAddedSqlite);
            }
        }).start();
    }


    private boolean checkEmailExists() {
        String emailText = Objects.requireNonNull(emailAdminRegister.getText()).toString().trim();
        return userRegisterService.findByEmail(emailText);
    }

    private void executeAfterRegistration(boolean isUserRegisteredFirebase, User user) {
        runOnUiThread(() -> {
            if (isUserRegisteredFirebase) {
                // Se bem-sucedido, vai para a tela de listar carros
                Intent intent= new Intent(RegisterAdminActivity.this, ProfileFragment.class);
                startActivity(intent);
            } else {
                // Se não for bem-sucedido
                Toast.makeText(RegisterAdminActivity.this, getString(R.string.error_register), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean verifyFormValidity(boolean emailAlreadyExists) {
        boolean[] verifyValid = {false};
        Object lock = new Object();

        runOnUiThread(() -> {

            verifyValid[0] = verifier.verifyRegisterUser(nameAdminRegister, emailAdminRegister, passwordAdminRegister, passwordConfirmeAdminRegister, emailAlreadyExists);
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