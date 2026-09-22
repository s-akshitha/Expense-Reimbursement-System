package com.ers.model;

import java.time.LocalDate;

public class ExpenseClaim {
    private int claimId;
    private Employee employee;
    private String claimDesc;
    private double claimAmount;
    private LocalDate claimDate;
    private String status;
    private String reason;
    private String documentPath;

    public ExpenseClaim(){
        this.employee = new Employee();
    }

    public ExpenseClaim(int employeeId, String claimDesc, double claimAmount, LocalDate claimDate, String status, String reason, String documentPath) {
        this.employee.setEmployeeId(employeeId);
        this.claimDesc = claimDesc;
        this.claimAmount = claimAmount;
        this.claimDate = claimDate;
        this.status = status;
        this.reason = reason;
        this.documentPath = documentPath;
    }

    public int getClaimId() {
        return claimId;
    }

    public void setClaimId(int claimId) {
        this.claimId = claimId;
    }

    public int getEmployeeId() {
        return employee.getEmployeeId();
    }

    public void setEmployeeId(int employeeId) {
        employee.setEmployeeId(employeeId);
    }

    public String getClaimDesc() {
        return claimDesc;
    }

    public void setClaimDesc(String claimDesc) {
        this.claimDesc = claimDesc;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    @Override
    public String toString() {
        return "ExpenseClaim{" +
                "claimId=" + claimId +
                ", employeeId=" + employee.getEmployeeId() +
                ", claimDesc='" + claimDesc + '\'' +
                ", claimAmount=" + claimAmount +
                ", claimDate=" + claimDate +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                ", documentPath='" + documentPath + '\'' +
                '}';
    }
}
