/*
 * NodeImage.java
 *
 * Created on January 13, 2004, 11:13 AM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.tolweb.treegrow.page.*;
import org.tolweb.treegrow.tree.*;

/**
 * Base class for all media objects along with representing  
 * an Image in the system.   
 * 
 * Contains all constants for versions of licenses. 
 * 
 * NodeImage is a poor name, it's really "Media" and thus 
 * an implicit implementation of an image. 
 * 
 * @hibernate.class  table="Images" discriminator-value="0"
 * @hibernate.cache usage="nonstrict-read-write"
 * @hibernate.discriminator column="discriminator" type="int"
 * @author  dmandel
 */
public class NodeImage implements AuxiliaryChangedFromServerProvider, Comparable, Serializable, UsePermissable {
    /** used for serialization */
	private static final long serialVersionUID = -5795543892491399032L;
	// media types
    public static final int NON_EXISTENT_MEDIA = -1;
    public static final int IMAGE = 0;
    public static final int SOUND = 1;
    public static final int MOVIE = 2;
    public static final int DOCUMENT = 3;
    
    public static final String TOL_IMG_STRING = "<ToLimg id=\"";
    public static final String TO_STRING_FORMAT = "{media-id: %1$d sci-name: %2$s filename: %3$s }";
    public static final byte COPY_OWNER_KEY = 2;
    public static final byte COPY_EMAIL_KEY = 3;
    public static final byte COPY_URL_KEY = 4;
    public static final byte SCI_NAME_KEY = 5;
    public static final byte COMMON_NAME_KEY = 6;
    public static final byte REFERENCE_KEY = 7;
    public static final byte CREATOR_KEY = 8;
    public static final byte IDENTIFIER_KEY = 9;
    public static final byte ACKS_KEY = 10;
    public static final byte GEO_LOCATION_KEY = 11;
    public static final byte STAGE_KEY = 12;
    public static final byte BODYPART_KEY = 13;
    public static final byte SIZE_KEY = 14;
    public static final byte VIEW_KEY = 15;
    public static final byte PERIOD_KEY = 16;
    public static final byte COLLECTION_KEY = 17;
    public static final byte COLLECTION_ACC_KEY = 18;
    public static final byte TYPE_KEY = 19;
    public static final byte COLLECTOR_KEY = 20;
    public static final byte SEX_KEY = 21;
    public static final byte COMMENTS_KEY = 22;
    public static final byte COPYDATE_KEY = 23;
    public static final byte COLLDATE_KEY = 24;
    public static final byte FOSSIL_KEY = 25;
    public static final byte ALT_TEXT_KEY = 26;
    public static final byte CREATIONDATE_KEY = 27;
    public static final byte SPECIMEN_KEY = 28;
    public static final byte BODYPARTS_KEY = 29;
    public static final byte ULTRASTRUCTURE_KEY = 30;
    public static final byte HABITAT_KEY = 31;
    public static final byte EQUIPMENT_KEY = 32;
    public static final byte PEOPLE_KEY = 33;
    public static final byte ALIVE_KEY = 34;
    public static final byte BEHAVIOR_KEY = 35;
    public static final byte NOTES_KEY = 36;
    public static final byte VOUCHER_NUMBER_KEY = 37;
    public static final byte VOUCHER_NUMBER_COLL_KEY = 38;
    
    // License / Intellectual Property
    public static final byte ALL_USES = -1; // ALL_USES maps to PUBLIC_DOMAIN
    public static final byte RESTRICTED_USE = 0; // being phased out, only valid for media at the moment
    public static final byte TOL_USE = 1;
    public static final byte EVERYWHERE_USE = 2; // TOL & Partners
    // Creative Commons 1.0
	public static final byte CC_BY10 = 21;    
    public static final byte CC_BY_NC10 = 22;
	public static final byte CC_BY_ND10 = 23;
	public static final byte CC_BY_SA10 = 24;
	public static final byte CC_BY_NC_SA10 = 25;
	public static final byte CC_BY_NC_ND10 = 26;
	// Creative Commons 2.0
	public static final byte CC_BY20 = 3;    
    public static final byte CC_BY_NC20 = 4;
	public static final byte CC_BY_ND20 = 5;
	public static final byte CC_BY_SA20 = 6;
	public static final byte CC_BY_NC_SA20 = 7;
	public static final byte CC_BY_NC_ND20 = 8;
	// Creative Commons 2.5
	public static final byte CC_BY25 = 9;    
    public static final byte CC_BY_NC25 = 10;
	public static final byte CC_BY_ND25 = 11;
	public static final byte CC_BY_SA25 = 12;
	public static final byte CC_BY_NC_SA25 = 13;
	public static final byte CC_BY_NC_ND25 = 14;
	// Creative Commons 3.0
	public static final byte CC_BY30 = 15;    
    public static final byte CC_BY_NC30 = 16;
	public static final byte CC_BY_ND30 = 17;
	public static final byte CC_BY_SA30 = 18;
	public static final byte CC_BY_NC_SA30 = 19;
	public static final byte CC_BY_NC_ND30 = 20;
	
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String UNKNOWN = "Unknown";
    public static final String ALIVE = "Alive";
    public static final String DEAD = "Dead";
    public static final String FOSSIL = "Fossil";
    public static final String MODEL = "Model";
    public static final String OTHER = "Other";
    
