package com.ifbaiano.powermap.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.fragment.CarFragment;
import com.ifbaiano.powermap.fragment.MapFragment;
import com.ifbaiano.powermap.fragment.ModelsFragment;
import com.ifbaiano.powermap.fragment.ProfileFragment;
import com.ifbaiano.powermap.fragment.ScheduleFragment;
import com.ifbaiano.powermap.fragment.UserGoogleFragment;
import com.ifbaiano.powermap.fragment.UsersFragment;

public class MenuActivity extends AppCompatActivity  {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bottomNavigationView = findViewById(R.id.bottomNavigationMenuUser);

        this.verifyUser();
        this.setInitialFragment();

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.icon_car_admin) {
                replaceFragment(new ModelsFragment());
            } else if (itemId == R.id.icon_users_admin) {
                replaceFragment(new UsersFragment());
            }  else if (itemId == R.id.icon_car_user) {
                replaceFragment(new CarFragment());
            } else if (itemId == R.id.icon_map_user) {
                replaceFragment(new MapFragment());
            } else if (itemId == R.id.icon_schedule_user) {
                replaceFragment(new ScheduleFragment());
            } else if (itemId == R.id.icon_profile_user) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    replaceFragment(new UserGoogleFragment());
                }
                else{
                        replaceFragment(new ProfileFragment());
                }
            }

            return true;
        });
    }

    private void verifyUser(){
        if( UserFactory.getUserInMemory(getApplicationContext()).isAdmin()) {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.menu_admin);
        }
    }

    private void setInitialFragment(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String fragmentName = extras.getString("fragment");
            if ("profile".equals(fragmentName)) {
                replaceFragment(new ProfileFragment());
                bottomNavigationView.getMenu().findItem(R.id.icon_profile_user).setChecked(true);
            } else if ("schedule".equals(fragmentName)) {
                replaceFragment(new ScheduleFragment());
                bottomNavigationView.getMenu().findItem(R.id.icon_schedule_user).setChecked(true);
            }
            else if ("users".equals(fragmentName)) {
                replaceFragment(new UsersFragment());
                bottomNavigationView.getMenu().findItem(R.id.icon_users_admin).setChecked(true);
            }

        } else {
            if (UserFactory.getUserInMemory(getApplicationContext()).isAdmin()) {
                replaceFragment(new ModelsFragment());
            } else {
                replaceFragment(new CarFragment());
            }
        }

    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutUser, fragment);
        fragmentTransaction.commit();
    }

}

