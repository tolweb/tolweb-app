/*
 * Created on Oct 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.OtherName;

/**
 * Extends properties of an OtherName from the TreeGrow context to 
 * defined additional properties and is mapped as part of a 
 * MappedNode via the Synonyms collection. 
 * 
 * @author dmandel
 */
public class MappedOtherName extends OtherName {
    /** used for serialization */
	private static final long serialVersionUID = 2560640210342366770L;
	private Long otherNameId;
    private Integer authorityYear;
    private boolean italicize;
    private String comment;
    private boolean isCommonName;
    private boolean isDontList; 
    
    /**
     * Indicates if the name should be included in the other names lists.
     * 
     * @return true, if the name is not marked "don't list" and not common; otherwise false
     */
    public boolean shouldDisplay() {
    	return !getIsDontList() && !getIsCommonName();
    }
    
    /**
     * @hibernate.property column="auth_year"
     * @return
     */    
    public Integer getAuthorityYear() {
        return authorityYear;
    }
    public void setAuthorityYear(Integer authorityYear) {
        this.authorityYear = authorityYear;
    }
    
    /**
     * 
     */
    public Long getOtherNameId() {
        return otherNameId;
    }
    public void setOtherNameId(Long otherNameId) {
        this.otherNameId = otherNameId;
    }
    
    /**
     * @hibernate.property column="name_order"
     */
    public int getOrder() {
        return super.getOrder();
    }
    public int hashCode() {
        return super.getHashCode();
    }
    public boolean equals(Object o) {
        return super.doEquals(o); // man - I don't get why order defines equality...
        // Answer to the above: 
        // this defined equality because of the way that hibernate tracks changes 
        // to an embedded/child object.  It goes off the hashCode generated, so an 
        // object changing it's name means that Hibernate will do a delete/insert  
        // instead of an update. 
    }
    
    /**
     * Determines if two instances have the same name
     * @param othername
     * @return true if the names are equal, otherwise false
     */
    public boolean nameEquals(MappedOtherName othername) {
    	if (othername != null) {
    		return this.getName().equals(othername.getName());
    	} else {
    		return false;
    	}
    }
    
    /**
     * Determines if two instances have the same authority information 
     * (authority & authority year)
     * @param othername
     * @return true if the names are equal, otherwise false
     */
    public boolean authorityInfoEquals(MappedOtherName othername) {
    	if (hasMissingAuthorityValues(othername) && hasAuthorityInfo()) {
    		return false;
    	} 
    	return authorityEquals(othername) && 
    		authYearEquals(othername) && 
    		nameEquals(othername);
    }
    
    /**
     * Merges the authority information present in the argument 
     * with the instance. 
     * 
     * The "seed" data exists in the passed argument with the 
     * result being data merged into the current instance. 
     * @param rhs othername to merge authority information with
     */
    public void mergeAuthorityInfo(MappedOtherName rhs) {
    	if (lhsAuthorityIsNull(getAuthority(), rhs.getAuthority())) {
    		setAuthority(rhs.getAuthority());
    	}
    	if (lhsAuthYearIsNull(getAuthorityYear(), rhs.getAuthorityYear())) {
    		setAuthorityYear(rhs.getAuthorityYear());
    	}
    }
    
    /**
     * Indicates that some authority information is missing. 
     * 
     * @return true if either authority or authority year is null, 
     * otherwise false
     */
    public boolean hasIncompleteAuthorityInfo() {
    	return StringUtils.isEmpty(getAuthority()) || getAuthorityYear() == null;
    }

    /**
     * Indicates complete authority information present. 
     * 
     * @return true if the authority & authority year are present; otherwise false
     */
    public boolean hasAuthorityInfo() {
    	return !hasIncompleteAuthorityInfo();
    }
    
    /**
     * Indicate that all authority information is missing. 
     * 
     * @return true if both authority & authority year are null, 
     * otherwise false 
     */
    public boolean hasNoAuthorityInfo() {
    	return StringUtils.isEmpty(getAuthority()) && getAuthorityYear() == null;
    }
    
