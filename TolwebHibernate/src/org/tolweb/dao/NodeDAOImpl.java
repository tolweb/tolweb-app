/*
 * NodeDAOImpl.java
 *
 * Created on May 4, 2004, 3:13 PM
 */

package org.tolweb.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.tolweb.btol.AdditionalFields;
import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;

/**
 *
 * @author  dmandel
 */
public class NodeDAOImpl extends WorkingAwareDAO implements NodeDAO {
	public void addNodeWithId(Long id) {
		addObjectWithId(id, "NODES", "node_id");
	}
	
    public Integer getNumNodesNamed(String name) {
        name = getLikeName(name);
        return (Integer) doNodeNameSearch(name, "like", true, false);
    }
    
    public Integer getNumNodesExactlyNamed(String name) {
        return (Integer) doNodeNameSearch(name, "=", true, false);
    }
    
    public List findNodesNamed(String name) {
        return findNodesNamed(name, false);
    }

    public List findNodesNamedStartingWith(String name) {
        String selectString = getQueryKeyForNodeNames(false, false);
    	name = getLikeName(name, false, true);
        return (List) doNodeNameSearch(name, "like", false, false, selectString);
    }

    public List findNodesNamedEndingWith(String name) {
        String selectString = getQueryKeyForNodeNames(false, false);
    	name = getLikeName(name, true, false);
        return (List) doNodeNameSearch(name, "like", false, false, selectString);
    }
    
    public List findNodesNamed(String name, boolean idOnly) {
        String selectString = getQueryKeyForNodeNames(false, idOnly);
    	name = getLikeName(name);
        return (List) doNodeNameSearch(name, "like", false, idOnly, selectString);
    }
    
    public List<Object[]> findNodeNamesParentIdsAndIdsExactlyNamed(String name) {
    	return (List<Object[]>) doNodeNameSearch(name, "=", false, false, "node.name, node.nodeId, node.parentNodeId");
    }
    
    public List<Object[]> findNodeNamesParentIdsAndIdsNamed(String name) {
    	return (List<Object[]>) doNodeNameSearch(getLikeName(name), "like", false, false, "node.name, node.nodeId, node.parentNodeId");
    }
    
    protected String getLikeName(String originalName) {
    	return getLikeName(originalName, true, true);
    }
    
    protected String getLikeName(String originalName, boolean starting, boolean ending) {
    	if (StringUtils.notEmpty(originalName)) {
    		return (starting ? "%" : "") + originalName.trim() + (ending ? "%" : "");
    	} else {
    		return "";
    	}
    }
    
    public MappedNode getFirstNodeExactlyNamed(String name) {
    	List results = findNodesExactlyNamed(name);
    	if (results != null && results.size() > 0) {
    		return (MappedNode) results.get(0);
    	} else {
    		return null;
    	}
    }
    public List findNodesExactlyNamed(String name) {
        return findNodesExactlyNamed(name, false);
    }
    
    public List findNodesExactlyNamed(String name, boolean idOnly) {
        return (List) doNodeNameSearch(name, "=", false, idOnly);
    }
    
    protected Object doNodeNameSearch(String name, String delimiter, boolean returnCountOnly, boolean returnIdOnly) {
    	String selectString = getQueryKeyForNodeNames(returnCountOnly, returnIdOnly);
        return doNodeNameSearch(name, delimiter, returnCountOnly, returnIdOnly, selectString);
    }
    
    protected Object doNodeNameSearch(String name, String delimiter, boolean returnCountOnly, boolean returnIdsOnly, String selectString) {
        if (name != null) {
            name = name.trim();
        }  
        String nodeNameQuerySelect = getNodeNameSearchNodeNameQuery(selectString, delimiter);
        String otherNameQuerySelect = getNodeNameSearchOtherNameQuery(selectString, delimiter);
        // selectString names to have 'syn.' added to the node select
        //String otherNameQuerySelect = "select " + selectString + " from org.tolweb.hibernate.MappedOtherName as syn where syn.name " + delimiter + " :name";
        // AJL - limit the query result
        Object result1 = doNameQuery(nodeNameQuerySelect, name, returnCountOnly, Integer.valueOf(300));
        Object result2 = doNameQuery(otherNameQuerySelect, name, returnCountOnly, Integer.valueOf(300));
        if (returnCountOnly) {
            int intResult1 = ((Integer) result1).intValue();
            int intResult2 = ((Integer) result2).intValue();
            return Integer.valueOf(intResult1 + intResult2);
        } else {
	        Set hashSet = new HashSet();
	        List list1 = (List) result1;
	        List list2 = (List) result2;
	        boolean isNodes = (list1 != null && list1.size() > 0 && MappedNode.class.isInstance(list1.get(0))) ||
	        	(list2 != null && list2.size() > 0 && MappedNode.class.isInstance(list2.get(0)));
	        if (isNodes) {
	            Hashtable hash = new Hashtable();
		        addNodesToHash((List) result1, hash);
		        addNodesToHash((List) result2, hash);
		        return new ArrayList(hash.values());
	        } else {
	            hashSet.addAll((List) result1);
	            hashSet.addAll((List) result2);
	            return new ArrayList(hashSet);
	        }
        }
    }
    
