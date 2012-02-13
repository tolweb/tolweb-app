package org.tolweb.btol.dao;

import java.util.Collection;
import java.util.List;

import org.tolweb.dao.BaseDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.BtolMetaNode;

public interface BtolMetaNodeDAO extends BaseDAO {
	public BtolMetaNode getMetaNodeForBtol(Long id);
	public BtolMetaNode getMetaNodeForBtol(MappedNode nd);
	public List<BtolMetaNode> getMetaNodesForBtolWithIds(Collection<Long> ids);
	public List<BtolMetaNode> getMetaNodesForBtolWithIds(Collection<Long> ids, boolean includeInactiveNodes);
	public List<BtolMetaNode> getMetaNodesForBtol(Collection<MappedNode> nodes);
	public List<BtolMetaNode> getMetaNodesForBtol(Collection<MappedNode> nodes, boolean includeInactiveNodes);
}
