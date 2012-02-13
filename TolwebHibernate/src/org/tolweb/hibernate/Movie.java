/*
 * Created on Apr 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.tolweb.treegrow.main.DescriptiveMedia;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.subclass discriminator-value="2"
 */
public class Movie extends NodeImage implements DescriptiveMedia {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5023537282893303151L;
	protected String description;
    protected String runningTime;
    protected boolean useFlashVideo;

    
    public static final List MOVIE_TYPES_LIST;
    
    static {
        MOVIE_TYPES_LIST = new ArrayList();
        MOVIE_TYPES_LIST.add("Documentary");
        MOVIE_TYPES_LIST.add("Staged");
        MOVIE_TYPES_LIST.add("Animation");
        MOVIE_TYPES_LIST.add("Mixed");
    }
    
    public void setValues(NodeImage other, boolean doThumbnail, boolean copyIdsAndLocs) {
        super.setValues(other, doThumbnail, copyIdsAndLocs);
        if (Movie.class.isInstance(other)) {
            Movie otherMov = (Movie) other;
            setDescription(otherMov.getDescription());
            setTitle(otherMov.getTitle());
            setRunningTime(otherMov.getRunningTime());
        }
    }
    
    /**
     * @hibernate.property
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @hibernate.property column="running_time"
     * @return Returns the runningTime.
     */
    public String getRunningTime() {
        return runningTime;
    }
    /**
     * @param runningTime The runningTime to set.
     */
    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }
    public String getMediaTypeDescription() {
        return "movie";
    }
    public int getMediaType() {
        return NodeImage.MOVIE;
    }
    /**
     * @hibernate.property
     * @return
     */
	public boolean getUseFlashVideo() {
		return useFlashVideo;
	}
	public void setUseFlashVideo(boolean getUseFlashVideo) {
		this.useFlashVideo = getUseFlashVideo;
	}
}
