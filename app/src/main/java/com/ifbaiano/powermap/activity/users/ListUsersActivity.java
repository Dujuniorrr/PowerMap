package com.ifbaiano.powermap.activity.users;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.databinding.ActivityListUsersBinding;
import com.ifbaiano.powermap.factory.DataBindingFactory;

public class ListUsersActivity extends AppCompatActivity {

    ActivityListUsersBinding binding;
    DataBindingFactory bindingFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

    }


}