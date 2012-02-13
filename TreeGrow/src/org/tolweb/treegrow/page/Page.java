package org.tolweb.treegrow.page;

import java.io.Serializable;
import java.util.*;

import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.tree.*;

/**
 * Page is a class that holds information pertaining to the page of a node
 * <p>
 * This class hold the following details
 * <ul>
 * <li>	Clade name	- group
 * <li>	Authors		- authorList
 * <li>	FirstOnline
 * <li>	CopyRight Infomation
 * <li> Acknowlegements	- note
 * <li> WriteAsList & Status	- option
 * <li> GenBank string
 * <li> TreeBase string
 * <li> Contents of the page	- textList
 * <li> Title of the page
 * <li> Menu url's of the page	- links
 * <li> Citation Date
 * <li> Image Caption.
 * <li> SubTitle
 * <li> Pre Tree Text
 * <li> Post Tree Text
 * <li> Tagged Image Caption
 * <li> Image Caption
 * <li> Accesory Page Details	- lichenList
 * </ul>
 */

public class Page implements Serializable, ChangedFromServerProvider {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8776974723918786920L;

	/**
    * Holds the list of authors and thier details
    */
    private Vector contributorList = null;

    /**
    * Holds the list of images associated with the page
    */
    private Vector imageList = null;

    /**
    * Holds the acknowlegements for this page
    */
    private String acknowledgements = null;

    /**
    * Holds the writeaslist and status for this page.
    * writeaslist states whether the page's tree should be displayed as a list or as
    * a normal tree.
    * Status states what the status of the page is, ie Skeletal, Under Construction etc.
    */
    //protected Option options = null;

    private boolean writeAsList;
    
    private String status;

    /**
    * Holds the genbank string
    */
    private String genBank = null;

    /**
    * Holds the treebase string
    */
    private String treeBase = null;

    /**
    * Holds the contents of this page. 
    * ie Introduction, Characteristics, Information on the net, references etc.
    */
    private Vector textList = null;

    /**
    * Holds the title of the page
    */
    private String title = null;

    /**
    * Holds the list of menu entries associated with this page.
    */
    private Vector links = null;

    /**
    * Holds the caption that is displayed with the images
    */
    private String imgCaption = null;
    
    private boolean printImageData;
    private boolean printCustomCaption;    

    /**
    * Holds the subttitle of the page
    */
    private String subTitle = null;

    /**
    * Holds the pretreetext
    */
    private String leadText = null;

    /**
    * Holds the accessory page informaion.
    */
    private Vector accessoryPages = null;
    
    private String references;
    
    private String internetLinks;

    /**
     * Holds the integer-code for use permission "license" for this page.  All 
     * values are defined in org.tolweb.treegrow.main.NodeImage. 
     * @see org.tolweb.treegrow.main.NodeImage
     */
    private byte usePermission; 
    
    /**
    * Holds the posttreetext
    */
    private String postTreeText = null;
    

	/**
	* Holds the copyright date details
	*/
	protected String copyrightDate = null;

	/**
	* Holds the copyright holder details
	*/
	protected String copyrightHolder = null;    

    private boolean copyrightDateChanged = false;
    private boolean copyrightHolderChanged = false;
	private boolean genBankChanged = false;
    private boolean treeBaseChanged = false;
    private boolean titleChanged = false;
    private boolean subTitleChanged = false;
    private boolean acksChanged = false;
    private boolean imgCaptionChanged = false;
    private boolean taggedIMGCaptionChanged = false;
    private boolean leadTextChanged = false;
    private boolean postTreeTextChanged = false;
    private boolean contributorsChanged = false;
    private boolean imageChanged = false;
    private boolean listChanged = false;
    private boolean linkChanged = false;
    private boolean copyrightChanged = false;
    private boolean optionsChanged = false;
    private boolean accessoriesChanged = false;
    private boolean writeChanged = false;
    private boolean statusChanged = false;
    private boolean referencesChanged = false;
    private boolean internetLinksChanged = false;
    private boolean usePermissionChanged = false;
    private boolean changed = false;
    private boolean textSectionsSorted = false;
    
    /**
     * Used to determine if this is a newly created Page i.e. hasn't been
     * uploaded to the server
     */
    private boolean isNewFromServer;
    
