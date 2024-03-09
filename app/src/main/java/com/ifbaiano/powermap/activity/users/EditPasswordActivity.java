package com.ifbaiano.powermap.activity.users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
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


public class EditPasswordActivity extends AppCompatActivity {

    Button backButonProfilePassword, editProfileUserBtn ;
    ImageView imageProfilEditPassword;
    TextInputEditText editPasswordText, editPasswordNewText, editPassworConfirmedNewText;
    TextView textNameEditP;
    UserDaoFirebase userDaoFirebase;
    UserService userRegisterService;
    UserDaoSqlite userDaoSqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        this.findViewsById();
        this.setUserAttributes();
        this.makeInstances();
        UserDaoSqlite userDao = new UserDaoSqlite(this);


        editPasswordText.getText();


        editProfileUserBtn.setOnClickListener(v -> {
            RegisterUserVerifier passwordVerifier = new RegisterUserVerifier(this);

            boolean passwordsValid = passwordVerifier.verifyPasswordEdit(editPasswordText, editPasswordNewText, editPassworConfirmedNewText);

            if (passwordsValid) {
                User newUser = UserFactory.getUserInMemory(this);
                String password = Objects.requireNonNull(editPasswordNewText.getText()).toString().trim();

                // Criptografar a senha antes de salvar
                String encryptedPassword = CryptographyPasswordService.encryptPassword(password);
                newUser.setPassword(encryptedPassword);

                User userEditFirebase = userRegisterService.edit(newUser);
                userRegisterService.setDao(new UserDaoSqlite(this));
                User userEditSqlite = userRegisterService.edit(newUser);

                this.executeAfterRegistration(userEditFirebase != null && userEditSqlite != null, userEditSqlite);
            }


        });


    }


    private void findViewsById() {
        findViewById(R.id.backButonProfilePassword).setOnClickListener(v -> {
            startActivity(new Intent(this, MenuActivity.class));
        });
        textNameEditP = findViewById(R.id.textNameEditP);
        backButonProfilePassword = findViewById(R.id.backButonProfilePassword);
        editProfileUserBtn = findViewById(R.id.editProfileUserBtn);
        imageProfilEditPassword = findViewById(R.id.imageProfilEditPassword);
        editPasswordText = findViewById(R.id.editPasswordText);
        editPasswordNewText = findViewById(R.id.editPasswordNewText);
        editPassworConfirmedNewText = findViewById(R.id.editPassworConfirmedNewText);
    }

    private void makeInstances() {
        userDaoFirebase = new UserDaoFirebase(this);
        userDaoSqlite = new UserDaoSqlite(this);
        userRegisterService = new UserService(userDaoFirebase);
    }

    private void setUserAttributes() {

        User user = UserFactory.getUserInMemory(this);

        if (user != null) {
            String userName = user.getName().toString();
            textNameEditP.setText(userName);

            // new BitmapCustomFactory(this, imageProfilEditPassword).setImageByUri(user.getImgpath(), R.drawable.baseline_person);
        } else {
            startActivity(new Intent(this, InitialUsersActivity.class));
        }

    }

    private void executeAfterRegistration(boolean isUserRegisteredFirebase, User user) {



            if (isUserRegisteredFirebase) {
                Toast.makeText(this, getString(R.string.success_edit), Toast.LENGTH_SHORT).show();

                UserFactory.saveUserInMemory(user, EditPasswordActivity.this);

                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
            } else {
                // If unsuccessful
                Toast.makeText(this, getString(R.string.error_register), Toast.LENGTH_SHORT).show();
            }
    }





}