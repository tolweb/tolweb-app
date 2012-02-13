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
 * @hibernate.subclass discriminator-value="1"
 */
public class Sound extends NodeImage implements DescriptiveMedia {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8623357879314017371L;

	public static final List SOUND_TYPES_LIST;
    
    // These are the same as the Movie subclass but xdoclet is too retarded
    // to figure out inheritance in generating mapping files.  Once we upgrade
    // to the new version of hibernate and use annotations, this should be fixed
    protected String description;
    protected String runningTime;
    private boolean isOrganism;
    private boolean isEnvironmental;
    private boolean isNarrative;
    
    static {
        SOUND_TYPES_LIST = new ArrayList();
        SOUND_TYPES_LIST.add("Natural Sound");
        SOUND_TYPES_LIST.add("Speech");
        SOUND_TYPES_LIST.add("Music");
        SOUND_TYPES_LIST.add("Artificial Sound Effects");
        SOUND_TYPES_LIST.add("Mixed");
    }
    
    public void setValues(NodeImage other, boolean doThumbnail, boolean copyIdsAndLocs) {
        super.setValues(other, doThumbnail, copyIdsAndLocs);
        if (Sound.class.isInstance(other)) {
            Sound otherSound = (Sound) other;
            setDescription(otherSound.getDescription());
            setTitle(otherSound.getTitle());
            setRunningTime(otherSound.getRunningTime());
            setIsOrganism(otherSound.getIsOrganism());
            setIsEnvironmental(otherSound.getIsEnvironmental());
            setIsNarrative(otherSound.getIsNarrative());
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
    /**
     * @hibernate.property column="is_environmental"
     * @return Returns the isEnvironmental.
     */
    public boolean getIsEnvironmental() {
        return isEnvironmental;
    }
    /**
     * @param isEnvironmental The isEnvironmental to set.
     */
    public void setIsEnvironmental(boolean isEnvironmental) {
        this.isEnvironmental = isEnvironmental;
    }
    /**
     * @hibernate.property column="is_narrative"
     * @return Returns the isNarrative.
     */
    public boolean getIsNarrative() {
        return isNarrative;
    }
    /**
     * @param isNarrative The isNarrative to set.
     */
    public void setIsNarrative(boolean isNarrative) {
        this.isNarrative = isNarrative;
    }
    /**
     * @hibernate.property column="is_organism"
     * @return Returns the isOrganism.
     */
    public boolean getIsOrganism() {
        return isOrganism;
    }
    /**
     * @param isOrganism The isOrganism to set.
     */
    public void setIsOrganism(boolean isOrganism) {
        this.isOrganism = isOrganism;
    }
    
    public String getMediaTypeDescription() {
        return "sound";
    }
    public int getMediaType() {
        return NodeImage.SOUND;
    }
}
