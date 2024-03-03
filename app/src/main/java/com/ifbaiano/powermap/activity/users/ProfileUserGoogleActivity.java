package com.ifbaiano.powermap.activity.users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MainActivity;
import com.ifbaiano.powermap.databinding.ActivityProfileUserGoogleBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;
import com.ifbaiano.powermap.fragment.CarFragment;
import com.squareup.picasso.Picasso;

public class ProfileUserGoogleActivity extends AppCompatActivity {

    ActivityProfileUserGoogleBinding binding;
    DataBindingFactory bindingFactory;
    ImageView imageProfilView;
    Button logouProfileGoogle;
    TextView textNameProfileGoogle, textEmailProfileGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user_google);

        this.doBinding();

        imageProfilView = findViewById(R.id.imageProfilView);
        textNameProfileGoogle = findViewById(R.id.textNameProfileGoogle);
        textEmailProfileGoogle = findViewById(R.id.textEmailProfileGoogle);
        logouProfileGoogle = findViewById(R.id.logouProfileGoogle);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {


            String userName = user.getDisplayName();
            textNameProfileGoogle.setText(userName);

            String userEmail = user.getEmail();
            textEmailProfileGoogle.setText(userEmail);

            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                // carregar a imagem de perfil na imageProfilView
                Picasso.get().load(photoUrl).into(imageProfilView);
            } else {
                // Se a URL da foto estiver vazia, definir uma imagem de perfil padrão
                imageProfilView.setImageResource(R.drawable.baseline_person);
            }

        }

        logouProfileGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });


    }

    private void doBinding() {
        bindingFactory = new DataBindingFactory(this, R.id.frameLayoutUser);
        binding = ActivityProfileUserGoogleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindingFactory.replaceFragment(new CarFragment());
        binding.bottomNavigationMenuUser.setOnItemSelectedListener(item -> bindingFactory.bindingMenu(item));
    }

    private void logoutUser() {
        // Desconecta do Firebase Auth
        FirebaseAuth.getInstance().signOut();

        // Desconecta do Google SignIn (revoga o acesso)
        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Redireciona para a tela de login
                Intent it = new Intent(ProfileUserGoogleActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }
        });
    }

}