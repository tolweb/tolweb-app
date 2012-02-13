package org.tolweb.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tolweb.hibernate.MappedNode;

/**
 * Models a single result from a Group Search
 * 
 * @author lenards
 *
 */
public class GroupSearchResult {
	private MappedNode node;
	private boolean hasPage;
	private MappedNode containingGroup;
	private int ancestorCount;
	private Map<Long, Long> descendents;
	private List<GroupSearchResult> relatedGroups;
	
	public GroupSearchResult(MappedNode mnode) {
		this.node = mnode;
		this.descendents = new HashMap<Long, Long>(0);
		//this.relatedGroups = new ArrayList<GroupSearchResult>();
	}

	@Override
	public String toString() {
		return String.format("{result: %1$s (%2$d)}", node.getName(), node.getNodeId());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else  if (o == null) {
			return false;
		} else if (getClass().equals(o.getClass())) {
			GroupSearchResult other = (GroupSearchResult) o;
			if (getNode() != null && other.getNode() != null) {
				return getNode().equals(other.getNode());
			}else {
				return false;
			}			
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getNode().hashCode() * 31;
	}

	public Long getNodeId() {
		return getNode().getNodeId();
	}
	
	public String getGroupName() {
		return getNode().getName();
	}
	
	public String getContainingGroupName() {
		return getContainingGroup() != null ? getContainingGroup().getName() : ""; 
	}
	
	/**
	 * @return the node
	 */
	public MappedNode getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(MappedNode node) {
		this.node = node;
	}
	
	/**
	 * @return the containingGroup
	 */
	public MappedNode getContainingGroup() {
		return containingGroup;
	}

	/**
	 * @param containingGroup the containingGroup to set
	 */
	public void setContainingGroup(MappedNode containingGroup) {
		this.containingGroup = containingGroup;
	}
	
	public boolean getHasPage() {
		return hasPage;
	}
	
	public void setHasPage(boolean pageAttached) {
		hasPage = pageAttached;
	}

	/**
	 * @return the ancestorCount
	 */
	public int getAncestorCount() {
		return ancestorCount;
	}

	/**
	 * @param ancestorCount the ancestorCount to set
	 */
	public void setAncestorCount(int ancestorCount) {
		this.ancestorCount = ancestorCount;
	}

	/**
	 * @return the descendents
	 */
	public Map<Long, Long> getDescendents() {
		return descendents;
	}

	/**
	 * @param descendents the descendents to set
	 */
	public void setDescendents(Map<Long, Long> descendents) {
		this.descendents = descendents;
	}

	public void addRelatedGroupToResult(GroupSearchResult related) {
		if (relatedGroups == null) {
			this.relatedGroups = new ArrayList<GroupSearchResult>();
		}
		this.relatedGroups.add(related);
	}
	
	/**
	 * @return the relatedGroups
	 */
	public List<GroupSearchResult> getRelatedGroups() {
		return relatedGroups;
	}

	/**
	 * @param relatedGroups the relatedGroups to set
	 */
//	public void setRelatedGroups(List<GroupSearchResult> relatedGroups) {
//		this.relatedGroups = relatedGroups;
//	}
}
