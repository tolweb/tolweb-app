/*
 * Created on Jun 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.link.DefaultLinkRenderer;
import org.tolweb.hibernate.PortfolioPage;
import org.tolweb.misc.URLBuilder;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class WebquestViewProcess extends ViewWebquest {
    public abstract PortfolioPage getCurrentPage();
    
    public String getIntro() {
        return getPreparedText(getWebquest().getProcessIntroduction());
    }
    
    public String getDestinationUrl() {
        PortfolioPage portPage = getCurrentPage();
        URLBuilder urlBuilder = getUrlBuilder();
        return urlBuilder.getURLForPortfolioPage(portPage);
    }
    
    public String getDestinationTitle() {
        PortfolioPage portPage = getCurrentPage();
        if (portPage.getIsExternal()) {
            return portPage.getExternalPageName();
        } else {
            return portPage.getPageTitle();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List getPageInstructionsList() {
        return getTextPreparer().getNewlineSeparatedList(getCurrentPage().getComments());
    }
    
    public DefaultLinkRenderer getRenderer() {
    	return getRendererFactory().getLinkRenderer("WebquestViewWindow", 800, 500, "scrollbars=yes, resizable=yes, menubar=yes, titlebar=yes, toolbar=yes, status=yes, location=yes");
    }
}
