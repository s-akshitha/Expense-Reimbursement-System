package com.ers.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtil {
    public Connection getConnection(){
        try {
           return DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_reimbursment_sys","root","root");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: "+e.getMessage());
            return null;
        }
    }
}
