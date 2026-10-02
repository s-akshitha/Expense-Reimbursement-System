package com.ers.dao;

import com.ers.model.ClaimItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IClaimItemDao {
    ClaimItem addClaimItem(ClaimItem claimItem);
    ClaimItem insertClaimItem(Connection connection, ClaimItem claimItem) throws SQLException;
    boolean updateClaimItem(ClaimItem claimItem);
    ClaimItem getClaimItemById(int itemId);
    List<ClaimItem> getAllClaimItems();
    boolean deleteClaimItemById(int itemId);
    List<ClaimItem> getClaimItemsByClaimId(int claimId);
}
