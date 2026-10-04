package com.ers.controller;

import ch.qos.logback.classic.Logger;
import com.ers.model.Department;
import com.ers.service.DepartmentServiceImpl;
import com.ers.service.IDepartmentService;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class DepartmentController {
    private final Scanner scanner;
    private final IDepartmentService departmentService;
    private static final Logger logger=(Logger) LoggerFactory.getLogger(DepartmentController.class);

    public DepartmentController() {
        this.scanner = new Scanner(System.in);
        this.departmentService = new DepartmentServiceImpl();
    }

    public void showMenu() {
        logger.info("Started DepartmentController.showMenu()");
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("DEPARTMENT MANAGEMENT\n1. Add Department\n2. Update Department\n3. Get Department By ID\n4. List All Departments\n5. Delete Department\n6. Assign Manager\n7. Back\nEnter your choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: addDepartment(); break;
                case 2: updateDepartment(); break;
                case 3: getDepartmentById(); break;
                case 4: listAllDepartments(); break;
                case 5: deleteDepartment(); break;
                case 6: assignManager(); break;
                case 7: running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
        logger.info("Ending DepartmentController.showMenu()");
    }

    private void addDepartment() {
        try {
            System.out.print("Enter department name: ");
            String name = scanner.nextLine();
            Department department = new Department();
            department.setDepartmentName(name);
            Department saved = departmentService.addDepartment(department);
            System.out.println("Department added successfully. Department ID: " + saved.getDepartmentId());
        } catch (Exception e) {
            logger.error("ERROR at DepartmentController.addDepartment()", e);
            System.out.println("Failed to add department: " + e.getMessage());
        }
    }

    private void updateDepartment() {
        try {
            System.out.print("Enter department ID: ");
            int departmentId = readInt();
            System.out.print("Enter new department name: ");
            String name = scanner.nextLine();
            Department department = new Department();
            department.setDepartmentId(departmentId);
            department.setDepartmentName(name);
            boolean updated = departmentService.updateDepartment(department);
            System.out.println(updated ? "Department updated successfully." : "Department update failed.");
        } catch (Exception e) {
            logger.error("ERROR at DepartmentController.updateDepartment()", e);
            System.out.println("Failed to update department: " + e.getMessage());
        }
    }

    private void getDepartmentById() {
        try {
            System.out.print("Enter department ID: ");
            int departmentId = readInt();
            Department department = departmentService.getDepartmentById(departmentId);
            if (department == null) {
                System.out.println("Department not found.");
                return;
            }
            displayDepartment(department);
        } catch (Exception e) {
            logger.error("ERROR at DepartmentController.getDepartmentById()", e);
            System.out.println("Failed to get department: " + e.getMessage());
        }
    }

    private void listAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            if (departments.isEmpty()) {
                System.out.println("No departments found.");
                return;
            }
            departments.forEach(this::displayDepartment);
        } catch (Exception e) {
            logger.error("ERROR at DepartmentController.listAllDepartments()", e);
            System.out.println("Failed to list departments: " + e.getMessage());
        }
    }

    private void deleteDepartment() {
        try {
            System.out.print("Enter department ID: ");
            int departmentId = readInt();
            boolean deleted = departmentService.deleteDepartmentById(departmentId);
            System.out.println(deleted ? "Department deleted successfully." : "Department deletion failed.");
        } catch (Exception e) {
            logger.error("ERROR at DepartmentController.deleteDepartment()", e);
            System.out.println("Failed to delete department: " + e.getMessage());
        }
    }

    private void assignManager() {
        try {
            System.out.print("Enter department ID: ");
            int departmentId = readInt();
            System.out.print("Enter employee ID of manager: ");
            int employeeId = readInt();
            boolean assigned = departmentService.assignManager(departmentId, employeeId);
            System.out.println(assigned ? "Manager assigned successfully." : "Manager assignment failed.");
        } catch (Exception e) {
            logger.error("ERROR at DepartmentController.assignManager()", e);
            System.out.println("Failed to assign manager: " + e.getMessage());
        }
    }

    private void displayDepartment(Department department) {
        System.out.println("\nDEPARTMENT");
        System.out.println("Department ID: " + department.getDepartmentId());
        System.out.println("Department Name: " + department.getDepartmentName());
        System.out.println("Manager ID: " + (department.getManagerId() == 0 ? "None" : department.getManagerId()));
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