package org.tolweb.tapestry.xml.taxaimport;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.html.BasePage;
import org.tolweb.tapestry.injections.BaseInjectable;

public abstract class TaxaImportHome extends BasePage implements IExternalPage, BaseInjectable {

	public abstract Long getNodeId();
	public abstract void setNodeId(Long id);

	public abstract Long getPvcNodeId();
	public abstract void setPvcNodeId(Long id);

	public abstract Long getExcNodeId();
	public abstract void setExcNodeId(Long id);
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {

	}
	
	public void doObjectManifestCreate(IRequestCycle cycle) {
        ObjectManifest manifestPage = (ObjectManifest) cycle.getPage("taxaimport/ObjectManifest");
        manifestPage.setCurrentNodeId(getNodeId());
        cycle.activate(manifestPage);		
	}
	
	public void doPageValidityCheck(IRequestCycle cycle) {
		PageValidityCheck redirPage = (PageValidityCheck)cycle.getPage("taxaimport/PageValidityCheck");
		redirPage.setCurrentNodeId(getPvcNodeId());
		cycle.activate(redirPage);		
	}

	public void doExtinctCheck(IRequestCycle cycle) {
		ExtinctCheck redirPage = (ExtinctCheck)cycle.getPage("taxaimport/ExtinctCheck");
		redirPage.setCurrentNodeId(getExcNodeId());
		cycle.activate(redirPage);		
	}
	
    public PopupLinkRenderer getRenderer() {
    	int width = 750;
    	int height = 550;
    	return getRendererFactory().getLinkRenderer("", width, height);
    }	
}
