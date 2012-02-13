/*
 * Contributor.java
 *
 * Created on December 22, 2003, 10:01 AM
 */

package org.tolweb.treegrow.main;

import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;

import org.tolweb.hibernate.ContributorPermission;
import org.tolweb.treegrow.page.*;

/**
 *
 * @author  dmandel
 * @hibernate.class table="Contributors" discriminator-value="0" dynamic-insert="true"
 * @hibernate.cache usage="nonstrict-read-write"
 * @hibernate.discriminator column="discriminator" type="int"
 */
public class Contributor implements ChangedFromServerProvider, Serializable {
    /** used for serialization */
	public static final String DEFAULT_CONTRIBUTOR_URL = "http://tolweb.org/people/";
	private static final long serialVersionUID = 5316157757008691358L;
	public static final String CONTRIBUTOR_FILENAME = "contributors.xml";
    public static Contributor BLANK_CONTRIBUTOR;
    public static Contributor PUBLIC_DOMAIN_CONTRIBUTOR;
    public static final String PUBLIC_DOMAIN_NAME = "Image is in the public domain";
    public static final String CHOOSE_AUTHOR_STRING = "Select a Contributor";
    private static Hashtable contributors;
    private static Vector sortedContributors;
    
    public static final byte ANY_CONTRIBUTOR = -1;
    // andy: sci core contributors
    public static final byte SCIENTIFIC_CONTRIBUTOR = 0;
    // andy: accessory contributor maps to general
    public static final byte ACCESSORY_CONTRIBUTOR = 2;
    // andy: this is likely a student w/ a auto-gen'd name like blowfish24 if they're under 18 
    public static final byte TREEHOUSE_CONTRIBUTOR = 3;
    // andy: encompasses all media contribution, not limited to images
    public static final byte IMAGES_CONTRIBUTOR = 4;
    // andy: enthusiast, only edit personal info
    public static final byte OTHER_SCIENTIST = 5;
    // andy: not implemented yet - but would be for review usage only
    public static final byte REVIEWER = 6;
    
    public static final byte FIRST_NAME_KEY = 2;
    public static final byte LAST_NAME_KEY = 3;
    public static final byte EMAIL_KEY = 4;
    public static final byte ADDRESS_KEY = 5;
    public static final byte HOMEPAGE_KEY = 6;
    public static final byte INSTITUTION_KEY = 7;
    public static final byte PHONE_KEY = 8;
    public static final byte FAX_KEY = 9;
    public static final byte NOTES_KEY = 10;
    
    public static final int NODE_ATTACHMENT_INTEREST = 0;
    public static final int NODE_ATTACHMENT_PERMISSION = 1;
    public static final int NODE_ATTACHMENT_CUTOFF = 2;
    
    public static final String ENTHUSIAST = "Enthusiast";
    public static final String STUDENT = "Student";
    public static final String SCIENTIST = "Scientist";

    private int id = -1;
    private String city;
    private String state;
    private String country;
    private String category;
    private String firstName;
    private String lastName;    
    private String email;
    private String institution;
    private String address;
    private String homepage;
    private String notes;
    private String phone;
    private String fax;
    private String bio;
    private String additionalInfo;
    private String imageFilename;
    private String password = "";
    private String plans;
    private String qualifications;
    private String publications;
    private Vector permissions;
    private String assignmentApproval;
    private String coordinationComments;
    private String coAuthors;
    private byte contributorType;
    private byte unapprovedContributorType = ANY_CONTRIBUTOR;
    private boolean checkedOut;
    private boolean changed = false;
    private boolean dontShowEmail = false;
    private boolean dontShowAddress = false;
    private boolean isImageEditor = false;
    private boolean isLearningEditor = false;
    private boolean dontShowLocation = false;
    private boolean dontUseEditor = false;
    private boolean willingToCoordinate;
    private Long editingRootNodeId;
    private Long checkoutDownloadId;
    private Date checkoutDate;
    private Long checkedOutContributorId;
    private Boolean imageModificationDefault;
    private Byte imageUseDefault;
    private Boolean noteModificationDefault;
    private Byte noteUseDefault;
    private Set contributorPermissions;
    private Long editHistoryId;
    private String initials;
    
