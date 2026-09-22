package com.ers.service;

import com.ers.model.User;

import java.util.List;

public interface IUserService {
    User addUser(User user);
    boolean updateUser(User user);
    User getUserById(int userId);
    List<User> getAllUsers();
    boolean deleteUser(int userId);
    boolean updateUserStatus(int userId, boolean active);
}