    public static final String PHOTOGRAPH = "Photograph";
    public static final String DRAWING_PAINTING = "Drawing/Painting";
    public static final String DIAGRAM = "Diagram";
    
    public static final Object[] TYPES_ARRAY = new Object[] {"Holotype","Paratype","Syntype","Lectotype","Neotype", 
            "Isosyntype","Paralectotype","Paraneotype","Isotype","Epitype","Iconotype","Hapantotype"};
    public static final List TYPES_LIST = Arrays.asList(TYPES_ARRAY);
    public static final Object[] IMAGE_TYPES_ARRAY = new Object[] {PHOTOGRAPH, DRAWING_PAINTING, DIAGRAM};
    public static final List IMAGE_TYPES_LIST = Arrays.asList(IMAGE_TYPES_ARRAY);
    
    private String location, copyrightOwner, copyrightEmail, copyrightUrl, 
            scientificName, reference, altText,
            creator, identifier, acknowledgements, geoLocation, stage, bodyPart, 
            size, view, period, collection, collectionAcronym, type, 
            collector, sex, comments, copyrightDate, additionalDateTimeInfo, alive,
            season, imageType, behavior, voucherNumber, voucherNumberCollection, notes, userCreationDate,
            publicDomainSourceName, publicDomainSourceUrl, publicDomainAddress, publicDomainCreator,
            publicDomainTitle, publicDomainPublisher, publicDomainAuthor, publicDomainPublicationDate, attachmentComments;
    private boolean isFossil;
    private boolean inPublicDomain;
    private boolean changedFromServer;
    private boolean checkedOut;
    private boolean onlineCheckedOut;
    private boolean isSpecimen, isBodyParts, isUltrastructure, isHabitat, isEquipment, isPeopleWorking;
    private boolean dontKnowAttachment;
    private int id = -1;
    private int copyrightContributorId = 0;
    private byte usePermission;
    private JLabel thumbnail;
    private Vector nodes, deletedNodes;
    private Contributor contributor;
    private Contributor copyrightOwnerContributor;
    private Contributor lastEditedContributor;
    private Contributor checkedOutContributor;
    private Date creationDate;
    private Date lastEditedDate;
    private Date checkoutDate;
    private Set nodesSet;
    private SortedSet versionsSet;
    private Set permissionsSet;
    private Boolean modificationPermitted = Boolean.valueOf(false);
    private Keywords keywords;
    private Languages languages;
    private boolean artisticInterpretation;
    private String technicalInformation;
    private boolean isPrimaryImage;
    protected String title;
    private boolean isUnapproved;
    private boolean showImageInfoLink;
    private boolean showImageInfoLinkForInlineImage;
    private String sourceCollectionUrl;
    private String sourceCollectionTitle;
    private String otherSourceCollectionInfo;
    private Long sourceDbId;
    private boolean considerForPodcast;

    static {
        
    }
    
    /** Creates a new instance of NodeImage */
    public NodeImage() {
        nodes = new Vector();
        deletedNodes = new Vector();
    }
    
    public NodeImage(NodeImage other) {
        setValues(other, true);
    }
    
    public String toString() {
    	return String.format(TO_STRING_FORMAT, 
    			getId(), getScientificName(), getLocation());
    }
    
    public void setValues(NodeImage other) {
        setValues(other, false);
    }
    
    public void setValues(NodeImage other, boolean doThumbnail) {
    	setValues(other, doThumbnail, true);
    }
    
