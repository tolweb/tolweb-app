package org.tolweb.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.tolweb.hibernate.EditHistory;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public class EditHistoryDAOImpl extends BaseDAOImpl implements
        EditHistoryDAO {
    private ContributorDAO contributorDAO;
    private PageDAO pageDAO;
    public void deleteHistoryWithId(Long id) {
        try {
            getHibernateTemplate().delete(getEditHistoryWithId(id));
        } catch (Exception e) {
            // if it didn't exist, that's fine
            e.printStackTrace();
        }
    }
    /**
     * Updates the last edited date for the history as well as the lock
     * date
     */
    public void updateLastEdited(Long historyId, Contributor contributor) {
        EditHistory history = getEditHistoryWithId(historyId);
        if (contributor != null) {
        	history.setLastEditedContributorId((long) contributor.getId());
        	history.setLastEditedDate(new Date());
            updateLock(history, contributor);        	
        } else {
        	history.setLastEditedContributorId(null);
        	history.setLastEditedDate(null);
        	history.setLockedContributorId(null);
        	history.setLockedDate(null);
        	getHibernateTemplate().saveOrUpdate(history);
        }
    }
    public void updateLock(Long historyId, Long contributorId) {
        updateLock(historyId, getContributorDAO().getContributorWithId(contributorId.toString()));
    }
    public void updateLock(Long historyId, Contributor contributor) {
        updateLock(getEditHistoryWithId(historyId), contributor);
    }
    private void updateLock(EditHistory history, Contributor contributor) {
        history.setLockedContributorId((long)contributor.getId());
        history.setLockedDate(new Date());
        getHibernateTemplate().saveOrUpdate(history);        
    }
    public void clearAllLocksForContributor(final Contributor contributor) {
        String queryString = getClearLockPrefixString() + " where lockedContributorId=" + contributor.getId(); 
        executeUpdateQuery(queryString);
    }
    public void clearLock(final Long historyId) {
        String queryString = getClearLockPrefixString() + " where id=" + historyId;
        executeUpdateQuery(queryString);
    }
    public Long createNewHistory(Contributor contributor) {
        return createAndReturnNewHistory(contributor).getId();
    }    
    public EditHistory createAndReturnNewHistory(Contributor contributor) {
        EditHistory history = new EditHistory();
        history.setCreatedContributorId(Long.valueOf(contributor.getId()));
        history.setCreationDate(new Date());
        getHibernateTemplate().saveOrUpdate(history);
        return history;
    }
    public EditHistory getEditHistoryWithId(Long historyId) {
        return (EditHistory) getHibernateTemplate().load(EditHistory.class, historyId);
    }
    private String getClearLockPrefixString() {
        return "update org.tolweb.hibernate.EditHistory set lockedContributorId=NULL, lockedDate=NULL";
    }
    public void clearAllLocksForPages(Collection pageIds) {
        List editHistoryIds = getPageDAO().getEditHistoryIdsForPageIds(pageIds);
        String updateString = getClearLockPrefixString() + " where id " + StringUtils.returnSqlCollectionString(editHistoryIds); 
        executeUpdateQuery(updateString);
    }    
    /**
     * @return Returns the contributorDAO.
     */
    public ContributorDAO getContributorDAO() {
        return contributorDAO;
    }
    /**
     * @param contributorDAO The contributorDAO to set.
     */
    public void setContributorDAO(ContributorDAO contributorDAO) {
        this.contributorDAO = contributorDAO;
    }
    /**
     * @return Returns the pageDAO.
     */
    public PageDAO getPageDAO() {
        return pageDAO;
    }
    /**
     * @param pageDAO The pageDAO to set.
     */
    public void setPageDAO(PageDAO pageDAO) {
        this.pageDAO = pageDAO;
    }
}
