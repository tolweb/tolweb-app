package org.tolweb.treegrow.main;

import org.jdom.*;
import java.util.*;

import org.tolweb.base.xml.BaseXMLWriter;
import org.tolweb.treegrow.page.*;
import org.tolweb.treegrow.tree.*;

/**
 * Class responsible for writing XML based on some Tree object
 */
public abstract class XMLWriter extends BaseXMLWriter {
    
    protected boolean m_HasChildren;
    protected Document m_FileDocument = null;
    protected Document m_WebDocument = null;
    protected boolean m_FileChanged = false;
    protected Tree tree;
    
    protected void initTree() {
        if (tree == null) {
            tree = TreePanel.getTreePanel().getTree();
        }        
    }
    
    /**
     * Constructs an XMLWriter that writes out the passed-in tree
     *
     * @param t The tree to write XML for
     */
    public XMLWriter(Tree t) {
        tree = t;
    }
    
    /**
     * Constructs an XMLWriter that writes out the currently open tree
     */
    public XMLWriter() {
        if (TreePanel.class != null && TreePanel.getTreePanel() != null) {
            tree = TreePanel.getTreePanel().getTree();
        }
    }
    
    /**
     * Writes the XML starting at the root of the tree 
     *
     * @return true if writing was successful, false otherwise
     */
    public boolean writeXML() {
        return writeXML(tree.getRoot());
    }
    
    /**
     * Writes the XML starting at the node passed-in
     *
     * @param root The node to start writing at
     * @return true if writing was successful, false otherwise
     */
    public boolean writeXML(Node root) {
        //try {
        Controller controller = Controller.getController();
        
        Stack nodeStack = new Stack();
        Stack elmntStack = new Stack();
        initTree();
        
        
        
        //if uploading a subtree, then set the "ancestors" list to correctly include nodes between subtree root and full root.
        if (tree != null  && root != tree.getRoot()) {
            Node node = root.getParent();
            String prefix = "";
            while (node != null) {
                prefix += node.getId() +",";
                node = node.getParent();
            }
        }
        
        
        //XML header info
        Element mainElmt = new Element(XMLConstants.TREE);
        addAdditionalRootElements(mainElmt);
        mainElmt.setAttribute(XMLConstants.NEW_VERSION, XMLConstants.ONE);
        if (saveToFile() ) {
            // The only time TreePanel is null is if we are at the file manager,
            // since we cant modify anything from there, dont overwrite the
            // modified date.
            if (TreePanel.getTreePanel() != null) {
                mainElmt.setAttribute(XMLConstants.MODIFIEDDATE, "" + System.currentTimeMillis());
            } else {
                mainElmt.setAttribute(XMLConstants.MODIFIEDDATE, "" + tree.getModifiedDate());                
            }
            mainElmt.setAttribute(XMLConstants.DOWNLOADDATE, "" + tree.getDownloadDate());
            mainElmt.setAttribute(XMLConstants.UPLOADDATE, "" + tree.getUploadDate());
            if (controller.getEditorBatchId() != null) {
                mainElmt.setAttribute(XMLConstants.BATCHID, controller.getEditorBatchId());
            }
            Element fileElmt = new Element(XMLConstants.FILENAME);
            fileElmt.setText( controller.getFileName() );
            mainElmt.addContent(fileElmt);
            
            Element nodeidElmt = new Element(XMLConstants.NODEID);
            nodeidElmt.setText("" + controller.getNodeId());
            mainElmt.addContent(nodeidElmt);
            
            Element depthElmt = new Element(XMLConstants.DEPTH);
            depthElmt.setText("" + controller.getDepth());
            mainElmt.addContent(depthElmt);
        }
        
        Element treeStructureElmt = new Element(XMLConstants.TREESTRUCTURE);
        treeStructureElmt.setText(tree.toString());
        mainElmt.addContent(treeStructureElmt);
        
        Element downloadIdElmt = new Element(XMLConstants.DOWNLOAD_ID);
        downloadIdElmt.setText("" + controller.getDownloadId());
        mainElmt.addContent(downloadIdElmt);
        Iterator it;        

        Element nodesElmt = new Element(XMLConstants.NODES);
        mainElmt.addContent(nodesElmt);
        
        
        //Deal with nodes of the tree
        nodeStack.push(root);
        Element rootElmt = constructNodeElement();
        nodesElmt.addContent(rootElmt);
        elmntStack.push(rootElmt);
        
        while (! nodeStack.empty() ) {
            Node node = (Node)nodeStack.pop();
            Element nodeElmt = (Element)elmntStack.pop();
            
            fleshOutNode(node,nodeElmt);
            m_FileChanged = m_FileChanged || node.getChanged();
            
            if ( saveToFile() || !tree.isTerminalNode(node) )  {
                it = node.getChildren().iterator();
                if ( it.hasNext() ) {
                    nodesElmt = new Element(XMLConstants.NODES);
                    nodeElmt.addContent(nodesElmt);
                }
                
                while (it.hasNext() ) {
                    Node child = (Node)it.next();
                    nodeStack.push(child);
                    
                    Element childElmt = constructNodeElement();
                    nodesElmt.addContent(childElmt);
                    elmntStack.push(childElmt);
                }
            }
        }
        
        if (writeOutContributors()) {
            it = Controller.getController().getEditableContributors().iterator();
            Element contrElement = new Element(XMLConstants.CONTRIBUTORLIST);
            mainElmt.addContent(contrElement);
            while (it.hasNext()) {
                Contributor contr = (Contributor) it.next();
                contrElement.addContent(encodeContributor(contr, true));
            }
        }
        
        if (writeOutImages()) {
            it = tree.getImages().iterator();
            Element imgsElement = new Element(XMLConstants.IMAGELIST);
            mainElmt.addContent(imgsElement);
            while (it.hasNext()) {
                NodeImage img = (NodeImage) it.next();
                imgsElement.addContent(encodeImage(img));
            }
        }
        
        if (saveToFile() ) {
            Element fileChanged = new Element("FILECHANGED");
            if(m_FileChanged == true)
                fileChanged.setText("TRUE");
            else
                fileChanged.setText("FALSE");
            mainElmt.addContent(fileChanged);
        }
        
        Document outDoc = new Document(mainElmt);
        return outputDocument(outDoc, root);
        //return outDoc ; //?
    }
    