    public void setValues(NodeImage other, boolean doThumbnail, boolean copyIdsAndLocs) {
        if (copyIdsAndLocs) {
			location = other.getLocation();
			id = other.getId();			
			contributor = other.getContributor();
        }
        copyrightOwnerContributor = other.getCopyrightOwnerContributor();
        copyrightOwner = other.getCopyrightOwner();
        copyrightEmail = other.getCopyrightEmail();
        copyrightUrl = other.getCopyrightUrl(); 
        scientificName = other.getScientificName();
        altText = other.getAltText();
        reference = other.getReference();
        creator = other.getCreator();
        identifier = other.getIdentifier();
        acknowledgements = other.getAcknowledgements();
        geoLocation = other.getGeoLocation();
        stage = other.getStage();
        bodyPart = other.getBodyPart(); 
        size = other.getSize();
        view = other.getView();
        period = other.getPeriod();
        collection = other.getCollection();
        collectionAcronym = other.getCollectionAcronym();
        type = other.getType(); 
        collector = other.getCollector();
        sex = other.getSex();
        comments = other.getComments();
        copyrightDate = other.getCopyrightDate();
        creationDate = other.getCreationDate();
        userCreationDate = other.getUserCreationDate();
        additionalDateTimeInfo = other.getAdditionalDateTimeInfo();
        alive = other.getAlive();
        season = other.getSeason();
        imageType = other.getImageType();
        isFossil = other.isFossil();
        inPublicDomain = other.inPublicDomain();
        isSpecimen = other.getIsSpecimen();
        isBodyParts = other.getIsBodyParts();
        isUltrastructure = other.getIsUltrastructure();
        isEquipment = other.getIsEquipment();
        isHabitat = other.getIsHabitat();
        isPeopleWorking = other.getIsPeopleWorking();
        behavior = other.getBehavior();
        voucherNumber = other.getVoucherNumber();
        voucherNumberCollection = other.getVoucherNumberCollection();
        notes = other.getNotes();
        copyrightContributorId = other.getCopyrightContributorId();
        usePermission = other.getUsePermission();
        nodes = new Vector(other.getNodes());
        nodesSet = new HashSet(other.getNodesSet());
        modificationPermitted = other.getModificationPermitted();
        if (doThumbnail) {
            thumbnail = new JLabel(other.getThumbnail().getIcon());    
        }
        permissionsSet = new HashSet(other.getPermissionsSet());
        modificationPermitted = other.getModificationPermitted();
        if (keywords != null && other.getKeywords() != null) {
            keywords.copyValues(other.getKeywords());
        }
        if (languages != null && other.getLanguages() != null) {
            languages.copyValues(other.getLanguages());
        }
        artisticInterpretation = other.getArtisticInterpretation();
        technicalInformation = other.getTechnicalInformation();
        showImageInfoLink = other.getShowImageInfoLink();
        showImageInfoLinkForInlineImage = other.getShowImageInfoLinkForInlineImage();
    }
    
    /**
     * @hibernate.property column="checked_out"
     */
    public boolean getCheckedOut() {
        return checkedOut();
    }

    public boolean checkedOut() {
        return isLocal() || checkedOut;
    }
    
    public void setCheckedOut(boolean value) {
        checkedOut = value;
    }
    
    /**
     * @hibernate.property column="online_checked_out"
     */
    public boolean getOnlineCheckedOut() {
        return onlineCheckedOut;
    }
    
    public void setOnlineCheckedOut(boolean value) {
        onlineCheckedOut = value;
    }
    
    /**
     * @hibernate.property column="is_specimen"
     * @return
     */
    public boolean getIsSpecimen() {
    	return isSpecimen;
    }
    
    public void setIsSpecimen(boolean value) {
    	isSpecimen = value;
    }
    
    /**
     * @hibernate.property column="is_body_parts"
     * @return
     */
    public boolean getIsBodyParts() {
    	return isBodyParts;
    }
    
    public void setIsBodyParts(boolean value) {
    	isBodyParts = value;
    }
    
    /**
     * @hibernate.property column="is_ultrastructure"
     * @return
     */
    public boolean getIsUltrastructure() {
    	return isUltrastructure; 
    }
    
    public void setIsUltrastructure(boolean value) {
    	isUltrastructure = value;
    }
    
    
    /**
     * Generic setter method in order to make the undoable edit code easier
     *
     * @param key Which value to set on the NodeImage
     * @param value The value to set
     */
    public void setValue(byte key, String value) {
        switch (key) {
            case COPY_OWNER_KEY: setCopyrightOwner(value); break;
            case COPY_EMAIL_KEY: setCopyrightEmail(value); break;
            case COPY_URL_KEY: setCopyrightUrl(value); break;
            case SCI_NAME_KEY: setScientificName(value); break;
            case REFERENCE_KEY: setReference(value); break;
            case CREATOR_KEY: setCreator(value); break;
            case IDENTIFIER_KEY: setIdentifier(value); break;
            case ACKS_KEY: setAcknowledgements(value); break;
            case GEO_LOCATION_KEY: setGeoLocation(value); break;
            case STAGE_KEY: setStage(value); break;
            case BODYPART_KEY: setBodyPart(value); break;
            case SIZE_KEY: setSize(value); break;
            case VIEW_KEY: setView(value); break;
            case PERIOD_KEY: setPeriod(value); break;
            case COLLECTION_KEY: setCollection(value); break;
            case COLLECTION_ACC_KEY: setCollectionAcronym(value); break;
            case TYPE_KEY: setType(value); break;
            case COLLECTOR_KEY: setCollector(value); break;
            case SEX_KEY: setSex(value); break;
            case COMMENTS_KEY: setComments(value); break;
            case COPYDATE_KEY: setCopyrightDate(value); break;
            case ALT_TEXT_KEY: setAltText(value); break;
            case CREATIONDATE_KEY: setUserCreationDate(value); break;
            case ALIVE_KEY: setAlive(value); break;
            case BEHAVIOR_KEY: setBehavior(value); break;
            case NOTES_KEY: setNotes(value); break;
            case VOUCHER_NUMBER_KEY: setVoucherNumber(value); break;
            case VOUCHER_NUMBER_COLL_KEY: setVoucherNumberCollection(value); break;
            default: break;
        }
    }
    
