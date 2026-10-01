package com.ers.service;

import ch.qos.logback.classic.Logger;
import com.ers.dao.*;
import com.ers.exception.ServiceException;
import com.ers.model.ClaimItem;
import com.ers.model.Employee;
import com.ers.model.ExpenseClaim;
import com.ers.model.Role;
import com.ers.util.JDBCUtil;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseClaimServiceImpl implements IExpenseClaimService {
    private final JDBCUtil jdbcUtil;
    private final IExpenseClaimDao expenseClaimDao;
    private final IClaimItemDao claimItemDao;
    private final IEmployeeDao employeeDao;
    private final IDepartmentDao departmentDao;

    public ExpenseClaimServiceImpl() {
        this.jdbcUtil = new JDBCUtil();
        this.expenseClaimDao = new ExpenseClaimDaoImpl();
        this.claimItemDao = new ClaimItemDaoImpl();
        this.employeeDao = new EmployeeDaoImpl();
        this.departmentDao = new DepartmentDaoImpl();
    }

    public ExpenseClaimServiceImpl(JDBCUtil jdbcUtil, IExpenseClaimDao expenseClaimDao, IClaimItemDao claimItemDao, IEmployeeDao employeeDao, IDepartmentDao departmentDao) {
        this.jdbcUtil = jdbcUtil;
        this.expenseClaimDao = expenseClaimDao;
        this.claimItemDao = claimItemDao;
        this.employeeDao = employeeDao;
        this.departmentDao = departmentDao;
    }
    private static final Logger logger=(Logger) LoggerFactory.getLogger(ExpenseClaimServiceImpl.class);
    @Override
    public ExpenseClaim addExpenseClaim(ExpenseClaim expenseClaim) {
        logger.info("Started ExpenseClaimServiceImpl.addExpenseClaim()");
        try {
            if (expenseClaim.getStatus() == null) {
                expenseClaim.setStatus("DRAFT");
            }
            ExpenseClaim saved = expenseClaimDao.addExpenseClaim(expenseClaim);
            logger.info("Ending ExpenseClaimServiceImpl.addExpenseClaim(), claimId={}", saved.getClaimId());
            return saved;
        } catch (Exception e) {
            logger.error("ERROR at ExpenseClaimServiceImpl.addExpenseClaim()", e);
            throw new ServiceException("Unable to add expense claim", e);
        }
    }

    @Override
    public ExpenseClaim submitExpenseClaim(ExpenseClaim expenseClaim, List<ClaimItem> items) {
        logger.info("Started ExpenseClaimServiceImpl.submitExpenseClaim()");
        if (items == null || items.isEmpty()) {
            throw new ServiceException("At least one claim item is required");
        }
        Connection connection = null;
        try {
            connection = jdbcUtil.getConnection();
            connection.setAutoCommit(false);
            logger.info("TRANSACTION START");
            double total = 0;
            for (ClaimItem item : items) {
                if (item.getAmount() <= 0) {
                    throw new ServiceException("Invalid claim item amount");
                }
                total += item.getAmount();
            }
            expenseClaim.setClaimAmount(total);
            expenseClaim.setStatus("SUBMITTED");
            ExpenseClaim savedExpenseClaim = expenseClaimDao.insertClaim(connection, expenseClaim);
            for (ClaimItem claimItem : items) {
                claimItem.setExpenseClaim(savedExpenseClaim);
                claimItemDao.insertClaimItem(connection, claimItem);
            }
            connection.commit();
            logger.info("TRANSACTION COMMIT");
            logger.info("Ending ExpenseClaimServiceImpl.submitExpenseClaim()");
            return savedExpenseClaim;
        } catch (Exception e) {
            logger.error("ERROR at ExpenseClaimServiceImpl.submitExpenseClaim()", e);
            if (connection != null) {
                try {
                    connection.rollback();
                    logger.warn("TRANSACTION ROLLBACK");
                } catch (SQLException ex) {
                    logger.error("ERROR at Rollback failed", ex);
                }
            }
            throw new ServiceException("Unable to submit expense claim", e);
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
    public ExpenseClaim getExpenseClaimById(int claimId) {
        logger.info("Started ExpenseClaimServiceImpl.getExpenseClaimById()");
        try {
            ExpenseClaim expenseClaim = expenseClaimDao.getExpenseClaimById(claimId);
            logger.info("Ending ExpenseClaimServiceImpl.getExpenseClaimById()");
            return expenseClaim;
        } catch (RuntimeException e) {
            logger.error("ERROR at ExpenseClaimServiceImpl.getExpenseClaimById()", e);
            throw e;
        }
    }

    @Override
    public void reviewExpenseClaim(int claimId, int managerId, String decision, String reason) {
        logger.info("Started ExpenseClaimServiceImpl.reviewExpenseClaim()");
        Connection connection = null;
        try {
            if (!"APPROVED".equals(decision) && !"REJECTED".equals(decision)) {
                throw new ServiceException("Decision must be APPROVED or REJECTED");
            }
            if ("REJECTED".equals(decision) && (reason == null || reason.trim().isEmpty())) {
                throw new ServiceException("Rejection reason is required");
            }
            connection = jdbcUtil.getConnection();
            connection.setAutoCommit(false);
            logger.info("TRANSACTION START");
            ExpenseClaim claim = expenseClaimDao.getExpenseClaimById(connection, claimId);
            if (claim == null) {
                throw new ServiceException("Expense claim not found");
            }
            if (!"SUBMITTED".equals(claim.getStatus())) {
                throw new ServiceException("Only SUBMITTED claims can be reviewed");
            }
            Employee manager = employeeDao.getEmployeeById(connection, managerId);
            if (manager == null) {
                throw new ServiceException("Manager not found");
            }
            if (manager.getUser() == null || manager.getUser().getRole() != Role.MANAGER) {
                throw new ServiceException("Employee is not a manager");
            }
            Employee claimEmployee = employeeDao.getEmployeeById(connection, claim.getEmployee().getEmployeeId());
            if (claimEmployee == null) {
                throw new ServiceException("Claim employee not found");
            }
            if (claimEmployee.getDepartment() == null) {
                throw new ServiceException("Claim employee has no department");
            }
            int departmentId = claimEmployee.getDepartment().getDepartmentId();
            Integer assignedManagerId = departmentDao.getManagerIdByDepartmentId(connection, departmentId);
            if (assignedManagerId == null) {
                throw new ServiceException("No manager assigned to this department");
            }
            if (assignedManagerId != managerId) {
                throw new ServiceException("Manager is not authorized to review this claim");
            }
            boolean updated = expenseClaimDao.reviewExpenseClaim(connection, claimId, decision, "REJECTED".equals(decision) ? reason : null);
            if (!updated) {
                throw new ServiceException("Failed to update expense claim");
            }
            connection.commit();
            logger.info("TRANSACTION COMMIT");
            logger.info("Expense claim reviewed successfully, claimId={}, decision={}", claimId, decision);
        } catch (ServiceException e) {
            logger.error("ERROR at ExpenseClaimServiceImpl.reviewExpenseClaim()", e);
            rollbackQuietly(connection);
            throw e;
        } catch (Exception e) {
            logger.error("ERROR at ExpenseClaimServiceImpl.reviewExpenseClaim()", e);
            rollbackQuietly(connection);
            throw new ServiceException("Failed to review expense claim", e);
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
        logger.info("Ending ExpenseClaimServiceImpl.reviewExpenseClaim()");
    }

    @Override
    public List<ExpenseClaim> getAllExpenseClaims() {
        logger.info("Started ExpenseClaimServiceImpl.getAllExpenseClaims()");
        try {
            return expenseClaimDao.getAllExpenseClaims();
        } catch (Exception e) {
            logger.error("ERROR at ExpenseClaimServiceImpl.getAllExpenseClaims()", e);
            throw new ServiceException("Unable to get expense claims", e);
        }
    }

    @Override
    public List<ExpenseClaim> getClaimsByEmployeeId(int employeeId) {
        logger.info("Started ExpenseClaimServiceImpl.getClaimsByEmployeeId()");
        try {
            return expenseClaimDao.getClaimsByEmployeeId(employeeId);
        } catch (Exception e) {
            logger.error("ERROR at ExpenseClaimServiceImpl.getClaimsByEmployeeId()", e);
            throw new ServiceException("Unable to get expense claims for employee", e);
        }
    }

    @Override
    public List<ExpenseClaim> getClaimsByStatus(String status) {
        logger.info("Started ExpenseClaimServiceImpl.getClaimsByStatus()");
        try {
            return expenseClaimDao.getClaimsByStatus(status);
        } catch (Exception e) {
            logger.error("ERROR at ExpenseClaimServiceImpl.getClaimsByStatus()", e);
            throw new ServiceException("Unable to get expense claims by status", e);
        }
    }

    @Override
    public List<ExpenseClaim> getPendingClaimsForManager(int managerId) {
        logger.info("Started ExpenseClaimServiceImpl.getPendingClaimsForManager(), managerId={}", managerId);
        try {
            Employee manager = employeeDao.getEmployeeById(managerId);
            if (manager == null || manager.getDepartment() == null) {
                throw new ServiceException("Manager or manager's department not found");
            }
            int departmentId = manager.getDepartment().getDepartmentId();
            List<ExpenseClaim> submittedClaims = expenseClaimDao.getClaimsByStatus("SUBMITTED");
            List<ExpenseClaim> pendingForDept = new ArrayList<>();
            for (ExpenseClaim claim : submittedClaims) {
                Employee claimEmployee = employeeDao.getEmployeeById(claim.getEmployee().getEmployeeId());
                if (claimEmployee != null && claimEmployee.getDepartment() != null
                        && claimEmployee.getDepartment().getDepartmentId() == departmentId) {
                    pendingForDept.add(claim);
                }
            }
            logger.info("Ending ExpenseClaimServiceImpl.getPendingClaimsForManager(), count={}", pendingForDept.size());
            return pendingForDept;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("ERROR at ExpenseClaimServiceImpl.getPendingClaimsForManager()", e);
            throw new ServiceException("Unable to get pending claims for manager", e);
        }
    }

    private void rollbackQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
            logger.warn("TRANSACTION ROLLBACK");
        } catch (SQLException rollbackException) {
            logger.error("ERROR at Rollback failed", rollbackException);
        }
    }
}
