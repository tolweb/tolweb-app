package org.tolweb.treegrow.tree;
/*
 * Node.java
 *
 * Created on May 28, 2003, 11:13 AM
 */

import java.io.*;
import java.util.*;
import org.tolweb.treegrow.page.*;

/**
 * Node class stores information related to the content of each node, along with 
 * a little structural information (parent/children). Used by the Tree class 
 * to build the Tree. Sortable.
 *
 */
public class Node implements ChangedFromServerProvider, Serializable, Cloneable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 826061586321366528L;
	public static final int MONOPHYLETIC = 0;
    public static final int NOT_MONOPHYLETIC = 1;
    public static final int MONOPHYLY_UNCERTAIN = 2;
    public static final int INCERT_OFF = 0;
    public static final int INCERT_PUTATIVE = 1;
    public static final int INCERT_UNSPECIFIED = 2;
    public static final int NOT_LEAF = 0;
    public static final int LEAF = 1;
    public static final int NOT_EXTINCT = 0;
    public static final int EXTINCT = 2;
    
    /**
     * The prominent/non-trunk-prominent was added with the intent 
     * that the value would be used for tree-branch-coloring.  That 
     * will not happen on my watch (lenards), but that is the reason 
     * for having these different flags present. 
     * 
     * The trunk_node property was added by me, it probably should 
     * have been added to MappedNode - but there seemed to be a 
     * chance that TreeGrow would need to edit it - so I placed it 
     * here.  I don't have any real defense for that action. 
     */
    public static final int NOT_TRUNK_NODE = 0;
    public static final int TRUNK_NODE = 1;
    public static final int TRUNK_NODE_PROMINENT = 2;
    public static final int NON_TRUNK_NODE_PROMINENT = 3;
    
    /** 
     *Strings used as choices on the popup menu
     */
    private static ArrayList phylesisStrings;
    /** 
     *Strings used as choices on the popup menu
     */    
    private static ArrayList confidenceStrings;
    private static ArrayList priorityStrings;
    
    
    private int id = -1;
    
    /** 
     * Extinct? Endangered?
     */
    private int extinct;
    
    /**
     *Monophyletic? Nonmonophyletic?
     */
    private int phylesis;
    
    /** 
     * Confidence of placement 
     * (incertaesdeis?, incertaesedis, position unspecified?)
     */        
    private int confidence;
    private Boolean isLeaf;
    private String name;

    private ArrayList children;
    private Node parent;
    
    private boolean changed = false;
    private boolean nameChanged = false;
    private boolean extinctChanged = false;
    private boolean confidenceChanged = false;
    private boolean phylesisChanged = false;
    private boolean leafChanged = false;
    private boolean nodeRankChanged = false;
    private boolean extraIncertChanged = false;
    private boolean childCountChanged = false;
    private boolean childrenChanged = false;
    private boolean sequenceChanged = false;
    private boolean descriptionChanged = false;
    private boolean otherNamesChanged = false;
    private boolean pageObjectChanged = false;
    private boolean downloadComplete = false;
    private boolean pageClosed = false;
    private boolean hasPageOnServer = false;
    private boolean authorityChanged = false;
    private boolean dateChanged = false;
    private boolean showPreferredAuthorityChanged = false;
    private boolean showNameAuthorityChanged = false;
    private boolean showImportantAuthorityChanged = false;
    private boolean dontPublishChanged = false;
    private boolean pageWasRemoved = false;
    private boolean pageWasAdded = false;
    private boolean hasIncompleteSubgroups = false;
    private boolean incompleteSubgroupsChanged = false;
    private boolean priorityChanged = false;
    private boolean trunkNodeChanged = false;
    
    /**
     * used to determine if the tree branch color will be different, 
     * thus, indicating that the branch is a trunk-node branch and 
     * will not be modified.
     */
    private int trunkNode = NOT_TRUNK_NODE;
    
    /**
     * Stores the name of the node on the server -- used to make sure
     * that a web call to preview a page goes to the name that's on the server
     */
    private String nameOnServer = null; 
    /**
     *Lessinformative than it sounds. Only to be used to determine if a 
     * not-checkedout, terminal node has children
     */
    private int childCountOnServer = 0; 
    private boolean checkedOut = false;
    private boolean locked = false;
    /** 
     * "Submitted", "Locked By Other"
     */
    private String lockType = null; 
    private String lockDate = null;
    private String lockUser = null;
    
    private boolean dontPublish;

    private Vector otherNames = null; 
    private SortedSet synonyms;
    //private Set synonyms;
    private String nameAuthority = "";
    private int nameDate;
    private boolean showPreferredAuthority;
    private boolean showNameAuthority;
    private boolean showImportantAuthority;
    
    private int nodeRank = -1;

    private String description = null;
    private Page pageObject = null;
    
    private int priority;
    private String sourceDbNodeId;
	private Long sourceDbId;

    private String rankName;	
	
	public static final String INCERT_OFF_TEXT = "incertae sedis off";
	public static final String INCERT_PUTATIVE_TEXT = "incertae sedis in putative position";
	public static final String INCERT_UNSPECIFIED_TEXT = "incertae sedis position unspecified";
	
	public static final String MONOPHYLETIC_TEXT = "monophyletic";
    public static final String NOT_MONOPHYLETIC_TEXT = "not monophyletic";
    public static final String MONOPHYLY_UNCERTAIN_TEXT = "monophyly uncertain";	
	
    public static final String PRIORITY_LOW_TEXT = "low priority";
    public static final String PRIORITY_MEDIUM_TEXT = "medium priority";
    public static final String PRIORITY_HIGH_TEXT = "high priority";    
    
    static {
        phylesisStrings = new ArrayList();
        phylesisStrings.add(MONOPHYLETIC_TEXT);
        phylesisStrings.add(NOT_MONOPHYLETIC_TEXT);
        phylesisStrings.add(MONOPHYLY_UNCERTAIN_TEXT);
        confidenceStrings = new ArrayList();
        confidenceStrings.add(INCERT_OFF_TEXT);
        confidenceStrings.add(INCERT_PUTATIVE_TEXT);
        confidenceStrings.add(INCERT_UNSPECIFIED_TEXT);
        priorityStrings = new ArrayList();
        priorityStrings.add(PRIORITY_LOW_TEXT);
        priorityStrings.add(PRIORITY_MEDIUM_TEXT);
        priorityStrings.add(PRIORITY_HIGH_TEXT);
    }
        
    /** 
     * returns order in which this child appears on parent's list of children
     */   
    public int getSequence() {
        if (getParent() != null) {
            return getParent().indexOfChild(this);
        } else {
            return 0;
        }
    }

    
    /** Creates a new instance of Node, with no children by default
     *
     * @param ident     id number - should be unique
     *        name      the name/label of the node
     */
    public Node(int id, String name) {
        super();
        extinct = NOT_EXTINCT;
        this.id = id;
        this.name = name;
    }

    /** Creates a new instance of Node, with no children by default
     *
     * @param ident     id number - should be unique
     */    
    public Node(int id) {
        this (id,"");
    }
    
    public Node() {
        this (0,"");
    }

    public int getId () {
        return id;
    }
    public void setId (int x) {
        id = x;
    }        
    
    /**
     * @hibernate.property column="node_Extinct"
     */
    public int getExtinct () {
        return extinct;
    }
    
    public void setExtinct (int x) {
        extinct = x;
    }
    
    /**
     * @hibernate.property column="node_Phylesis"
     */
    public int getPhylesis () {
        return phylesis;
    }
    public void setPhylesis (int x) {
        phylesis = x;
    }
    public void setPhylesis(String s) {
        setPhylesis(phylesisStrings.indexOf(s));
    }
    
    /**
     * @hibernate.property column="node_Confidence"
     */
    public int getConfidence () {
        return confidence;
    }
    
    public boolean getExtraIncert()
    {
            return confidence==INCERT_UNSPECIFIED;
    }    
    
    public void setConfidence (int x) {
        confidence = x;
    }
    public void setConfidence (String s) {
        setConfidence(confidenceStrings.indexOf(s));
    }    
    
    /**
     * Returns a boolean indicating if the node is a leaf. 
     * 
     * DEVN: There is a Java Binary compatibility issue here. 
     * During debugging for Taxa Import bugfix (circa Aug '08), 
     * it was determined that Hibernate exceptions were being 
     * thrown and swallowed regarding trying to set the node_Leaf 
     * "property" to null (since it was modeled as a primitive 
     * boolean, it cannot be null).  So the instance variable was 
     * changed to Boolean, but there was issues with the JVM not 
     * finding the "getIsLeaf()Z" method, which is to say, a 
     * method called getIsLeaf that takes no arguments and returns 
     * a primitive boolean.  I didn't have time to figure out 
     * the true issue.  But that was the motivation for changing 
     * the return type back to boolean and adding the overloaded 
     * setter method. 
     * 
     * If you're running into this problem because of a change, 
     * you'll be seeing error/exception messages like: 
     * 
     *  java.lang.NoSuchMethodError: Node.getIsLeaf()Z
     *  
     *  or
     *  
     *  java.lang.NoSuchMethodError: MappedNode.getIsLeaf()Z
     * 
     * @hibernate.property column="node_Leaf"
     */
    public boolean getIsLeaf() {
        return (isLeaf == null) ? false : isLeaf.booleanValue();
    }

    /**
     * See Developer Note on getIsLeaf() for further explanation 
     * regarding the overloaded setters. 
     */    
    public void setIsLeaf(Boolean leaf) {
    	if (leaf == null) {
    		setIsLeaf(false);
    	} else {
    		isLeaf = leaf;
    	}
    }
    
    /**
     * See Developer Note on getIsLeaf() for further explanation 
     * regarding the overloaded setters. 
     */
    public void setIsLeaf(boolean leaf) {
        isLeaf = Boolean.valueOf(leaf);
    }    
    
    /**
     * @hibernate.property column="node_Name"
     */
    public String getName () {
        return name;
    }
    
    public void setName (String x) {
        name = x;
        if (name != null && nameOnServer == null) {
            nameOnServer = name;
        }
    }
    
    public String getNameOnServer() {
        return nameOnServer;
    }    
    
    public void setNameOnServer(String x) {
        nameOnServer = x;
    }    
    
    public void setNameAuthority(String value) {
        nameAuthority = value;
    }
    
    /**
     * @hibernate.property column="authority"
     */
    public String getNameAuthority() {
        return nameAuthority;
    }
    
    public void setNameAuthorityChanged(boolean value) {
        authorityChanged = value;
    }
    
    public boolean nameAuthorityChanged() {
        return authorityChanged;
    }
    
    public void setNameDate(int value) {
        nameDate = value;
    }

    public int getNameDate() {
        return nameDate;
    }
    
    public void setNameDateChanged(boolean value) {
        dateChanged = value;
    }
    
    public boolean nameDateChanged() {
        return dateChanged;
    }
    
    public void setShowPreferredAuthority(boolean value) {
        showPreferredAuthority = value;
    }
    
    public boolean showPreferredAuthority() {
        return showPreferredAuthority;
    }
    
    /**
     * @hibernate.property column="show_supertitle_authority"
     */
    public boolean getShowPreferredAuthority() {
        return showPreferredAuthority();
    }
    
    public void setShowPreferredAuthorityChanged(boolean value) {
        showPreferredAuthorityChanged = value;
    }
    
    public boolean showPreferredAuthorityChanged() {
        return showPreferredAuthorityChanged;
    }

    public void setShowNameAuthority(boolean value) {
        showNameAuthority = value;
    }
    
    public boolean showNameAuthority() {
        return showNameAuthority;
    }
    
    /**
     * @hibernate.property column="show_name_authority"
     */
    public boolean getShowNameAuthority() {
        return showNameAuthority();
    }
    
    public void setShowNameAuthorityChanged(boolean value) {
        showNameAuthorityChanged = value;
    }
    
    public boolean showNameAuthorityChanged() {
        return showNameAuthorityChanged;
    }
    
    public void setShowImportantAuthority(boolean value) {
        showImportantAuthority = value;
    }
    
    public boolean showImportantAuthority() {
        return showImportantAuthority;
    }
    
    /**
     * @hibernate.property column="show_important_authority"
     */
    public boolean getShowImportantAuthority() {
        return showImportantAuthority();
    }
    
    public void setShowImportantAuthorityChanged(boolean value) {
        showImportantAuthorityChanged = value;
    }
    
    public boolean showImportantAuthorityChanged() {
        return showImportantAuthorityChanged;
    }    
    
    public boolean hasPageOnServer() {
        return hasPageOnServer;
    }
    
    public boolean hasPage() {
        return hasPageOnServer();
    }

    public Page getPageObject() {
            return pageObject;
    }

    public void setPageObject(Page pageObj)  {
            pageObject = pageObj;
    }
    
    public String getLockDate() {
            return lockDate;
    }

    public void setLockDate(String date){
            lockDate = date;
    }

    public String getLockUser(){
            return lockUser;
    }

    public void setLockUser(String user)  {
            lockUser = user;
    }

    public String getLockType(){
            return lockType;
    }

    public void setLockType(String type) {
            lockType = type;
    }

    public boolean getLocked() {
            return locked;
    }

    public void setLocked(boolean locked) {
            this.locked = locked;
    }

    public int getChildCountOnServer() {
            return childCountOnServer;
    }
    
    public void setChildCountOnServer(int value) {
        childCountOnServer = value;
    }
    
    public boolean getTermWithSubTree() {
        return childCountOnServer !=0 && getChildren().size() == 0;
    }
    
    public boolean getCheckedOut() {
        return (checkedOut && childCountOnServer == 0) || !(getChildren().size() == 0 && hasPageOnServer && pageObject == null) || getPageWasAdded();
    }
    
    public void setCheckedOut(boolean value) {
        checkedOut = value;
    }

    public boolean getHasPageOnServer() {
            return hasPageOnServer;
    }

    public void setHasPageOnServer(boolean value) {
        hasPageOnServer = value;
    }        
    
    /**
     * Gets the synonyms (realized as MappedOtherNames) associated with a node. 
     * 
     * This relation means that MappedOtherNames cannot be shared outside of the Node.  An attempt was made 
     * to change this to a full parent/child one-to-many relation with extremely poor consequences in March 
     * 2008.  It is the suggestion of this author (lenards) that you leave this relation alone and find 
     * other avenues to fetch data without modifying the overall association between objects. 
     * 
     * @hibernate.set role="synonyms" table="OTHERNAMES" order-by="name_order asc" sort="natural" cascade="all"
     * @hibernate.collection-key column="node_id"
     * @hibernate.collection-composite-element class="org.tolweb.hibernate.MappedOtherName" column="node_id"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     */  
    public SortedSet getSynonyms() {
        return synonyms;
    }
    
    public void setSynonyms(SortedSet value) {
        synonyms = value;
    }

    public Vector getOtherNames() {
    	if (otherNames == null) {
    		otherNames = new Vector();
    	}
    	return otherNames;
    }

    public void setOtherNames(Vector syns) {
            otherNames = syns;
    }
    
    /**
     * Returns the first OtherName that is preferred (used in WriteAsList
     * previews on Pages)
     *
     * @return The first OtherName that is preferred if one exists, null if it 
     *         doesnt
     */
    public OtherName getFirstPreferredOtherName() {
        Iterator it = getOtherNames().iterator();
        while (it.hasNext()) {
            OtherName other = (OtherName) it.next();
            if (other.isPreferred()) {
                return other;
            }
        }
        return null;
    }

    public int getNodeRank() {
            return nodeRank;
    }

    public void setNodeRank(int value) {
            nodeRank = value;
    }

    /**
     * @hibernate.property column="node_Note"
     */
    public String getDescription() {
            return description;
    }

    public void setDescription(String nt) {
            description = nt;
    }

    public void setDownloadComplete(boolean value) {
            downloadComplete = value;
    }
    public boolean getDownloadComplete()  {
            return downloadComplete;
    }

    /** 
     *returns true if any of the "changed" values are true
     */
    public boolean getChanged() {
            return getOtherNamesChanged() || 
                    getDescriptionChanged() ||
                    getExtinctChanged() ||
                    getNameChanged() ||
                    getConfidenceChanged() ||
                    getPhylesisChanged() ||
                    getSequenceChanged() ||
                    getChildrenChanged() ||
                    getChildCountChanged() ||
                    getLeafChanged()  ||
                    getExtraIncertChanged()  ||
                    getNodeRankChanged() ||
                    getPageChanged()  || 
                    getNodeRankChanged() ||
                    getDontPublishChanged() ||
                    showPreferredAuthorityChanged() ||
                    showNameAuthorityChanged() ||
                    showImportantAuthorityChanged() ||
                    nameAuthorityChanged() ||
                    nameDateChanged() ||
                    getIncompleteSubgroupsChanged() ||
                    getPriorityChanged() ||
                    getTrunkNodeChanged();
    }

    public void setChildrenChanged(boolean value)
    {
            childrenChanged = value;
    }
    public boolean getChildrenChanged()
    {
            return childrenChanged ;
    }        

    public void setNameChanged(boolean value)
    {
            nameChanged = value;
    }
    public boolean getNameChanged()
    {
            return nameChanged;
    }

    public void setExtinctChanged(boolean value)
    {
            extinctChanged = value;
    }
    public boolean getExtinctChanged()
    {
            return extinctChanged;
    }

    public void setConfidenceChanged(boolean value)
    {
            confidenceChanged = value;
    }
    public boolean getConfidenceChanged()
    {
            return confidenceChanged;
    }
    
    public void setPhylesisChanged(boolean value)
    {
            phylesisChanged = value;
    }
    public boolean getPhylesisChanged()
    {
            return phylesisChanged;
    }

    public void setLeafChanged(boolean value)
    {
            leafChanged = value;
    }
    public boolean getLeafChanged()
    {
            return leafChanged;
    }

    public void setNodeRankChanged(boolean value)
    {
            nodeRankChanged = value;
    }
    public boolean getNodeRankChanged()
    {
            return nodeRankChanged;
    }

    public void setExtraIncertChanged(boolean value)
    {
            extraIncertChanged = value;
    }
    public boolean getExtraIncertChanged()
    {
            return extraIncertChanged;
    }

    public void setChildCountChanged(boolean value)
    {
            childCountChanged = value;
    }
    public boolean getChildCountChanged()
    {
            return getChildrenChanged();
    }

    public void setSequenceChanged(boolean value)
    {
            sequenceChanged = value;
    }
    public boolean getSequenceChanged()
    {
            return sequenceChanged;
    }

    public void setDescriptionChanged(boolean value)
    {
            descriptionChanged = value;
    }
    public boolean getDescriptionChanged()
    {
            return descriptionChanged;
    }

    public void setOtherNamesChanged(boolean value) {
        otherNamesChanged = value;
    }

    public boolean getOtherNamesChanged() {
    	Iterator it = getOtherNames().iterator();
    	while (it.hasNext()) {
    		OtherName other = (OtherName) it.next();
    		if (other.changedFromServer()) {
    			return true;	
    		}
    	}
        return otherNamesChanged;
    }

    public void setPageChanged(boolean value)
    {
            pageObjectChanged = value;
    }
    public boolean getPageChanged()
    {
            return pageObjectChanged;
    }
    
    public void setPageClosed(boolean value)
    {
            pageClosed = value;
    }
    public boolean getPageClosed()
    {
            return pageClosed;
    }

    /** 
     * Sets all the xyzChanged props to false
     */
    public void setChangedToFalse() {
        setAllNodePropertiesChanged(false);
    }

    /** 
     * Sets all the xyzChanged props to value
     */    
    private void setAllNodePropertiesChanged(boolean value) {
        setNameChanged(value);
        setExtinctChanged(value);
        setConfidenceChanged(value);
        setPhylesisChanged(value);
        setLeafChanged(value);
        setNodeRankChanged(value);
        setExtraIncertChanged(value);
        setChildCountChanged(value);
        setChildrenChanged(value);
        setSequenceChanged(value);
        setDescriptionChanged(value);
        setOtherNamesChanged(value);
        setPageChanged(value);
        setShowImportantAuthorityChanged(value);
        setShowNameAuthorityChanged(value);
        setShowPreferredAuthorityChanged(value);
        setNameAuthorityChanged(value);
        setNameDateChanged(value);
        setDontPublishChanged(value);
        setPageWasAdded(false);
        setPageWasRemoved(false);
        setIncompleteSubgroupsChanged(value);
        setPriorityChanged(value);
        setTrunkNodeChanged(value);
        Iterator it = getOtherNames().iterator();
        while (it.hasNext()) {
            OtherName name = (OtherName) it.next();
            name.setChangedFromServer(false);
        }
    }
    
    /**
     * Used to initialize all of a given node's values to true.  Used when a new node
     * is created in the tree editor in order to make sure the db will get all the new
     * values.
     */
    public void setChangedToTrue() {
        setAllNodePropertiesChanged(true);
    }
    
    public int getNumNonISPUChildren() {
        int count = 0;
        Iterator it = getChildren().iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (node.getConfidence() != INCERT_UNSPECIFIED) {
                count++;
            }
        }
        return count;
    }
    
    public void clearChildren() {
        getChildren().clear();
        childCountOnServer = 0;
    }
    
    public ArrayList getChildren() {
    	if (children == null) {
    		children = new ArrayList();
    	}
        return children;
    }
       
    public Node getChildAtIndex(int index) {
        return (Node) getChildren().get(index);
    }
    
    public void addToChildren(Node value) {
        childCountOnServer++;
        getChildren().add(value);
        value.setParent(this);
    }
    
    public void addToChildren(int index, Node value) {
        childCountOnServer++;
        getChildren().add(index, value);
        value.setParent(this);
    }
    
    public void removeChild(Node value) {
        childCountOnServer--;
        getChildren().remove(value);
    }
    
    public void removeChildren() {
        childCountOnServer = 0;
        getChildren().clear();
    }
    
