/*
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.asset.ExternalAsset;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.TreehouseInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditTreehouseWrapper extends BaseComponent implements TreehouseInjectable {
	private ExternalAsset stylesheet;
    public abstract IRender getAdditionalDelegate();
    public abstract boolean getIsTeacherResourceSelection();
    
    public IRender getDelegate() {
        return new IRender() {
            public void render(IMarkupWriter writer, IRequestCycle cycle) {
                writer.printRaw("<script type=\"text/javascript\">function showbranch(){arg = showbranch.arguments;if (document.getElementById(arg[0]).className == arg[1]){document.getElementById(arg[0]).className = arg[2];}else{document.getElementById(arg[0]).className = arg[1];}}</script>");
                writer.printRaw("<script src=\"/tree/tip-n-tip/js/dw_event.js\" type=\"text/javascript\"></script>");
                writer.printRaw("<script src=\"/tree/tip-n-tip/js/dw_viewport.js\" type=\"text/javascript\"></script>");
                writer.printRaw("<script src=\"/tree/tip-n-tip/js/dw_tooltip.js\" type=\"text/javascript\"></script>");
                if (getAdditionalDelegate() != null) {
                    getAdditionalDelegate().render(writer, cycle);
                }
            }
        };
    }
    
    public ExternalAsset getStylesheet() {
    	if (stylesheet == null) {
    		stylesheet = new ExternalAsset("/tree/css/trhsform.css", null);
    	}
    	return stylesheet;
    }
    
    public String getIconUrl() {
        String imgFirstPart = "";
        String teacherPrefix = "teacher";
        if (getIsTeacherResourceSelection()) {
            imgFirstPart = teacherPrefix;            
        } else {
            byte treehouseType = getTreehouse().getTreehouseType();
	        switch (treehouseType) {
		    	case MappedAccessoryPage.ARTANDCULTURE: imgFirstPart = "artcult"; break;
		    	case MappedAccessoryPage.BIOGRAPHY: imgFirstPart = "bio"; break;
		    	case MappedAccessoryPage.GAME: imgFirstPart = "fung"; break;
		    	case MappedAccessoryPage.INVESTIGATION: imgFirstPart = "invest"; break;
		    	case MappedAccessoryPage.STORY: imgFirstPart = "stories"; break;
		    	case MappedAccessoryPage.TEACHERRESOURCE: imgFirstPart = teacherPrefix; break;   
		    	case MappedAccessoryPage.PORTFOLIO: imgFirstPart = "portfolio"; break;
                case MappedAccessoryPage.WEBQUEST: imgFirstPart = "webq"; break;
	        }
        }
        return "/tree/learn/lisa.images/trhouseimage/" + imgFirstPart + "iconminiblu.jpg";        
    }
    
	public String getTreehouseHeadlineId() {
	    String teacherHead = "deresourceshead";
	    if (getIsTeacherResourceSelection()) {
	        return teacherHead;
	    } else {
		    byte treehouseType = getTreehouse().getTreehouseType();	        
		    switch (treehouseType) {
		    	case MappedAccessoryPage.ARTANDCULTURE: return "deartculthead";
		    	case MappedAccessoryPage.BIOGRAPHY: return "debiohead";
		    	case MappedAccessoryPage.GAME: return "degamehead";
		    	case MappedAccessoryPage.INVESTIGATION: return "deinvestigationhead";
		    	case MappedAccessoryPage.STORY: return "destorieshead";
		    	case MappedAccessoryPage.TEACHERRESOURCE: return teacherHead;
		    	default: return "";
		    }
	    }
	}
	
	public boolean getCheckboxEnabled() {
	    return !TreehouseEditPublish.class.isInstance(getPage());
	}
    
    public boolean getCanGoToManager() {
        return getIsToolTryout() == null || !getIsToolTryout();
    }
}
