/*
 * Created on Jul 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.PortfolioPage;
import org.tolweb.hibernate.PortfolioSection;
import org.tolweb.hibernate.Student;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PortfolioViewPage extends ViewPortfolio implements PageBeginRenderListener {
    public abstract void setActualPage(MappedAccessoryPage treehouse);
    public abstract MappedAccessoryPage getActualPage();
    public abstract void setPortfolioPage(PortfolioPage page);
    public abstract PortfolioPage getPortfolioPage();
	
    public abstract void setPortfolioIsMinorAuthor(boolean value);
	public abstract void setPortfolioMinorAuthor(Student value);
	public abstract Student getPortfolioMinorAuthor();    
    
	@SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        super.activateExternalPage(parameters, cycle);
        setIsFromPreview(false);
        // The id of the 'other' page to view
        Integer otherPageId = (Integer) parameters[2];
        MappedAccessoryPage actualPage = getDAO().getAccessoryPageWithId(otherPageId.intValue());
        // Need to switch the parameters so that the portfolio page is the first
        // in the array -- since it's possible the redirect will go to an external page
        parameters = new Object[1];
        parameters[0] = otherPageId;
        if (checkForRedirect(actualPage, cycle, parameters)) {
            return;
        }
        setActualPage(actualPage);
        // Loop through and find the portfolio page -- needed in order to determine
        // whether to import various pieces of the portfolio's contents to the portfolio
        // pages
        int otherPageIdInt = otherPageId.intValue();
        PortfolioPage destinationPortPage = null;
        for (Iterator iter = getPortfolio().getSections().iterator(); iter.hasNext();) {
            PortfolioSection section = (PortfolioSection) iter.next();
            for (Iterator iterator = section.getPages().iterator(); iterator.hasNext();) {
                PortfolioPage page = (PortfolioPage) iterator.next();
                if (page.getDestinationId() == otherPageIdInt) {
                    setPortfolioPage(page);
                    destinationPortPage = page;
                    break;
                }
            }
            if (destinationPortPage != null) {
                break;
            }
        }
    }
    
    public MappedAccessoryPage getActualTreehouse() {
    	MappedAccessoryPage actualPage = getActualPage();
        return actualPage;
    }
    
    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        Student stud = findMinorAuthor(getTreehouse());
        if (stud != null) {
            setPortfolioIsMinorAuthor(true);
            setPortfolioMinorAuthor(stud);
        }
    }
    
    public String getMainText() {
    	String text = getActualPage() != null ? getActualPage().getText() : "";
        return getPreparedText(text);
    }
    
    public String getTreehouseBodyId() {
        return getBodyIdForPage(getActualPage());
    }
    
    public String getTreehouseHeadlineClass() {
        return getHeadlineClassForPage(getActualPage());
    }
    
    @SuppressWarnings("unchecked")
	public Collection getContributors() {
	    return (getActualPage() != null) ? getActualPage().getContributors() : new ArrayList();
	}
	
	public String getPortfolioAuthorsString() {
	    return getAuthorsString(getTreehouse().getContributors());
	}
	
	public String getPortfolioProjectName() {
	    if (getPortfolioMinorAuthor() != null) {
	        return getPortfolioMinorAuthor().getProject().getName();
	    } else {
	        return null;
	    }
	}	
}