    private Node node;
    
    public Page() {
        this(null);
    }

    /**
    * Constructors a new instance of a Page with initialized 
    * collections and objects associations.
    *
    * @param n node associated with the page 
    */
    public Page(Node n) {
            imageList = new Vector();
            textList = new Vector();
            accessoryPages = new Vector();
            contributorList = new Vector();
            links = new Vector();
            node = n;
            // the reason this is not just set to 
            // ContributorLicenseInfo.LICENSE_DEFAULT 
            // is because TreeGrow/TGS do not have 
            // the TolwebUtils jar as a dependency 
            // and we do not wish to expand the 
            // dependencies of an application that's 
            // in it's twilight operating days. 
            usePermission = NodeImage.CC_BY_NC30;
    }

    /**
    * sets all the details of the page to an empty string.
    * Usually called when a new page object is created.
    *
    * @param
    * @return
    */
    public void getDefault()
    {
            title = new String("");
            subTitle = new String("");
            leadText = new String("");
            postTreeText = new String("");
            imgCaption = new String("");
            acknowledgements = new String("");
            genBank = new String("");
            treeBase = new String("");
    }

    /**
     * Returns a deep (i.e. new Object) copy of the page
     *
     * @return A deep copy of this Page
     */
    public Page getDeepCopy() {
        Page p = null;
        try {
            p = (Page)ObjectCloner.deepCopy(this);
        } catch (Exception e) {
            System.out.println ("Can't deepcopy page; ");
            System.out.println (e.getMessage());
            System.out.println (e.getStackTrace());
        }
        p.setChanged(true);
        return p;
    }


    /**
    * gets the genbank string associated with this page
    *
    * @hibernate.property column="genbank_string"
    * @param
    * @return	the genbank string
    */
    public String getGenBank()
    {
            return genBank;
    }

    /**
    * sets the genbank string associated with this page
    *
    * @param gen	the genbank string
    * @return
    */
    public void setGenBank(String gen)
    {
            genBank = gen;
    }

    /**
    * gets the treebase string associated with this page
    * @hibernate.property column="treebase_string"
    * @param
    * @return	the treebase string
    */
    public String getTreeBase()
    {
            return treeBase;
    }

    /**
    * sets the treebase string associated with this page
    *
    * @param gen	the treebase string
    * @return
    */
    public void setTreeBase(String gen)
    {
            treeBase = gen;
    }

    /**
    * gets the groupName for this page
    * 
    * @param
    * @return	the group name
    */
    public String getGroupName()
    {
            return node.getName();
    }

    public Node getNode() {
        return node;
    }

    /**
    * sets the groupName for this page
    *
    * @param name	the group name
    * @return
    */
    public void setGroupName(String name)
    {
        node.setName(name);
    }

    /**
    * gets the title for this page
    * @hibernate.property
    * @param
    * @return	the title
    */
    public String getTitle()
    {
            return title;
    }

    /**
    * sets the title for this page
    *
    * @param Title	the title
    * @return
    */
    public void setTitle(String Title)
    {
            title = Title;
    }

    /**
    * gets the subtitle for this page
    *
    * @param
    * @return	the subtitle
    */
    public String getSubTitle()
    {
            return subTitle;
    }

    /**
    * sets the subtitle for this page
    *
    * @param subTitle	the subtitle
    * @return
    */
    public void setSubTitle(String value)
    {
            subTitle = value;
    }

    /**
    * gets the List of images for this page as a vector of ImageList Objects.
    *
    * @param
    * @return	the list of images
    */
    public Vector getImageList()
    {
            return imageList;
    }

    /**
    * sets the List of images for this page as a vector of ImageList Objects.
    *
    * @param list	the list of images
    * @return
    */
    public void setImageList(Vector list)
    {
            imageList = list;
    }

    /**
    * gets the pretreetext for this page
    * @hibernate.property column="page_leadtext"
    * @param
    * @return	the pretreetext
    */
    public String getLeadText()
    {
            return leadText;
    }

    /**
    * sets the pretreetext for this page
    *
    * @param preTreeText	the pretreetext
    * @return
    */
    public void setLeadText(String preTreeText)
    {
            leadText = preTreeText;
    }

