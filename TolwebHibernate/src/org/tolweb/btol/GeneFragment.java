package org.tolweb.btol;

import java.io.Serializable;

/**
 * A GeneFragment is a small piece of a gene
 * 
 * @hibernate.class table="GeneFragments"
 * @author lenards
 *
 */
public class GeneFragment extends NamedObject implements Serializable {
	public static final int NO_TIER_REQUIREMENT = -1;
	public static final int TIER0_REQUIREMENT = 0;
	public static final int TIER1_REQUIREMENT = 1;
	public static final int TIER2_REQUIREMENT = 2;
	public static final int TIER3_REQUIREMENT = 3;
	
	private static final long serialVersionUID = 3630812968214276436L;
	
	private Gene gene; 
	private boolean important;
	private String abbreviatedName;
	private int requiredForTier;
	
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
	
	/**
	 * @hibernate.many-to-one column="geneId" class="org.tolweb.btol.Gene" cascade="none" 
	 * @return the Gene which this is a fragment of
	 */
	public Gene getGene() {
		return gene;
	}
	
	public void setGene(Gene gene) {
		this.gene = gene;
	}
	
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean isImportant() {
		return important;
	}
	
	public void setImportant(boolean important) {
		this.important = important;
	}
	
	/**
	 * @hibernate.property
	 * @return
	 */
	public int getRequiredForTier() {
		return requiredForTier;
	}
	public void setRequiredForTier(int requiredForTier) {
		this.requiredForTier = requiredForTier;
	}
}
