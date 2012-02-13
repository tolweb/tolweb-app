package org.tolweb.tapestry;

import java.util.List;
import java.util.TreeSet;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.ArchivedPageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.tapestry.injections.PageInjectable;

public abstract class ViewAllPageRevisions extends AbstractBranchOrLeafPage implements IExternalPage, PageInjectable {

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		Number pageId = (Number) parameters[0];
		MappedPage page = getPublicPageDAO().getPageWithId(Long.valueOf(pageId.intValue()));
		setTolPage(page);
		if (page != null) {
			setNode(page.getMappedNode());
		} else {
			MappedPage mpage = new MappedPage();
			MappedNode mnode = new MappedNode();
			mnode.setNodeId(Long.valueOf(-1));
			mnode.setSynonyms(new TreeSet());
			mnode.setIsLeaf(false);
			mpage.setMappedNode(mnode);

			setTolPage(mpage);
			setNode(mnode);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List getArchivedVersions() {
		ArchivedPageDAO dao = getArchivedPageDAO(); 
		return dao.getArchivedPagesForPage(getTolPage().getPageId(), false); 
	}
	public String getPageTitle() {
		return getNode().getActualPageTitle(true, false, false);
	}
}
