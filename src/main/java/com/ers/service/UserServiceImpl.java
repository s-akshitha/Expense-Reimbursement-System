package com.ers.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.dao.IUserDao;
import com.ers.dao.UserDaoImpl;
import com.ers.model.User;


import java.sql.Connection;
import java.util.List;

public class UserServiceImpl implements IUserService{
    private IUserDao userDao;
    private static final Logger logger;
    static {
        LoggerContext context = new LoggerContext();
        logger = context.getLogger(EmployeeServiceImpl.class.getName());
    }

    public UserServiceImpl(){
        this.userDao=new UserDaoImpl();
    }

    @Override
    public User addUser(Connection connection,User user) {
        logger.info("Started UserServiceImpl.addUser()");
        try{
            User result = userDao.addUser(connection,user);
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
        boolean result = userDao.updateUser(user);
        logger.info("Ending UserService.updateUser()");
        return result;
    }

    @Override
    public User getUserById(int userId) {
        logger.info("Started UserService.getUserById()");
        try{
            User user = userDao.getUserById(userId);
            logger.info("Ending UserService.getUserById()");
            return user;
        }catch(RuntimeException e){
            logger.error("ERROR at UserService.getUserById()");
            throw e;
        }
    }
    @Override
    public List<User> getAllUsers() {
        logger.info("Started UserService.getAllUsers()");
        try{
            List<User> users =userDao.getAllUsers();
            logger.info("Ending UserService.getAllUsers()");
            return users;
        }catch(RuntimeException e){
            logger.error("ERROR at UserService.getAllUsers()",e);
            throw e;
        }
    }

    @Override
    public boolean deleteUser(Connection connection,int userId) {
        logger.info("Started UserService.deleteUser()");
        boolean result =userDao.deleteUser(connection,userId);
        logger.info("Ending UserService.deleteUser()");
        return result;
    }

    @Override
    public boolean updateUserStatus(int userId,boolean status) {
        logger.info("Started UserService.updateUserStatus()");
        boolean result =userDao.updateUserStatus(userId,status);
        logger.info("Ending UserService.updateUserStatus()");
        return result;
    }
}
