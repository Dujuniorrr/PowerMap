package com.ifbaiano.powermap.activity.users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.car.ListCarActivity;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
import com.ifbaiano.powermap.dao.sqlite.UserDaoSqlite;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.service.UserRegisterService;
import com.ifbaiano.powermap.verifier.RegisterUserVerifier;

public class RegisterActivity extends AppCompatActivity {

    Button backButonRegister, RegisterUserBtn;
    RegisterUserVerifier verifier;

    TextInputEditText nameUserRegister, emailUserRegister,passwordUserRegister, passwordUserRegisterConfirme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backButonRegister = findViewById(R.id.backButonUserRegiter);
        RegisterUserBtn = findViewById(R.id.RegisterUserBtn);
        nameUserRegister  = findViewById(R.id.nameUserRegister);
        emailUserRegister  = findViewById(R.id.emailUserRegister);
        passwordUserRegister  = findViewById(R.id.passwordUserRegister);
        passwordUserRegisterConfirme  = findViewById(R.id.passwordUserRegisterConfirme);

        UserDaoFirebase userDaoFirebase = new UserDaoFirebase(this);
        UserDaoSqlite userDaoSqlite = new UserDaoSqlite(this);

        UserRegisterService userRegisterServiceFirebase = new UserRegisterService(userDaoFirebase);
        UserRegisterService userRegisterServiceSqlite = new UserRegisterService(userDaoSqlite);


      verifier =  new RegisterUserVerifier(getApplicationContext());

        RegisterUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user input from EditText fields
                boolean verifyValid = false;
                String name = nameUserRegister.getText().toString().trim();
                String email = emailUserRegister.getText().toString().trim();
                String password = passwordUserRegister.getText().toString().trim();
                String passwordConfirm = passwordUserRegisterConfirme.getText().toString().trim();


                verifyValid = verifier.verifyRegisterUser(nameUserRegister, emailUserRegister, passwordUserRegister, passwordUserRegisterConfirme);

                //Verifica se todos os campos estão preenchidos
                if (verifyValid) {
                    // Cria um objeto User com as informações recuperadas
                    User newUser = new User();
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setPassword(passwordConfirm);
                    newUser.setAdmin(false);  // Assuming isAdmin is a boolean property
                    newUser.setImgpath(null);

                    // Firebase
                    boolean isUserRegisteredFirebase = userRegisterServiceFirebase.add(newUser);
                    if (isUserRegisteredFirebase) {
                        Log.d("Firebase", "Usuário adicionado ao firbase: " + newUser.getName());
                    } else {
                        Log.d("Firebase", "não adicionado ao firebase ");
                    }


                    /*
                    // SQLite
                    boolean isUserRegisteredSqlite = userRegisterServiceSqlite.add(newUser);
                    if (isUserRegisteredSqlite) {
                        Log.d("SQLlite", "Usuário adicionado ao sqlite: " + newUser.getName());

                    } else {
                        Log.d("SQLite", "não adicionado ao sql");
                    }

                    */
                    // Verifica se o registro do usuário foi bem-sucedido
                    //if (isUserRegisteredFirebase && isUserRegisteredSqlite) {
                    if (isUserRegisteredFirebase) {
                        // Se bem-sucedido, vai para a tela de listar carros
                        Intent intent= new Intent(RegisterActivity.this, ListCarActivity.class);
                        startActivity(intent);

                        Toast.makeText(RegisterActivity.this, getString(R.string.success_register), Toast.LENGTH_SHORT).show();
                    } else {
                        // Se não for bem-sucedido, tenta novamente.
                        Toast.makeText(RegisterActivity.this, getString(R.string.error_register), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    // Se algum campo estiver vazio, exibe uma mensagem indicando que todos os campos são obrigatórios
                    Toast.makeText(RegisterActivity.this, getString(R.string.fields_required), Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(RegisterActivity.this, InitialUsersActivity.class);
                startActivity(intent);
            }
        });


    }
}