    /**
    * gets the posttreetext for this page
    * @hibernate.property column="page_aftertreetext"
    * @param
    * @return	the posttreetext
    */
    public String getPostTreeText()
    {
            return postTreeText;
    }

    /**
    * sets the posttreetext for this page
    *
    * @param postTreeText	the posttreetext
    * @return
    */
    public void setPostTreeText(String value)
    {
            postTreeText = value;
    }

    /**
    * gets the contents of this page as a vector of TextSection Objects.
    *
    * @param
    * @return	the contents of this page
    */
    public Vector getTextList() {
        if (!textSectionsSorted) {
            // Adding necessary sections
            checkForNecessarySections();
            Collections.sort(textList);
            textSectionsSorted = true;
        }
        return textList;
    }
    
    /**
     * Create the discussions of phylogenetic relationships section for a page
     * that was previously a leaf but no longer is
     *
     * @return The newly created DPR TextSection
     */
    public TextSection addDPR() {
        TextSection newSection = new TextSection(TextSection.DISCUSSION, "", this);
        return addDPR(newSection);
    }
    
    public TextSection addDPR(TextSection value) {
        textList.add(2, value);
        return value;
    }
    
    public TextSection removeDPR() {
        TextSection dprSection = checkForSectionNamed(TextSection.DISCUSSION);
        if (dprSection != null) {
            textList.remove(dprSection);
        }
        return dprSection;
    }    

    /**
     * Checks to make sure there is 1 section for each immutable text 
     * section.  If there isn't, then a text section is added with the
     * missing immutable section.
     */
    private void checkForNecessarySections() {
        TextSection intro, characteristics, discussion;//, references, information;
        
        intro = createAndAddSectionIfAbsent(TextSection.INTRODUCTION);
        characteristics = createAndAddSectionIfAbsent(TextSection.CHARACTERISTICS);        
        if (!node.getIsLeaf()) {
            discussion = checkForSectionNamed(TextSection.DISCUSSION); 
            if (discussion == null) {
                discussion = new TextSection(TextSection.DISCUSSION, "", this);
                textList.add(discussion);
                // Only set the order for the discussion of phylogenetic
                // relationships if it's missing.  Otherwise it can be moved
                // anywhere
                discussion.setOrder(2);
            }
        }       
        intro.setOrder(0);
        characteristics.setOrder(1);
    }
    
    private TextSection createAndAddSectionIfAbsent(String name) {
        TextSection result = checkForSectionNamed(name);
        if (result == null) {
            result = new TextSection(name, "", this);
            textList.add(result);
        }
        return result;
    }

    public TextSection checkForSectionNamed(String name) {
        Iterator it = textList.iterator();
        while (it.hasNext()) {
            TextSection next = (TextSection) it.next();
            if (next.getHeading().equals(name)) {
                return next;
            }
        }
        return null;
    }

    /**
    * sets the contents of this page as a vector of TextSection Objects.
    *
    * @param list	the contents of this page
    * @return
    */
    public void setTextList(Vector list)
    {
            textList = list;
    }
    
    public boolean referencesChanged() {
        return referencesChanged;
    }
    
    public void setReferencesChanged(boolean value) {
        referencesChanged = value;
    }
    
    /**
     * @hibernate.property column="refs"
     * @return
     */
    public String getReferences() {
        return references;
    }
    
    public void setReferences(String value) {
        references = value;
    }
    
    public boolean internetLinksChanged() {
        return internetLinksChanged;
    }
    
    public void setInternetLinksChanged(boolean value) {
        internetLinksChanged = value;
    }
    
    /**
     * @hibernate.property column="internetinfo_text"
     * @return
     */
    public String getInternetLinks() {
        return internetLinks;
    }
    
    public void setInternetLinks(String value) {
        internetLinks = value;
    }

    /**
     * Loops through all the accessory pages and returns those pages
     * that are linked to the accessor page passed-in
     *
     * @param link The AccessoryPage which other pages link to
     * @return A list of the pages that link to that page
     */
    public ArrayList getAccessoryPagesLinkedTo(AccessoryPage link) {
        Iterator it = accessoryPages.iterator();
        ArrayList returnList = new ArrayList();
        while (it.hasNext()) {
            AccessoryPage nextPage = (AccessoryPage) it.next();
            AccessoryPage linkedPage = nextPage.getLinkedAccPage();
            if (linkedPage != null && linkedPage == link) {
                returnList.add(nextPage);
            }
        }

        return returnList;
    }

