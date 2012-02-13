/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.components.Block;
import org.tolweb.misc.TextPreparer;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehouseEditPageContent extends AbstractTreehouseEditingPage implements UserInjectable, TreehouseInjectable {
	public abstract void setForbiddenImageId(String value);
	public abstract String getForbiddenImageId();
	public abstract void setNonexistentImageId(String value);
	public abstract String getNonexistentImageId();
	
	public static final String PROGRESS_PROPERTY = "pageContentProgress";
	
	public String getTreehouseText() {
	    TextPreparer preparer = getTextPreparer(); 
	    if (getUseHTMLEditor()) {
	        return preparer.prepareMedia(getTreehouse().getText(), true);
	    } else {
	        return preparer.translateImagesToTreehouseFormat(getTreehouse().getText());
	    }
	}
	
	public void setTreehouseText(String value) {
	    TextPreparer preparer = getTextPreparer();
	    String nonexistentImageId = preparer.getNonExistentTreehouseImageId(value);
	    if (nonexistentImageId != null) {
	        setNonexistentImageId(nonexistentImageId);
	    } else {
		    NodeImage image = preparer.getDisallowedTreehouseImage(value);
		    if (image != null) {
		        Contributor contributor = getContributor();		        
		        boolean hasPermission = getPermissionChecker().checkUsePermissionForImageOnPage(contributor, image, getTreehouse().getAccessoryPageId(), false);
			    if (!hasPermission) {
			        setForbiddenImageId("" + image.getId());
			    }
		    }
	    }
	    String preparedText = preparer.translateImagesFromTreehouseFormat(value);
	    preparedText = preparer.translateImagesFromWysiwygFormat(preparedText);
	    getTreehouse().setText(preparedText);
	}
    
    public Block getHelpBlock() {
        String blockName = "";
        if (getIsPortfolio()) {
            blockName = "portfolioHelpBlock";
        } else {
            blockName = "treehouseHelpBlock";
        }
        return (Block) getComponents().get(blockName);
    }
	
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}
}
