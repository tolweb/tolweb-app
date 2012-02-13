/*
 * Created on Apr 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.annotations.Persist;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class CopyImageDataSearch extends ReturnablePage {
    @Persist("client")
	public abstract String getMediaType();
	public abstract void setMediaType(String value);
    @Persist("client")
	public abstract Long getEditedObjectId();	
    public abstract void setEditedObjectId(Long value);
    
    public String getInstructions() {
        return "Click the 'select' link to choose the " + StringUtils.getStringOrEmptyString(getMediaType()) + " whose data you would like to copy";
    }
    
    public String getCapitalMediaType() {
        if (getMediaType() != null) {
            return StringUtils.capitalizeString(getMediaType());
        } else {
            return "";
        }
    }
}
