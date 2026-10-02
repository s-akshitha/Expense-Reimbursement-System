package com.ers.dao;

import ch.qos.logback.classic.Logger;
import com.ers.exception.DaoException;
import com.ers.model.Reimbursement;
import com.ers.util.JDBCUtil;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//CRUD Operations
public class ReimbursementDaoImpl implements IReimbursementDao{
    private JDBCUtil jdbcUtil;
    public ReimbursementDaoImpl(JDBCUtil jdbcUtil){
        this.jdbcUtil = jdbcUtil;
    }
    public ReimbursementDaoImpl() {
        this.jdbcUtil = new JDBCUtil();
    }
    private static final Logger logger=(Logger) LoggerFactory.getLogger(ReimbursementDaoImpl.class);
    private static final String insertQuery = "insert into reimbursements (claim_id, reimbursed_amount, payment_mode, transaction_ref, reimbursement_date, processed_by, status) values (?,?,?,?,?,?,?)";
    private static final String updateQuery = "update reimbursements set reimbursed_amount=?, payment_mode=?, transaction_ref=?, reimbursement_date=?, status=? where reimbursement_id=?";
    private static final String selectByIdQuery = "select reimbursement_id, claim_id, reimbursed_amount, payment_mode, transaction_ref, reimbursement_date, processed_by, status from reimbursements where reimbursement_id=?";
    private static final String selectAllQuery = "select reimbursement_id, claim_id, reimbursed_amount, payment_mode, transaction_ref, reimbursement_date, processed_by, status from reimbursements";
    private static final String deleteQuery = "delete from reimbursements where reimbursement_id=?";
    private static final String selectByClaimIdQuery = "select reimbursement_id, claim_id, reimbursed_amount, payment_mode, transaction_ref, reimbursement_date, processed_by, status from reimbursements where claim_id=?";
    private static final String selectByEmployeeIdQuery = "select r.reimbursement_id, r.claim_id, r.reimbursed_amount, r.payment_mode, r.transaction_ref, r.reimbursement_date, r.processed_by, r.status " +
            "from reimbursements r join expense_claims c on r.claim_id = c.claim_id where c.employee_id=? order by r.reimbursement_date desc";
    private static final String selectByStatusQuery = "select reimbursement_id, claim_id, reimbursed_amount, payment_mode, transaction_ref, reimbursement_date, processed_by, status from reimbursements where status=?";

    @Override
    public Reimbursement addReimbursement(Reimbursement reimbursement) {
        logger.info("Started ReimbursementDaoImpl.addReimbursement()");
        try (Connection connection = jdbcUtil.getConnection()) {
            Reimbursement result = insertReimbursement(connection, reimbursement);
            logger.info("Ending ReimbursementDaoImpl.addReimbursement()");
            return result;
        } catch (SQLException e) {
            logger.error("ERROR at ReimbursementDaoImpl.addReimbursement()", e);
            throw new DaoException("Unable to add reimbursement", e);
        }
    }

