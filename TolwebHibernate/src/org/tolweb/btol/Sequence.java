package org.tolweb.btol;

import java.util.Set;

import org.tolweb.hibernate.PersistentObject;

/**
 * @hibernate.class table="Sequences"
 * @author dmandel
 *
 */
public class Sequence extends PersistentObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7627085472252404351L;
	private String fasta;
	private Set chromatograms;
	private int contributorId;
	
    /**
	 * @hibernate.set table="ChromatogramsToSequences" lazy="false" cascade="all"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.Chromatogram" column="chromatogramId"
	 * @hibernate.collection-key column="sequenceId"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 */	
	public Set getChromatograms() {
		return chromatograms;
	}
	public void setChromatograms(Set chromatograms) {
		this.chromatograms = chromatograms;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getFasta() {
		return fasta;
	}
	public void setFasta(String fasta) {
		this.fasta = fasta;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public int getContributorId() {
		return contributorId;
	}
	public void setContributorId(int contributorId) {
		this.contributorId = contributorId;
	}
}
