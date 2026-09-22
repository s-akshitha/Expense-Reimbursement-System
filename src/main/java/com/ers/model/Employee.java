package com.ers.model;

public class Employee {

    private int employeeId;
    private User user;
    private String fullName;
    private String email;
    private Department department;

    public Employee(){

    }

    public Employee(User user, String fullName, String email, Department department) {
        this.user = user;
        this.fullName = fullName;
        this.email = email;
        this.department=department;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", userId=" + user.getUserId() +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", department=" + department +
                '}';
    }
}