    /**
    * gets the LichenPages information for this page as a vector of TextSection Objects.
    *
    * @param
    * @return	the LichenPages information for this page
    */
    public Vector getAccessoryPages()
    {
            return accessoryPages;
    }

    /**
    * sets the LichenPages information for this page as a vector of TextSection Objects.
    *
    * @param list	the LichenPages information for this page
    * @return
    */
    public void setAccessoryPages(Vector list)
    {
            accessoryPages = list;
    }

    /**
    * gets the image caption for this page
    * @hibernate.property column="graphiccaption_text"
    * @param
    * @return	the imagecaption
    */
    public String getImageCaption()
    {
            return imgCaption;
    }

    /**
    * sets the image caption for this page
    *
    * @param titleIllustration	the imagecaption
    * @return
    */
    public void setImageCaption(String titleIllustration)
    {
            imgCaption = titleIllustration;
    }
    
    public boolean printImageData() {
        return printImageData;
    }
    
    public void setPrintImageData(boolean value) {
        printImageData = value;
    }

    public boolean printCustomCaption() {
        return printCustomCaption;
    }
    
    public void setPrintCustomCaption(boolean value) {
        printCustomCaption = value;
    }

    /**
    * gets the acknowledgements for this page
    * @hibernate.property column="page_notes"
    * @param
    * @return	the acknowledgements
    */
    public String getAcknowledgements()
    {
            return acknowledgements;
    }

    /**
    * sets the acknowledgements for this page
    *
    * @param note	the acknowledgements
    * @return
    */
    public void setAcknowledgements(String Note)
    {
            acknowledgements = Note;
    }

    /**
    * gets the author information for this page as a vector of Author Objects.
    *
    * @param
    * @return	the author information for this page
    */
    public Vector getContributorList()
    {
            return contributorList;
    }

    /**
    * sets the author information for this page as a vector of Author Objects.
    *
    * @param list	the author information for this page
    * @return
    */
    public void setContributorList(Vector list)
    {
            contributorList = list;
    }

    /**
    * gets the menu entries for this page as a vector of Link Objects.
    *
    * @param
    * @return	the menu entries for this page
    */
    public Vector getLinks()
    {
            return links;
    }

    /**
    * sets the menu entries for this page as a vector of Link Objects.
    *
    * @param link	the menu entries for this page
    * @return
    */
    public void setLinks(Vector link)
    {
            links = link;
    }

    /**
     * @hibernate.property
     * @return
     */
    public String getStatus() {
        if (status == null) {
            return XMLConstants.SKELETAL;
        } else {
            return status;
        }
    }

    public void setStatus(String value) {
        status = value;
    }

    public void setStatusChanged(boolean value) {
        statusChanged = value;
    }

    public boolean getStatusChanged() {
        return statusChanged;
    }
    
    /**
     * @hibernate.property column="writeaslist"
     * @return
     */
    public boolean getWriteAsList() {
        return writeAsList();
    }

    public boolean writeAsList() {
        return writeAsList;
    }

    public void setWriteAsList(boolean value) {
        writeAsList = value;
    }

    public void setWriteChanged(boolean value) {
        writeChanged = value;
    }

    public boolean getWriteChanged() {
        return writeChanged;
    }

    /**
    * sets the information as to whether genbank string was changed or not.
    *
    * @param value	genbank string changed status
    * @return
    */
    public void setGenBankChanged(boolean value)
    {
            genBankChanged = value;
    }

    /**
    * gets the information as to whether genbank string was changed or not.
    *
    * @param
    * @return	genbank string changed status
    */
    public boolean getGenBankChanged()
    {
            return genBankChanged;
    }

    /**
    * sets the information as to whether treebase string was changed or not.
    *
    * @param value	treebase string changed status
    * @return
    */
    public void setTreeBaseChanged(boolean value)
    {
            treeBaseChanged = value;
    }

