package com.ifbaiano.powermap.dao.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ifbaiano.powermap.dao.contracts.UserDao;
import com.ifbaiano.powermap.model.User;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

public class UserDaoFirebase implements UserDao {

    private final String TABLE_NAME = "users";
    private final FirebaseDatabase firebaseDatabase;

    public UserDaoFirebase(Context ctx) {

        FirebaseApp.initializeApp(ctx);
        firebaseDatabase = FirebaseDatabase.getInstance();
    }



    @Override
    public User add(User user) {
        user.setId(firebaseDatabase.getReference(TABLE_NAME).push().getKey());
        firebaseDatabase.getReference(TABLE_NAME).child(user.getId()).setValue(user);
        return user;
    }

    @Override
    public User edit(User user) {
        firebaseDatabase.getReference(TABLE_NAME).child(user.getId()).setValue(user);
        return user;
    }

    @Override
    public Boolean remove(User user) {
        firebaseDatabase.getReference(TABLE_NAME).child(user.getId()).removeValue();
        return true;
    }

    @Override
    public User findOne(final String id) {
        final User[] foundUser = new User[1];
        firebaseDatabase.getReference(TABLE_NAME).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foundUser[0] = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        return foundUser[0];
    }

    @Override
    public ArrayList<User>  findAll() {
        ArrayList<User> allUsers = new ArrayList<>();
        Query query = firebaseDatabase.getReference(TABLE_NAME);
        try {
            DataSnapshot dataSnapshot = Tasks.await(query.get());

            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    allUsers.add(user);
                }
                return allUsers;
            }
            return allUsers;
        } catch (ExecutionException | InterruptedException e) {
            Log.d("teste", e.getMessage());
            return allUsers;
        }
    }

    @Override
    public ArrayList<User> findAllClients() {
        ArrayList<User> allUsers = new ArrayList<>();

        Query query = firebaseDatabase.getReference(TABLE_NAME).orderByChild("admin").equalTo(false);
        try {
            DataSnapshot dataSnapshot = Tasks.await(query.get());
            Log.d("teste", Boolean.toString(dataSnapshot.exists()));

            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    allUsers.add(user);
                }
                return allUsers;
            }
            return allUsers;
        } catch (ExecutionException | InterruptedException e) {
            Log.d("teste", e.getMessage());

            return allUsers;
        }
    }

    @Override
    public ArrayList<User> findAllAdmins() {
        ArrayList<User> allUsers = new ArrayList<>();
        Query query = firebaseDatabase.getReference(TABLE_NAME).orderByChild("admin").equalTo(true);
        try {
            DataSnapshot dataSnapshot = Tasks.await(query.get());
            Log.d("teste", Boolean.toString(dataSnapshot.exists()));
            if (dataSnapshot.exists()) {
                 allUsers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    allUsers.add(user);
                }
                return allUsers;
            }
            return allUsers;
        } catch (ExecutionException | InterruptedException e) {
            return allUsers;
        }
    }


    public User findByEmail(String email) {
        if(this.findAll() != null){
            return this.findAll()
                    .stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst()
                    .orElse(null);
        }

        return null;

    }

    @Override
    public Boolean changePassword(User user, String password) {
        user.setPassword(password);
        firebaseDatabase.getReference(TABLE_NAME).child(user.getId()).child("password").setValue(password);
        return true;
    }



    public User findByEmailAndPassword(final String email, final String password) {
        final Semaphore semaphore = new Semaphore(0);
        final User[] foundUser = new User[1];
        Query query = firebaseDatabase.getReference(TABLE_NAME);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                        foundUser[0] = user;
                        semaphore.release();
                        return;
                    }
                }
                semaphore.release(); // Release semaphore even if user not found
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                semaphore.release();
            }
        });

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return foundUser[0];
    }


}
