/*
 * Created on Sep 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.markup.MarkupWriterImpl;
import org.apache.tapestry.markup.UTFMarkupFilter;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.tapestry.injections.NodeInjectable;

/**
 * @author dmandel
 *
 * 
 * To create a component that caches its output, implement the following two
 * methods:
 * 
 * protected abstract String getCachedOutput(); -- returns the cached results
 * protected abstract void setCachedOutput(String value); -- sets the cached results
 * 
 * Then in the component template include a conditional that checks for the presence
 * of a property named output (which holds the cached output if there is any), if there
 * is, simply do an insert.  Otherwise, render the component as usual and the output
 * will be cached when it is next requested.
 * In the component specification add the following property:
 *     <property-specification name="output" type="java.lang.String"/>
 */
public abstract class CachedOutputComponent extends BaseComponent implements NodeInjectable {
    public void prepareForRender(IRequestCycle cycle) {
        super.prepareForRender(cycle);
	    String results = getCachedOutput();
	    setOutput(results);
	    Boolean useCache = (Boolean) cycle.getAttribute(CacheAndPublicAwarePage.USE_CACHE);
	    if (useCache != null && useCache.booleanValue()) {
	        setUseCache(true);
	    } else {
	        setUseCache(false);
	    }
    }
    
	protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
	    String results = getOutput();	    
	    super.renderComponent(writer, cycle);
	    if (results == null) {
		    ByteArrayOutputStream stream = new ByteArrayOutputStream(2000);
		    IMarkupWriter otherWriter = new MarkupWriterImpl("text/html", new PrintWriter(stream), new UTFMarkupFilter());
		    //HTMLWriter otherWriter = new HTMLWriter(stream);
		    super.renderComponent(otherWriter, cycle);
		    otherWriter.flush();
		    if (getUseCache()) {
		        setCachedOutput(stream.toString());
		    }
	    }
	}
	
	public PageDAO getPageDAO() {
	    return (PageDAO) getPage().getRequestCycle().getAttribute(CacheAndPublicAwarePage.PAGE_DAO);
	}	
	public NodeDAO getNodeDAO() {
		return (NodeDAO) getPage().getRequestCycle().getAttribute(CacheAndPublicAwarePage.NODE_DAO);
	}
	
	protected abstract String getCachedOutput();
	protected abstract void setCachedOutput(String value);
	/**
	 * Tapestry-generated property to hold the output for rendering
	 * @param value
	 */
	public abstract void setOutput(String value);
	public abstract String getOutput();
	
	public abstract void setUseCache(boolean value);
	public abstract boolean getUseCache();
}
