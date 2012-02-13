package org.tolweb.misc;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class PopulateTreeOrder extends BasePage implements IExternalPage,
	NodeInjectable {
	private static final long SIX_FIFTY = 650;
	
	public abstract long getCounterValue();
	public abstract void setCounterValue(long val);
	
	protected void increment() {
		setCounterValue(getCounterValue()+1);
	}
	
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
		long secretNumber = 99L;
		if (args.length == 1) {
			secretNumber = ((Number) args[0]).longValue();
		} 

		if (secretNumber == SIX_FIFTY) {
			populateTreeOrder();
		}
	}

	private void populateTreeOrder() {
		//populateTreeOrder(getMiscNodeDAO());
		populateTreeOrder(getWorkingNodeDAO());
		//populateTreeOrder(getPublicNodeDAO());
	}
	
	private void populateTreeOrder(NodeDAO ndao) {
		MappedNode root = ndao.getNodeWithId(1L);
		setCounterValue(0L);
		traverseTree(ndao, root);

	}
	
	@SuppressWarnings("unchecked")
	private void traverseTree(NodeDAO ndao, MappedNode curr) {
		if (curr != null) {
			System.out.println(" " + curr.getName());
			increment();
			curr.setTreeOrder(getCounterValue());
		
			List children = ndao.getChildrenNodes(curr);
						
			for (Iterator i = children.iterator(); i.hasNext(); ) {
				MappedNode child = (MappedNode)i.next();
				System.out.println("\t child:" + child.getName());
				traverseTree(ndao, child);
			}
			
			ndao.saveNode(curr);
		}
	}
}
