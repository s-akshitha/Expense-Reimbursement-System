package com.ers.model;

import java.time.LocalDate;

public class ClaimItem {
    private int itemId;
    private ExpenseClaim expenseClaim;
    private ExpenseCategory expenseCategory;
    private String description;
    private double amount;
    private LocalDate expenseDate;

    public ClaimItem(ExpenseClaim expenseClaim, ExpenseCategory expenseCategory, String description, double amount, LocalDate expenseDate) {
        this.expenseClaim= expenseClaim;
        this.expenseCategory=expenseCategory;
        this.description = description;
        this.amount = amount;
        this.expenseDate = expenseDate;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public ExpenseClaim getExpenseClaim() {
        return expenseClaim;
    }

    public void setExpenseClaim(ExpenseClaim expenseClaim) {
        this.expenseClaim=expenseClaim;
    }

    public ExpenseCategory getCategory() {
        return expenseCategory;
    }

    public void setCategoryId(ExpenseCategory expenseCategory) {
        this.expenseCategory=expenseCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    @Override
    public String toString() {
        return "ClaimItems{" +
                "itemId=" + itemId +
                ", claimId=" +  expenseClaim.getClaimId()+
                ", categoryId=" + expenseCategory.getCategoryId() +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", expenseDate=" + expenseDate +
                '}';
    }
}
