package com.ifbaiano.powermap.dao.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ifbaiano.powermap.connection.SqliteConnection;
import com.ifbaiano.powermap.dao.contracts.UserDao;
import com.ifbaiano.powermap.factory.UserFactory;
import com.ifbaiano.powermap.model.User;

import java.util.ArrayList;

public class UserDaoSqlite implements UserDao {

    public static UserDao userDao;
    private final SqliteConnection conn;
    private SQLiteDatabase db;
    private final String TABLE_NAME = "users";
    private final String FIND_ONE_QUERY = "SELECT * FROM "+ this.TABLE_NAME +" WHERE id = ?";
    private final String FIND_ALL_QUERY = "SELECT * FROM " + this.TABLE_NAME;
    private final String FIND_ALL_FILTER_QUERY = "SELECT * FROM "+ this.TABLE_NAME +" WHERE isAdmin = ? ";
    private final String FIND_BY_EMAIL = "SELECT * FROM " + this.TABLE_NAME + " WHERE email = ?";

    private final String ADMIN = "1";
    private final String CLIENT = "0";


    public UserDaoSqlite(Context ctx) {
        this.conn = new SqliteConnection(ctx);
    }
    @Override
    public User add(User user) {
        this.db = this.conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("imgPath", user.getImgpath());
        values.put("isAdmin", user.isAdmin());
        values.put("password", user.getPassword());

        long id = db.insert(this.TABLE_NAME, null, values);
        user.setId(Long.toString(id));

        return  (id > 0) ? user :  null;
    }

    @Override
    public User edit(User user) {
        this.db = this.conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("imgPath", user.getImgpath());
        values.put("isAdmin", user.isAdmin());

        return (this.db.update(this.TABLE_NAME, values, "id = ?",  new String[]{ user.getId() }) > 0) ? user : null;

    }

    @Override
    public Boolean remove(User user) {
        this.db = this.conn.getWritableDatabase();
        return db.delete(this.TABLE_NAME, "id = ?", new String[]{user.getId()}) > 0;
    }

    @Override
    public  User findOne(String id) {
        this.db = this.conn.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(this.FIND_ONE_QUERY, new String[]{id});

        if(cursor.moveToFirst()){
            return UserFactory.createByCursor(cursor);
        }

        return null;
    }

    @Override
    public Boolean findByEmail(String email) {
        this.db = this.conn.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(this.FIND_BY_EMAIL, new String[]{email} );
        return this.makeUserList(cursor).size() > 0;
    }

    @Override
    public ArrayList<User> findAll() {
           this.db = this.conn.getWritableDatabase();
           @SuppressLint("Recycle") Cursor cursor = db.rawQuery(this.FIND_ALL_QUERY, null);
           return this.makeUserList(cursor);
    }

    @Override
    public ArrayList<User> findAllClients() {
        this.db = this.conn.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(this.FIND_ALL_FILTER_QUERY, new String[]{ this.CLIENT } );
        return this.makeUserList(cursor);
    }

    @Override
    public ArrayList<User> findAllAdmins() {
        this.db = this.conn.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(this.FIND_ALL_FILTER_QUERY,  new String[]{ this.ADMIN });
        return this.makeUserList(cursor);
    }

    @Override
    public Boolean changePassword(User user, String password) {
        this.db = this.conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("password", password);

        return this.db.update(this.TABLE_NAME, values, "id = ?", new String[]{user.getId()}) > 0;
    }
    public ArrayList<User> makeUserList(Cursor cursor){
        ArrayList<User> userList = new ArrayList<>();
        while(cursor.moveToNext()) {
            userList.add(UserFactory.createByCursor(cursor));
        }
        return userList.size() > 0 ? userList : null;
    }

    public User findByEmailAndPassword(String email, String password) {
        this.db = this.conn.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT * FROM " + this.TABLE_NAME + " WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursor.moveToFirst()) {
            return UserFactory.createByCursor(cursor);
        }
        return null;
    }
}