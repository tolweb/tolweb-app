/*
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.hibernate.TeacherResource;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PageContentMenu extends BaseComponent {
    public abstract MappedTextSection getCurrentTextSection();
    
    public String getCurrentTextSectionAnchor() {
        return getCurrentTextSection().getHeadingNoSpaces();
    }
    
    public boolean getIsWebquest() {
        return ViewWebquest.class.isInstance(getPage());
    }
    
    public String getPageType() {
        if (getIsTreehouse() && !getIsWebquest()) {
            return "Treehouse";
        } else {
            return "Page";
        }
    }
    
    public boolean getShowPortfolioLink() {
        if (getIsTreehouse()) {
            MappedAccessoryPage treehouse = ((ViewTreehouse) getPage()).getActualTreehouse();
            boolean notSpecialTr = !treehouse.getIsTeacherResourceInstance() || ((TeacherResource) treehouse).getResourceType() == TeacherResource.OTHER;
            return treehouse.getPortfolio() != null && !treehouse.getPortfolio().getIsEmpty() && notSpecialTr && !treehouse.getIsWebquest();
        } else {
            return false;
        }
    }
    
    public boolean getIsTreehouse() {
        try {
            ViewTreehouse page = (ViewTreehouse) getPage();
            if (page.getActualTreehouse().getIsTreehouse()) {
                return true;
            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
