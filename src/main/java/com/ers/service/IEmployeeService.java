package com.ers.service;


import com.ers.exception.ServiceException;
import com.ers.model.Employee;
import com.ers.model.User;

import java.util.List;

public interface IEmployeeService {
    //Write business logic here
    Employee addEmployee(Employee employee, User user) throws ServiceException;
    boolean updateEmployee(Employee employee);
    Employee getEmployeeById(int employeeId);
    List<Employee> getAllEmployees();
    boolean deleteEmployee(int employeeId) throws ServiceException, com.ers.exception.ServiceException;
}