    /**
    * gets the information as to whether treebase string was changed or not.
    *
    * @param
    * @return	treebase string changed status
    */
    public boolean getTreeBaseChanged()
    {
            return treeBaseChanged;
    }

    /**
    * sets the information as to whether the title was changed or not.
    *
    * @param value	title changed status
    * @return
    */
    public void setTitleChanged(boolean value)
    {
            titleChanged = value;
    }

    /**
    * gets the information as to whether the title was changed or not.
    *
    * @param
    * @return	title changed status
    */
    public boolean getTitleChanged()
    {
            return titleChanged;
    }

    /**
    * sets the information as to whether the subtitle was changed or not.
    *
    * @param value	subtitle changed status
    * @return
    */
    public void setSubTitleChanged(boolean value)
    {
            subTitleChanged = value;
    }

    /**
    * gets the information as to whether the subtitle was changed or not.
    *
    * @param
    * @return	subtitle changed status
    */
    public boolean getSubTitleChanged()
    {
            return subTitleChanged;
    }

    /**
    * sets the information as to whether the acknowlegements was changed or not.
    *
    * @param value	acknowlegements changed status
    * @return
    */
    public void setAcksChanged(boolean value)	{
            acksChanged = value;
    }

    /**
    * gets the information as to whether the acknowlegements was changed or not.
    *
    * @param
    * @return	acknowlegements changed status
    */
    public boolean getAcksChanged()
    {
            return acksChanged;
    }

    /**
    * sets the information as to whether the image caption was changed or not.
    *
    * @param value	image caption changed status
    * @return
    */
    public void setImgCaptionChanged(boolean value)
    {
            imgCaptionChanged = value;
    }

    /**
    * gets the information as to whether the image caption was changed or not.
    *
    * @param
    * @return	image caption changed status
    */
    public boolean getImgCaptionChanged()
    {
            return imgCaptionChanged;
    }

    /**
    * sets the information as to whether the tagged image caption was changed or not.
    *
    * @param value	tagged image caption changed status
    * @return
    */
    public void setTaggedImgCaptionChanged(boolean value)
    {
            taggedIMGCaptionChanged = value;
    }

    /**
    * gets the information as to whether the tagged image caption was changed or not.
    *
    * @param
    * @return	tagged image caption changed status
    */
    public boolean getTaggedImgCaptionChanged()
    {
            return taggedIMGCaptionChanged;
    }

    /**
    * sets the information as to whether the pretreetext was changed or not.
    *
    * @param value	pretreetext changed status
    * @return
    */
    public void setLeadTextChanged(boolean value)
    {
            leadTextChanged = value;
    }

    /**
    * gets the information as to whether the pretreetext was changed or not.
    *
    * @param
    * @return	pretreetext changed status
    */
    public boolean getLeadTextChanged()
    {
            return leadTextChanged;
    }

    /**
    * sets the information as to whether the posttreetext was changed or not.
    *
    * @param value	posttreetext changed status
    * @return
    */
    public void setPostTreeTextChanged(boolean value)
    {
            postTreeTextChanged = value;
    }

    /**
    * gets the information as to whether the posttreetext was changed or not.
    *
    * @param
    * @return	posttreetext changed status
    */
    public boolean getPostTreeTextChanged()
    {
            return postTreeTextChanged;
    }

    /**
    * sets the information as to whether the author list was changed or not.
    *
    * @param value	author list changed status
    * @return
    */
    public void setContributorsChanged(boolean value)
    {
            contributorsChanged = value;
    }

    /**
    * gets the information as to whether the author list was changed or not.
    *
    * @param
    * @return	author list changed status
    */
    public boolean getContributorsChanged()
    {
            return contributorsChanged;
    }

    /**
    * sets the information as to whether the image list was changed or not.
    *
    * @param value	image list changed status
    * @return
    */
    public void setImageChanged(boolean value)
    {
            imageChanged = value;
    }

    /**
    * gets the information as to whether the image list was changed or not.
    *
    * @param
    * @return	image list changed status
    */
    public boolean getImageChanged()
    {
            return imageChanged;
    }

    /**
    * sets the information as to whether the content list was changed or not.
    *
    * @param value	content list changed status
    * @return
    */
    public void setListChanged(boolean value)
    {
            listChanged = value;
    }

