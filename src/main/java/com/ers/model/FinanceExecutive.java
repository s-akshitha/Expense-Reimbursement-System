package com.ers.model;

public class FinanceExecutive {
    private Employee employee;
    private String fullName;
    private String email;
    private String department;

    public FinanceExecutive(Employee employee, String fullName, String email, String department) {
        this.employee = employee;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
    }

    public FinanceExecutive() {

    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "FinanceExecutives{" +
                "employeeId=" + employee.getEmployeeId() +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
