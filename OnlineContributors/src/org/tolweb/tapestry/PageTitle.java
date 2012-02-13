/*
 * Created on Oct 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.URLBuilder;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PageTitle extends BaseComponent {
    public abstract MappedNode getNode();
    @InjectObject("spring:urlBuilder")
    public abstract URLBuilder getUrlBuilder();
    public abstract boolean getIsIndexPage();
    public String getTitleClass() {
        if (StringUtils.isEmpty(getNode().getPageSubtitle())) {
            return "nosub";
        } else {
            return null;
        }
    }
    
    public boolean getIsExtinct() {
        return getNode().getExtinct() == Node.EXTINCT;
    }
    public String getWorkingUrl() {
    	return getUrlBuilder().getWorkingURLForObject(getNode());
    }
    @Asset("img/extinctl.gif")
    public abstract IAsset getExtinctImage();
}
