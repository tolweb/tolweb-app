package org.tolweb.exporter;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.util.ContentType;
import org.tolweb.tapestry.CacheAndPublicAwarePage;

public abstract class ExportTaxaNames extends CacheAndPublicAwarePage implements IExternalPage {
	public abstract void setNamesString(String value);

	@InjectObject("spring:tabbedNameExporter")
	public abstract TabbedNameExporter getTabbedNameExporter();
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		Number nodeId = (Number) parameters[0];
		setNamesString(getTabbedNameExporter().getTabSeparatedTreeString(Long.valueOf(nodeId.longValue()), getIsWorking()));
	}
	/**
     * overridden to set the content-type to text/plain
     */    
    public ContentType getResponseContentType() {
        return new ContentType("text/plain");
    }    
    @SuppressWarnings("unchecked")
    public Collection getContributors() {
    	return new ArrayList();
    }
}
