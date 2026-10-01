package com.ers.dao;

import ch.qos.logback.classic.Logger;
import com.ers.exception.DaoException;
import com.ers.model.ExpenseCategory;
import com.ers.util.JDBCUtil;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseCategoryDaoImpl implements IExpenseCategoryDao{
    private final JDBCUtil jdbcUtil;

    public ExpenseCategoryDaoImpl() {
        this.jdbcUtil = new JDBCUtil();
    }
    private static final Logger logger=(Logger) LoggerFactory.getLogger(ExpenseCategoryDaoImpl.class);

    private static final String insertQuery = "insert into expense_categories (category_name, description) values (?, ?)";
    private static final String updateQuery = "update expense_categories set category_name=?, description=? where category_id=?";
    private static final String selectByIdQuery = "select category_id, category_name, description from expense_categories where category_id=?";
    private static final String selectAllQuery = "select category_id, category_name, description from expense_categories";
    private static final String deleteQuery = "delete from expense_categories where category_id=?";

    @Override
    public ExpenseCategory addExpenseCategory(ExpenseCategory expenseCategory) {
        logger.info("Started ExpenseCategoryDaoImpl.addExpenseCategory()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, expenseCategory.getCategoryName());
            ps.setString(2, expenseCategory.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    expenseCategory.setCategoryId(rs.getInt(1));
                }
            }
            logger.info("Ending ExpenseCategoryDaoImpl.addExpenseCategory()");
            return expenseCategory;
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseCategoryDaoImpl.addExpenseCategory()", e);
            throw new DaoException("Unable to add expense category", e);
        }
    }

    @Override
    public boolean updateExpenseCategory(ExpenseCategory expenseCategory) {
        logger.info("Started ExpenseCategoryDaoImpl.updateExpenseCategory()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setString(1, expenseCategory.getCategoryName());
            ps.setString(2, expenseCategory.getDescription());
            ps.setInt(3, expenseCategory.getCategoryId());
            boolean updated = ps.executeUpdate() > 0;
            logger.info("Ending ExpenseCategoryDaoImpl.updateExpenseCategory()");
            return updated;
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseCategoryDaoImpl.updateExpenseCategory()", e);
            throw new DaoException("Unable to update expense category", e);
        }
    }

    @Override
    public ExpenseCategory getExpenseCategoryById(int categoryId) {
        logger.info("Started ExpenseCategoryDaoImpl.getExpenseCategoryById()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectByIdQuery)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCategory(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseCategoryDaoImpl.getExpenseCategoryById()", e);
            throw new DaoException("Unable to retrieve expense category", e);
        }
    }

    @Override
    public List<ExpenseCategory> getAllExpenseCategories() {
        logger.info("Started ExpenseCategoryDaoImpl.getAllExpenseCategories()");
        List<ExpenseCategory> categories = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectAllQuery); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
            logger.info("Ending ExpenseCategoryDaoImpl.getAllExpenseCategories()");
            return categories;
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseCategoryDaoImpl.getAllExpenseCategories()", e);
            throw new DaoException("Unable to retrieve expense categories", e);
        }
    }

    @Override
    public boolean deleteExpenseCategoryById(int categoryId) {
        logger.info("Started ExpenseCategoryDaoImpl.deleteExpenseCategoryById()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setInt(1, categoryId);
            boolean deleted = ps.executeUpdate() > 0;
            logger.info("Ending ExpenseCategoryDaoImpl.deleteExpenseCategoryById()");
            return deleted;
        } catch (SQLException e) {
            logger.error("ERROR at ExpenseCategoryDaoImpl.deleteExpenseCategoryById()", e);
            throw new DaoException("Unable to delete expense category", e);
        }
    }

    private ExpenseCategory mapCategory(ResultSet rs) throws SQLException {
        ExpenseCategory category = new ExpenseCategory();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));
        return category;
    }
}
