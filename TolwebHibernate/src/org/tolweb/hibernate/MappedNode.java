/*
 * Created on Jul 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.tolweb.btol.AdditionalFields;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.OtherName;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * @hibernate.class table="NODES"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class MappedNode extends Node implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8711127782177202781L;
	private Long id;
	private Set ancestors;
	private Long pageId, parentId;
	private Integer nameDate;
	private Integer orderOnParent;
	private Integer nodeRankInteger;
	private boolean isSubmitted;
    private Integer orderOnPage;
    private String namesNotes;
	
    // new names handling stuff
    private boolean showAuthorityInContainingGroup;
    private boolean italicizeName;
    private String nameComment;
    private boolean isNewCombination;
    private String combinationAuthor;
    private Integer combinationDate;
    
    // Cached in memory
	private String pageTitle, pageSupertitle, pageSubtitle, pageSupertitleNoAuthority, pageTitleNoCombo, pageAuthority, nodeAuthority;
    private boolean hasPage;
    private AdditionalFields additionalFields;
	
    private Long treeOrder;
    
    /* 'status' was added to give a way to indicate multi-state statuses for nodes, 
     * primarily it was added to indicate when a node was 'inactive', as all nodes 
     * are, by default, active (0 is active). An integer is used since other states 
     * may be added in the future */
    private Integer status;
    
    private ExtendedNodeProperties extendedProps;
    
    private Date updated;
    private Date created;
    /* last-inactivated - when the status of a node was last changed to 1
	 * last-activated - when the status of a node was last changed to 0
	 * last-retired - when the status of a node was last changed to 2 */
    private Date lastInactivated;
    private Date lastActivated;
    private Date lastRetired;
    
    public static final Integer ACTIVE = 0;
    public static final Integer INACTIVE = 1;
    public static final Integer RETIRED = 2;
    
    public MappedNode() {
    	super();
    	treeOrder = Long.valueOf(0);
    	status = ACTIVE;
    }
    
	/**
	 * Copies all the values we care about from one node to the other
	 * @param other
	 */
	public void copyValues(MappedNode other) {
		copyValues(other, false);
	}
	
	public void copyValues(MappedNode other, boolean mergeOtherNames) {
	    setPageId(other.getPageId());
	    setParentNodeId(other.getParentNodeId());
	    setAuthorityDate(other.getAuthorityDate());
	    setOrderOnParent(other.getOrderOnParent());
	    setNodeRankInteger(other.getNodeRankInteger());
	    setIsSubmitted(other.getIsSubmitted());
	    setExtinct(other.getExtinct());
	    setPhylesis(other.getPhylesis());
	    setConfidence(other.getConfidence());
	    setIsLeaf(other.getIsLeaf());
	    setName(other.getName());
	    setNameAuthority(other.getNameAuthority());
	    setShowPreferredAuthority(other.getShowPreferredAuthority());
	    setShowNameAuthority(other.getShowNameAuthority());
	    setShowImportantAuthority(other.getShowImportantAuthority());
	    if (other.getSynonyms() != null) {
	    	if (!mergeOtherNames) {
	    		setSynonyms(new TreeSet(other.getSynonyms()));
	    	} else {
	    		int sequence;
	    		if (this.getSynonyms() == null) {
	    			setSynonyms(new TreeSet());
	    			sequence = 0;
	    		} else {
	    			sequence = getSynonyms().size();
	    		}
	    		for (Iterator itr = other.getSynonyms().iterator(); itr.hasNext(); ) {
	    			MappedOtherName othername = (MappedOtherName)itr.next();
	    			othername.setOrder(sequence);
	    			boolean added = this.addSynonym(othername);
	    			if (added) {
	    				sequence++;
	    			}
	    		}
	    		int order = 0; // re-order the othernames
	    		for(Iterator itr = this.getSynonyms().iterator(); itr.hasNext(); order++) {
	    			((MappedOtherName)itr.next()).setOrder(order);
	    		}
	    	}
	    }
	    setDescription(other.getDescription());
	    setDontPublish(other.getDontPublish());
        setOrderOnPage(other.getOrderOnPage());
        setShowAuthorityInContainingGroup(other.getShowAuthorityInContainingGroup());
        setItalicizeName(other.getItalicizeName());
        setNameComment(other.getNameComment());
        setIsNewCombination(other.getIsNewCombination());
        setCombinationAuthor(other.getCombinationAuthor());
        setCombinationDate(other.getCombinationDate());
        setHasIncompleteSubgroups(other.getHasIncompleteSubgroups());
        setRankName(other.getRankName());
        setStatus(other.getStatus());		
	}
	
	public int getId() {
	    return id.intValue();
	}

	/**
	 * @hibernate.id generator-class="native" column="node_id" unsaved-value="null"
	 */
	public Long getNodeId() {
		return id;
	}
	public void setNodeId(Long value) {
		id = value;
	} 
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else  if (o == null) {
			return false;
		} else if (getClass().equals(o.getClass())) {
			MappedNode other = (MappedNode) o;
			if (getNodeId() != null && other.getNodeId() != null) {
				return getNodeId().equals(other.getNodeId());
			}else {
				return false;
			}
		}
		return false;
	}
    
	public int hashCode() {
		if (getNodeId() != null) {
			return getNodeId().hashCode();
		} else {
			return 0;
		}
	}
	
	public boolean addSynonym(MappedOtherName othername) {
		// TODO refactor... this is some crazy logic 
		if (getSynonyms() != null && getSynonyms().isEmpty()) {
			return getSynonyms().add(othername);
		} else {
			List currSynonyms = new ArrayList(getSynonyms());
			boolean shouldAdd = true;
			for (Iterator itr = currSynonyms.iterator(); itr.hasNext(); ) {
				MappedOtherName curr = (MappedOtherName) itr.next();
				if (curr.nameEquals(othername)) {
					// try to merge before making auth info check
					if (curr.hasIncompleteAuthorityInfo() && 
						!othername.hasNoAuthorityInfo()) {
						curr.mergeAuthorityInfo(othername);
						return true;
					}
					// two names, both w/o auth info are equal 
					// and new name has auth info shouldn't be added 
					if (!curr.authorityInfoEquals(othername) && 
						!othername.hasNoAuthorityInfo()) {
						getSynonyms().add(othername);
						return true;
					}
					shouldAdd = false;
				}
			}
			if (shouldAdd) {
				getSynonyms().add(othername);
				return true;
			}
		}
		return false; 
	}
	
	public void addChild(MappedNode newChild) {
		newChild.setParent(this);
		newChild.setParentNodeId(this.getNodeId());
		getChildren().add(newChild);
	}
	
	public void removeChild(MappedNode oldChild) {
		getChildren().remove(oldChild);
	}
	
    /**
     * @hibernate.set table="NODEANCESTORS" lazy="true"
     * @hibernate.collection-key column="node_id"
     * @hibernate.collection-many-to-many class="org.tolweb.hibernate.MappedNode" column="ancestor_id"
     */	
	public Set getAncestors() {
	    if (ancestors == null) {
	        ancestors = new HashSet();
	    }
	    return ancestors;
	}
	
	public void setAncestors(Set value) {
	    ancestors = value;
	}
	
	/**
	 * Returns the page-id which this node appears as a terminal taxa  
	 * (not to be confused with the page attached with this node).
	 * @hibernate.property column="page_id"
	 * @return page-id for the page which the node appears as a terminal taxa
	 */
	public Long getPageId() {
	    return pageId;
	}
	
	public void setPageId(Long value) {
	    pageId = value;
	}
	
	/**
	 * @hibernate.property column="parentnode_id"
	 * @return Returns the page this Node sits on
	 */
	public Long getParentNodeId() {
	    return parentId;
	}
	
	public void setParentNodeId(Long value) {
	    parentId = value;
	}
	
	/**
	 * @hibernate.property column="auth_year"
	 * @return
	 */
	public Integer getAuthorityDate() {
	    return nameDate;
	}
	
	public void setAuthorityDate(Integer value) {
	    nameDate = value;
	}
	
	/**
	 * @hibernate.property column="order_on_parent"
	 * @return
	 */
    public Integer getOrderOnParent() {
        return orderOnParent;
    }
    public void setOrderOnParent(Integer orderOnParent) {
        this.orderOnParent = orderOnParent;
    }

    /**
     * @hibernate.property column="node_Rank"
     * @return
     */    
    public Integer getNodeRankInteger() {
        return nodeRankInteger;
    }
    public void setNodeRankInteger(Integer nodeRankInteger) {
        this.nodeRankInteger = nodeRankInteger;
    }
    
    /**
     * @hibernate.property column="is_submitted"
     * @return
     */
    public boolean getIsSubmitted() {
        return isSubmitted;
    }
    public void setIsSubmitted(boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }	
 
	
	/**
	 * If there is a supertitle, returns it, otherwise returns the title.
	 * @param isContainingGroup TODO
	 * @param ignoreItalics TODO
	 * @param printAuthority Whether to print the authority information along with the 
	 * 		  title
	 */
	public String getActualPageTitle(boolean showAuthority, boolean isContainingGroup, boolean ignoreItalics) {
	    if (StringUtils.notEmpty(getPageSupertitle())) {
	        if (showAuthority) {
	            return pageSupertitle;
	        } else {
	            return getPageSupertitleNoAuthority();
	        }
	    } else {
	        if (showAuthority) {
                if (isContainingGroup) {
                    return getPageTitleNoCombo();
                } else {
                    return getPageTitle();
                }
	        } else {
                if (getItalicizeName() && !ignoreItalics) {
                    return "<em>" + getName() + "</em>";
                } else {
                    return getName();
                }
	        }
	    }
	}
	
	/**
	 * Goes through the node name and returns the title string
	 */
	public String getPageTitle() {
	    if (pageTitle == null) {
	        initPageNames();
	    }
	    return pageTitle;
	}
	
	public String getPageSupertitle() {
	    if (pageSupertitle == null) {
	        initPageNames();
	    }
	    return pageSupertitle;
	}
	
	public String getPageSubtitle() {
	    if (pageSubtitle == null) {
	        initPageNames();
	    }
	    return pageSubtitle;
	}
    /**
     * @return Returns the pageTitleNoCombo.
     */
    public String getPageTitleNoCombo() {
        if (pageTitleNoCombo == null) {
            initPageNames();
        }
        return pageTitleNoCombo;
    }	
    public String getPageAuthority() {
        if (pageAuthority == null) {
            initPageNames();
        }
        return pageAuthority;
    }       
	private String getPageSupertitleNoAuthority() {
	    return pageSupertitleNoAuthority;
	}
    
    public OtherName getFirstPreferredOtherName() {
        for (Iterator iter = getSynonyms().iterator(); iter.hasNext();) {
            MappedOtherName name = (MappedOtherName) iter.next();
            if (name.getIsPreferred()) {
                return name;
            }
        }
        return null;
    }
    
	private void initPageNames() {
	    // Check supertitle and subtitle
	    Iterator it = getSynonyms().iterator();
	    ArrayList subtitleNames, supertitleNames;
	    if (it.hasNext()) {
	        subtitleNames = new ArrayList();
	        supertitleNames = new ArrayList();
	        while (it.hasNext()) {
	            MappedOtherName name = (MappedOtherName) it.next();
	            String actualName = " ";
                actualName += getNameString(name.getName(), name.getAuthority(), name.getAuthorityYear(), 
                        getShowNameAuthority(), name.getItalicize(), false, null, null, name.getComment());                
	            if (name.isImportant()) {
	                subtitleNames.add(actualName);
	            } else if (name.isPreferred()) {
	                supertitleNames.add(actualName);
	                if (pageSupertitleNoAuthority == null) {
	                    pageSupertitleNoAuthority = name.getName();
	                }
                    pageAuthority = name.getAuthority();
                    if (name.getAuthorityYear() != null && name.getAuthorityYear() > 0) {
                        pageAuthority += " " + name.getAuthorityYear();
                    }
	            }
	        }
	        if (subtitleNames.size() > 0) {
	            pageSubtitle = StringUtils.returnCommaJoinedString(subtitleNames);
	        }
	        if (supertitleNames.size() > 0) {
	            pageSupertitle = StringUtils.returnCommaJoinedString(supertitleNames);
	        }
	    }
	    pageTitle = getNameString(getName(), getNameAuthority(), getAuthorityDate(), 
                getShowNameAuthority(), getItalicizeName(), getIsNewCombination(),
                getCombinationAuthor(), getCombinationDate(), getNameComment());
        if (pageAuthority == null) {
            pageAuthority = constructNodeAuthority();
        }
        nodeAuthority = constructNodeAuthority();
        pageTitleNoCombo = getNameString(getName(), getNameAuthority(), getAuthorityDate(), 
                getShowNameAuthority(), getItalicizeName(), getIsNewCombination(),
                null, null, getNameComment());
	}
    
    private String constructNodeAuthority() {
        String auth = getNameAuthority();
        if (getAuthorityDate() != null && getAuthorityDate() > 0) {
            auth += " " + getAuthorityDate();
        }
        if (getIsNewCombination()) {
            auth = "(" + auth + ")";
            if (StringUtils.notEmpty(getCombinationAuthor())) {
                auth += " " + getCombinationAuthor();
                if (getCombinationDate() != null && getCombinationDate() > 0) {
                    auth += " " + getCombinationDate();
                }
            }
        }
        return auth;
    }

	private String getNameString(String name, String authority, Integer date, 
            boolean showAuthority, boolean italicize, boolean isCombination,
            String combinationAuthor, Integer combinationDate, String comment) {
	    String returnString = name;
        if (italicize) {
            returnString = "<em>" + returnString + "</em>";
        }
	    if (StringUtils.notEmpty(authority) && showAuthority) {
	        if (date != null && date.intValue() > 0) {
	            authority += " " + date;
	        }
            if (isCombination) {
                authority = "(" + authority + ")";
                if (StringUtils.notEmpty(combinationAuthor)) {
                    authority += " " + combinationAuthor;
                }
                if (combinationDate != null) {
                    authority += " " + combinationDate;
                }
            }
            returnString += " <span class=\"authority\">" + authority + "</span>";
            
            if (StringUtils.notEmpty(comment)) {
                returnString += " <span class=\"comment\">" + comment + "</span>";
            }
	        
	    }   
	    return returnString;
	}	

	public String toString() {
	    return "MappedNode named: " + getName() + " with id: " + getNodeId();
	}
	
	public String compareValues(MappedNode node2) {
        if (getNodeId() != null && !getNodeId().equals(node2.getNodeId())) {
            return returnDifferenceString("nodeId", getNodeId(), node2.getNodeId());
        }
        if (getPageId() != null && !getPageId().equals(node2.getPageId())) {
            return returnDifferenceString("pageId", getPageId(), node2.getPageId());
        }
        if (getParentNodeId() != null && !getParentNodeId().equals(node2.getParentNodeId())) {
            return returnDifferenceString("parentNodeId", getParentNodeId(), node2.getParentNodeId());
        }
        if (getAuthorityDate() != null && !getAuthorityDate().equals(node2.getAuthorityDate())) {
            return returnDifferenceString("authoritydate", getAuthorityDate(), node2.getAuthorityDate());
        }
        if (getOrderOnParent() != null && !getOrderOnParent().equals(node2.getOrderOnParent())) {
            return returnDifferenceString("order on parent", getOrderOnParent(), node2.getOrderOnParent());
        }
        if (getNodeRankInteger() != null && !getNodeRankInteger().equals(node2.getNodeRankInteger())) {
            return returnDifferenceString("node rank", getNodeRankInteger(), node2.getNodeRankInteger());
        }
        if (getExtinct() != node2.getExtinct()) {
            return returnDifferenceString("extinct", Integer.valueOf(getExtinct()), Integer.valueOf(node2.getExtinct()));
        }
        if (getPhylesis() != node2.getPhylesis()) {
            return returnDifferenceString("phylesis", Integer.valueOf(getPhylesis()), Integer.valueOf(node2.getPhylesis()));
        }
        if (getConfidence() != node2.getConfidence()) {
            return returnDifferenceString("confidence of placement", Integer.valueOf(getConfidence()), Integer.valueOf(node2.getConfidence()));
        }
        if (!getIsLeaf() == node2.getIsLeaf()) {
            return returnDifferenceString("leaf", Boolean.valueOf(getIsLeaf()), Boolean.valueOf(node2.getIsLeaf()));
        }
        if (getName() != null && !getName().equals(node2.getName())) {
            return returnDifferenceString("name", getName(), node2.getName());
        }
        if (getNameAuthority() != null && !getNameAuthority().equals(node2.getNameAuthority())) {
            return returnDifferenceString("name authority", getNameAuthority(), node2.getNameAuthority());
        }
        if (getShowPreferredAuthority() != node2.getShowPreferredAuthority()) {
            return returnDifferenceString("show preferred authority", Boolean.valueOf(getShowPreferredAuthority()), Boolean.valueOf(node2.getShowPreferredAuthority()));
        }
        if (!getShowNameAuthority() == node2.getShowNameAuthority()) {
            return returnDifferenceString("show name authority", Boolean.valueOf(getShowNameAuthority()), Boolean.valueOf(node2.getShowNameAuthority()));
        }
        if (!getShowImportantAuthority() == node2.getShowImportantAuthority()) {
            return returnDifferenceString("show important authority", Boolean.valueOf(getShowImportantAuthority()), Boolean.valueOf(node2.getShowImportantAuthority()));
        }
        if (getDescription() != null && !getDescription().equals(node2.getDescription())) {
            return returnDifferenceString("description", getDescription(), node2.getDescription());
        }
        if (getSynonyms() != null || node2.getSynonyms() != null) {
	        Iterator it1 = getSynonyms().iterator(), it2 = node2.getSynonyms().iterator();
	        while (it1.hasNext()) {
	            MappedOtherName name1 = (MappedOtherName) it1.next();
	            MappedOtherName name2 = (MappedOtherName) it2.next();
	            if (name1.getAuthorityYear() != null && !name1.getAuthorityYear().equals(name2.getAuthorityYear())) {
	                return returnDifferenceString("othername authyear", name1.getAuthorityYear(), name2.getAuthorityYear());
	            }
	            if (name1.getOrder() != name2.getOrder()) {
	                return returnDifferenceString("othername order", name1.getOrder(), name2.getOrder());
	            }
	            if (name1.getName() != null && !name1.getName().equals(name2.getName())) {
	                return returnDifferenceString("othername name", name1.getName(), name2.getName());
	            }
	            if (name1.getAuthority() != null && !name1.getAuthority().equals(name2.getAuthority())) {
	                return returnDifferenceString("othername authority", name1.getAuthority(), name2.getAuthority());
	            }
	            if (name1.getIsImportant() == name2.getIsImportant()) {
	                return returnDifferenceString("othername isImportant", Boolean.valueOf(name1.getIsImportant()), Boolean.valueOf(name2.getIsImportant()));
	            }
	            if (name1.getIsPreferred() != name2.getIsPreferred()) {
	                return returnDifferenceString("othername isPreferred", Boolean.valueOf(name1.getIsPreferred()), Boolean.valueOf(name2.getIsPreferred()));
	            }	            
	        }
        }
        // no changes
        return null;
	}
	
	private String returnDifferenceString(String fieldName, Object workingValue, Object publicValue) {
	    return "Node " + getName() + " has different " + fieldName + ", working: " + workingValue + " public: " + publicValue;
	}

    /**
     * @hibernate.property column="order_on_page"
     * @return Returns the orderOnPage.
     */
    public Integer getOrderOnPage() {
        return orderOnPage;
    }

    /**
     * @param orderOnPage The orderOnPage to set.
     */
    public void setOrderOnPage(Integer orderOnPage) {
        this.orderOnPage = orderOnPage;
    }

    /**
     * @hibernate.property
     * @return Returns the namesNotes.
     */
    public String getNamesNotes() {
        return namesNotes;
    }

    /**
     * @param namesNotes The namesNotes to set.
     */
    public void setNamesNotes(String namesNotes) {
        this.namesNotes = namesNotes;
    }

    /**
     * @hibernate.property
     * @return Returns the combinationAuthor.
     */
    public String getCombinationAuthor() {
        return combinationAuthor;
    }

    /**
     * @param combinationAuthor The combinationAuthor to set.
     */
    public void setCombinationAuthor(String combinationAuthor) {
        this.combinationAuthor = combinationAuthor;
    }

    /**
     * @hibernate.property
     * @return Returns the combinationDate.
     */
    public Integer getCombinationDate() {
        return combinationDate;
    }

    /**
     * @param combinationDate The combinationDate to set.
     */
    public void setCombinationDate(Integer combinationDate) {
        this.combinationDate = combinationDate;
    }

    /**
     * @hibernate.property
     * @return Returns the isNewCombination.
     */
    public boolean getIsNewCombination() {
        return isNewCombination;
    }

    /**
     * @param isNewCombination The isNewCombination to set.
     */
    public void setIsNewCombination(boolean isNewCombination) {
        this.isNewCombination = isNewCombination;
    }

    /**
     * @hibernate.property
     * @return Returns the italicizeName.
     */
    public boolean getItalicizeName() {
        return italicizeName;
    }

    /**
     * @param italicizeName The italicizeName to set.
     */
    public void setItalicizeName(boolean italicizeName) {
        this.italicizeName = italicizeName;
    }

    /**
     * @hibernate.property
     * @return Returns the nameComment.
     */
    public String getNameComment() {
        return nameComment;
    }

    /**
     * @param nameComment The nameComment to set.
     */
    public void setNameComment(String nameComment) {
        this.nameComment = nameComment;
    }

    /**
     * @hibernate.property
     * @return Returns the showAuthorityInContainingGroup.
     */
    public boolean getShowAuthorityInContainingGroup() {
        return showAuthorityInContainingGroup;
    }

    /**
     * @param showAuthorityInContainingGroup The showAuthorityInContainingGroup to set.
     */
    public void setShowAuthorityInContainingGroup(
            boolean showAuthorityInContainingGroup) {
        this.showAuthorityInContainingGroup = showAuthorityInContainingGroup;
    }

    /**
     * @return Returns the nodeAuthority.
     */
    public String getNodeAuthority() {
        if (nodeAuthority == null) {
            initPageNames();
        }
        return nodeAuthority;
    }

    /**
     * @param nodeAuthority The nodeAuthority to set.
     */
    public void setNodeAuthority(String nodeAuthority) {
        this.nodeAuthority = nodeAuthority;
    }

    /**
     * DEVN:
     * Needs to be populated by calls to PageDAO method getNodeHasPage(), 
     * this is not a value that hibernate is automatically populating (don't 
     * get bit like 'lenards' has 10 times already).
     * 
     * If you're looking for a way to get node-ids that have pages attached 
     * look at getNodeIdsWithPages() in PageDAO
     * 
     * @return Returns the hasPage.
     */
    public boolean getHasPage() {
        return hasPage;
    }

    /**
     * @param hasPage The hasPage to set.
     */
    public void setHasPage(boolean hasPage) {
        this.hasPage = hasPage;
    }
    
    public boolean getHasSupertitle() {
    	return getFirstPreferredOtherName() != null;
    }
    
    public MappedOtherName getSupertitle() {
    	return (MappedOtherName) getFirstPreferredOtherName();
    }

	public AdditionalFields getAdditionalFields() {
		return additionalFields;
	}

	public void setAdditionalFields(AdditionalFields additionalFields) {
		this.additionalFields = additionalFields;
	}

    /**
     * @hibernate.property
     * @return Returns a numeric value representing the node's place in the overall tree.
     */	
	public Long getTreeOrder() {
		return treeOrder;
	}

	public void setTreeOrder(Long treeOrder) {
		this.treeOrder = treeOrder;
	}

	/**
	 * Returns the current status of the node.  This was added to indicate activity (active vs. inactive) 
	 * for nodes when the Taxa Import functionality was added to the application.
     * @hibernate.property
	 * @return Return an integer representing the status of the node (0=active, 1=inactive, 2=retired, etc.)
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * Changes the node's status, which is used to 
	 * filter the node from contexts it does not 
	 * apply. 
	 * 
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		if (INACTIVE.equals(status)) {
			this.lastInactivated = new Date();
		} else if (RETIRED.equals(status)) {
			this.lastRetired = new Date();
		} else if (ACTIVE.equals(status)) {
			this.lastActivated = new Date();
		}
		this.status = status;
	}

	/**
	 * @return the extendedProps
     * @hibernate.many-to-one column="extendedNodePropertiesId" class="org.tolweb.hibernate.ExtendedNodeProperties" cascade="all"
	 */
	public ExtendedNodeProperties getExtendedNodeProperties() {
		return extendedProps;
	}

	/**
	 * @param extendedProps the extendedProps to set
	 */
	public void setExtendedNodeProperties(ExtendedNodeProperties extendedProps) {
		this.extendedProps = extendedProps;
	}

	/**
	 * Returns the date and time information for when the node was updated. 
	 * It falls back on using the default date for 'created' if null. 
	 * @return the updated date for the node
     * @hibernate.property
	 */
	public Date getUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	/**
	 * Returns the date and time information for when the node was created. 
	 * The default is November 16, 1994 - the original launch date for the 
	 * Tree of Life Web Project. 
	 * @return the created date for the node
     * @hibernate.property
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * Gets the last inactivated date.  When a node is 
	 * inactivated, the date will be stored to allow for 
	 * searching and queries. 
	 * 
	 * @return the date representing the last update to inactive status. 
     * @hibernate.property
	 */	
	public Date getLastInactivated() {
		return lastInactivated;
	}

	public void setLastInactivated(Date lastInactivated) {
		this.lastInactivated = lastInactivated;
	}
	
	/**
	 * Gets the last activated date.  When a node has 
	 * been inactivate and then re-activated, the date  
	 * will be stored to allow for searching and queries.
	 * 
	 * @return the date representing the last update to active status. 
     * @hibernate.property
	 */
	public Date getLastActivated() {
		return lastActivated;
	}

	public void setLastActivated(Date lastActivated) {
		this.lastActivated = lastActivated;
	}

	/**
	 * Gets the last retired date.  When a node has 
	 * been retired, the date will be stored to allow 
	 * for searching and queries. 
	 *  
	 * @return the date representing the last update to retire status. 
     * @hibernate.property
	 */
	public Date getLastRetired() {
		return lastRetired;
	}

	public void setLastRetired(Date lastRetired) {
		this.lastRetired = lastRetired;
	}
}
