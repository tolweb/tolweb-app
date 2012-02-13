package org.tolweb.treegrow.page;

import java.io.*;
import java.util.*;

import org.tolweb.treegrow.main.*;


/**
 * AccessoryPage holds the data for an accessory page
 */
public class AccessoryPage extends OrderedObject implements Comparable, Serializable, AuxiliaryChangedFromServerProvider {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3367444982771396618L;
	public static final byte RESTRICTED_USE = 0;
	public static final byte TOL_USE = 1;
	public static final byte EVERYWHERE_USE = 2;
    private static ArrayList legalStatuses ;
    private SortedSet internetLinks;    
	private int contributorId;
    private boolean isArticle;
    /**
     * Used for permission awareness on the client-side (cached from the server-provided info)
     */
    private boolean isEditable;
    
    /**
    * Holds the list of authors and thier details
    */
    private Vector contributorList = null;        
    
    /**
    * Holds the menu for this accessory page
    */
    private String menu = null;

    /**
    * Holds the menu for this accessory page as it exists on the server
    * (will differ from "menu" if menu has changed or if this acc_page
    * is a new one). Updated after each upload
    */
    private String menuOnServer = null;        

    /**
    * Holds the page title for this accessory page
    */
    private String pageTitle = null;

    /**
    * Holds the copyright information for this accessory page
    */
    private String copyright = null;

    /**
    * Holds the author information for this accessory page
    */
    private String authors = null;

    /**
    * Holds the text information for this accessory page
    */
    private String text = null;

    /**
    * Holds the references for this accessory page
    */
    private String references = null;

    /**
    * Holds the page status of the accessory page
    */
    private String status = null;        

    /**
     * Determines whether to use the linkedAccessoryPageID or the the 
     * data entered for the given accessory page.
     */
    private boolean useContent;

    /**
     * Whether the accessory page actually shows up on the menu
     */
    private boolean showOnMenu;

    /**
    * Holds the order in which the menu of this accessory page is displayed
    */
    private int order ;

    /**
    * Holds information as to whether the details of this accessory page has been changed or not.
    * true  - change has been made.
    * false - no change has been made.
    */
    private boolean changed = false;

    /**
     * Additional boolean to check whether the actual text of the accessory
     * page has changed.  This is needed in order to determine whether we
     * send up the text to the server (because it could possibly be a lot
     * of text and could seriously affect upload times.
     */
    private boolean textChanged;

    /**
     * Additional boolean to check whether the references of the accessory
     * page has changed.  Same idea as textChanged
     */
    private boolean referencesChanged;            

    private Page page;
    
    private String copyrightYear;
    private String acknowledgements;
    private String notes;

    /**
    * The id for an accessory page owned by some other page. This is used
    * when one accessory page is linked to by multiple branch pages. The
    * root-most page gets ownership of the accessory page, and others point
    * down the tree to that page's acc_page
    */        
    private AccessoryPage linkedAccessoryPage;

    /**
    * Same as above but in this case it isn't an actual accessory page 
    * object since it is pointing to something that wasn't downloaded in
    * current tree.
    */
    private Ancestor.AncestorAccPage linkedAncestorAccessoryPage;
	
	private byte usePermission;
	private int id = -1;
	
	private boolean isSubmitted;
	private boolean isTreehouse;

    static {
        legalStatuses = new ArrayList( Arrays.asList( new String[] {
                                                XMLConstants.SKELETAL, 
                                                XMLConstants.UNDER_CONSTRUCTION, 
                                                XMLConstants.COMPLETE, 
                                                XMLConstants.PEER_REVIEWED})
                                      );
    }
    
    public AccessoryPage() {
    }
    
    /**
     * Sets the values of this object to be equal to the AccessoryPage passed-in 
     * @param other The other accessory page to copy the values from
     */
    public void copyValues(AccessoryPage other) {
    	setNotes(other.getNotes());
    	setAcknowledgements(other.getAcknowledgements());
		setCopyright(other.getCopyright());
		setIsSubmitted(other.getIsSubmitted());
		setIsTreehouse(other.getIsTreehouse());
		setUsePermission(other.getUsePermission());
		setPageTitle(other.getPageTitle());
		setMenu(other.getMenu());
		setReferences(other.getReferences());
		setStatus(other.getStatus());
		setContributorId(other.getContributorId());
		setCopyrightYear(other.getCopyrightYear());
		setText(other.getText());
		SortedSet links = new TreeSet();
		Iterator it = other.getInternetLinks().iterator();
		while (it.hasNext()) {
			InternetLink link = (InternetLink) it.next();
			links.add(link.clone());
		}
		setInternetLinks(links);
    }

    /**
    * empty constructor.
    */

    public AccessoryPage(Page p) {
        contributorList = new Vector();
        page = p;
        menu = "<< New Accessory Page >>";
        status = XMLConstants.SKELETAL;
        useContent = true;
    }

    public Page getPage() {
        return page;
    }
    
