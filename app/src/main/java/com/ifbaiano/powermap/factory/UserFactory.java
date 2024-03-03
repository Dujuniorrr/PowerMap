package com.ifbaiano.powermap.factory;

import android.database.Cursor;

import com.google.firebase.auth.FirebaseUser;
import com.ifbaiano.powermap.model.User;

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
                user.getPhotoUrl().toString(),
                false,
                null,
                null
        );
    }

    public static User getUserInMemory(){
        return new User();
    }

}
