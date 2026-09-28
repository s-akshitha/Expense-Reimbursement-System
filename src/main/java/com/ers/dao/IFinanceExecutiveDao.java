package com.ers.dao;

import com.ers.model.Employee;
import com.ers.model.ExpenseClaim;
import com.ers.model.FinanceExecutive;
import com.ers.model.Reimbursement;

import java.sql.Connection;
import java.util.List;

public interface IFinanceExecutiveDao {
    //CRUD Operations
    FinanceExecutive addFinanceExecutive(Connection connection,FinanceExecutive financeExecutive);
    boolean updateFinanceExecutive(FinanceExecutive financeExecutive);
    FinanceExecutive getFinanceExecutiveById(Employee employee);
    List<FinanceExecutive> getAllFinanceExecutives();
    boolean deleteFinanceExecutiveById(Employee employee);
    List<ExpenseClaim> getApprovedClaims(Connection connection);
    ExpenseClaim getClaimById(Connection connection, int claimId);
}
