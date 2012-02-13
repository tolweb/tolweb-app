package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;

/**
 * A Hibernate Criteria-based implementation of {@link NodeDAO} that 
 * filters out, by default, "nodes" (instances of {@link MappedNode}) 
 * that have a 'status' indicating that they are not active.  The 
 * 'status' was originally implemented to signal active and inactive 
 * nodes, but could be used to filter nodes based on varying criteria 
 * (though that is not currently implement [as of 2008/04/02]).  The 
 * DAO was put in place to help move the project away from physical 
 * deletes when removing nodes.  The hope to was move to logical 
 * deletes that would merely deactivate the node and its' descendants 
 * (because only deactivating a target node would make the descendants   
 * unreachable when navigating the tree from the root or ancestor 
 * basal node). 
 * 
 * @author lenards
 * @see NodeDAOImpl NodeDAO
 * @since April 2, 2008 - added during implementation of Custom Taxa Import
 */
public class FilteredNodeDAOImpl extends NodeDAOImpl implements NodeDAO, FilteredNodeDAO {

	protected MappedNode getNodeWithIdAndStatus(Long id, Integer status) {
    	Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
		.add(Restrictions.conjunction()
				.add(Restrictions.eq("nodeId", id))
				.add(Restrictions.eq("status", status)));	
    	return (MappedNode) getFirstElementFromList(c.list());		
	}
	