    protected String getNodeNameSearchNodeNameQuery(String selectString, String delimiter) {
    	return "select " + selectString +  " from org.tolweb.hibernate.MappedNode as node where node.name " + delimiter +  " :name";
    }
    
    protected String getNodeNameSearchOtherNameQuery(String selectString, String delimiter) {
    	return "select " + selectString + " from org.tolweb.hibernate.MappedNode as node inner join node.synonyms as syn where syn.name " + delimiter + " :name";
    }
    
    protected String getQueryKeyForNodeNames(boolean returnCountOnly, boolean returnIdsOnly) {
        if (returnCountOnly) {
            return "count (*) ";
        } else if (returnIdsOnly) {
            return "distinct node.nodeId ";
        } else {
            return "distinct node "; 
        }
    }
    
    /**
     * Perform a name query. 
     * 
     * This retains the method signature used throughout the current 
     * implementation.  A desire to restrict the number of queries means 
     * that a new version of the method was created.  This is to support 
     * the tapestry SearchPage implementation. 
     * 
     * @param queryString the HQL query to execute as a name search
     * @param name the name to search for
     * @param returnCountOnly indicates if only the count of the results is needed
     * @return a unique result or a list of results
     */
    protected Object doNameQuery(String queryString, String name, boolean returnCountOnly) {
    	return doNameQuery(queryString, name, returnCountOnly, null);
    }
    
    /**
     * Perform a name query with optional query results limit. 
     * 
     * @param queryString the HQL query to execute as a name search
     * @param name the name to search for
     * @param returnCountOnly indicates if only the count of the results is needed
     * @param queryLimit define a limit to the number of results returns from a query
     * @return a unique result or a list of results
     */
    protected Object doNameQuery(String queryString, String name, boolean returnCountOnly, Integer queryLimit) {
        Object returnObject;
        try {
            Session session = getSession();
            Query nameQuery = session.createQuery(queryString);
            nameQuery.setString("name", name);
            nameQuery.setCacheable(!getIsWorking());
            if (queryLimit != null) {
            	nameQuery.setMaxResults(queryLimit.intValue());
            }
            if (!returnCountOnly) {
                returnObject = nameQuery.list();
            } else {
                returnObject = nameQuery.uniqueResult();
            }
        } catch (Exception e) {
            returnObject = new ArrayList();
            e.printStackTrace();
        }
        return returnObject;
    }
    
    protected void addNodesToHash(List list, Hashtable hash) {
    	if (list != null && list.size() > 0 && MappedNode.class.isInstance(list.get(0))) {
	        Iterator it = list.iterator();
	        while (it.hasNext()) {
	            MappedNode node = (MappedNode) it.next();
	            hash.put(node.getNodeId(), node);
	        }
    	}
    }

    public List getChildrenNodeIds(MappedNode node) {
    	return getChildrenNodes(node, false, true);
    }
    
    public List getChildrenNodes(MappedNode node) {
        return getChildrenNodes(node, false, false);
    }
    
	public List getAllChildrenNodes(MappedNode parent) {
		return getChildrenNodes(parent);
	}    
    
    /**
     * Returns the children nodes for a node
     * @param samePage Only return those nodes that fall on the same page
     * @param node The node to return the children for
     */
    public List getChildrenNodes(MappedNode node, boolean samePage, boolean idOnly) {
        return getChildrenNodes(node.getNodeId(), idOnly, (samePage) ? node.getPageId() : null);
    }
    
    public List getChildrenNodeIds(Long nodeId) {
        return getChildrenNodes(nodeId, true, null);
    }
    
