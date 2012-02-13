/*
 * Created on Jul 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.Keywords;
import org.tolweb.treegrow.main.Languages;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.UsePermissable;
import org.tolweb.treegrow.page.AccessoryPage;
import org.tolweb.treegrow.page.AccessoryPageContributor;
import org.tolweb.treegrow.page.InternetLink;
import org.tolweb.treegrow.page.PageContributor;

/**
 * @author dmandel
 *
 @hibernate.class table="ACCESSORY_PAGES" discriminator-value="0"
 @hibernate.cache usage="nonstrict-read-write"
 @hibernate.discriminator column="discriminator" type="int"
 */
public class MappedAccessoryPage extends AccessoryPage implements MultiContributorObject, UsePermissable, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -761793862286811074L;
	public static final byte INVESTIGATION = 1;
    public static final byte STORY = 2;
    public static final byte ARTANDCULTURE = 3;
    public static final byte TEACHERRESOURCE = 4;
    public static final byte GAME = 5;
    public static final byte BIOGRAPHY = 6;
    public static final byte PORTFOLIO = 7;
    public static final byte WEBQUEST = 8;
    public static final String ARTICLE = "Article";
    public static final String NOTE = "Note";
    public static final int NEED_TO_EDIT = 0;
    public static final int WORK_IN_PROGRESS = 1;
    public static final int COMPLETE = 2;
    
	private Date submissionDate, creationDate, lastEditedDate;
	private Long submittedContributorId, lastEditedContributorId;
	private Long id;
	private Long copiedId;
	private SortedSet contributors;
    private SortedSet editComments;
	private Contributor submittedContributor, lastEditedContributor, contributor;
	private byte treehouseType;
	private boolean isInvestigation, isStory, isArtAndCulture, isTeacherResource, isGame,
		isBiography, isBeginnerLevel, isIntermediateLevel, isAdvancedLevel;
	private String comments, scientificName, internetInfo;
	private SortedSet nodesSet;
	private MappedNode primaryAttachedNode;
	private Boolean modificationPermitted = Boolean.valueOf(false);
	private int aboutPageProgress;
	private int pageContentProgress;
	private int mediaProgress;
	private int refsProgress;
	private int learningProgress;
	private int attachProgress;
	private int notesProgress;
	private int portfolioProgress;
    private Keywords keywords;
    private Portfolio portfolio;
    private boolean isSubmittedToTeacher;
	private int gradeLevel;
	private boolean gradeModifiable;
	private String description;
	private boolean isClassroom;
	private boolean isLab;
	private boolean isField;
	private boolean isFieldTrip;
	private boolean isWebBased;
	private boolean isHomeBased;
	private boolean isInformal;
	private boolean isMuseumBased;
	private boolean isLanguageArts;
	private boolean isTechnology;
	private boolean isMathematics;
	private boolean isHealth;
	private boolean isSocialStudies;
	private boolean isArts;
	private boolean isInquiryLearning;
	private boolean isHandsOnLearning;
	private boolean isVisualInstruction;
	private boolean isRolePlaying;
	private boolean isMovement;
	private boolean isTechnologyIntegration;
	private boolean isLearningModules;
	private boolean isDiscussion;
	private boolean isDemonstration;
	private boolean isLecture;
	private boolean isPresentation;
	private String otherLearningStrategy;
	private boolean isHeterogeneous;
	private boolean isHomogeneous;
	private boolean isCrossAge;
	private boolean isIndividualized;
	private boolean isLargeGroup;
	private boolean isSmallGroup;
	private String timeFrame;
	private String sequenceContext;
	private Languages languages;
	private String stateStandards;
	private String stateStandardsSubjects;
	private String stateStandardsValue;
	private String nationalStandardsSubjects;
	private String nationalStandardsValue;
	private Long stateStandardsId;
	private Document stateStandardsDocument;
	private Long nationalStandardsId;
	private Document nationalStandardsDocument;
	private Long editHistoryId;

	public void setAccessoryPageId(Long value) {
		id = value;
	}

	/**
	 * @hibernate.id generator-class="native" column="id" unsaved-value="null"
	 * @return
	 */	
	public Long getAccessoryPageId() {
		return id;
	}
	
	public void setCopiedIdentifier(Long value) {
		copiedId = value;
	}
	
	public Long getCopiedIdentifier() {
		return copiedId;		
	}
	
	/**
     * @hibernate.set table="EditComments" lazy="false" order-by="page_order desc" sort="natural" cascade="all"
     * @hibernate.collection-composite-element class="org.tolweb.hibernate.EditComment"
     * @hibernate.collection-key column="page_id"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     * @return Returns the editComments.
     */
    public SortedSet getEditComments() {
        if (editComments == null) {
            editComments = new TreeSet();
        }
        return editComments;
    }

    /**
     * @param editComments The editComments to set.
     */
    public void setEditComments(SortedSet editComments) {
        this.editComments = editComments;
    }

    /**
	 * @hibernate.set table="ACC_PAGE_CONTRIBUTORS" lazy="false" order-by="page_order asc" sort="natural" cascade="all"
	 * @hibernate.collection-composite-element class="org.tolweb.treegrow.page.AccessoryPageContributor"
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
	
	public void addToContributors(AccessoryPageContributor value) {
	    if (!getContributors().isEmpty()) {
			// Make sure it gets added on to the end of the list
		    AccessoryPageContributor lastContr = (AccessoryPageContributor) getContributors().last();
	        value.setOrder(lastContr.getOrder() + 1);
	    } else {
	        value.setOrder(0);
	    }
		getContributors().add(value);
	}
	
	public void removeFromContributors(AccessoryPageContributor value) {
		getContributors().remove(value);
	}
	
	public void copyValues(AccessoryPage other) {
		super.copyValues(other);
		MappedAccessoryPage otherPage = (MappedAccessoryPage) other;
		Iterator it = otherPage.getContributors().iterator();
		SortedSet contributors = new TreeSet();
		while (it.hasNext()) {
			AccessoryPageContributor nextOtherContr = (AccessoryPageContributor) it.next();
			contributors.add(nextOtherContr.clone());
		}
		it = ((MappedAccessoryPage) other).getNodesSet().iterator();
		SortedSet nodes = new TreeSet();
		while (it.hasNext()) {
			AccessoryPageNode node = (AccessoryPageNode) it.next();
			AccessoryPageNode clonedNode = (AccessoryPageNode) node.clone();
			System.out.println("is the clone primary? " + clonedNode.getIsPrimaryAttachedNode());
			nodes.add(clonedNode);
		}
		setNodesSet(nodes);
		setContributors(contributors);
		setSubmissionDate(otherPage.getSubmissionDate());
		setSubmittedContributorId(otherPage.getSubmittedContributorId());
		setTreehouseType(otherPage.getTreehouseType());
		setIsAdvancedLevel(otherPage.getIsAdvancedLevel());
		setIsArtAndCulture(otherPage.getIsArtAndCulture());
		setIsBeginnerLevel(otherPage.getIsBeginnerLevel());
		setIsGame(otherPage.getIsGame());
		setIsIntermediateLevel(otherPage.getIsIntermediateLevel());
		setIsInvestigation(otherPage.getIsInvestigation());
		setIsStory(otherPage.getIsStory());
		setIsTeacherResource(otherPage.getIsTeacherResource());
		setCreationDate(otherPage.getCreationDate());
		setContributor(otherPage.getContributor());
		setLastEditedContributor(otherPage.getLastEditedContributor());
		setLastEditedContributorId(otherPage.getLastEditedContributorId());
		setLastEditedDate(otherPage.getLastEditedDate());
		setComments(otherPage.getComments());
		setIsBiography(otherPage.getIsBiography());
		setScientificName(otherPage.getScientificName());
		setUsePermission(other.getUsePermission());
	}
	
	/**
	 * Returns the id of the contributor who submitted this AccessoryPage
	 * @hibernate.property column="submitted_contributor_id"
	 * @return
	 */
	public Long getSubmittedContributorId() {
		if (submittedContributorId != null && !(submittedContributorId.longValue() == 0)) {
			return submittedContributorId;
		} else if (submittedContributor != null) {
			return Long.valueOf(submittedContributor.getId()); 
		} else {
			return null;
		}
	}
	
	public void setSubmittedContributorId(Long value) {
		submittedContributorId = value;
	}
	
	public Contributor getSubmittedContributor() {
		return submittedContributor;	
	}
	
	public void setSubmittedContributor(Contributor value) {
		submittedContributor = value;
	}
	
	/**
	 * @hibernate.property column="submission_date"
	 * @return
	 */
	public Date getSubmissionDate() {
		return submissionDate;
	}
	
	public void setSubmissionDate(Date value) {
		submissionDate = value;
	}
    
	/**
	 * @hibernate.property column="treehouse_type"
	 * @return
	 */
	public byte getTreehouseType() {
        return treehouseType;
    }
	
    public void setTreehouseType(byte treehouseType) {
        this.treehouseType = treehouseType;
    }
    
    public String getTreehouseTypeString() {
        return getTreehouseTypeString(getTreehouseType());
    }
    
    public static String getTreehouseTypeString(byte treehouseType) {
	    switch (treehouseType) {
	    	case MappedAccessoryPage.ARTANDCULTURE: return "Art and Culture";
	    	case MappedAccessoryPage.BIOGRAPHY: return "Biography";
	    	case MappedAccessoryPage.GAME: return "Fun and Games";
	    	case MappedAccessoryPage.INVESTIGATION: return "Investigation";
	    	case MappedAccessoryPage.STORY: return "Story";
	    	case MappedAccessoryPage.TEACHERRESOURCE: return "Teacher Resource";
	    	case MappedAccessoryPage.PORTFOLIO: return "Portfolio";
	    	default: return "";
	    }        
    }
    
    public static String getGradeLevelLabel(int index) {
	    // needed in order to work with legacy stuff
	    switch (index) {
	    	case -1: return "Choose Grade/Age level";
	    	case 0: return "All Grade/Age levels";
	    	case 1: return "Grades k-2 (Ages 5-7)";
	    	case 2: return "Grades 3-5 (Ages 8-12)";
	    	case 3: return "Grades 6-8 (Ages 12-14)";
	    	case 4: return "High School (Ages 15-18)";
	    	case 5: return "Adult/Continuing education";
	    	case 6: return "Undergraduate (Lower Division)";
	    	case 7: return "Undergraduate (Upper Division)";
	    	case 8: return "Graduate";
	    	default: return "";
	    }
	}

	/**
     * @hibernate.property column="is_investigation"
     * @return
     */
    public boolean getIsInvestigation() {
        return isInvestigation;
    }
    
    public void setIsInvestigation(boolean value) {
        isInvestigation = value;
    }
    
    /**
     * @hibernate.property column="is_story"
     * @return
     */
    public boolean getIsStory() {
        return isStory;
    }
    
    public void setIsStory(boolean value) {
        isStory = value;
    }
    
    /**
     * @hibernate.property column="is_artandculture"
     * @return
     */
    public boolean getIsArtAndCulture() {
        return isArtAndCulture;
    }
    
    public void setIsArtAndCulture(boolean value) {
        isArtAndCulture = value;
    }
    
    /**
     * @hibernate.property column="is_teacherresource"
     * @return
     */
    public boolean getIsTeacherResource() {
        return isTeacherResource;
    }
    
    public void setIsTeacherResource(boolean value) {
        isTeacherResource = value;
    }
    
    /**
     * @hibernate.property column="is_game"
     * @return
     */
    public boolean getIsGame() {
        return isGame;
    }
    
    public void setIsGame(boolean value) {
        isGame = value;
    }    

    /**
     * @hibernate.property column="is_beginner"
     * @return
     */
    public boolean getIsBeginnerLevel() {
        return isBeginnerLevel;
    }
    
    public void setIsBeginnerLevel(boolean value) {
        isBeginnerLevel = value;
    }

    /**
     * @hibernate.property column="is_intermediate"
     * @return
     */
    public boolean getIsIntermediateLevel() {
        return isIntermediateLevel;
    }
    
    public void setIsIntermediateLevel(boolean value) {
        isIntermediateLevel = value;
    }  
    
    /**
     * @hibernate.property column="is_advanced"
     * @return
     */
    public boolean getIsAdvancedLevel() {
        return isAdvancedLevel;
    }
    
    public void setIsAdvancedLevel(boolean value) {
        isAdvancedLevel = value;
    }
    
    /**
     * @hibernate.property column="is_biography"
     * @return
     */
    public boolean getIsBiography() {
        return isBiography;
    }
    
    public void setIsBiography(boolean value) {
        isBiography = value;
    }    
    /**
     * @hibernate.property column="creation_date"
     * @return
     */
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    public Contributor getLastEditedContributor() {
        return lastEditedContributor;
    }
    public void setLastEditedContributor(Contributor lastEditedContributor) {
        this.lastEditedContributor = lastEditedContributor;
        if (lastEditedContributor != null) {
            setLastEditedContributorId(Long.valueOf(lastEditedContributor.getId()));
        }
    }
    
    /**
     * @hibernate.property column="last_edited_contributor_id"
     * @return
     */
    public Long getLastEditedContributorId() {
        return lastEditedContributorId;
    }
    public void setLastEditedContributorId(Long lastEditedContributorId) {
        this.lastEditedContributorId = lastEditedContributorId;
    }
    
    /**
     * @hibernate.property column="last_edited_date"
     * @return
     */
    public Date getLastEditedDate() {
        return lastEditedDate;
    }
    public void setLastEditedDate(Date lastEditedDate) {
        this.lastEditedDate = lastEditedDate;
    }
    
    public Contributor getContributor() {
        return contributor;
    }
    
    public void setContributor(Contributor value) {
        contributor = value;
    }
    
    /**
     * @hibernate.property
     * @return
     */
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    /**
     * @hibernate.property column="scientific_name"
     * @return
     */
    public String getScientificName() {
        return scientificName;
    }
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }
    
	/**
	 * @hibernate.set table="Acc_Pages_To_Nodes" lazy="false"  sort="natural"
	 * @hibernate.collection-composite-element class="org.tolweb.hibernate.AccessoryPageNode"
	 * @hibernate.collection-key column="acc_page_id"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 */
	public SortedSet getNodesSet() {
		if (nodesSet == null) {
			nodesSet = new TreeSet();
		}
		return nodesSet;
	}
    
	public void setNodesSet(SortedSet value) {
		nodesSet = value;
	}
	
	public void addToNodesSet(AccessoryPageNode value) {
		nodesSet.add(value);	
	}
	
	public void removeFromNodesSet(AccessoryPageNode value) {
		nodesSet.remove(value);
	} 
	
	/**
	 * Returns the 'primary node of attachment' for this page.  
	 * This is the node that shows up as a link back to the branch page
	 * @return The primary node of attachment
	 */
	public MappedNode getPrimaryAttachedNode() {
	    if (primaryAttachedNode == null) {
		    Iterator it = nodesSet.iterator();
		    while (it.hasNext()) {
		        AccessoryPageNode nd = (AccessoryPageNode) it.next();
		        if (nd.getIsPrimaryAttachedNode()) {
		            primaryAttachedNode = nd.getNode();
		            return primaryAttachedNode;
		        }
		    }
		    return null;
	    }
	    return primaryAttachedNode;
	}
	
	/**
     * @hibernate.property column="menu_order"
     */
    public int getOrder() {
        return super.getOrder();
    }
    /**
     * @hibernate.property column="internetinfo_text"
     * @return Returns the internetInfo.
     */
    public String getInternetInfo() {
        return internetInfo;
    }
    /**
     * @param internetInfo The internetInfo to set.
     */
    public void setInternetInfo(String internetInfo) {
        this.internetInfo = internetInfo;
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
    
    
    public int getId() {
    	if (getAccessoryPageId() != null) {
    	    return getAccessoryPageId().intValue();
    	} else {
    	    return -1;
    	}
    }
    /**
     * @hibernate.property column="aboutpage_progress"
     * @return Returns the step1Progress.
     */
    public int getAboutPageProgress() {
        return aboutPageProgress;
    }
    /**
     * 
     * @param step1Progress The step1Progress to set.
     */
    public void setAboutPageProgress(int step1Progress) {
        this.aboutPageProgress = step1Progress;
    }
    /**
     * @hibernate.property column="pagecontent_progress"
     * @return Returns the step2Progress.
     */
    public int getPageContentProgress() {
        return pageContentProgress;
    }
    /**
     * @param step2Progress The step2Progress to set.
     */
    public void setPageContentProgress(int step2Progress) {
        this.pageContentProgress = step2Progress;
    }
    /**
     * @hibernate.property column="media_progress"
     * @return Returns the step3Progress.
     */
    public int getMediaProgress() {
        return mediaProgress;
    }
    /**
     * @param step3Progress The step3Progress to set.
     */
    public void setMediaProgress(int step3Progress) {
        this.mediaProgress = step3Progress;
    }
    /**
     * @hibernate.property column="refs_progress"
     * @return Returns the step4Progress.
     */
    public int getRefsProgress() {
        return refsProgress;
    }
    /**
     * @param step4Progress The step4Progress to set.
     */
    public void setRefsProgress(int step4Progress) {
        this.refsProgress = step4Progress;
    }
    /**
     * @hibernate.property column="learning_progress"
     * @return Returns the step5Progress.
     */
    public int getLearningProgress() {
        return learningProgress;
    }
    /**
     * @param step5Progress The step5Progress to set.
     */
    public void setLearningProgress(int step5Progress) {
        this.learningProgress = step5Progress;
    }
    /**
     * @hibernate.property column="attach_progress"
     * @return Returns the step6Progress.
     */
    public int getAttachProgress() {
        return attachProgress;
    }
    /**
     * @param step6Progress The step6Progress to set.
     */
    public void setAttachProgress(int step6Progress) {
        this.attachProgress = step6Progress;
    }
    /**
     * @hibernate.property column="notes_progress"
     * @return Returns the step7Progress.
     */
    public int getNotesProgress() {
        return notesProgress;
    }
    /**
     * @param step7Progress The step7Progress to set.
     */
    public void setNotesProgress(int step7Progress) {
        this.notesProgress = step7Progress;
    }
    
    /**
     * @hibernate.property column="portfolio_progress"
     * @return Returns the portfolioProgress.
     */
    public int getPortfolioProgress() {
        return portfolioProgress;
    }
    /**
     * @param portfolioProgress The portfolioProgress to set.
     */
    public void setPortfolioProgress(int portfolioProgress) {
        this.portfolioProgress = portfolioProgress;
    }
    public boolean getHasInternetLinks() {
	    Set links = getInternetLinks();
	    if (links != null && links.size() > 0) {
	        // Check to see if any of the link objects actually have any content in them.
	        for (Iterator iter = links.iterator(); iter.hasNext();) {
                InternetLink nextLink = (InternetLink) iter.next();
                if (StringUtils.notEmpty(nextLink.getUrl())) {
                    return true;
                }
            }
	        return false;
	    } else {
	        return false;
	    }
    }
    
    /**
     * @hibernate.many-to-one column="keywords_id" cascade="all" unique="true"
     * @return Returns the keywords.
     */
    public Keywords getKeywords() {
        return keywords;
    }
    /**
     * @param keywords The keywords to set.
     */
    public void setKeywords(Keywords keywords) {
        this.keywords = keywords;
    }    
    /**
     * @hibernate.many-to-one column="portfolio_id" cascade="all" unique="true"
     * @return Returns the portfolio.
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }
    /**
     * @param portfolio The portfolio to set.
     */
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
    
    public String getArticleNoteTypeString() {
        if (getIsArticle()) {
            return "Article";
        } else {
            return "Note";
        }
    }
    
    public boolean getIsOther() {
        return getIsTeacherResourceInstance() && ((TeacherResource) this).getResourceType() == TeacherResource.OTHER;
    }
    
    public boolean getIsTeacherResourceInstance() {
        return TeacherResource.class.isInstance(this) && !getIsWebquest();
    }
    
    public boolean getIsWebquest() {
        return Webquest.class.isInstance(this);
    }
    
    public boolean getIsPortfolio() {
        return getTreehouseType() == MappedAccessoryPage.PORTFOLIO;
    }
    
    public boolean getHasPortfolio() {
        return getIsTeacherResourceInstance() || getIsPortfolio();
    }
    
    public String getLearnerLevelString() {
        List strings = new ArrayList();
        if (getIsBeginnerLevel()) {
            strings.add("Beginner");
        }
        if (getIsIntermediateLevel()) {
            strings.add("Intermediate");
        }
        if (getIsAdvancedLevel()) {
            strings.add("Advanced");
        }
        return StringUtils.returnJoinedString(strings, ";");
    }    
    
    public boolean getHasAnyLearningInfo() {
        return StringUtils.notEmpty(getLearnerLevelString()) || getAdditionalTypesList().size() > 0 ||
        	StringUtils.notEmpty(getComments()) || getGradeModifiable() ||
        	StringUtils.notEmpty(getDescription()) || getIsClassroom() ||
        	getIsLab() || getIsField() || getIsFieldTrip() || getIsWebBased() || 
        	getIsHomeBased() || getIsInformal() || getIsMuseumBased() || getIsLanguageArts() ||
        	getIsTechnology() || getIsMathematics() || getIsHealth() || getIsField() || 
        	getIsSocialStudies() || getIsArts() || getIsInquiryLearning() || getIsHandsOnLearning() ||
        	getIsVisualInstruction() || getIsRolePlaying() || getIsMovement() || 
        	getIsTechnologyIntegration() || getIsLearningModules() || getIsDiscussion() ||
        	getIsDemonstration() || getIsLecture() || getIsPresentation() ||
        	StringUtils.notEmpty(getOtherLearningStrategy()) || getIsHeterogeneous() ||
        	getIsHomogeneous() || getIsCrossAge() || getIsIndividualized() || 
        	getIsLargeGroup() || getIsSmallGroup() || StringUtils.notEmpty(getTimeFrame()) ||
        	StringUtils.notEmpty(getSequenceContext()) || getLanguages().getHasAnyFields() ||
        	StringUtils.notEmpty(getStateStandards()) || StringUtils.notEmpty(getStateStandardsSubjects()) ||
        	StringUtils.notEmpty(getStateStandardsValue()) || 
        	StringUtils.notEmpty(getNationalStandardsSubjects()) || 
        	StringUtils.notEmpty(getNationalStandardsValue());
    }
    
    public List getAdditionalTypesList() {
        List strings = new ArrayList();
        if (getIsArtAndCulture()) {
            strings.add("Art and Culture");
        }
        if (getIsBiography()) {
            strings.add("Biography");
        }
        if (getIsGame()) {
            strings.add("Fun and Games");
        }
        if (getIsInvestigation()) {
            strings.add("Investigation");
        }
        if (getIsStory()) {
            strings.add("Story");
        }
        return strings;        
    }
    
    public Student getMinorAuthor() {
        for (Iterator iter = getContributors().iterator(); iter.hasNext();) {
            PageContributor contributor = (PageContributor) iter.next();
            if (Student.class.isInstance(contributor.getContributor())) {
                return (Student) contributor.getContributor();
            }
        }       
        return null;        
    }
    
    public int getDiscriminatorValue() {
        if (Webquest.class.isInstance(this)) {
            return 2; 
        } else if (TeacherResource.class.isInstance(this)) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * @hibernate.property
     * @return Returns the isSubmitted.
     */
    public boolean getIsSubmittedToTeacher() {
        return isSubmittedToTeacher;
    }
    /**
     * @param isSubmitted The isSubmitted to set.
     */
    public void setIsSubmittedToTeacher(boolean isSubmitted) {
        this.isSubmittedToTeacher = isSubmitted;
    }

	/**
	 * @hibernate.property column="grade_level"
	 * @return Returns the gradeLevel.
	 */
	public int getGradeLevel() {
	    return gradeLevel;
	}

	/**
	 * @param gradeLevel The gradeLevel to set.
	 */
	public void setGradeLevel(int gradeLevel) {
	    this.gradeLevel = gradeLevel;
	}

	/**
	 * @hibernate.property column="grade_modifiable"
	 * @return Returns the gradeModifiable.
	 */
	public boolean getGradeModifiable() {
	    return gradeModifiable;
	}

	/**
	 * @param gradeModifiable The gradeModifiable to set.
	 */
	public void setGradeModifiable(boolean gradeModifiable) {
	    this.gradeModifiable = gradeModifiable;
	}

	/**
	 * @hibernate.property
	 * @return Returns the description.
	 */
	public String getDescription() {
	    return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
	    this.description = description;
	}

	/**
	 * @hibernate.property column="is_classroom"
	 * @return Returns the isClassroom.
	 */
	public boolean getIsClassroom() {
	    return isClassroom;
	}

	/**
	 * @param isClassroom The isClassroom to set.
	 */
	public void setIsClassroom(boolean isClassroom) {
	    this.isClassroom = isClassroom;
	}

	/**
	 * @hibernate.property column="is_field"
	 * @return Returns the isField.
	 */
	public boolean getIsField() {
	    return isField;
	}

	/**
	 * @param isField The isField to set.
	 */
	public void setIsField(boolean isField) {
	    this.isField = isField;
	}

	/**
	 * @hibernate.property column="is_field_trip"
	 * @return Returns the isFieldTrip.
	 */
	public boolean getIsFieldTrip() {
	    return isFieldTrip;
	}

	/**
	 * @param isFieldTrip The isFieldTrip to set.
	 */
	public void setIsFieldTrip(boolean isFieldTrip) {
	    this.isFieldTrip = isFieldTrip;
	}

	/**
	 * @hibernate.property column="is_home_based"
	 * @return Returns the isHomeBased.
	 */
	public boolean getIsHomeBased() {
	    return isHomeBased;
	}

	/**
	 * @param isHomeBased The isHomeBased to set.
	 */
	public void setIsHomeBased(boolean isHomeBased) {
	    this.isHomeBased = isHomeBased;
	}

	/**
	 * @hibernate.property column="is_informal"
	 * @return Returns the isInformal.
	 */
	public boolean getIsInformal() {
	    return isInformal;
	}

	/**
	 * @param isInformal The isInformal to set.
	 */
	public void setIsInformal(boolean isInformal) {
	    this.isInformal = isInformal;
	}

	/**
	 * @hibernate.property column="is_lab"
	 * @return Returns the isLab.
	 */
	public boolean getIsLab() {
	    return isLab;
	}

	/**
	 * @param isLab The isLab to set.
	 */
	public void setIsLab(boolean isLab) {
	    this.isLab = isLab;
	}

	/**
	 * @hibernate.property column="is_museum_based"
	 * @return Returns the isMuseumBased.
	 */
	public boolean getIsMuseumBased() {
	    return isMuseumBased;
	}

	/**
	 * @param isMuseumBased The isMuseumBased to set.
	 */
	public void setIsMuseumBased(boolean isMuseumBased) {
	    this.isMuseumBased = isMuseumBased;
	}

	/**
	 * @hibernate.property column="is_web_based"
	 * @return Returns the isWebBased.
	 */
	public boolean getIsWebBased() {
	    return isWebBased;
	}

	/**
	 * @param isWebBased The isWebBased to set.
	 */
	public void setIsWebBased(boolean isWebBased) {
	    this.isWebBased = isWebBased;
	}

	/**
	 * @hibernate.property column="is_arts"
	 * @return Returns the isArts.
	 */
	public boolean getIsArts() {
	    return isArts;
	}

	/**
	 * @param isArts The isArts to set.
	 */
	public void setIsArts(boolean isArts) {
	    this.isArts = isArts;
	}

	/**
	 * @hibernate.property column="is_health"
	 * @return Returns the isHealth.
	 */
	public boolean getIsHealth() {
	    return isHealth;
	}

	/**
	 * @param isHealth The isHealth to set.
	 */
	public void setIsHealth(boolean isHealth) {
	    this.isHealth = isHealth;
	}

	/**
	 * @hibernate.property column="is_language_arts"
	 * @return Returns the isLanguageArts.
	 */
	public boolean getIsLanguageArts() {
	    return isLanguageArts;
	}

	/**
	 * @param isLanguageArts The isLanguageArts to set.
	 */
	public void setIsLanguageArts(boolean isLanguageArts) {
	    this.isLanguageArts = isLanguageArts;
	}

	/**
	 * @hibernate.property column="is_mathematics"
	 * @return Returns the isMathematics.
	 */
	public boolean getIsMathematics() {
	    return isMathematics;
	}

	/**
	 * @param isMathematics The isMathematics to set.
	 */
	public void setIsMathematics(boolean isMathematics) {
	    this.isMathematics = isMathematics;
	}

	/**
	 * @hibernate.property column="is_social_studies"
	 * @return Returns the isSocialStudies.
	 */
	public boolean getIsSocialStudies() {
	    return isSocialStudies;
	}

	/**
	 * @param isSocialStudies The isSocialStudies to set.
	 */
	public void setIsSocialStudies(boolean isSocialStudies) {
	    this.isSocialStudies = isSocialStudies;
	}

	/**
	 * @hibernate.property column="is_technology"
	 * @return Returns the isTechnology.
	 */
	public boolean getIsTechnology() {
	    return isTechnology;
	}

	public boolean getHasStateStandards() {
	    return StringUtils.notEmpty(getStateStandards()) || StringUtils.notEmpty(getStateStandardsValue()) 
	    	|| getStateStandardsDocument() != null || StringUtils.notEmpty(getStateStandardsSubjects());
	}

	public boolean getHasNationalStandards() {
	    return StringUtils.notEmpty(getNationalStandardsValue()) || StringUtils.notEmpty(getNationalStandardsSubjects()) 
	    	|| getNationalStandardsDocument() != null;
	}

	public boolean getHasStandards() {
	    return getHasStateStandards() || getHasNationalStandards();
	}

	/**
	 * @hibernate.property column="is_hands_on_learning"
	 * @return Returns the isHandsOnLearning.
	 */
	public boolean getIsHandsOnLearning() {
	    return isHandsOnLearning;
	}

	/**
	 * @param isHandsOnLearning The isHandsOnLearning to set.
	 */
	public void setIsHandsOnLearning(boolean isHandsOnLearning) {
	    this.isHandsOnLearning = isHandsOnLearning;
	}

	/**
	 * @hibernate.property column="is_inquiry_learning"
	 * @return Returns the isInquiryLearning.
	 */
	public boolean getIsInquiryLearning() {
	    return isInquiryLearning;
	}

	/**
	 * @param isInquiryLearning The isInquiryLearning to set.
	 */
	public void setIsInquiryLearning(boolean isInquiryLearning) {
	    this.isInquiryLearning = isInquiryLearning;
	}

	/**
	 * @hibernate.property column="is_demonstration"
	 * @return Returns the isDemonstration.
	 */
	public boolean getIsDemonstration() {
	    return isDemonstration;
	}

	/**
	 * @param isDemonstration The isDemonstration to set.
	 */
	public void setIsDemonstration(boolean isDemonstration) {
	    this.isDemonstration = isDemonstration;
	}

	/**
	 * @hibernate.property column="is_discussion"
	 * @return Returns the isDiscussion.
	 */
	public boolean getIsDiscussion() {
	    return isDiscussion;
	}

	/**
	 * @param isDiscussion The isDiscussion to set.
	 */
	public void setIsDiscussion(boolean isDiscussion) {
	    this.isDiscussion = isDiscussion;
	}

	/**
	 * @hibernate.property column="is_learning_modules"
	 * @return Returns the isLearningModules.
	 */
	public boolean getIsLearningModules() {
	    return isLearningModules;
	}

	/**
	 * @param isLearningModules The isLearningModules to set.
	 */
	public void setIsLearningModules(boolean isLearningModules) {
	    this.isLearningModules = isLearningModules;
	}

	/**
	 * @hibernate.property column="is_lecture"
	 * @return Returns the isLecture.
	 */
	public boolean getIsLecture() {
	    return isLecture;
	}

	/**
	 * @param isLecture The isLecture to set.
	 */
	public void setIsLecture(boolean isLecture) {
	    this.isLecture = isLecture;
	}

	/**
	 * @hibernate.property column="is_movement"
	 * @return Returns the isMovement.
	 */
	public boolean getIsMovement() {
	    return isMovement;
	}

	/**
	 * @param isMovement The isMovement to set.
	 */
	public void setIsMovement(boolean isMovement) {
	    this.isMovement = isMovement;
	}

	/**
	 * @hibernate.property column="is_presentation"
	 * @return Returns the isPresentation.
	 */
	public boolean getIsPresentation() {
	    return isPresentation;
	}

	/**
	 * @param isPresentation The isPresentation to set.
	 */
	public void setIsPresentation(boolean isPresentation) {
	    this.isPresentation = isPresentation;
	}

	/**
	 * @hibernate.property column="is_role_playing"
	 * @return Returns the isRolePlaying.
	 */
	public boolean getIsRolePlaying() {
	    return isRolePlaying;
	}

	/**
	 * @param isRolePlaying The isRolePlaying to set.
	 */
	public void setIsRolePlaying(boolean isRolePlaying) {
	    this.isRolePlaying = isRolePlaying;
	}

	/**
	 * @hibernate.property column="is_technology_integration"
	 * @return Returns the isTechnologyIntegration.
	 */
	public boolean getIsTechnologyIntegration() {
	    return isTechnologyIntegration;
	}

	/**
	 * @param isTechnologyIntegration The isTechnologyIntegration to set.
	 */
	public void setIsTechnologyIntegration(boolean isTechnologyIntegration) {
	    this.isTechnologyIntegration = isTechnologyIntegration;
	}

	/**
	 * @hibernate.property column="is_visual_instruction"
	 * @return Returns the isVisualInstruction.
	 */
	public boolean getIsVisualInstruction() {
	    return isVisualInstruction;
	}

	/**
	 * @param isVisualInstruction The isVisualInstruction to set.
	 */
	public void setIsVisualInstruction(boolean isVisualInstruction) {
	    this.isVisualInstruction = isVisualInstruction;
	}

	/**
	 * @hibernate.property column="other_learning_strategy"
	 * @return Returns the otherLearningStrategy.
	 */
	public String getOtherLearningStrategy() {
	    return otherLearningStrategy;
	}

	/**
	 * @param otherLearningStrategy The otherLearningStrategy to set.
	 */
	public void setOtherLearningStrategy(String otherLearningStrategy) {
	    this.otherLearningStrategy = otherLearningStrategy;
	}

	/**
	 * @hibernate.property column="is_cross_age"
	 * @return Returns the isCrossAge.
	 */
	public boolean getIsCrossAge() {
	    return isCrossAge;
	}

	/**
	 * @param isCrossAge The isCrossAge to set.
	 */
	public void setIsCrossAge(boolean isCrossAge) {
	    this.isCrossAge = isCrossAge;
	}

	/**
	 * @hibernate.property column="is_heterogeneous"
	 * @return Returns the isHeterogeneous.
	 */
	public boolean getIsHeterogeneous() {
	    return isHeterogeneous;
	}

	/**
	 * @param isHeterogeneous The isHeterogeneous to set.
	 */
	public void setIsHeterogeneous(boolean isHeterogeneous) {
	    this.isHeterogeneous = isHeterogeneous;
	}

	/**
	 * @hibernate.property column="is_homogeneous"
	 * @return Returns the isHomogeneous.
	 */
	public boolean getIsHomogeneous() {
	    return isHomogeneous;
	}

	/**
	 * @param isHomogeneous The isHomogeneous to set.
	 */
	public void setIsHomogeneous(boolean isHomogeneous) {
	    this.isHomogeneous = isHomogeneous;
	}

	/**
	 * @hibernate.property column="is_individualized"
	 * @return Returns the isIndividualized.
	 */
	public boolean getIsIndividualized() {
	    return isIndividualized;
	}

	/**
	 * @param isIndividualized The isIndividualized to set.
	 */
	public void setIsIndividualized(boolean isIndividualized) {
	    this.isIndividualized = isIndividualized;
	}

	/**
	 * @hibernate.property column="is_large_group"
	 * @return Returns the isLargeGroup.
	 */
	public boolean getIsLargeGroup() {
	    return isLargeGroup;
	}

	/**
	 * @param isLargeGroup The isLargeGroup to set.
	 */
	public void setIsLargeGroup(boolean isLargeGroup) {
	    this.isLargeGroup = isLargeGroup;
	}

	/**
	 * @hibernate.property column="is_small_group"
	 * @return Returns the isSmallGroup.
	 */
	public boolean getIsSmallGroup() {
	    return isSmallGroup;
	}

	/**
	 * @param isSmallGroup The isSmallGroup to set.
	 */
	public void setIsSmallGroup(boolean isSmallGroup) {
	    this.isSmallGroup = isSmallGroup;
	}

	/**
	 * @hibernate.property column="sequence_context"
	 * @return Returns the sequenceContext.
	 */
	public String getSequenceContext() {
	    return sequenceContext;
	}

	/**
	 * @param sequenceContext The sequenceContext to set.
	 */
	public void setSequenceContext(String sequenceContext) {
	    this.sequenceContext = sequenceContext;
	}

	/**
	 * @hibernate.property column="time_frame"
	 * @return Returns the timeFrame.
	 */
	public String getTimeFrame() {
	    return timeFrame;
	}

	/**
	 * @param timeFrame The timeFrame to set.
	 */
	public void setTimeFrame(String timeFrame) {
	    this.timeFrame = timeFrame;
	}

	/**
	 * @hibernate.many-to-one column="languages_id" cascade="all" unique="true"
	 * @return Returns the languages.
	 */
	public Languages getLanguages() {
	    return languages;
	}

	/**
	 * @param languages The languages to set.
	 */
	public void setLanguages(Languages languages) {
	    this.languages = languages;
	}

	/**
	 * @hibernate.property column="national_standards_subjects"
	 * @return Returns the nationalStandardsSubjects.
	 */
	public String getNationalStandardsSubjects() {
	    return nationalStandardsSubjects;
	}

	/**
	 * @param nationalStandardsSubjects The nationalStandardsSubjects to set.
	 */
	public void setNationalStandardsSubjects(String nationalStandardsSubjects) {
	    this.nationalStandardsSubjects = nationalStandardsSubjects;
	}

	/**
	 * @hibernate.property column="national_standards_value"
	 * @return Returns the nationalStandardsValue.
	 */
	public String getNationalStandardsValue() {
	    return nationalStandardsValue;
	}

	/**
	 * @param nationalStandardsValue The nationalStandardsValue to set.
	 */
	public void setNationalStandardsValue(String nationalStandardsValue) {
	    this.nationalStandardsValue = nationalStandardsValue;
	}

	/**
	 * @hibernate.property column="state_standards"
	 * @return Returns the stateStandards.
	 */
	public String getStateStandards() {
	    return stateStandards;
	}

	/**
	 * @param stateStandards The stateStandards to set.
	 */
	public void setStateStandards(String stateStandards) {
	    this.stateStandards = stateStandards;
	}

	/**
	 * @hibernate.property column="state_standards_subjects"
	 * @return Returns the stateStandardsSubjects.
	 */
	public String getStateStandardsSubjects() {
	    return stateStandardsSubjects;
	}

	/**
	 * @param stateStandardsSubjects The stateStandardsSubjects to set.
	 */
	public void setStateStandardsSubjects(String stateStandardsSubjects) {
	    this.stateStandardsSubjects = stateStandardsSubjects;
	}

	/**
	 * @hibernate.property column="state_standards_value"
	 * @return Returns the stateStandardsValue.
	 */
	public String getStateStandardsValue() {
	    return stateStandardsValue;
	}

	/**
	 * @param stateStandardsValue The stateStandardsValue to set.
	 */
	public void setStateStandardsValue(String stateStandardsValue) {
	    this.stateStandardsValue = stateStandardsValue;
	}

	/**
	 * @return Returns the nationalStandardsDocument.
	 */
	public Document getNationalStandardsDocument() {
	    return nationalStandardsDocument;
	}

	/**
	 * @param nationalStandardsDocument The nationalStandardsDocument to set.
	 */
	public void setNationalStandardsDocument(Document nationalStandardsDocument) {
	    this.nationalStandardsDocument = nationalStandardsDocument;
	    if (nationalStandardsDocument != null) {
	        setNationalStandardsId(Long.valueOf(nationalStandardsDocument.getId()));
	    } else {
	        setNationalStandardsId(null);
	    }
	}

	/**
	 * @return Returns the stateStandardsDocument.
	 */
	public Document getStateStandardsDocument() {
	    return stateStandardsDocument;
	}

	/**
	 * @param stateStandardsDocument The stateStandardsDocument to set.
	 */
	public void setStateStandardsDocument(Document stateStandardsDocument) {
	    this.stateStandardsDocument = stateStandardsDocument;
	    if (stateStandardsDocument != null) {
	        setStateStandardsId(Long.valueOf(stateStandardsDocument.getId()));
	    } else {
	        setStateStandardsId(null);
	    }
	}

	/**
	 * @hibernate.property column="national_standards_id"
	 * @return Returns the nationalStandardsId.
	 */
	public Long getNationalStandardsId() {
	    return nationalStandardsId;
	}

	/**
	 * @param nationalStandardsId The nationalStandardsId to set.
	 */
	public void setNationalStandardsId(Long nationalStandardsId) {
	    this.nationalStandardsId = nationalStandardsId;
	}

	/**
	 * @hibernate.property column="state_standards_id"
	 * @return Returns the stateStandardsId.
	 */
	public Long getStateStandardsId() {
	    return stateStandardsId;
	}

	/**
	 * @param stateStandardsId The stateStandardsId to set.
	 */
	public void setStateStandardsId(Long stateStandardsId) {
	    this.stateStandardsId = stateStandardsId;
	}

	public String getGradeLevelLabel() {
	    return getGradeLevelLabel(getGradeLevel());
	}

	/**
	 * @param isTechnology The isTechnology to set.
	 */
	public void setIsTechnology(boolean isTechnology) {
	    this.isTechnology = isTechnology;
	}

	public boolean getHasSupportMaterials() {
	    return getIsTeacherResourceInstance() && !getIsOther() && !getIsWebquest();
	}	
    public List getCurricularAreas() {
        List strings = new ArrayList();
        if (getIsArts()) {
            strings.add("Arts");
        }
        if (getIsHealth()) {
            strings.add("Health");
        }
        if (getIsLanguageArts()) {
            strings.add("Language Arts");
        }
        if (getIsMathematics()) {
            strings.add("Mathematics");
        }
        if (getIsSocialStudies()) {
            strings.add("Social Studies");
        }
        if (getIsTechnology()) {
            strings.add("Technology");
        }
        return strings;
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
	public String toString() {
		return "MappedAccessoryPage with title: " + getPageTitle() + " is treehouse? " + getIsTreehouse();
	}
}
