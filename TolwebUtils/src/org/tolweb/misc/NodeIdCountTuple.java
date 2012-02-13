package org.tolweb.misc;

/**
 * A tuple that represents a Node-Id mapped to a Count that can be sorted
 * @author lenards
 *
 */
public class NodeIdCountTuple implements Comparable<NodeIdCountTuple> {
	private Long nodeId;
	private int count;
	
	public NodeIdCountTuple() {}
	
	public NodeIdCountTuple(Long nodeId, int count) {
		this.nodeId = nodeId;
		this.count = count;
	}
	
	/**
	 * @return the nodeId
	 */
	public Long getNodeId() {
		return nodeId;
	}
	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	public int compareTo(NodeIdCountTuple o) {
		int diff = o.getCount() - count;
		if (diff == 0) {
			return (int)(o.getNodeId() - nodeId);
		}
		return o.getCount() - count;
	}
	
	@Override
	public int hashCode() {
		return nodeId.intValue() + (31 * count);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (NodeIdCountTuple.class.isInstance(obj)) {
			return this.compareTo((NodeIdCountTuple)obj) == 0;
		} else {
			return false;
		}
		
	}
	
	@Override
	public String toString() {
		return "node-id: " + nodeId + " count: " + count;
	}
}
