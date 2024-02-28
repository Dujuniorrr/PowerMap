package com.ifbaiano.powermap.service;

import com.ifbaiano.powermap.dao.contracts.UserDao;
import com.ifbaiano.powermap.model.User;

public class UserRegisterService {

    private UserDao dao;

    public UserRegisterService(UserDao dao) {
        this.dao = dao;
    }

    public boolean add(User user) {
        User addedUser = this.dao.add(user);
        return addedUser != null;
    }
    public UserDao getDao() {
        return dao;
    }
    public void setDao(UserDao dao) {
        this.dao = dao;
    }

}
