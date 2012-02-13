package org.tolweb.btol;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @hibernate.class table="ChromatogramBatches"
 * @author dmandel
 *
 */
public class ChromatogramBatch extends NamedObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3111267244121767817L;
	private String description;
	private Set chromatograms = new HashSet(); 
	private int contributorId;
	private Date creationDate;
	
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
    /**
	 * @hibernate.set table="ChromatogramsToBatches" lazy="false" cascade="all"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.Chromatogram" column="chromatogramId"
	 * @hibernate.collection-key column="batchId"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 */	
	public Set getChromatograms() {
		return chromatograms;
	}
	public void setChromatograms(Set chromatograms) {
		this.chromatograms = chromatograms;
	}
	public void addToChromatograms(Chromatogram chro) {
		getChromatograms().add(chro);
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
	/**
	 * @hibernate.property
	 * @return
	 */	
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