    protected void addAdditionalRootElements(Element mainElmt) {
		// override in subclasses for custom attributes
	   
	}

 /**
     * Meant to be subclassed so that server code can also use these classes since server
     * methods for detecting if a node has a page are different than client methods
     * @param node
     * @return
     */
    protected abstract boolean getNodeHasPage(Node node);
    
    /**
     * Will return the number of children for this node.  On the server this comes from
     * the database, on the client it comes from the tree structure in memory
     * @param node
     * @return
     */
    protected abstract int getNodeChildcount(Node node);
    
    protected abstract Collection getOtherNamesForNode(Node node);
    protected abstract Page getPageForNode(Node node);
    protected abstract Collection getTextSectionsForPage(Page page);
    protected abstract Collection getAccessoryPagesForPage(Page page, Node node);
	protected abstract Collection getContributorsForPage(Page page);
	protected abstract Collection getContributorsForAccessoryPage(AccessoryPage accPage);
	protected abstract int getNameDateForNode(Node node);
	protected abstract int getNodeRankForNode(Node node);
	protected abstract int getNodeIdForNode(Node node);
	protected abstract int getDateForOtherName(OtherName otherName);
    protected abstract void writeOutCopyrightInfo(Page page, Element pageElement);
	
	protected void doAdditionalTextSectionProcessing(TextSection textSection, Set imgs) {}
	protected void doAdditionalAccessoryPageProcessing(AccessoryPage accPage, Set imgs) {}
    
    /**
     * Used to indicate whether a subclass of XMLWriter is in file writing mode,
     * in which case more information is written.
     *
     * @return true If the subclass is writing to a file, false otherwise.
     */
    protected abstract boolean saveToFile();
    /**
     * Writes the given document to either a file or a URL
     *
     * @param outDuc The doc to write
     * @return true If the write was successful, false otherwise.
     */
    protected abstract boolean outputDocument(Document outDoc, Node root) ;
    
    /**
     * Translates a local img path to it's counterpart on the server.  This is
     * likely called from text sections that have images added to them.  By
     * default, however it just returns the same thing (overridden in 
     * URLXMLWriter)
     *
     * @param input The original path to the file
     * @return The new path to the file
     */
    protected String getFixedLocalFileUrl(String input) {
        return input;
    }
    
    protected void getFixedLocalContributorId(PageContributor pc) {
    }
    
    /**
     * By default, writes a simple "creating xml document" message to the status bar.
     * Available to be overridden by the AutoSaveFileXMLWriter, so that the status
     * bar won't be changed by autosaves.
     */
    protected void setStatusMessage() {
        Controller controller = Controller.getController();
        String ST_CREATING_XML = controller.getMsgString("ST_CREATING_XML");
        controller.setStatusMessage(ST_CREATING_XML);
    }
    
    public void fleshOutNode(Node node, Element nodeElement) {
    	fleshOutNode(node, nodeElement, true, null, null, null, true);
    }
    
