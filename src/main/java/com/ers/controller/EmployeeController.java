package com.ers.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.exception.ServiceException;
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

    private final IEmployeeService employeeService;
    private final Scanner scanner;

    public EmployeeController() {
        this.employeeService = new EmployeeServiceImpl();
        this.scanner = new Scanner(System.in);
    }
    private static final Logger logger =(Logger)LoggerFactory.getLogger(EmployeeController.class);

    public void showMenu() {
        logger.info("Started EmployeeController.showMenu()");
        while(true){
            System.out.println();
            System.out.println("Employee Management:");
            System.out.println("1. Add Employee");
            System.out.println("2. Update Employee");
            System.out.println("3. Delete Employee");
            System.out.println("4. Get Employee By ID");
            System.out.println("5. Get All Employees");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            try{
                int choice = Integer.parseInt(scanner.nextLine());
                switch(choice){
                    case 1:
                        addEmployee();
                        break;

                    case 2:
                        updateEmployee();
                        break;

                    case 3:
                        deleteEmployee();
                        break;

                    case 4:
                        getEmployeeById();
                        break;
                    case 5: getAllEmployees();
                        break;
                    case 6: logger.info("Ending EmployeeController.showMenu()");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }catch(NumberFormatException e){
                logger.error("Invalid menu input", e);
                System.out.println("Please enter a valid number.");
            }catch(Exception e){
                logger.error("Error in EmployeeController.showMenu()", e);
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void addEmployee() {
        logger.info("Started EmployeeController.addEmployee()");
        try {
            System.out.println();
            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Role (EMPLOYEE/MANAGER/FINANCE/ADMIN): ");
            Role role=Role.valueOf(scanner.nextLine().toUpperCase());

            System.out.print("Full Name: ");
            String fullName =scanner.nextLine();

            System.out.print("Email: ");
            String email =scanner.nextLine();

            System.out.print("Department ID: ");
            int departmentId =Integer.parseInt(scanner.nextLine());

            User user = new User();
            user.setUserName(username);
            user.setPassword(password);
            user.setRole(role);
            Department department = new Department();
            department.setDepartmentId(departmentId);
            Employee employee = new Employee();
            employee.setFullName(fullName);
            employee.setEmail(email);
            employee.setDepartment(department);
            Employee savedEmployee = employeeService.addEmployee(employee, user);
            System.out.println("Employee added successfully.");
            System.out.println("Employee ID: "+savedEmployee.getEmployeeId());
            logger.info("Employee added successfully");
        }catch(ServiceException e){
            logger.error("ERROR at EmployeeController.addEmployee()",e);
            System.out.println("Unable to add employee: "+e.getMessage());
        }catch(Exception e){
            logger.error("ERROR at EmployeeController.addEmployee()",e);
            System.out.println("Invalid input: "+e.getMessage());
        }finally{
            logger.info("Ending EmployeeController.addEmployee()");
        }
    }

    private void updateEmployee() {

        logger.info("Started EmployeeController.updateEmployee()");
        try {

            System.out.println();
            System.out.print("Employee ID: ");
            int employeeId = Integer.parseInt(scanner.nextLine());

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Department ID: ");
            int departmentId=Integer.parseInt(scanner.nextLine());

            Department department = new Department();
            department.setDepartmentId(departmentId);

            Employee employee = new Employee();

            employee.setEmployeeId(employeeId);
            employee.setEmail(email);
            employee.setDepartment(department);
            boolean updated=employeeService.updateEmployee(employee);
            if(updated){
                System.out.println("Employee updated successfully.");
            }else{
                System.out.println("Employee update failed.");
            }
        }catch(Exception e){
            logger.error( "ERROR at EmployeeController.updateEmployee()",e);
            System.out.println("Unable to update employee: "+e.getMessage());
        }finally{
            logger.info("Ending EmployeeController.updateEmployee()");
        }
    }

    private void deleteEmployee() {
        logger.info("Started EmployeeController.deleteEmployee()");
        try{
            System.out.println();
            System.out.print("Employee ID: ");
            int employeeId=Integer.parseInt(scanner.nextLine());
            System.out.print("Are you sure you want to delete this employee? (Y/N): ");
            String confirmation=scanner.nextLine();
            if (!confirmation.equalsIgnoreCase("Y")){
                System.out.println("Employee deletion cancelled.");
                return;
            }
            boolean deleted=employeeService.deleteEmployee(employeeId);
            if(deleted){
                System.out.println("Employee deleted successfully.");
            }else{
                System.out.println("Employee deletion failed.");
            }
        }catch(ServiceException e){
            logger.error("ERROR at EmployeeController.deleteEmployee()",e);
            System.out.println("Unable to delete employee: "+e.getMessage());
        }catch(Exception e){
            logger.error("ERROR at EmployeeController.deleteEmployee()",e);
            System.out.println("Invalid input: "+e.getMessage());
        }finally{
            logger.info("Ending EmployeeController.deleteEmployee()");
        }
    }
    private void getEmployeeById(){
        logger.info("Started EmployeeController.getEmployeeById()");
        try{
            System.out.println();
            System.out.print("Employee ID: ");
            int employeeId=Integer.parseInt(scanner.nextLine());
            Employee employee =employeeService.getEmployeeById(employeeId);
            if(employee==null){
                System.out.println("Employee not found.");
                return;
            }
            displayEmployee(employee);
        }catch(Exception e){
            logger.error("ERROR at EmployeeController.getEmployeeById()",e);
            System.out.println("Unable to retrieve employee: "+e.getMessage());
        }finally{
            logger.info("Ending EmployeeController.getEmployeeById()");
        }
    }
    private void getAllEmployees(){
        logger.info("Started EmployeeController.getAllEmployees()");
        try{
            System.out.println();
            System.out.println("All Employees:");
            List<Employee> employees=employeeService.getAllEmployees();
            if(employees==null||employees.isEmpty()){
                System.out.println("No employees found.");
                return;
            }
            for(Employee employee:employees){
                displayEmployee(employee);
            }
        }catch(Exception e){
            logger.error("ERROR at EmployeeController.getAllEmployees()",e);
            System.out.println("Unable to retrieve employees: "+e.getMessage());
        }finally{
            logger.info("Ending EmployeeController.getAllEmployees()");
        }
    }
    private void displayEmployee(Employee employee) {
        System.out.println("Employee ID: "+employee.getEmployeeId());
        System.out.println("Full Name: "+employee.getFullName());
        System.out.println("Email: "+employee.getEmail());
        if(employee.getDepartment()!=null){
            System.out.println("Department ID: "+employee.getDepartment().getDepartmentId());
        }
        if(employee.getUser()!=null){
            System.out.println("User ID: "+employee.getUser().getUserId());
            System.out.println("Role: "+employee.getUser().getRole());
        }
    }

}
