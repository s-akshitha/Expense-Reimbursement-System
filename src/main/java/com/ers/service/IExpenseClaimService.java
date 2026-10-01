package com.ers.service;

import com.ers.model.ClaimItem;
import com.ers.model.ExpenseClaim;

import java.util.List;

public interface IExpenseClaimService {
    ExpenseClaim addExpenseClaim(ExpenseClaim expenseClaim);
    ExpenseClaim submitExpenseClaim(ExpenseClaim expenseClaim, List<ClaimItem> items);
    ExpenseClaim getExpenseClaimById(int claimId);
    void reviewExpenseClaim(int claimId, int managerId, String decision, String reason);
    List<ExpenseClaim> getAllExpenseClaims();
    List<ExpenseClaim> getClaimsByEmployeeId(int employeeId);
    List<ExpenseClaim> getClaimsByStatus(String status);
    List<ExpenseClaim> getPendingClaimsForManager(int managerId);
}