    /**
     * Takes the passed-in Node object and Node element and fills out the 
     * details
     *
     * @param node The Node object to use to fill out the element
     * @param nodeElmt The Element to fill out
     * @param isComplete Whether or not to fill out page and other detail information for this node
     * @param includeContributors 
     */
    public void fleshOutNode(Node node, Element nodeElmt, boolean isComplete, Set imgs, Set contributors, Contributor contr, boolean includeContributors) {
        String name = node.getName();
        if((name == null) || (name.equals("")) || name.equals("Unnamed")) {
            name = "";
        }
        Element nameElement = new Element(XMLConstants.NAME);
        nameElement.addContent(getCDATA(name));
        nodeElmt.addContent(nameElement);
        
        nodeElmt.setAttribute(XMLConstants.HASPAGE, getNodeHasPage(node) || node.getHasPageOnServer() ? XMLConstants.ONE : XMLConstants.ZERO);
        nodeElmt.setAttribute(XMLConstants.NAMECHANGED, node.getNameChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        nodeElmt.setAttribute(XMLConstants.ID, getNodeIdForNode(node) + "");
        
        Element lock_info = null;
        if (saveToFile()) {
            nodeElmt.setAttribute(XMLConstants.LOCKED,node.getLocked() ? XMLConstants.ONE : XMLConstants.ZERO);
            if(node.getLocked()) {

                lock_info = new Element(XMLConstants.LOCK_INFO);
                nodeElmt.addContent(lock_info);
                String tempString = node.getLockDate();

                if(StringUtils.notEmpty(tempString)) {
                    lock_info.setAttribute(XMLConstants.DATE_TIME, tempString);
                }
                tempString = node.getLockUser();
                if(StringUtils.notEmpty(tempString)) {
                    lock_info.setAttribute(XMLConstants.USER, tempString);
                }
                tempString = node.getLockType();
                if(StringUtils.notEmpty(tempString)) {
                    lock_info.setAttribute(XMLConstants.TYPE, tempString);
                }
            }
        }
        nodeElmt.setAttribute(XMLConstants.EXTINCT,""+node.getExtinct());
        if (saveToFile() || node.getExtinctChanged()) {
            if (saveToFile()) {
                nodeElmt.setAttribute(XMLConstants.EXTINCTCHANGED, node.getExtinctChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
            }
        }
        nodeElmt.setAttribute(XMLConstants.CONFIDENCE,""+node.getConfidence());        
        if (saveToFile() || node.getConfidenceChanged()) {
            if (saveToFile()) {
                nodeElmt.setAttribute(XMLConstants.CONFIDENCECHANGED, node.getConfidenceChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
            }
        }
        nodeElmt.setAttribute(XMLConstants.PHYLESIS,""+node.getPhylesis());        
        if (saveToFile() || node.getPhylesisChanged()) {
            if (saveToFile()) {
                nodeElmt.setAttribute(XMLConstants.PHYLESISCHANGED,  node.getPhylesisChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
            }
        }
        handleAuthority(node, nodeElmt);       
        nodeElmt.setAttribute(XMLConstants.CHILDCOUNT, getNodeChildcount(node) + "");
        if (saveToFile() || node.getChildrenChanged()) {
        	nodeElmt.setAttribute(XMLConstants.CHILDRENCHANGED, node.getChildrenChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }
        nodeElmt.setAttribute(XMLConstants.SEQUENCE, (node.getSequence() + 1) + "");
        
        if (saveToFile()) {
            nodeElmt.setAttribute(XMLConstants.SEQUENCECHANGED, node.getSequenceChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }
        
        nodeElmt.setAttribute(XMLConstants.LEAF, node.getIsLeaf() ? XMLConstants.ONE : XMLConstants.ZERO);        
        if (saveToFile() || node.getLeafChanged()) {

            if (saveToFile()) {
                nodeElmt.setAttribute(XMLConstants.LEAFCHANGED, node.getLeafChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
            }
        }
        nodeElmt.setAttribute(XMLConstants.PRIORITY, node.getPriority() + "");
        if (saveToFile()) {
            nodeElmt.setAttribute(XMLConstants.PRIORITYCHANGED, node.getPriorityChanged() ? XMLConstants.ONE : XMLConstants.ZERO);
        }
        nodeElmt.setAttribute(XMLConstants.INCOMPLETESUBGROUPS, node.getHasIncompleteSubgroups() ? XMLConstants.ONE : XMLConstants.ZERO);
        if (saveToFile()) {
            nodeElmt.setAttribute(XMLConstants.INCOMPLETESUBGROUPSCHANGED, node.getIncompleteSubgroupsChanged() ? XMLConstants.ONE : XMLConstants.ZERO);
        }
        nodeElmt.setAttribute(XMLConstants.NODERANK, getNodeRankForNode(node) + "");        
        if (saveToFile()) {
            nodeElmt.setAttribute(XMLConstants.NODERANKCHANGED, node.getNodeRankChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }
        
        if (saveToFile() || node.getDontPublishChanged()) {
            nodeElmt.setAttribute(XMLConstants.DONTPUBLISHCHANGED, node.getDontPublishChanged() ? XMLConstants.ONE : XMLConstants.ZERO);
        }
        nodeElmt.setAttribute(XMLConstants.PAGEREMOVED, node.getPageWasRemoved() ? XMLConstants.ONE : XMLConstants.ZERO);
        nodeElmt.setAttribute(XMLConstants.PAGEADDED, node.getPageWasAdded() ? XMLConstants.ONE : XMLConstants.ZERO);
        if (saveToFile()) {
            nodeElmt.setAttribute(XMLConstants.PAGECHANGED, node.getPageChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }
        if (isComplete) {
	        Element pageElmt = addPageContent(node, imgs, contributors, contr, includeContributors);
	        if (pageElmt != null) {
	            nodeElmt.addContent(pageElmt);
	        }
        }
        
        Element noteElmt = new Element(XMLConstants.DESCRIPTION);            
        noteElmt.addContent(getCDATA(node.getDescription()));
        nodeElmt.addContent(noteElmt);        
        if (saveToFile() || node.getDescriptionChanged()) {
            if (saveToFile()) {
                nodeElmt.setAttribute(XMLConstants.DESCRIPTIONCHANGED, node.getDescriptionChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
            }
        }
        
        if (isComplete) {
        	Iterator it = getOtherNamesForNode(node).iterator();
            
            Element synonyms = new Element(XMLConstants.OTHERNAMES);
            synonyms.setAttribute(XMLConstants.CHANGED, node.getOtherNamesChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
            nodeElmt.addContent(synonyms);
            int counter = 0;
            while(it != null && it.hasNext()) {
                OtherName tempSynonym = (OtherName)it.next() ;
                Element synElmt = new Element(XMLConstants.OTHERNAME);
                synElmt.setAttribute(XMLConstants.ISIMPORTANT, tempSynonym.getIsImportant() ? XMLConstants.ONE : XMLConstants.ZERO);
                synElmt.setAttribute(XMLConstants.ISPREFERRED, tempSynonym.getIsPreferred() ? XMLConstants.ONE : XMLConstants.ZERO);
                synElmt.setAttribute(XMLConstants.SEQUENCE, counter++ + "");
                synElmt.setAttribute(XMLConstants.CHANGED, tempSynonym.changedFromServer() ? XMLConstants.TRUE : XMLConstants.FALSE);
                synElmt.setAttribute(XMLConstants.DATE, ""+getDateForOtherName(tempSynonym));
                if(tempSynonym.getName() != null) {
                    Element nameElem = new Element(XMLConstants.NAME);
                    synElmt.addContent(nameElem);
                    nameElem.addContent(getCDATA(tempSynonym.getName()));                    
                }
                if (tempSynonym.getAuthority() != null) {
                    Element authElem = new Element(XMLConstants.AUTHORITY);
                    synElmt.addContent(authElem);
                    authElem.addContent(getCDATA(tempSynonym.getAuthority()));                                        
                }
                
                synonyms.addContent(synElmt);
            }
        }
        if (saveToFile()) {
            nodeElmt.setAttribute(XMLConstants.OTHERNAMESCHANGED, node.getOtherNamesChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }
        String sourceDbNodeIdString = node.getSourceDbNodeId() != null ? node.getSourceDbNodeId() : "";
        nodeElmt.setAttribute(XMLConstants.SOURCE_DB_ID, sourceDbNodeIdString);
        String sourceDbIdString = node.getSourceDbId() != null ? node.getSourceDbId().toString() : "-1";
        nodeElmt.setAttribute(XMLConstants.SOURCE_DB, sourceDbIdString);
    }
    
    
    protected void handleAuthority(Node node, Element nodeElement) {
        Element authorityElmt = new Element(XMLConstants.AUTHORITY);
        authorityElmt.addContent(getCDATA(node.getNameAuthority()));
        nodeElement.addContent(authorityElmt);        
        if (saveToFile() || node.nameAuthorityChanged()) {
            if (saveToFile()) {
                authorityElmt.setAttribute(XMLConstants.CHANGED, node.nameAuthorityChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
            }
        }
        Element dateElmt = new Element(XMLConstants.AUTHDATE); 
        dateElmt.addContent("" + getNameDateForNode(node));
        nodeElement.addContent(dateElmt);
        if (saveToFile() || node.nameDateChanged()) {
            if (saveToFile()) {
                dateElmt.setAttribute(XMLConstants.CHANGED, node.nameDateChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
            }        
        }
    }

    /**
     * Used during file upload and also during image check-in/upload
     *
     * @param img The Image to encode
     * @return An XML element containing the data corresponding to the img
     */
    public Element encodeImage(NodeImage img) {
        Element imgElt = new Element(XMLConstants.IMAGE);
        imgElt.setAttribute(XMLConstants.ID, img.getId() + "");
        finishEncoding(img, imgElt);

        return imgElt;
    }
    
    protected void finishEncoding(NodeImage img, Element imgElt) {
        imgElt.setAttribute(XMLConstants.CHANGED, img.changedFromServer() ? XMLConstants.ONE : XMLConstants.ZERO);
        if (saveToFile()) {
            imgElt.setAttribute(XMLConstants.CHECKED_OUT_FILE, img.checkedOut() ? XMLConstants.ONE : XMLConstants.ZERO);
        }
        if (img.getLocation() != null) {
            imgElt.setAttribute(XMLConstants.LOCATION, img.getLocation());
        }
        imgElt.setAttribute(XMLConstants.PERMISSION, "" + img.getUsePermission());
        imgElt.setAttribute(XMLConstants.COPYRIGHTOWNER, "" + img.getCopyrightContributorId());
        imgElt.setAttribute(XMLConstants.FOSSIL, img.isFossil() ? XMLConstants.ONE : XMLConstants.ZERO);
        imgElt.setAttribute(XMLConstants.PUBLICDOMAIN, img.inPublicDomain() ? XMLConstants.ONE : XMLConstants.ZERO);
        imgElt.setAttribute(XMLConstants.SPECIMEN, img.getIsSpecimen() ? XMLConstants.ONE : XMLConstants.ZERO);
        imgElt.setAttribute(XMLConstants.BODYPARTS, img.getIsBodyParts() ? XMLConstants.ONE : XMLConstants.ZERO);
        imgElt.setAttribute(XMLConstants.ULTRASTRUCTURE, img.getIsUltrastructure() ? XMLConstants.ONE : XMLConstants.ZERO);
        imgElt.setAttribute(XMLConstants.HABITAT, img.getIsHabitat() ? XMLConstants.ONE : XMLConstants.ZERO);
        imgElt.setAttribute(XMLConstants.EQUIPMENT, img.getIsEquipment() ? XMLConstants.ONE : XMLConstants.ZERO);
        imgElt.setAttribute(XMLConstants.PEOPLE, img.getIsPeopleWorking() ? XMLConstants.ONE : XMLConstants.ZERO);
        addCDATAElement(img.getUserCreationDate(), XMLConstants.CREATIONDATE, imgElt);
        addCDATAElement(img.getAlive(), XMLConstants.ALIVE, imgElt);
        addCDATAElement(img.getBehavior(), XMLConstants.BEHAVIOR, imgElt);
        addCDATAElement(img.getVoucherNumber(), XMLConstants.VOUCHERNUMBER, imgElt);
        addCDATAElement(img.getVoucherNumberCollection(), XMLConstants.VOUCHERNUMBERCOLLECTION, imgElt);
        addCDATAElement(img.getNotes(), XMLConstants.NOTES, imgElt);
        addCDATAElement(img.getImageType(), XMLConstants.IMAGETYPE, imgElt);        
        addCDATAElement(img.getCopyrightUrl(), XMLConstants.COPYRIGHTURL, imgElt);
        addCDATAElement(img.getCopyrightEmail(), XMLConstants.COPYRIGHTEMAIL, imgElt);
        addCDATAElement(img.getCopyrightDate(), XMLConstants.COPYRIGHTDATE, imgElt);
        addCDATAElement(img.getSex(), XMLConstants.SEX, imgElt);
        //addCDATAElement(img.getDescription(), XMLConstants.CAPTION, imgElt);
        addCDATAElement(img.getCopyrightOwner(), XMLConstants.COPYRIGHTOWNER, imgElt);
        addCDATAElement(img.getScientificName(), XMLConstants.SCIENTIFICNAME, imgElt);
        addCDATAElement(img.getReference(), XMLConstants.REFERENCES, imgElt);
        addCDATAElement(img.getCreator(), XMLConstants.CREATOR, imgElt);
        addCDATAElement(img.getIdentifier(), XMLConstants.IDENTIFIER, imgElt);
        addCDATAElement(img.getAcknowledgements(), XMLConstants.ACKS, imgElt);
        addCDATAElement(img.getGeoLocation(), XMLConstants.GEOLOCATION, imgElt);
        addCDATAElement(img.getStage(), XMLConstants.STAGE, imgElt);
        addCDATAElement(img.getBodyPart(), XMLConstants.BODYPART, imgElt);
        addCDATAElement(img.getSize(), XMLConstants.SIZE, imgElt);
        addCDATAElement(img.getView(), XMLConstants.VIEW, imgElt);
        addCDATAElement(img.getPeriod(), XMLConstants.PERIOD, imgElt);
        addCDATAElement(img.getCollection(), XMLConstants.COLLECTION, imgElt);
        addCDATAElement(img.getCollectionAcronym(), XMLConstants.COLLECTIONAC, imgElt);
        addCDATAElement(img.getType(), XMLConstants.TYPE, imgElt);
        addCDATAElement(img.getCollector(), XMLConstants.COLLECTOR, imgElt);
        addCDATAElement(img.getAltText(), XMLConstants.ALTTEXT, imgElt);
        addCDATAElement(img.getComments(), XMLConstants.COMMENTS, imgElt);
        Element nodesElmt = new Element(XMLConstants.NODES);
        imgElt.addContent(nodesElmt);
        writeOutNodesForImage(img, nodesElmt);
        Element versionsElmt = new Element(XMLConstants.VERSIONS);
        imgElt.addContent(versionsElmt);
        writeOutVersionsForImage(img, versionsElmt);
    }
    
    protected Element encodeImageVersion(ImageVersion version) {
        Element versionElement = new Element(XMLConstants.VERSION);
        versionElement.setAttribute(XMLConstants.ID, version.getVersionId().toString());
        versionElement.setAttribute(XMLConstants.NAME, version.getVersionName());
        versionElement.setAttribute(XMLConstants.WIDTH, version.getWidth().toString());
        versionElement.setAttribute(XMLConstants.HEIGHT, version.getHeight().toString());
        return versionElement;
    }
    
    protected abstract void writeOutVersionsForImage(NodeImage img, Element versionsElmt);
    protected abstract void writeOutNodesForImage(NodeImage img, Element nodesElmt);
    
    public Element constructNodeElement() {
    	return new Element(XMLConstants.NODE);
    }
    
    public Element encodeContributor(Contributor contr) {
		return encodeContributor(contr, true);
	}

	public Element encodeContributor(Contributor contr, boolean encodePermissions) {
        Element contributor = new Element(XMLConstants.CONTRIBUTOR);
        contributor.setAttribute(XMLConstants.ID, "" + contr.getId());
        contributor.setAttribute(XMLConstants.CHANGED, contr.changedFromServer() ? XMLConstants.ONE : XMLConstants.ZERO);
        contributor.setAttribute(XMLConstants.CHECKED_OUT_FILE, contr.checkedOut() ? XMLConstants.ONE : XMLConstants.ZERO);
        contributor.setAttribute(XMLConstants.NOEMAIL, contr.dontShowEmail() ? XMLConstants.ONE : XMLConstants.ZERO);
        contributor.setAttribute(XMLConstants.NOADDRESS, contr.dontShowAddress() ? XMLConstants.ONE : XMLConstants.ZERO);        
        addCDATAElement(contr.getFirstName(), XMLConstants.FIRSTNAME, contributor);
        addCDATAElement(contr.getLastName(), XMLConstants.LASTNAME, contributor);        
        addCDATAElement(contr.getEmail(), XMLConstants.EMAIL, contributor);
        addCDATAElement(contr.getHomepage(), XMLConstants.HOMEPAGE, contributor);
        addCDATAElement(contr.getAddress(), XMLConstants.ADDRESS, contributor);
        addCDATAElement(contr.getInstitution(), XMLConstants.INSTITUTION, contributor);
        addCDATAElement(contr.getPhone(), XMLConstants.PHONE, contributor);
        addCDATAElement(contr.getFax(), XMLConstants.FAX, contributor);
        addCDATAElement(contr.getNotes(), XMLConstants.NOTES, contributor);
        if (encodePermissions) {
        	writeOutPermissionsForContributor(contr, contributor);
        }
        return contributor;        
        
    }
    
    protected abstract void writeOutPermissionsForContributor(Contributor contr, Element contrElement);
    
    protected void addCDATAElement(String value, String tagName, Element parentElement) {
        if (value != null) {
            Element newElt = new Element(tagName);
            newElt.addContent(getCDATA(value));
            parentElement.addContent(newElt);
        }
    }
    
    /**
     * Adds page details to the passed-in node
     *
     * @param node The node to read page information from
     * @return A new Page element
     */
    private Element addPageContent(Node node, Set imgs, Set contributors, Contributor contr, boolean includeContributors) {
        Iterator it;
        Page page = getPageForNode(node);
        Element pageElmt = null;
        
        if(page != null) {
            pageElmt = constructPageElement(page, imgs, contributors, node, contr, includeContributors);
        }
        
        if ( pageElmt == null ||
             ( (pageElmt.getChildren() == null || pageElmt.getChildren().size() == 0 )
              && ( pageElmt.getAttributes()== null || pageElmt.getAttributes().size()==0 ) ) ) {
            pageElmt = null;
        }
        return pageElmt;
    }
    
    public Element constructPageElement(Page page, Set imgs, Set contributors, Node node, Contributor contr, 
    		boolean includeContributors) {
        Element pageElmt = new Element(XMLConstants.PAGE);     
        if (saveToFile()) {
            pageElmt.setAttribute(XMLConstants.NEW_FROM_SERVER, page.isNewFromServer() ? XMLConstants.TRUE : XMLConstants.FALSE);
            pageElmt.setAttribute(XMLConstants.REFERENCESCHANGED, page.referencesChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
            pageElmt.setAttribute(XMLConstants.INTERNETLINKSCHANGED, page.internetLinksChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
            pageElmt.setAttribute(XMLConstants.CHANGED, page.changedFromServer() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }

        Element refs = new Element(XMLConstants.REFERENCES);
        refs.addContent(getCDATA(page.getReferences()));
        pageElmt.addContent(refs);            

        Element links = new Element(XMLConstants.INTERNETLINKS);
        links.addContent(getCDATA(page.getInternetLinks()));
        pageElmt.addContent(links);
        

        Element imagelist = new Element(XMLConstants.IMAGELIST);
        pageElmt.addContent(imagelist);
        addTitleIllustrations(imagelist, page, imgs);

        if (saveToFile()) {
            pageElmt.setAttribute(XMLConstants.IMAGECHANGED, page.getImageChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }
        
        Element pretreetext = new Element(XMLConstants.PRETREETEXT);
        pageElmt.addContent(pretreetext);
        if(page.getLeadText() != null) {
            String fixedText = getFixedLocalFileUrl(page.getLeadText());
            page.setLeadText(fixedText);
            pretreetext.addContent(getCDATA(fixedText));
        }

        if (saveToFile()) {
            pageElmt.setAttribute(XMLConstants.PRETREETEXTCHANGED, page.getLeadTextChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }
        
        Element posttreetext = new Element(XMLConstants.POSTTREETEXT);
        pageElmt.addContent(posttreetext);
        if(page.getPostTreeText() != null) {
            String fixedText = getFixedLocalFileUrl(page.getPostTreeText());
            page.setPostTreeText(fixedText);
            posttreetext.addContent(getCDATA(fixedText));
        }            
        if (saveToFile()) {
            pageElmt.setAttribute(XMLConstants.POSTTREETEXTCHANGED, page.getPostTreeTextChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }

        Iterator it = getTextSectionsForPage(page).iterator();
        
        Element textlist = new Element(XMLConstants.TEXTLIST);
        pageElmt.addContent(textlist);
        int counter = 0;
        while(it.hasNext()) {
            TextSection tempTextSection = (TextSection) it.next();
            Element textsection = new Element(XMLConstants.TEXTSECTION);
            counter++;
            
            Element heading = new Element(XMLConstants.HEADING);
            String headingStr = tempTextSection.getHeading();
            heading.addContent(getCDATA(headingStr));
            textsection.addContent(heading);
            
            textsection.setAttribute(XMLConstants.SEQUENCE, "" + counter);
            textsection.setAttribute(XMLConstants.INDENT, headingStr.equalsIgnoreCase(XMLConstants.REFERENCES) ? XMLConstants.ONE : XMLConstants.ZERO);
            textsection.setAttribute(XMLConstants.CHANGED, tempTextSection.changedFromServer() ? XMLConstants.TRUE : XMLConstants.FALSE );
            
            Element text = new Element(XMLConstants.TEXT);
            String fixedText = getFixedLocalFileUrl(tempTextSection.getText());
            tempTextSection.setText(fixedText);
            
            text.addContent(getCDATA(fixedText));
            textsection.addContent(text);
            
            textlist.addContent(textsection);
            doAdditionalTextSectionProcessing(tempTextSection, imgs);
        }

        if (saveToFile()) {
            pageElmt.setAttribute(XMLConstants.LISTCHANGED, page.getListChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }
        if (node != null) {
            it = getAccessoryPagesForPage(page, node).iterator();
        } else {
            it = new ArrayList().iterator();
        }
        Element accessoryList = new Element(XMLConstants.ACCESSORYPAGES);
        pageElmt.addContent(accessoryList);
        counter = 0;
        while(it.hasNext()) {
            AccessoryPage accPage = (AccessoryPage) it.next();
            accessoryList.addContent(encodeAccessoryPage(accPage, contr));            
            //doAdditionalAccessoryPageProcessing(accPage, imgs);
        }
        pageElmt.setAttribute(XMLConstants.STATUS, page.getStatus());
        if (saveToFile()) {
            pageElmt.setAttribute(XMLConstants.STATUSCHANGED, page.getStatusChanged() || page.isNewFromServer() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }
        
        pageElmt.setAttribute(XMLConstants.WRITEASLIST, page.writeAsList() ? XMLConstants.ONE : XMLConstants.ZERO);
        if (saveToFile() ) {
            pageElmt.setAttribute(XMLConstants.WRITECHANGED, page.getWriteChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }        
        Element note = new Element(XMLConstants.ACKS);
        pageElmt.addContent(note);        
        if(page.getAcknowledgements() != null) {

            String fixedNote = getFixedLocalFileUrl(page.getAcknowledgements());
            page.setAcknowledgements(fixedNote);
            note.addContent(getCDATA(fixedNote));
        }
        if (saveToFile() ) {
            pageElmt.setAttribute(XMLConstants.ACKSCHANGED, page.getAcksChanged() ? XMLConstants.TRUE : XMLConstants.FALSE );
        }
    
        writeOutCopyrightInfo(page, pageElmt);          
        if (includeContributors) {
        	writeOutContributors(page, null, pageElmt, contributors);
        }
        if (saveToFile()) {
            pageElmt.setAttribute(XMLConstants.CONTRIBUTORCHANGED, page.getContributorsChanged() ? XMLConstants.TRUE : XMLConstants.FALSE);
        }        
        return pageElmt;
    }
    
    public Element encodeAccessoryPage(AccessoryPage accPage, Contributor contr) {
        Element accPageElem = new Element(XMLConstants.ACCESSORYPAGE);

        Element pageTitleElem = new Element(XMLConstants.PAGETITLE);
        pageTitleElem.addContent(getCDATA(accPage.getPageTitle()));
        accPageElem.addContent(pageTitleElem);              
        accPageElem.setAttribute(XMLConstants.ID, accPage.getId() + "");
        accPageElem.setAttribute(XMLConstants.IS_ARTICLE, accPage.getIsArticle() ? XMLConstants.ONE : XMLConstants.ZERO);
        accPageElem.setAttribute(XMLConstants.IS_TREEHOUSE, accPage.getIsTreehouse() ? XMLConstants.ONE : XMLConstants.ZERO);
        writeOutPermissionForAccPage(accPageElem, accPage, contr);
        return accPageElem;
    }
    
    protected void writeOutPermissionForAccPage(Element accPageElmt, AccessoryPage accPage, Contributor contr) {
        accPageElmt.setAttribute(XMLConstants.PERMISSION, accPage.getIsEditable() ? XMLConstants.ONE : XMLConstants.ZERO);
    }
    
    protected void addTitleIllustrations(Element imagelist, Page page, Set images) {
        /*Iterator it = page.getImageList().iterator();
        if(it.hasNext()) {
            int counter = 0;
            while(it.hasNext()) {
                Element image = new Element(XMLConstants.IMAGE);                        
                PageImage img = (PageImage) it.next();
                String fixedUrl = getFixedLocalFileUrl(img);
                img.setImageUrl(fixedUrl);
                String dbPrefix = Controller.getController().getWebPath();
                // In this case, we don't want to write out the database prefix,
                // since the imgs are the same regardless of which host we are
                // using
                if (fixedUrl != null && fixedUrl.startsWith(dbPrefix)) {
                    fixedUrl = fixedUrl.substring(dbPrefix.length()-1);
                }
                if(fixedUrl != null) {
                    image.setAttribute(XMLConstants.URL, fixedUrl);
                }
                image.setAttribute(XMLConstants.ORDER, "" + counter++);
                if (img.getImageId() != null) {
                    image.setAttribute(XMLConstants.IMAGEID, img.getImageId());
                }
                imagelist.addContent(image);
            }
        }*/   	
    }
    
    private void writeOutContributors(Page page, AccessoryPage accPage, Element parentElmnt, Set contributors) {
        Iterator it;
        if (page != null) {
            it = getContributorsForPage(page).iterator();
        } else {
            it = getContributorsForAccessoryPage(accPage).iterator();
        }

        Element contrlist = new Element(XMLConstants.CONTRIBUTORLIST);
        parentElmnt.addContent(contrlist);
        int counter = 0;
        while(it.hasNext()) {
            PageContributor contr = (PageContributor)it.next();
            // Update the contributor id if a new contributor was set up
            getFixedLocalContributorId(contr);
            Element contrElmt = new Element(XMLConstants.CONTRIBUTOR);
            contrlist.addContent(contrElmt);

            contrElmt.setAttribute(XMLConstants.ID, contr.getContributorId() + "");
            contrElmt.setAttribute(XMLConstants.ORDER, "" + counter++);

            if(contr.getIsContact()) {
                contrElmt.setAttribute(XMLConstants.CONTACT, XMLConstants.ONE);
            } else {
                contrElmt.setAttribute(XMLConstants.CONTACT, XMLConstants.ZERO);
            }

            if (contr.getIsAuthor()) {
                contrElmt.setAttribute(XMLConstants.IS_AUTHOR, XMLConstants.ONE);
            } else {
                contrElmt.setAttribute(XMLConstants.IS_AUTHOR, XMLConstants.ZERO);
            }

            if (contr.getIsCopyOwner()) {
                contrElmt.setAttribute(XMLConstants.COPYRIGHT, XMLConstants.ONE);
            } else {
                contrElmt.setAttribute(XMLConstants.COPYRIGHT, XMLConstants.ZERO);
            }
            if (contributors != null) {
            	contributors.add(new Integer(contr.getContributorId()));
            }
        }    
    }
    
    /**
     * Returns the document contructed during XML writing
     *
     * @return The document contructed during XML writing
     */
    public Document getDocument() {
        return m_WebDocument;
    }
    
    /** 
     *Decides if the entire Ancestors document-subtree needs to be written out.
     *If to file, yes (default). If to server, no (overridden in URLXMLRWriter)
     */
    protected boolean writeOutAncestors () {
        return true;
    }
    
    protected boolean writeOutContributors() {
        return true;
    }
    
    protected boolean writeOutImages() {
        return true;
    }
    
    protected CDATA getCDATA(String value) {
        return new CDATA(value);
    }
}



