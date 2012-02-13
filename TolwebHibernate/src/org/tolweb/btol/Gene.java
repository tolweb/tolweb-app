package org.tolweb.btol;

import java.io.Serializable;

/**
 * @hibernate.class table="Genes"
 * @author dmandel
 *
 */
public class Gene extends NamedObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1883393699489728799L;
	private String drosophilaName;
	private Integer triboliumLength;
	private String synonyms;
	private boolean hidden;
	private boolean important;
	private String abbreviatedName;
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getDrosophilaName() {
		return drosophilaName;
	}
	public void setDrosophilaName(String drosophilaName) {
		this.drosophilaName = drosophilaName;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public Integer getTriboliumLength() {
		return triboliumLength;
	}
	public void setTriboliumLength(Integer tribolium) {
		this.triboliumLength = tribolium;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public String getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(String synonym) {
		this.synonyms = synonym;
	}
	/**
	 * used for things that aren't really genes but we want them to show up as such 
	 * (for now it's the Mitochondrial genome though it may be other things later)
	 * @hibernate.property
	 * @return
	 */
	public boolean getHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getImportant() {
		return important;
	}
	public void setImportant(boolean primary) {
		this.important = primary;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getAbbreviatedName() {
		return abbreviatedName;
	}
	public void setAbbreviatedName(String abbreviatedName) {
		this.abbreviatedName = abbreviatedName;
	}
}
