package com.ers.service;

import com.ers.dao.IDepartmentDao;
import com.ers.dao.IEmployeeDao;
import com.ers.dao.IUserDao;
import com.ers.exception.ServiceException;
import com.ers.model.Department;
import com.ers.model.Employee;
import com.ers.model.Role;
import com.ers.model.User;
import com.ers.util.JDBCUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {
    private IEmployeeDao employeeDao;
    private IUserDao userDao;
    private IDepartmentDao departmentDao;
    private JDBCUtil jdbcUtil;
    private Connection connection;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeDao=mock(IEmployeeDao.class);
        userDao=mock(IUserDao.class);
        departmentDao=mock(IDepartmentDao.class);
        jdbcUtil=mock(JDBCUtil.class);
        connection=mock(Connection.class);
        employeeService = new EmployeeServiceImpl(employeeDao, jdbcUtil, userDao, departmentDao);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addEmployee() throws SQLException {
        User user = mock(User.class);
        Employee employee = mock(Employee.class);

        User savedUser = mock(User.class);
        Employee savedEmployee = mock(Employee.class);
        when(jdbcUtil.getConnection()).thenReturn(connection);
        when(userDao.addUser(connection,user)).thenReturn(savedUser);
        when(savedUser.getRole()).thenReturn(Role.EMPLOYEE);
        when(employeeDao.addEmployee(connection, employee)).thenReturn(savedEmployee);
        // Act
        Employee result=employeeService.addEmployee(employee, user);
        //Assert
        assertSame(savedEmployee, result);
        verify(connection).setAutoCommit(false);
        verify(userDao).addUser(connection,user);
        verify(employee).setUser(savedUser);
        verify(employeeDao).addEmployee(connection, employee);
        verify(connection).commit();
        verify(connection).close();
        verify(connection,never()).rollback();
    }
    @Test
    void addEmployee_whenManager() throws Exception{
        //Arrange
        User user=mock(User.class);
        User savedUser=mock(User.class);
        Employee employee=mock(Employee.class);
        Employee savedEmployee=mock(Employee.class);
        Department department=mock(Department.class);
        when(jdbcUtil.getConnection()).thenReturn(connection);
        when(userDao.addUser(connection, user)).thenReturn(savedUser);
        when(savedUser.getRole()).thenReturn(Role.MANAGER);
        when(employee.getDepartment()).thenReturn(department);
        when(department.getDepartmentId()).thenReturn(3);
        when(savedEmployee.getEmployeeId()).thenReturn(10);
        when(employeeDao.addEmployee(connection, employee)).thenReturn(savedEmployee);
        when(departmentDao.hasManager(connection, 3)).thenReturn(false);
        //Act
        Employee result=employeeService.addEmployee(employee, user);
        //Assert
        assertSame(savedEmployee, result);
        verify(departmentDao).hasManager(connection, 3);
        verify(departmentDao).assignManager(connection, 3, 10);
        verify(connection).commit();
        verify(connection).close();
    }
    @Test
    void addEmployee_whenEmployeeDaoFails_shouldRollback() throws Exception{
        // Arrange
        User user = mock(User.class);
        User savedUser = mock(User.class);
        Employee employee = mock(Employee.class);
        when(jdbcUtil.getConnection()).thenReturn(connection);
        when(userDao.addUser(connection, user)).thenReturn(savedUser);
        when(savedUser.getRole()).thenReturn(Role.EMPLOYEE);
        when(employeeDao.addEmployee(connection, employee)).thenThrow(new RuntimeException("Database error"));
        //Act+Assert
        assertThrows(ServiceException.class, ()->employeeService.addEmployee(employee, user));
        //Verify rollback
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).close();
    }

    @Test
    void updateEmployee(){
        Employee employee = mock(Employee.class);
        when(employeeDao.updateEmployee(employee)).thenReturn(true);
        boolean result = employeeService.updateEmployee(employee);
        assertTrue(result);
        verify(employeeDao).updateEmployee(employee);
    }

    @Test
    void getEmployeeById() {
        Employee employee = mock(Employee.class);
        when(employeeDao.getEmployeeById(1)).thenReturn(employee);
        Employee result = employeeService.getEmployeeById(1);
        assertSame(employee,result);
        verify(employeeDao).getEmployeeById(1);
    }

    @Test
    void getAllEmployees() {
        List<Employee> employees=List.of(mock(Employee.class), mock(Employee.class));
        when(employeeDao.getAllEmployees()).thenReturn(employees);
        List<Employee> result = employeeService.getAllEmployees();
        assertEquals(2, result.size());
        assertSame(employees, result);
        verify(employeeDao).getAllEmployees();
    }

    @Test
    void deleteEmployee() throws SQLException{
        // Arrange
        Employee employee = mock(Employee.class);
        User user = mock(User.class);
        when(jdbcUtil.getConnection()).thenReturn(connection);
        when(employeeDao.getEmployeeById(connection, 1)).thenReturn(employee);
        when(employee.getUser()).thenReturn(user);
        when(user.getUserId()).thenReturn(10);
        when(employee.getDepartment()).thenReturn(mock(com.ers.model.Department.class));
        when(employee.getDepartment().getDepartmentId()).thenReturn(3);
        when(departmentDao.isManager(connection, 1)).thenReturn(false);
        when(employeeDao.deleteEmployee(connection, 1)).thenReturn(true);
        when(userDao.deleteUser(connection, 10)).thenReturn(true);
        //Act
        boolean result = employeeService.deleteEmployee(1);
        //Assert
        assertTrue(result);
        verify(connection).setAutoCommit(false);
        verify(employeeDao).getEmployeeById(connection, 1);
        verify(departmentDao).isManager(connection, 1);
        verify(employeeDao).deleteEmployee(connection, 1);
        verify(userDao).deleteUser(connection, 10);
        verify(connection).commit();
        verify(connection).close();
        verify(connection, never()).rollback();
        verify(departmentDao, never()).removeManager(any(Connection.class), anyInt());
    }
    @Test
    void deleteEmployee_whenEmployeeIsManager() throws SQLException{
        // Arrange
        Employee employee=mock(Employee.class);
        User user=mock(User.class);
        Department department=mock(com.ers.model.Department.class);
        when(jdbcUtil.getConnection()).thenReturn(connection);
        when(employeeDao.getEmployeeById(connection, 1)).thenReturn(employee);
        when(employee.getUser()).thenReturn(user);
        when(user.getUserId()).thenReturn(10);
        when(employee.getDepartment()).thenReturn(department);
        when(department.getDepartmentId()).thenReturn(3);
        when(departmentDao.isManager(connection, 1)).thenReturn(true);
        when(employeeDao.deleteEmployee(connection, 1)).thenReturn(true);
        when(userDao.deleteUser(connection, 10)).thenReturn(true);
        //Act
        boolean result = employeeService.deleteEmployee(1);
        //Assert
        assertTrue(result);
        verify(departmentDao).isManager(connection, 1);
        verify(departmentDao).removeManager(connection, 3);
        verify(employeeDao).deleteEmployee(connection, 1);
        verify(userDao).deleteUser(connection, 10);
        verify(connection).commit();
        verify(connection).close();
        verify(connection,never()).rollback();
    }
    @Test
    void deleteEmployee_whenEmployeeNotFound() throws SQLException {
        //Arrange
        when(jdbcUtil.getConnection()).thenReturn(connection);
        when(employeeDao.getEmployeeById(connection, 1)).thenReturn(null);
        //Act+Assert
        assertThrows(ServiceException.class,()->employeeService.deleteEmployee(1));
        //Verify
        verify(employeeDao).getEmployeeById(connection, 1);
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).close();
        verify(employeeDao, never()).deleteEmployee(any(Connection.class), anyInt());
        verify(userDao, never()).deleteUser(any(Connection.class), anyInt());
    }
}
