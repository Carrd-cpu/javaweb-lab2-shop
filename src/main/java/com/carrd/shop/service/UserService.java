package com.carrd.shop.service;

import com.carrd.shop.dao.UserDao;
import com.carrd.shop.entity.User;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank()) return null;
        if (password == null || password.isBlank()) return null;
        return userDao.findByUsernameAndPassword(username.trim(), password);
    }
}
