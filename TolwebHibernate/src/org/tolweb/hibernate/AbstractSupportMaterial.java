/*
 * Created on May 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import org.tolweb.treegrow.main.OrderedObject;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AbstractSupportMaterial extends OrderedObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7964786939748338792L;
	private String documentType;    
    /**
     * @hibernate.property column="support_order"
     */
    public int getOrder() {
        return super.getOrder();
    }
    /**
     * @hibernate.property column="document_type"
     * @return Returns the documentType.
     */
    public String getDocumentType() {
        return documentType;
    }
    /**
     * @param documentType The documentType to set.
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    private boolean isLearner;
    /**
     * @hibernate.property column="is_learner"
     * @return Returns the isLearner.
     */
    public boolean getIsLearner() {
        return isLearner;
    }
    public void setIsLearner(boolean isLearner) {
        this.isLearner = isLearner;
    }
    public boolean equals(Object o) {
        boolean equal = super.doEquals(o);
        return equal;
    }
    public int hashCode() {
    	return super.getHashCode();
    }    
}
