package com.ifbaiano.powermap.activity.users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MainActivity;
import com.ifbaiano.powermap.activity.cars.ListCarActivity;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
import com.ifbaiano.powermap.dao.sqlite.UserDaoSqlite;
import com.ifbaiano.powermap.databinding.ActivityProfileUserBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.fragment.CarFragment;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.service.UserService;
import com.ifbaiano.powermap.verifier.LoginVerifier;
import com.ifbaiano.powermap.verifier.RegisterUserVerifier;

import java.util.Objects;

public class ProfileUserActivity extends AppCompatActivity {

    Button submitEditImageProfile,editProfileUserBtn, logouProfile, btnDeleteAccount, btnEditPassword;
    ImageView imageEditProfil;
    TextInputEditText nameEditProfile, emailEditProfile;
    ActivityProfileUserBinding binding;
    DataBindingFactory bindingFactory;
    UserDaoFirebase userDaoFirebase;
    UserService userRegisterService;
    UserDaoSqlite userDaoSqlite;
    RegisterUserVerifier verifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        this.doBinding();
        this.findViewsById();
        this.makeInstances();

        verifier =  new RegisterUserVerifier(getApplicationContext());
        LoginVerifier logout = new LoginVerifier(this);
        User userInMemory = UserFactory.getUserInMemory(ProfileUserActivity.this);
        UserDaoSqlite userDao = new UserDaoSqlite(ProfileUserActivity.this);


        btnEditPassword.setOnClickListener(v -> {
            // Redirect to MainActivity
            Intent intent = new Intent(ProfileUserActivity.this, EditPasswordActivity.class);
            startActivity(intent);
        });

        logouProfile.setOnClickListener(v -> {
            logout.userLogout();

                Intent intent;
                intent = new Intent(ProfileUserActivity.this, MainActivity.class);
                startActivity(intent);
         });

        btnDeleteAccount.setOnClickListener(v -> {

            userDao.remove(userInMemory);
            //userDao.findOne(userInMemory.getName()); para verificar se foi apagado
            logout.userLogout();

            // Redirect to MainActivity
            Intent intent = new Intent(ProfileUserActivity.this, MainActivity.class);
            startActivity(intent);
        });




        //recupera da memoria - prefrences
        User user = UserFactory.getUserInMemory(this);
        if(user != null){
            String userName = user.getName().toString();
            nameEditProfile.setText(userName);
            String userEmail = user.getEmail().toString();
            emailEditProfile.setText(userEmail);

            String userImgpath = user.getImgpath();

            if(userImgpath != null){
                //lembrar de ajustar de onde vem a imagem - trocar para o share
                Toast.makeText(this, "Imagem existe", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            startActivity(new Intent(this, InitialUsersActivity.class));
        }

        editProfileUserBtn.setOnClickListener(v -> {
            this.submitForm();
        });

    }


    private void findViewsById(){
        submitEditImageProfile = findViewById(R.id.submitEditImageProfile);
        editProfileUserBtn = findViewById(R.id.editProfileUserBtn);
        logouProfile = findViewById(R.id.logouProfile);
        imageEditProfil = findViewById(R.id.imageEditProfil);
        nameEditProfile = findViewById(R.id.nameEditProfile);
        emailEditProfile = findViewById(R.id.emailEditProfile);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnEditPassword = findViewById(R.id.btnEditPassword);
    }

    private void makeInstances(){
        userDaoFirebase = new UserDaoFirebase(this);
        userDaoSqlite = new UserDaoSqlite(this);
        userRegisterService  = new UserService(userDaoFirebase);
    }

    private boolean checkEmailExists() {
        String emailText = Objects.requireNonNull(emailEditProfile.getText()).toString().trim();
        return userRegisterService.findByEmail(emailText);

    }
    private void submitForm() {
        new Thread(() -> {
            boolean emailAlreadyExists = checkEmailExists();

            if (verifyFormValidity(emailAlreadyExists)) {
                User newUser = UserFactory.getUserInMemory(getApplicationContext());
                newUser.setName( Objects.requireNonNull(nameEditProfile.getText()).toString().trim());
                newUser.setEmail( Objects.requireNonNull(emailEditProfile.getText()).toString().trim());


                User userEditFirebase = userRegisterService.edit(newUser);

                userRegisterService.setDao(new UserDaoSqlite(getApplicationContext()));
                User userEditSqlite = userRegisterService.edit(newUser);

                executeAfterRegistration(userEditFirebase != null&& userEditSqlite != null, userEditSqlite);
            }

        }).start();
    }


    private void doBinding(){
        bindingFactory = new DataBindingFactory(this, R.id.frameLayoutUser);
        binding = ActivityProfileUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindingFactory.replaceFragment(new CarFragment());
        binding.bottomNavigationMenuUser.setOnItemSelectedListener(item -> bindingFactory.bindingMenu(item));
    }

    private void executeAfterRegistration(boolean isUserRegisteredFirebase, User user) {
        runOnUiThread(() -> {
            if (isUserRegisteredFirebase) {
                // Se bem-sucedido, vai para a tela de listar carros
                UserFactory.saveUserInMemory(user, getApplicationContext());
                Intent intent= new Intent(ProfileUserActivity.this, ListCarActivity.class);
                startActivity(intent);
            } else {
                // Se não for bem-sucedido
                Toast.makeText(ProfileUserActivity.this, getString(R.string.error_register), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean verifyFormValidity(boolean emailAlreadyExists) {
        boolean[] verifyValid = {false};
        Object lock = new Object();

        runOnUiThread(() -> {
            verifyValid[0] = verifier.verifyEditUser(nameEditProfile, emailEditProfile, emailAlreadyExists);
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