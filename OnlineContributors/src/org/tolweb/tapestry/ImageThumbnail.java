/*
 * Created on Sep 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.awt.Dimension;

import org.apache.tapestry.BaseComponent;
import org.tolweb.misc.ImageUtils;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ImageThumbnail extends BaseComponent implements ImageInjectable {
    private ImageUtils utils;

    public abstract boolean getIsPreview();
    public abstract NodeImage getImage();
    
    public ImageUtils getUtils() {
        if (utils == null) {
            utils = getImageUtils();
        }
        return utils;
    }

    public String getJavascriptImgWindowString() {
        if (getIsPreview()) {
            return "";
        } else {
            NodeImage img = getImage();
            Dimension dim = getUtils().getImageDimensions(img);
            int width = dim.width + 50;
            int height = dim.height + 50;
            return "<a href=\"#\" onclick=\"javascript:window.open('" + getUtils().getImageUrl(img) + "', '" + 
            	img.getId() + "', 'width=" + width + ",height=" + height + 
            	",scrollbars=no,menubar=no,location=no,status=no,toolbar=no'); return false;\">";
        }
    }
    
    public String getCloseJavascriptImgWindowString() {
        if (getIsPreview()) {
            return "";
        } else {
            return "</a>";
        }        
    }
}
