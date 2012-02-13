/*
 * Created on Oct 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.util.Set;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="Glossary"
 */
public class GlossaryEntry {
	private boolean hide;
	private String word;
	private String definition;
	private Long glossaryId;
	private Set synonyms;
	private Set plurals; 
	
	/**
	 * @hibernate.property column="def"
	 * @return Returns the definition.
	 */
	public String getDefinition() {
		return definition;
	}
	/**
	 * @param definition The definition to set.
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	/**
	 * @hibernate.id generator-class="native" column="id" unsaved-value="null"
	 * @return Returns the glossaryId.
	 */
	public Long getGlossaryId() {
		return glossaryId;
	}
	/**
	 * @param glossaryId The glossaryId to set.
	 */
	public void setGlossaryId(Long glossaryId) {
		this.glossaryId = glossaryId;
	}
	/**
	 * @hibernate.property
	 * @return Returns the hide.
	 */
	public boolean getHide() {
		return hide;
	}
	/**
	 * @param hide The hide to set.
	 */
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	/**
	 * @hibernate.property
	 * @return Returns the word.
	 */
	public String getWord() {
		return word;
	}
	/**
	 * @param word The word to set.
	 */
	public void setWord(String word) {
		this.word = word;
	}
	/**
	 * @hibernate.set table="Glossary_Plurals" lazy="false" sort="natural"
	 * @hibernate.collection-element type="string" column="plural"
	 * @hibernate.collection-key column="glossary_id"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 * @return Returns the plurals.
	 */
	public Set getPlurals() {
		return plurals;
	}
	/**
	 * @param plurals The plurals to set.
	 */
	public void setPlurals(Set plurals) {
		this.plurals = plurals;
	}
	/**
	 * @hibernate.set table="Glossary_Synonyms" lazy="false" sort="natural"
	 * @hibernate.collection-element type="string" column="syn"
	 * @hibernate.collection-key column="glossary_id"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 * @return Returns the synonyms.
	 */
	public Set getSynonyms() {
		return synonyms;
	}
	/**
	 * @param synonyms The synonyms to set.
	 */
	public void setSynonyms(Set synonyms) {
		this.synonyms = synonyms;
	}
	
	public String toString() {
		return this.word;
	}
}
