package org.tolweb.btol;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.tolweb.hibernate.PersistentObject;

/**
 * @hibernate.class table="PCRReactions"
 * @author dmandel
 */
public class PCRReaction extends NotesObject implements Comparable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3581801916548571061L;
	public static final int SINGLE_BAND = 0;
	public static final int DOUBLE_BAND = 1;
	public static final int MULTIPLE_BAND = 2;
	public static final int SMEAR = 3;
	public static final int BLANK = 4;
	
	private SpecimenExtraction extraction;
	private boolean isNegativeControl;
	private String btolCode;
	private Date sequencingDate;
	private String sequencingResults;
	private int reactionResult;	
	private int bandIntensity;
	private Set chromatograms = new HashSet();

	/**
	 * @hibernate.many-to-one column="extractionId" class="org.tolweb.btol.SpecimenExtraction" cascade="none"
	 * @return
	 */
	public SpecimenExtraction getExtraction() {
		return extraction;
	}
	public void setExtraction(SpecimenExtraction extraction) {
		this.extraction = extraction;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getIsNegativeControl() {
		return isNegativeControl;
	}
	public void setIsNegativeControl(boolean isNegativeControl) {
		this.isNegativeControl = isNegativeControl;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getBtolCode() {
		return btolCode;
	}
	public void setBtolCode(String btolCode) {
		this.btolCode = btolCode;
	}
	/**
	 * Set automatically when results are fetched from the sequencing facility
	 * 
	 * @hibernate.property
	 * @return
	 */		
	public Date getSequencingDate() {
		return sequencingDate;
	}
	public void setSequencingDate(Date sequenceDate) {
		this.sequencingDate = sequenceDate;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getSequencingResults() {
		return sequencingResults;
	}
	public void setSequencingResults(String sequencingResults) {
		this.sequencingResults = sequencingResults;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public int getBandIntensity() {
		return bandIntensity;
	}
	public void setBandIntensity(int bandIntensity) {
		this.bandIntensity = bandIntensity;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public int getReactionResult() {
		return reactionResult;
	}
	public void setReactionResult(int bandResult) {
		this.reactionResult = bandResult;
	}
	public String getReactionResultString() {
		if (getBandIntensity() > 0) {
			switch (getReactionResult()) {
			case DOUBLE_BAND: return "double";
			case MULTIPLE_BAND: return "multiple";
			case SMEAR: return "smear";
			case BLANK: return "blank";
			default: return "single";		
			}
		} else {
			return "";
		}
	}
	public int compareTo(Object arg0) {
		if (arg0 == null || !PersistentObject.class.isInstance(arg0)) {
			return 1;
		} else {
			return doCompareBasedOnId((PersistentObject) arg0);
		}
	}
	public void addToChromatograms(Chromatogram chro) {
		getChromatograms().add(chro);
	}

    /**
	 * @hibernate.set table="ReactionChromatograms" lazy="false" cascade="all"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.Chromatogram" column="chromatogramId"
	 * @hibernate.collection-key column="reactionId"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 */    
	public Set getChromatograms() {
		return chromatograms;
	}
	public void setChromatograms(Set synonyms) {
		this.chromatograms = synonyms;
	}
}
