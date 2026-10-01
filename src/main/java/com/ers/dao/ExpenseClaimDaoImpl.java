package com.ers.dao;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.controller.AppController;
import com.ers.exception.DaoException;
import com.ers.model.Employee;
import com.ers.model.ExpenseClaim;
import com.ers.service.EmployeeServiceImpl;
import com.ers.util.JDBCUtil;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseClaimDaoImpl implements IExpenseClaimDao{

    JDBCUtil jdbcUtil;
    public ExpenseClaimDaoImpl(){
        this.jdbcUtil=new JDBCUtil();
    }
    private static final Logger logger=(Logger)LoggerFactory.getLogger(ExpenseClaimDaoImpl.class);

    private static final String insertQuery = "insert into expense_claims (employee_id, claim_description, claim_date, claim_amount, status, document_path, reason) values(?, ?, ?, ?, ?, ?, ?)";
    private static final String selectByIdQuery = "select claim_id, employee_id, claim_description, claim_date, claim_amount, status, document_path, reason from expense_claims where claim_id=?";
    private static final String selectAllQuery = "select claim_id, employee_id, claim_description, claim_date, claim_amount, status, document_path, reason from expense_claims order by claim_date desc";
    private static final String selectByEmployeeIdQuery = "select claim_id, employee_id, claim_description, claim_date, claim_amount, status, document_path, reason from expense_claims where employee_id=? order by claim_date desc";
    private static final String selectByStatusQuery = "select claim_id, employee_id, claim_description, claim_date, claim_amount, status, document_path, reason from expense_claims where status=? order by claim_date";
    private static final String deleteQuery = "delete from expense_claims where claim_id=?";
    private static final String reviewClaimQuery = "update expense_claims set status = ?, reason = ? where claim_id = ?";

    @Override
    public ExpenseClaim addExpenseClaim(ExpenseClaim expenseClaim) {
        logger.info("Started ExpenseClaimDaoImpl.addExpenseClaim()");
        try (Connection connection = jdbcUtil.getConnection()) {
            ExpenseClaim result = insertClaim(connection, expenseClaim);
            logger.info("Ending ExpenseClaimDaoImpl.addExpenseClaim()");
            return result;
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseClaimDaoImpl.addExpenseClaim()", e);
            throw new DaoException("Unable to add expense claim", e);
        }
    }

    @Override
    public ExpenseClaim insertClaim(Connection connection, ExpenseClaim claim) throws SQLException {
        logger.info("Started ExpenseClaimDaoImpl.insertClaim()");
        try (PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, claim.getEmployee().getEmployeeId());
            ps.setString(2, claim.getClaimDesc());
            ps.setDate(3, Date.valueOf(claim.getClaimDate()));
            ps.setDouble(4, claim.getClaimAmount());
            ps.setString(5, claim.getStatus());
            ps.setString(6, claim.getDocumentPath());
            ps.setString(7, claim.getReason());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    claim.setClaimId(rs.getInt(1));
                }
            }
            logger.info("Ending ExpenseClaimDaoImpl.insertClaim(), claimId={}", claim.getClaimId());
            return claim;
        }
    }

    @Override
    public boolean reviewExpenseClaim(Connection connection, int claimId, String status, String reason) {
        logger.info("Started ExpenseClaimDaoImpl.reviewExpenseClaim()");
        try (PreparedStatement ps = connection.prepareStatement(reviewClaimQuery)) {
            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setInt(3, claimId);
            int rows = ps.executeUpdate();
            logger.info("Ending ExpenseClaimDaoImpl.reviewExpenseClaim(), rows={}", rows);
            return rows > 0;
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseClaimDaoImpl.reviewExpenseClaim()", e);
            throw new DaoException("Unable to review expense claim", e);
        }
    }

    @Override
    public ExpenseClaim getExpenseClaimById(int claimId) {
        logger.info("Started ExpenseClaimDaoImpl.getExpenseClaimById()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectByIdQuery)) {
            ps.setInt(1, claimId);
            try (ResultSet rs = ps.executeQuery()) {
                ExpenseClaim claim = rs.next() ? mapClaim(rs) : null;
                logger.info("Ending ExpenseClaimDaoImpl.getExpenseClaimById()");
                return claim;
            }
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseClaimDaoImpl.getExpenseClaimById()", e);
            throw new DaoException("Unable to retrieve expense claim", e);
        }
    }

    @Override
    public ExpenseClaim getExpenseClaimById(Connection connection, int claimId) {
        logger.info("Started ExpenseClaimDaoImpl.getExpenseClaimById(Connection)");
        try (PreparedStatement ps = connection.prepareStatement(selectByIdQuery)) {
            ps.setInt(1, claimId);
            try (ResultSet rs = ps.executeQuery()) {
                ExpenseClaim claim = rs.next() ? mapClaim(rs) : null;
                logger.info("Ending ExpenseClaimDaoImpl.getExpenseClaimById(Connection)");
                return claim;
            }
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseClaimDaoImpl.getExpenseClaimById(Connection)", e);
            throw new DaoException("Unable to retrieve expense claim", e);
        }
    }

    @Override
    public List<ExpenseClaim> getAllExpenseClaims() {
        logger.info("Started ExpenseClaimDaoImpl.getAllExpenseClaims()");
        List<ExpenseClaim> claims = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectAllQuery); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                claims.add(mapClaim(rs));
            }
            logger.info("Ending ExpenseClaimDaoImpl.getAllExpenseClaims()");
            return claims;
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseClaimDaoImpl.getAllExpenseClaims()", e);
            throw new DaoException("Unable to retrieve expense claims", e);
        }
    }

    @Override
    public boolean deleteExpenseClaimById(int claimId) {
        logger.info("Started ExpenseClaimDaoImpl.deleteExpenseClaimById()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setInt(1, claimId);
            boolean deleted = ps.executeUpdate() > 0;
            logger.info("Ending ExpenseClaimDaoImpl.deleteExpenseClaimById()");
            return deleted;
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseClaimDaoImpl.deleteExpenseClaimById()", e);
            throw new DaoException("Unable to delete expense claim", e);
        }
    }

    @Override
    public List<ExpenseClaim> getClaimsByEmployeeId(int employeeId) {
        logger.info("Started ExpenseClaimDaoImpl.getClaimsByEmployeeId()");
        List<ExpenseClaim> claims = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectByEmployeeIdQuery)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    claims.add(mapClaim(rs));
                }
            }
            logger.info("Ending ExpenseClaimDaoImpl.getClaimsByEmployeeId()");
            return claims;
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseClaimDaoImpl.getClaimsByEmployeeId()", e);
            throw new DaoException("Unable to retrieve expense claims for employee", e);
        }
    }

    @Override
    public List<ExpenseClaim> getClaimsByStatus(String status) {
        logger.info("Started ExpenseClaimDaoImpl.getClaimsByStatus()");
        List<ExpenseClaim> claims = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectByStatusQuery)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    claims.add(mapClaim(rs));
                }
            }
            logger.info("Ending ExpenseClaimDaoImpl.getClaimsByStatus()");
            return claims;
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseClaimDaoImpl.getClaimsByStatus()", e);
            throw new DaoException("Unable to retrieve expense claims by status", e);
        }
    }

    private ExpenseClaim mapClaim(ResultSet rs) throws SQLException {
        ExpenseClaim claim = new ExpenseClaim();
        claim.setClaimId(rs.getInt("claim_id"));
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        claim.setEmployee(employee);
        claim.setClaimDesc(rs.getString("claim_description"));
        Date claimDate = rs.getDate("claim_date");
        if (claimDate != null) {
            claim.setClaimDate(claimDate.toLocalDate());
        }
        claim.setClaimAmount(rs.getDouble("claim_amount"));
        claim.setStatus(rs.getString("status"));
        claim.setDocumentPath(rs.getString("document_path"));
        claim.setReason(rs.getString("reason"));
        return claim;
    }
}
