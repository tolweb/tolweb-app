package org.tolweb.tapestry.xml.taxaimport.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OMNode implements Serializable, Comparable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2694077735250483174L;
	private Long id;
	private String name;
	private OMPage page; 
	private List<OMMediaFile> mediafiles;
	private List<OMOtherName> othernames;
	private List<OMContributor> contributors;
	private List<OMAccessoryPage> accessorypages;
	private Long newId;
	private boolean shouldRetire;
	
	public OMNode() {
		mediafiles = new ArrayList<OMMediaFile>();
		othernames = new ArrayList<OMOtherName>();
		contributors = new ArrayList<OMContributor>();
		accessorypages = new ArrayList<OMAccessoryPage>();
	}

	@Override
	public int hashCode() {
		return id.hashCode() + name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.compareTo(obj) == 0;
	}
	
	public int compareTo(Object obj) {
		OMNode rhs = (OMNode)obj;
		String lhsName = (this.getName() != null) ? this.getName() : "";
		String rhsName = (rhs.getName() != null) ? rhs.getName() : "";
		return lhsName.compareTo(rhsName);
	}
	
/*
	public boolean hasAttachments() {
		return page != null || (accessoryPages != null && !accessoryPages.isEmpty()) || 
			(otherNames != null && !otherNames.isEmpty()) || (media != null && !media.isEmpty()) ||
			(contributors != null && !contributors.isEmpty());
	}
 */	
	
	public boolean getHasPageAttachment() {
		return page != null;
	}
	
	public boolean getHasAttachments() {
		return page != null || (mediafiles != null && !mediafiles.isEmpty()) ||
		(othernames != null && !othernames.isEmpty()) || (contributors != null && !contributors.isEmpty()) ||
		(accessorypages != null && !accessorypages.isEmpty());
	}
	
	public void addMediafile(OMMediaFile mediafile) {
		getMediafiles().add(mediafile);
	}
	
	public void addOthername(OMOtherName othername) {
		getOthernames().add(othername);
	}

	public void addContributor(OMContributor contributor) {
		getContributors().add(contributor);
	}
	
	public void addAccessorypage(OMAccessoryPage accpage) {
		getAccessorypages().add(accpage);
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @return the page
	 */
	public OMPage getPage() {
		return page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(OMPage page) {
		this.page = page;
	}

	/**
	 * @return the mediafiles
	 */
	public List<OMMediaFile> getMediafiles() {
		return mediafiles;
	}

	/**
	 * @param mediafiles the mediafiles to set
	 */
	public void setMediafiles(List<OMMediaFile> mediafiles) {
		this.mediafiles = mediafiles;
	}

	/**
	 * @return the othernames
	 */
	public List<OMOtherName> getOthernames() {
		return othernames;
	}

	/**
	 * @param othernames the othernames to set
	 */
	public void setOthernames(List<OMOtherName> othernames) {
		this.othernames = othernames;
	}

	/**
	 * @return the contributors
	 */
	public List<OMContributor> getContributors() {
		return contributors;
	}

	/**
	 * @param contributors the contributors to set
	 */
	public void setContributors(List<OMContributor> contributors) {
		this.contributors = contributors;
	}

	/**
	 * @return the accessorypages
	 */
	public List<OMAccessoryPage> getAccessorypages() {
		return accessorypages;
	}

	/**
	 * @param accessorypages the accessorypages to set
	 */
	public void setAccessorypages(List<OMAccessoryPage> accessorypages) {
		this.accessorypages = accessorypages;
	}

	/**
	 * @return the newId
	 */
	public Long getNewId() {
		return newId;
	}

	/**
	 * @param newId the newId to set
	 */
	public void setNewId(Long newId) {
		this.newId = newId;
	}

	/**
	 * @return the shouldRetire
	 */
	public boolean getShouldRetire() {
		return shouldRetire;
	}

	/**
	 * @param shouldRetire the shouldRetire to set
	 */
	public void setShouldRetire(boolean shouldRetire) {
		this.shouldRetire = shouldRetire;
	}
	
/*
    <node id="530" name="Bembidion durangoense">
        <page id="2905" type="content" data-table="PAGES"/>
        <mediafiles>
            <mediafile id="5970" type="image" data-table="IMAGES"/>
            <mediafile id="8529" type="image" data-table="IMAGES"/>
        </mediafiles>
        <othernames>
            <othername id="1812" type="content" data-table="OTHERNAMES"/>
        </othernames>
    </node>
    <node id="57" name="Elaphrini">
        <page id="136" type="content" data-table="PAGES"/>
        <mediafiles>
            <mediafile id="422" type="image" data-table="IMAGES"/>
            <mediafile id="2438" type="image" data-table="IMAGES"/>
            <mediafile id="3572" type="image" data-table="IMAGES"/>
        </mediafiles>
        <contributors>
            <contributor id="1969" type="other" data-table="CONTRIBUTORS"/>
        </contributors>
    </node>
    <node id="398" name="Bembidion zephyrum">
        <page id="66" type="content" data-table="PAGES"/>
        <accessorypages>
            <accessorypage id="14" type="content" data-table="ACCESSORY_PAGES"/>
            <accessorypage id="29" type="content" data-table="ACCESSORY_PAGES"/>
        </accessorypages>
        <mediafiles>
            <mediafile id="231" type="image" data-table="IMAGES"/>
            <mediafile id="4314" type="image" data-table="IMAGES"/>
            <mediafile id="4356" type="image" data-table="IMAGES"/>
            <mediafile id="4702" type="image" data-table="IMAGES"/>
            <mediafile id="5470" type="image" data-table="IMAGES"/>
            <mediafile id="5510" type="image" data-table="IMAGES"/>
            <mediafile id="5984" type="image" data-table="IMAGES"/>
        </mediafiles>
    </node>
 */
}
