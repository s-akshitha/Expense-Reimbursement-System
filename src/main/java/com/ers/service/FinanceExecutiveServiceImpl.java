package com.ers.service;

import ch.qos.logback.classic.Logger;
import com.ers.dao.FinanceExecutiveDaoImpl;
import com.ers.dao.IFinanceExecutiveDao;
import com.ers.exception.ServiceException;
import com.ers.model.Employee;
import com.ers.model.ExpenseClaim;
import com.ers.model.FinanceExecutive;
import com.ers.util.JDBCUtil;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

//Business Logic

public class FinanceExecutiveServiceImpl implements IFinanceExecutiveService {
    private final IFinanceExecutiveDao financeExecutiveDao;
    private final JDBCUtil jdbcUtil;
    private static final Logger logger=(Logger)LoggerFactory.getLogger(FinanceExecutiveServiceImpl.class);

    public FinanceExecutiveServiceImpl() {
        this.financeExecutiveDao = new FinanceExecutiveDaoImpl();
        this.jdbcUtil = new JDBCUtil();
    }

    public FinanceExecutiveServiceImpl(IFinanceExecutiveDao financeExecutiveDao, JDBCUtil jdbcUtil) {
        this.financeExecutiveDao = financeExecutiveDao;
        this.jdbcUtil = jdbcUtil;
    }

    @Override
    public FinanceExecutive addFinanceExecutive(FinanceExecutive financeExecutive){
        logger.info("Started FinanceExecutiveServiceImpl.addFinanceExecutive()");
        if(financeExecutive == null){
            logger.error("FinanceExecutive is null");
            throw new ServiceException("Finance executive cannot be null");
        }
        try(Connection connection=jdbcUtil.getConnection()){
            FinanceExecutive savedFinanceExecutive = financeExecutiveDao.addFinanceExecutive(connection,financeExecutive);
            logger.info("Finance executive created successfully");
            logger.info("Ending FinanceExecutiveServiceImpl.addFinanceExecutive()");
            return savedFinanceExecutive;
        }catch(SQLException e){
            logger.error("ERROR at FinanceExecutiveServiceImpl.addFinanceExecutive()", e);
            throw new ServiceException("Unable to create finance executive", e);
        }
    }

    @Override
    public boolean updateFinanceExecutive(FinanceExecutive financeExecutive){
        logger.info("Started FinanceExecutiveServiceImpl.updateFinanceExecutive()");
        if(financeExecutive==null){
            logger.error("FinanceExecutive is null");
            throw new ServiceException("Finance executive cannot be null");
        }
        try{
            boolean updated = financeExecutiveDao.updateFinanceExecutive(financeExecutive);
            logger.info("Finance executive update completed, result={}", updated);
            logger.info("Ending FinanceExecutiveServiceImpl.updateFinanceExecutive()");
            return updated;
        }catch(Exception e){
            logger.error("ERROR at FinanceExecutiveServiceImpl.updateFinanceExecutive()", e);
            throw new ServiceException("Unable to update finance executive", e);
        }
    }

    @Override
    public FinanceExecutive getFinanceExecutiveById(Employee employee){
        logger.info("Started FinanceExecutiveServiceImpl.getFinanceExecutiveById()");
        if(employee==null){
            logger.error("Employee is null");
            throw new ServiceException("Employee cannot be null");
        }
        try{
            FinanceExecutive financeExecutive = financeExecutiveDao.getFinanceExecutiveById(employee);
            if(financeExecutive==null){
                logger.warn("Finance executive not found, employeeId={}", employee.getEmployeeId());
            } else {
                logger.info("Finance executive found");
            }
            logger.info("Ending FinanceExecutiveServiceImpl.getFinanceExecutiveById()");
            return financeExecutive;
        }catch(Exception e){
            logger.error("ERROR at FinanceExecutiveServiceImpl.getFinanceExecutiveById()", e);
            throw new ServiceException("Unable to get finance executive", e);
        }
    }

    @Override
    public List<FinanceExecutive> getAllFinanceExecutives(){
        logger.info("Started FinanceExecutiveServiceImpl.getAllFinanceExecutives()");
        try{
            List<FinanceExecutive> financeExecutives = financeExecutiveDao.getAllFinanceExecutives();
            logger.info("Finance executives retrieved, count={}", financeExecutives.size());
            logger.info("Ending FinanceExecutiveServiceImpl.getAllFinanceExecutives()");
            return financeExecutives;
        }catch(Exception e){
            logger.error("ERROR at FinanceExecutiveServiceImpl.getAllFinanceExecutives()", e);
            throw new ServiceException("Unable to get finance executives", e);
        }
    }

    @Override
    public boolean deleteFinanceExecutiveById(Employee employee){
        logger.info("Started FinanceExecutiveServiceImpl.deleteFinanceExecutiveById()");
        if(employee==null){
            logger.error("Employee is null");
            throw new ServiceException("Employee cannot be null");
        }
        try{
            boolean deleted=financeExecutiveDao.deleteFinanceExecutiveById(employee);
            logger.info("Finance executive deletion completed");
            logger.info("Ending FinanceExecutiveServiceImpl.deleteFinanceExecutiveById()");
            return deleted;
        }catch(Exception e){
            logger.error("ERROR at FinanceExecutiveServiceImpl.deleteFinanceExecutiveById()", e);
            throw new ServiceException("Unable to delete finance executive", e);
        }
    }

    @Override
    public List<ExpenseClaim> getApprovedClaims(){
        logger.info("Started FinanceExecutiveServiceImpl.getApprovedClaims()");
        try(Connection connection=jdbcUtil.getConnection()){
            List<ExpenseClaim> approvedClaims=financeExecutiveDao.getApprovedClaims(connection);
            logger.info("Approved claims retrieved, count={}", approvedClaims.size());
            logger.info("Ending FinanceExecutiveServiceImpl.getApprovedClaims()");
            return approvedClaims;
        }catch(SQLException e){
            logger.error("ERROR at FinanceExecutiveServiceImpl.getApprovedClaims()", e);
            throw new ServiceException("Unable to get approved claims", e);
        }
    }

    @Override
    public ExpenseClaim getClaimById(int claimId){
        logger.info("Started FinanceExecutiveServiceImpl.getClaimById()");
        if(claimId<=0){
            logger.error("Invalid claimId={}", claimId);
            throw new ServiceException("Invalid claim ID");
        }
        try(Connection connection=jdbcUtil.getConnection()){
            ExpenseClaim expenseClaim=financeExecutiveDao.getClaimById(connection, claimId);
            if(expenseClaim==null){
                logger.warn("Expense claim not found, claimId={}", claimId);
            }else{
                logger.info("Expense claim found, claimId={}", claimId);
            }
            logger.info("Ending FinanceExecutiveServiceImpl.getClaimById()");
            return expenseClaim;
        }catch(SQLException e){
            logger.error("ERROR at FinanceExecutiveServiceImpl.getClaimById()", e);
            throw new ServiceException("Unable to get expense claim", e);
        }
    }
}
