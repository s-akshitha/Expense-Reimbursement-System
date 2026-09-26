package com.ers.dao;

import com.ers.exception.ServiceException;
import com.ers.model.Employee;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IEmployeeDao {
    Employee addEmployee(Connection connection,Employee employee);
    boolean updateEmployee(Employee employee);
    Employee getEmployeeById(int employeeId);
    List<Employee> getAllEmployees();
    boolean deleteEmployee(Connection connection,int employeeId) throws ServiceException;
    Employee getEmployeeById(Connection connection,int employeeId);
}