    public void setValue(byte key, boolean value) {
        switch(key) {
        	case SPECIMEN_KEY: setIsSpecimen(value); break;
        	case BODYPARTS_KEY: setIsBodyParts(value); break;
        	case ULTRASTRUCTURE_KEY: setIsUltrastructure(value); break;
        	case HABITAT_KEY: setIsHabitat(value); break;
        	case EQUIPMENT_KEY: setIsEquipment(value); break;
        	case PEOPLE_KEY: setIsPeopleWorking(value); break;
        }
    }
    
    public Boolean getBooleanValue(byte key) {
        boolean returnVal;
        switch(key) {
	    	case SPECIMEN_KEY: returnVal = getIsSpecimen(); break;
	    	case BODYPARTS_KEY: returnVal = getIsBodyParts(); break;
	    	case ULTRASTRUCTURE_KEY: returnVal = getIsUltrastructure(); break;
	    	case HABITAT_KEY: returnVal = getIsHabitat(); break;
	    	case EQUIPMENT_KEY: returnVal = getIsEquipment(); break;
	    	case PEOPLE_KEY: returnVal = getIsPeopleWorking(); break;
	    	default: returnVal = true;
        }        
        return Boolean.valueOf(returnVal);
    }
    
    public String getStringValue(byte key) {
        switch(key) {
        	case ALIVE_KEY: return getAlive();
        	case SEX_KEY: return getSex();
        	case TYPE_KEY: return getType();
        	default: return null;
        }
    }
    
    public void setUsePermission(byte value) {
        usePermission = value;
    }
    
    /**
     * @hibernate.property column="use_permission"
     */
    public byte getUsePermission() {
        return usePermission;
    }
        
    public void setLocation(String value) {
	location = value;
    }

    /**
     * @hibernate.property
     */
    public String getLocation() {
	return location;
    }

    public void setCopyrightOwner(String value) {
	copyrightOwner = value;
    }

    /**
     * @hibernate.property column="copyright_owner"
     */
    public String getCopyrightOwner() {
	return copyrightOwner;
    }

    public void setCopyrightEmail(String value) {
	copyrightEmail = value;
    }

    /**
     * @hibernate.property column="copyright_email"
     */
    public String getCopyrightEmail() {
	return copyrightEmail;
    }

    public void setCopyrightUrl(String value) {
	copyrightUrl = value;
    }

    /**
     * @hibernate.property column="copyright_url"
     */
    public String getCopyrightUrl() {
	return copyrightUrl;
    }

    public int getCopyrightContributorId() {
        return (copyrightOwnerContributor != null) ? copyrightOwnerContributor.getId() : copyrightContributorId;
    }
    
    public void setCopyrightContributorId(int value) {
        copyrightContributorId = value;
    }
    
    /**
     * @hibernate.many-to-one column="copyright_contributor_id" class="org.tolweb.treegrow.main.Contributor not-null="false"
     */
    public Contributor getCopyrightOwnerContributor() {
        return copyrightOwnerContributor;
    }
    
    public void setCopyrightOwnerContributor(Contributor value) {
        copyrightOwnerContributor = value;
    }
    
    /**
     * @hibernate.many-to-one column="contributor_id" class="org.tolweb.treegrow.main.Contributor not-null="true"
     */
    public Contributor getContributor() {
        return contributor;
    }
    
    public void setContributor(Contributor value) {
        contributor = value;
    }
    
    /**
     * @hibernate.property column="creation_date"
     */
    public Date getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(Date value) {
        creationDate = value;
    }
    
    /**
     * @hibernate.property column="additional_time_info"
     * @return
     */
    public String getAdditionalDateTimeInfo() {
    	return additionalDateTimeInfo;
    }
    
    public void setAdditionalDateTimeInfo(String value) {
    	additionalDateTimeInfo = value;
    }
    
    /**
     * @hibernate.property column="alive"
     * @return
     */
    public String getAlive() {
    	return alive;
    }
    
    public void setAlive(String value) {
    	alive = value;
    }
    
    /**
     * @hibernate.property column="season"
     * @return
     */
    public String getSeason() {
    	return season;
    }
    
    public void setSeason(String value) {
    	season = value;
    }
    
    /**
     * @hibernate.property column="image_type"
     * @return
     */
    public String getImageType() {
    	return imageType;
    }
    
    public void setImageType(String value) {
    	imageType = value;
    }
    
	/**
	 * @hibernate.property column="user_creation_date"
	 */
	public String getUserCreationDate() {
		return userCreationDate;
	}
    
	public void setUserCreationDate(String value) {
		userCreationDate = value;
	}    
    
    /**
     * @hibernate.property column="last_edited_date"
     */
    public Date getLastEditedDate() {
        return lastEditedDate;
    }
    
    public void setLastEditedDate(Date value) {
        lastEditedDate = value;
    }
    
