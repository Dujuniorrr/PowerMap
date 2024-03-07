package com.ifbaiano.powermap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MainActivity;
import com.ifbaiano.powermap.activity.users.InitialUsersActivity;
import com.ifbaiano.powermap.appearance.StatusBarAppearance;
import com.ifbaiano.powermap.factory.BitmapCustomFactory;


public class UserGoogleFragment extends Fragment {
    ImageView imageProfilView;
    Button logouProfileGoogle;
    TextView textNameProfileGoogle, textEmailProfileGoogle;
    AppCompatActivity mainActivity;
    View rootView;
    public UserGoogleFragment() {
        // Required empty public constructor
    }


    public static UserGoogleFragment newInstance(String param1, String param2) {
        return new UserGoogleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mainActivity =  (AppCompatActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_user_google, container, false);
        StatusBarAppearance.changeStatusBarColor(mainActivity, R.color.sub_background_form);

        this.findViewsById();
        this.setUserAttributes();

        logouProfileGoogle.setOnClickListener( v -> {
            logoutUser();
        });
        return rootView; // Retorna a rootView previamente inflada
    }

    private void findViewsById(){
        imageProfilView = rootView.findViewById(R.id.imageProfilView);
        textNameProfileGoogle = rootView.findViewById(R.id.textNameProfileGoogle);
        textEmailProfileGoogle =  rootView.findViewById(R.id.textEmailProfileGoogle);
        logouProfileGoogle =  rootView.findViewById(R.id.logouProfileGoogle);
    }

    private void setUserAttributes(){

        // aq nao precisa do shared, pois so entra nessa parte se houver o login com google
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("user do google", "user do google: " + userF.getDisplayName());

        if(userF != null){
            String userName = userF.getDisplayName().toString();
            textNameProfileGoogle.setText(userName);
            String userEmail = userF.getEmail().toString();
            textEmailProfileGoogle.setText(userEmail);

            new BitmapCustomFactory(mainActivity, imageProfilView).setImageByUri(userF.getPhotoUrl(), R.drawable.baseline_person);
        }
        else{
            startActivity(new Intent( mainActivity, InitialUsersActivity.class));
        }

    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        GoogleSignIn.getClient(mainActivity, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut().addOnCompleteListener(mainActivity, task -> {
            Intent intent = new Intent(mainActivity, MainActivity.class);
            startActivity(intent);

        });
    }
}




