package com.ers.service;

import com.ers.model.User;

import java.sql.Connection;
import java.util.List;

public interface IUserService {
    User addUser(Connection connection,User user);
    boolean updateUser(User user);
    User getUserById(int userId);
    List<User> getAllUsers();
    boolean deleteUser(Connection connection,int userId);
    boolean updateUserStatus(int userId, boolean active);
}