    /**
     * @hibernate.property column="checkout_date"
     */
    public Date getCheckoutDate() {
        return checkoutDate;
    }
    
    public void setCheckoutDate(Date value) {
        checkoutDate = value;
    }
    
    /**
     * @hibernate.many-to-one column="checkout_contributor_id" class="org.tolweb.treegrow.main.Contributor" not-null="false"
     */    
    public Contributor getCheckedOutContributor() {
        return checkedOutContributor;
    }
    
    public void setCheckedOutContributor(Contributor value) {
        checkedOutContributor = value;
    }
    
    /**
     * @hibernate.many-to-one column="last_edited_contributor_id" class="org.tolweb.treegrow.main.Contributor" not-null="false"
     */
    public Contributor getLastEditedContributor() {
        return lastEditedContributor;
    }
    
    public void setLastEditedContributor(Contributor value) {
        lastEditedContributor = value;
    }

    public void setScientificName(String value) {
	scientificName = value;
    }

    /**
     * @hibernate.property column="scientific_name"
     */
    public String getScientificName() {
	return scientificName;
    }

    public void setAltText(String value) {
        altText = value;
    }
    
    /**
     * @hibernate.property column="alt_text"
     */
    public String getAltText() {
        return altText;
    }

    public void setReference(String value) {
    	reference = value;
    }

    /**
     * @hibernate.property
     */
    public String getReference() {
    	return reference;
    }

    public void setCreator(String value) {
    	creator = value;
    }

    /**
     * @hibernate.property
     */
    public String getCreator() {
    	return creator;
    }

    public void setIdentifier(String value) {
    	identifier = value;
    }

    /**
     * @hibernate.property column="specimen_identifier"
     */
    public String getIdentifier() {
    	return identifier;
    }

    public void setAcknowledgements(String value) {
    	acknowledgements = value;
    }

    /**
     * @hibernate.property
     */
    public String getAcknowledgements() {
    	return acknowledgements;
    }

    public void setGeoLocation(String value) {
    	geoLocation = value;
    }

    /**
     * @hibernate.property column="geo_location"
     */
    public String getGeoLocation() {
    	return geoLocation;
    }

    public void setStage(String value) {
    	stage = value;
    }

    /**
     * @hibernate.property column="life_stage"
     */
    public String getStage() {
    	return stage;
    }

    public void setBodyPart(String value) {
    	bodyPart = value;
    }

    /**
     * @hibernate.property column="body_part"
     */
    public String getBodyPart() {
    	return bodyPart;
    }

    public void setSize(String value) {
    	size = value;
    }

    /**
     * @hibernate.property
     */
    public String getSize() {
    	return size;
    }

    public void setView(String value) {
    	view = value;
    }

    /**
     * @hibernate.property
     */
    public String getView() {
    	return view;
    }

    public void setPeriod(String value) {
    	period = value;
    }

    /**
     * @hibernate.property
     */
    public String getPeriod() {
    	return period;
    }

    public void setCollection(String value) {
		collection = value;
	}

	/**
	 * @hibernate.property
	 */
	public String getCollection() {
		return collection;
	}

	public void setCollectionAcronym(String value) {
		collectionAcronym = value;
	}

	/**
	 * @hibernate.property column="collection_acronym"
	 */
	public String getCollectionAcronym() {
		return collectionAcronym;
	}

	public void setType(String value) {
		type = value;
	}

	/**
	 * @hibernate.property
	 */
	public String getType() {
		return type;
	}

	public void setCollector(String value) {
		collector = value;
	}

	/**
	 * @hibernate.property
	 */
	public String getCollector() {
		return collector;
	}

    public void setSex(String value) {
		sex = value;
	}

	/**
	 * @hibernate.property
	 */
	public String getSex() {
		return sex;
	}

	public void setComments(String value) {
		comments = value;
	}

	/**
	 * @hibernate.property
	 */
	public String getComments() {
		return comments;
	}

	public void setCopyrightDate(String value) {
		copyrightDate = value;
	}

	/**
	 * @hibernate.property column="copyright_date"
	 */
	public String getCopyrightDate() {
		return copyrightDate;
	}

	public boolean isFossil() {
		return isFossil;
	}

    public void setIsFossil(boolean value) {
		isFossil = value;
	}

    /**
     * @hibernate.property column="fossil"
     */    
    public boolean getIsFossil() {
        return isFossil();
    }
    
    public boolean inPublicDomain() {
        return inPublicDomain;
    }
    
    public void setInPublicDomain(boolean value) {
        inPublicDomain = value;
    }
    
    /**
     * @hibernate.property column="public_domain"
     */
    public boolean getInPublicDomain() {
        return inPublicDomain();
    }
    
    public boolean auxiliaryChangedFromServer() {
        return true;
    }
    
    public boolean changedFromServer() {
        return changedFromServer;
    }
    
    public void setAuxiliaryChangedFromServer(boolean value) {
        //System.out.println("settting node changed to: " + value + " node is: " + node.getName());
        //node.setImagesChanged(value);
    }
    
