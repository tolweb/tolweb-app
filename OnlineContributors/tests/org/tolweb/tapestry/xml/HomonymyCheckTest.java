package org.tolweb.tapestry.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.misc.NodeHelper;
import org.tolweb.tapestry.xml.taxaimport.TaxaImportCheck;

public class HomonymyCheckTest extends ApplicationContextTestAbstract {
	private List<String> othernames;
	private List<String> names;
	
	public HomonymyCheckTest(String name) {
		super(name);
		NodeDAO dao = (NodeDAO)context.getBean("workingNodeDAO");
		names = new ArrayList<String>();
		othernames = new ArrayList<String>();
		MappedNode root = NodeHelper.getClade(16248L, dao);
		traverse(root);
	}

	private void traverse(MappedNode current) {
		if (current != null) {
			names.add(current.getName());
			addOtherNamesForNode(current);
			if (current.getChildren() != null) {
				for (Iterator itr = current.getChildren().iterator(); itr.hasNext(); ) {
					traverse((MappedNode)itr.next());
				}
			}
		}
	}
	
	private void addOtherNamesForNode(MappedNode mnode) {
		for (Iterator itr = mnode.getSynonyms().iterator(); itr.hasNext(); ) {
			MappedOtherName moname = (MappedOtherName)itr.next();
			othernames.add(moname.getName());
		}
	}	
	
	public void test() {
		Set<String> dupes = new HashSet<String>();
		boolean result = TaxaImportCheck.performHomonymyCheck(names, dupes);
		assertTrue(result);
		System.out.println(dupes);
	}
}
