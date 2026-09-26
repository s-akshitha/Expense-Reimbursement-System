package com.ers.dao;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.exception.DaoException;
import com.ers.model.Department;
import com.ers.model.Employee;
import com.ers.util.JDBCUtil;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoImpl implements IDepartmentDao{
    private static final Logger logger;
    static {
        LoggerContext context = new LoggerContext();
        logger = context.getLogger(DepartmentDaoImpl.class.getName());
    }
    private final JDBCUtil jdbcUtil;
    public DepartmentDaoImpl(){
        this.jdbcUtil = new JDBCUtil();
    }
    private static final String insertQuery="insert into departments(department_name) values(?)";
    private static final String updateQuery = "update departments set department_name=? where department_id=?";
    private static final String selectById ="select department_id,department_name,manager_id from departments where department_id=?";
    private static final String selectAllQuery="select department_id, department_name,manager_id from departments";
    private static final String selectManagerQuery ="select manager_id from departments where department_id=?";
    private static final String setManagerQuery="update departments set manager_id=? where department_id=?";
    private static final String deleteQuery="delete from departments where department_id=?";
    private static final String isManagerQuery ="select manager_id from departments where manager_id=?";
    private static final String removeManagerQuery="update departments set manager_id=NULL where department_id=?";
    private static final String getManagerIdQuery = "select manager_id from departments where department_id=?";
    @Override
    public Department addDepartment(Department department) {
        try(Connection connection = jdbcUtil.getConnection();PreparedStatement ps=connection.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,department.getDepartmentName());
            ps.executeUpdate();
            try(ResultSet rs=ps.getGeneratedKeys()) {
                if(rs.next()){
                    department.setDepartmentId(rs.getInt(1));
                }
            }
            logger.info("Ending DepartmentDaoImpl.addDepartment()");
            return department;
        }catch(SQLException e){
            logger.error("ERROR at DepartmentDaoImpl.addDepartment()", e);
            throw new DaoException("Unable to add department",e);
        }
    }

    @Override
    public boolean updateDepartment(Department department) {
        logger.info("Started DepartmentDaoImpl.updateDepartment()");
        try(Connection connection=jdbcUtil.getConnection();PreparedStatement ps=connection.prepareStatement(updateQuery)){
            ps.setString(1,department.getDepartmentName());
            ps.setInt(2,department.getDepartmentId());
            int rows=ps.executeUpdate();
            logger.info("Ending DepartmentDaoImpl.updateDepartment()");
            return rows>0;
        }catch(SQLException e){
            logger.error("Error at DepartmentDaoImpl.updateDepartment()",e);
            throw new DaoException("Unable to update department",e);
        }
    }

    @Override
    public Department getDepartmentById(int departmentId){
        logger.info("Started DepartmentDaoImpl.getDepartmentById()");
        try(Connection connection = jdbcUtil.getConnection();PreparedStatement ps=connection.prepareStatement(selectById)){
            ps.setInt(1, departmentId);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    Department department=new Department();
                    department.setDepartmentId(rs.getInt("department_id"));
                    department.setDepartmentName(rs.getString("department_name"));
                    logger.info("Ending DepartmentDaoImpl.getDepartmentById()");
                    return department;
                }
                return null;
            }
        }catch(SQLException e){
            logger.error("ERROR at DepartmentDaoImpl.getDepartmentById()",e);
            throw new DaoException("Unable to get department",e);
        }
    }

    @Override
    public List<Department> getAllDepartments() {
        logger.info("Starting DepartmentDaoImpl.getAllDepartments()");
        List<Department> departments=new ArrayList<>();
        try(Connection connection=jdbcUtil.getConnection();PreparedStatement ps=connection.prepareStatement(selectAllQuery);ResultSet rs=ps.executeQuery()){
            while(rs.next()){
                Department department=new Department();
                department.setDepartmentId(rs.getInt("department_id"));
                department.setDepartmentName(rs.getString("department_name"));
                departments.add(department);
            }
            logger.info("Ending DepartmentDaoImpl.getAllDepartments()");
            return departments;
        }catch(SQLException e){
            logger.error("ERROR at DepartmentDaoImpl.getAllDepartments()",e);
            throw new DaoException("Unable to get departments",e);
        }
    }

    @Override
    public boolean deleteDepartmentById(int departmentId) {
        logger.info("Started DepartmentDaoImpl.deleteDepartmentById(),departmentId={}",departmentId);
        try(Connection connection=jdbcUtil.getConnection();PreparedStatement ps=connection.prepareStatement(deleteQuery)){
            ps.setInt(1, departmentId);
            int rows=ps.executeUpdate();
            logger.info("Ending DepartmentDaoImpl.deleteDepartmentById()");
            return rows>0;
        }catch(SQLException e){
            logger.error("ERROR at DepartmentDaoImpl.deleteDepartmentById()", e);
            throw new DaoException("Unable to delete department", e);
        }
    }
