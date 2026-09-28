package com.ers.dao;

import ch.qos.logback.classic.Logger;
import com.ers.exception.DaoException;
import com.ers.model.Employee;
import com.ers.model.ExpenseClaim;
import com.ers.model.FinanceExecutive;
import com.ers.util.JDBCUtil;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FinanceExecutiveDaoImpl implements IFinanceExecutiveDao{
    JDBCUtil jdbcUtil;
    public FinanceExecutiveDaoImpl(){
        this.jdbcUtil=new JDBCUtil();
    }
    private static final Logger logger=(Logger)LoggerFactory.getLogger(FinanceExecutiveDaoImpl.class);
    private static final String insertQuery ="insert into finance_executives(employee_id, full_name, email,department)values(?,?,?,?)";
    private static final String updateQuery ="update finance_executives set full_name=?,email=?,department=? where employee_id=?";
    private static final String getEmployeeByIdQuery ="select employee_id, full_name, email, department from finance_executives where employee_id = ?";
    private static final String selectQuery ="select employee_id, full_name, email, department from finance_executives order by employee_id";
    private static final String deleteQuery ="delete from finance_executives where employee_id = ?";
    private static final String selectApprovedQuery="select claim_id,employee_id,claim_description,claim_date,claim_amount,status,document_path,reason from expense_claims where status='APPROVED' order by claim_date";
    private static final String selectClaimByIdQuery = "select claim_id,employee_id,claim_description,claim_date,claim_amount,status,document_path,reason from expense_claims where claim_id=?";

    @Override
    public FinanceExecutive addFinanceExecutive(Connection connection, FinanceExecutive financeExecutive) {
        logger.info("Started addFinanceExecutive()");
        try(PreparedStatement ps = connection.prepareStatement(insertQuery)){
            ps.setInt(1, financeExecutive.getEmployee().getEmployeeId());
            ps.setString(2, financeExecutive.getFullName());
            ps.setString(3, financeExecutive.getEmail());
            ps.setString(4, financeExecutive.getDepartment());
            int rows = ps.executeUpdate();
            if(rows==1){
                logger.info("Finance executive added successfully, employeeId={}", financeExecutive.getEmployee().getEmployeeId());
                return financeExecutive;
            }
            logger.warn("Finance executive was not added");
            return null;
        }catch(SQLException e){
            logger.error("Error while adding finance executive", e);
            throw new DaoException("Failed to add finance executive", e);
        }
    }

    @Override
    public boolean updateFinanceExecutive(FinanceExecutive financeExecutive){
        logger.info("Started updateFinanceExecutive()");
        try(Connection connection=jdbcUtil.getConnection();PreparedStatement ps=connection.prepareStatement(updateQuery)){
            ps.setString(1,financeExecutive.getFullName());
            ps.setString(2,financeExecutive.getEmail());
            ps.setString(3,financeExecutive.getDepartment());
            ps.setInt(4,financeExecutive.getEmployee().getEmployeeId());
            int rows=ps.executeUpdate();
            if(rows==1) {
                logger.info("Finance executive updated successfully, employeeId={}", financeExecutive.getEmployee().getEmployeeId());
                return true;
            }
            logger.warn("Finance executive not found, employeeId={}",financeExecutive.getEmployee().getEmployeeId());
            return false;
        }catch(SQLException e){
            logger.error("Error while updating finance executive",e);
            throw new DaoException("Failed to update finance executive",e);
        }
    }

    @Override
    public FinanceExecutive getFinanceExecutiveById(Employee employee){
        logger.info("Started getFinanceExecutiveById()");
        try(Connection connection=jdbcUtil.getConnection();PreparedStatement ps=connection.prepareStatement(getEmployeeByIdQuery)){
            ps.setInt(1,employee.getEmployeeId());
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    FinanceExecutive financeExecutive=new FinanceExecutive();
                    financeExecutive.setEmployee(employee);
                    financeExecutive.setFullName(rs.getString("full_name"));
                    financeExecutive.setEmail(rs.getString("email"));
                    financeExecutive.setDepartment(rs.getString("department"));
                    logger.info("Finance executive found, employeeId={}",employee.getEmployeeId());
                    return financeExecutive;
                }
                logger.warn("Finance executive not found, employeeId={}", employee.getEmployeeId());
                return null;
            }
        }catch(SQLException e){
            logger.error("Error while getting finance executive", e);
            throw new DaoException("Failed to get finance executive", e);
        }
    }

    @Override
    public List<FinanceExecutive> getAllFinanceExecutives(){
        logger.info("Started getAllFinanceExecutives()");
        List<FinanceExecutive> financeExecutives=new ArrayList<>();
        try(Connection connection=jdbcUtil.getConnection();PreparedStatement ps=connection.prepareStatement(selectQuery);ResultSet rs=ps.executeQuery()){
            while(rs.next()){
                Employee employee=new Employee();
                employee.setEmployeeId(rs.getInt("employee_id"));
                FinanceExecutive financeExecutive=new FinanceExecutive();
                financeExecutive.setEmployee(employee);
                financeExecutive.setFullName(rs.getString("full_name"));
                financeExecutive.setEmail(rs.getString("email"));
                financeExecutive.setDepartment(rs.getString("department"));
                financeExecutives.add(financeExecutive);
            }
            logger.info("Retrieved {} finance executives",financeExecutives.size());
            return financeExecutives;
        }catch(SQLException e){
            logger.error("Error while getting all finance executives",e);
            throw new DaoException("Failed to get finance executives",e);
        }
    }

    @Override
    public boolean deleteFinanceExecutiveById(Employee employee){
        logger.info("Started deleteFinanceExecutiveById(), employeeId={}",employee.getEmployeeId());
        try(Connection connection=jdbcUtil.getConnection();PreparedStatement ps=connection.prepareStatement(deleteQuery)){
            ps.setInt(1,employee.getEmployeeId());
            boolean deleted=ps.executeUpdate()>0;
            logger.info("Finance executive deletion status={}", deleted);
            return deleted;
        }catch(SQLException e){
            logger.error("Error while deleting finance executive", e);
            throw new DaoException("Failed to delete finance executive", e);
        }
    }

    @Override
    public List<ExpenseClaim> getApprovedClaims(Connection connection){
        logger.info("Started getApprovedClaims()");
        List<ExpenseClaim> claims = new ArrayList<>();
        try(PreparedStatement ps=connection.prepareStatement(selectApprovedQuery);ResultSet rs=ps.executeQuery()){
            while(rs.next()){
                claims.add(mapClaim(rs));
            }
            logger.info("Retrieved {} approved claims", claims.size());
            return claims;
        }catch(SQLException e){
            logger.error("Error while getting approved claims", e);
            throw new DaoException("Failed to get approved claims", e);
        }
    }

    @Override
    public ExpenseClaim getClaimById(Connection connection,int claimId){
        logger.info("Started getClaimById(), claimId={}",claimId);
        try(PreparedStatement ps=connection.prepareStatement(selectClaimByIdQuery)){
            ps.setInt(1,claimId);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    ExpenseClaim claim=mapClaim(rs);
                    logger.info("Claim found, claimId={}",claimId);
                    return claim;
                }
                logger.warn("Claim not found, claimId={}",claimId);
                return null;
            }
        }catch(SQLException e){
            logger.error("Error while getting claim, claimId={}",claimId,e);
            throw new DaoException("Failed to get claim",e);
        }
    }

    private ExpenseClaim mapClaim(ResultSet rs) throws SQLException{
        ExpenseClaim claim=new ExpenseClaim();
        claim.setClaimId(rs.getInt("claim_id"));
        Employee employee=new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        claim.setEmployee(employee);
        claim.setClaimDesc(rs.getString("claim_description"));
        Date claimDate=rs.getDate("claim_date");
        if(claimDate!=null){
            claim.setClaimDate(claimDate.toLocalDate());
        }
        claim.setClaimAmount(rs.getDouble("claim_amount"));
        claim.setStatus(rs.getString("status"));
        claim.setDocumentPath(rs.getString("document_path"));
        claim.setReason(rs.getString("reason"));
        return claim;
    }

}
