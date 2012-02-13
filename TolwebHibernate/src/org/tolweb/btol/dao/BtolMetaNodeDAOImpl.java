package org.tolweb.btol.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.tolweb.dao.BaseDAOImpl;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.BtolMetaNode;

public class BtolMetaNodeDAOImpl extends BaseDAOImpl implements BtolMetaNodeDAO {
	public static final Long BTOL_PROJECT_ID = Long.valueOf(1);
	private NodeDAO nodeDAO;
	private ProjectDAO projectDAO;
	private GeneFragmentNodeStatusDAO geneFragmentNodeStatusDAO;
	
	public BtolMetaNode getMetaNodeForBtol(Long id) {
		MappedNode nd = nodeDAO.getNodeWithId(id, true);
		return getMetaNodeForBtol(nd);
	}
	
	public BtolMetaNode getMetaNodeForBtol(MappedNode nd) {
		BtolMetaNode btolNode = new BtolMetaNode();
		nd.setAdditionalFields(projectDAO.getAdditionalFieldsForNodeInProject(nd, BTOL_PROJECT_ID));
		btolNode.setNode(nd);
		List<Long> nodeIds = new ArrayList<Long>();
		nodeIds.add(nd.getNodeId());
		List statuses = geneFragmentNodeStatusDAO.getStatusesForNodeIdsInProject(nodeIds, BTOL_PROJECT_ID);
		btolNode.setStatuses(statuses);
		return btolNode;
	}
	
	public List<BtolMetaNode> getMetaNodesForBtolWithIds(Collection<Long> ids) {
		return getMetaNodesForBtolWithIds(ids, false);
	}
	
	public List<BtolMetaNode> getMetaNodesForBtolWithIds(Collection<Long> ids, boolean includeInactiveNodes) {
		List nodes = getNodeDAO().getNodesWithIds(ids, includeInactiveNodes);
		return getMetaNodesForBtol(nodes, includeInactiveNodes);
	}
	
	public List<BtolMetaNode> getMetaNodesForBtol(Collection<MappedNode> nodes) {
		return getMetaNodesForBtol(nodes, false);
	}	

	public List<BtolMetaNode> getMetaNodesForBtol(Collection<MappedNode> nodes, boolean includeInactiveNodes) {
		List<BtolMetaNode> metaNodes = new ArrayList<BtolMetaNode>();
		for (Iterator itr = nodes.iterator(); itr.hasNext(); ) {
			MappedNode nd = (MappedNode)itr.next();
			BtolMetaNode meta = getMetaNodeForBtol(nd);
			metaNodes.add(meta);
		}
		return metaNodes;
	}
	
/*
	public List<MetaNode> getMetaNodes(Collection<Long> ids) {
		return getMetaNodes(ids, false);
	}
	public List<MetaNode> getMetaNodes(Collection<Long> ids, boolean includeInactiveNodes) {
		List mappedNodes = nodeDAO.getNodesWithIds(ids, includeInactiveNodes);
		List<MetaNode> metaNodes = new ArrayList<MetaNode>();
		for (Iterator itr = mappedNodes.iterator(); itr.hasNext(); ) {
			MappedNode nd = (MappedNode)itr.next();
			MetaNode meta = getMetaNode(nd);
			metaNodes.add(meta);
		}
		return metaNodes;
	}
 */
	
	/**
	 * @return the nodeDAO
	 */
	public NodeDAO getNodeDAO() {
		return nodeDAO;
	}

	/**
	 * @param nodeDAO the nodeDAO to set
	 */
	public void setNodeDAO(NodeDAO nodeDAO) {
		this.nodeDAO = nodeDAO;
	}

	/**
	 * @return the projectDAO
	 */
	public ProjectDAO getProjectDAO() {
		return projectDAO;
	}

	/**
	 * @param projectDAO the projectDAO to set
	 */
	public void setProjectDAO(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}

	/**
	 * @return the geneFragmentNodeStatusDAO
	 */
	public GeneFragmentNodeStatusDAO getGeneFragmentNodeStatusDAO() {
		return geneFragmentNodeStatusDAO;
	}

	/**
	 * @param geneFragmentNodeStatusDAO the geneFragmentNodeStatusDAO to set
	 */
	public void setGeneFragmentNodeStatusDAO(
			GeneFragmentNodeStatusDAO geneFragmentNodeStatusDAO) {
		this.geneFragmentNodeStatusDAO = geneFragmentNodeStatusDAO;
	}
	
}
