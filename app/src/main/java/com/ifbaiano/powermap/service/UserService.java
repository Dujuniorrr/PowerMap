package com.ifbaiano.powermap.service;

import com.ifbaiano.powermap.dao.contracts.UserDao;
import com.ifbaiano.powermap.model.User;

public class UserService {

    private UserDao dao;

    public UserService(UserDao dao) {
        this.dao = dao;
    }

    public User add(User user) {
        return this.dao.add(user);
    }

    public User edit(User user) {
        return this.dao.edit(user);
    }

    public User findByEmail(String email){
        return this.dao.findByEmail(email);
    }

    public UserDao getDao() {
        return dao;
    }
    public void setDao(UserDao dao) {
        this.dao = dao;
    }

}
