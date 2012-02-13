/*
 * Created on Apr 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.Iterator;
import java.util.List;

import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchComparerImpl implements BatchComparer {
    private PageDAO workingPageDAO;
    private PageDAO publicPageDAO;
    private NodeDAO workingNodeDAO;
    private NodeDAO publicNodeDAO;

    /* (non-Javadoc)
     * @see org.tolweb.treegrowserver.BatchComparer#compareWorkingBatchToPublicVersion(org.tolweb.treegrowserver.UploadBatch)
     */
    public String compareWorkingBatchToPublicVersion(UploadBatch batch) {
        String changelog = "";
        for (Iterator iter = batch.getUploadedPagesSet().iterator(); iter.hasNext();) {
            UploadPage up = (UploadPage) iter.next();
            MappedPage workingPage = workingPageDAO.getPageWithId(up.getPageId());
            if (workingPage != null) {
                MappedPage publicPage = publicPageDAO.getPageWithId(up.getPageId());
                if (publicPage == null) {
                    changelog = addChange(changelog, "New page in working: " + workingPage.getGroupName());
                } else {
                    // Check the nodes for both pages
                    List workingNodes = workingPageDAO.getNodesOnPage(workingPage);
                    List publicNodes = publicPageDAO.getNodesOnPage(publicPage);
                    int index = 0;
                    for (Iterator iterator = workingNodes.iterator(); iterator
                    .hasNext();) {
                        MappedNode workingNode = (MappedNode) iterator.next();
                        MappedNode publicNode;
                        try {
                            publicNode = (MappedNode) publicNodes.get(index++);
                            if (!workingNode.getNodeId().equals(publicNode.getNodeId())) {
                                throw new RuntimeException("");
                            }
                        } catch (Exception e) {
                            changelog = addChange(changelog, "New node in working: " + workingNode.getName());
                            break;
                        }
                        String nodeValueComparison = workingNode.compareValues(publicNode);
                        if (nodeValueComparison != null) {
                            changelog = addChange(changelog, nodeValueComparison);
                        }                        
                    }
                    String pageValueComparison = workingPage.compareValues(publicPage);
                    changelog = addChange(changelog, pageValueComparison);
                }

            }
        }
        return changelog;
    }
    
    private String addChange(String changeLog, String change) {
        changeLog += change + "\n";
        return changeLog;
    }

    /**
     * @return Returns the publicNodeDAO.
     */
    public NodeDAO getPublicNodeDAO() {
        return publicNodeDAO;
    }
    /**
     * @param publicNodeDAO The publicNodeDAO to set.
     */
    public void setPublicNodeDAO(NodeDAO publicNodeDAO) {
        this.publicNodeDAO = publicNodeDAO;
    }
    /**
     * @return Returns the publicPageDAO.
     */
    public PageDAO getPublicPageDAO() {
        return publicPageDAO;
    }
    /**
     * @param publicPageDAO The publicPageDAO to set.
     */
    public void setPublicPageDAO(PageDAO publicPageDAO) {
        this.publicPageDAO = publicPageDAO;
    }
    /**
     * @return Returns the workingNodeDAO.
     */
    public NodeDAO getWorkingNodeDAO() {
        return workingNodeDAO;
    }
    /**
     * @param workingNodeDAO The workingNodeDAO to set.
     */
    public void setWorkingNodeDAO(NodeDAO workingNodeDAO) {
        this.workingNodeDAO = workingNodeDAO;
    }
    /**
     * @return Returns the workingPageDAO.
     */
    public PageDAO getWorkingPageDAO() {
        return workingPageDAO;
    }
    /**
     * @param workingPageDAO The workingPageDAO to set.
     */
    public void setWorkingPageDAO(PageDAO workingPageDAO) {
        this.workingPageDAO = workingPageDAO;
    }
}