    public List getChildrenNodes(Long nodeId, boolean idOnly, Long pageId) {
        String selectString = "";
        if (idOnly) {
            selectString = "select node.nodeId ";
        }
        String pageIdString = "";
        if (pageId != null) { 
        	// if a page-id has been passed then we want only those nodes that fall on the same page 
        	pageIdString = " and node.pageId=" + pageId;
        }
        return getHibernateTemplate().find(selectString + "from org.tolweb.hibernate.MappedNode as node where node.parentNodeId=" + nodeId + pageIdString + " order by node.orderOnParent");        
    }
    
    public MappedNode getNodeWithId(Long id) {
        try {
            return (MappedNode) getHibernateTemplate().load(org.tolweb.hibernate.MappedNode.class, id);
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            return null;
        }
    }
    
    public MappedNode getNodeWithId(Long id, boolean includeInactiveNodes) {
    	return getNodeWithId(id);
    }
    
    public List getNodesWithIds(Collection ids) {
    	return fetchNodeCollectionWithIds(ids, "", null);
    }
    
    // only here to comply with the NodeDAO contract - real implementation is in the Filter version of this
    public List getNodesWithIds(Collection ids, boolean includeInactiveNodes) {
    	return getNodesWithIds(ids);
    }
    
    public List getNodeNamesWithIds(Collection ids) {
    	return fetchNodeCollectionWithIds(ids, "select n.name ", null);
    }
    public String getNodeNameWithId(Long id) {
    	List names = getNodeNamesWithIds(Arrays.asList(new Long[] {id}));
    	if (names != null && names.size() > 0) {
    		return (String) names.get(0);
    	} else {
    		return null;
    	}
    }
	public List getNodeIdsWithIds(Collection ids) {
		return fetchNodeCollectionWithIds(ids, "select n.nodeId ", null);
	}    
    protected List fetchNodeCollectionWithIds(Collection ids, String selectString, String orderByString) {
    	if (ids != null && ids.size() > 0) {
	    	String queryString = selectString + "from org.tolweb.hibernate.MappedNode n where n.nodeId " + StringUtils.returnSqlCollectionString(ids);
	    	if (StringUtils.notEmpty(orderByString)) {
	    		queryString += " order by " + orderByString;
	    	}
	    	Query query = getSession().createQuery(queryString);
	    	query.setCacheable(true);
	        return query.list();
    	} else {
    		return new ArrayList();
    	}
    }
    
    public MappedNode getParentNodeForNode(MappedNode nd) {
        return getNodeWithId(nd.getParentNodeId());
    }
    
    public Integer getNumChildrenForNode(MappedNode nd) {
    	return getNumChildrenForNodeId(nd.getNodeId());
    }
    
    public Integer getNumChildrenForNodeId(Long nodeId) {
        try {
            Session session = getSession();
            Query nameQuery = session.createQuery("select count(*) from org.tolweb.hibernate.MappedNode n where n.parentNodeId=" + nodeId);
            nameQuery.setCacheable(!getIsWorking());            
            Integer results = (Integer) nameQuery.uniqueResult();
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.valueOf(0);
        }    	
    }
    
    public Integer getNumChildrenForNodeId(Long nodeId, boolean checkIncertaeSedis) {
    	String queryString = "select count(*) from org.tolweb.hibernate.MappedNode n where n.parentNodeId=" + nodeId;
    	if (checkIncertaeSedis) {
    		queryString += " and n.confidence !=" + Node.INCERT_UNSPECIFIED;
    	}
        try {
            Session session = getSession();
            Query nameQuery = session.createQuery(queryString);
            nameQuery.setCacheable(!getIsWorking() && !checkIncertaeSedis);            
            Integer results = (Integer) nameQuery.uniqueResult();
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.valueOf(0);
        } 
    }
    
    public Integer getMaxChildOrderOnParent(Long nodeId) {
    	String queryString = "select max(orderOnParent) from org.tolweb.hibernate.MappedNode where parentNodeId=" + nodeId;
    	return (Integer) getFirstObjectFromQuery(queryString);
    }
    
    public String getNameForNodeWithId(Long id) {
        return (String) getHibernateTemplate().find("select n.name from org.tolweb.hibernate.MappedNode n where n.nodeId=" + id).get(0);
    }
    
    public void clearCacheForNode(MappedNode node) {
        ArrayList<Long> nodeIdList = new ArrayList<Long>();
        nodeIdList.add(node.getNodeId());
        clearCacheForNodeIds(nodeIdList);
    }
    
