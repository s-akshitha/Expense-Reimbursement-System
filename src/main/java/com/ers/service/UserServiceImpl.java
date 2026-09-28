package com.ers.service;

import ch.qos.logback.classic.Logger;
import com.ers.dao.IUserDao;
import com.ers.dao.UserDaoImpl;
import com.ers.exception.ServiceException;
import com.ers.model.User;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.util.List;
import java.util.Objects;

public class UserServiceImpl implements IUserService{
    private IUserDao userDao;
    private static final Logger logger=(Logger)LoggerFactory.getLogger(UserServiceImpl.class);
    public UserServiceImpl(){
        this.userDao=new UserDaoImpl();
    }

    public UserServiceImpl(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User addUser(Connection connection, User user){
        logger.info("Started UserServiceImpl.addUser()");
        try{
            User result = userDao.addUser(connection, user);
            logger.info("Ending UserServiceImpl.addUser()");
            return result;
        }catch(RuntimeException e){
            logger.error("ERROR at UserServiceImpl.addUser()",e);
            throw e;
        }
    }

    @Override
    public boolean updateUser(User user) {
        logger.info("Started UserService.updateUser()");
        try {
            boolean result = userDao.updateUser(user);
            logger.info("Ending UserService.updateUser()");
            return result;
        } catch (RuntimeException e) {
            logger.error("ERROR at UserService.updateUser()", e);
            throw e;
        }
    }

    @Override
    public User getUserById(int userId) {
        logger.info("Started UserService.getUserById()");
        try {
            User user = userDao.getUserById(userId);
            logger.info("Ending UserService.getUserById()");
            return user;
        } catch (RuntimeException e) {
            logger.error("ERROR at UserService.getUserById()", e);
            throw e;
        }
    }

    @Override
    public List<User> getAllUsers(){
        logger.info("Started UserService.getAllUsers()");
        try{
            List<User> users = userDao.getAllUsers();
            logger.info("Ending UserService.getAllUsers()");
            return users;
        }catch(RuntimeException e){
            logger.error("ERROR at UserService.getAllUsers()", e);
            throw e;
        }
    }

    @Override
    public boolean deleteUser(Connection connection, int userId){
        logger.info("Started UserService.deleteUser()");
        try{
            boolean result=userDao.deleteUser(connection, userId);
            logger.info("Ending UserService.deleteUser()");
            return result;
        }catch(RuntimeException e){
            logger.error("ERROR at UserService.deleteUser()", e);
            throw e;
        }
    }

    @Override
    public boolean updateUserStatus(int userId, boolean status){
        logger.info("Started UserService.updateUserStatus()");
        try{
            boolean result = userDao.updateUserStatus(userId, status);
            logger.info("Ending UserService.updateUserStatus()");
            return result;
        }catch(RuntimeException e){
            logger.error("ERROR at UserService.updateUserStatus()", e);
            throw e;
        }
    }
    @Override
    public User authenticate(String username, String password){
        logger.info("Started UserService.authenticate(), username={}", username);
        if(username==null || username.isBlank() || password==null){
            throw new ServiceException("Username and password are required");
        }
        User user=userDao.getUserByUsername(username);
        if(user==null){
            logger.warn("Login failed: unknown username={}",username);
            throw new ServiceException("Invalid username or password");
        }
        if(!user.isActive()){
            logger.warn("Login failed: inactive account, username={}",username);
            throw new ServiceException("This account has been deactivated");
        }
        if(!Objects.equals(user.getPassword(),password)){
            logger.warn("Login failed: bad password, username={}",username);
            throw new ServiceException("Invalid username or password");
        }
        logger.info("Ending UserService.authenticate(), login successful for username={}",username);
        return user;
    }
}
