package com.ers.dao;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.exception.DaoException;
import com.ers.model.ClaimItem;
import com.ers.model.ExpenseCategory;
import com.ers.model.ExpenseClaim;
import com.ers.service.DepartmentServiceImpl;
import com.ers.util.JDBCUtil;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClaimItemDaoImpl implements IClaimItemDao{
    //add constructor for JDBCUtil object Initialization
    JDBCUtil jdbcUtil;
    public ClaimItemDaoImpl(){
        this.jdbcUtil=new JDBCUtil();
    }
    private static final Logger logger=(Logger) LoggerFactory.getLogger(ClaimItemDaoImpl.class);
    private static final String insertQuery = "insert into claim_items (claim_id, category_id, description, amount, expense_date) values (?, ?, ?, ?, ?)";
    private static final String updateQuery = "update claim_items set category_id=?, description=?, amount=?, expense_date=? where item_id=?";
    private static final String selectByIdQuery = "select item_id, claim_id, category_id, description, amount, expense_date from claim_items where item_id=?";
    private static final String selectAllQuery = "select item_id, claim_id, category_id, description, amount, expense_date from claim_items";
    private static final String deleteQuery = "delete from claim_items where item_id=?";
    private static final String selectByClaimIdQuery = "select item_id, claim_id, category_id, description, amount, expense_date from claim_items where claim_id=?";

    @Override
    public ClaimItem addClaimItem(ClaimItem claimItem) {
        logger.info("Started ClaimItemDaoImpl.addClaimItem()");
        try (Connection connection = jdbcUtil.getConnection()) {
            ClaimItem result = insertClaimItem(connection, claimItem);
            logger.info("Ending ClaimItemDaoImpl.addClaimItem()");
            return result;
        } catch (SQLException e) {
            logger.error("ERROR at ClaimItemDaoImpl.addClaimItem()", e);
            throw new DaoException("Unable to add claim item", e);
        }
    }

    @Override
    public ClaimItem insertClaimItem(Connection connection, ClaimItem claimItem) throws SQLException {
        logger.info("Started ClaimItemDaoImpl.insertClaimItem()");
        try (PreparedStatement ps = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, claimItem.getExpenseClaim().getClaimId());
            ps.setInt(2, claimItem.getCategory().getCategoryId());
            ps.setString(3, claimItem.getDescription());
            ps.setDouble(4, claimItem.getAmount());
            ps.setDate(5, Date.valueOf(claimItem.getExpenseDate()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    claimItem.setItemId(rs.getInt(1));
                }
            }
            logger.info("Ending ClaimItemDaoImpl.insertClaimItem()");
            return claimItem;
        }
    }

    @Override
    public boolean updateClaimItem(ClaimItem claimItem) {
        logger.info("Started ClaimItemDaoImpl.updateClaimItem()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setInt(1, claimItem.getCategory().getCategoryId());
            ps.setString(2, claimItem.getDescription());
            ps.setDouble(3, claimItem.getAmount());
            ps.setDate(4, Date.valueOf(claimItem.getExpenseDate()));
            ps.setInt(5, claimItem.getItemId());
            boolean updated = ps.executeUpdate() > 0;
            logger.info("Ending ClaimItemDaoImpl.updateClaimItem()");
            return updated;
        } catch (SQLException e) {
            logger.error("ERROR at ClaimItemDaoImpl.updateClaimItem()", e);
            throw new DaoException("Unable to update claim item", e);
        }
    }

    @Override
    public ClaimItem getClaimItemById(int itemId) {
        logger.info("Started ClaimItemDaoImpl.getClaimItemById()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectByIdQuery)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapClaimItem(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("ERROR at ClaimItemDaoImpl.getClaimItemById()", e);
            throw new DaoException("Unable to retrieve claim item", e);
        }
    }

    @Override
    public List<ClaimItem> getAllClaimItems() {
        logger.info("Started ClaimItemDaoImpl.getAllClaimItems()");
        List<ClaimItem> items = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectAllQuery); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(mapClaimItem(rs));
            }
            logger.info("Ending ClaimItemDaoImpl.getAllClaimItems()");
            return items;
        } catch (SQLException e) {
            logger.error("ERROR at ClaimItemDaoImpl.getAllClaimItems()", e);
            throw new DaoException("Unable to retrieve claim items", e);
        }
    }

    @Override
    public boolean deleteClaimItemById(int itemId) {
        logger.info("Started ClaimItemDaoImpl.deleteClaimItemById()");
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setInt(1, itemId);
            boolean deleted = ps.executeUpdate() > 0;
            logger.info("Ending ClaimItemDaoImpl.deleteClaimItemById()");
            return deleted;
        } catch (SQLException e) {
            logger.error("ERROR at ClaimItemDaoImpl.deleteClaimItemById()", e);
            throw new DaoException("Unable to delete claim item", e);
        }
    }

    @Override
    public List<ClaimItem> getClaimItemsByClaimId(int claimId) {
        logger.info("Started ClaimItemDaoImpl.getClaimItemsByClaimId()");
        List<ClaimItem> items = new ArrayList<>();
        try (Connection connection = jdbcUtil.getConnection(); PreparedStatement ps = connection.prepareStatement(selectByClaimIdQuery)) {
            ps.setInt(1, claimId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapClaimItem(rs));
                }
            }
            logger.info("Ending ClaimItemDaoImpl.getClaimItemsByClaimId()");
            return items;
        } catch (SQLException e) {
            logger.error("ERROR at ClaimItemDaoImpl.getClaimItemsByClaimId()", e);
            throw new DaoException("Unable to retrieve claim items", e);
        }
    }

    private ClaimItem mapClaimItem(ResultSet rs) throws SQLException {
        ClaimItem item = new ClaimItem();
        item.setItemId(rs.getInt("item_id"));
        ExpenseClaim claim = new ExpenseClaim();
        claim.setClaimId(rs.getInt("claim_id"));
        item.setExpenseClaim(claim);
        ExpenseCategory category = new ExpenseCategory();
        category.setCategoryId(rs.getInt("category_id"));
        item.setCategory(category);
        item.setDescription(rs.getString("description"));
        item.setAmount(rs.getDouble("amount"));
        Date expenseDate = rs.getDate("expense_date");
        if (expenseDate != null) {
            item.setExpenseDate(expenseDate.toLocalDate());
        }
        return item;
    }
}