    public void setIsSubmitted(boolean value) {
    	isSubmitted = value;
    }

	/**
	 *  @hibernate.property column="is_submitted"
	 * @return
	 */    
    public boolean getIsSubmitted() {
    	return isSubmitted;
    }
    
    public void setIsTreehouse(boolean value) {
    	isTreehouse = value;
    }
    
    /**
     *  @hibernate.property column="is_treehouse"
     * @return
     */
    public boolean getIsTreehouse() {
    	return isTreehouse;
    }

    /**
    * gets the menu heading of this accessory page
    * 
    * @hibernate.property
    *
    * @return	menu heading of this accessory page
    */
    public String getMenu(){
        if (useContent()) {
            return menu;
        } else {
            if (linkedAccessoryPage != null) {
                return linkedAccessoryPage.getMenu();
            } else if (linkedAncestorAccessoryPage != null) {
                return linkedAncestorAccessoryPage.getMenu();
            } else {
                return menu;
            }
        }
    }



    /**
    * sets the menu heading of this accessory page
    *
    * @param value	menu heading for this accessory page
    */
    public void setMenu(String value){
        menu = value;
    }

    /**
    * gets the menu heading of this accessory page, as it exists on the 
    * server
    *
    * @return	server's menu heading of this accessory page
    */
    public String getMenuOnServer(){
        return menuOnServer;
    }        

    /**
    * sets the menu heading of this accessory page, as it exists on the 
    * server
    *
    * @param	server's menu heading for this accessory page
    */
    public void setMenuOnServer(String value){
        menuOnServer = value;
    }        

    /**
    * gets the page title of this accessory page
    * @hibernate.property column="page_title"
    * @return	page title of this accessory page
    */
    public String getPageTitle(){
            return pageTitle;
    }

    /**
    * sets the page title of this accessory page
    *
    * @param value	page title for this accessory page
    * @return
    */
    public void setPageTitle(String value) {
            pageTitle = value;

    }

    /**
    * gets the copyright information of this accessory page
    * @hibernate.property column="page_copyrightholder"
    * @param
    * @return	copyright information of this accessory page
    */
    public String getCopyright(){
            return copyright;
    }

    /**
    * sets the copyright information of this accessory page
    *
    * @param value	copyright information for this accessory page
    * @return
    */
    public void setCopyright(String value){
            copyright = value;

    }
    
    public void setCopyrightYear(String value) {
        copyrightYear = value;
    }
    
	/**
	 * @hibernate.property column="page_copyrightdate"
	 * @return
	 */
    public String getCopyrightYear() {
        return copyrightYear;
    }

    /**
    * gets the author information of this accessory page
    *
    * @param
    * @return	author information of this accessory page
    */
    public String getAuthor(){
        return authors;
    }

    /**
    * sets the author information of this accessory page
    *
    * @param value	author information for this accessory page
    * @return
    */
    public void setAuthor(String value) {
            authors = value;    
    }


    /**
    * gets the text of this accessory page
    *
    * @hibernate.property column="main_text"
    * @return	text of this accessory page
    */
    public String getText() {
            return text;
    }

    /**
    * sets the text of this accessory page
    *
    * @param value		text of this accessory page
    */
    public void setText(String value) {
            text = value;
    }


    /**
    * gets the reference information of this accessory page
    *
    * @hibernate.property column="refs"
    * @return	reference information of this accessory page
    */
    public String getReferences() {
            return references;
    }

    /**
    * sets the reference information of this accessory page
    *
    * @param value	reference information for this accessory page
    * @return
    */
    public void setReferences(String value) {
            references = value;
    }

    /**
     * Returns the separate changed value for the references.  This is 
     * maintained separately because the references don't get uploaded
     * unless they have changed from the server
     *
     * @return Whether the references have been changed from the server
     */
    public boolean referencesChanged() {
        return referencesChanged;
    }

    public void setReferencesChanged(boolean value) {
        referencesChanged = value;
    }



    /**
    * gets the page status of this accessory page
    *
    * @hibernate.property
    * @return	page status of this accessory page
    */
    public String getStatus(){
            return status;
    }

    /**
    * sets the page status of this accessory page
    *
    * @param value		page status of this accessory page 
    * @return
    */
    public void setStatus(String value) {
            status = value;
            if ( !legalStatuses.contains(status) ) {
                status = XMLConstants.SKELETAL;
            }
    }        

    /**
     * Returns the linked accessory page.  This will be null unless this
     * accessory page links to another one in the currently downloaded tree
     *
     * @return The linked accessory page
     */
    public AccessoryPage getLinkedAccPage() {
        return linkedAccessoryPage;
    }

    public void setLinkedAccPage(AccessoryPage page) {
        linkedAccessoryPage = page;
    }

