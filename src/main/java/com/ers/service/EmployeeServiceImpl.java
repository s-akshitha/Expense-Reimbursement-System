package com.ers.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.dao.*;
import com.ers.exception.ServiceException;
import com.ers.model.Employee;
import com.ers.model.Role;
import com.ers.model.User;
import com.ers.util.JDBCUtil;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EmployeeServiceImpl implements IEmployeeService{
    private IEmployeeDao employeeDao;
    private JDBCUtil jdbcUtil;
    private IUserDao userDao;
    private IDepartmentDao departmentDao;
    private static final Logger logger;
    static {
        LoggerContext context=new LoggerContext();
        logger=context.getLogger(EmployeeServiceImpl.class.getName());
    }
    public EmployeeServiceImpl(){
        this.employeeDao = new EmployeeDaoImpl();
        this.jdbcUtil=new JDBCUtil();
        this.userDao = new UserDaoImpl();
        this.departmentDao=new DepartmentDaoImpl();
    }
    public EmployeeServiceImpl(IEmployeeDao employeeDao,JDBCUtil jdbcUtil, IUserDao userDao, IDepartmentDao departmentDao) {
        this.employeeDao = employeeDao;
        this.jdbcUtil = jdbcUtil;
        this.userDao = userDao;
        this.departmentDao = departmentDao;
    }

    //Write business logic here
    @Override
    public Employee addEmployee(Employee employee, User user) throws ServiceException {
        logger.info("Started EmployeeServiceImpl.addEmployee()");
        Connection connection =jdbcUtil.getConnection();
        try{
            connection.setAutoCommit(false);

            logger.info("Transaction started");

            User savedUser=userDao.addUser(connection, user);
            employee.setUser(savedUser);
            logger.info("User created,userId={}", savedUser.getUserId());
            Employee savedEmployee = employeeDao.addEmployee(connection, employee);
            logger.info("Employee created, employeeId={}", savedEmployee.getEmployeeId());

            if(savedUser.getRole()==Role.MANAGER) {
                int departmentId=employee.getDepartment().getDepartmentId();
                int employeeId=savedEmployee.getEmployeeId();
                logger.info("Employee is MANAGER");
                boolean hasManager=departmentDao.hasManager(connection, departmentId);
                if(hasManager){
                    logger.warn("Department already has a manager");
                    throw new ServiceException("Department already has a manager");
                }
                departmentDao.assignManager(connection, departmentId, employeeId);
                logger.info("Employee assigned as department manager");
            }
            connection.commit();
            logger.info("Transaction commit");
            logger.info("Ending EmployeeServiceImpl.addEmployee()");
            return savedEmployee;
        }catch(Exception e){
            logger.error("ERROR at EmployeeServiceImpl.addEmployee()",e);
            if(connection != null){
                try{
                    connection.rollback();
                    logger.warn("Transaction rollback");
                }catch(SQLException exception){
                    logger.error("ERROR at Rollback failed",exception);
                }
            }
            throw new ServiceException("Employee registration failed",e);
        }finally{
            if(connection!=null){
                try{
                    connection.close();
                    logger.info("Database connection closed");
                }catch(SQLException e){
                    logger.error("ERROR at Closing connection",e);
                }
            }
        }
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        logger.info("At updateEmployee()");
        boolean updated=employeeDao.updateEmployee(employee);
        if(updated){
            logger.info("Employee updated successfully");
        }else{
            logger.warn("Employee update failed");
        }
        return updated;
    }

    @Override
    public Employee getEmployeeById(int employeeId) {
        logger.info("Started EmployeeServiceImpl.getEmployeeById()");
        Employee employee=employeeDao.getEmployeeById(employeeId);
        if(employee!=null){
            logger.info("Employee found, employeeId={}", employeeId);
        }else{
            logger.warn("Employee not found");
        }
        return employee;
    }

    @Override
    public List<Employee> getAllEmployees() {
        logger.info("Started EmployeeServiceImpl.getAllEmployees()");
        List<Employee> employees = employeeDao.getAllEmployees();
        logger.info("Employees retrieved successfully");
        return employees;
    }

    @Override
    public boolean deleteEmployee(int employeeId) throws ServiceException {
        logger.info("Started EmployeeServiceImpl.deleteEmployee(), employeeId={}",employeeId);
        Connection connection= jdbcUtil.getConnection();
        try{
            connection.setAutoCommit(false);
            logger.info("Transaction started to delete Employee");
            Employee employee=employeeDao.getEmployeeById(connection,employeeId);
            if(employee==null) {
                logger.warn("Employee not found, employeeId={}", employeeId);
                throw new ServiceException("Employee not found");
            }
            int userId=employee.getUser().getUserId();
            int departmentId=employee.getDepartment().getDepartmentId();
            logger.info("Employee found, employeeId={}, userId={}, departmentId={}",employeeId,userId,departmentId);
            boolean isManager = departmentDao.isManager(connection,employeeId);
            if(isManager){logger.info("Employee is department manager, removing manager assignment, employeeId={}",employeeId);
                departmentDao.removeManager(connection, departmentId);
            }
            boolean employeeDeleted =employeeDao.deleteEmployee(connection,employeeId);
            if (!employeeDeleted){
                throw new ServiceException("Employee deletion failed");
            }
            logger.info("Employee deleted successfully, employeeId={}", employeeId);
            boolean userDeleted= userDao.deleteUser(connection, userId);
            if (!userDeleted) {
                throw new ServiceException("User deletion failed");
            }
            logger.info("User deleted successfully, userId={}",userId);
            connection.commit();
            logger.info("Transaction committed");
            logger.info("Ending EmployeeServiceImpl.deleteEmployee()");
            return true;
        }catch(Exception e){
            logger.error("ERROR at EmployeeServiceImpl.deleteEmployee(), employeeId={}",employeeId,e);
            if(connection != null){
                try{
                    connection.rollback();
                    logger.warn("Transaction rollback while deleting Employee");
                }catch(SQLException exception){
                    logger.error("ERROR - Rollback failed",exception);
                }
            }
            throw new ServiceException("Employee deletion failed", e);
        }finally{
            if(connection!=null){
                try{
                    connection.close();
                    logger.info("Database connection closed at EmployeeServiceImpl.deleteEmployee()");
                }catch(SQLException e){
                    logger.error("ERROR at Closing connection at EmployeeServiceImpl.deleteEmployee()", e);
                }
            }
        }
    }
}
