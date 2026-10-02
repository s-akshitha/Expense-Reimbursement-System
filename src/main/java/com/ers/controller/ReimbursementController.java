package com.ers.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.model.Employee;
import com.ers.model.ExpenseClaim;
import com.ers.model.Reimbursement;
import com.ers.service.FinanceExecutiveServiceImpl;
import com.ers.service.IFinanceExecutiveService;
import com.ers.service.IReimbursementService;
import com.ers.service.ReimbursementServiceImpl;

import java.util.List;
import java.util.Scanner;

public class ReimbursementController {
    private final Scanner scanner;
    private final IFinanceExecutiveService financeExecutiveService;
    private final IReimbursementService reimbursementService;
    private static final Logger logger;
    static {
        LoggerContext context=new LoggerContext();
        logger=context.getLogger(ReimbursementController.class.getName());
    }

    public ReimbursementController() {
        this.scanner = new Scanner(System.in);
        this.financeExecutiveService = new FinanceExecutiveServiceImpl();
        this.reimbursementService = new ReimbursementServiceImpl();
    }

    public void showMenu(Employee financeEmployee) {
        logger.info("Started ReimbursementController.showMenu()");
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("FINANCE - REIMBURSEMENTS\n1. View Approved Claims\n2. Process Reimbursement\n3. View Reimbursement By Claim ID\n4. Back\nEnter your choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: viewApprovedClaims(); break;
                case 2: processReimbursement(financeEmployee); break;
                case 3: viewReimbursementByClaimId(); break;
                case 4: running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
        logger.info("Ending ReimbursementController.showMenu()");
    }

    private void viewApprovedClaims() {
        try {
            List<ExpenseClaim> claims = financeExecutiveService.getApprovedClaims();
            if (claims.isEmpty()) {
                System.out.println("No approved claims awaiting reimbursement.");
                return;
            }
            for (ExpenseClaim claim : claims) {
                System.out.println("\nClaim ID: " + claim.getClaimId() + " | Employee ID: " + claim.getEmployee().getEmployeeId() + " | Amount: " + claim.getClaimAmount() + " | Date: " + claim.getClaimDate());
            }
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementController.viewApprovedClaims()", e);
            System.out.println("Failed to view approved claims: " + e.getMessage());
        }
    }

    private void processReimbursement(Employee financeEmployee) {
        try {
            System.out.print("Enter claim ID to reimburse: ");
            int claimId = readInt();
            System.out.print("Enter payment mode (BANK_TRANSFER/CASH/UPI/CHEQUE): ");
            String paymentMode = scanner.nextLine().trim().toUpperCase();
            System.out.print("Enter transaction reference: ");
            String transactionRef = scanner.nextLine();

            Reimbursement reimbursement = reimbursementService.processReimbursement(
                    claimId, financeEmployee.getEmployeeId(), paymentMode, transactionRef);

            System.out.println("\nReimbursement processed successfully.");
            System.out.println("Reimbursement ID: " + reimbursement.getReimbursementId());
            System.out.println("Amount: " + reimbursement.getReimbursedAmount());
            System.out.println("Status: " + reimbursement.getStatus());
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementController.processReimbursement()", e);
            System.out.println("Failed to process reimbursement: " + e.getMessage());
        }
    }

    private void viewReimbursementByClaimId() {
        try {
            System.out.print("Enter claim ID: ");
            int claimId = readInt();
            Reimbursement reimbursement = reimbursementService.getReimbursementByClaimId(claimId);
            if (reimbursement == null) {
                System.out.println("No reimbursement found for this claim.");
                return;
            }
            System.out.println("\nREIMBURSEMENT");
            System.out.println("Reimbursement ID: " + reimbursement.getReimbursementId());
            System.out.println("Claim ID: " + reimbursement.getClaimId());
            System.out.println("Amount: " + reimbursement.getReimbursedAmount());
            System.out.println("Payment Mode: " + reimbursement.getPaymentMode());
            System.out.println("Transaction Ref: " + reimbursement.getTransactionRef());
            System.out.println("Date: " + reimbursement.getReimbursementDate());
            System.out.println("Status: " + reimbursement.getStatus());
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementController.viewReimbursementByClaimId()", e);
            System.out.println("Failed to get reimbursement: " + e.getMessage());
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