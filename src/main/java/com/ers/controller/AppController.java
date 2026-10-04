package com.ers.controller;

import ch.qos.logback.classic.Logger;

import com.ers.exception.ServiceException;
import com.ers.model.Department;
import com.ers.model.Employee;
import com.ers.model.Role;
import com.ers.model.User;
import com.ers.service.EmployeeServiceImpl;
import com.ers.service.IEmployeeService;
import com.ers.service.IUserService;
import com.ers.service.UserServiceImpl;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class AppController{
    private final Scanner scanner;
    private final IUserService userService;
    private final IEmployeeService employeeService;
    private final ExpenseClaimController expenseClaimController;
    private final ReimbursementController reimbursementController;
    private static final Logger logger=(Logger)LoggerFactory.getLogger(AppController.class);
    public AppController(){
        this.scanner=new Scanner(System.in);
        this.userService=new UserServiceImpl();
        this.employeeService=new EmployeeServiceImpl();
        this.expenseClaimController=new ExpenseClaimController();
        this.reimbursementController=new ReimbursementController();
    }
    public void start(){
        logger.info("Started AppController.start()");
        boolean running=true;
        while(running){
            System.out.println("       EXPENSE REIMBURSEMENT SYSTEM");
            System.out.println("1. Login");
            System.out.println("2. Register Employee");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice=readInt();
            switch(choice){
                case 1:login();
                    break;
                case 2:registerEmployee();
                    break;
                case 3:running = false;
                    System.out.println("Ending Expense Reimbursement System.");
                    break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
        logger.info("Ending AppController.start()");
    }
    private void login(){
        logger.info("Started AppController.login()");
        try{
            System.out.println("LOGIN");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            User user=userService.authenticate(username,password);
            if(user==null){
                System.out.println("Invalid username or password.");
                logger.warn("Login failed for username={}", username);
                return;
            }
            System.out.println("Login successful.");
            System.out.println("Welcome, "+user.getUserName());
            System.out.println("Role: "+user.getRole());
            logger.info("User login successful, userId={}, role={}",user.getUserId(),user.getRole());
            routeByRole(user);
        }catch(ServiceException e){
            logger.error("ERROR at AppController.login()",e);
            System.out.println("Login failed: "+e.getMessage());
        }catch(Exception e){
            logger.error("ERROR at AppController.login()",e);
            System.out.println("Login failed: " + e.getMessage());
        }
        logger.info("Ending AppController.login()");
    }
    private void registerEmployee(){
        logger.info("Started AppController.registerEmployee()");
        try{
            System.out.println();
            System.out.println("EMPLOYEE REGISTRATION");
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.println();
            System.out.println("Available Roles:");
            System.out.println("1. EMPLOYEE");
            System.out.println("2. MANAGER");
            System.out.println("3. FINANCE_EXECUTIVE");
            System.out.print("Enter role: ");
            String roleInput = scanner.nextLine();
            Role role;
            switch(roleInput){
                case "1":role=Role.EMPLOYEE;
                    break;
                case "2":role=Role.MANAGER;
                    break;
                case "3":role=Role.FINANCE_EXECUTIVE;
                    break;
                default:System.out.println("Invalid role.");
                    logger.warn("Invalid role selected during registration: {}",roleInput);
                    return;
            }
            System.out.print("Enter full name: ");
            String fullName = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter department ID: ");
            int departmentId=readInt();
            if(departmentId<=0){
                System.out.println("Invalid department ID.");
                return;
            }
            User user = new User();
            user.setUserName(username);
            user.setPassword(password);
            user.setRole(role);
            user.setActive(true);
            Department department = new Department();
            department.setDepartmentId(departmentId);
            Employee employee = new Employee();
            employee.setFullName(fullName);
            employee.setEmail(email);
            employee.setDepartment(department);
            Employee savedEmployee=employeeService.addEmployee(employee, user);
            System.out.println("Employee registered successfully.");
            System.out.println("Employee ID: "+savedEmployee.getEmployeeId());
            System.out.println("Username: "+username);
            System.out.println("Role: "+role);
            logger.info("Employee registration successful, employeeId={}, role={}",
                    savedEmployee.getEmployeeId(),role);
        }catch(ServiceException e){
            logger.error("ERROR at AppController.registerEmployee()",e);
            System.out.println("Employee registration failed: " + e.getMessage());
        }catch(Exception e){
            logger.error("ERROR at AppController.registerEmployee()",e);
            System.out.println("Employee registration failed: " + e.getMessage());
        }
        logger.info("Ending AppController.registerEmployee()");
    }
    private void routeByRole(User user){
        logger.info("Started AppController.routeByRole(), role={}",user.getRole());
        try{
            Employee employee=employeeService.getEmployeeByUserId(user.getUserId());
            if(employee==null){
                logger.warn("No employee record found for userId={}",
                        user.getUserId());
                System.out.println("No employee record is linked to this account.");
                return;
            }
            switch(user.getRole()){
                case EMPLOYEE:logger.info("user to Employee menu, employeeId={}",
                            employee.getEmployeeId());
                    expenseClaimController.showEmployeeMenu(employee);
                    break;
                case MANAGER:logger.info("user to Manager menu, employeeId={}", employee.getEmployeeId());
                    expenseClaimController.showManagerMenu(employee);
                    break;
                case FINANCE_EXECUTIVE:logger.info("user to Finance menu, employeeId={}", employee.getEmployeeId());
                    reimbursementController.showMenu(employee);
                    break;
                default: logger.warn("No menu configured for role={}",user.getRole());
                    System.out.println("No menu configured for role: "+ user.getRole());
            }
        }catch(ServiceException e){
            logger.error("ERROR at AppController.routeByRole()", e);
            System.out.println("Unable to load role menu: " + e.getMessage());
        }
        logger.info("Ending AppController.routeByRole()");
    }
    private int readInt(){
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        }catch(NumberFormatException e){
            logger.warn("Invalid integer input");
            return -1;
        }
    }
}