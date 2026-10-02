package com.ers.service;

import ch.qos.logback.classic.Logger;
import com.ers.dao.*;
import com.ers.exception.ServiceException;
import com.ers.model.Employee;
import com.ers.model.ExpenseClaim;
import com.ers.model.FinanceExecutive;
import com.ers.model.Reimbursement;
import com.ers.util.JDBCUtil;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReimbursementServiceImpl implements IReimbursementService{

    private final JDBCUtil jdbcUtil;
    private final IReimbursementDao reimbursementDao;
    private final IExpenseClaimDao expenseClaimDao;
    private final IFinanceExecutiveDao financeExecutiveDao;

    public ReimbursementServiceImpl() {
        this.jdbcUtil = new JDBCUtil();
        this.reimbursementDao = new ReimbursementDaoImpl();
        this.expenseClaimDao = new ExpenseClaimDaoImpl();
        this.financeExecutiveDao = new FinanceExecutiveDaoImpl();
    }

    public ReimbursementServiceImpl(JDBCUtil jdbcUtil, IReimbursementDao reimbursementDao, IExpenseClaimDao expenseClaimDao, IFinanceExecutiveDao financeExecutiveDao) {
        this.jdbcUtil = jdbcUtil;
        this.reimbursementDao = reimbursementDao;
        this.expenseClaimDao = expenseClaimDao;
        this.financeExecutiveDao = financeExecutiveDao;
    }
    private static final Logger logger=(Logger) LoggerFactory.getLogger(ReimbursementServiceImpl.class);

    @Override
    public Reimbursement processReimbursement(int claimId, int financeEmployeeId, String paymentMode, String transactionRef) {
        logger.info("Started ReimbursementServiceImpl.processReimbursement(), claimId={}, financeEmployeeId={}", claimId, financeEmployeeId);
        if (paymentMode == null || paymentMode.trim().isEmpty()) {
            throw new ServiceException("Payment mode is required");
        }
        Reimbursement existing = reimbursementDao.getReimbursementByClaimId(claimId);
        if (existing != null) {
            throw new ServiceException("Claim has already been reimbursed");
        }
        Employee financeEmployee = new Employee();
        financeEmployee.setEmployeeId(financeEmployeeId);
        FinanceExecutive financeExecutive = financeExecutiveDao.getFinanceExecutiveById(financeEmployee);
        if (financeExecutive == null) {
            throw new ServiceException("Only a finance executive can process a reimbursement");
        }
        Connection connection = null;
        try {
            connection = jdbcUtil.getConnection();
            connection.setAutoCommit(false);
            logger.info("TRANSACTION START");

            ExpenseClaim claim = expenseClaimDao.getExpenseClaimById(connection, claimId);
            if (claim == null) {
                throw new ServiceException("Expense claim not found");
            }
            if (!"APPROVED".equals(claim.getStatus())) {
                throw new ServiceException("Only APPROVED claims can be reimbursed");
            }
            Reimbursement reimbursement = new Reimbursement();
            reimbursement.setClaimId(claimId);
            reimbursement.setReimbursedAmount(claim.getClaimAmount());
            reimbursement.setPaymentMode(paymentMode);
            reimbursement.setTransactionRef(transactionRef);
            reimbursement.setReimbursementDate(LocalDate.now());
            reimbursement.setProcessedBy(financeEmployeeId);
            reimbursement.setStatus("COMPLETED");

            Reimbursement saved = reimbursementDao.insertReimbursement(connection, reimbursement);

            boolean claimUpdated = expenseClaimDao.reviewExpenseClaim(connection, claimId, "REIMBURSED", null);
            if (!claimUpdated) {
                throw new ServiceException("Failed to update claim status to REIMBURSED");
            }

            connection.commit();
            logger.info("TRANSACTION COMMIT");
            logger.info("Ending ReimbursementServiceImpl.processReimbursement(), reimbursementId={}", saved.getReimbursementId());
            return saved;
        } catch (ServiceException e) {
            logger.error("ERROR at ReimbursementServiceImpl.processReimbursement()", e);
            rollbackQuietly(connection);
            throw e;
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementServiceImpl.processReimbursement()", e);
            rollbackQuietly(connection);
            throw new ServiceException("Unable to process reimbursement", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    logger.info("Database connection closed");
                } catch (SQLException e) {
                    logger.error("ERROR at Closing connection", e);
                }
            }
        }
    }

    @Override
    public boolean updateReimbursement(Reimbursement reimbursement) {
        logger.info("Started ReimbursementServiceImpl.updateReimbursement()");
        try {
            boolean updated = reimbursementDao.updateReimbursement(reimbursement);
            logger.info("Reimbursement update status={}", updated);
            return updated;
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementServiceImpl.updateReimbursement()", e);
            throw new ServiceException("Unable to update reimbursement", e);
        }
    }

    @Override
    public Reimbursement getReimbursementById(int reimbursementId) {
        logger.info("Started ReimbursementServiceImpl.getReimbursementById()");
        try {
            return reimbursementDao.getReimbursementById(reimbursementId);
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementServiceImpl.getReimbursementById()", e);
            throw new ServiceException("Unable to get reimbursement", e);
        }
    }

    @Override
    public List<Reimbursement> getAllReimbursements() {
        logger.info("Started ReimbursementServiceImpl.getAllReimbursements()");
        try {
            return reimbursementDao.getAllReimbursements();
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementServiceImpl.getAllReimbursements()", e);
            throw new ServiceException("Unable to get reimbursements", e);
        }
    }

    @Override
    public boolean deleteReimbursementById(int reimbursementId) {
        logger.info("Started ReimbursementServiceImpl.deleteReimbursementById()");
        try {
            boolean deleted = reimbursementDao.deleteReimbursementById(reimbursementId);
            logger.info("Reimbursement deletion status={}", deleted);
            return deleted;
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementServiceImpl.deleteReimbursementById()", e);
            throw new ServiceException("Unable to delete reimbursement", e);
        }
    }

    @Override
    public Reimbursement getReimbursementByClaimId(int claimId) {
        logger.info("Started ReimbursementServiceImpl.getReimbursementByClaimId()");
        try {
            return reimbursementDao.getReimbursementByClaimId(claimId);
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementServiceImpl.getReimbursementByClaimId()", e);
            throw new ServiceException("Unable to get reimbursement for claim", e);
        }
    }

    @Override
    public List<Reimbursement> getReimbursementsByEmployeeId(int employeeId) {
        logger.info("Started ReimbursementServiceImpl.getReimbursementsByEmployeeId()");
        try {
            return reimbursementDao.getReimbursementsByEmployeeId(employeeId);
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementServiceImpl.getReimbursementsByEmployeeId()", e);
            throw new ServiceException("Unable to get reimbursements for employee", e);
        }
    }

    @Override
    public List<Reimbursement> getReimbursementsByStatus(String status) {
        logger.info("Started ReimbursementServiceImpl.getReimbursementsByStatus()");
        try {
            return reimbursementDao.getReimbursementsByStatus(status);
        } catch (Exception e) {
            logger.error("ERROR at ReimbursementServiceImpl.getReimbursementsByStatus()", e);
            throw new ServiceException("Unable to get reimbursements by status", e);
        }
    }

    private void rollbackQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
            logger.warn("TRANSACTION ROLLBACK");
        } catch (SQLException e) {
            logger.error("ERROR at Rollback failed", e);
        }
    }
}
