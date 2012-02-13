/*
 * Created on Jan 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.Movie;
import org.tolweb.hibernate.Sound;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ViewImageData extends BasePage implements IExternalPage, UserInjectable, 
		ImageInjectable, BaseInjectable {
    public abstract void setImage(NodeImage image);
    public abstract NodeImage getImage();
    public abstract void setMasterVersion(ImageVersion version);
    public abstract ImageVersion getMasterVersion();
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        try {
            Integer imageId = (Integer) parameters[0];
            NodeImage image = getImageDAO().getImageWithId(imageId.intValue());
            setImage(image);
            if (Movie.class.isInstance(image) && ((Movie) image).getUseFlashVideo()) {
            	setMasterVersion(getImageDAO().getMasterVersion(imageId));
            }
        } catch (Exception e) {
            // Just display a blank page if the image can't be found
        }
    }
    
	public boolean getCanEdit() {
        if (getContributor() != null) {
            return getPermissionChecker().checkEditingPermissionForImage(getContributor(), getImage());
        } else {
            return false;
        }
	}
	
	public String getImageUrl() {
	    ImageVersion maxVersion = getImageDAO().getMaxAllowedVersion(getImage());
	    return getImageUtils().getVersionUrl(maxVersion);
	}
	
	public boolean getIsImage() {
	    NodeImage image = getImage();
	    if (Sound.class.isInstance(image) || Movie.class.isInstance(image) || Document.class.isInstance(image)) {
	        return false;
	    } else {
	        return true;
	    }
	}
    public boolean getShowFlashVideo() {
    	return Movie.class.isInstance(getImage()) && ((Movie) getImage()).getUseFlashVideo();
    }
    public String getMediaActionString() {
        return getTextPreparer().getDetailedMediaActionString(getImage().getId(), getImage().getMediaType());
    }
    public int getMovieWidth() {
    	return getMasterVersion().getWidth();
    }
    public int getMovieHeight() {
    	return getMasterVersion().getHeight();
    }
    public String getMovieFilename() {
    	return getImageUtils().getFlashVideoUrl((Movie) getImage()); 
    }
    public String getThumbnailImage() {
        return getTextPreparer().getMediaImageThumbnail(getImage().getMediaType(), "floatleft");
    }
    public boolean getHasTitle() {
        return StringUtils.notEmpty(getImage().getTitle());
    }
    public String getTitle() {
        if (getImage() != null) {
            return "Tree of Life Web Project - Details for Media ID# " + getImage().getId();
        } else {
            return "";
        }
    }
    public String getUnescapedMediaTitle() {
    	if(getImage() != null && getHasTitle()) {
    		return StringEscapeUtils.unescapeHtml(getImage().getTitle());
    	}
    	return "";
    }
    public String getFullImageUrl() {
    	return getImageUtils().getImageUrl(getImage());
    }
}
