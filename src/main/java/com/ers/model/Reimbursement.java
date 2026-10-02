package com.ers.model;

import java.time.LocalDate;

public class Reimbursement {
    private int reimbursementId;
    private int claimId;
    private double reimbursedAmount;
    private String paymentMode;
    private String transactionRef;
    private LocalDate reimbursementDate;
    private int processedBy;
    private String status;

    public Reimbursement(int claimId, double reimbursedAmount, String paymentMode, String transactionRef, LocalDate reimbursementDate, int processedBy, String status) {
        this.claimId = claimId;
        this.reimbursedAmount = reimbursedAmount;
        this.paymentMode = paymentMode;
        this.transactionRef = transactionRef;
        this.reimbursementDate = reimbursementDate;
        this.processedBy = processedBy;
        this.status = status;
    }

    public Reimbursement() {

    }

    public int getReimbursementId() {
        return reimbursementId;
    }

    public void setReimbursementId(int reimbursementId) {
        this.reimbursementId = reimbursementId;
    }

    public int getClaimId() {
        return claimId;
    }

    public void setClaimId(int claimId) {
        this.claimId = claimId;
    }

    public double getReimbursedAmount() {
        return reimbursedAmount;
    }

    public void setReimbursedAmount(double reimbursedAmount) {
        this.reimbursedAmount = reimbursedAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public LocalDate getReimbursementDate() {
        return reimbursementDate;
    }

    public void setReimbursementDate(LocalDate reimbursementDate) {
        this.reimbursementDate = reimbursementDate;
    }

    public int getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(int processedBy) {
        this.processedBy = processedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reimbursements{" +
                "reimbursementId=" + reimbursementId +
                ", claimId=" + claimId +
                ", reimbursedAmount=" + reimbursedAmount +
                ", paymentMode='" + paymentMode + '\'' +
                ", transactionRef='" + transactionRef + '\'' +
                ", reimbursementDate=" + reimbursementDate +
                ", processedBy=" + processedBy +
                ", status='" + status + '\'' +
                '}';
    }
}
