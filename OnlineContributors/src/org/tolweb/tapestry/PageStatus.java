/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.ILink;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.wrappers.AbstractWrapper;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.XMLConstants;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PageStatus extends BaseComponent implements UserInjectable, CookieInjectable, BaseInjectable {
    public abstract String getStatus();
    public abstract void setStatusString(String value);
    public abstract void setStatusImageString(String value);
    public abstract void setClassString(String value);
    
	protected void prepareForRender(IRequestCycle cycle) {
	    super.prepareForRender(cycle);
	    String status = getStatus();
        if (status.equals(MappedPage.PEERREVIEWED) || status.equals(XMLConstants.PEER_REVIEWED)) {
            setStatusString("Peer-Reviewed");
            setClassString("peer");
            setStatusImageString("<img src=\"/tree/img/3check.gif\" />");
        } else if (status.equals(XMLConstants.TOL_REVIEWED)) {
            setStatusString("ToL-Reviewed");
            setClassString("tolreview");
            setStatusImageString("<img src=\"/tree/img/2check.gif\" />");
        } else if (status.equals(MappedPage.COMPLETE) || status.equals(XMLConstants.COMPLETE)) {
            setStatusString("Complete");
            setClassString("complete");
            setStatusImageString("<img src=\"/tree/img/check.gif\" />");
        } else if (status.equals(MappedPage.UNDERCONSTRUCTION) || status.equals(XMLConstants.UNDER_CONSTRUCTION)) {
            setStatusString("Under Construction");
            setClassString("construction");
            setStatusImageString("");
        } else if (status.equals(MappedAccessoryPage.NOTE)) {
            setStatusString("Note");
            setClassString("note");
            setStatusImageString("");
        } else if (status.equals(MappedPage.SKELETAL) || status.equals(XMLConstants.SKELETAL) || status.equals(XMLConstants.TEMPORARY)) {
            setStatusString("Temporary Page");
            setClassString("temp");
            setStatusImageString("");
        }
        /*Contributor contr = (Contributor) getPage().getRequestCycle().getAttribute(CacheAndPublicAwarePage.CONTRIBUTOR);
        setContributor(contr);*/
	}
	
    public boolean getCanEdit() {
        Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
        if (contr != null) {
            PermissionChecker checker = getPermissionChecker();
            IPage page = getPage();
            if (ViewBranchOrLeaf.class.isInstance(page)) {
                return checker.checkHasEditingPermissionForNode(contr, ((ViewBranchOrLeaf) getPage()).getNode().getNodeId())
                    && ((ViewBranchOrLeaf) getPage()).getCanEdit();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public void goToLogin(IRequestCycle cycle) {
        ScientificContributorsLogin loginPage = (ScientificContributorsLogin) cycle.getPage("ScientificContributorsLogin");
        loginPage.setupPageCallback(getIsBranchOrLeaf(), cycle.getListenerParameters(), false, cycle);
    }

    public boolean getIsBranchOrLeaf() {
    	return ViewBranchOrLeaf.class.isInstance(getPage());
    }
    
    public ILink logout() {
    	AbstractWrapper wrapper = (AbstractWrapper) getPage().getComponent("wrapper");
    	return wrapper.logout();
    }
}
