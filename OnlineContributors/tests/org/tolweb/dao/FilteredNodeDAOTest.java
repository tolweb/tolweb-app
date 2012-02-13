package org.tolweb.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.tolweb.hibernate.MappedNode;

public class FilteredNodeDAOTest extends ApplicationContextTestAbstract {
	private NodeDAO dao; 
	
	public FilteredNodeDAOTest(String name) {
		super(name);
		dao = (NodeDAO)context.getBean("workingNodeDAO");
		System.out.println("dao-class: " + dao.getClass().toString());
	}

	public void testGetChildren() {
		MappedNode node = dao.getNodeWithId(new Long(8884));
		boolean isRoot = true;
		List children = dao.getChildrenNodes(node, !isRoot, false);
		System.out.println("children: " + children);
		Collection matches = CollectionUtils.select(children, isNotActive);
		for(Iterator itr = matches.iterator(); itr.hasNext(); ) {
			System.out.println(itr.next());
		}		
		assertFalse(CollectionUtils.exists(children, isNotActive));

	}
	
	Predicate isNotActive = new Predicate() {
	    public boolean evaluate(Object object) {
	    	MappedNode node = (MappedNode)object;
	    	boolean notActive = !node.getStatus().equals(MappedNode.ACTIVE);
	    	if (notActive) System.out.println(node + " status:" + node.getStatus());
	        return notActive;
	    }
	};
}
