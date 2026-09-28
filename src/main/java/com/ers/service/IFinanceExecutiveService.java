package com.ers.service;

import com.ers.model.Employee;
import com.ers.model.ExpenseClaim;
import com.ers.model.FinanceExecutive;
import com.ers.model.Reimbursement;

import java.sql.Connection;
import java.util.List;

public interface IFinanceExecutiveService {
    //CRUD Operations
    FinanceExecutive addFinanceExecutive(FinanceExecutive financeExecutive);
    boolean updateFinanceExecutive(FinanceExecutive financeExecutive);
    FinanceExecutive getFinanceExecutiveById(Employee employee);
    List<FinanceExecutive> getAllFinanceExecutives();
    boolean deleteFinanceExecutiveById(Employee employee);
    List<ExpenseClaim> getApprovedClaims();
    ExpenseClaim getClaimById(int claimId);
}