    /**
    * gets the information as to whether the content list was changed or not.
    *
    * @param
    * @return	content list changed status
    */
    public boolean getListChanged()
    {
            return listChanged;
    }

    /**
    * sets the information as to whether the lichen list was changed or not.
    *
    * @param value	lichen list changed status
    * @return
    */
    public void setAccessoriesChanged(boolean value)
    {
            accessoriesChanged = value;
    }

    /**
    * gets the information as to whether the lichen list was changed or not.
    *
    * @param
    * @return	lichen list changed status
    */
    public boolean getAccessoriesChanged()
    {
        Iterator it = accessoryPages.iterator();
        // Check to see if any individual accessory page was changed
        while (it.hasNext()) {
            AccessoryPage page = (AccessoryPage) it.next();
            if (page.changedFromServer()) {
                return true;
            }
        }
        // Otherwise return the changed variable (this could still be true if all 
        // the accessories were deleted)
        return accessoriesChanged;
    }

    /**
    * sets the information as to whether the copyright details was changed or not.
    *
    * @param value	copyright details changed status
    * @return
    */
    public void setCopyrightChanged(boolean value)
    {
            copyrightChanged = value;
    }

    /**
    * gets the information as to whether the copyright details was changed or not.
    *
    * @param
    * @return	copyright details changed status
    */
    public boolean getCopyrightChanged()
    {
            return copyrightChanged;
    }

    /**
    * sets the information as to whether the status or writeaslist was changed or not.
    *
    * @param value	status or writeaslist changed status
    * @return
    */
    public void setOptionsChanged(boolean value)
    {
            optionsChanged = value;
    }

    /**
    * gets the information as to whether the status or writeaslist was changed or not.
    *
    * @param
    * @return	status or writeaslist changed status
    */
    public boolean getOptionsChanged()
    {
            return optionsChanged;
    }

    /**
    * sets the information as to whether the Link list was changed or not.
    *
    * @param value	Link list changed status
    * @return
    */
    public void setLinkChanged(boolean value)
    {
            linkChanged = value;
    }

    /**
    * gets the information as to whether the Link list was changed or not.
    *
    * @param
    * @return	Link list changed status
    */
    public boolean getLinkChanged()
    {
            return linkChanged;
    }

    public boolean changedFromServer() {
        return changed;
    }

    public void setChangedFromServer(boolean value) {
        changed = value;
    }
    
	/**
	 * @return Returns the copyrightDateChanged.
	 */
	public boolean getCopyrightDateChanged() {
		return copyrightDateChanged;
	}
	/**
	 * @param copyrightDateChanged The copyrightDateChanged to set.
	 */
	public void setCopyrightDateChanged(boolean copyrightDateChanged) {
		this.copyrightDateChanged = copyrightDateChanged;
	}
	/**
	 * @return Returns the copyrightHolderChanged.
	 */
	public boolean getCopyrightHolderChanged() {
		return copyrightHolderChanged;
	}
	/**
	 * @param copyrightHolderChanged The copyrightHolderChanged to set.
	 */
	public void setCopyrightHolderChanged(boolean copyrightHolderChanged) {
		this.copyrightHolderChanged = copyrightHolderChanged;
	}    
    
	/**
	 * @return Returns the usePermissionChanged.
	 */
	public boolean getUsePermissionChanged() {
		return usePermissionChanged;
	}

	/**
	 * @param usePermissionChanged the usePermissionChanged is set to the parameter
	 */
	public void setUsePermissionChanged(boolean usePermissionChanged) {
		this.usePermissionChanged = usePermissionChanged;
	} 
	
    /**
     * Used to determine if this page hasn't been uploaded yet.  Needed because
     * it is illegal to upload a subtree that has an ancestor subtree with an
     * unuploaded page
     *
     * @return Whether or not this page has been uploaded yet
     */
    public boolean isNewFromServer() {
        return isNewFromServer;
    }
    
    /**
     * Sets whether or not this page has been uploaded yet
     *
     * @param value
     */
    public void setIsNewFromServer(boolean value) {
        isNewFromServer = value;
    }

