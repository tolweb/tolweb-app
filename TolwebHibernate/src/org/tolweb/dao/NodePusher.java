/*
 * Created on Dec 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import org.tolweb.hibernate.MappedNode;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NodePusher {
	public MappedNode pushNodeToDB(MappedNode nodeToPush, NodeDAO dao) throws Exception {
		// need to know if it exists - regardless of it's "status" (active vs. inactive)
		boolean nodeExists = dao.getNodeExistsWithIdUnfiltered(nodeToPush.getNodeId());
		if (!nodeExists) {
			dao.addNodeWithId(nodeToPush.getNodeId());
		}
		MappedNode otherNode = dao.getNodeWithId(nodeToPush.getNodeId(), true);
		otherNode.copyValues(nodeToPush);
		dao.saveNode(otherNode);
	    return otherNode;
	}
}
