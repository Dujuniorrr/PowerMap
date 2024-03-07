package com.ifbaiano.powermap.factory;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.google.firebase.auth.FirebaseUser;
import com.ifbaiano.powermap.model.User;

import java.util.Objects;

public class UserFactory {

    public static User createByCursor(Cursor cursor){
        return new User(
                cursor.getString(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("imgPath")),
                cursor.getInt(cursor.getColumnIndexOrThrow("isAdmin")) == 1,
                null,
                null
        );
    }

    public static User createByFirebase(FirebaseUser user){
        return new User(
                user.getUid(),
                user.getDisplayName(),
                user.getEmail(),
                null,
                Objects.requireNonNull(user.getPhotoUrl()).toString(),
                false,
                null,
                null
        );
    }

    public static User getUserInMemory(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences("power_map_memory", Context.MODE_PRIVATE);

        return new User(
                preferences.getString("id", ""),
                preferences.getString("name", ""),
                preferences.getString("email", ""),
                preferences.getString("password", ""),
                preferences.getString("imgPath", ""),
                preferences.getBoolean("isAdmin", false),
                null,
                null
        );
    }

    public static boolean saveUserInMemory(User user, Context ctx){
        SharedPreferences preferences = ctx.getSharedPreferences("power_map_memory", ctx.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putString("id", user.getId());
        edit.putString("name", user.getName());
        edit.putString("email", user.getEmail());
        edit.putString("imgPath", user.getImgpath());
        edit.putString("password", user.getPassword());
        edit.putBoolean("isAdmin", user.isAdmin());

        edit.commit();

        return true;
    }

    public static boolean clearUserInMemory(Context ctx) {
        SharedPreferences preferences = ctx.getSharedPreferences("power_map_memory", ctx.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();

        edit.clear();
        edit.apply();

        return true;
    }


}