    private String geographicAreaInterest;
    private boolean interestedInTaxonomy;
    private boolean interestedInPhylogenetics;
    private boolean interestedInMorphology;
    private boolean interestedInBiogeography;
    private boolean interestedInImmatureStages;
    private boolean interestedInEcology;
    private boolean interestedInBehavior;
    private boolean interestedInCytogenetics;
    private boolean interestedInProteins;
    private String otherInterests;
    /*
     * Who confirmed this contributor to the project
     */
    private Long confirmerContributorId;
    private Date confirmationDate;
    
    private Date noteUseLastUpdated;
    private boolean noteUseLastChanged;
    private Date imageUseLastUpdated;
    private boolean imageUseLastChanged;
    private Long licenseReviewerContributorId;
    
    static {
        contributors = new Hashtable(300);
        BLANK_CONTRIBUTOR = new Contributor();
        BLANK_CONTRIBUTOR.setId(0);
        BLANK_CONTRIBUTOR.setLastName(CHOOSE_AUTHOR_STRING);
        BLANK_CONTRIBUTOR.setEmail(CHOOSE_AUTHOR_STRING);
        BLANK_CONTRIBUTOR.setAddress(CHOOSE_AUTHOR_STRING);
        BLANK_CONTRIBUTOR.setHomepage(CHOOSE_AUTHOR_STRING);
        PUBLIC_DOMAIN_CONTRIBUTOR = new Contributor();
        PUBLIC_DOMAIN_CONTRIBUTOR.setLastName(PUBLIC_DOMAIN_NAME);
    }
    
    /** Creates a new instance of Contributor */
    public Contributor() {
        permissions = new Vector();
    }
    
    public String getProfileUrl() {
    	return DEFAULT_CONTRIBUTOR_URL + getId();
    }
    
    public void setValues(Contributor other) {
        id = other.getId();
        firstName = other.getFirstName();
        lastName = other.getLastName();
        email = other.getEmail();
        address = other.getAddress();
        homepage = other.getHomepage();
        permissions = other.getPermissions();
        institution = other.getInstitution();
        phone = other.getPhone();
        fax = other.getFax();
        notes = other.getNotes();
    }
    
    /**
     * Generic setter method in order to make the undoable edit code easier
     *
     * @param key Which value to set on the NodeImage
     * @param value The value to set
     */
    public void setValue(byte key, String value) {
        switch (key) {
            case FIRST_NAME_KEY: setFirstName(value); break;
            case LAST_NAME_KEY: setLastName(value); break;
            case EMAIL_KEY: setEmail(value); break;
            case ADDRESS_KEY: setAddress(value); break;
            case HOMEPAGE_KEY: setHomepage(value); break;
            case INSTITUTION_KEY: setInstitution(value); break;
            case PHONE_KEY: setPhone(value); break;
            case FAX_KEY: setFax(value); break;
            case NOTES_KEY: setNotes(value); break;
        }
    }
    
    public void setId(int value) {
        id = value;
    }
    
    /** The getter method for this Contributer's identifier.
      *
      * @hibernate.id  generator-class="native" column="contributor_id" unsaved-value="-1"
      */
    public int getId() {
        return id;
    }
    
    public void setNotes(String value) {
        notes = value;
    }
    
    /**
     * @hibernate.property
     */
    public String getNotes() {
        return notes;
    }
    
    public void setPhone(String value) {
        phone = value;
    }
    
    /**
     * @hibernate.property column="phone_number"
     */
    public String getPhone() {
        return phone;
    }
    
    public void setFax(String value) {
        fax = value;
    }
    
    /**
     * @hibernate.property
     */
    public String getFax() {
        return fax;
    }
    
    public void setInstitution(String value) {
        institution = value;
    }
    
    /**
     * @hibernate.property column="institution"
     */
    public String getInstitution() {
        return institution;
    }
    
    public void setDontShowEmail(boolean value) {
        dontShowEmail = value;
    }
    
    public boolean dontShowEmail() {
        return dontShowEmail;
    }
    
    public boolean getDontShowEmail() {
        return dontShowEmail();
    }
    
    /**
     * @hibernate.property column="print_email"
     */
    public boolean getShowEmail() {
        return !getDontShowEmail();
    }
    
    public void setShowEmail(boolean value) {
        setDontShowEmail(!value);
    }
    