/*    public void moveChildToEnd(Node value) {
        children.remove(value);
        children.add(value);
    }
 
    
    public void moveChildToBeforeUnspecified(Node value) {
        children.remove(value);
        int lastUnspecified, i;
        for (i = children.size() - 1; i >= 0; i--) {
            Node n = (Node) children.get(i);
            if (n.getConfidence() != INCERT_UNSPECIFIED) {
                break;
            }
        }
        children.add(i + 1, value);
    }
 */
    
    
    /**
     * Replaces the current child at index with the passed-in node
     * 
     * @param value The Node that will replace the current Node
     * @param index The index of the child to replace
     */
    public void replaceChildAtIndex(int index, Node value) {
        getChildren().set(index, value);
        value.setParent(this);
    }
    
    /**
     * Returns the index of the passed-in node in the children array.  Returns
     * -1 if not found
     *
     * @param value The Node to search for
     * @return The index of that Node in the children array
     */
    public int indexOfChild(Node value) {
        return getChildren().indexOf(value);
    }
    
    public void setChildren(ArrayList value) {
        children = value;
        childCountOnServer = getChildren().size();
    }
    
    public Node getParent() {
        return parent;
    }
    
    public void setParent(Node value) {
        parent = value;
    }
        
    /**
     * Checks to see if this child node needs to be reordered according to its
     * confidence placement.
     *
     * @param value The child node to check.
     *
    public void reorderIfNecessary(Node value) {
        if (parent != null) {
            int nodeIndex = indexOfChild(value);
            Node prevChild = nodeIndex != 0 ? (Node) children.get(nodeIndex - 1) : null;
            Node nextChild = nodeIndex != children.size() - 1 ? (Node) children.get(nodeIndex + 1) : null;
            if (value.getConfidence() == INCERT_UNSPECIFIED && nextChild != null && nextChild.getConfidence() != INCERT_UNSPECIFIED) {
                moveChildToEnd(value);
            } else if (value.getConfidence() != INCERT_UNSPECIFIED && prevChild != null && prevChild.getConfidence() == INCERT_UNSPECIFIED) {
                moveChildToBeforeUnspecified(value);
            }
        }    
    }*/
       
    /**
     * Test if this node has the passed-in node as an ancestor.
     * Walk down tree to the root (parent, then grandparent, etc). If the 
     * node passed as an argument is reached, then this node is a 
     * descendant of that one.
     *
     * @param n    The possible ancestor of this node.
     *
     * @return     <code>true</code> if argument is 
     *             an ancestor of this node;
     *             <code>false</code> otherwise.
     */
    public boolean isDescendantOf(Node n){
        Node node = getParent();
        while (node != null) {
            if (node == n) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }
        
    /**
     * Returns a single node that is a descendent, and has a page.
     * Used to limit actions that would cause descendents to be harmed.
     */
    public Node firstDescendantWithPage() {
        Stack stack = new Stack();
        stack.push(this);
        while( !stack.isEmpty() ) {
            Node node = (Node)stack.pop();
            if (node.hasPage() || node.hasPageOnServer()){
                return node;
            }
            Iterator it = node.getChildren().iterator();
            while (it.hasNext() ) {
                stack.push(it.next());
            }
        }
        return null;
    }
    
    /**
     * Returns all of the nodes in this node's subtree
     *
     * @return An ArrayList of all children and their descendants
     */
    public ArrayList getSubtreeNodes() {
        ArrayList returnList = new ArrayList();
        Stack stack = new Stack();
        stack.push(this);
        while (!stack.isEmpty()) {
            Node node = (Node) stack.pop();

            Iterator it = node.getChildren().iterator();
            while (it.hasNext()) {
                Node child = (Node) it.next();
                stack.push(child);
                returnList.add(child);
            }
        }
        return returnList;
    }
    
    public static ArrayList getPhylesisStrings() {
        return phylesisStrings;
    }

    public static ArrayList getConfidenceStrings() {
        return confidenceStrings;
    }    
    
    public static ArrayList getPriorityStrings() {
    	return priorityStrings;
    }
    

    /**
     * This is here for compatibility purposes with some of the undoable
     * edits.  It shouldn't really be taken to mean anything.
     */
    public boolean changedFromServer() {
        return true;
    }
    
    /**
     * This is here for compatibility purposes with some of the undoable
     * edits.  It shouldn't really be taken to mean anything.
     */
    public void setChangedFromServer(boolean value) {
        changed = value;
    }
    
    public Object clone() {
    	try {
    		return super.clone();
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    /**
     * @hibernate.property column="dont_publish"
     * @return Returns the dontPublish.
     */
    public boolean getDontPublish() {
        return dontPublish;
    }
    /**
     * @param dontPublish The dontPublish to set.
     */
    public void setDontPublish(boolean dontPublish) {
        this.dontPublish = dontPublish;
    }
    
    public String toString() {
        return "Node named: " + getName() + " with id: " + getId();
    }
    /**
     * @return Returns the dontPublishChanged.
     */
    public boolean getDontPublishChanged() {
        return dontPublishChanged;
    }
    /**
     * @param dontPublishChanged The dontPublishChanged to set.
     */
    public void setDontPublishChanged(boolean dontPublishChanged) {
        this.dontPublishChanged = dontPublishChanged;
    }
    /**
     * @return Returns the pageWasRemoved.
     */
    public boolean getPageWasRemoved() {
        return pageWasRemoved;
    }
    /**
     * @param pageWasRemoved The pageWasRemoved to set.
     */
    public void setPageWasRemoved(boolean pageWasRemoved) {
        this.pageWasRemoved = pageWasRemoved;
    }
    /**
     * @return Returns the pageWasAdded.
     */
    public boolean getPageWasAdded() {
        return pageWasAdded;
    }
    /**
     * @param pageWasAdded The pageWasAdded to set.
     */
    public void setPageWasAdded(boolean pageWasAdded) {
        this.pageWasAdded = pageWasAdded;
    }
    /**
     * @hibernate.property
     * @return Returns the hasChildrenNotDisplayed.
     */
    public boolean getHasIncompleteSubgroups() {
        return hasIncompleteSubgroups;
    }
    /**
     * @param hasChildrenNotDisplayed The hasChildrenNotDisplayed to set.
     */
    public void setHasIncompleteSubgroups(boolean hasChildrenNotDisplayed) {
        this.hasIncompleteSubgroups = hasChildrenNotDisplayed;
    }


    /**
     * @return Returns the childrenNotDisplayedChanged.
     */
    public boolean getIncompleteSubgroupsChanged() {
        return incompleteSubgroupsChanged;
    }


    /**
     * @param childrenNotDisplayedChanged The childrenNotDisplayedChanged to set.
     */
    public void setIncompleteSubgroupsChanged(boolean childrenNotDisplayedChanged) {
        this.incompleteSubgroupsChanged = childrenNotDisplayedChanged;
    }
	/**
	 * @return Returns the priority.
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority The priority to set.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	/**
	 * @return Returns the priorityChanged.
	 */
	public boolean getPriorityChanged() {
		return priorityChanged;
	}
	/**
	 * @param priorityChanged The priorityChanged to set.
	 */
	public void setPriorityChanged(boolean priorityChanged) {
		this.priorityChanged = priorityChanged;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getSourceDbNodeId() {
		return sourceDbNodeId;
	}
	public void setSourceDbNodeId(String foreignDbNodeId) {
		this.sourceDbNodeId = foreignDbNodeId;
	}


	/**
	 * @hibernate.property
	 * Returns the id of the database this node was imported from
	 * @return Returns the sourceDbId.
	 */
	public Long getSourceDbId() {
	    return sourceDbId;
	}


	/**
	 * @param sourceDbId The sourceDbId to set.
	 */
	public void setSourceDbId(Long databaseId) {
	    this.sourceDbId = databaseId;
	}

	/**
	 * @hibernate.property column="isTrunkNode"
	 * @return
	 */
	public int getTrunkNode() {
		return trunkNode;
	}
	public void setTrunkNode(int trunkNode) {
		this.trunkNode = trunkNode;
	}


	public boolean getTrunkNodeChanged() {
		return trunkNodeChanged;
	}
	public void setTrunkNodeChanged(boolean trunkNodeChanged) {
		this.trunkNodeChanged = trunkNodeChanged;
	}

	/**
	 * @hibernate.property
	 * @return the rankName
	 */
	public String getRankName() {
		return rankName;
	}

	/**
	 * @param rankName the rankName to set
	 */
	public void setRankName(String rankName) {
		this.rankName = rankName;
	}	
}