    public void clearCacheForNodeIds(Collection<Long> nodeIds) {
    	SessionFactory factory = getSessionFactory();    	
    	for (Long nodeId : nodeIds) {
	        factory.evict(MappedNode.class, nodeId);	        
	        factory.evictCollection("org.tolweb.hibernate.MappedNode.synonyms", nodeId);			
		}
    }
    
    public void saveNode(MappedNode nd) {
    	Date now = new Date();
    	if (nd.getNodeId() == null) {
    		nd.setCreated(now);
    	}
    	nd.setUpdated(now);
    	getHibernateTemplate().saveOrUpdate(nd);
    }
    
    /**
     * Performs a logical delete of the argument node.  
     * 
     * @param nd the MappedNode that will be 'deleted' (or marked as inactive)
     * @param immediateFlush indicates if caller wants hibernate's cache flushed
     */
    public void deleteNode(MappedNode nd, boolean immediateFlush) {
    	Date now = new Date();
    	nd.setUpdated(now);
    	nd.setStatus(MappedNode.INACTIVE);
    	saveNode(nd);

//        getHibernateTemplate().delete(nd); // we're no longer physical deleting nodes
    	
    	// since we're not physically deleting the nodes I'm not sure if this 
    	// flush is a moot operation - but I'd like any inactive node removed 
    	// from the results of cached queries, so I'm leaving this call in
        if (immediateFlush) {
        	getHibernateTemplate().flush();
        }
    }