    /**
     * Determines if the two instances have the same authority value
     * 
     * if the case they both have null for the authority true is returned. 
     * 
     * @param othername
     * @return true if the authority values are equal, otherwise false 
     */
    private boolean authorityEquals(MappedOtherName othername) {
    	if (othername == null) {
    		return false; 
    	} else if (bothAuthorityIsNull(getAuthority(), othername.getAuthority())) {
    		return true;
    	} else if (getAuthority() == null && othername.getAuthority() != null) {
    		return false;
    	}
    	return getAuthority().equals(othername.getAuthority());
    }
    
    /**
     * Determines if the two instances have the same authority year
     * 
     * if the case they both have null for the authority year true is returned.
     * 
     * @param othername
     * @return true if the authority year values are equal, otherwise false
     */
    private boolean authYearEquals(MappedOtherName othername) {
    	if (othername == null) {
    		return false;
    	} else if (bothAuthYearIsNull(getAuthorityYear(), othername.getAuthorityYear())) {
    		return true;
    	} else if (getAuthorityYear() == null && othername.getAuthorityYear() != null) {
    		return false;
    	}
    	return getAuthorityYear().equals(othername.getAuthorityYear());
    }
    
    /**
     * Indicates if one of the two instances is missing authority information 
     * @param othername
     * @return true if authority information is missing, otherwise false
     */
    private boolean hasMissingAuthorityValues(MappedOtherName othername) {
    	return othername != null
				&& (this.hasIncompleteAuthorityInfo() || 
					othername.hasIncompleteAuthorityInfo()); 
    }

    /**
     * Indicates that the left-hand side (lhs) is null and right-hand side is not
     * @param lhsAuthority left-hand side value
     * @param rhsAuthority right-hand side value
     * @return true if the left-hand side is null, otherwise false
     */
    private boolean lhsAuthorityIsNull(String lhsAuthority, String rhsAuthority) {
    	return StringUtils.isEmpty(lhsAuthority) && rhsAuthority != null;
    }

    /**
     * Indicates that both values are null
     * @param lhsAuthority left-hand side value
     * @param rhsAuthority right-hand side value
     * @return true if both argument are null, otherwise false
     */
    private boolean bothAuthorityIsNull(String lhsAuthority, String rhsAuthority) {
    	return lhsAuthority == null && rhsAuthority == null;
    }
    
    /**
     * Indicates that the left-hand side (lhs) is null and right-hand side is not 
     * @param lhsYear left-hand side value
     * @param rhsYear right-hand side value
     * @return true if the left-hand side is null, otherwise false
     */
    private boolean lhsAuthYearIsNull(Integer lhsYear, Integer rhsYear) {
    	return lhsYear == null && rhsYear != null;
    }

    /**
     * Indicates that both values are null
     * @param lhsYear left-hand side value
     * @param rhsYear right-hand side value
     * @return true if both argument are null, otherwise false
     */
    private boolean bothAuthYearIsNull(Integer lhsYear, Integer rhsYear) {
    	return lhsYear == null && rhsYear == null;
    }    
    
    /**
     * Provides a string representation of the instance.
     * @return a string representing the state of the instance. 
     */
    public String toString() {
        return "OtherName with name: " + getName() + " order: " + getOrder()
				+ " authority: " + getAuthority() + " date: "
				+ getAuthorityYear() + " important: " + getIsImportant()
				+ " preferred: " + getIsPreferred();
    }
    /**
     * @hibernate.property
     * @return Returns the comment.
     */
    public String getComment() {
        return comment;
    }
    /**
     * @param comment The comment to set.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
    /**
     * @hibernate.property
     * @return Returns the italicize.
     */
    public boolean getItalicize() {
        return italicize;
    }
    /**
     * @param italicize The italicize to set.
     */
    public void setItalicize(boolean italicize) {
        this.italicize = italicize;
    }

    /**
     * @hibernate.property column="is_commonname"
     * @return
     */
	public boolean getIsCommonName() {
		return isCommonName;
	}
	
	public void setIsCommonName(boolean isCommonName) {
		this.isCommonName = isCommonName;
	}
	
	/**
     * @hibernate.property column="is_dontlist"
	 * @return
	 */
	public boolean getIsDontList() {
		return isDontList;
	}
	
	public void setIsDontList(boolean isDontList) {
		this.isDontList = isDontList;
	}
}