    public MappedNode getNodeWithId(Long id) {
//        try {
//            return (MappedNode) getHibernateTemplate().load(org.tolweb.hibernate.MappedNode.class, id);
//        } catch (Exception e) {
//            return null;
//        }
    	return getNodeWithIdAndStatus(id, MappedNode.ACTIVE);
    }	
    
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#doNodeNameSearch(java.lang.String, java.lang.String, boolean, boolean, java.lang.String)
	 */
	protected Object doNodeNameSearch(String name, String delimiter, boolean returnCountOnly, boolean returnIdsOnly, String selectString) {
/*		Object result2 = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
									.createAlias("this", "node")
									.createAlias("node.synonyms", "syn")
									.add(Restrictions.eq("syn.name", name))
									.setProjection(Projections.property(selectString))
									.list();
 */	
		return super.doNodeNameSearch(name, delimiter, returnCountOnly, returnIdsOnly, selectString);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#getNodeNameSearchNodeNameQuery(java.lang.String, java.lang.String)
	 */
	protected String getNodeNameSearchNodeNameQuery(String selectString, String delimiter) {
		return "select " + selectString +  " from org.tolweb.hibernate.MappedNode as node where node.name " + delimiter +  " :name and node.status=0";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#getNodeNameSearchOtherNameQuery(java.lang.String, java.lang.String)
	 */
	protected String getNodeNameSearchOtherNameQuery(String selectString, String delimiter) {
		return "select " + selectString + " from org.tolweb.hibernate.MappedNode as node inner join node.synonyms as syn where syn.name " + delimiter + " :name and node.status=0";
	}
	
	public List getAllChildrenNodes(MappedNode parent) {
		return super.getChildrenNodes(parent.getNodeId(), false, null);
	}	
	
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#getChildrenNodes(java.lang.Long, boolean, java.lang.Long)
	 */
	public List getChildrenNodes(Long nodeId, boolean idOnly, Long pageId) {
		Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
								.addOrder(Order.asc("orderOnParent"));
		Junction condition = Restrictions.conjunction()
								.add(Restrictions.eq("parentNodeId", nodeId))
								.add(Restrictions.eq("status", MappedNode.ACTIVE));
		if (pageId != null) {
			condition = Restrictions.conjunction()
							.add(condition)
							.add(Restrictions.eq("pageId", pageId));
		}
		c = c.add(condition);

		if (idOnly) {
			c = c.setProjection(Projections.property("nodeId"));
		}
		
		return c.list();
	}
	
	public List getNodesWithIds(Collection ids, boolean includeInactiveNodes) {
		if (ids != null && ids.size() > 0) {
			Criterion cond; 
			if (includeInactiveNodes) {
				cond = Restrictions.in("nodeId", ids);
			} else {
				cond = Restrictions.conjunction()
							.add(Restrictions.in("nodeId", ids))
							.add(Restrictions.eq("status", MappedNode.ACTIVE));				
			}
				
			Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
							.add(cond)
							.setCacheable(true);
			return c.list();	
		} else {
			return new ArrayList();
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#fetchNodeCollectionWithIds(java.util.Collection, java.lang.String, java.lang.String)
	 */
	protected List fetchNodeCollectionWithIds(Collection ids, String selectString, String orderByString) {
		if (ids != null && ids.size() > 0) {
			Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
							.add(Restrictions.conjunction()
									.add(Restrictions.in("nodeId", ids))
									.add(Restrictions.eq("status", MappedNode.ACTIVE)))
							.setCacheable(true);
			if (StringUtils.notEmpty(orderByString)) {
				c = c.addOrder(determineOrderBy(orderByString));
			}
			if (StringUtils.notEmpty(selectString)) {
				c = c.setProjection(Projections.property(determineSelectProperty(selectString)));
			}
			return c.list();	
		} else {
			return new ArrayList();
		}
	}
	
    public Set getDescendantIdsForNode(Long nodeId, boolean filterInactive) {
    	if (filterInactive) {
	    	return getFilteredDescendantIdsForNode(nodeId);
    	}
    	return getDescendantIdsForNode(nodeId);
    }

	private Set getFilteredDescendantIdsForNode(Long nodeId) {
		List nodeIds = new ArrayList();
		nodeIds.add(nodeId);    	
		String sqlString = "select na.node_id from NODEANCESTORS na inner join NODES n ON n.node_id = na.node_id where na.ancestor_id in (" + 
			StringUtils.returnCommaJoinedString(nodeIds) + ") and n.`status` = 0 order by na.node_id asc";
		return getNodeIdsWithAncestors(sqlString);
	}	
	
	/**
	 * Determines if the string-defined query parameter should be an 
	 * implementation of an Ascending Order or Descending Order object. 
	 * @param orderByString the string passed in the base class (defined as a 
	 * string because the base class uses HQL queries)
	 * @return the correct Order object given the HQL order by syntax
	 */
	private Order determineOrderBy(String orderByString) {
		boolean isDesc = orderByString.endsWith("desc");
		if (orderByString.contains(".")) {
			orderByString = orderByString.substring(orderByString.indexOf(".")+1);
		}
		String[] pieces = orderByString.split(" ");
		return (isDesc) ? Order.desc(pieces[0]) : Order.asc(pieces[0]);
	}
	
	/**
	 * Determines if the property specified in string-defined query parameter. 
	 * @param selectString the string passed in the base class (defined as a 
	 * string because the base class uses HQL queries)
	 * @return the string-value of the property that will be used in the Projection
	 */
	private String determineSelectProperty(String selectString) {
		if (selectString.contains("select")) {
			selectString = selectString.substring(selectString.indexOf("select")+"select".length()+1);
		}
		
		if (selectString.contains(".")) {
			selectString = selectString.substring(selectString.indexOf(".")+1);
		}
		return selectString.trim();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#getNumChildrenForNodeId(java.lang.Long)
	 */
	public Integer getNumChildrenForNodeId(Long nodeId) {
		try {
			Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
									.add(Restrictions.conjunction()
											.add(Restrictions.eq("parentNodeId", nodeId))
											.add(Restrictions.eq("status", MappedNode.ACTIVE)))
									.setProjection(Projections.rowCount())
									.setCacheable(!getIsWorking());
			return (Integer) c.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			return Integer.valueOf(0);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#getNumChildrenForNodeId(java.lang.Long, boolean)
	 */
	public Integer getNumChildrenForNodeId(Long nodeId, boolean checkIncertaeSedis) {
		try {
			Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
									.setProjection(Projections.rowCount())
									.setCacheable(!getIsWorking() && !checkIncertaeSedis);
			Junction where = Restrictions.conjunction()
									.add(Restrictions.eq("parentNodeId", nodeId))
									.add(Restrictions.eq("status", MappedNode.ACTIVE));
			if (checkIncertaeSedis) {
				where = where.add(Restrictions.ne("confidence", Node.INCERT_UNSPECIFIED));
			}
			c = c.add(where);
			return (Integer) c.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			return Integer.valueOf(0);
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#getMaxChildOrderOnParent(java.lang.Long)
	 */
	public Integer getMaxChildOrderOnParent(Long nodeId) {
		List results = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
								.add(Restrictions.conjunction()
										.add(Restrictions.eq("parentNodeId", nodeId))
										.add(Restrictions.eq("status", MappedNode.ACTIVE)))
								.setProjection(Projections.max("orderOnParent"))
								.list();
		return (Integer) getFirstElementFromList(results);
	}

	/*
	 * This can't be overridden because the NodeAncestors table only exists in the Misc 
	 * table. We'd likely being attempting a Theta-join from Public to Misc.  I don't 
	 * believe this is possible.  The solution is when a node is "deactivated", it is 
	 * removed from the ancestor relations - and any descendant of a node being 
	 * "deactivated" will in turn be "deactivated" and, thus, also removed from the 
	 * NodeAncestors table.  So when fetches are done against this table - it will 
	 * already be filtered and not require a "join" to the NODES table to determine 
	 * the status.  
	 * 
	 * Sound good? 
	 *  
	 * @author lenards   
	 */
	protected Set getNodeAncestors(Long nodeId, boolean isCount) {
		// don't re-implement this method - okay? thanks
		return super.getNodeAncestors(nodeId, isCount);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#getNodeExistsWithId(java.lang.Long)
	 */
	public boolean getNodeExistsWithId(Long id) {
		List results = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
								.add(Restrictions.conjunction()
										.add(Restrictions.eq("nodeId", id))
										.add(Restrictions.eq("status", MappedNode.ACTIVE)))
								.setProjection(Projections.rowCount())
								.list();
		if (results != null) {
			Integer count = (Integer)results.get(0);
			return count.intValue() == 1;
		} else {
			return false;
		}
	}

	public boolean getActiveNodeExistsWithId(Long id) {
		return getNodeExistsWithId(id, false);
	}
	
	public boolean getNodeExistsWithIdUnfiltered(Long id) {
		return getNodeExistsWithId(id, true);
	}
	
	protected boolean getNodeExistsWithId(Long id, boolean includeInactiveNodes) {
		Junction whereClause; 
		if (includeInactiveNodes) {
			whereClause = Restrictions.conjunction().add(Restrictions.eq("nodeId", id));
		} else {
			whereClause = Restrictions.conjunction()
							.add(Restrictions.eq("nodeId", id))
							.add(Restrictions.eq("status", MappedNode.ACTIVE));
		}
		List results = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
								.add(whereClause)
								.setProjection(Projections.rowCount())
								.list();
		if (results != null) {
			Integer count = (Integer)results.get(0);
			return count.intValue() == 1;
		} else {
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#getNodeWithName(java.lang.String, java.lang.Long)
	 */
    public MappedNode getNodeWithName(String name, Long pageId) {
/*
    	String queryString = "from org.tolweb.hibernate.MappedNode where name='" + name + "' and pageId=" + pageId;
    	return (MappedNode) getFirstObjectFromQuery(queryString);
 */    	
    	List results = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
    							.add(Restrictions.conjunction()
    									.add(Restrictions.eq("status", MappedNode.ACTIVE))
    									.add(Restrictions.eq("name", name))
    									.add(Restrictions.eq("pageId", pageId)))
    							.list();
    	
    	return (MappedNode)getFirstElementFromList(results);
    }
    
    /*
     * (non-Javadoc)
     * @see org.tolweb.dao.NodeDAOImpl#getNodeWithNameAndParent(java.lang.String, java.lang.Long)
     */
    public MappedNode getNodeWithNameAndParent(String name, Long parentNodeId) {
/*
    	String queryString = "from org.tolweb.hibernate.MappedNode where name='" + name + "' and parentNodeId=" + parentNodeId;
    	return (MappedNode) getFirstObjectFromQuery(queryString);    	
 */    	
    	List results = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)    							
    							.add(Restrictions.conjunction()
									.add(Restrictions.eq("status", MappedNode.ACTIVE))
									.add(Restrictions.eq("name", name))
									.add(Restrictions.eq("parentNodeId", parentNodeId)))
								.list();
    	return (MappedNode)getFirstElementFromList(results);
    }
    
    /*
     * (non-Javadoc)
     * @see org.tolweb.dao.NodeDAOImpl#getNodesWithNames(java.util.List, java.lang.Long)
     */
    public List getNodesWithNames(List nodeNames, Long parentNodeId) {
/*
    	String queryString = "select name from org.tolweb.hibernate.MappedNode where name " + 
    		StringUtils.returnSqlCollectionString(nodeNames, true) + " and parentNodeId=" + parentNodeId;
    	return getHibernateTemplate().find(queryString);

 */
    	Criteria c = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
    							.add(Restrictions.conjunction()
    									.add(Restrictions.in("name", nodeNames))
    									.add(Restrictions.eq("parentNodeId", parentNodeId)))
    									.add(Restrictions.eq("status", MappedNode.ACTIVE))
    							.setProjection(Projections.property("name"));
    	return c.list();
    }

    /*
     * (lenards) It's my opinion that this method will not need to be overridden because the   
     * query is executed with descendent-id information and that is fetched with filtered  
     * calls (or should be now) and thus inactive nodes should be make it into the business  
     * and presentation layers unless explicitly fetched through unfiltered channels.
     * 
     * (non-Javadoc)
     * @see org.tolweb.dao.NodeDAOImpl#getDescendantsForTabbedExport(java.lang.Long, java.util.Collection)
     */
	public List getDescendantsForTabbedExport(Long nodeId, Collection descendantIds) {
		return super.getDescendantsForTabbedExport(nodeId, descendantIds);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.tolweb.dao.NodeDAOImpl#getMinimalNodeInfoForNodeId(java.lang.Long)
	 */
	public Object[] getMinimalNodeInfoForNodeId(Long nodeId) {
/*
    	String queryString = "select name, extinct, confidence, phylesis, isLeaf, description from " + 
    		"org.tolweb.hibernate.MappedNode where nodeId=" + nodeId;
    	return (Object[]) getFirstObjectFromQuery(queryString);
 */		
		List results = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
								.add(Restrictions.conjunction()
										.add(Restrictions.eq("nodeId", nodeId))
										.add(Restrictions.eq("status", MappedNode.ACTIVE)))
								.setProjection(Projections.projectionList()
										.add(Projections.property("name"))
										.add(Projections.property("extinct"))
										.add(Projections.property("confidence"))
										.add(Projections.property("phylesis"))
										.add(Projections.property("isLeaf"))
										.add(Projections.property("description")))
								.list();
		return (Object[]) getFirstElementFromList(results);
	}

	public List<MappedNode> getAllInactiveNodes() {
		List result = getSession().createCriteria(org.tolweb.hibernate.MappedNode.class)
							.addOrder(Order.asc("nodeId"))
							.add(Restrictions.eq("status", MappedNode.INACTIVE))
							.list();
		return result;
	}

	public MappedNode getInactiveNodeWithId(Long id) {
		return getNodeWithIdAndStatus(id, MappedNode.INACTIVE);
	}

	public MappedNode getNodeWithId(Long id, boolean includeInactiveNodes) {
		return (includeInactiveNodes) ? super.getNodeWithId(id) : getNodeWithId(id);
	}
	
    public String getNameForNodeWithId(Long id) {
        //return (String) getHibernateTemplate().find("select n.name from org.tolweb.hibernate.MappedNode n where n.nodeId=" + id).get(0);
        return super.getNameForNodeWithId(id);
    }
}