    /**
     * @hibernate.property column="is_image_editor"
     */
    public boolean getIsImageEditor() {
        return isImageEditor;
    }
    
    public void setIsImageEditor(boolean value) {
        isImageEditor = value;
    }

	/**
	 * @hibernate.property column="is_learning_editor"
	 * @return
	 */
	public boolean getIsLearningEditor() {
		return isLearningEditor;	
	}
	
	public void setIsLearningEditor(boolean value) {
		isLearningEditor = value;
	}    
    
    public void setDontShowAddress(boolean value) {
        dontShowAddress = value;
    }
    
    public boolean dontShowAddress() {
        return dontShowAddress;
    }
    
    public boolean getDontShowAddress() {
        return dontShowAddress();
    }
    
    /**
     * @hibernate.property column="print_address"
     */
    public boolean getShowAddress() {
        return !getDontShowAddress();
    }
    
    public void setShowAddress(boolean value) {
        setDontShowAddress(!value);
    }
    
    public void setFirstName(String value) {
        firstName = value;
    }
    
    /**
     * @hibernate.property column="first_name"
     */
    public String getFirstName() {
        return firstName;
    }
    
    public void setLastName(String value) {
        lastName = value;
    }
    
    /**
     * @hibernate.property column="last_name"
     */
    public String getLastName() {
        return lastName;
    }
    
    public void setSurname(String value) {
        setLastName(value);
    }
    
    public String getSurname() {
        return getLastName();
    }
    
    public String getName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
    
    /**
     * MEthod used for the name to be printed on webpages.  This will be overridden
     * for various contributor subclasses
     * @return
     */
    public String getDisplayName() {
        String returnString = getName();
        if (getIsInstitution()) {
            returnString = getInstitution();
        }
        return returnString;
    }
    
    public boolean getIsInstitution() {
    	return StringUtils.isEmpty(getFirstName()) && StringUtils.isEmpty(getLastName());
    }
    
    /*public String getInitials() {
    	String initials = "";
    	String firstName = getFirstName();
    	if (StringUtils.notEmpty(firstName)) {
    		initials += firstName.substring(0, 1);
    		initials += " ";
    	}
    	String lastName = getLastName();
    	if (StringUtils.notEmpty(lastName)) {
    		initials += lastName.substring(0, 1);
    	}
    	return initials;
    }*/
    
    public void setEmail(String value) {
        email = value;
    }
    
    /**
     * @hibernate.property
     */
    public String getEmail() {
        return email;
    }
    
    public void setAddress(String value) {
        address = value;
    }
    
    /**
     * @hibernate.property
     */
    public String getAddress() {
        return address;
    }
    
    public void setHomepage(String value) {
        homepage = value;
    }
    
    /**
     * @hibernate.property
     */
    public String getHomepage() {
        return homepage;
    }
    
    public void addToPermissions(Permission p) {
        permissions.add(p);
    }
    
    public void removeFromPermissions(Permission p) {
        permissions.remove(p);
    }
    
    public Vector getPermissions() {
        return permissions;
    }
    
    public void setPermissions(Vector value) {
    	permissions = value;
    }
    
    public boolean checkedOut() {
        return checkedOut;
    }

    /**
     * @hibernate.property column="checked_out"
     */    
    public boolean getCheckedOut() {
        return checkedOut;
    }
    
    public void setCheckedOut(boolean value) {
        checkedOut = value;
    }
    
    /**
     * @hibernate.property
     */
    public String getBio() {
        return bio;
    }
    
    public void setBio(String value) {
        bio = value;
    }
    
    /**
     * @hibernate.property column="additional_info"
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(String value) {
        additionalInfo = value;
    }
    
    /**
     * @hibernate.property column="image_filename"
     */    
    public String getImageFilename() {
        return imageFilename;
    }
    
    public void setImageFilename(String value) {
        imageFilename = value;
    }
    
