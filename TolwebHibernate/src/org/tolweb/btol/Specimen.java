package org.tolweb.btol;

import org.tolweb.treegrow.main.StringUtils;

/**
 * @hibernate.class table="Specimens"
 * @author dmandel
 *
 */
public class Specimen extends FlexibleDateObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8137836098414416720L;
	private String family;
	private String genus;
	private String species;
	private String subfamily;
	private String tribe;
	
	private String identificationStatus;
	private String identificationNotes;
	private Long nodeId;
	private String collectionDataSource;
	private String collectionDataSourceId;
	private String collectionTime;
	private String country;
	private String adminDivision1;
	private String adminDivision2;
	private String location1;
	private String location2;
	private String latitude;
	private String longitude;
	private String elevation;
	private String habitat;
	private String collectionMethod;
	private String preservationMethod;
	private String currentCondition;
	private String lifeStage;
	private String sex;
	private String collector;
	private String credit;
	private String collection;
	
	public String getBtolSpecimenId() {
		return "BTOL" + getId();
	}
	
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getGenus() {
		return genus;
	}
	public void setGenus(String genus) {
		this.genus = genus;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public String getSubfamily() {
		return subfamily;
	}
	public void setSubfamily(String subfamily) {
		this.subfamily = subfamily;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public String getTribe() {
		return tribe;
	}
	public void setTribe(String tribe) {
		this.tribe = tribe;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public String getCollectionDataSourceId() {
		return collectionDataSourceId;
	}
	public void setCollectionDataSourceId(String collectionString) {
		this.collectionDataSourceId = collectionString;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getCollectionTime() {
		return collectionTime;
	}
	public void setCollectionTime(String collectionTime) {
		this.collectionTime = collectionTime;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getAdminDivision1() {
		return adminDivision1;
	}
	public void setAdminDivision1(String adminDivision1) {
		this.adminDivision1 = adminDivision1;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getAdminDivision2() {
		return adminDivision2;
	}
	public void setAdminDivision2(String adminDivision2) {
		this.adminDivision2 = adminDivision2;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getCollectionDataSource() {
		return collectionDataSource;
	}
	public void setCollectionDataSource(String collectionDataSource) {
		this.collectionDataSource = collectionDataSource;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getCollectionMethod() {
		return collectionMethod;
	}
	public void setCollectionMethod(String collectionMethod) {
		this.collectionMethod = collectionMethod;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getCollector() {
		return collector;
	}
	public void setCollector(String collector) {
		this.collector = collector;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getCurrentCondition() {
		return currentCondition;
	}
	public void setCurrentCondition(String currentCondition) {
		this.currentCondition = currentCondition;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getElevation() {
		return elevation;
	}
	public void setElevation(String elevation) {
		this.elevation = elevation;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getHabitat() {
		return habitat;
	}
	public void setHabitat(String habitat) {
		this.habitat = habitat;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getIdentificationNotes() {
		return identificationNotes;
	}
	public void setIdentificationNotes(String identificationNotes) {
		this.identificationNotes = identificationNotes;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getIdentificationStatus() {
		return identificationStatus;
	}
	public void setIdentificationStatus(String identificationStatus) {
		this.identificationStatus = identificationStatus;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getLifeStage() {
		return lifeStage;
	}
	public void setLifeStage(String lifeStage) {
		this.lifeStage = lifeStage;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getLocation1() {
		return location1;
	}
	public void setLocation1(String location1) {
		this.location1 = location1;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getLocation2() {
		return location2;
	}
	public void setLocation2(String location2) {
		this.location2 = location2;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getPreservationMethod() {
		return preservationMethod;
	}
	public void setPreservationMethod(String preservationMethod) {
		this.preservationMethod = preservationMethod;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getTaxonString() {
		String s = "";
	    if (StringUtils.notEmpty(getGenus()) && StringUtils.notEmpty(getSpecies())) {
	    	s = getGenus() + " " + getSpecies();
	    } else if (StringUtils.notEmpty(getGenus())) {
	    	s = getGenus();
	    } else if (StringUtils.notEmpty(getTribe())) {
	    	s = getTribe();
	    } else if (StringUtils.notEmpty(getSubfamily())) {
	    	s = getSubfamily();
	    } else if (StringUtils.notEmpty(getFamily())) {
	    	s = getFamily();
	    }		
	    return s;
	}
}
