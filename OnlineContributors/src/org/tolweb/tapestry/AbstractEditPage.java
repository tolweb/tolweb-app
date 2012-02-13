/*
 * Created on Jun 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.Set;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.event.PageEvent;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractEditPage extends AbstractContributorPage implements NodesSetPage {
	public abstract void setRemovedGroup(boolean value);
	public abstract boolean getRemovedGroup();
	public abstract void setSearchSelected(boolean value);
	public abstract boolean getSearchSelected();
	public abstract void setContributorSearchSelected(boolean value);
	public abstract boolean getContributorSearchSelected();
	public abstract Object getEditedObject();
	public abstract void setErrorMessage(String value);
	public abstract String getErrorMessage();
	public abstract void setSelectedAnchor(String value);
	public abstract String getSelectedAnchor();	
	
	public void pageValidate(PageEvent event) {
		// Make sure that a contributor is logged-in
		super.pageValidate(event); 
		// Now make sure that we have an Object to edit
		if (getEditedObject() == null) {
			IPage loginPage = getRequestCycle().getPage(getLoginPageName());
			throw new PageRedirectException(loginPage);
		}
	}
       
	public void removeGroup(IRequestCycle cycle) {
		setRemovedGroup(true);
	}
	
	public void addGroupSubmit(IRequestCycle cycle) {
		setSearchSelected(true);
	}	

	public void contributorSearchSubmit(IRequestCycle cycle) {
		setContributorSearchSelected(true);	
	}	

	protected String getHeadJavascript() {
	    String selectedAnchorJavascript = getSelectedAnchor() != null ? 
			"window.onload = function() { document.location=\"#" + getSelectedAnchor() + "\"; };" : "";
		return "\nfunction openWindow(href,w_width,w_height) {" +
				"\n\tif(!w_width) { w_width = 400; }" +
				"\n\tif(!w_height) { w_height= 300; }" + 
				"\n\twindow.open(href, '', 'fullscreen=no,toolbar=no,status=no,menubar=no,scrollbars=yes,resizable=yes,directories=no,location=no,width=' + w_width + ',height=' + w_height);" +
				"}\n" + selectedAnchorJavascript;		
	}
	
	@SuppressWarnings("unchecked")
	public abstract Set getNodesSet();
}

interface NodesSetPage {
	@SuppressWarnings("unchecked")
    public Set getNodesSet();
    public void setSelectedAnchor(String value);
}