    public void setChangedFromServer(boolean value) {
        changedFromServer = value;
    }
    
    /** The getter method for the image id
      *
      * @hibernate.id  generator-class="native" column="image_id" unsaved-value="-1"
      */
    public int getId() {
        return id;
    }
    
    public void setId(int value) {
        id = value;
    }
    
    public void setThumbnail(JLabel value) {
        thumbnail = value;
    }
    
    public JLabel getThumbnail() {
        if (thumbnail == null) {
            thumbnail = new JLabel();
        }
        return thumbnail;
    }
    
    public void initializeThumbnail() {
        if (!isLocal()) {
            String fileName = Controller.getController().getFileManager().getLocalImageFromNodeImage(this);
            File file = new File(fileName);
            String imgLoc = fileName;
            if (!file.exists()) {
                imgLoc = Controller.getController().getFileManager().getImagePath() + Controller.ERROR_IMG_NAME;
            }
            ImageIcon icon = new ImageIcon(imgLoc);
            thumbnail = new JLabel(icon);    
        } else {
            // If it's a local file make sure we scale it to a max height of 100
            // since that's what the server thumbs are scaled to
            scaleThumbnail();
        }
    }
    
    public void scaleThumbnail() {
        ImageIcon imgIcon = new ImageIcon(getLocation());
        Image img = imgIcon.getImage();
        int height = img.getHeight(null);
        if (height < 100) {
            thumbnail = new JLabel(imgIcon);
        } else {
            double scaleFactor = 100.0 / height;
            int width = img.getWidth(null);
            double newWidth = width * scaleFactor;
            Image scaledImg = img.getScaledInstance((int) newWidth, 100, Image.SCALE_FAST);
            ImageIcon scaledIcon = new ImageIcon(scaledImg);
            thumbnail = new JLabel(scaledIcon);
        }
    }

    public int compareTo(Object o1) {
        NodeImage img1 = (NodeImage) o1;
        if (getId() < img1.getId()) {
            return -1;
        } else if (getId() == img1.getId()) {
            return 0;
        } else {
            return 1;
        }
    }
    
    public boolean isLocal() {
        return getId() < 0;
    }
    
    public String getToLimgString() {
        return TOL_IMG_STRING + getId() + "\">";
    }

    public void addToNodes(ImageNode n) {
        nodes.add(n);
    }
    
    public void removeFromNodes(ImageNode n) {
        nodes.remove(n);
    }
    
    public void deleteNode(ImageNode n) {
        nodes.remove(n);
        deletedNodes.add(n);
    }
    
    public void undeleteNode(ImageNode n) {
        deletedNodes.remove(n);
        nodes.add(n);
    }
    
    public void addToDeletedNodes(ImageNode n) {
        deletedNodes.add(n);
    }    
    
    public void setNodes(Vector value) {
        nodes = value;
    }
    
    public void setDeletedNodes(Vector value) {
        deletedNodes = value;
    }
    
    public Vector getNodes() {
        return nodes;    
    }
    
    public Vector getDeletedNodes() {
        return deletedNodes;
    }
        
    /**
     * @hibernate.set table="Images_To_Nodes"
     * @hibernate.collection-key column="image_id"
     * @hibernate.collection-many-to-many class="org.tolweb.hibernate.MappedNode" column="node_id"
     * @hibernate.cache usage="nonstrict-read-write"
     */
    public Set getNodesSet() {
        if (nodesSet == null) {
            nodesSet = new HashSet();
        }
        return nodesSet;
    }
    
    public void setNodesSet(Set value) {
        nodesSet = value;
    }
    
    public void addToNodesSet(Node value) {
        getNodesSet().add(value);
    }
    
    public void removeFromNodesSet(Node value) {
        getNodesSet().remove(value);
    }
    
    public String getThumbnailUrl() {
        return Controller.getController().getWebPath() + "tree/ToLthumbs/" + getId() + ".png";
    }

	/**
	 * @hibernate.property column="is_people_working"
	 * @return
	 */    
    public boolean getIsPeopleWorking() {
    	return isPeopleWorking;
    }
    
    public void setIsPeopleWorking(boolean value) {
    	isPeopleWorking = value;
    }
    
	/**
	 * @hibernate.property column="is_equipment"
	 * @return
	 */
	public boolean getIsEquipment() {
		return isEquipment;
	}

	/**
	 * @param b
	 */
	public void setIsEquipment(boolean b) {
		isEquipment = b;
	}

	/**
	 * @hibernate.property column="is_habitat"
	 * @return
	 */
	public boolean getIsHabitat() {
		return isHabitat;
	}

