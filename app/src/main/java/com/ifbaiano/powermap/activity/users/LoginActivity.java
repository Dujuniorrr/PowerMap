package com.ifbaiano.powermap.activity.users;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MenuActivity;
import com.ifbaiano.powermap.appearance.StatusBarAppearance;
import com.ifbaiano.powermap.dao.contracts.StorageDao;
import com.ifbaiano.powermap.dao.contracts.UserDao;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
import com.ifbaiano.powermap.dao.media.StorageDaoMedia;
import com.ifbaiano.powermap.dao.sqlite.UserDaoSqlite;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.service.CryptographyPasswordService;
import com.ifbaiano.powermap.service.UserService;
import com.ifbaiano.powermap.verifier.RegisterUserVerifier;


public class LoginActivity extends AppCompatActivity {

    Button backButonLogin, enterLoginBtn;
    ProgressBar progressBar;
    TextInputEditText emailLogin, passwordLogin;
    TextView textNotAccount;
    private Button btnGoogleAuth;
    int RC_SIGN_IN=20;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    StorageDao storageDao;
    UserService userService;

    UserDao userDao;


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarAppearance.changeStatusBarColor(this, R.color.black);
        this.findViewsById();
        this.makeInstances();


        userDao = new UserDaoFirebase(this);

        backButonLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, InitialUsersActivity.class);
            startActivity(intent);
        });

        textNotAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleAuth.setOnClickListener(v -> {
            googleSignIn();
        });

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        }

        enterLoginBtn.setOnClickListener(v -> {
            enterLoginBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            TextInputEditText emailInput = emailLogin;
            TextInputEditText passwordInput = passwordLogin;

            RegisterUserVerifier registerUserVerifier = new RegisterUserVerifier(this);

            if (registerUserVerifier.verifyPasswordLogin(emailInput, passwordInput)) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String passwordCryp = CryptographyPasswordService.encryptPassword(password);


                new Thread(() -> {
                    userDao = new UserDaoSqlite(this);
                    User userS = userDao.findByEmailAndPassword(email, passwordCryp);

                    runOnUiThread(() -> {
                        if (userS != null) {
                            loginUser(userS);
                        } else {
                            loginUserFromFirebase(email, passwordCryp);
                        }
                    });
                }).start();
            }
            else{
                progressBar.setVisibility(View.GONE);
                enterLoginBtn.setVisibility(View.VISIBLE);
            }

        });


    }


    private void loginUser(User user) {
        UserFactory.saveUserInMemory(user, getApplicationContext());
        Toast.makeText(LoginActivity.this, getString(R.string.successLogin), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
    }

    private void loginUserFromFirebase(String email, String passwordCryp) {
        new Thread(() -> {
            userDao = new UserDaoFirebase(getApplicationContext());
            User user = userDao.findByEmailAndPassword(email, passwordCryp);
            runOnUiThread(() -> {
                if (user != null) {
                    saveUserAndLogin(user);
                } else {
                    progressBar.setVisibility(View.GONE);
                    enterLoginBtn.setVisibility(View.VISIBLE);
                    emailLogin.setError(getApplicationContext().getString(R.string.invalid_auth));
                }


            });
        }).start();
    }

    private void saveUserAndLogin(User user) {
        UserFactory.saveUserInMemoryFirebase(user, getApplicationContext());
        new Thread(() -> {
            user.setId(null);
            User userSqlite = new UserDaoSqlite(getApplicationContext()).add(user);
            if (user.getImgpath() != null) {
                userSqlite.setImgpath(new StorageDaoMedia(getApplicationContext()).putImage(new StorageDaoFirebase().getImageAsByteArray(user.getImgpath()), userSqlite.getId()));
                Log.d("URL IMG", userSqlite.getImgpath());
                userSqlite = new UserDaoSqlite(getApplicationContext()).edit(userSqlite);
            }
            UserFactory.saveUserInMemory(userSqlite, getApplicationContext());
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                enterLoginBtn.setVisibility(View.VISIBLE);

                Toast.makeText(LoginActivity.this, getString(R.string.successLogin), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
            });
        }).start();
    }


    //METODOS A PARTE
    private void findViewsById(){
        backButonLogin =findViewById(R.id.backButonLogin);
        btnGoogleAuth = findViewById(R.id.btnGoogleAuth);
        enterLoginBtn = findViewById(R.id.enterLoginBtn);
        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        textNotAccount = findViewById(R.id.textNotAccount);
        progressBar= findViewById(R.id.progressBar);
    }

    private void makeInstances(){
        storageDao = new StorageDaoFirebase();
        userService = new UserService(new UserDaoSqlite(getApplicationContext()));
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    private void googleSignIn(){
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode,int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account= task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            }catch (Exception e){
                Toast.makeText(this, getString(R.string. connectionError), Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = UserFactory.createByFirebase(auth.getCurrentUser());
                // Define um valor padrão ou deixa o campo de senha vazio para usuários autenticados pelo Google
                user.setPassword("");

                if(userService.add(user) != null){
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, getString(R.string.successLogin), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_data), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.notfound), Toast.LENGTH_SHORT).show();
            }
        });
    }





}

