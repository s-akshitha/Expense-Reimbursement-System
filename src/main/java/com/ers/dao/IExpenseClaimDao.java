package com.ers.dao;

import com.ers.model.ExpenseClaim;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IExpenseClaimDao {
      ExpenseClaim addExpenseClaim(ExpenseClaim expenseClaim);
      ExpenseClaim insertClaim(Connection connection, ExpenseClaim claim) throws SQLException;
      boolean reviewExpenseClaim(Connection connection,int claimId,String status,String reason);
      ExpenseClaim getExpenseClaimById(int claimId);
      ExpenseClaim getExpenseClaimById(Connection connection, int claimId);
      List<ExpenseClaim> getAllExpenseClaims();
      boolean deleteExpenseClaimById(int claimId);
      List<ExpenseClaim> getClaimsByEmployeeId(int employeeId);
      List<ExpenseClaim> getClaimsByStatus(String status);
}