//
//    @Override
//    public List<Employee> getEmployeesByDepartmentId(int departmentId) {
//        return List.of();
//    }
//
//    @Override
//    public Department getDepartmentByManagerId(int managerId) {
//        return null;
//    }

    @Override
    public boolean hasManager(Connection connection, int departmentId) {
        logger.info("Started DepartmentDaoImpl.hasManager()");
        try(PreparedStatement ps=connection.prepareStatement(selectManagerQuery)){
            ps.setInt(1, departmentId);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    boolean hasManager=rs.getObject("manager_id")!=null;
                    logger.info("Ending DepartmentDaoImpl.hasManager()");
                    return hasManager;
                }
                throw new DaoException("Department not found: "+departmentId);
            }
        }catch(SQLException e){
            logger.error("ERROR at DepartmentDaoImpl.hasManager()",e);
            throw new DaoException("Unable to check department manager",e);
        }
    }

    @Override
    public void removeManager(Connection connection, int departmentId) {
        logger.info("Started DepartmentDaoImpl.removeManager(), departmentId={}",departmentId);
        try(PreparedStatement ps =connection.prepareStatement(removeManagerQuery)){
            ps.setInt(1, departmentId);
            int rows = ps.executeUpdate();
            if(rows==0){
                throw new DaoException("Department not found: " + departmentId);
            }
            logger.info("Manager removed from department");
        }catch(SQLException e){
            logger.error("ERROR at DepartmentDaoImpl.removeManager()", e);
            throw new DaoException("Unable to remove department manager", e);
        }
    }

    @Override
    public boolean isManager(Connection connection, int employeeId){
        logger.info("Started DepartmentDaoImpl.isManager(), employeeId={}",employeeId);
        try(PreparedStatement ps =connection.prepareStatement(isManagerQuery)){
            ps.setInt(1, employeeId);
            try(ResultSet rs=ps.executeQuery()){
                boolean isManager=rs.next();
                logger.info("Employee is manager={}", isManager);
                return isManager;
            }
        }catch(SQLException e){
            logger.error("ERROR at DepartmentDaoImpl.isManager()", e);
            throw new DaoException("Unable to check employee manager", e);
        }
    }

    public int assignManager(Connection connection, int departmentId, int employeeId) {
        logger.info("Started DepartmentDaoImpl.assignManager()");
        try(PreparedStatement ps=connection.prepareStatement(setManagerQuery)){
            ps.setInt(1, employeeId);
            ps.setInt(2, departmentId);
            int rowsUpdated = ps.executeUpdate();
            logger.info("Ending DepartmentDaoImpl.assignManager()");
            return rowsUpdated;
        }catch(SQLException e){
            logger.error("ERROR at DepartmentDaoImpl.assignManager()",e);
            throw new DaoException("Unable to assign department manager",e);
        }
    }

    @Override
    public Integer getManagerIdByDepartmentId(Connection connection, int departmentId) {
        logger.info("Started DepartmentDaoImpl.getManagerIdByDepartmentId()");
        try(PreparedStatement ps =connection.prepareStatement(getManagerIdQuery)){
            ps.setInt(1, departmentId);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    int managerId=rs.getInt("manager_id");
                    logger.info("Ending DepartmentDaoImpl.getManagerIdByDepartmentId(), managerId={}",managerId);
                    return managerId;
                }
            }
            return null;
        }catch(SQLException e){
            logger.error("ERROR at DepartmentDaoImpl.getManagerIdByDepartmentId()", e);
            throw new DaoException("Unable to retrieve department manager",e);
        }
    }
}
