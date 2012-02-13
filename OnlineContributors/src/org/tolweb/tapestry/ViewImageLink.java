/*
 * Created on Apr 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.awt.Dimension;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ViewImageLink extends BaseComponent implements ImageInjectable, BaseInjectable {
    public abstract NodeImage getImage();
    
    public static int WIDTH_MINIMUM = 800;
    public static int WIDTH_MAXIMUM = 1010;    
    public static int HEIGHT_MINIMUM = 600; 
    
    public PopupLinkRenderer getRenderer() {
        Dimension dim = getImageUtils().getImageDimensions(getImage());
        int width = dim.width + 100;
        int height = dim.height + 200; 
        Dimension actualDims = getImageUtils().getImageInfoWindowSize(width, height);
    	if (getImage().getMediaType() == NodeImage.MOVIE) {
    		ImageVersion iver = getImageDAO().getMasterVersion(getImage().getId());
    		if (iver != null) { 
    			// apparently the fetch of the master can fail and null is returned -   
    			// this guards the dreaded null pointer exception
	    		width = iver.getWidth() + 100;
	    		height = iver.getHeight() + 200;
    		}
    		actualDims = getImageUtils().getImageInfoWindowSize(width, height);
    	}
        width = getPopupWidth(actualDims.width);
        height = getPopupHeight(actualDims.height);
    	return getRendererFactory().getLinkRenderer("" + getImage().getId(), width, height);
    }
    
    public String getImageUrl() {
    	return getImageUtils().getMediaUrl(getImage().getMediaType(), getImage().getLocation());
    }
    
    public int getPopupWidth(int width) {
    	if (width < WIDTH_MINIMUM) {
    		return WIDTH_MINIMUM;
    	} else if (width > WIDTH_MAXIMUM) {
    		return WIDTH_MAXIMUM;
    	}
    	return width; 
    }
    
    public int getPopupHeight(int height) {
    	return (height < HEIGHT_MINIMUM) ? HEIGHT_MINIMUM : height;
    }
}
