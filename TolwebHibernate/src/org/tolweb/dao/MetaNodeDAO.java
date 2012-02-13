package org.tolweb.dao;

import java.util.Collection;
import java.util.List;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.MetaNode;

public interface MetaNodeDAO extends BaseDAO {
	public MetaNode getMetaNode(MappedNode nd);
	public List<MetaNode> getMetaNodes(Collection<Long> ids);
	public List<MetaNode> getMetaNodes(Collection<Long> ids, boolean includeInactiveNodes);
}
