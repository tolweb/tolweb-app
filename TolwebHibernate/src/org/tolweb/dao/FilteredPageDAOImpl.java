package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.StringUtils;

public class FilteredPageDAOImpl extends PageDAOImpl implements PageDAO {

    public boolean getNodeHasPage(Long nodeId) {
    	List results = getSession().createCriteria(org.tolweb.hibernate.MappedPage.class)
    		    				.createAlias("mappedNode", "n")
	    						.setFetchMode("n", FetchMode.JOIN)    	
	    						.add(Restrictions.conjunction()
    								.add(Restrictions.eq("n.nodeId", nodeId))
    								.add(Restrictions.eq("n.status", MappedNode.ACTIVE)))
    							.setProjection(Projections.rowCount())
    							.list();
    	Integer count = (Integer)getFirstElementFromList(results);
    	if (count == null) {
    		return false;
    	} else {
    		return count.intValue() > 0;
    	}
    }
    
    public Collection getNodeIdsWithPages(Collection nodeIds) {
    	if (nodeIds == null || nodeIds.isEmpty()) {
    		return null;
    	}
    	Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedPage.class)
    		    				.createAlias("mappedNode", "n")
	    						.setFetchMode("n", FetchMode.JOIN)
    							.add(Restrictions.conjunction()
    									.add(Restrictions.in("n.nodeId", nodeIds))
    									.add(Restrictions.eq("n.status", MappedNode.ACTIVE)))
    							.setProjection(Projections.property("mappedNode.nodeId"))
    							.setCacheable(true);
        return c.list();
    }	
	
    public Long getPageIdNodeIdIsOn(Long nodeId) {
    	List results = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
    						.add(Restrictions.conjunction()
    								.add(Restrictions.eq("nodeId", nodeId))
    								.add(Restrictions.eq("status", MappedNode.ACTIVE)))
    						.setProjection(Projections.property("pageId"))
    						.list();
        return (Long)getFirstElementFromList(results);
    }    
    
    public MappedNode getNodeForPageNodeIsOn(MappedNode nd) {
    	Long id = nd.getPageId();
    	List results; 
    	try {
	    	Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedPage.class)
	    						.createAlias("mappedNode", "n")
	    						.setFetchMode("n", FetchMode.JOIN)
	    						.add(Restrictions.eq("pageId", id))
	    						.setCacheable(true)
	    						.setProjection(Projections.property("mappedNode"));
	    	results = c.list();
    	} catch (Exception e) {
    		results = new ArrayList();
    		e.printStackTrace();
    	}
        return (MappedNode)getFirstElementFromList(results);
    }    
    
    protected Object getPageForNode(Long nodeId, boolean onlyId) {
    	List results; 
    	try {
    		// at this point I'm not sure we need to filter out pages because we won't come upon them if the filter-node-dao is doing it's job    		
    		Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedPage.class)
	    						.createAlias("mappedNode", "n")
	    						.setFetchMode("n", FetchMode.JOIN)
	    						.add(Restrictions.eq("n.nodeId", nodeId))
    							.setCacheable(!getIsWorking());
    		
    		if (onlyId) {
    			c = c.setProjection(Projections.property("pageId"));
    		}
    		results = c.list();
    	} catch (Exception e) {
    		results = new ArrayList();
    		e.printStackTrace();
    	}
        return getFirstElementFromList(results);
    }    
    
    protected List getChildPages(Long pgId, boolean onlyNamesIds) {
    	Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedPage.class)
	    						.createAlias("mappedNode", "n")
	    						.setFetchMode("n", FetchMode.JOIN)    							
    							.addOrder(Order.asc("n.orderOnPage"))
    							.add(Restrictions.conjunction()
    									.add(Restrictions.eq("parentPageId", pgId))
    									.add(Restrictions.eq("n.status", MappedNode.ACTIVE)));
    	
    	if (onlyNamesIds) {
    		c = c.setProjection(Projections.projectionList()
    				.add(Projections.property("groupName"))
    				.add(Projections.property("pageId"))
    				.add(Projections.property("n.nodeId")));
    	}
    	List results = c.list();
    	if (!onlyNamesIds) {
    		fillOutInMemoryValuesForAll(results);
    	}
        return results;
    }    
    
    public List getNodesOnPage(Long pageId, boolean onlyNamed, boolean onlyIds, boolean orderByOrderOnParent, String additionalQuery) {
    	Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
    						.add(Restrictions.conjunction()
    								.add(Restrictions.eq("this.pageId", pageId))
    								.add(Restrictions.eq("this.status", MappedNode.ACTIVE)));
    	if (onlyNamed) {
    		c = c.add(Restrictions.ne("this.name", ""))
    			 .addOrder(Order.asc("this.orderOnParent"));
    	} else if (orderByOrderOnParent) {
    		c = c.addOrder(Order.asc("this.orderOnParent"));
    	} else if (StringUtils.notEmpty(additionalQuery)) {
    		Criterion additional = determineAdditionalQueryForNodesOnPage(additionalQuery);
    		if (additional != null) {
    			c = c.add(additional);
    		}
    	}
    	
    	if (onlyIds) {
    		c = c.setProjection(Projections.property("this.nodeId"));
    	}
    	
        return c.list();
    }

    protected Criterion determineAdditionalQueryForNodesOnPage(String additionalQuery) {
    	// use a simple 'split on equal-sign' approach for now and change to more complex if needed
    	additionalQuery = additionalQuery.replace("node.", "");
    	String[] pieces = additionalQuery.split("=");
    	if (pieces.length == 2) {
    		return Restrictions.sqlRestriction(pieces[0] + " = ?", convertStringToBoolean(pieces[1]), Hibernate.BOOLEAN);
    	} else {
    		return null;
    	}
    }

    /**
     * Converts an arcane string representation of a boolean into a Boolean type. 
     * 
     * This arcane representation of "1" being true and "0" being false is not 
     * supported by the Boolean type's constructor that accepts a string. Anything   
     * other than the string literal 'true' passed to the constructor is thought 
     * to be false.  So either "1" or "0" gets you false - this is implemented to 
     * preserve the arcane representation that exists in the ToL databases.  Find 
     * fault with its' creator (though this is just a bandage of sorts).
     *  
     * @param value a string representing the internal database convention of 
     * representing booleans as integer, so true as 1 and false as 0
     * @return true if the value is "1", otherwise false
     */
    private Boolean convertStringToBoolean(String value) {
    	if (StringUtils.isEmpty(value)) return Boolean.FALSE;
    	return "1".equals(value) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public String getPageTypeForPageWithId(int id) {
    	Long pageId = Long.valueOf(id);
    	List result = getSession().createCriteria(org.tolweb.hibernate.MappedPage.class)
							.createAlias("mappedNode", "n")
							.setFetchMode("n", FetchMode.JOIN)
							.add(Restrictions.conjunction()
									.add(Restrictions.eq("id", pageId))
									.add(Restrictions.eq("n.status", MappedNode.ACTIVE)))
							.setProjection(Projections.property("n.isLeaf"))
							.list();
    	Boolean isLeaf = (Boolean)getFirstElementFromList(result);
    	if (isLeaf != null) {
    		if (isLeaf.booleanValue()) {
    			return "Leaf";
    		} else {
    			return "Branch";
    		}
    	} else {
    		return "";
    	}
    }    
    
    public List<Object[]> getNodeIdsAndEditHistoryIds() {
    	Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedPage.class)
    								.createAlias("mappedNode", "n")
    								.setFetchMode("n", FetchMode.JOIN)
    								.setProjection(Projections.projectionList()
    										.add(Projections.property("n.nodeId"))
    										.add(Projections.property("editHistoryId")));
    	return c.list();
    }    
    
    public int getMaxOrderOnPage(Long pageId) {
    	Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedPage.class)
								.createAlias("mappedNode", "n")
								.setFetchMode("n", FetchMode.JOIN)
								.add(Restrictions.conjunction()
										.add(Restrictions.eq("id", pageId))
										.add(Restrictions.eq("n.status", MappedNode.ACTIVE)))
								.setProjection(Projections.max("n.orderOnPage"));
    	Number maxOrderOnPage = (Number)getFirstElementFromList(c.list());
    	return maxOrderOnPage.intValue();
    }    
    
	public List<Object[]> getPageIdsAndNodeIdsForPages(Collection ancestorIds) {
    	Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedPage.class)
								.createAlias("mappedNode", "n")
								.setFetchMode("n", FetchMode.JOIN)
								.add(Restrictions.conjunction()
										.add(Restrictions.in("id", ancestorIds))
										.add(Restrictions.eq("n.status", MappedNode.ACTIVE)))
								.setProjection(Projections.projectionList()
										.add(Projections.property("id"))
										.add(Projections.property("n.nodeId")));
		return c.list();
	}    
}