    /**
     * Returns the linked ancestor accessory page.  This will be null unless 
     * the accessory page links to another one not in the currently 
     * downloaded tree
     *
     * @return The linked accessory page
     */
    public Ancestor.AncestorAccPage getLinkedAncestorAccPage() {
        return linkedAncestorAccessoryPage;
    }

    public void setLinkedAncestorAccPage(Ancestor.AncestorAccPage value) {
        linkedAncestorAccessoryPage = value;
    }

    /**
     * Returns a boolean indicating whether this accessory page has its own
     * content or whether it links to another page
     *
     * @return A boolean indicating whether content is used
     */
    public boolean useContent() {
        return useContent;
    }

    public void setUseContent(boolean value) {
        useContent = value;
    }
    
    /**
     * @hibernate.property column="use_content"
     * @return
     */
    public boolean getUseContent() {
    	return useContent();
    }

    public boolean showOnMenu() {
        return showOnMenu;
    }

    public void setShowOnMenu(boolean value) {
        showOnMenu = value;
    }
    
   /**
    * gets the author information for this page as a vector of Author Objects.
    *
    * @param
    * @return	the author information for this page
    */
    public Vector getContributorList() {
        return contributorList;
    }
    
    public void setContributorList(Vector value) {
        contributorList = value;
    }    

	/**
	 * @hibernate.set table="InternetLinks" lazy="false" order-by="link_order asc" sort="natural" cascade="all"
	 * @hibernate.collection-composite-element class="org.tolweb.treegrow.page.InternetLink"
	 * @hibernate.collection-key column="acc_page_id"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 */ 	
	public SortedSet getInternetLinks() {
		if (internetLinks == null) {
			internetLinks = new TreeSet();
		}
		return internetLinks;
	}
	
	public void setInternetLinks(SortedSet value) {
		internetLinks = value;
	}
	
	public void addToInternetLinks(InternetLink value) {
	    value.setOrder(getInternetLinks().size());
		getInternetLinks().add(value);
	}
	
	public void removeFromInternetLinks(InternetLink value) {
		getInternetLinks().remove(value);
	}
	
	public boolean getHasInternetLinks() {
	    Iterator it = getInternetLinks().iterator();
	    while (it.hasNext()) {
	        InternetLink link = (InternetLink) it.next();
	        if (StringUtils.notEmpty(link.getUrl())) {
	            return true;
	        }
	    }
	    return false;	    
	}
	
	public boolean getHasReferences() {
	    return StringUtils.notEmpty(getReferences());
	}
	
	/**
	 * Returns the id of the contributor who actually created this AccessoryPage
	 * @hibernate.property column="contributor_id"
	 * @return
	 */
	public int getContributorId() {
		return contributorId;
	}
	
	public void setContributorId(int value) {
		contributorId = value;
	}
    
    public void setUsePermission(byte value) {
    	usePermission = value;
    }
    
    /**
     * @hibernate.property column="use_permission"
     * @return
     */
    public byte getUsePermission() {
    	return usePermission;
    }
    
    public int getId() {
    	return id;
    }
    
    public void setId(int value) {
    	id = value;
    }

    /**
     * Returns whether the page object that this accessory page is
     * attached to thinks that any of its accessory pages have changed
     *
     * @return Whether any of the pages accs have changed
     */
    public boolean auxiliaryChangedFromServer() {
        return page.getAccessoriesChanged();
    }

    /**
     * Sets the page object's accessorieschanged variable
     *
     * @param new value for the page object's accessorieschanged
     */
    public void setAuxiliaryChangedFromServer(boolean value) {
        page.setAccessoriesChanged(value);
    }

    /**
     * Returns whether or not anything has changed on the accessory page
     *
     * @return Whether or not anything has changed on the accessory page
     */
    public boolean changedFromServer() {
        return changed;
    }

    public void setChangedFromServer(boolean value) {
        changed = value;
        if (!value) {
            textChanged = false;
            referencesChanged = false;
        }
    }

    /**
     * Returns whether or not the text has changed.  This is maintained
     * separately since text sections don't get sent up to the server
     * unless they change
     *
     * @return Whether or not the page text has changed
     */
    public boolean textChanged() {
        return textChanged;
    }

    public void setTextChanged(boolean value) {
        textChanged = value;
    }
    
    /**
     * @hibernate.property
     */
    public String getAcknowledgements() {
    	return acknowledgements;
    }
    
    public void setAcknowledgements(String value) {
    	acknowledgements = value;
    }
    
    /**
     * @hibernate.property
     * @return
     */
    public String getNotes() {
    	return notes;
    }
    
    public void setNotes(String value) {
    	notes = value;
    }

    /**
     * @hibernate.property column="is_article"
     * @return
     */
    public boolean getIsArticle() {
        return isArticle;
    }

    public void setIsArticle(boolean isArticle) {
        this.isArticle = isArticle;
    }
    /**
     * @return Returns the isEditable.
     */
    public boolean getIsEditable() {
        return isEditable;
    }
    /**
     * @param isEditable The isEditable to set.
     */
    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }   
}

