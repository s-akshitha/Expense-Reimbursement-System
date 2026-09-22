package com.ers.dao;

import com.ers.model.User;

import java.util.List;

public class UserDaoImpl implements IUserDao{

    @Override
    public User addUser(User user) {
        return null;
    }
    @Override
    public boolean updateUser(User user) {
        return false;
    }
    @Override
    public User getUserById(int userId) {
        return null;
    }
    @Override
    public List<User> getAllUsers() {
        return List.of();
    }
    @Override
    public boolean deleteUser(int userId) {
        return false;
    }

    @Override
    public boolean updateUserStatus(int userId, boolean active) {
        return false;
    }
}
