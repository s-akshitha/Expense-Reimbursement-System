package com.ers.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ers.controller.AppController;
import com.ers.dao.ClaimItemDaoImpl;
import com.ers.dao.IClaimItemDao;
import com.ers.exception.ServiceException;
import com.ers.model.ClaimItem;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ClaimItemServiceImpl implements IClaimItemService {
    //add constructor to instantiate ClaimItemdao object

    private static final Logger logger=(Logger) LoggerFactory.getLogger(ClaimItemServiceImpl.class);
    private final IClaimItemDao claimItemDao;

    public ClaimItemServiceImpl() {
        this.claimItemDao = new ClaimItemDaoImpl();
    }
    public ClaimItemServiceImpl(IClaimItemDao claimItemDao) {
        this.claimItemDao = claimItemDao;
    }

    @Override
    public ClaimItem addClaimItem(ClaimItem claimItem){
        logger.info("Started ClaimItemServiceImpl.addClaimItem()");
        try{
            ClaimItem saved = claimItemDao.addClaimItem(claimItem);
            logger.info("Ending ClaimItemServiceImpl.addClaimItem()");
            return saved;
        }catch(Exception e){
            logger.error("ERROR at ClaimItemServiceImpl.addClaimItem()",e);
            throw new ServiceException("Unable to add claim item",e);
        }
    }

    @Override
    public boolean updateClaimItem(ClaimItem claimItem){
        logger.info("Started ClaimItemServiceImpl.updateClaimItem()");
        try {
            boolean updated = claimItemDao.updateClaimItem(claimItem);
            logger.info("Claim item update status={}", updated);
            return updated;
        } catch (Exception e) {
            logger.error("ERROR at ClaimItemServiceImpl.updateClaimItem()", e);
            throw new ServiceException("Unable to update claim item", e);
        }
    }

    @Override
    public ClaimItem getClaimItemById(int itemId) {
        logger.info("Started ClaimItemServiceImpl.getClaimItemById()");
        try {
            return claimItemDao.getClaimItemById(itemId);
        } catch (Exception e) {
            logger.error("ERROR at ClaimItemServiceImpl.getClaimItemById()", e);
            throw new ServiceException("Unable to get claim item", e);
        }
    }

    @Override
    public List<ClaimItem> getAllClaimItems() {
        logger.info("Started ClaimItemServiceImpl.getAllClaimItems()");
        try {
            return claimItemDao.getAllClaimItems();
        } catch (Exception e) {
            logger.error("ERROR at ClaimItemServiceImpl.getAllClaimItems()", e);
            throw new ServiceException("Unable to get claim items", e);
        }
    }

    @Override
    public boolean deleteClaimItemById(int itemId) {
        logger.info("Started ClaimItemServiceImpl.deleteClaimItemById()");
        try {
            boolean deleted = claimItemDao.deleteClaimItemById(itemId);
            logger.info("Claim item deletion status={}", deleted);
            return deleted;
        } catch (Exception e) {
            logger.error("ERROR at ClaimItemServiceImpl.deleteClaimItemById()", e);
            throw new ServiceException("Unable to delete claim item", e);
        }
    }

    @Override
    public List<ClaimItem> getClaimItemsByClaimId(int claimId) {
        logger.info("Started ClaimItemServiceImpl.getClaimItemsByClaimId()");
        try {
            return claimItemDao.getClaimItemsByClaimId(claimId);
        } catch (Exception e) {
            logger.error("ERROR at ClaimItemServiceImpl.getClaimItemsByClaimId()", e);
            throw new ServiceException("Unable to get claim items for claim", e);
        }
    }
}
