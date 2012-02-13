/*
 * Created on May 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.link.DefaultLinkRenderer;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditImageLink extends BaseComponent implements BaseInjectable {
    public abstract NodeImage getImage();
    public abstract String getEditWindowName();
    
    public String getEditPageName() {
        return EditImageData.getEditPageNameForMedia(getImage());
    }
    
    public DefaultLinkRenderer getRenderer() {
    	String windowName = getEditWindowName();
    	if (StringUtils.isEmpty(windowName)) {
    		windowName = "editImage";
    	}
    	return getRendererFactory().getLinkRenderer(windowName, 900, 700, "location=no, menubar=no, status=no, toolbar=no, scrollbars=yes, resizable=yes");
    }
}
