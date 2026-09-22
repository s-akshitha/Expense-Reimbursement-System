package com.ers.service;

import com.ers.dao.EmployeeDaoImpl;
import com.ers.dao.IEmployeeDao;
import com.ers.model.Employee;

import java.util.List;

public class EmployeeServiceImpl implements IEmployeeService{
    IEmployeeDao employeeDao;
    public EmployeeServiceImpl(IEmployeeDao employeeDao){
        this.employeeDao = employeeDao;
    }
    //Write business logic here

    @Override
    public Employee addEmployee(Employee employee) {
        return null;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        return false;
    }

    @Override
    public Employee getEmployeeById(int employeeId) {
        return null;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return List.of();
    }

    @Override
    public boolean deleteEmployee(int employeeId) {
        return false;
    }
}
