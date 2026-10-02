package com.ers.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.dao.DepartmentDaoImpl;
import com.ers.dao.IDepartmentDao;
import com.ers.exception.ServiceException;
import com.ers.model.Department;
import com.ers.model.Employee;
import com.ers.util.JDBCUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DepartmentServiceImpl implements IDepartmentService{
    private static final Logger logger;
    static{
        LoggerContext context=new LoggerContext();
        logger=context.getLogger(DepartmentServiceImpl.class.getName());
    }
    private final IDepartmentDao departmentDao;
    private final JDBCUtil jdbcUtil;

    public DepartmentServiceImpl() {
        this.departmentDao = new DepartmentDaoImpl();
        this.jdbcUtil = new JDBCUtil();
    }

    public DepartmentServiceImpl(IDepartmentDao departmentDao, JDBCUtil jdbcUtil) {
        this.departmentDao = departmentDao;
        this.jdbcUtil = jdbcUtil;
    }

    @Override
    public Department addDepartment(Department department) {
        logger.info("Started DepartmentServiceImpl.addDepartment()");
        try {
            Department savedDepartment = departmentDao.addDepartment(department);
            logger.info("Department created, departmentId={}", savedDepartment.getDepartmentId());
            return savedDepartment;
        } catch (Exception e) {
            logger.error("ERROR at DepartmentServiceImpl.addDepartment()", e);
            throw new ServiceException("Unable to add department", e);
        }
    }

    @Override
    public boolean updateDepartment(Department department) {
        logger.info("Started DepartmentServiceImpl.updateDepartment()");
        try {
            boolean updated = departmentDao.updateDepartment(department);
            logger.info("Department update status={}", updated);
            return updated;
        } catch (Exception e) {
            logger.error("ERROR at DepartmentServiceImpl.updateDepartment()", e);
            throw new ServiceException("Unable to update department", e);
        }
    }

    @Override
    public Department getDepartmentById(int departmentId) {
        logger.info("Started DepartmentServiceImpl.getDepartmentById(), departmentId={}", departmentId);
        try {
            Department department = departmentDao.getDepartmentById(departmentId);
            if (department == null) {
                logger.warn("Department not found, departmentId={}", departmentId);
            }
            return department;
        } catch (Exception e) {
            logger.error("ERROR at DepartmentServiceImpl.getDepartmentById()", e);
            throw new ServiceException("Unable to get department", e);
        }
    }

    @Override
    public List<Department> getAllDepartments() {
        logger.info("Started DepartmentServiceImpl.getAllDepartments()");
        try {
            return departmentDao.getAllDepartments();
        } catch (Exception e) {
            logger.error("ERROR at DepartmentServiceImpl.getAllDepartments()", e);
            throw new ServiceException("Unable to get departments", e);
        }
    }

    @Override
    public boolean deleteDepartmentById(int departmentId) {
        logger.info("Started DepartmentServiceImpl.deleteDepartmentById(), departmentId={}", departmentId);
        try {
            boolean deleted = departmentDao.deleteDepartmentById(departmentId);
            logger.info("Department deletion status={}", deleted);
            return deleted;
        } catch (Exception e) {
            logger.error("ERROR at DepartmentServiceImpl.deleteDepartmentById()", e);
            throw new ServiceException("Unable to delete department", e);
        }
    }

    @Override
    public boolean assignManager(int departmentId, int employeeId) {
        logger.info("Started DepartmentServiceImpl.assignManager(), departmentId={}, employeeId={}", departmentId, employeeId);
        Connection connection = null;
        try {
            connection = jdbcUtil.getConnection();
            boolean hasManager = departmentDao.hasManager(connection, departmentId);
            if (hasManager) {
                throw new ServiceException("Department already has a manager");
            }
            int rows = departmentDao.assignManager(connection, departmentId, employeeId);
            logger.info("Ending DepartmentServiceImpl.assignManager()");
            return rows > 0;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("ERROR at DepartmentServiceImpl.assignManager()", e);
            throw new ServiceException("Unable to assign manager", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("ERROR closing connection at DepartmentServiceImpl.assignManager()", e);
                }
            }
        }
    }
}
