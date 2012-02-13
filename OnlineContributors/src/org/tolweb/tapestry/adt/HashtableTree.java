package org.tolweb.tapestry.adt;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.tolweb.hibernate.MappedNode;

/**
 * class used to model a portion of the tree in memory
 * using a hashtable -- eventually a lot of the functionality
 * in TaxaIndex should be moved here
 * @author dmandel
 */
public class HashtableTree {
	private Hashtable<Long, List<MappedNode>> table;

	public Hashtable<Long, List<MappedNode>> getTable() {
		return table;
	}
	public void setTable(Hashtable<Long, List<MappedNode>> table) {
		this.table = table;
	}
	public Set<Long> getAllNodeIds() {
		Set<Long> ids = new HashSet<Long>();
		ids.addAll(getTable().keySet());
		for (List<MappedNode> nodes : getTable().values()) {
			for (MappedNode node : nodes) {
				ids.add(node.getNodeId());
			}
		}
		return ids;		
	}
	public MappedNode getNodeWithId(Long id) {
		for (List<MappedNode> nodes : getTable().values()) {
			for (MappedNode node : nodes) {
				if (node.getNodeId().equals(id)) {
					return node;
				}
			}
		}
		return null;		
	}
}
