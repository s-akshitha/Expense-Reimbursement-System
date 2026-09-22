package com.ers.dao;

import com.ers.model.Employee;
import com.ers.util.JDBCUtil;

import java.util.List;

public class EmployeeDaoImpl implements IEmployeeDao {
    JDBCUtil jdbcUtil;
    public EmployeeDaoImpl(JDBCUtil jdbcUtil){
        this.jdbcUtil=jdbcUtil;
    }
    //Crud operation will be done here
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
