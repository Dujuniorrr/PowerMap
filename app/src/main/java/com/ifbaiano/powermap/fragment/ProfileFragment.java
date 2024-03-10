package com.ifbaiano.powermap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.activity.MainActivity;
import com.ifbaiano.powermap.activity.users.EditPasswordActivity;
import com.ifbaiano.powermap.activity.users.InitialUsersActivity;
import com.ifbaiano.powermap.appearance.StatusBarAppearance;
import com.ifbaiano.powermap.dao.firebase.UserDaoFirebase;
import com.ifbaiano.powermap.dao.media.StorageDaoMedia;
import com.ifbaiano.powermap.dao.sqlite.UserDaoSqlite;
import com.ifbaiano.powermap.factory.BitmapCustomFactory;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.model.User;
import com.ifbaiano.powermap.service.UserService;
import com.ifbaiano.powermap.verifier.LoginVerifier;
import com.ifbaiano.powermap.verifier.RegisterUserVerifier;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    AppCompatButton submitEditImageProfile, editProfileUserBtn, logouProfile, btnDeleteAccount, btnEditPassword;
    ImageView imageEditProfile;
    TextInputEditText nameEditProfile, emailEditProfile;
    UserDaoFirebase userDaoFirebase;
    UserService userRegisterService;
    UserDaoSqlite userDaoSqlite;
    RegisterUserVerifier verifier;

    AppCompatActivity mainActivity;

    BitmapCustomFactory bitmapCustomFactory;
    byte[] byteArray = null;
    View rootView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mainActivity =  (AppCompatActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_profile_user, container, false);
        StatusBarAppearance.changeStatusBarColor(mainActivity, R.color.sub_background_form);

        this.findViewsById();
        this.setUserAttributes();
        this.makeInstances();
        UserDaoSqlite userDao = new UserDaoSqlite(getContext());
        StorageDaoMedia storageDaoMedia = new StorageDaoMedia(getContext());

        btnEditPassword.setOnClickListener(v -> {
            // Redirect to MainActivity
            Intent intent = new Intent(getActivity(), EditPasswordActivity.class);
            startActivity(intent);
        });

        LoginVerifier logout = new LoginVerifier(getActivity());
        logouProfile.setOnClickListener(v -> {
            logout.userLogout();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        User user = UserFactory.getUserInMemory(getActivity());
        btnDeleteAccount.setOnClickListener(v -> {
            new UserDaoFirebase(mainActivity).remove(UserFactory.getUserInMemoryFirebase(mainActivity));
            userDao.remove(user);
            logout.userLogout();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        editProfileUserBtn.setOnClickListener(v -> {
            submitForm();
        });

        submitEditImageProfile.setOnClickListener(v -> {

            //ajustar depois
        });


        return rootView;
    }


    private void setUserAttributes(){

        // Load user data
        User user = UserFactory.getUserInMemory(getActivity());
        if (user != null) {
            String userName = user.getName().toString();
            nameEditProfile.setText(userName);
            String userEmail = user.getEmail().toString();
            emailEditProfile.setText(userEmail);

            String userImgpath = String.valueOf(user.getImgpath());

            if (userImgpath != null) {

                // Remember to adjust where the image comes from
                Toast.makeText(getActivity(), "Imagem existe", Toast.LENGTH_SHORT).show();
            }

        } else {
            startActivity(new Intent(getActivity(), InitialUsersActivity.class));
        }

    }


    private void findViewsById() {
        submitEditImageProfile = rootView.findViewById(R.id.submitEditImageProfile);
        editProfileUserBtn = rootView.findViewById(R.id.editProfileUserBtn);
        logouProfile = rootView.findViewById(R.id.logouProfile);
        imageEditProfile = rootView.findViewById(R.id.imageEditProfil);
        nameEditProfile = rootView.findViewById(R.id.nameEditProfile);
        emailEditProfile = rootView.findViewById(R.id.emailEditProfile);
        btnDeleteAccount = rootView.findViewById(R.id.btnDeleteAccount);
        btnEditPassword = rootView.findViewById(R.id.btnEditPassword);
    }


    private void makeInstances() {
        userDaoFirebase = new UserDaoFirebase(getActivity());
        userDaoSqlite = new UserDaoSqlite(getActivity());
        userRegisterService = new UserService(userDaoFirebase);
        verifier = new RegisterUserVerifier(getActivity());
        bitmapCustomFactory = new BitmapCustomFactory(
                mainActivity, byteArray, imageEditProfile, submitEditImageProfile
        );
    }

    private boolean checkEmailExists() {
        String emailText = Objects.requireNonNull(emailEditProfile.getText()).toString().trim();
        return  userRegisterService.findByEmail(emailText) != null;
    }

    private void submitForm() {
        new Thread(() -> {
            boolean emailAlreadyExists = checkEmailExists();

            if (verifyFormValidity(emailAlreadyExists)) {



                userRegisterService.setDao(userDaoFirebase);
                User newUser = UserFactory.getUserInMemoryFirebase(getActivity());
                newUser.setName(Objects.requireNonNull(nameEditProfile.getText()).toString());
                newUser.setEmail(Objects.requireNonNull(emailEditProfile.getText()).toString());

                User userEditFirebase = userRegisterService.edit(newUser);

                User newUserSqlite = UserFactory.getUserInMemory(getActivity());
                newUserSqlite.setName(Objects.requireNonNull(nameEditProfile.getText()).toString());
                newUserSqlite.setEmail(Objects.requireNonNull(emailEditProfile.getText()).toString());
                userRegisterService.setDao(new UserDaoSqlite(getActivity()));

                User userEditSqlite = userRegisterService.edit(newUserSqlite);

                Log.d("TESTE FIREBASE", newUser.getId());
                Log.d("TESTE FIREBASE", newUserSqlite.getId());

                executeAfterRegistration(userEditFirebase != null && userEditSqlite != null, userEditSqlite, userEditFirebase);
            }

        }).start();
    }

    private void executeAfterRegistration(boolean isUserRegisteredFirebase, User user, User userF) {
        getActivity().runOnUiThread(() -> {
            if (isUserRegisteredFirebase) {
                // If successful, go to the car listing screen
                Toast.makeText(getActivity(), getString(R.string.success_edit), Toast.LENGTH_SHORT).show();
                UserFactory.saveUserInMemoryFirebase(userF, getActivity());
                UserFactory.saveUserInMemory(user, getActivity());
//
            } else {
                // If unsuccessful
                Toast.makeText(getActivity(), getString(R.string.error_register), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean verifyFormValidity(boolean emailAlreadyExists) {
        boolean[] verifyValid = {false};
        Object lock = new Object();

        getActivity().runOnUiThread(() -> {
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