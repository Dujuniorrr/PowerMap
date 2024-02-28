package com.ifbaiano.powermap.activity.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
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

      UserRegisterService userRegisterService;
      userRegisterService = new UserRegisterService(new UserDaoFirebase(this));
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


                // Check if all fields are filled
                if (verifyValid) {
                    // Create a User object with the retrieved information
                    User newUser = new User();
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setPassword(password);
                    newUser.setPassword(passwordConfirm);

                    // Call add method of UserRegisterService
                    boolean isUserRegistered = userRegisterService.add(newUser);

                    // Check if user registration was successful
                    if (isUserRegistered) {
                        Intent intent= new Intent(RegisterActivity.this, LoginActivity.ProfileActivity.class);
                        startActivity(intent);

                        Toast.makeText(RegisterActivity.this, "Registro feito com sucesso!", Toast.LENGTH_SHORT).show();
                        // If successful, perform necessary actions like displaying a success message or navigating to another activity
                    } else {
                        Toast.makeText(RegisterActivity.this, "bla bla", Toast.LENGTH_SHORT).show();

                        // If unsuccessful, handle the failure (e.g., display an error message)
                    }
                } else {
                    // Display a message indicating that all fields are required
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
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