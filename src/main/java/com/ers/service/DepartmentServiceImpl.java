package com.ers.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.dao.DepartmentDaoImpl;
import com.ers.exception.ServiceException;
import com.ers.model.Department;
import com.ers.model.Employee;

import java.util.List;

public class DepartmentServiceImpl implements IDepartmentService{
    private static final Logger logger;
    static{
        LoggerContext context=new LoggerContext();
        logger=context.getLogger(DepartmentServiceImpl.class.getName());
    }
    private final DepartmentDaoImpl departmentDao;
    public DepartmentServiceImpl() {
        this.departmentDao = new DepartmentDaoImpl();
    }
    @Override
    public Department addDepartment(Department department) {
        logger.info("Started DepartmentServiceImpl.addDepartment()");
        try {
            Department savedDepartment=departmentDao.addDepartment(department);
            logger.info("Department created, departmentId={}",savedDepartment.getDepartmentId());
            return savedDepartment;
        }catch(Exception e){
            logger.error("ERROR at DepartmentServiceImpl.addDepartment()", e);
            throw new ServiceException("Unable to add department", e);
        }
    }

    @Override
    public boolean updateDepartment(Department department) {
        logger.info("Started DepartmentServiceImpl.updateDepartment()");
        try{
            boolean updated=departmentDao.updateDepartment(department);
            logger.info("Department update status={}", updated);
            return updated;
        }catch(Exception e){
            logger.error("ERROR at DepartmentServiceImpl.updateDepartment()", e);
            throw new ServiceException("Unable to update department", e);
        }
    }

    @Override
    public Department getDepartmentById(int departmentId) {
        logger.info("Started DepartmentServiceImpl.getDepartmentById(), departmentId={}",departmentId);
        try {
            Department department=departmentDao.getDepartmentById(departmentId);
            if(department == null){
                logger.warn("Department not found, departmentId={}",departmentId);
                return null;
            }
            return department;
        }catch(Exception e){
            logger.error("ERROR at DepartmentServiceImpl.getDepartmentById()", e);
            throw new ServiceException("Unable to get department", e);
        }
    }

    @Override
    public List<Department> getAllDepartments(){
        logger.info("Started DepartmentServiceImpl.getAllDepartments()");
        try{
            return departmentDao.getAllDepartments();
        }catch(Exception e){
            logger.error("ERROR at DepartmentServiceImpl.getAllDepartments()", e);
            throw new ServiceException("Unable to get departments", e);
        }
    }

    @Override
    public boolean deleteDepartmentById(int departmentId) {
        logger.info("Started DepartmentServiceImpl.deleteDepartmentById(), departmentId={}",departmentId);
        try{
            boolean deleted=departmentDao.deleteDepartmentById(departmentId);
            logger.info("Department deletion status={}", deleted);
            return deleted;
        }catch(Exception e){
            logger.error("ERROR at DepartmentServiceImpl.deleteDepartmentById()",e);
            throw new ServiceException("Unable to delete department", e);
        }
    }

//    @Override
//    public List<Employee> getEmployeesByDepartmentId(int departmentId) {
//        return List.of();
//    }
//
//    @Override
//    public Department getDepartmentByManagerId(int managerId) {
//        return null;
//    }
}
