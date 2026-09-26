package com.ers.dao;

import com.ers.exception.DaoException;
import com.ers.model.Role;
import com.ers.model.User;
import com.ers.util.JDBCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class UserDaoImpl implements IUserDao{

    private JDBCUtil jdbcUtil;

    private static final Logger logger=LoggerFactory.getLogger(UserDaoImpl.class);

    private static final String insertQuery="insert into users (username,password,role,is_active,created_at) values(?,?,?,?,?)";

    private static final String updateQuery ="update users set password=? where user_id=?";

    private static final String deleteQuery ="delete from users where user_id=?";

    private static final String selectByIdQuery ="select user_id,username,password,role,is_active,created_at from users where user_id=?";

    private static final String selectAllQuery ="select user_id,username,password,role,is_active,created_at from users";

    private static final String updateStatusQuery="update users set is_active=? where user_id=?";

    public UserDaoImpl(){
        this.jdbcUtil = new JDBCUtil();
    }

    @Override
    public User addUser(Connection con,User user) {
        logger.info("Started UserDaoImpl.addUser()");
        try(PreparedStatement ps = con.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,user.getUserName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole().name());
            ps.setBoolean(4,user.isActive());
            ps.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
            int rows = ps.executeUpdate();
            if(rows==0)
                throw new DaoException("Failed to add User");
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    user.setUserId(rs.getInt(1));
                }
            }
            logger.info("User inserted successfully!");
            return user;
        }catch (SQLException exception){
            logger.error("Failed at UserDaoImpl.addUser()",exception);
            throw new DaoException("Unable to add user");
        }
    }
    @Override
    public boolean updateUser(User user){
        logger.info("started UserDaoImpl.updateUser()");
        try(Connection connection= jdbcUtil.getConnection();PreparedStatement ps = connection.prepareStatement(updateQuery)){
            ps.setString(1,user.getPassword());
            ps.setInt(2,user.getUserId());
            boolean res = ps.executeUpdate()>0;
            logger.info("Ending UserDaoImpl.updateUser");
            return res;
        }catch(SQLException e){
            logger.error("Failed at UserDaoImpl.updateUser()");
            throw new DaoException("Unable to update user",e);
        }
    }
    @Override
    public User getUserById(int userId) {
        logger.info("Started UserDaoImpl.getUserById()");
        try (Connection connection=jdbcUtil.getConnection();PreparedStatement ps=connection.prepareStatement(selectByIdQuery)){
            ps.setInt(1, userId);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUserName(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(Role.valueOf(rs.getString("role")));
                    user.setActive(rs.getBoolean("is_active"));
                    logger.info("End UserDaoImpl.getUserById()");
                    return user;
                }
            }
            logger.info("Ending UserDaoImpl.getUserById() no user found");
            return null;
        }catch(SQLException e){
            logger.error("ERROR at UserDaoImpl.getUserById()");
            throw new DaoException("Unable to retrieve user",e);
        }
    }
    @Override
    public List<User> getAllUsers() {
        logger.info("Started UserDaoImpl.getAllUsers()");
        List<User> users = new ArrayList<>();
        try(Connection con = jdbcUtil.getConnection();PreparedStatement ps=con.prepareStatement(selectAllQuery);ResultSet rs=ps.executeQuery()){
            while(rs.next()){
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUserName(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(Role.valueOf(rs.getString("role")));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
            logger.info("Ending UserDaoImpl.getAllUsers()");
            return users;
        } catch (SQLException e) {
            logger.error("ERROR at UserDaoImpl.getAllUsers()");
            throw new DaoException("Unable to retrieve users", e);
        }
    }
    @Override
    public boolean deleteUser(Connection connection,int userId) {
        logger.info("Started UserDaoImpl.deleteUser()");
        try(PreparedStatement ps=connection.prepareStatement(deleteQuery)){
            ps.setInt(1,userId);
            boolean result=ps.executeUpdate()>0;
            logger.info("Ending UserDaoImpl.deleteUser()");
            return result;
        }catch(SQLException e){
            logger.error("ERROR at UserDaoImpl.deleteUser()");
            throw new DaoException("Unable to delete user", e);
        }
    }

    @Override
    public boolean updateUserStatus(int userId, boolean active) {
        logger.info("Starting UserDaoImpl.updateUserStatus()");
        try(Connection connection = jdbcUtil.getConnection();PreparedStatement ps =connection.prepareStatement(updateStatusQuery)){
            ps.setBoolean(1, active);
            ps.setInt(2, userId);
            boolean result = ps.executeUpdate() > 0;
            logger.info("Ending UserDaoImpl.updateUserStatus()");
            return result;
        }catch(SQLException e){
            logger.error("ERROR at UserDaoImpl.updateUserStatus()");
            throw new DaoException("Unable to update user status",e);
        }
    }
}
