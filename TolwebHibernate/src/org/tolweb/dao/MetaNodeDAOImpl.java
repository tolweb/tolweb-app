package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;

import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.MetaNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

public class MetaNodeDAOImpl extends WorkingAwareDAO implements MetaNodeDAO {
	private NodeDAO nodeDAO;
	private PageDAO pageDAO;
	private AccessoryPageDAO accessoryPageDAO;
	private ImageDAO imageDAO;
	private ContributorDAO contributorDAO;
	private OtherNameDAO otherNameDAO;
	private NodeDAO miscNodeDAO;
	private PermissionChecker permsChecker;
	
	public MetaNode getMetaNode(MappedNode nd) {
		MetaNode meta = new MetaNode();
		meta.setNode(nd);
		meta.setPage(pageDAO.getPageForNode(nd));
		meta.setAccessoryPages(createAccessoryPagesList(nd));
		meta.setMedia(createMediaList(nd));
		meta.setContributors(createContributorsList(nd));
		meta.setOtherNameIds(createOtherNamesList(nd));
		return meta;
	}

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
	
	private List<MappedAccessoryPage> createAccessoryPagesList(MappedNode nd) {
		List accPages = accessoryPageDAO.getAccessoryPagesForNode(nd);
		if (accPages != null) {
			return new ArrayList<MappedAccessoryPage>(accPages);
		} else {
			return new ArrayList<MappedAccessoryPage>();
		}
	}
	
	private List<NodeImage> createMediaList(MappedNode nd) {
		List imgs = imageDAO.getImagesAttachedToNode(nd.getNodeId());
		if (imgs != null) {
			return new ArrayList<NodeImage>(imgs);
		} else {
			return new ArrayList<NodeImage>();
		}
	}
	
	private Set<Contributor> createContributorsList(MappedNode nd) {
		List<Long> tmpIds = new ArrayList<Long>();
		tmpIds.add(Long.valueOf(nd.getId()));
		List contrIds = getContributorDAO().getContributorsAttachedToNodeIds(tmpIds, permsChecker, true);
		if (contrIds != null) {
			return new HashSet<Contributor>(contrIds);
		} else {
			return new HashSet<Contributor>();
		}
	}
	
	private Map<Long, String> createOtherNamesList(MappedNode nd) {
		Map<Long, String> otherNames = otherNameDAO.getOtherNamesInfoForNode(nd);
		if (otherNames != null) {
			return otherNames;
		} else {
			return new HashMap<Long, String>();
		}
	}
	
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
	 * @return the pageDAO
	 */
	public PageDAO getPageDAO() {
		return pageDAO;
	}
	/**
	 * @param pageDAO the pageDAO to set
	 */
	public void setPageDAO(PageDAO pageDAO) {
		this.pageDAO = pageDAO;
	}
	/**
	 * @return the accessoryPageDAO
	 */
	public AccessoryPageDAO getAccessoryPageDAO() {
		return accessoryPageDAO;
	}
	/**
	 * @param accessoryPageDAO the accessoryPageDAO to set
	 */
	public void setAccessoryPageDAO(AccessoryPageDAO accessoryPageDAO) {
		this.accessoryPageDAO = accessoryPageDAO;
	}
	/**
	 * @return the imageDAO
	 */
	public ImageDAO getImageDAO() {
		return imageDAO;
	}
	/**
	 * @param imageDAO the imageDAO to set
	 */
	public void setImageDAO(ImageDAO imageDAO) {
		this.imageDAO = imageDAO;
	}
	/**
	 * @return the contributorDAO
	 */
	public ContributorDAO getContributorDAO() {
		return contributorDAO;
	}
	/**
	 * @param contributorDAO the contributorDAO to set
	 */
	public void setContributorDAO(ContributorDAO contributorDAO) {
		this.contributorDAO = contributorDAO;
	}
	/**
	 * @return the otherNameDAO
	 */
	public OtherNameDAO getOtherNameDAO() {
		return otherNameDAO;
	}
	/**
	 * @param otherNameDAO the otherNameDAO to set
	 */
	public void setOtherNameDAO(OtherNameDAO otherNameDAO) {
		this.otherNameDAO = otherNameDAO;
	}
	/**
	 * @return the miscNodeDAO
	 */
	public NodeDAO getMiscNodeDAO() {
		return miscNodeDAO;
	}
	/**
	 * @param miscNodeDAO the miscNodeDAO to set
	 */
	public void setMiscNodeDAO(NodeDAO miscNodeDAO) {
		this.miscNodeDAO = miscNodeDAO;
	}

	/**
	 * @return the permsChecker
	 */
	public PermissionChecker getPermsChecker() {
		return permsChecker;
	}

	/**
	 * @param permsChecker the permsChecker to set
	 */
	public void setPermsChecker(PermissionChecker permsChecker) {
		this.permsChecker = permsChecker;
	}
}
