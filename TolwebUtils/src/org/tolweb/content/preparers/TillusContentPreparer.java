package org.tolweb.content.preparers;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.tolweb.content.helpers.ContentParameters;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.TitleIllustration;
import org.tolweb.treegrow.main.NodeImage;

public class TillusContentPreparer extends MediaContentPreparer {
	public TillusContentPreparer(ContentParameters params, ImageDAO imageDao, NodeDAO publicNodeDao, PageDAO pageDao){
		super(params, imageDao, publicNodeDao);
		setPageDao(pageDao);
	}
	
	@Override 
	@SuppressWarnings("unchecked")
	protected NodeImage retrieveMediaByCriteria() {
		
		List<Object[]> resultNodes = doGroupNameSearch();
		
		if (resultNodes != null && resultNodes.size() >= 1) {
			NodeImage returnImg = new NodeImage();
			
			Object[] nodeInfo = resultNodes.get(0);
			Long nodeId = Long.valueOf(nodeInfo[1].toString());
			MappedNode mnode = getPublicNodeDao().getNodeWithId(nodeId);
			
			if (getPageDao().getNodeHasPage(nodeId)) {
				MappedPage mpage = getPageDao().getPage(mnode);
				SortedSet tilluses = mpage.getTitleIllustrations();
				Iterator i = tilluses.iterator();
				
				if (i.hasNext()) {
					TitleIllustration tillus = (TitleIllustration)i.next();
					returnImg = tillus.getImage();
				}
			} else {
				Set descendants = getPublicNodeDao().getDescendantIdsForNode(nodeId);
				if (descendants.size() > 0) {
					Collection descPages = getPageDao().getNodeIdsWithPages(descendants);
					for (Iterator itr = descPages.iterator(); itr.hasNext(); ) {
						MappedPage desc = (MappedPage)itr.next();
						SortedSet tilluses = desc.getTitleIllustrations();
						Iterator tmp = tilluses.iterator();
						if(tmp.hasNext()) {
							TitleIllustration ti = (TitleIllustration)tmp.next();
							returnImg = ti.getImage();
							break;
						}
					}
				}
			}
			return returnImg;
		}
		
		return new NodeImage();
	}
	
	private List<Object[]> doGroupNameSearch() {
		String group = getParams().getGroupName();
		List<Object[]> resultNodes = getPublicNodeDao().findNodeNamesParentIdsAndIdsExactlyNamed(group);
		if (resultNodes == null || resultNodes.size() == 0) {
			resultNodes = getPublicNodeDao().findNodeNamesParentIdsAndIdsNamed(group);
		}

		return resultNodes;
	}
	
	
}