	/**
	 * @param b
	 */
	public void setIsHabitat(boolean b) {
		isHabitat = b;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getBehavior() {
		return behavior;
	}

	/**
	 * @param string
	 */
	public void setBehavior(String string) {
		behavior = string;
	}	

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getNotes() {
		return notes;
	}
	
	/**
	 * @param string
	 */
	public void setNotes(String string) {
		notes = string;
	}	

	/**
	 * @hibernate.property column="voucher_number"
	 * @return
	 */
	public String getVoucherNumber() {
		return voucherNumber;
	}

	/**
	 * @param string
	 */
	public void setVoucherNumber(String string) {
		voucherNumber = string;
	}

	/**
	 * @hibernate.property column="voucher_number_collection"
	 * @return
	 */
	public String getVoucherNumberCollection() {
		return voucherNumberCollection;
	}

	/**
	 * @param string
	 */
	public void setVoucherNumberCollection(String string) {
		voucherNumberCollection = string;
	}
    /**
     * @return Returns the versionsSet.
     */
    public SortedSet getVersionsSet() {
        if (versionsSet == null) {
            versionsSet = new TreeSet();
        }
        return versionsSet;
    }
    /**
     * @param versionsSet The versionsSet to set.
     */
    public void setVersionsSet(SortedSet versionsSet) {
        this.versionsSet = versionsSet;
    }
    public void addToVersionsSet(ImageVersion version) {
        getVersionsSet().add(version);
    }
    /**
     * @hibernate.property column="modification_permitted"
     * @return Returns the modificationPermitted.
     */
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
    /**
     * @hibernate.many-to-one column="keywords_id" cascade="all" unique="true"
     * @return Returns the keywords.
     */
    public Keywords getKeywords() {
        if (keywords == null) {
            return new Keywords();
        }
        return keywords;
    }
    /**
     * @param keywords The keywords to set.
     */
    public void setKeywords(Keywords keywords) {
        this.keywords = keywords;
    }
    /**
     * @hibernate.many-to-one column="languages_id" cascade="all" unique="true"
     * @return Returns the languages.
     */
    public Languages getLanguages() {
        if (languages == null) {
            return new Languages();
        }
        return languages;
    }
    /**
     * @param languages The languages to set.
     */
    public void setLanguages(Languages languages) {
        this.languages = languages;
    }
    /**
     * @hibernate.property column="artistic"
     * @return Returns the artisticInterpretation.
     */
    public boolean getArtisticInterpretation() {
        return artisticInterpretation;
    }
    /**
     * @param artisticInterpretation The artisticInterpretation to set.
     */
    public void setArtisticInterpretation(boolean artisticInterpretation) {
        this.artisticInterpretation = artisticInterpretation;
    }
    /**
     * @hibernate.property column="technical_information"
     * @return Returns the technicalInformation.
     */
    public String getTechnicalInformation() {
        return technicalInformation;
    }
    /**
     * @param technicalInformation The technicalInformation to set.
     */
    public void setTechnicalInformation(String technicalInformation) {
        this.technicalInformation = technicalInformation;
    }
    /**
     * @hibernate.set table="Image_Permissions" lazy="false
     * @hibernate.collection-composite-element class="org.tolweb.hibernate.ImagePermission"
     * @hibernate.collection-key column="image_id"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     * @return Returns the permissionsSet.
     */
    public Set getPermissionsSet() {
        return permissionsSet;
    }
    /**
     * @param permissionsSet The permissionsSet to set.
     */
    public void setPermissionsSet(Set permissionsSet) {
        this.permissionsSet = permissionsSet;
    }
    
    public String getMediaTypeDescription() {
        return "image";
    }
    
    public int getMediaType() {
        return NodeImage.IMAGE;
    }

    /**
     * @hibernate.property
     * @return Returns the title.
     */
    public String getTitle() {
        if (StringUtils.notEmpty(title)) {
            return title;
        } else {
            return getLocation();
        }
    }

    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

	/**
	 * @hibernate.property
	 * @return
	 */
    public boolean getIsPrimaryImage() {
		return isPrimaryImage;
	}
	public void setIsPrimaryImage(boolean isPrimaryImage) {
		this.isPrimaryImage = isPrimaryImage;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getPublicDomainAddress() {
		return publicDomainAddress;
	}

	public void setPublicDomainAddress(String publicDomainAddress) {
		this.publicDomainAddress = publicDomainAddress;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getPublicDomainAuthor() {
		return publicDomainAuthor;
	}

	public void setPublicDomainAuthor(String publicDomainAuthor) {
		this.publicDomainAuthor = publicDomainAuthor;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getPublicDomainCreator() {
		return publicDomainCreator;
	}

	public void setPublicDomainCreator(String publicDomainCreator) {
		this.publicDomainCreator = publicDomainCreator;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getPublicDomainPublicationDate() {
		return publicDomainPublicationDate;
	}

	public void setPublicDomainPublicationDate(String publicDomainPublicationDate) {
		this.publicDomainPublicationDate = publicDomainPublicationDate;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getPublicDomainPublisher() {
		return publicDomainPublisher;
	}

	public void setPublicDomainPublisher(String publicDomainPublisher) {
		this.publicDomainPublisher = publicDomainPublisher;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getPublicDomainSourceName() {
		return publicDomainSourceName;
	}

	public void setPublicDomainSourceName(String publicDomainSourceName) {
		this.publicDomainSourceName = publicDomainSourceName;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getPublicDomainSourceUrl() {
		return publicDomainSourceUrl;
	}

	public void setPublicDomainSourceUrl(String publicDomainSourceUrl) {
		this.publicDomainSourceUrl = publicDomainSourceUrl;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getPublicDomainTitle() {
		return publicDomainTitle;
	}
	public void setPublicDomainTitle(String publicDomainTitle) {
		this.publicDomainTitle = publicDomainTitle;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getAttachmentComments() {
		return attachmentComments;
	}
	public void setAttachmentComments(String attachmentComments) {
		this.attachmentComments = attachmentComments;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getDontKnowAttachment() {
		return dontKnowAttachment;
	}
	public void setDontKnowAttachment(boolean dontKnowAttachment) {
		this.dontKnowAttachment = dontKnowAttachment;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getIsUnapproved() {
		return isUnapproved;
	}
	public void setIsUnapproved(boolean isUnapproved) {
		this.isUnapproved = isUnapproved;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getShowImageInfoLink() {
		return showImageInfoLink;
	}
	public void setShowImageInfoLink(boolean showImageInfoLink) {
		this.showImageInfoLink = showImageInfoLink;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public boolean getShowImageInfoLinkForInlineImage() {
		return showImageInfoLinkForInlineImage;
	}
	public void setShowImageInfoLinkForInlineImage(
			boolean showImageInfoLinkForInlineImage) {
		this.showImageInfoLinkForInlineImage = showImageInfoLinkForInlineImage;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Long getSourceDbId() {
		return sourceDbId;
	}
	public void setSourceDbId(Long sourceDbId) {
		this.sourceDbId = sourceDbId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getOtherSourceCollectionInfo() {
		return otherSourceCollectionInfo;
	}

	public void setOtherSourceCollectionInfo(String otherSourceCollectionInfo) {
		this.otherSourceCollectionInfo = otherSourceCollectionInfo;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getSourceCollectionTitle() {
		return sourceCollectionTitle;
	}

	public void setSourceCollectionTitle(String sourceCollectionTitle) {
		this.sourceCollectionTitle = sourceCollectionTitle;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getSourceCollectionUrl() {
		return sourceCollectionUrl;
	}
	public void setSourceCollectionUrl(String sourceCollectionUrl) {
		this.sourceCollectionUrl = sourceCollectionUrl;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getConsiderForPodcast() {
		return considerForPodcast;
	}
	public void setConsiderForPodcast(boolean considerForPodcast) {
		this.considerForPodcast = considerForPodcast;
	}
	public String getCcLicenseVersion() {
		if (getUsePermission() >= CC_BY20 && getUsePermission() <= CC_BY_NC_ND20) {
			return "2.0";
		} else {
			return "2.5";
		}
	}
	public String getCcLicenseName() {
		byte usePermission = getUsePermission();
		if (usePermission == CC_BY20 || usePermission == CC_BY25) {
			return "Attribution";
		} else if (usePermission == CC_BY_NC20 || usePermission == CC_BY_NC25) {
			return "Attribution-NonCommercial";
		} else if (usePermission == CC_BY_ND20 || usePermission == CC_BY_ND25) {
			return "Attribution-NoDerivs";
		} else if (usePermission == CC_BY_SA20 || usePermission == CC_BY_SA25) {
			return "Attribution-ShareAlike";
		} else if (usePermission == CC_BY_NC_SA20 || usePermission == CC_BY_NC_SA25) {
			return "Attribution-NonCommercial-ShareAlike";
		} else if (usePermission == CC_BY_NC_ND20 || usePermission == CC_BY_NC_ND25) {
			return "Attribution-NonCommercial-NoDerivs";
		}
		return "";
	}
	public String getCcLicenseUrl() {
		String url = "http://creativecommons.org/licenses/";
		if (usePermission == CC_BY20 || usePermission == CC_BY25) {
			url += "by";
		} else if (usePermission == CC_BY_NC20 || usePermission == CC_BY_NC25) {
			url += "by-nc";
		} else if (usePermission == CC_BY_ND20 || usePermission == CC_BY_ND25) {
			url += "by-nd";
		} else if (usePermission == CC_BY_SA20 || usePermission == CC_BY_SA25) {
			url += "by-sa";
		} else if (usePermission == CC_BY_NC_SA20 || usePermission == CC_BY_NC_SA25) {
			url += "by-nc-sa";
		} else if (usePermission == CC_BY_NC_ND20 || usePermission == CC_BY_NC_ND25) {
			url += "by-nc-nd";
		}
		url += "/" + getCcLicenseVersion() + "/";
		return url;
	}
	public static final String getWordForMediaType(int mediaType) {
		switch(mediaType) {
			case DOCUMENT: return "document";
			case SOUND: return "sound";
			case MOVIE: return "movie";
			default: return "image";
		}	
	}
}