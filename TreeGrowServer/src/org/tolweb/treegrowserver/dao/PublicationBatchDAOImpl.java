package org.tolweb.treegrowserver.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.tolweb.dao.BaseDAOImpl;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrowserver.PublicationBatch;
import org.tolweb.treegrowserver.SubmittedPage;

public class PublicationBatchDAOImpl extends BaseDAOImpl implements
        PublicationBatchDAO {
    private PageDAO pageDAO;
    private PermissionChecker permissionChecker;
    
    public void saveBatch(PublicationBatch batch) {
        getHibernateTemplate().saveOrUpdate(batch);
    }
    public PublicationBatch getBatchWithId(Long id) {
    	try {
    		return (PublicationBatch) getHibernateTemplate().load(PublicationBatch.class, id);
    	} catch (Exception e) {
    		return null;
    	}
    }
    public PublicationBatch getBatchForSubmittedNode(Long nodeId) {
        // get the page id for the node and see if it was
        // part of a recently submitted batch
        Long pageId = getPageDAO().getPageIdForNodeId(nodeId);
        if (pageId == null) {
            // get the id of the page that the node sits on since it 
            // doesn't have it's own page
            pageId = getPageDAO().getPageIdNodeIdIsOn(nodeId);
        }
        List batches = getHibernateTemplate().find("from org.tolweb.treegrowserver.PublicationBatch b join b.submittedPages as page where b.isClosed=0 and page.pageId=" + pageId);
        if (batches != null && batches.size() > 0) {
            return (PublicationBatch) batches.get(0);
        } else {
            return null;
        }
    }
    public boolean getContributorCanPublishBatch(Contributor contr, PublicationBatch batch) {
        for (Iterator iter = batch.getSubmittedPages().iterator(); iter.hasNext();) {
            SubmittedPage nextPage = (SubmittedPage) iter.next();
            if (getPermissionChecker().checkHasEditingPermissionForNode(contr, getPageDAO().getNodeIdForPage(nextPage.getPageId()))) {
                return true;
            }
        }
        return false;
    }
    public List<PublicationBatch> getOpenPublicationBatches() {
    	return getHibernateTemplate().find("from org.tolweb.treegrowserver.PublicationBatch where isClosed=0");
    }
	public Date getLastPublishedDateForNode(Long nodeId) {
		Long pageId = getPageDAO().getPageIdForNodeId(nodeId);
		return getLastPublishedDateForPage(pageId);
	}
	public Date getLastPublishedDateForPage(Long pageId) {
		String queryString = "select b.decisionDate from org.tolweb.treegrowserver.PublicationBatch b join b.submittedPages as page where b.wasPublished=1 and page.pageId=" 
			+ pageId + " order by b.decisionDate desc limmit 1";
		return (Date) getFirstObjectFromQuery(queryString);
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
    /**
     * @return Returns the permissionChecker.
     */
    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }
    /**
     * @param permissionChecker The permissionChecker to set.
     */
    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }
}
