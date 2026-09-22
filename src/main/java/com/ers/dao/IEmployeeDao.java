package com.ers.dao;

import com.ers.model.Employee;

import java.util.List;

public interface IEmployeeDao {
    Employee addEmployee(Employee employee);
    boolean updateEmployee(Employee employee);
    Employee getEmployeeById(int employeeId);
    List<Employee> getAllEmployees();
    boolean deleteEmployee(int employeeId);
}
