package com.ers.controller;

import ch.qos.logback.classic.Logger;
import com.ers.exception.ServiceException;
import com.ers.model.ClaimItem;
import com.ers.model.Department;
import com.ers.model.Employee;
import com.ers.model.ExpenseCategory;
import com.ers.model.ExpenseClaim;
import com.ers.service.*;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExpenseClaimController {
    private final Scanner scanner;
    private final IExpenseClaimService expenseClaimService;
    private final IDepartmentService departmentService;

    public ExpenseClaimController() {
        this.scanner = new Scanner(System.in);
        this.expenseClaimService = new ExpenseClaimServiceImpl();
        this.departmentService = new DepartmentServiceImpl();
    }

    private static final Logger logger=(Logger) LoggerFactory.getLogger(AppController.class);


    public void showEmployeeMenu(Employee employee) {
        logger.info("Started ExpenseClaimController.showEmployeeMenu()");
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("MY EXPENSE CLAIMS\n1. Submit Expense Claim\n2. View My Claims\n3. Get Claim By ID\n4. View Profile\n5. Back\nEnter your choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: submitExpenseClaim(employee); break;
                case 2: viewClaims(expenseClaimService.getClaimsByEmployeeId(employee.getEmployeeId())); break;
                case 3: getExpenseClaimById(); break;
                case 4: viewProfile(employee); break;
                case 5: running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
    }


    public void showManagerMenu(Employee manager) {
        logger.info("Started ExpenseClaimController.showManagerMenu()");
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("CLAIM REVIEW\n1. View Pending Claims (My Department)\n2. Approve/Reject Claim\n3. Get Claim By ID\n4. Back\nEnter your choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: viewClaims(expenseClaimService.getPendingClaimsForManager(manager.getEmployeeId())); break;
                case 2: reviewExpenseClaim(manager); break;
                case 3: getExpenseClaimById(); break;
                case 4: running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void submitExpenseClaim(Employee employee) {
        logger.info("Started ExpenseClaimController.submitExpenseClaim()");
        try {
            ExpenseClaim expenseClaim = new ExpenseClaim();
            expenseClaim.setEmployee(employee);
            System.out.print("Enter claim description: ");
            expenseClaim.setClaimDesc(scanner.nextLine());
            System.out.print("Enter claim date (YYYY-MM-DD): ");
            expenseClaim.setClaimDate(LocalDate.parse(scanner.nextLine()));
            System.out.print("Enter document path: ");
            expenseClaim.setDocumentPath(scanner.nextLine());
            System.out.print("Enter number of claim items: ");
            int itemCount = readInt();
            List<ClaimItem> claimItems = new ArrayList<>();
            for (int i = 1; i <= itemCount; i++) {
                System.out.println("Claim Item " + i);
                ClaimItem item = new ClaimItem();
                System.out.print("Enter category ID: ");
                int categoryId = readInt();
                ExpenseCategory category = new ExpenseCategory();
                category.setCategoryId(categoryId);
                item.setCategory(category);
                System.out.print("Enter item description: ");
                item.setDescription(scanner.nextLine());
                System.out.print("Enter amount: ");
                double amount = readDouble();
                item.setAmount(amount);
                System.out.print("Enter expense date(YYYY-MM-DD): ");
                LocalDate expenseDate = LocalDate.parse(scanner.nextLine());
                item.setExpenseDate(expenseDate);
                claimItems.add(item);
            }
            ExpenseClaim savedClaim = expenseClaimService.submitExpenseClaim(expenseClaim, claimItems);
            System.out.println("\nExpense claim submitted successfully.");
            System.out.println("Claim ID: " + savedClaim.getClaimId());
            System.out.println("Total Amount: " + savedClaim.getClaimAmount());
        } catch (Exception e) {
            logger.error("ERROR at ExpenseClaimController.submitExpenseClaim()", e);
            System.out.println("Failed to submit expense claim: " + e.getMessage());
        }
        logger.info("Ending ExpenseClaimController.submitExpenseClaim()");
    }

    private void getExpenseClaimById() {
        logger.info("Started ExpenseClaimController.getExpenseClaimById()");
        try {
            System.out.print("Enter claim ID: ");
            int claimId = readInt();
            ExpenseClaim expenseClaim = expenseClaimService.getExpenseClaimById(claimId);
            if (expenseClaim == null) {
                System.out.println("Expense claim not found.");
                return;
            }
            displayExpenseClaim(expenseClaim);
        } catch (Exception e) {
            logger.error("ERROR at ExpenseClaimController.getExpenseClaimById()", e);
            System.out.println("Failed to get expense claim: " + e.getMessage());
        }
        logger.info("Ending ExpenseClaimController.getExpenseClaimById()");
    }

    private void reviewExpenseClaim(Employee manager) {
        logger.info("Started ExpenseClaimController.reviewExpenseClaim()");
        try {
            System.out.print("Enter claim ID: ");
            int claimId = readInt();
            System.out.print("Enter decision (APPROVED/REJECTED): ");
            String decision = scanner.nextLine().trim().toUpperCase();
            String reason = null;
            if (decision.equals("REJECTED")) {
                System.out.print("Enter rejection reason: ");
                reason = scanner.nextLine();
            }
            expenseClaimService.reviewExpenseClaim(claimId, manager.getEmployeeId(), decision, reason);
            System.out.println("Expense claim reviewed successfully.");
        } catch (ServiceException e) {
            logger.error("ERROR at ExpenseClaimController.reviewExpenseClaim()", e);
            System.out.println("Failed to review expense claim: " + e.getMessage());
        }
        logger.info("Ending ExpenseClaimController.reviewExpenseClaim()");
    }

    private void viewProfile(Employee employee) {
        logger.info("Started ExpenseClaimController.viewProfile()");
        try {
            System.out.println("\nMY PROFILE");
            System.out.println("Employee ID: " + employee.getEmployeeId());
            System.out.println("Full Name: " + employee.getFullName());
            System.out.println("Email: " + employee.getEmail());

            if (employee.getDepartment() != null) {
                Department department = departmentService.getDepartmentById(employee.getDepartment().getDepartmentId());
                System.out.println("Department: " + (department != null ? department.getDepartmentName() : "Unknown"));
            }

            if (employee.getUser() != null) {
                System.out.println("Username: " + employee.getUser().getUserName());
                System.out.println("Role: " + employee.getUser().getRole());
                System.out.println("Account Active: " + employee.getUser().isActive());
            }
        } catch (Exception e) {
            logger.error("ERROR at ExpenseClaimController.viewProfile()", e);
            System.out.println("Failed to load profile: " + e.getMessage());
        }
        logger.info("Ending ExpenseClaimController.viewProfile()");
    }

    private void viewClaims(List<ExpenseClaim> claims) {
        if (claims == null || claims.isEmpty()) {
            System.out.println("No expense claims found.");
            return;
        }
        for (ExpenseClaim claim : claims) {
            displayExpenseClaim(claim);
        }
    }

    private void displayExpenseClaim(ExpenseClaim expenseClaim) {
        System.out.println("\nEXPENSE CLAIM");
        System.out.println("Claim ID: " + expenseClaim.getClaimId());
        if (expenseClaim.getEmployee() != null) {
            System.out.println("Employee ID: " + expenseClaim.getEmployee().getEmployeeId());
        }
        System.out.println("Description: " + expenseClaim.getClaimDesc());
        System.out.println("Claim Date: " + expenseClaim.getClaimDate());
        System.out.println("Claim Amount: " + expenseClaim.getClaimAmount());
        System.out.println("Status: " + expenseClaim.getStatus());
        if (expenseClaim.getReason() != null) {
            System.out.println("Reason: " + expenseClaim.getReason());
        }
        System.out.println("Document Path: " + expenseClaim.getDocumentPath());
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer input");
            return -1;
        }
    }

    private double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            logger.warn("Invalid amount input");
            return -1;
        }
    }
}