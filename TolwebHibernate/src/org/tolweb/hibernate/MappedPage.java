/*
 * Created on Aug 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tolweb.treegrow.main.UsePermissable;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrow.page.Page;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="PAGES"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class MappedPage extends Page implements MultiContributorObject, UsePermissable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1951291850013207863L;
	public static final String SKELETAL = "SKELETAL";
    public static final String COMPLETE = "COMPLETE";
    public static final String UNDERCONSTRUCTION = "UNDERCONSTRUCTION";
    public static final String PEERREVIEWED = "PEERREVIEWED";
    
    private Long pageId, parentPageId;
    private String groupName;
    private SortedSet textSections, titleIllustrations, contributors;
    private Set ancestors;
    private MappedNode node;
    private Date firstOnlineDate;
    private Date contentChangedDate;
    private Date titleIllustrationLastEditedDate;
    private int titleIllustrationHeight;
    
    // notes for edit forms
    private String internetLinksNotes;
    private String textSectionsNotes;
    private String contributorsNotes;
    private String referencesNotes;
    private String titleIllustrationsNotes;
    private String treeTextNotes;
    
    private Long editHistoryId;
    
    /**
     * Really whether any non-terminal node on this page has incomplete
     * subgroups, but it's easier and more efficient to store this info
     * upon tree upload so that all nodes don't need to be fetched everytime
     * the page is viewed
     */
    private boolean hasIncompleteSubgroups;
    
	private Boolean modificationPermitted = Boolean.valueOf(true);
    
    public void copyValues(MappedPage other, MappedNode otherNode) {
        setParentPageId(other.getParentPageId());
        setPrintImageData(other.getPrintImageData());
        setPrintCustomCaption(other.getPrintCustomCaption());
        setGroupName(other.getGroupName());
        if (other.getContributors() != null) {
            setContributors(new TreeSet(other.getContributors()));
        }
        if (other.getTextSections() != null) {
            setTextSections(new TreeSet(other.getTextSections()));
        }
        if (other.getTitleIllustrations() != null) {
            setTitleIllustrations(new TreeSet(other.getTitleIllustrations()));
        }
        setMappedNode(otherNode);
        setFirstOnlineDate(other.getFirstOnlineDate());
        setContentChangedDate(other.getContentChangedDate());
        //setTitleIllustrationLastEditedContributor(other.getTitleIllustrationLastEditedContributor());
        setTitleIllustrationLastEditedDate(other.getTitleIllustrationLastEditedDate());
        setGenBank(other.getGenBank());
        setTreeBase(other.getTreeBase());
        setTitle(other.getTitle());
        setLeadText(other.getLeadText());
        setPostTreeText(other.getPostTreeText());
        setReferences(other.getReferences());
        setInternetLinks(other.getInternetLinks());
        setImageCaption(other.getImageCaption());
        setAcknowledgements(other.getAcknowledgements());
        setStatus(other.getStatus());
        setWriteAsList(other.getWriteAsList());
        setCopyrightDate(other.getCopyrightDate());
        setCopyrightHolder(other.getCopyrightHolder());
        setHasIncompleteSubgroups(other.getHasIncompleteSubgroups());
        setUsePermission(other.getUsePermission());
    }

	/**
	 * @hibernate.id generator-class="native" column="page_id" unsaved-value="null"
	 */
	public Long getPageId() {
		return pageId;
	}
	public void setPageId(Long value) {
		pageId = value;
	}
	
	/**
	 * @hibernate.property column="parent_page_id"
	 * @return
	 */
	public Long getParentPageId() {
	    return parentPageId;
	}
	
	public void setParentPageId(Long value) {
	    parentPageId = value;
	}
	
    /**
     * @hibernate.property column="print_image_data"
     * @return
     */
    public boolean getPrintImageData() {
        return printImageData();
    }
    
    /**
     * @hibernate.property column="print_custom_caption"
     * @return
     */
    public boolean getPrintCustomCaption() {
        return printCustomCaption();
    }
    
	public void setGroupName(String value) {
	    groupName = value;
	}
	
	/**
	 * @hibernate.property column="cladename"
	 */
	public String getGroupName() {
	    return groupName;
	}
	
	
	/**
	 * @hibernate.set table="PAGE_CONTRIBUTORS" lazy="false" order-by="page_order asc" sort="natural"
	 * @hibernate.collection-composite-element class="org.tolweb.treegrow.page.PageContributor"
	 * @hibernate.collection-key column="page_id"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 */ 
	public SortedSet getContributors() {
		if (contributors == null) {
			contributors = new TreeSet();
		}
		return contributors;		    
	}
	
	public void setContributors(SortedSet value) {
		contributors = value;
	}	
	
    
    /**
     * @hibernate.set table="Sections_To_Pages" cascade="save-update" sort="natural"
     * @hibernate.collection-key column="page_id"
     * @hibernate.collection-many-to-many class="org.tolweb.hibernate.MappedTextSection" column="section_id"
     * @hibernate.cache usage="nonstrict-read-write"
     */
	public SortedSet getTextSections() {
        if (textSections == null) {
            textSections = new TreeSet();
        }
	    return textSections;
	}
	
	public void setTextSections(SortedSet value) {
	    textSections = value;
	}
	
	/**
	 * @hibernate.set table="GRAPHICS" lazy="false" order-by="page_order asc" sort="natural"
	 * @hibernate.collection-composite-element class="org.tolweb.hibernate.TitleIllustration"
	 * @hibernate.collection-key column="page_id"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 */
	public SortedSet getTitleIllustrations() {
	    return titleIllustrations;
	}
	
	public void setTitleIllustrations(SortedSet value) {
	    titleIllustrations = value;
	}	

	public String getInternetInfo() {
	    return getInternetLinks();
	}
	
	public void setInternetInfo(String value) {
	    setInternetLinks(value);
	}
	
	public void setMappedNode(MappedNode value) {
	    node = value;
	    if (value != null) {
	    	setGroupName(value.getName());
	    } else {
	    	setGroupName("");
	    }
	}
	
	/**
	 * @hibernate.many-to-one column="node_id" class="org.tolweb.hibernate.MappedNode" cascade="none"
	 * @return Returns the node that is the root of this page
	 */
	public MappedNode getMappedNode() {
	    return node;
	}
    
    /**
     * @hibernate.property column="page_firstonline"
     * @return
     */
    public Date getFirstOnlineDate() {
        return firstOnlineDate;
    }
    public void setFirstOnlineDate(Date firstOnlineDate) {
        this.firstOnlineDate = firstOnlineDate;
    }    
    /**
     * @hibernate.property column="page_contentchangeddate"
     * @return
     */
    public Date getContentChangedDate() {
        return contentChangedDate;
    }
    public void setContentChangedDate(Date contentChangedDate) {
        this.contentChangedDate = contentChangedDate;
    }
    
    public String getFirstOnlineString() {
    	if (getFirstOnlineDate() != null) {
    		return getFirstOnlineDate().toString();
    	} else {
    		return null;
    	}
    }
    /**
     * @hibernate.propert
     * @return Returns the titleIllustrationLastEditedContributor.
     /
    public Contributor getTitleIllustrationLastEditedContributor() {
        return titleIllustrationLastEditedContributor;
    }
    /**
     * @param titleIllustrationLastEditedContributor The titleIllustrationLastEditedContributor to set.
    /
    public void setTitleIllustrationLastEditedContributor(
            Contributor titleIllustrationLastEditedContributor) {
        this.titleIllustrationLastEditedContributor = titleIllustrationLastEditedContributor;
    }*/
    /**
     * @hibernate.property column="tillus_last_edited_date"
     * @return Returns the titleIllustrationLastEditedDate.
     */
    public Date getTitleIllustrationLastEditedDate() {
        return titleIllustrationLastEditedDate;
    }
    /**
     * @param titleIllustrationLastEditedDate The titleIllustrationLastEditedDate to set.
     */
    public void setTitleIllustrationLastEditedDate(
            Date titleIllustrationLastEditedDate) {
        this.titleIllustrationLastEditedDate = titleIllustrationLastEditedDate;
    }
    
    public String toString() {
        return "Mapped Page with id: " + getPageId() + " for node: " + getGroupName();
    }
    /**
     * @hibernate.property column="tillus_height"
     * @return Returns the titleIllustrationHeight.
     */
    public int getTitleIllustrationHeight() {
        return titleIllustrationHeight;
    }
    /**
     * @param titleIllustrationHeight The titleIllustrationHeight to set.
     */
    public void setTitleIllustrationHeight(int titleIllustrationHeight) {
        this.titleIllustrationHeight = titleIllustrationHeight;
    }
    
    public String getPageType() {
        if (getMappedNode().getIsLeaf()) {
            return "Leaf";
        } else {
            return "Branch";
        }
    }
    
    public String compareValues(MappedPage pg2) {
        if (!getParentPageId().equals(pg2.getParentPageId())) {
            return returnDifferenceString("parent page id", getParentPageId(), pg2.getParentPageId());
        }
        if (getPrintImageData() != pg2.getPrintImageData()) {
            return returnDifferenceString("print image data", Boolean.valueOf(getPrintImageData()), Boolean.valueOf(pg2.getPrintImageData()));
        }
        if (getPrintCustomCaption() != pg2.getPrintCustomCaption()) {
            return returnDifferenceString("print custom caption", Boolean.valueOf(getPrintCustomCaption()), Boolean.valueOf(pg2.getPrintCustomCaption()));
        }
        if (!getGroupName().equals(pg2.getGroupName())) {
            return returnDifferenceString("group name", getGroupName(), pg2.getGroupName());
        }
        if (getContributors().size() != pg2.getContributors().size()) {
            return returnDifferenceString("page contributors size", Integer.valueOf(getContributors().size()), Integer.valueOf(pg2.getContributors().size()));
        }
        if (getTextSections().size() != pg2.getTextSections().size()) {
            return returnDifferenceString("text sections size", Integer.valueOf(getTextSections().size()), Integer.valueOf(pg2.getTextSections().size()));
        }
        Iterator it1 = getTextSections().iterator();
        Iterator it2 = pg2.getTextSections().iterator();
        while (it1.hasNext()) {
            MappedTextSection s1 = (MappedTextSection) it1.next();
            MappedTextSection s2 = (MappedTextSection) it2.next();
            if (!s1.getHeading().equals(s2.getHeading())) {
                return returnDifferenceString("text sections heading", s1.getHeading(), s2.getHeading());
            }
            if (!s1.getText().equals(s2.getText())) {
                return returnDifferenceString("text sections text", s1.getText(), s2.getText());
            }
        }
        if (getTitleIllustrations().size() != pg2.getTitleIllustrations().size()) {
            return returnDifferenceString("title illustrations size", Integer.valueOf(getTitleIllustrations().size()), Integer.valueOf(pg2.getTitleIllustrations().size()));
        }
        it1 = getTitleIllustrations().iterator();
        it2 = pg2.getTitleIllustrations().iterator();
        while (it1.hasNext()) {
            TitleIllustration t1 = (TitleIllustration) it1.next();
            TitleIllustration t2 = (TitleIllustration) it2.next();
            if (!t1.getVersionId().equals(t2.getVersionId())) {
                return returnDifferenceString("title illustrations version", t1.getVersionId(), t2.getVersionId());
            }
            if (t1.getOrder() != t2.getOrder()) {
                return returnDifferenceString("title illustrations order", Integer.valueOf(t1.getOrder()), Integer.valueOf(t2.getOrder()));
            }
        }
        if (getFirstOnlineDate() != null && !getFirstOnlineDate().equals(pg2.getFirstOnlineDate())) {
            return returnDifferenceString("first online date", getFirstOnlineDate(), pg2.getFirstOnlineDate());
        }
        if (getContentChangedDate() != null && !getContentChangedDate().equals(pg2.getContentChangedDate())) {
            return returnDifferenceString("content changed date", getContentChangedDate(), pg2.getContentChangedDate());
        }
        if (getLeadText() != null && !getLeadText().equals(pg2.getLeadText())) {
            return returnDifferenceString("lead text", getLeadText(), pg2.getLeadText());
        }
        if (getPostTreeText() != null && !getPostTreeText().equals(pg2.getPostTreeText())) {
            return returnDifferenceString("post tree text", getPostTreeText(), pg2.getPostTreeText());
        }
        if (getReferences() != null && !getReferences().equals(pg2.getReferences())) {
            return returnDifferenceString("references", getReferences(), pg2.getReferences());
        }
        if (getInternetLinks() != null && !getInternetLinks().equals(pg2.getInternetLinks())) {
            return returnDifferenceString("internet links", getInternetLinks(), pg2.getInternetLinks());
        }
        if (getImageCaption() != null && !getImageCaption().equals(pg2.getImageCaption())) {
            return returnDifferenceString("image caption", getImageCaption(), pg2.getImageCaption());
        }
        if (getAcknowledgements() != null && !getAcknowledgements().equals(pg2.getAcknowledgements())) {
            return returnDifferenceString("acknowledgements", getAcknowledgements(), pg2.getAcknowledgements());
        }
        if (getWriteAsList() != pg2.getWriteAsList()) {
            return returnDifferenceString("write as list", Boolean.valueOf(getWriteAsList()), Boolean.valueOf(pg2.getWriteAsList()));
        }
        if (!getCopyrightDate().equals(pg2.getCopyrightDate())) {
            return returnDifferenceString("copyright date", getCopyrightDate(), pg2.getCopyrightDate());
        }
        if (!getCopyrightHolder().equals(pg2.getCopyrightHolder())) {
            return returnDifferenceString("copyright holder", getCopyrightHolder(), pg2.getCopyrightHolder());
        }
        // no changes
        return null;
    }
    
	private String returnDifferenceString(String fieldName, Object workingValue, Object publicValue) {
	    return "Page " + getGroupName() + " has different " + fieldName + ", working: " + workingValue + " public: " + publicValue;
	}    
	
	public static List getValidPageStatuses() {
	    ArrayList statusList = new ArrayList();
	    statusList.add(XMLConstants.TEMPORARY);
	    statusList.add(XMLConstants.UNDER_CONSTRUCTION);
	    statusList.add(XMLConstants.COMPLETE);
	    statusList.add(XMLConstants.TOL_REVIEWED);
	    statusList.add(XMLConstants.PEER_REVIEWED);
	    return statusList;
	}

    /**
     * @hibernate.property
     * @return Returns the contributorsNotes.
     */
    public String getContributorsNotes() {
        return contributorsNotes;
    }

    /**
     * @param contributorsNotes The contributorsNotes to set.
     */
    public void setContributorsNotes(String contributorsNotes) {
        this.contributorsNotes = contributorsNotes;
    }

    /**
     * @hibernate.property
     * @return Returns the internetLinksNotes.
     */
    public String getInternetLinksNotes() {
        return internetLinksNotes;
    }

    /**
     * @param internetLinksNotes The internetLinksNotes to set.
     */
    public void setInternetLinksNotes(String internetLinksNotes) {
        this.internetLinksNotes = internetLinksNotes;
    }

    /**
     * @hibernate.property
     * @return Returns the referencesNotes.
     */
    public String getReferencesNotes() {
        return referencesNotes;
    }

    /**
     * @param referencesNotes The referencesNotes to set.
     */
    public void setReferencesNotes(String referencesNotes) {
        this.referencesNotes = referencesNotes;
    }

    /**
     * @hibernate.property
     * @return Returns the textSectionsNotes.
     */
    public String getTextSectionsNotes() {
        return textSectionsNotes;
    }

    /**
     * @param textSectionsNotes The textSectionsNotes to set.
     */
    public void setTextSectionsNotes(String textSectionsNotes) {
        this.textSectionsNotes = textSectionsNotes;
    }

    /**
     * @hibernate.property
     * @return Returns the titleIllustrationsNotes.
     */
    public String getTitleIllustrationsNotes() {
        return titleIllustrationsNotes;
    }

    /**
     * @param titleIllustrationsNotes The titleIllustrationsNotes to set.
     */
    public void setTitleIllustrationsNotes(String titleIllustrationsNotes) {
        this.titleIllustrationsNotes = titleIllustrationsNotes;
    }

    /**
     * @hibernate.property
     * @return Returns the treeTextNotes.
     */
    public String getTreeTextNotes() {
        return treeTextNotes;
    }

    /**
     * @param treeTextNotes The treeTextNotes to set.
     */
    public void setTreeTextNotes(String treeTextNotes) {
        this.treeTextNotes = treeTextNotes;
    }

    /**
     * @hibernate.property
     * @return Returns the editHistoryId.
     */
    public Long getEditHistoryId() {
        return editHistoryId;
    }

    /**
     * @param editHistoryId The editHistoryId to set.
     */
    public void setEditHistoryId(Long editHistoryId) {
        this.editHistoryId = editHistoryId;
    }
    
    public String getCopyrightYear() {
        String copyrightDate = getCopyrightDate(); 
        if (copyrightDate != null && copyrightDate.length() >= 4) {
            return copyrightDate.substring(0, 4);
        } else {
            return copyrightDate;
        }        
    }
    
    public void setCopyrightYear(String value) {
        if (value != null) {
            if (value.length() > 4) {
                setCopyrightDate(value.substring(0, 4));
            } else {
                setCopyrightDate(value);
            }
        } else {
            setCopyrightDate(null);
        }
    }

    /**
     * @hibernate.property
     * @return Returns the hasIncompleteSubgroups.
     */
    public boolean getHasIncompleteSubgroups() {
        return hasIncompleteSubgroups;
    }
    /**
     * @param hasIncompleteSubgroups The hasIncompleteSubgroups to set.
     */
    public void setHasIncompleteSubgroups(boolean hasIncompleteSubgroups) {
        this.hasIncompleteSubgroups = hasIncompleteSubgroups;
    }

    /**
     * @hibernate.set table="PAGEANCESTORS" lazy="true"
     * @hibernate.collection-key column="page_id"
     * @hibernate.collection-many-to-many class="org.tolweb.hibernate.MappedPage" column="ancestor_id"
     */		
    public Set getAncestors() {
		return ancestors;
	}
	public void setAncestors(Set ancestors) {
		this.ancestors = ancestors;
	}
	
    public Boolean getModificationPermitted() {
        return modificationPermitted;
    }
    /**
     * @param modificationPermitted The modificationPermitted to set.
     */
    public void setModificationPermitted(Boolean modificationPermitted) {
        if (modificationPermitted != null) {
            this.modificationPermitted = modificationPermitted;
        } else {
            this.modificationPermitted = Boolean.valueOf(false);
        }
    }	
}
