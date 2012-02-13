package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class NoPermissionsPage extends BasePage implements PageBeginRenderListener, PageInjectable,
		UserInjectable, CookieInjectable, BaseInjectable {
	@SuppressWarnings("unchecked")
	public abstract void setNodeNames(List value);
	@SuppressWarnings("unchecked")
	public abstract List getNodeNames();
	public abstract MappedNode getPreviousNode();
	public abstract void setPreviousNode(MappedNode value);
	
	public boolean getHasGroups() {
		return getNodeNames() != null && getNodeNames().size() > 0;
	}
	
	@SuppressWarnings("unchecked")
	public void pageBeginRender(PageEvent event) {
		Contributor contr = getContributor();
		List nodeNames = getPermissionChecker().getNodeNamesContributorAttachedTo(contr.getId(), true);		
		setNodeNames(nodeNames);
	}
	
	public String getSOrNull() {
		if (getNodeNames().size() > 0) {
			return "s";
		} else {
			return null;
		}
	}
	public String getPublicUrl() {
		return getUrlBuilder().getPublicURLForBranchPage(getPreviousNode());
	}
}
