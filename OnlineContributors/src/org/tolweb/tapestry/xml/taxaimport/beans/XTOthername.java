package org.tolweb.tapestry.xml.taxaimport.beans;

public class XTOthername {
/*
    <othernames>
      <othername is-important="true" is-preferred="false" sequence="0" date="1871" italicize-name="1">
        <name>Heliconius cydno pachinus></name>
        <authority>Salvin</authority>
        <comments>[Lamas (2004) views &lt;em&gt;pachinus&lt;/em&gt; as a subspecies of &lt;em&gt;H. cydno&lt;/em&gt;]</comments>
      </othername>
    </othernames>
 */
	// xml attributes of XTOtherName
	private Boolean isImportant;
	private Boolean isPreferred;
	private Integer sequence; 
	private String date; 
	private Boolean italicizeName; 
	// child elements of XTOtherName
	private String name;
	private String authority;
	private String comments;
	
	public String toString() {
		return getName();
	}
	
	/**
	 * @return the isImportant
	 */
	public Boolean getIsImportant() {
		return isImportant;
	}
	/**
	 * @param isImportant the isImportant to set
	 */
	public void setIsImportant(Boolean isImportant) {
		this.isImportant = isImportant;
	}
	/**
	 * @return the isPreferred
	 */
	public Boolean getIsPreferred() {
		return isPreferred;
	}
	/**
	 * @param isPreferred the isPreferred to set
	 */
	public void setIsPreferred(Boolean isPreferred) {
		this.isPreferred = isPreferred;
	}
	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the italicizeName
	 */
	public Boolean getItalicizeName() {
		return italicizeName;
	}
	/**
	 * @param italicizeName the italicizeName to set
	 */
	public void setItalicizeName(Boolean italicizeName) {
		this.italicizeName = italicizeName;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the authority
	 */
	public String getAuthority() {
		return authority;
	}
	/**
	 * @param authority the authority to set
	 */
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
}
