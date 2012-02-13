/*
 * Created on May 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.main;

import java.io.Serializable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="Languages"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class Languages implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6908975227307423819L;
	private Long id;
    private boolean english;
    private boolean spanish;
    private boolean french;
    private boolean german;
    private String otherLanguage;
    
    public void copyValues(Languages other) {
        english = other.getEnglish();
        spanish = other.getSpanish();
        french = other.getFrench();
        german = other.getGerman();
        if (other.getOtherLanguage() != null) {
            otherLanguage = new String(other.getOtherLanguage());
        } else {
            otherLanguage = null;
        }
    }
    
    /**
     * @hibernate.property
     * @return Returns the english.
     */
    public boolean getEnglish() {
        return english;
    }
    /**
     * @param english The english to set.
     */
    public void setEnglish(boolean english) {
        this.english = english;
    }
    /**
     * @hibernate.property
     * @return Returns the french.
     */
    public boolean getFrench() {
        return french;
    }
    /**
     * @param french The french to set.
     */
    public void setFrench(boolean french) {
        this.french = french;
    }
    /**
     * @hibernate.property
     * @return Returns the german.
     */
    public boolean getGerman() {
        return german;
    }
    /**
     * @param german The german to set.
     */
    public void setGerman(boolean german) {
        this.german = german;
    }
    /**
     * @hibernate.id generator-class="native" column="language_id"
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @hibernate.property column="other_language"
     * @return Returns the otherLanguage.
     */
    public String getOtherLanguage() {
        return otherLanguage;
    }
    /**
     * @param otherLanguage The otherLanguage to set.
     */
    public void setOtherLanguage(String otherLanguage) {
        this.otherLanguage = otherLanguage;
    }
    /**
     * @hibernate.property
     * @return Returns the spanish.
     */
    public boolean getSpanish() {
        return spanish;
    }
    /**
     * @param spanish The spanish to set.
     */
    public void setSpanish(boolean spanish) {
        this.spanish = spanish;
    }
    public boolean getHasAnyFields() {
    	return getEnglish() || getSpanish() || getFrench() ||
    		getGerman() || StringUtils.notEmpty(getOtherLanguage());
    }
}
