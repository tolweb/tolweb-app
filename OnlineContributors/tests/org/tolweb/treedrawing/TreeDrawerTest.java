package org.tolweb.treedrawing;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.TreeGif;


public class TreeDrawerTest  extends ApplicationContextTestAbstract {
	public static boolean drawLifeOnEarthFirst = true;
	public TreeDrawerTest(String name) {
		super(name);
	}
	
	public void testTreeDrawing() {
		MappedNode terrestrialVertebrates = ((NodeDAO) context.getBean("workingNodeDAO")).getNodeWithId(2L);
		MappedPage page = ((PageDAO) context.getBean("workingPageDAO")).getPage(terrestrialVertebrates);
		TreeDrawer workingTreeDrawer = (TreeDrawer) context.getBean("workingTreeDrawer");
		workingTreeDrawer.setTrunkColoringEnabled(false);
		TreeGif treeGif = workingTreeDrawer.drawTreeForPage(page);
		System.out.println("treeGif url is: " + treeGif.getName());
	}
	
	public void testLifeOnEarthTree() {
		if (drawLifeOnEarthFirst) {
			MappedNode terrestrialVertebrates = ((NodeDAO) context.getBean("workingNodeDAO")).getNodeWithId(1L);
			MappedPage page = ((PageDAO) context.getBean("workingPageDAO")).getPage(terrestrialVertebrates);
			TreeDrawer workingTreeDrawer = (TreeDrawer) context.getBean("workingTreeDrawer");
			workingTreeDrawer.setTrunkColoringEnabled(true);
			TreeGif treeGif = workingTreeDrawer.drawTreeForPage(page);
			System.out.println("treeGif url is: " + treeGif.getName());			
		}
	}
}
