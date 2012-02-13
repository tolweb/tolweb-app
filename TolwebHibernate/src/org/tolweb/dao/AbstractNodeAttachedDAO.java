/*
 * Created on Jul 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import org.tolweb.hibernate.MappedNode;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AbstractNodeAttachedDAO extends WorkingAwareDAO {
	private NodeDAO nodeDao;
		
	protected boolean addCriteriaForNodesSet(Criteria criteria, String groupName) {
	    return addCriteriaForNodesSet(criteria, groupName, null);
	}
	
	protected boolean addCriteriaForNodesSet(Criteria criteria, String groupName, Long nodeId) {
	    return addCriteriaForNodes(criteria, groupName, nodeId, "nodes.nodeId");
	}
	
	protected boolean addCriteriaForNodesAncestorsSet(Criteria criteria, String groupName) {
	    return addCriteriaForNodes(criteria, groupName, null, "nodeAncestors.nodeId");
	}

	private boolean addCriteriaForNodes(Criteria criteria, String groupName, Long nodeId, String keyName) {
		ArrayList ids = new ArrayList();
		if (nodeId == null) {
			List nodes = nodeDao.findNodesNamed(groupName);
			if (nodes.size() == 0) {
				return false;
			}
			Iterator it = nodes.iterator();
			while (it.hasNext()) {
				MappedNode node = (MappedNode) it.next();
				ids.add(node.getNodeId());
			}		    
		} else {
		    ids.add(nodeId);
		}

		try {
			criteria.createAlias("nodesSet", "nodes");
			if (keyName.indexOf("Ancestors") != -1) {
			    criteria.createAlias("nodes.ancestors", "nodeAncestors");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		criteria.add(Restrictions.in(keyName, ids));
		return true;			
	}
	
	protected void addNodeAncestorsCriteria(Criteria criteria, Collection nodeIds) {
		try {
			criteria.createAlias("nodesSet", "nodes");
		    criteria.createAlias("nodes.ancestors", "nodeAncestors");
		} catch (Exception e) {
			e.printStackTrace();
		}		
		criteria.add(Restrictions.in("nodeAncestors", nodeIds));		
	}
	
	public void setNodeDAO(NodeDAO value) {
		nodeDao = value;
	}

	public NodeDAO getNodeDAO() {
		return nodeDao;
	}	
}
