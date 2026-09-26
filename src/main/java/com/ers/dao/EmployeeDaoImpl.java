package com.ers.dao;

import com.ers.exception.DaoException;
import com.ers.model.Department;
import com.ers.model.Employee;
import com.ers.model.Role;
import com.ers.model.User;
import com.ers.util.JDBCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ers.exception.ServiceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDaoImpl implements IEmployeeDao {
    JDBCUtil jdbcUtil;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeDaoImpl.class);
    public EmployeeDaoImpl()
    {
        this.jdbcUtil=new JDBCUtil();

    }
    private static final String insertQuery ="insert into employees(user_id,full_name,email,department_id) values(?,?,?,?)";
    private static final String selectByIdQuery ="select e.employee_id,e.user_id,e.full_name,e.email,e.department_id,u.username,u.password,u.role,u.is_active from employees e join users u on e.user_id=u.user_id where e.employee_id=?";
    private static final String selectAllQuery ="select employee_id,user_id,full_name,email,department_id from employees";
    private static final String updateQuery= "update employees set email=?,department_id=? where employee_id=?";
    private static final String deleteQuery= "delete from employees where employee_id=?";
    //Crud operation will be done here
    @Override
    public Employee addEmployee(Connection connection,Employee employee) {
        logger.info("Started EmployeeDaoImpl.addEmployee()");
        try(PreparedStatement ps= connection.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, employee.getUser().getUserId());
            ps.setString(2, employee.getFullName());
            ps.setString(3, employee.getEmail());
            ps.setInt(4, employee.getDepartment().getDepartmentId());
            ps.executeUpdate();
            try(ResultSet rs=ps.getGeneratedKeys()){
                if(rs.next()){
                    employee.setEmployeeId(rs.getInt(1));
                }
            }
            logger.info("Ending EmployeeDaoImpl.addEmployee()");
            return employee;
        }catch(SQLException e){
            logger.error("ERROR at EmployeeDaoImpl.addEmployee()",e);
            throw new DaoException("Unable to add employee",e);
        }
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        logger.info("START - EmployeeDaoImpl.updateEmployee()");
        try(Connection con=jdbcUtil.getConnection();PreparedStatement ps =con.prepareStatement(updateQuery)){
            ps.setString(1, employee.getEmail());
            ps.setInt(2, employee.getDepartment().getDepartmentId());
            ps.setInt(3, employee.getEmployeeId());
            boolean result=ps.executeUpdate()>0;
            logger.info("Ending EmployeeDaoImpl.updateEmployee()");
            return result;
        }catch(SQLException e){
            logger.error("ERROR at EmployeeDaoImpl.updateEmployee()",e);
            throw new DaoException("Unable to update employee",e);
        }
    }

    @Override
    public Employee getEmployeeById(int employeeId) {
        logger.info("Started EmployeeDaoImpl.getEmployeeById()");
        Employee employee = null;
        try(Connection con = jdbcUtil.getConnection();PreparedStatement ps=con.prepareStatement(selectByIdQuery)){
            ps.setInt(1, employeeId);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    employee=mapEmployee(rs);
                }
            }
            logger.info("Ending EmployeeDaoImpl.getEmployeeById()");
            return employee;
        }catch(SQLException e){
            logger.error("ERROR at EmployeeDaoImpl.getEmployeeById()",e);
            throw new DaoException("Unable to retrieve employee with ID: "+employeeId,e);
        }
    }


    @Override
    public List<Employee> getAllEmployees() {
        logger.info("Started EmployeeDaoImpl.getAllEmployees()");
        List<Employee> employees = new ArrayList<>();
        try(Connection con =jdbcUtil.getConnection();PreparedStatement ps =con.prepareStatement(selectAllQuery);ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                employees.add(mapEmployee(rs));
            }
            logger.info("Ending EmployeeDaoImpl.getAllEmployees()");
            return employees;
        }catch(SQLException e){
            logger.error("ERROR at EmployeeDaoImpl.getAllEmployees()",e);
            throw new DaoException("Unable to retrieve employees",e);
        }
    }

    @Override
    public boolean deleteEmployee(Connection con,int employeeId) throws ServiceException{
        logger.info("Started EmployeeDaoImpl.deleteEmployee()");
        try(PreparedStatement ps =con.prepareStatement(deleteQuery)){
            ps.setInt(1,employeeId);
            boolean deleted =ps.executeUpdate()>0;
            logger.info("Ending EmployeeDaoImpl.deleteEmployee()");
            return deleted;
        }catch(SQLException e){
            logger.error("ERROR at EmployeeDaoImpl.deleteEmployee()",e);
            throw new DaoException("Unable to delete employee",e);
        }
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException{
        Employee employee=new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUserName(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        employee.setUser(user);
        employee.setFullName(rs.getString("full_name"));
        employee.setEmail(rs.getString("email"));
        Department department = new Department();
        department.setDepartmentId(rs.getInt("department_id"));
        employee.setDepartment(department);
        return employee;
    }
    public Employee getEmployeeById(Connection connection,int employeeId){
        logger.info("Started EmployeeDaoImpl.getEmployeeById()");
        try (PreparedStatement ps=connection.prepareStatement(selectByIdQuery)){
            ps.setInt(1, employeeId);
            try(ResultSet rs =ps.executeQuery()){
                if(rs.next()){
                    Employee employee=mapEmployee(rs);
                    logger.info("Ending EmployeeDaoImpl.getEmployeeById()");
                    return employee;
                }
            }
            return null;
        }catch(SQLException e){
            logger.error("ERROR at EmployeeDaoImpl.getEmployeeById()",e);
            throw new DaoException("Unable to retrieve employee",e);
        }
    }
}
