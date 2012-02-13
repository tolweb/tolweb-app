/*
 * Created on Jul 6, 2005
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.tapestry.BaseComponent;
import org.tolweb.misc.TextPreparer;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 */
public abstract class PreparedTextArea extends BaseComponent implements UserInjectable, TreehouseInjectable,
		BaseInjectable {
    public abstract String getValuePath();
    public abstract Object getTargetObject();
    public abstract Boolean getUseEditor();
    public abstract boolean getUseTreehouseImageFormat();
    public abstract boolean getUseDefaultText();
    
	public String getPreparedText() {
	    TextPreparer preparer = getTextPreparer();
	    String result = "";
	    try {
            result = (String) Ognl.getValue(getValuePath(), getTargetObject());
        } catch (OgnlException e) {
            e.printStackTrace();
        }
        boolean shouldUseEditor = false;
        if (getUseEditor() != null) {
            shouldUseEditor = getUseEditor().booleanValue();
        } else {
            shouldUseEditor = ((AbstractTreehouseEditingPage) getPage()).getUseHTMLEditor(); 
        }
	    if (shouldUseEditor) {
	        result = preparer.prepareMedia(result, true);
            if (getUseTreehouseImageFormat() && StringUtils.isEmpty(result) && getUseDefaultText()) {
                // make sure there is some default text there for treehouses
                result = "<p>enter text here</p>";
            }
            return result;
	    } else {
            if (getUseTreehouseImageFormat()) {
                return preparer.translateImagesToTreehouseFormat(result);
            } else {
                return result;
            }
	    }
	}
	
	public void setPreparedText(String value) {
	    TextPreparer preparer = getTextPreparer();
	    /*String nonexistentImageId = preparer.getNonExistentTreehouseImageId(value);
	    if (nonexistentImageId != null) {
	        setNonexistentImageId(nonexistentImageId);
	    } else {
		    NodeImage image = preparer.getDisallowedTreehouseImage(value);
		    if (image != null) {
		        Contributor contributor = getContributor();		        
		        boolean hasPermission = ((Global) getGlobal()).getPermissionChecker().checkUsePermissionForImageOnPage(contributor, image, getTreehouse().getAccessoryPageId(), false);
			    if (!hasPermission) {
			        setForbiddenImageId("" + image.getId());
			    }
		    }
	    }*/
	    String preparedText = preparer.translateImagesFromAllEditFormats(value);
	    try {
            Ognl.setValue(getValuePath(), getTargetObject(), preparedText);
        } catch (OgnlException e) {
            e.printStackTrace();
        }
	    
	}
}
