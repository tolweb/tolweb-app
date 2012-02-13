package org.tolweb.tapestry.admin;

import java.util.Date;
import java.util.List;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.AbstractScientificContributorPage;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;

public abstract class RecentActivity extends AbstractScientificContributorPage 
		implements ImageInjectable, AccessoryInjectable, NodeInjectable, PageInjectable,
		TreeGrowServerInjectable {
	private int MOST_RECENT_NUM = 10;
	
	public abstract MappedNode getRootToCheck();
	public abstract void setRootToCheck(MappedNode value);
	public abstract String getNodeNameToCheck();
	public abstract void setNodeNameToCheck(String value);
	public abstract Date getActivityDate();
	public abstract void setActivityDate(Date value);
	
	public abstract void setUploadActivity(List<Object[]> uploadInfo);
	public abstract List<Object[]> getUploadActivity();
	public abstract void setPageActivity(List<Object[]> pageInfo);
	public abstract List<Object[]> getPageActivity();
	public abstract Object[] getCurrentActivity();
	public abstract void setCurrentActivity(Object[] value);
	
	@SuppressWarnings("unchecked")
	public List getMostRecentlyEditedTreehouses() {
		return getWorkingAccessoryPageDAO().getMostRecentlyEditedTreehouses(MOST_RECENT_NUM);
	}
	
	@SuppressWarnings("unchecked")
	public List getMostRecentlyEditedImages() {
		return getImageDAO().getMostRecentlyEditedImages(MOST_RECENT_NUM);
	}
	
	@SuppressWarnings("unchecked")
	public List getMostRecentlyEditedPages() {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List getMostRecentUploads() {
		//return ((UploadDAO) ((Global) getGlobal()).getContext().getBean("uploadDAO")).getMostRecentUploads(MOST_RECENT_NUM);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void updateActivity() {
		List nodes = getWorkingNodeDAO().findNodesExactlyNamed(getNodeNameToCheck());
		if (nodes != null && nodes.size() > 0) {
			MappedNode node = (MappedNode) nodes.get(0);
			Long pageId = getWorkingPageDAO().getPageId(node.getNodeId());
			setUploadActivity(getUploadDAO().getContributorsDatesAndNodesRecentlyChanged(node.getNodeId(), getActivityDate()));
			setPageActivity(getWorkingPageDAO().getContributorsDatesAndPagesRecentlyChanged(pageId, getActivityDate()));
		} else {
			
		}
		 
	}
	public String getCurrentUploadNodeName() {
		return (String) getCurrentActivity()[3];
	}
	public String getCurrentUploadPersonName() {
		return getCurrentActivity()[0] + " " + getCurrentActivity()[1];
	}
	public String getCurrentUploadDate() {
		return ((Date) getCurrentActivity()[2]).toString();
	}
	public String getCurrentPageNodeName() {
		return (String) getCurrentActivity()[2];
	}
	public String getCurrentPagePersonName() {
		return (String) getCurrentActivity()[0];
	}
	public String getCurrentPageDate() {
		return ((Date) getCurrentActivity()[1]).toString();
	}	
	public String getCurrentUploadPublishDate() {
		Long nodeId = ((Long) getCurrentActivity()[4]);
		Date lastPublishDate = getPublicationBatchDAO().getLastPublishedDateForNode(nodeId);
		Date lastEditDate = (Date) getCurrentActivity()[2];
		return getLastUploadDateString(lastPublishDate, lastEditDate);
	}
	public String getCurrentPagePublishDate() {
		Long pageId = ((Long) getCurrentActivity()[3]);
		Date lastPublishDate = getPublicationBatchDAO().getLastPublishedDateForPage(pageId);
		Date lastEditDate = (Date) getCurrentActivity()[1];
		return getLastUploadDateString(lastPublishDate, lastEditDate);
	}
	private String getLastUploadDateString(Date lastPublishDate, Date lastEditDate) {
		if (lastPublishDate == null || lastEditDate.after(lastPublishDate)) {
			return "Unpublished";
		} else {
			return lastPublishDate.toString();
		}
	}
}
