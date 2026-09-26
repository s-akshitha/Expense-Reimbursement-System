package com.ers.dao;

import com.ers.model.Department;
import com.ers.model.Employee;

import java.sql.Connection;
import java.util.List;

public interface IDepartmentDao {
    Department addDepartment(Department department);
    boolean updateDepartment(Department department);
    Department getDepartmentById(int departmentId);
    List<Department> getAllDepartments();
    boolean deleteDepartmentById(int departmentId);
    //List<Employee> getEmployeesByDepartmentId(int departmentId);
    //Department getDepartmentByManagerId(int managerId);
    boolean hasManager(Connection connection, int departmentId);
    void removeManager(Connection connection, int departmentId);
    boolean isManager(Connection connection, int employeeId);
    int assignManager(Connection connection, int departmentId, int employeeId);
    Integer getManagerIdByDepartmentId(Connection connection, int departmentId);
}