    @Override
    public Reimbursement insertReimbursement(Connection connection, Reimbursement reimbursement) throws SQLException {
        logger.info("Started ReimbursementDaoImpl.insertReimbursement()");
        try (PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reimbursement.getClaimId());
            ps.setDouble(2, reimbursement.getReimbursedAmount());
            ps.setString(3, reimbursement.getPaymentMode());
            ps.setString(4, reimbursement.getTransactionRef());
            ps.setDate(5, reimbursement.getReimbursementDate() != null ? Date.valueOf(reimbursement.getReimbursementDate()) : null);
            ps.setInt(6, reimbursement.getProcessedBy());
            ps.setString(7, reimbursement.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    reimbursement.setReimbursementId(rs.getInt(1));
                }
            }
            logger.info("Ending ReimbursementDaoImpl.insertReimbursement(), reimbursementId={}", reimbursement.getReimbursementId());
            return reimbursement;
        }
    }

    @Override
    public boolean updateReimbursement(Reimbursement reimbursement) {
        logger.info("Started ReimbursementDaoImpl.updateReimbursement()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setDouble(1, reimbursement.getReimbursedAmount());
            ps.setString(2, reimbursement.getPaymentMode());
            ps.setString(3, reimbursement.getTransactionRef());
            ps.setDate(4, reimbursement.getReimbursementDate() != null ? Date.valueOf(reimbursement.getReimbursementDate()) : null);
            ps.setString(5, reimbursement.getStatus());
            ps.setInt(6, reimbursement.getReimbursementId());
            boolean updated = ps.executeUpdate() > 0;
            logger.info("Ending ReimbursementDaoImpl.updateReimbursement()");
            return updated;
        } catch (SQLException e) {
            logger.error("ERROR at ReimbursementDaoImpl.updateReimbursement()", e);
            throw new DaoException("Unable to update reimbursement", e);
        }
    }

    @Override
    public Reimbursement getReimbursementById(int reimbursementId) {
        logger.info("Started ReimbursementDaoImpl.getReimbursementById()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectByIdQuery)) {
            ps.setInt(1, reimbursementId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapReimbursement(rs) : null;
            }
        } catch (SQLException e) {
            logger.error("ERROR at ReimbursementDaoImpl.getReimbursementById()", e);
            throw new DaoException("Unable to retrieve reimbursement", e);
        }
    }

    @Override
    public List<Reimbursement> getAllReimbursements() {
        logger.info("Started ReimbursementDaoImpl.getAllReimbursements()");
        List<Reimbursement> reimbursements = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectAllQuery); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reimbursements.add(mapReimbursement(rs));
            }
            logger.info("Ending ReimbursementDaoImpl.getAllReimbursements()");
            return reimbursements;
        } catch (SQLException e) {
            logger.error("ERROR at ReimbursementDaoImpl.getAllReimbursements()", e);
            throw new DaoException("Unable to retrieve reimbursements", e);
        }
    }

    @Override
    public boolean deleteReimbursementById(int reimbursementId) {
        logger.info("Started ReimbursementDaoImpl.deleteReimbursementById()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setInt(1, reimbursementId);
            boolean deleted = ps.executeUpdate() > 0;
            logger.info("Ending ReimbursementDaoImpl.deleteReimbursementById()");
            return deleted;
        } catch (SQLException e) {
            logger.error("ERROR at ReimbursementDaoImpl.deleteReimbursementById()", e);
            throw new DaoException("Unable to delete reimbursement", e);
        }
    }

    @Override
    public Reimbursement getReimbursementByClaimId(int claimId) {
        logger.info("Started ReimbursementDaoImpl.getReimbursementByClaimId()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectByClaimIdQuery)) {
            ps.setInt(1, claimId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapReimbursement(rs) : null;
            }
        } catch (SQLException e) {
            logger.error("ERROR at ReimbursementDaoImpl.getReimbursementByClaimId()", e);
            throw new DaoException("Unable to retrieve reimbursement for claim", e);
        }
    }

    @Override
    public List<Reimbursement> getReimbursementsByEmployeeId(int employeeId) {
        logger.info("Started ReimbursementDaoImpl.getReimbursementsByEmployeeId()");
        List<Reimbursement> reimbursements = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectByEmployeeIdQuery)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reimbursements.add(mapReimbursement(rs));
                }
            }
            logger.info("Ending ReimbursementDaoImpl.getReimbursementsByEmployeeId()");
            return reimbursements;
        } catch (SQLException e) {
            logger.error("ERROR at ReimbursementDaoImpl.getReimbursementsByEmployeeId()", e);
            throw new DaoException("Unable to retrieve reimbursements for employee", e);
        }
    }

    @Override
    public List<Reimbursement> getReimbursementsByStatus(String status) {
        logger.info("Started ReimbursementDaoImpl.getReimbursementsByStatus()");
        List<Reimbursement> reimbursements = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectByStatusQuery)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reimbursements.add(mapReimbursement(rs));
                }
            }
            logger.info("Ending ReimbursementDaoImpl.getReimbursementsByStatus()");
            return reimbursements;
        } catch (SQLException e) {
            logger.error("ERROR at ReimbursementDaoImpl.getReimbursementsByStatus()", e);
            throw new DaoException("Unable to retrieve reimbursements by status", e);
        }
    }

    private Reimbursement mapReimbursement(ResultSet rs) throws SQLException {
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setReimbursementId(rs.getInt("reimbursement_id"));
        reimbursement.setClaimId(rs.getInt("claim_id"));
        reimbursement.setReimbursedAmount(rs.getDouble("reimbursed_amount"));
        reimbursement.setPaymentMode(rs.getString("payment_mode"));
        reimbursement.setTransactionRef(rs.getString("transaction_ref"));
        Date reimbursementDate = rs.getDate("reimbursement_date");
        if (reimbursementDate != null) {
            reimbursement.setReimbursementDate(reimbursementDate.toLocalDate());
        }
        reimbursement.setProcessedBy(rs.getInt("processed_by"));
        reimbursement.setStatus(rs.getString("status"));
        return reimbursement;
    }


}
