/*
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry.wrappers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.CacheAndPublicAwarePage;
import org.tolweb.tapestry.TreehouseWrapper;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class BranchLeafWrapper extends TreehouseWrapper implements BaseInjectable {
    public static String FEED_ELEMENT_FORMAT = "<link rel=\"alternate\" type=\"application/rss+xml\" title=\"%1$s\" href=\"%2$s\" />";
	
	public abstract MappedNode getNode();
    public abstract MappedPage getTolPage();
    public abstract String getTitleParam();
    public abstract boolean getIsImageGallery();
    public abstract boolean getIsMovieGallery();
    
    public List<IAsset> getStylesheets() {
    	List<IAsset> returnList = new ArrayList<IAsset>();
    	returnList.add(getTolCssStylesheet());
    	if (getAdditionalStylesheet() != null) {
    		returnList.add(getAdditionalStylesheet());
    	}
    	return returnList;
    }
    
    public boolean getIsLeaf() {
        return (getNode() != null) ? getNode().getIsLeaf() : false;
    }
    
    public boolean getHasInternetInfo() {
        return StringUtils.notEmpty(getTolPage().getInternetInfo());
    }
    
    public boolean getHasRefs() {
        return StringUtils.notEmpty(getTolPage().getReferences());
    }
    
	public IRender getSyndicationDelegate() {
		delegate = new IRender() {
			public void render(IMarkupWriter writer, IRequestCycle cycle) {
				if (getNode() != null) {
					if (getIsImageGallery() || getIsMovieGallery()) {
						writer.printRaw(getUrlBuilder().getGallerySyndication(getNode().getNodeId(), getNode().getName()));
					}
				}
			}
		};
		return delegate;
	}    
	
    public String getTitle() {
    	if (getTitleParam() != null) {
    		return getTitleParam();
    	} else {
    		return getTolPage().getGroupName();
    	}
    }
    public boolean getIsWorking() {
    	return ((CacheAndPublicAwarePage) getPage()).getIsWorking();
    }    
    
    @SuppressWarnings("unchecked")
	public boolean getHasOtherNamesToDisplay() {
		SortedSet synonyms = getNode().getSynonyms();
		int dontlistNames = 0;
		for (Iterator itr = synonyms.iterator(); itr.hasNext(); ) {
			MappedOtherName moname = (MappedOtherName) itr.next();
			if (moname.getIsDontList()) {
				dontlistNames++;
			}
		}
		return (synonyms.size() - dontlistNames) > 0;
	}    
    
    @InjectObject("spring:urlBuilder")
    public abstract URLBuilder getUrlBuilder();
    
    public String getPageUrl() {
    	return getUrlBuilder().getURLForObject(getTolPage());
    }
}