    /**
     * @hibernate.property 
     */
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String value) {
        password = value;
    }

    /**
     * @hibernate.property column="type"
     */
    public byte getContributorType() {
        return contributorType;
    }    
    
    public void setContributorType(byte value) {
        contributorType = value;
    }
    
    public boolean getIsCoreScientificContributor() {
    	return getContributorType() == Contributor.SCIENTIFIC_CONTRIBUTOR;
    }
    public boolean getIsAccessoryContributor() {
    	return getContributorType() == Contributor.ACCESSORY_CONTRIBUTOR;
    }
    public boolean getIsReviewer() {
    	return getContributorType() == Contributor.REVIEWER;
    }
    
    public Contributor getContributorWithId(int id) {
        return (Contributor) contributors.get(Integer.valueOf(id));
    }
    
    public static void buildContributorsList() {
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(new File(Controller.getController().getFileManager().getSystemDir() + CONTRIBUTOR_FILENAME));
            Element root = doc.getRootElement();
            Iterator it = root.getChildren(XMLConstants.CONTRIBUTOR).iterator();
            while (it.hasNext()) {
                Element contributorElmt = (Element) it.next();
                Contributor contributor = XMLReader.getContributorFromElement(contributorElmt);
                contributors.put(Integer.valueOf(contributor.getId()), contributor);
            }
            contributors.put(Integer.valueOf(0), BLANK_CONTRIBUTOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Vector getSortedContributors() {
        if (sortedContributors == null) {
            sortedContributors = new Vector(contributors.values());
            Collections.sort(sortedContributors, new ContributorComparator());
            sortedContributors.add(0, BLANK_CONTRIBUTOR);
        }
        return sortedContributors;
    }
    
    public static Hashtable getContributors() {
        return contributors;
    }
    
    public static Contributor getContributor(int id) {
        return (Contributor) contributors.get(Integer.valueOf(id));
    }

    public boolean changedFromServer() {
        return changed;
    }
    
    public void setChangedFromServer(boolean value) {
        changed = value;
    }
    
    public String getNameOrInstitution() {
        if (StringUtils.notEmpty(getFirstName()) || StringUtils.notEmpty(getLastName())) {
            String firstnameString = getFirstName() != null ? ", " + getFirstName() : "";
            return getLastName() + firstnameString;
        } else {
            return getInstitution();
        }
    }
    
    public static class ContributorComparator implements Comparator {
        public int compare(Object c1, Object c2) {
            Contributor con1 = (Contributor) c1;
            Contributor con2 = (Contributor) c2;
            String cmp1Str = con1.getNameOrInstitution();
            String cmp2Str = con2.getNameOrInstitution();
            if (cmp1Str == null) {
                return -1;
            } else if (cmp2Str == null) {
                return 1;
            } else {
                // First check for the last name, then fall back to the institution
                return (cmp1Str.compareTo(cmp2Str));
            }
        }
    }    
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param string
	 */
	public void setCity(String string) {
		city = string;
	}

	/**
	 * @param string
	 */
	public void setCountry(String string) {
		country = string;
	}

	/**
	 * @param string
	 */
	public void setState(String string) {
		state = string;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param string
	 */
	public void setCategory(String string) {
		category = string;
	}

    /**
     * @hibernate.property
     * @return
     */
	public String getPlans() {
        return plans;
    }
    public void setPlans(String plans) {
        this.plans = plans;
    }
    /**
     * @hibernate.property column="dont_show_location"
     * @return Returns the dontShowLocation.
     */
    public boolean getDontShowLocation() {
        return dontShowLocation;
    }
    /**
     * @param dontShowLocation The dontShowLocation to set.
     */
    public void setDontShowLocation(boolean dontShowLocation) {
        this.dontShowLocation = dontShowLocation;
    }
    /**
     * @hibernate.property column="editing_rootnode_id"
     * @return Returns the editingRootNodeId.
     */
    public Long getEditingRootNodeId() {
        return editingRootNodeId;
    }
    /**
     * @param editingRootNodeId The editingRootNodeId to set.
     */
    public void setEditingRootNodeId(Long editingRootNodeId) {
        this.editingRootNodeId = editingRootNodeId;
    }
    /**
     * @hibernate.property column="checkout_date"
     * @return Returns the checkoutDate.
     */
    public Date getCheckoutDate() {
        return checkoutDate;
    }
    /**
     * @param checkoutDate The checkoutDate to set.
     */
    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }
    /**
     * @hibernate.property column="checkout_download_id"
     * @return Returns the checkoutDownloadId.
     */
    public Long getCheckoutDownloadId() {
        return checkoutDownloadId;
    }
    /**
     * @param checkoutDownloadId The checkoutDownloadId to set.
     */
    public void setCheckoutDownloadId(Long checkoutDownloadId) {
        this.checkoutDownloadId = checkoutDownloadId;
    }
    /**
     * @hibernate.property column="checkout_contributor_id"
     * @return Returns the checkedOutContributorId.
     */
    public Long getCheckedOutContributorId() {
        return checkedOutContributorId;
    }
    /**
     * @param checkedOutContributorId The checkedOutContributorId to set.
     */
    public void setCheckedOutContributorId(Long checkedOutContributorId) {
        this.checkedOutContributorId = checkedOutContributorId;
    }
    /**
     * @hibernate.property column="dont_use_editor"
     * @return Returns the dontUseEditor.
     */
    public boolean getDontUseEditor() {
        return dontUseEditor;
    }
    /**
     * @param dontUseEditor The dontUseEditor to set.
     */
    public void setDontUseEditor(boolean dontUseEditor) {
        this.dontUseEditor = dontUseEditor;
    }
    /**
     * @hibernate.property column="imagemodification_default"
     * @return Returns the imageModificationDefault.
     */
    public Boolean getImageModificationDefault() {
        return imageModificationDefault;
    }
    /**
     * @param imageModificationDefault The imageModificationDefault to set.
     */
    public void setImageModificationDefault(Boolean imageModificationDefault) {
        this.imageModificationDefault = imageModificationDefault;
    }
    /**
     * @hibernate.property column="imageuse_default"
     * @return Returns the imageUseDefault.
     */
    public Byte getImageUseDefault() {
        return imageUseDefault;
    }
    /**
     * @param imageUseDefault The imageUseDefault to set.
     */
    public void setImageUseDefault(Byte imageUseDefault) {
    	if (imageUseDefault != null) {
    		boolean change = !imageUseDefault.equals(this.imageUseDefault);
    		setImageUseLastChanged(change);
    		setImageUseLastUpdated(new Date());
    	}
        this.imageUseDefault = imageUseDefault;
    }
    /**
     * @hibernate.property column="notemodification_default"
     * @return Returns the noteModificationDefault.
     */
    public Boolean getNoteModificationDefault() {
        return noteModificationDefault;
    }
    /**
     * @param noteModificationDefault The noteModificationDefault to set.
     */
    public void setNoteModificationDefault(Boolean noteModificationDefault) {
        this.noteModificationDefault = noteModificationDefault;
    }
    /**
     * @hibernate.property column="noteuse_default"
     * @return Returns the noteUseDefault.
     */
    public Byte getNoteUseDefault() {
        return noteUseDefault;
    }
    /**
     * @param noteUseDefault The noteUseDefault to set.
     */
    public void setNoteUseDefault(Byte noteUseDefault) {
    	if (noteUseDefault != null) {
    		boolean change = !noteUseDefault.equals(this.noteUseDefault);
    		setNoteUseLastChanged(change);
    		setNoteUseLastUpdated(new Date());
    	}
        this.noteUseDefault = noteUseDefault;
    }
    
    public String toString() {
        return "Contributor named: " + getDisplayName() + " institution: " + getInstitution();
    }
    /**
     * @hibernate.property
     * @return
     */
	public String getQualifications() {
		return qualifications;
	}

	public void setQualifications(String qualifications) {
		this.qualifications = qualifications;
	}
    /**
     * @hibernate.property
     * @return
     */
	public String getPublications() {
		return publications;
	}

	public void setPublications(String publications) {
		this.publications = publications;
	}
    /**
     * @hibernate.property
     * @return
     */
	public byte getUnapprovedContributorType() {
		return unapprovedContributorType;
	}
	public boolean getIsApproved() {
		return getUnapprovedContributorType() < 0;
	}

	public void setUnapprovedContributorType(byte unapprovedContributorType) {
		this.unapprovedContributorType = unapprovedContributorType;
	}
	public String getUnapprovedContributorTypeString() {
		return getContributorTypeString(getUnapprovedContributorType());		
	}
	public String getContributorTypeString() {
		return getContributorTypeString(getContributorType());
	}
	protected String getContributorTypeString(byte contributorType) {
		switch (contributorType) {
			case SCIENTIFIC_CONTRIBUTOR: return "Scientific Core";
			case ACCESSORY_CONTRIBUTOR: return "General Scientific";
			case TREEHOUSE_CONTRIBUTOR: return "Treehouse";
			case OTHER_SCIENTIST: return "Other Scientist";
			default: return "Media";
		}
	}
	
    /**
     * @hibernate.property
     * @return
     */
	public String getAssignmentApproval() {
		return assignmentApproval;
	}

	public void setAssignmentApproval(String assignmentApproval) {
		this.assignmentApproval = assignmentApproval;
	}
    /**
     * @hibernate.property
     * @return
     */
	public String getCoordinationComments() {
		return coordinationComments;
	}

	public void setCoordinationComments(String coordinationComments) {
		this.coordinationComments = coordinationComments;
	}
    /**
     * @hibernate.property
     * @return
     */
	public boolean getWillingToCoordinate() {
		return willingToCoordinate;
	}

	public void setWillingToCoordinate(boolean willingToCoordinate) {
		this.willingToCoordinate = willingToCoordinate;
	}
    /**
     * @hibernate.property
     * @return
     */
	public String getCoAuthors() {
		return coAuthors;
	}
	public void setCoAuthors(String coAuthors) {
		this.coAuthors = coAuthors;
	}
    /**
     * @hibernate.set table="ContributorPermissions" lazy="false
     * @hibernate.collection-composite-element class="org.tolweb.hibernate.ContributorPermission"
     * @hibernate.collection-key column="contributorId"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     * @return Returns the permissionsSet.
     */
	public Set getContributorPermissions() {
		if (contributorPermissions == null) {
			contributorPermissions = new HashSet();
		}
		return contributorPermissions;
	}
	public void setContributorPermissions(Set contributorPermissions) {
		this.contributorPermissions = contributorPermissions;
	}
	public void addToContributorPermissions(Contributor contr) {
		ContributorPermission permission = new ContributorPermission();
		permission.setContributor(contr);
		getContributorPermissions().add(permission);
	}
	public void removeFromContributorPermissions(ContributorPermission permission) {
		for (Iterator iter = getContributorPermissions().iterator(); iter.hasNext();) {
			ContributorPermission nextPermission = (ContributorPermission) iter.next();
			if (nextPermission.equals(permission)) {
				getContributorPermissions().remove(permission);
			}
		}
	}
	
	public void removeFromContributorPermissions(Contributor contr) {
		for (Iterator iter = getContributorPermissions().iterator(); iter.hasNext();) {
			ContributorPermission nextPermission = (ContributorPermission) iter.next();
			if (nextPermission.getContributor().getId() == contr.getId()) {
				getContributorPermissions().remove(nextPermission);
			}
		}
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Long getEditHistoryId() {
		return editHistoryId;
	}

	public void setEditHistoryId(Long editHistoryId) {
		this.editHistoryId = editHistoryId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Date getConfirmationDate() {
		return confirmationDate;
	}

	public void setConfirmationDate(Date approvalDate) {
		this.confirmationDate = approvalDate;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Long getConfirmerContributorId() {
		return confirmerContributorId;
	}

	public void setConfirmerContributorId(Long approvedContributorId) {
		this.confirmerContributorId = approvedContributorId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getGeographicAreaInterest() {
		return geographicAreaInterest;
	}

	public void setGeographicAreaInterest(String geographicAreaInterest) {
		this.geographicAreaInterest = geographicAreaInterest;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getInterestedInBehavior() {
		return interestedInBehavior;
	}

	public void setInterestedInBehavior(boolean interestedInBehavior) {
		this.interestedInBehavior = interestedInBehavior;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getInterestedInBiogeography() {
		return interestedInBiogeography;
	}

	public void setInterestedInBiogeography(boolean interestedInBiogeography) {
		this.interestedInBiogeography = interestedInBiogeography;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getInterestedInCytogenetics() {
		return interestedInCytogenetics;
	}

	public void setInterestedInCytogenetics(boolean interestedInCytogenetics) {
		this.interestedInCytogenetics = interestedInCytogenetics;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getInterestedInEcology() {
		return interestedInEcology;
	}

	public void setInterestedInEcology(boolean interestedInEcology) {
		this.interestedInEcology = interestedInEcology;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getInterestedInImmatureStages() {
		return interestedInImmatureStages;
	}

	public void setInterestedInImmatureStages(boolean interestedInImmatureStages) {
		this.interestedInImmatureStages = interestedInImmatureStages;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getInterestedInMorphology() {
		return interestedInMorphology;
	}

	public void setInterestedInMorphology(boolean interestedInMorphology) {
		this.interestedInMorphology = interestedInMorphology;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getInterestedInPhylogenetics() {
		return interestedInPhylogenetics;
	}

	public void setInterestedInPhylogenetics(boolean interestedInPhylogenetics) {
		this.interestedInPhylogenetics = interestedInPhylogenetics;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getInterestedInProteins() {
		return interestedInProteins;
	}

	public void setInterestedInProteins(boolean interestedInProteins) {
		this.interestedInProteins = interestedInProteins;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getInterestedInTaxonomy() {
		return interestedInTaxonomy;
	}

	public void setInterestedInTaxonomy(boolean interestedInTaxonomy) {
		this.interestedInTaxonomy = interestedInTaxonomy;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getOtherInterests() {
		return otherInterests;
	}

	public void setOtherInterests(String otherInterests) {
		this.otherInterests = otherInterests;
	}
	public String getInterestsString() {
		ArrayList interests = new ArrayList();
		if (getInterestedInBehavior()) {
			interests.add("Behavior");
		}
		if (getInterestedInBiogeography()) {
			interests.add("Biogeography");
		}
		if (getInterestedInCytogenetics()) {
			interests.add("Cytogenetics");
		}
		if (getInterestedInEcology()) {
			interests.add("Ecology");
		}
		if (getInterestedInImmatureStages()) {
			interests.add("Immature Stages");
		}
		if (getInterestedInMorphology()) {
			interests.add("Morphology");
		}
		if (getInterestedInPhylogenetics()) {
			interests.add("Phylogenetics");
		}
		if (getInterestedInProteins()) {
			interests.add("DNA/proteins");
		}
		if (getInterestedInTaxonomy()) {
			interests.add("Taxonomy");
		}
		if (StringUtils.notEmpty(getOtherInterests())) {
			interests.add(getOtherInterests());
		}
		return StringUtils.returnCommaJoinedString(interests);
	}
    
    public boolean equals(Object other) {
    	if (other == null) {
    		return false;
    	}
    	boolean sameClass = other.getClass() == getClass();
    	return sameClass && getId() == ((Contributor) other).getId();
    }

    public boolean isScientificContributor() {
    	return getContributorType() == Contributor.SCIENTIFIC_CONTRIBUTOR; 
    }
    
    public boolean isTreehouseContributor() {
    	return getContributorType() == Contributor.TREEHOUSE_CONTRIBUTOR;
    }
    
    public boolean isGeneralContributor() {
    	return getContributorType() == Contributor.ACCESSORY_CONTRIBUTOR;
    }
    
	/**
	 * @hibernate.property
	 * @return
	 */
    public String getInitials() {
		return initials;
	}
	public void setInitials(String initials) {
		this.initials = initials;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getImageUseLastChanged() {
		return imageUseLastChanged;
	}

	public void setImageUseLastChanged(boolean imageUseLastChanged) {
		this.imageUseLastChanged = imageUseLastChanged;
	}

	/**
	 * @hibernate.property
	 * @return
	 */	
	public Date getImageUseLastUpdated() {
		return imageUseLastUpdated;
	}

	public void setImageUseLastUpdated(Date imageUseLastUpdated) {
		this.imageUseLastUpdated = imageUseLastUpdated;
	}

	/**
	 * @hibernate.property
	 * @return
	 */	
	public boolean getNoteUseLastChanged() {
		return noteUseLastChanged;
	}

	public void setNoteUseLastChanged(boolean noteUseLastChanged) {
		this.noteUseLastChanged = noteUseLastChanged;
	}

	/**
	 * @hibernate.property
	 * @return
	 */	
	public Date getNoteUseLastUpdated() {
		return noteUseLastUpdated;
	}

	public void setNoteUseLastUpdated(Date noteUseLastUpdated) {
		this.noteUseLastUpdated = noteUseLastUpdated;
	}

	/**
	 * @hibernate.property
	 * @return
	 */		
	public Long getLicenseReviewerContributorId() {
		return licenseReviewerContributorId;
	}

	public void setLicenseReviewerContributorId(Long licenseReviewerContributorId) {
		this.licenseReviewerContributorId = licenseReviewerContributorId;
	}	
}