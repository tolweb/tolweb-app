/*
 * Created on Oct 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.List;

import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.misc.CacheAccess;
import org.tolweb.misc.TextPreparer;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TextSections extends CachedOutputComponent implements BaseInjectable {
    public abstract boolean getUseGlossary();
    public abstract MappedTextSection getCurrentTextSection();
    public abstract MappedPage getTolPage();
	public abstract String getTotalJavascriptString();
	public abstract void setTotalJavascriptString(String value);
    
	@SuppressWarnings("unchecked")
	public String getCurrentTextSectionText() {
	    TextPreparer preparer = getTextPreparer();
	    List results = preparer.prepareText(getCurrentTextSection().getText(), CacheAccess.PAGE_TEXT_CACHE, 
	            getTolPage(), false, getUseGlossary());
	    String actualText = (String) results.get(0);
	    if (getUseGlossary()) {
		    String totalJavascript = getTotalJavascriptString();
		    if (StringUtils.notEmpty(totalJavascript)) {
		        totalJavascript += (String) results.get(1);
		    } else {
		        totalJavascript = (String) results.get(1);
		    }
		    setTotalJavascriptString(totalJavascript);
	    }
	    return actualText;
	}
	
	public String getCurrentTextSectionHeading() {
	    return getCurrentTextSection().getHeadingNoSpaces();
	}
	
	protected String getCachedOutput() {
	    CacheAccess cacheAccess = getCacheAccess(); 
	    if (getUseGlossary()) {
	        setTotalJavascriptString(cacheAccess.getPageJavascript(getTolPage()));
	    }
	    // turned off (temporarily) due to problems with characters getting mangled
	    //return cacheAccess.getTextSectionTextForPage(getTolPage(), getUseGlossary());
	    return null;
	}
	
	protected void setCachedOutput(String value) {
	    getCacheAccess().setTextSectionTextForPage(getTolPage(), value, getUseGlossary());
	    if (getUseGlossary()) {
	        getCacheAccess().setPageJavascript(getTolPage(), getTotalJavascriptString());
	    }
	}
}
