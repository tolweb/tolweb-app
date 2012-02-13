package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.Specimen;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

@Deprecated
public class SpecimenDAOImpl extends BaseDAOImpl implements SpecimenDAO {
    private EditHistoryDAO editHistoryDAO;
    private PageDAO pageDAO;
    private NodeDAO nodeDAO;
    
    public Specimen addSpecimen(MappedNode node, Contributor contr) {
        Specimen specimen = new Specimen();
        specimen.setNode(node);
        specimen.setEditHistory(getEditHistoryDAO().createAndReturnNewHistory(contr));
        saveSpecimen(specimen);
        return specimen;
    }
    
    public void saveSpecimens(List specimens) {
        getHibernateTemplate().saveOrUpdateAll(specimens);
    }

    public void saveSpecimen(Specimen specimen) {
        getHibernateTemplate().saveOrUpdate(specimen);
    }
    
    public void deleteSpecimenWithId(Long specimenId) {
        try {
            Specimen specimen = (Specimen) getHibernateTemplate().load(Specimen.class, specimenId);
            if (specimen != null) {
                getEditHistoryDAO().deleteHistoryWithId(specimen.getEditHistory().getId());
                getHibernateTemplate().delete(specimen);
            }
        } catch (Exception e) {
            // if it didn't exist it could raise, but then that's not much of a problem is it?
        	e.printStackTrace();
        }
    }
    
    public List getSpecimensOnPage(MappedPage page) {
        List nodeIds = getPageDAO().getNodeIdsOnPage(page);
        nodeIds.add(page.getMappedNode().getNodeId());
        List results = getHibernateTemplate().find("from org.tolweb.hibernate.Specimen where nodeId " + StringUtils.returnSqlCollectionString(nodeIds));
        return results;
    }
    
    public int getNumSpecimensAttachedToNodeIds(Collection nodeIds, int specimenType) {
    	String queryString = "select count(*) from org.tolweb.hibernate.Specimen where nodeId " + StringUtils.returnSqlCollectionString(nodeIds) + " and type=" + specimenType;
    	return getCountResultFromQuery(queryString);
    }
    
    public boolean getNodeHasSpecimens(MappedNode node, int type, boolean includeDescendants) {
    	List nodeIds = new ArrayList();
    	nodeIds.add(node.getNodeId());
    	if (includeDescendants) {
    		nodeIds.addAll(getNodeDAO().getDescendantIdsForNode(node.getNodeId()));
    	}
    	return getNumSpecimensAttachedToNodeIds(nodeIds, type) > 0;
    }    

    /**
     * @return Returns the editHistoryDAO.
     */
    public EditHistoryDAO getEditHistoryDAO() {
        return editHistoryDAO;
    }
    /**
     * @param editHistoryDAO The editHistoryDAO to set.
     */
    public void setEditHistoryDAO(EditHistoryDAO editHistoryDAO) {
        this.editHistoryDAO = editHistoryDAO;
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

	public NodeDAO getNodeDAO() {
		return nodeDAO;
	}

	public void setNodeDAO(NodeDAO nodeDAO) {
		this.nodeDAO = nodeDAO;
	}
}
