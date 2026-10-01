package com.ers.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.dao.ExpenseCategoryDaoImpl;
import com.ers.dao.IExpenseCategoryDao;
import com.ers.exception.ServiceException;
import com.ers.model.ExpenseCategory;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ExpenseCategoryServiceImpl implements IExpenseCategoryService{
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ExpenseCategoryServiceImpl.class);

    private final IExpenseCategoryDao expenseCategoryDao;

    public ExpenseCategoryServiceImpl() {
        this.expenseCategoryDao = new ExpenseCategoryDaoImpl();
    }

    public ExpenseCategoryServiceImpl(IExpenseCategoryDao expenseCategoryDao) {
        this.expenseCategoryDao = expenseCategoryDao;
    }

    @Override
    public ExpenseCategory addExpenseCategory(ExpenseCategory expenseCategory) {
        logger.info("Started ExpenseCategoryServiceImpl.addExpenseCategory()");
        try {
            ExpenseCategory saved = expenseCategoryDao.addExpenseCategory(expenseCategory);
            logger.info("Expense category created, categoryId={}", saved.getCategoryId());
            return saved;
        } catch (Exception e) {
            logger.error("ERROR at ExpenseCategoryServiceImpl.addExpenseCategory()", e);
            throw new ServiceException("Unable to add expense category", e);
        }
    }

    @Override
    public boolean updateExpenseCategory(ExpenseCategory expenseCategory) {
        logger.info("Started ExpenseCategoryServiceImpl.updateExpenseCategory()");
        try {
            boolean updated = expenseCategoryDao.updateExpenseCategory(expenseCategory);
            logger.info("Expense category update status={}", updated);
            return updated;
        } catch (Exception e) {
            logger.error("ERROR at ExpenseCategoryServiceImpl.updateExpenseCategory()", e);
            throw new ServiceException("Unable to update expense category", e);
        }
    }

    @Override
    public ExpenseCategory getExpenseCategoryById(int categoryId) {
        logger.info("Started ExpenseCategoryServiceImpl.getExpenseCategoryById()");
        try {
            ExpenseCategory category = expenseCategoryDao.getExpenseCategoryById(categoryId);
            if (category == null) {
                logger.warn("Expense category not found, categoryId={}", categoryId);
            }
            return category;
        } catch (Exception e) {
            logger.error("ERROR at ExpenseCategoryServiceImpl.getExpenseCategoryById()", e);
            throw new ServiceException("Unable to get expense category", e);
        }
    }

    @Override
    public List<ExpenseCategory> getAllExpenseCategories() {
        logger.info("Started ExpenseCategoryServiceImpl.getAllExpenseCategories()");
        try {
            return expenseCategoryDao.getAllExpenseCategories();
        } catch (Exception e) {
            logger.error("ERROR at ExpenseCategoryServiceImpl.getAllExpenseCategories()", e);
            throw new ServiceException("Unable to get expense categories", e);
        }
    }

    @Override
    public boolean deleteExpenseCategoryById(int categoryId) {
        logger.info("Started ExpenseCategoryServiceImpl.deleteExpenseCategoryById()");
        try {
            boolean deleted = expenseCategoryDao.deleteExpenseCategoryById(categoryId);
            logger.info("Expense category deletion status={}", deleted);
            return deleted;
        } catch (Exception e) {
            logger.error("ERROR at ExpenseCategoryServiceImpl.deleteExpenseCategoryById()", e);
            throw new ServiceException("Unable to delete expense category", e);
        }
    }
}
