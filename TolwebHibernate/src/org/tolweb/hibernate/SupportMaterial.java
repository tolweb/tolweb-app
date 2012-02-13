/*
 * Created on May 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;

import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SupportMaterial extends AbstractSupportMaterial implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8231719871426791630L;
	private String title;
    private String text;
    /**
     * @hibernate.property
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }
    /**
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }
    /**
     * @hibernate.property
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    public String toString() {
	    return "support material with order: " + getOrder() + " and title: " + getTitle();
	}
    
    public boolean getHasContent() {
        return StringUtils.notEmpty(getText());
    }
}
