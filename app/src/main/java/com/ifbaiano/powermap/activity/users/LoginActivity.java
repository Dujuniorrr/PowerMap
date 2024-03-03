package com.ifbaiano.powermap.activity.users;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.car.ListCarActivity;
import com.ifbaiano.powermap.dao.contracts.StorageDao;
import com.ifbaiano.powermap.dao.firebase.StorageDaoFirebase;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
import com.ifbaiano.powermap.model.User;


public class LoginActivity extends AppCompatActivity {

    Button backButonLogin, enterLoginBtn;

    TextInputEditText emailLogin, passwordLogin;

    private Button btnGoogleAuth;
    int RC_SIGN_IN=20;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;

    StorageDao storageDao;


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backButonLogin =findViewById(R.id.backButonLogin);
        btnGoogleAuth = findViewById(R.id.btnGoogleAuth);
        enterLoginBtn = findViewById(R.id.enterLoginBtn);
        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        storageDao = new StorageDaoFirebase();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        backButonLogin.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(LoginActivity.this, InitialUsersActivity.class);
            startActivity(intent);
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleAuth.setOnClickListener(v -> {
            googleSignIn();
        });

        //verifica se já está logado

        if(auth.getCurrentUser() != null){
            Intent it = new Intent(LoginActivity.this, ListCarActivity.class);
            startActivity(it);
            finish();
        }

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
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,  null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    User newUser = new User();

                    newUser.setEmail(user.getEmail());
                    newUser.setId(user.getUid());
                    newUser.setImgpath(user.getPhotoUrl().toString());
                    newUser.setName(user.getDisplayName());
                    newUser.setAdmin(false);

                    // Utilize the UserDaoFirebase to add the user to Firebase
                    UserDaoFirebase userDaoFirebase = new UserDaoFirebase(getApplicationContext());
                    userDaoFirebase.add(newUser);
                    Log.d("Firebase", "Usuário adicionado ao firbase: " + newUser.getName());


                    Intent intent = new Intent(LoginActivity.this, ListCarActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, getString(R.string.successLogin), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.notfound), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class ProfileActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_list_car);
        }
    }

}

