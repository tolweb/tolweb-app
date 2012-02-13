package org.tolweb.btol;

/**
 * @hibernate.class table="SpecimenExtractions"
 * @author dmandel
 */
public class SpecimenExtraction extends FlexibleDateObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1856392716415761608L;
	private String code;
	private String protocol;
	private SourceCollection sourceCollection;
	private Specimen specimen;
	private String method;
	private String person;
	private String target;
	private String dnaCollections;
	
	
	/**
	 * @hibernate.property
	 * @return	
	 */	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @hibernate.many-to-one column="collectionId" class="org.tolweb.btol.SourceCollection" cascade="none"
	 * @return Returns the source collection for this extraction
	 */
	public SourceCollection getSourceCollection() {
		return sourceCollection;
	}
	public void setSourceCollection(SourceCollection collection) {
		this.sourceCollection = collection;
	}

	public String getSequenceCode() {
	    String s = getTaxonString();
	    s += " " + getExtractionCode();
	    return s;
	}
	public String getExtractionCode() {
		return getSourceCollection().getCode() + code;
	}
	public String getTaxonString() {
		return getSpecimen().getTaxonString();
	}

	/**
	 * not needed in this case so we need to override so hibernate doesn't think
	 * there is a column
	 */
	public String getName() {
		return null;
	}
	public void setName(String value) {}
	/**
	 * for beanfield.  NOT actually used
	 * @return
	 */
	public SpecimenExtraction getExtractionDate() {
		return null;
	}
	public void setExtractionDate(SpecimenExtraction value) {}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	/**
	 * @hibernate.many-to-one column="specimenId" class="org.tolweb.btol.Specimen" cascade="none"
	 * @return Returns the source collection for this extraction
	 */	
	public Specimen getSpecimen() {
		return specimen;
	}
	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getDnaCollections() {
		return dnaCollections;
	}
	public void setDnaCollections(String dnaCollections) {
		this.dnaCollections = dnaCollections;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
}