    /**
     * Convenience method to set all of the changed variables on the page
     */
    public void setChanged(boolean value) {        
        setTreeBaseChanged(value);
        setGenBankChanged(value);
        setTitleChanged(value);
        setSubTitleChanged(value);
        setAcksChanged(value);
        setImgCaptionChanged(value);
        setLeadTextChanged(value);
        setPostTreeTextChanged(value);
        setContributorsChanged(value);

        Iterator it = getContributorList().iterator();
        while( it.hasNext() ) {
                PageContributor tempAuthor = (PageContributor)it.next();
                tempAuthor.setChangedFromServer(value);
        }

        setImageChanged(value);
        setListChanged(value);

        it = getTextList().iterator();
        while (it.hasNext()) {
            TextSection tempText = (TextSection) it.next();
            tempText.setChangedFromServer(value);
        }

        setCopyrightChanged(value);

        setStatusChanged(value);
        setWriteChanged(value);

        setAccessoriesChanged(value);
        it = accessoryPages.iterator();
        while (it.hasNext()) {
            AccessoryPage accessory = (AccessoryPage) it.next();
            accessory.setChangedFromServer(value);
            accessory.setReferencesChanged(value);
            accessory.setTextChanged(value);
        }
        
        setReferencesChanged(value);
        setInternetLinksChanged(value);
        setUsePermissionChanged(value);
        setCopyrightDateChanged(value);
        setCopyrightHolderChanged(value);
        setChangedFromServer(value);
    }
    
    public boolean isChanged() {
        return getTitleChanged() ||
        getSubTitleChanged() ||
        getImageChanged() ||
        getLeadTextChanged() ||
        getPostTreeTextChanged() ||
        getListChanged() ||
        getOptionsChanged() ||
        getCopyrightChanged() ||
        getLinkChanged()  ||
        getContributorsChanged() ||
        getWriteChanged() ||
        getAccessoriesChanged() ||
        getStatusChanged() ||
        getGenBankChanged() ||
        getTreeBaseChanged() ||
        getAcksChanged() ||
        getImgCaptionChanged() ||
        getTaggedImgCaptionChanged() ||
        referencesChanged() ||
        internetLinksChanged() ||
		getCopyrightDateChanged() ||
		getCopyrightHolderChanged() ||
		getUsePermissionChanged() ||
        changedFromServer();   
    }
    
    /*public boolean removeAllAccessoryPagesLinkedToMe() {
        Vector accPages = getAccessoryPages();
        Iterator it = accPages.iterator();
        boolean prompt = true;
        Controller controller = Controller.getController();
        while (it.hasNext()) {
            AccessoryPage page = (AccessoryPage) it.next();
            if (prompt) {
                // Ask the user if they really want to delete the page
                boolean result = controller.removeAccessoryPageFromLinkComboBoxes(page);
                if (!result) {
                    // They decided not to, so don't delete the page
                    return false;
                }
                prompt = false;
            } else {
                // The user has already chosen to delete the page, so just delete all of the 
                // links to this accessory page
                controller.removeAccessoryPageFromLinkComboBoxes(page, false);
            }
        }    
        return true;
    }*/

	/**
	* Returns the CopyRight date.
	* @hibernate.property column="page_copyrightdate"
	* @param
	* @return	copyright date
	*/
	public String getCopyrightDate() {
		return copyrightDate;
	}

	/**
	* set the CopyRight date.
	*
	* @param date	copyright date
	* @return
	*/
	public void setCopyrightDate(String date) {
		copyrightDate = date;
	}	

	/**
	* Returns the CopyRight holder.
	* @hibernate.property column="page_copyrightholder"
	* @param
	* @return	copyright holder
	*/
	public String getCopyrightHolder() {
		return copyrightHolder;
	}

	/**
	* set the CopyRight holder.
	*
	* @param holder	copyright holder
	* @return
	*/
	public void setCopyrightHolder(String holder) {
		copyrightHolder = holder;
	}

	/**
	 * Returns an integer-code that correponds to a license 
	 * defined in org.tolweb.treegrow.main.NodeImage
	 * @hibernate.property column="usePermission"
	 * @return an integer representing the license to govern the usage of the material
	 */
	public byte getUsePermission() {
		return usePermission;
	}

	public void setUsePermission(byte usePermission) {
		this.usePermission = usePermission;
	}  
}








