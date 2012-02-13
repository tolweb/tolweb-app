package org.tolweb.btol;

import org.tolweb.hibernate.PersistentObject;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @hibernate.class table="AdditionalFields"
 * @hibernate.cache usage="nonstrict-read-write"
 * @author dmandel
 * Object used for storing additional sorts of info that is meant to 
 * be extensible for general tolweb extension
 */
public class AdditionalFields extends PersistentObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2646620211988615036L;
	public static final int NO_TIER_SET = -2;
	public static final int LOW_PRIORITY = 0;
	public static final int MEDIUM_PRIORITY = 1;
	public static final int HIGH_PRIORITY = 2;
	public static final int DONT_HAVE_ANY = 0;
	public static final int HAVE_SOME = 1;
	public static final int HAVE_ENOUGH = 2;
	public static final int SPECIMENS_TO_LAB = 3;
	public static final int HAVE_DNA = 4;
	// values for the mt genome state column
	public static final int NO_MT_GENOME = 0;
	public static final int MT_GENOME_FIRST_YEAR = 1;
	public static final int HAVE_MT_GENOME = 2;
	public static final int MT_GENOME_NOT_NEEDED = -1;
	// values for the microCT State column
	public static final int MICRO_CT_NOT_NEEDED = 0;
	public static final int MICRO_CT_NEED_SPECIMEN = 1;
	public static final int MICRO_CT_HAS_SPECIMEN = 2;
	public static final int MICRO_CT_COMPLETED = 3;

	private Long nodeId;
	private int dnaPriority;
	private int adultPriority;
	private int larvaePriority;
	private String location;
	private String habitat;
	private int tier;	
	private int mtGenomeState;
	private String tierNotes;
	private Contributor larvalPosessionPerson;
	private Contributor larvalDestinationPerson;
	private Contributor dnaPossessionPerson;
	private Contributor dnaDestinationPerson;
	private Contributor adultPossessionPerson;
	private Contributor adultDestinationPerson;
	private Integer larvalPossessionPersonId;
	private Integer larvalDestinationPersonId;
	private Integer dnaPossessionPersonId;
	private Integer dnaDestinationPersonId;
	private Integer adultPossessionPersonId;
	private Integer adultDestinationPersonId;
	private String larvalNotes;
	private String dnaNotes;
	private String adultNotes;
	private String geographicDistribution;
	private int microCTState;
	private Contributor microCTPossessionPerson;
	private Integer microCTPossessionPersonId;
	private String microCTNotes;
	
	/**
	 * cached in memory to store query results
	 */
	private boolean hasPage;
    
	private int larvalSpecimensState;
	private int dnaSpecimensState;
	private int adultSpecimensState;
	
	/**
	 * @hibernate.property
	 */
	public int getAdultPriority() {
		return adultPriority;
	}
	public void setAdultPriority(int adultPriority) {
		this.adultPriority = adultPriority;
	}
	/**
	 * @hibernate.property
	 */	
	public int getDnaPriority() {
		return dnaPriority;
	}
	public void setDnaPriority(int dnaPriority) {
		this.dnaPriority = dnaPriority;
	}
	/**
	 * @hibernate.property
	 */	
	public int getLarvaePriority() {
		return larvaePriority;
	}
	public void setLarvaePriority(int larvaePriority) {
		this.larvaePriority = larvaePriority;
	}
	/**
	 * @hibernate.property
	 */	
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	/**
	 * @hibernate.property
	 */	
	public String getHabitat() {
		return habitat;
	}
	public void setHabitat(String habitat) {
		this.habitat = habitat;
	}
	/**
	 * @hibernate.property
	 */	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @hibernate.property
	 */	
	public String getTierNotes() {
		return tierNotes;
	}
	public void setTierNotes(String notes) {
		this.tierNotes = notes;
	}
	public boolean getHasTierNotes() {
		return StringUtils.notEmpty(getTierNotes());
	}
	/**
	 * @hibernate.property
	 */	
	public int getTier() {
		return tier;
	}
	public void setTier(int tier) {
		this.tier = tier;
	}
	public boolean getHasTierAssigned() {
		return getTier() != NO_TIER_SET;
	}
	public boolean getHasPage() {
		return hasPage;
	}
	public void setHasPage(boolean hasAdultSpecimens) {
		this.hasPage = hasAdultSpecimens;
	}
	/**
	 * Whether any fields are
	 * @return
	 */
	public boolean getHasAnySamplingInfoAssigned() {
		return getHasTierAssigned() || getHasLarvalSpecimensAssigned() || getHasDnaSpecimensAssigned()
			|| getHasAdultSpecimensAssigned();
		
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public int getLarvalSpecimensState() {
		return larvalSpecimensState;
	}
	public void setLarvalSpecimensState(int value) {
		this.larvalSpecimensState = value;
	}
	public boolean getHasLarvalSpecimens() {
		return getLarvalSpecimensState() != DONT_HAVE_ANY;
	}
	/**
	 * Whether there are some specimens available or whether
	 * someone is marked as having something 
	 * @return
	 */
	public boolean getHasLarvalSpecimensAssigned() {
		return getHasLarvalSpecimens() || getLarvalPossessionPerson() != null;
	}	
	public boolean getHasAnyLarvalSpecimenInfo() {
		return getHasLarvalSpecimensAssigned() || getLarvalDestinationPerson() != null;
	}

	public Contributor getLarvalPossessionPerson() {
		return larvalPosessionPerson;
	}
	public void setLarvalPossessionPerson(Contributor larvaeContributor) {
		this.larvalPosessionPerson = larvaeContributor;
		setLarvalPossessionPersonId(getSafeId(larvaeContributor));
	}
	public Contributor getLarvalDestinationPerson() {
		return larvalDestinationPerson;
	}
	public void setLarvalDestinationPerson(Contributor larvaePersonWorking) {
		this.larvalDestinationPerson = larvaePersonWorking;
		setLarvalDestinationPersonId(getSafeId(larvaePersonWorking));
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public String getLarvalNotes() {
		return larvalNotes;
	}
	public void setLarvalNotes(String larvalNotes) {
		this.larvalNotes = larvalNotes;
	}
	public boolean getHasLarvalNotes() {
		return StringUtils.notEmpty(getLarvalNotes());
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public Integer getLarvalDestinationPersonId() {
		return larvalDestinationPersonId;
	}
	public void setLarvalDestinationPersonId(Integer larvalDestinationPersonId) {
		this.larvalDestinationPersonId = larvalDestinationPersonId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public Integer getLarvalPossessionPersonId() {
		return larvalPossessionPersonId;
	}
	public void setLarvalPossessionPersonId(Integer larvalPossessionPersonId) {
		this.larvalPossessionPersonId = larvalPossessionPersonId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public Integer getAdultDestinationPersonId() {
		return adultDestinationPersonId;
	}
	public void setAdultDestinationPersonId(Integer adultDestinationPersonId) {
		this.adultDestinationPersonId = adultDestinationPersonId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public String getAdultNotes() {
		return adultNotes;
	}
	public void setAdultNotes(String adultNotes) {
		this.adultNotes = adultNotes;
	}
	public boolean getHasAdultNotes() {
		return StringUtils.notEmpty(getAdultNotes());
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public Integer getAdultPossessionPersonId() {
		return adultPossessionPersonId;
	}
	public void setAdultPossessionPersonId(Integer adultPossessionPersonId) {
		this.adultPossessionPersonId = adultPossessionPersonId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public int getAdultSpecimensState() {
		return adultSpecimensState;
	}
	public void setAdultSpecimensState(int adultSpecimensState) {
		this.adultSpecimensState = adultSpecimensState;
	}
	public boolean getHasAdultSpecimens() {
		return getAdultSpecimensState() != AdditionalFields.DONT_HAVE_ANY;
	}
	/**
	 * Don't take into account if the destination person is assigned?
	 * @return
	 */
	public boolean getHasAdultSpecimensAssigned() {
		return getHasAdultSpecimens() || getAdultPossessionPerson() != null;
	}	
	/**
	 * @hibernate.property
	 * @return
	 */		
	public Integer getDnaDestinationPersonId() {
		return dnaDestinationPersonId;
	}
	public void setDnaDestinationPersonId(Integer dnaDestinationPersonId) {
		this.dnaDestinationPersonId = dnaDestinationPersonId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public String getDnaNotes() {
		return dnaNotes;
	}
	public void setDnaNotes(String dnaNotes) {
		this.dnaNotes = dnaNotes;
	}
	public boolean getHasDnaNotes() {
		return StringUtils.notEmpty(getDnaNotes());
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public Integer getDnaPossessionPersonId() {
		return dnaPossessionPersonId;
	}
	public void setDnaPossessionPersonId(Integer dnaPossessionPersonId) {
		this.dnaPossessionPersonId = dnaPossessionPersonId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public int getDnaSpecimensState() {
		return dnaSpecimensState;
	}
	public void setDnaSpecimensState(int dnaSpecimensState) {
		this.dnaSpecimensState = dnaSpecimensState;
	}
	public boolean getHasDnaSpecimens() {
		return getDnaSpecimensState() != DONT_HAVE_ANY;
	}	
	public boolean getHasDnaSpecimensAssigned() {
		return getHasDnaSpecimens() || getDnaPossessionPerson() != null;
	}
	public Contributor getAdultDestinationPerson() {
		return adultDestinationPerson;
	}
	public void setAdultDestinationPerson(Contributor adultDestinationPerson) {
		this.adultDestinationPerson = adultDestinationPerson;
		setAdultDestinationPersonId(getSafeId(adultDestinationPerson));
	}
	public Contributor getAdultPossessionPerson() {
		return adultPossessionPerson;
	}
	public void setAdultPossessionPerson(Contributor adultPossessionPerson) {
		this.adultPossessionPerson = adultPossessionPerson;
		setAdultPossessionPersonId(getSafeId(adultPossessionPerson));
	}
	public Contributor getDnaDestinationPerson() {
		return dnaDestinationPerson;
	}
	public void setDnaDestinationPerson(Contributor dnaDestinationPerson) {
		this.dnaDestinationPerson = dnaDestinationPerson;
		setDnaDestinationPersonId(getSafeId(dnaDestinationPerson));
	}
	public Contributor getDnaPossessionPerson() {
		return dnaPossessionPerson;
	}
	public void setDnaPossessionPerson(Contributor dnaPossessionPerson) {
		this.dnaPossessionPerson = dnaPossessionPerson;
		setDnaPossessionPersonId(getSafeId(dnaPossessionPerson));
	}
	public void clearAllSamplingInformation() {
		setTier(NO_TIER_SET);
		setTierNotes(null);
		setDnaNotes(null);
		setDnaDestinationPerson(null);
		setDnaPossessionPerson(null);
		setDnaSpecimensState(DONT_HAVE_ANY);
		setLarvalNotes(null);
		setLarvalDestinationPerson(null);
		setLarvalPossessionPerson(null);
		setLarvalSpecimensState(DONT_HAVE_ANY);
		setAdultNotes(null);
		setAdultDestinationPerson(null);
		setAdultPossessionPerson(null);
		setAdultSpecimensState(DONT_HAVE_ANY);
		setMtGenomeState(MT_GENOME_NOT_NEEDED);
		setMicroCTState(MICRO_CT_NOT_NEEDED);
		setMicroCTNotes(null);
		setMicroCTPossessionPerson(null);
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public int getMtGenomeState() {
		return mtGenomeState;
	}
	public void setMtGenomeState(int mtGenomeState) {
		this.mtGenomeState = mtGenomeState;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getGeographicDistribution() {
		return geographicDistribution;
	}
	public void setGeographicDistribution(String geographicDistribution) {
		this.geographicDistribution = geographicDistribution;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public int getMicroCTState() {
		return microCTState;
	}
	public void setMicroCTState(int microCTState) {
		this.microCTState = microCTState;
	}
	public boolean getHasMicroCTSpecimens() {
		return getMicroCTState() != MICRO_CT_NEED_SPECIMEN;
	}	
	/**
	 * @hibernate.property
	 * @return
	 */		
	public String getMicroCTNotes() {
		return microCTNotes;
	}
	public void setMicroCTNotes(String microCTNotes) {
		this.microCTNotes = microCTNotes;
	}
	public boolean getHasMicroCTNotes() {
		return StringUtils.notEmpty(getMicroCTNotes());
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public Integer getMicroCTPossessionPersonId() {
		return microCTPossessionPersonId;
	}
	public void setMicroCTPossessionPersonId(Integer microCTPossessionPersonId) {
		this.microCTPossessionPersonId = microCTPossessionPersonId;
	}
	public Contributor getMicroCTPossessionPerson() {
		return microCTPossessionPerson;
	}
	public void setMicroCTPossessionPerson(Contributor microCTPossessionPerson) {
		this.microCTPossessionPerson = microCTPossessionPerson;
		setMicroCTPossessionPersonId(getSafeId(microCTPossessionPerson));
	}	
}