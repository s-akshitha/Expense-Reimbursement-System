package com.ers.service;


import com.ers.model.Employee;

import java.util.List;

public interface IEmployeeService {
    Employee addEmployee(Employee employee);
    boolean updateEmployee(Employee employee);
    Employee getEmployeeById(int employeeId);
    List<Employee> getAllEmployees();
    boolean deleteEmployee(int employeeId);
}
