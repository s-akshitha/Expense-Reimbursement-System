package com.ers.controller;

import com.ers.model.Employee;
import com.ers.service.IEmployeeService;

import java.util.List;

public class EmployeeController {
    private IEmployeeService employeeService;

    public EmployeeController(IEmployeeService employeeService){
        this.employeeService = employeeService;
    }

    public Employee addNewEmployee(){
        return null;
    }
    public boolean updateEmployee(Employee employee){
        return false;
    }
    public Employee getEmployeeById(int employeeId) {
        return null;
    }
    public List<Employee> getAllEmployees() {
        return List.of();
    }
    public boolean deleteEmployee(int employeeId) {
        return false;
    }
}