    /* (non-Javadoc)
     * @see org.tolweb.dao.NodeDAO#getNodeIsAncestor(java.lang.Long, java.lang.Long)
     */
    public boolean getNodeIsAncestor(Long nodeId, Long potentialAncestorId) {
        ResultSet results;
        boolean returnVal = false;
        Session session = null;
		try {
		    session = getSession();
			Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            String queryString = "select ancestor_id from NODEANCESTORS where node_id=" + nodeId + " and ancestor_id=" + potentialAncestorId;
			results = selectStatement.executeQuery(queryString);
			if (results.next()) {
			    returnVal = true;
			} else {
			    returnVal = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnVal;
    }

    public boolean getNodeIsInactive(Long nodeId) {
        ResultSet results;
        boolean returnVal = false;
        Session session = null;
		try {
		    session = getSession();
			Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            String queryString = "select node_id from NODES where node_id=" + nodeId + " and status=" + MappedNode.INACTIVE;
			results = selectStatement.executeQuery(queryString);
			if (results.next()) {
			    returnVal = true;
			} else {
			    returnVal = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnVal;
    }
    
    /*
     * This method is used in the algorithm implemented by NodeHelper.findClosestContainingGroupWithPage(). 
     * The idea is that if you take all of the ancestors of a node, and determine a count for them - the 
     * one with the highest count will be the closest containing group.  If you were to filter out the 
     * ancestors without pages, now you're able to determine the "closest containing group with a page." 
     * If you think of it like this, we have a node, id #31 and it has the following ancestors:
     *  
     * 31 -> 31, 29, 27, 22, 21, 13, 9, 2, 1
     *  
     * (A node is an ancestor of itself, so this is an identity quality to the ancestor relation.)
     *  
     * Now, if we filter out those nodes that does not have a page attachment, we get this: 
     * 
     * 31 -> 28, 21, 13, 2, 1
     * 
     * So if we create a histogram like representation of this using the number of ancestors a node has 
     * we'd get something like this: 
     * 
     * 28 **** (4)
     * 21 ***  (3)
     * 13 **   (2)
     *  2 *    (1)
     *  1      (0)
     * 
     * The node with the highest count is the node closest to "us", if we take the view of the node 
     * in question. 
     * 
     * (non-Javadoc)
     * @see org.tolweb.dao.NodeDAO#getAncestorCount(java.lang.Long)
     */
    public int getAncestorCount(Long nodeId) {
    	String queryString = "select count(ancestor_id) from NODEANCESTORS na where na.node_id = " + nodeId;
    	Set results = executeRawSQLSelectForLongs(queryString);
    	if (results != null && results.size() > 0) {
    		return ((Number)results.iterator().next()).intValue();
    	} else {
    		return 0;
    	}
    }
    
    /* (non-Javadoc)
     * @see org.tolweb.dao.NodeDAO#getNodeIdsWithAncestors(java.util.Collection)
     */    
    public Set getNodeIdsWithAncestors(Collection ancestorIds) {
        String sqlString = "select na.node_id from NODEANCESTORS na where na.ancestor_id in (" + 
			StringUtils.returnCommaJoinedString(ancestorIds) + ") order by na.node_id asc";
    	return getNodeIdsWithAncestors(sqlString);
    }
    
    public Set getInactiveNodeIdsWithAncestors(Long nodeId) {
    	Object[] args = {nodeId, MappedNode.INACTIVE};
    	String sqlString = "SELECT N.node_id FROM NODEANCESTORS NA INNER JOIN NODES N on N.node_id = NA.node_id WHERE ancestor_id = %1$d  and status = %2$d";
    	sqlString = String.format(sqlString, args);
    	return getNodeIdsWithAncestors(sqlString);
    }
    
    public Set getDeletedNodeIdsWithAncestors(Collection ancestorIds) {
    	String sql = "select na.node_id from NODEANCESTORS na, Download_Nodes dn where na.ancestor_id in (" + 
			StringUtils.returnCommaJoinedString(ancestorIds) + ") and dn.node_id=na.node_id and dn.deleted=1 order by na.node_id asc";
    	return getNodeIdsWithAncestors(sql);
    }
    
    
    protected Set getNodeIdsWithAncestors(String sqlString) {
        Session session = null;
        Set nodeIds = new HashSet();
		try {
		    session = getSession();
		    // Danny -- this is an integer because hibernate freaks out if you call it a long -- longs
		    // are ok everywhere else but they screw things up here.
		    SQLQuery query = session.createSQLQuery(sqlString).addScalar("node_id", Hibernate.INTEGER);
		    query.setCacheable(true);
		    List results = query.list();
		    for (Iterator iter = results.iterator(); iter.hasNext();) {
				Integer nextId = (Integer) iter.next();
				// convert it to longs for the sake of consistency
				nodeIds.add(Long.valueOf(nextId.longValue()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeIds;    	
    }

    /* (non-Javadoc)
     * @see org.tolweb.dao.NodeDAO#getAncestorsForNode(java.lang.Long)
     */
    public Set getAncestorsForNode(Long nodeId) {
    	return getNodeAncestors(nodeId, false);
    }
    
    public Integer getNumAncestorsForNode(Long nodeId) {
    	return ((Number) getNodeAncestors(nodeId, true).iterator().next()).intValue();
    }
    /**
     * Order a collection of node ids by their closeness to root --
     * the closest node id to root is the first element in the list,
     * furthest away from root is the last
     * @param nodeIds
     * @return
     */    
    public List getOrderedByNumAncestorsNodes(Collection nodeIds) {
    	final Hashtable<Number, Integer> nodeIdsToNumAncestors = new Hashtable<Number, Integer>();
    	List nodeIdList = new ArrayList();
    	for (Iterator iter = nodeIds.iterator(); iter.hasNext();) {
			Number nodeId = (Number) iter.next();
			int numAncestors = getNumAncestorsForNode(Long.valueOf(nodeId.longValue()));
			nodeIdsToNumAncestors.put(nodeId, numAncestors);
			nodeIdList.add(nodeId);
		}
    	// now do the sort
    	Comparator numAncestorsComparator = new Comparator<Number> () {
			public int compare(Number arg0, Number arg1) {
				if (arg0 == null && arg1 != null) {
					return -1;
				} else if (arg0 != null && arg1 == null) {
					return 1;
				} else if (arg0 == null && arg1 == null) {
					return 0;
				} else {
					Number arg0Ancestors = nodeIdsToNumAncestors.get(arg0);
					Number arg1Ancestors = nodeIdsToNumAncestors.get(arg1);
					int arg0AncestorsInt = arg0Ancestors.intValue();
					int arg1AncestorsInt = arg1Ancestors.intValue();
					return arg0AncestorsInt > arg1AncestorsInt ? 1 : arg0AncestorsInt == arg1AncestorsInt ? 0 : -1;
				}
			}
    	};
    	Collections.sort(nodeIdList, numAncestorsComparator);
    	return nodeIdList;
    }    
    protected Set getNodeAncestors(Long nodeId, boolean isCount) {
        Set ancestors = new HashSet();
        ResultSet results;
        Session session = null;
        String ancestorIdSql = "ancestor_id";
        if (isCount) {
        	ancestorIdSql = "count(" + ancestorIdSql + ")";
        }
		try {
		    session = getSession();
			Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			String ancestorString = "select " + ancestorIdSql + " from NODEANCESTORS where node_id=" + nodeId;
			results = selectStatement.executeQuery(ancestorString);
			System.out.println("ancestors query was: " + ancestorString);
			while (results.next()) {
			    ancestors.add(Long.valueOf(results.getInt(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return ancestors;    	
    }
    
    /**
     * Returns a set of the descendant ids that descend from the node id passed in
     * @param nodeId
     * @return
     */
    public Set getDescendantIdsForNode(Long nodeId) {
    	List nodeIds = new ArrayList();
    	nodeIds.add(nodeId);
    	return getNodeIdsWithAncestors(nodeIds);
    }
    
    public Set getDescendantIdsForNode(Long nodeId, boolean filterInactive) {
    	return getDescendantIdsForNode(nodeId);
    }
    
    public Set getDeletedDescendantIdsForNode(Long nodeId) {
    	List nodeIds = new ArrayList();
    	nodeIds.add(nodeId);
    	return getDeletedNodeIdsWithAncestors(nodeIds);
    }
    
    public Collection<Long> getOnlyAncestorIds(Long nodeId, Collection potentialAncestorIds) {
    	String queryString = "select ancestors.nodeId from org.tolweb.hibernate.MappedNode n join n.ancestors as ancestors " 
    		+ "where n.nodeId=" + nodeId + " and ancestors.nodeId " + 
    			StringUtils.returnSqlCollectionString(potentialAncestorIds);
    	return getHibernateTemplate().find(queryString);    	
    }
    
    public void resetAncestorsForNode(Long nodeId, Collection ancestors) {
        Session session = null;
        try {
          session = getSession();
          Statement deleteStatement = session.connection().createStatement();
          String deleteString = "delete from NODEANCESTORS where node_id=" + nodeId;
          deleteStatement.executeUpdate(deleteString);
          if (ancestors != null && ancestors.size() > 0) {
	          String insertString = getBaseAncestorSqlInsertString();
	          Statement ancestorsStatement = session.connection().createStatement();
	          String valuesList = "";
	          for (Iterator iter = ancestors.iterator(); iter.hasNext();) {
	              Long nextAncestorId = (Long) iter.next();
	              valuesList += "(" + nodeId + "," + nextAncestorId + ")";
	              if (iter.hasNext()) {
	                  valuesList += ",";
	              }
	          }
	          String sql = insertString + valuesList;
	          ancestorsStatement.executeUpdate(sql);
          }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deleteAncestorsForNode(Long nodeId) {
        Session session = null;
        try {
          session = getSession();
          Statement deleteStatement = session.connection().createStatement();
          String deleteString = "delete from NODEANCESTORS where node_id=" + nodeId;
          deleteStatement.executeUpdate(deleteString);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }          
    }
    
    public void flushQueryCache() {
    	getSessionFactory().evictQueries();
    }
    
    protected String getBaseAncestorSqlInsertString() {
    	return "insert into NODEANCESTORS (node_id,ancestor_id) values ";
    }
    
    public void addAncestorsForNodes(Collection nodeIds, Collection ancestorIds) {
    	StringBuffer sqlStringBuffer = new StringBuffer(getBaseAncestorSqlInsertString());
    	sqlStringBuffer.append(StringUtils.buildAncestorSqlString(nodeIds, ancestorIds));
    	executeRawSQLUpdate(sqlStringBuffer.toString());
    }
    
    public boolean getNodeExistsWithId(Long id) {
        List results = null;
        results = getHibernateTemplate().find("select count(*) from org.tolweb.hibernate.MappedNode n where n.nodeId=" + id);
        if (results != null) {
            Integer count = (Integer) results.get(0);
            return count.intValue() == 1;
        } else {
            return false;
        }
    }
    
    public boolean getNodeExistsWithIdUnfiltered(Long id) {
    	return getNodeExistsWithId(id);
    }
    
    public boolean getNodeIsSubmitted(Long nodeId) {
        List results = getHibernateTemplate().find("select n.isSubmitted from org.tolweb.hibernate.MappedNode n where n.nodeId=" + nodeId);
        if (results != null && results.size() > 0) {
            return ((Boolean) results.get(0)).booleanValue();
        } else {
            return false;
        }
    }
    public AdditionalFields getAdditionalFieldsForNode(MappedNode node) {
    	return (AdditionalFields) getFirstObjectFromQuery("from org.tolweb.btol.AdditionalFields where nodeId=" + node.getNodeId());
    }
    public void addAdditionalFieldsForNode(MappedNode node) {
    	AdditionalFields newAddFields = new AdditionalFields();
    	newAddFields.setNodeId(node.getNodeId());
    	getHibernateTemplate().saveOrUpdate(newAddFields);
    }
    public void saveAdditionalFields(AdditionalFields fields) {
    	getHibernateTemplate().saveOrUpdate(fields);
    }
    public MappedNode getRootNode() {
    	return getNodeWithId(Long.valueOf(1));
    }
    public MappedNode getNodeWithName(String name, Long pageId) {
    	String queryString = "from org.tolweb.hibernate.MappedNode where name='" + name + "' and pageId=" + pageId;
    	return (MappedNode) getFirstObjectFromQuery(queryString);
    }
    public MappedNode getNodeWithNameAndParent(String name, Long parentNodeId) {
    	String queryString = "from org.tolweb.hibernate.MappedNode where name='" + name + "' and parentNodeId=" + parentNodeId;
    	return (MappedNode) getFirstObjectFromQuery(queryString);    	
    }
    public List getNodesWithNames(List nodeNames, Long parentNodeId) {
    	String queryString = "select name from org.tolweb.hibernate.MappedNode where name " + 
    		StringUtils.returnSqlCollectionString(nodeNames, true) + " and parentNodeId=" + parentNodeId;
    	return getHibernateTemplate().find(queryString);
    }
    public void resetPageIdForNodeIds(Long pageId, Collection nodeIds) {
    	if (nodeIds != null && nodeIds.size() > 0) {
    		executeUpdateQuery("update org.tolweb.hibernate.MappedNode set pageId=" + pageId + " where nodeId " + StringUtils.returnSqlCollectionString(nodeIds));
    	}
    }
    public void updateOrderOnPageForNode(Long nodeId, int order) {
    	executeUpdateQuery("update org.tolweb.hibernate.MappedNode set orderOnPage=" + order + " where nodeId=" + nodeId);
    }
    public void updateHasIncompleteSubgroupsForNode(Long nodeId, boolean value) {
    	executeUpdateQuery("update org.tolweb.hibernate.MappedNode set hasIncompleteSubgroups=" + value + " where nodeId=" + nodeId);
    }
	public void deleteAncestorsNotInBranch(Collection nodeIds) {
		if (nodeIds != null && nodeIds.size() > 0) {
			String inString = StringUtils.returnSqlCollectionString(nodeIds);
			String sql = "delete from NODEANCESTORS where node_id " + inString + " and ancestor_id not " + inString;
			executeRawSQLUpdate(sql);
		}
	}

	public void replaceTextInTaxa(Long rootNodeId, String toReplace, String replacementText, boolean caseSensitive, boolean wholeWords) {
		String caseSensitiveString = caseSensitive ? " BINARY " : "";
		String searchText = wholeWords ? ("[[:<:]]" + toReplace + "[[:>:]]") : toReplace;
		String sqlString = "select node_id, node_Name from NODES where node_Name REGEXP " + caseSensitiveString + "'" +  searchText + "'";
		try {
		    Session session = getSession();
			Statement selectStatement = session.connection().createStatement();
			ResultSet results = selectStatement.executeQuery(sqlString);
			while (results.next()) {
				System.out.println("current id: " + results.getInt(1) + " name: " + results.getString(2));
			    //nodeIds.add(new Long(results.getInt(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		/*SQLQuery query = getSession().createSQLQuery(sqlString).addScalar("node_id", Hibernate.LONG).addScalar("node_Name", Hibernate.STRING);
		List results = query.list();
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] idNamePair = (Object[]) iter.next();
			System.out.println("current id: " + idNamePair[0] + " name: " + idNamePair[1]);
		}*/
		//System.out.println("results size: " + results.size());
	}
	
	public List getDescendantsForTabbedExport(Long nodeId, Collection descendantIds) {
		//Set descendantIds = getDescendantIdsForNode(nodeId);
		String sqlString = "select node_id, node_Name, authority, auth_year, node_Extinct, parentnode_id from NODES where node_id " + 
			StringUtils.returnSqlCollectionString(descendantIds) + " order by parentnode_id, order_on_parent desc";
		SQLQuery query = getSession().createSQLQuery(sqlString).
			addScalar("node_id", Hibernate.LONG).
			addScalar("node_Name", Hibernate.STRING).
			addScalar("authority", Hibernate.STRING).
			addScalar("auth_year", Hibernate.INTEGER).
			addScalar("node_Extinct", Hibernate.INTEGER).
			addScalar("parentnode_id", Hibernate.LONG);
		List results = query.list();
		return results;
	}

	public Hashtable<Long, List<MappedNode>> getDescendantNodesForIndexPage(Collection descendantIds) {
		long currentTime = System.currentTimeMillis();
		List nodes = fetchNodeCollectionWithIds(descendantIds, "", " n.orderOnParent asc");
		System.out.println("fetching collection took: " + (System.currentTimeMillis() - currentTime));
		Hashtable<Long, List<MappedNode>> nodesToParents = new Hashtable<Long, List<MappedNode>>();
		for (Iterator iter = nodes.iterator(); iter.hasNext();) {
			MappedNode nextNode = (MappedNode) iter.next();
			Long parentNodeId = nextNode.getParentNodeId();
			List nodesWithSameParent = nodesToParents.get(parentNodeId);
			if (nodesWithSameParent == null) {
				nodesWithSameParent = new ArrayList<MappedNode>();
				nodesToParents.put(parentNodeId, nodesWithSameParent);
			}
			nodesWithSameParent.add(nextNode);
		}
		return nodesToParents;
	}
    public List getAllForeignDatabases() {
        return getHibernateTemplate().find("from org.tolweb.hibernate.ForeignDatabase");
    }
    public void saveForeignDatabase(ForeignDatabase db) {
    	ensureUrlsValid(db);
    	getHibernateTemplate().saveOrUpdate(db);
    }
    
    private void ensureUrlsValid(ForeignDatabase fdb) {
    	if (fdb != null) {
    		if (StringUtils.notEmpty(fdb.getUrl()) && !hasHttpPrefix(fdb.getUrl())) {
    			fdb.setUrl(HTTP_PREFIX + fdb.getUrl());
    		}
    	}
	}

	public ForeignDatabase getForeignDatabaseWithId(Long id) {
    	return (ForeignDatabase) getObjectWithId(ForeignDatabase.class, id);
    }
    
    public ForeignDatabase getForeignDatabaseWithName(String name) {
    	String queryString = "from org.tolweb.hibernate.ForeignDatabase where name=?";
    	return (ForeignDatabase)getFirstObjectFromQuery(queryString, name);
    }
    
    /**
     * Returns an object array with minimal node info for xml export
     * @param nodeId 
     * @return An object array with the values: node_Name (string), 
     * 	node_Extinct (int), node_Confidence (int), node_Phylesis (int), node_Leaf (boolean)
     */    
    public Object[] getMinimalNodeInfoForNodeId(Long nodeId) {
    	String queryString = "select name, extinct, confidence, phylesis, isLeaf, description from " + 
    		"org.tolweb.hibernate.MappedNode where nodeId=" + nodeId;
    	return (Object[]) getFirstObjectFromQuery(queryString);
    }

	public void updatePageIdForNode(MappedNode node, Long pageId) {
		if (node != null && node.getNodeId() != null && pageId != null) {
			List nodeIds = new ArrayList();
			nodeIds.add(node.getNodeId());
			resetPageIdForNodeIds(pageId, nodeIds);
		}
	}
	
	/*
	 * moves the ancestry of nodes 
	 */
	public void swapNodeAncestry(Long srcNodeId, Long destNodeId) {
        Session session = null;
        try {
          session = getSession();
          Statement updateStatement = session.connection().createStatement();
          String updateNodeIdString = "update NODEANCESTORS set node_id=" + srcNodeId + " where node_id=" + destNodeId;
          String updateAncestorIdString = "update NODEANCESTORS set ancestor_id=" + srcNodeId + " where ancestor_id=" + destNodeId;
          updateStatement.executeUpdate(updateNodeIdString);
          updateStatement.executeUpdate(updateAncestorIdString);
          getHibernateTemplate().flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 		
	}
	
	/*
	 * changes parental relations of the children from the destination to the source.
	 */
	public void swapNodeParenthood(Long newParentNodeId, Long oldParentNodeId) {
        Session session = null;
        try {
          session = getSession();
          Statement updateStatement = session.connection().createStatement();
          String updateChildrenString = "update NODES set parentnode_id=" + newParentNodeId + " where parentnode_id=" + oldParentNodeId;          
          updateStatement.executeUpdate(updateChildrenString);
          getHibernateTemplate().flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }		
		
	}
}
