package com.ers.controller;

import ch.qos.logback.classic.Logger;
import com.ers.model.Department;
import com.ers.model.Employee;
import com.ers.model.Role;
import com.ers.model.User;
import com.ers.service.EmployeeServiceImpl;
import com.ers.service.IEmployeeService;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class EmployeeController {
    private final Scanner scanner;
    private final IEmployeeService employeeService;
    private static final Logger logger=(Logger) LoggerFactory.getLogger(EmployeeController.class);


    public EmployeeController() {
        this.scanner = new Scanner(System.in);
        this.employeeService = new EmployeeServiceImpl();
    }

    public void showMenu() {
        logger.info("Started EmployeeController.showMenu()");
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("EMPLOYEE MANAGEMENT\n1. Add Employee\n2. Update Employee\n3. Get Employee By ID\n4. List All Employees\n5. Delete Employee\n6. Back\nEnter your choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: addEmployee(); break;
                case 2: updateEmployee(); break;
                case 3: getEmployeeById(); break;
                case 4: listAllEmployees(); break;
                case 5: deleteEmployee(); break;
                case 6: running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
        logger.info("Ending EmployeeController.showMenu()");
    }

    private void addEmployee() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter role (EMPLOYEE/MANAGER/FINANCE_EXECUTIVE): ");
            Role role = Role.valueOf(scanner.nextLine().trim().toUpperCase());

            User user = new User();
            user.setUserName(username);
            user.setPassword(password);
            user.setRole(role);
            user.setActive(true);

            System.out.print("Enter full name: ");
            String fullName = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter department ID: ");
            int departmentId = readInt();
            Department department = new Department();
            department.setDepartmentId(departmentId);

            Employee employee = new Employee();
            employee.setFullName(fullName);
            employee.setEmail(email);
            employee.setDepartment(department);

            Employee saved = employeeService.addEmployee(employee, user);
            System.out.println("Employee added successfully. Employee ID: " + saved.getEmployeeId());
        } catch (Exception e) {
            logger.error("ERROR at EmployeeController.addEmployee()", e);
            System.out.println("Failed to add employee: " + e.getMessage());
        }
    }

    private void updateEmployee() {
        try {
            System.out.print("Enter employee ID: ");
            int employeeId = readInt();
            System.out.print("Enter new email: ");
            String email = scanner.nextLine();
            System.out.print("Enter new department ID: ");
            int departmentId = readInt();

            Employee employee = new Employee();
            employee.setEmployeeId(employeeId);
            employee.setEmail(email);
            Department department = new Department();
            department.setDepartmentId(departmentId);
            employee.setDepartment(department);

            boolean updated = employeeService.updateEmployee(employee);
            System.out.println(updated ? "Employee updated successfully." : "Employee update failed.");
        } catch (Exception e) {
            logger.error("ERROR at EmployeeController.updateEmployee()", e);
            System.out.println("Failed to update employee: " + e.getMessage());
        }
    }

    private void getEmployeeById() {
        try {
            System.out.print("Enter employee ID: ");
            int employeeId = readInt();
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                System.out.println("Employee not found.");
                return;
            }
            displayEmployee(employee);
        } catch (Exception e) {
            logger.error("ERROR at EmployeeController.getEmployeeById()", e);
            System.out.println("Failed to get employee: " + e.getMessage());
        }
    }

    private void listAllEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            if (employees.isEmpty()) {
                System.out.println("No employees found.");
                return;
            }
            employees.forEach(this::displayEmployee);
        } catch (Exception e) {
            logger.error("ERROR at EmployeeController.listAllEmployees()", e);
            System.out.println("Failed to list employees: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        try {
            System.out.print("Enter employee ID: ");
            int employeeId = readInt();
            boolean deleted = employeeService.deleteEmployee(employeeId);
            System.out.println(deleted ? "Employee deleted successfully." : "Employee deletion failed.");
        } catch (Exception e) {
            logger.error("ERROR at EmployeeController.deleteEmployee()", e);
            System.out.println("Failed to delete employee: " + e.getMessage());
        }
    }

    private void displayEmployee(Employee employee) {
        System.out.println("\nEMPLOYEE");
        System.out.println("Employee ID: " + employee.getEmployeeId());
        System.out.println("Full Name: " + employee.getFullName());
        System.out.println("Email: " + employee.getEmail());
        if (employee.getDepartment() != null) {
            System.out.println("Department ID: " + employee.getDepartment().getDepartmentId());
        }
        if (employee.getUser() != null) {
            System.out.println("Username: " + employee.getUser().getUserName());
            System.out.println("Role: " + employee.getUser().getRole());
            System.out.println("Active: " + employee.getUser().isActive());
        }
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer input");
            return -1;
        }
    }
}