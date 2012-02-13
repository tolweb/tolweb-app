/*
 * Created on Jun 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.PortfolioPage;
import org.tolweb.hibernate.PortfolioSection;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class WebquestEditProcess extends TreehouseEditPortfolio {
    public abstract PortfolioSection getSectionToAddExternalPageTo();
    public static final String PROGRESS_PROPERTY = "processProgress";    
    
    public boolean doAdditionalFormProcessing(IRequestCycle cycle) {
        if (getSectionToAddExternalPageTo() != null) {
            addExternalPageToSection();
            return true;
        } else {
            return super.doAdditionalFormProcessing(cycle);
        }
    }
    
	protected boolean getCheckPermissionsForSearch() {
	    return false;
	} 
	
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}	
	
	protected String getEditTypeName() {
	    return "WebQuest";
	}
    
    private void addExternalPageToSection() {
        PortfolioSection section = getSectionToAddExternalPageTo();
        // get the index of the section so we can refresh the page to that point
        getReorderHelper().addToSet(section.getPages(), constructNewExternalPortfolioPage());        
        setLastPageAnchorForSection(section);
        doSave();
    }
    
    public String getPreviewPageName() {
        return "WebquestViewProcess";
    }
    
    private PortfolioPage constructNewExternalPortfolioPage() {
        PortfolioPage page = new PortfolioPage();
        page.setComments("");
        page.setIsExternal(true);
        return page;
    }
}
