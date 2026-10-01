package com.ers.service;

import com.ers.dao.*;
import com.ers.exception.ServiceException;
import com.ers.model.ClaimItem;
import com.ers.model.ExpenseClaim;
import com.ers.util.JDBCUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseClaimServiceImplTest {
    private JDBCUtil jdbcUtil;
    private IExpenseClaimDao expenseClaimDao;
    private IClaimItemDao claimItemDao;
    private Connection connection;
    private IEmployeeDao employeeDao;
    private IDepartmentDao departmentDao;

    private ExpenseClaimServiceImpl expenseClaimService;

    @BeforeEach
    void setUp() {
        jdbcUtil=mock(JDBCUtil.class);
        expenseClaimDao=mock(IExpenseClaimDao.class);
        claimItemDao=mock(IClaimItemDao.class);
        employeeDao=mock(EmployeeDaoImpl.class);
        connection=mock(Connection.class);
        departmentDao=mock(DepartmentDaoImpl.class);
        expenseClaimService=new ExpenseClaimServiceImpl(jdbcUtil,expenseClaimDao,claimItemDao,employeeDao,departmentDao);
    }
    @AfterEach
    void tearDown() {
    }

    @Test
    void addExpenseClaim() {
    }

    @Test
    void submitExpenseClaim() throws SQLException {
        //Arrange
        ExpenseClaim claim=mock(ExpenseClaim.class);
        ClaimItem item1=mock(ClaimItem.class);
        ClaimItem item2=mock(ClaimItem.class);
        ExpenseClaim savedClaim = mock(ExpenseClaim.class);
        when(jdbcUtil.getConnection()).thenReturn(connection);
        when(item1.getAmount()).thenReturn(500.0);
        when(item2.getAmount()).thenReturn(300.0);
        when(expenseClaimDao.insertClaim(connection, claim)).thenReturn(savedClaim);
        List<ClaimItem> items=List.of(item1,item2);
        //Act
        ExpenseClaim result= expenseClaimService.submitExpenseClaim(claim,items);
        //Assert
        assertSame(savedClaim, result);
        //Total=500+300
        verify(claim).setClaimAmount(800.0);
        //Claim inserted
        verify(expenseClaimDao).insertClaim(connection, claim);
        //Claim items linked to saved claim
        verify(item1).setExpenseClaim(savedClaim);
        verify(item2).setExpenseClaim(savedClaim);
        //Both items use SAME connection
        verify(claimItemDao).insertClaimItem(connection, item1);
        verify(claimItemDao).insertClaimItem(connection, item2);
        //Transaction
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection, never()).rollback();
        verify(connection).close();
    }
    @Test
    void submitExpenseClaim_whenAmountInvalid_shouldRollback() throws Exception{
        //Arrange
        ExpenseClaim claim = mock(ExpenseClaim.class);
        ClaimItem item = mock(ClaimItem.class);
        when(jdbcUtil.getConnection()).thenReturn(connection);
        when(item.getAmount()).thenReturn(-100.0);
        //Act+Assert
        assertThrows(ServiceException.class,()->expenseClaimService.submitExpenseClaim(claim, List.of(item)));
        //Nothing should be inserted
        verify(expenseClaimDao, never()).insertClaim(any(), any());
        verify(claimItemDao,never()).insertClaimItem(any(),any());
        //Transaction must rollback
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).close();
    }
    @Test
    void submitExpenseClaim_whenClaimItemFails_shouldRollback() throws Exception{
        //Arrange
        ExpenseClaim claim = mock(ExpenseClaim.class);
        ExpenseClaim savedClaim = mock(ExpenseClaim.class);
        ClaimItem item1 = mock(ClaimItem.class);
        ClaimItem item2 = mock(ClaimItem.class);
        when(jdbcUtil.getConnection()).thenReturn(connection);
        when(item1.getAmount()).thenReturn(500.0);
        when(item2.getAmount()).thenReturn(300.0);
        when(expenseClaimDao.insertClaim(connection,claim)).thenReturn(savedClaim);
        when(claimItemDao.insertClaimItem(connection,item1)).thenReturn(item1);
        when(claimItemDao.insertClaimItem(connection,item2)).thenThrow(new RuntimeException("Item insert failed"));
        //Act+Assert
        assertThrows(ServiceException.class,()->expenseClaimService.submitExpenseClaim(claim,List.of(item1, item2)));
        //Claim was attempted
        verify(expenseClaimDao).insertClaim(connection,claim);
        //First item was attempted
        verify(claimItemDao).insertClaimItem(connection,item1);
        //Second item failed
        verify(claimItemDao).insertClaimItem(connection,item2);
        //Entire transaction rolls back
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).close();
    }

    @Test
    void getExpenseClaimById() {
    }
}