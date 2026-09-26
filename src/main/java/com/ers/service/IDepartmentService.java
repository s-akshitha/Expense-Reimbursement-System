package com.ers.service;

import com.ers.model.Department;
import com.ers.model.Employee;

import java.util.List;

public interface IDepartmentService {
    Department addDepartment(Department department);
    boolean updateDepartment(Department department);
    Department getDepartmentById(int departmentId);
    List<Department> getAllDepartments();
    boolean deleteDepartmentById(int departmentId);
    //List<Employee> getEmployeesByDepartmentId(int departmentId);
   // Department getDepartmentByManagerId(int managerId);
}
