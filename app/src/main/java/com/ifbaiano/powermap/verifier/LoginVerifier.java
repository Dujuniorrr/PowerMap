package com.ifbaiano.powermap.verifier;

import android.content.Context;

import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.model.User;

public class LoginVerifier {

    private Context ctx;

    public LoginVerifier(Context ctx) {
        this.ctx = ctx;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public boolean isUserLogged() {
        User user = UserFactory.getUserInMemory(ctx);

        boolean isLogged = user != null && !user.getId().isEmpty();

        return isLogged;
    }

    public boolean userLogout() {
        UserFactory.clearUserInMemory(ctx);
        return true;
    }





}
