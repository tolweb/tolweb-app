package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.MappedNode;

public interface FilteredNodeDAO extends NodeDAO {
	public List<MappedNode> getAllInactiveNodes();
	public MappedNode getNodeWithId(Long id, boolean includeInactiveNodes);
	public MappedNode getInactiveNodeWithId(Long id);
}
