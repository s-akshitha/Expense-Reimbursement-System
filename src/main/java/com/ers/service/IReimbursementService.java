package com.ers.service;

import com.ers.model.Reimbursement;

import java.util.List;

public interface IReimbursementService {
    boolean updateReimbursement(Reimbursement reimbursement);
    Reimbursement getReimbursementById(int reimbursementId);
    List<Reimbursement> getAllReimbursements();
    boolean deleteReimbursementById(int reimbursementId);
    Reimbursement getReimbursementByClaimId(int claimId);
    List<Reimbursement> getReimbursementsByEmployeeId(int employeeId);
    List<Reimbursement> getReimbursementsByStatus(String status);
    Reimbursement processReimbursement(int claimId, int financeEmployeeId, String paymentMode, String transactionRef);
}
