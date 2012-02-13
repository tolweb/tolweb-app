/*
 * Created on Dec 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.main;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.tolweb.base.http.BaseHttpRequestMaker;
import org.tolweb.treegrow.page.Page;
import org.tolweb.treegrow.page.TextSection;
import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.OtherName;
import org.tolweb.treegrow.tree.Tree;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreeGrowXMLReader extends XMLReader {
    protected String treeStructure;
    protected ArrayList nodeList;
    protected ArrayList ancestorsList;
    protected ArrayList nodeImagesList;
    protected ArrayList editableContributorsList = new ArrayList();
    private boolean isLocal;
    private long modifiedDate = -1, downloadDate = -1, uploadDate = -1;
    String lockTime ;
    String lockUser ;
    String lockIP ;
    private String url = "";
    private boolean isRootTree;
    
    public static final String USERNAME = "ToL";
    public static final String PASSWORD = "develop";
    public static final String REALM = "TreeGrow Users Only"; 
    
    public TreeGrowXMLReader() {}
	
    /**
     * Constructs an XML reader to read from the specified URL and timeout
     * after the passed-in number of seconds
     * @param urlForDoc The URL to read from
     * @param isRootTree TODO
     * @param timeOut The number of seconds to wait before thinking the 
     *        connection is dead
     */
    public TreeGrowXMLReader(URL urlForDoc, boolean isRootTree) {
        this(urlForDoc, null, isRootTree);
    }
    
    public TreeGrowXMLReader(URL urlForDoc, Node n, boolean isRootTree) {
        url = urlForDoc.toString();
        node = n;
        try {
            ByteArrayInputStream stream = fetchInputStream(urlForDoc, n == null);
            if (stream != null) {
                constructMe(new SAXBuilder().build(stream), isRootTree);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorString = "Jdom error : " + e.getMessage();
            errorNum = -1;
        }
    }
    
    public TreeGrowXMLReader(String docLocation, boolean isRootTree) {
        this(docLocation, null, isRootTree);
    }
    
    public TreeGrowXMLReader(String docLocation, Node n, boolean isRootTree)  {
        isLocal = true;
        node = n;
        try {
            constructMe(new SAXBuilder().build(new File(docLocation)), isRootTree);
        } catch (Exception e) {
            errorString = "Jdom error : " + e.getMessage();
            errorNum = -1;
        }
    }
    

    public String getURL () {
        return url;
    }    
    
    /** Creates a new instance of XmlReader */
    private void constructMe(Document doc, boolean isRoot) {
    	isRootTree = isRoot;
        xmlDocument = doc;
        rootElement = doc.getRootElement();
        boolean result = UserValidationChecker.checkValidation(rootElement);
        if (!result) {
            errorString = "Validation failed";
            errorNum = -1;
        }
        
        Controller controller = Controller.getController();
        String attrString = rootElement.getAttributeValue(XMLConstants.MODIFIEDDATE);
        if (attrString != null) {
            modifiedDate = Long.valueOf(attrString).longValue();
        }

        attrString = rootElement.getAttributeValue(XMLConstants.DOWNLOADDATE);
        if (attrString != null) {
            downloadDate = Long.valueOf(attrString).longValue();
        } else {
            downloadDate = System.currentTimeMillis();            
        }
        
        attrString = rootElement.getAttributeValue(XMLConstants.UPLOADDATE);
        if (attrString != null) {
            uploadDate = Long.valueOf(attrString).longValue();
        }
        
        attrString = rootElement.getAttributeValue(XMLConstants.BATCHID);
        if (attrString != null) {
            controller.setEditorBatchId(attrString);
        }
         
        
        Element errDescElem = rootElement.getChild(XMLConstants.ERRORTEXT);
        
        if ( errDescElem != null) {
            errorString = errDescElem.getTextTrim();
            Element errNumElem = rootElement.getChild(XMLConstants.ERRORNUM);
            if (errNumElem != null) {
                Integer num = new Integer(errNumElem.getTextTrim());
                errorNum = num.intValue();
                if ( errorNum == 10102  || errorNum == 10103  ) { //locked or submitted
                    Element lockElem = rootElement.getChild(XMLConstants.LOCK_INFO);
                    
                    if (lockElem != null) {
                        Attribute timeAttr = lockElem.getAttribute(XMLConstants.DATE_TIME);
                        if (timeAttr!=null) {
                            lockTime = timeAttr.getValue();
                        }
                        
                        Attribute userAttr = lockElem.getAttribute(XMLConstants.USER);
                        if (userAttr!=null) {
                            lockUser = userAttr.getValue();
                        }                        
                    }
                }
            }
            
            return;
        }
        
        
        fetchDownloadId();
        fetchTreeStructure();
        fetchFileName();
        fetchNodeId();
        fetchDepth();
        fetchObsoleteMessage();
        nodeList = new ArrayList();
        ancestorsList = new ArrayList();
        nodeImagesList = new ArrayList();
        //fetchEditableContributors();
        //fetchImages();
    } 

	protected void doAdditionalNodeProcessing(Node node) {
    	nodeList.add(node);
    }
    
    protected void setNodeRankForNode(Node node, int nodeRank) {
    	node.setNodeRank(nodeRank);    	
    }
    
    protected Collection getSynonymCollectionInstance() {
    	return new Vector();
    }
    
    protected void setOtherNamesForNode(Node node, Collection syns) {
    	Collections.sort((List) syns);
    	node.setOtherNames((Vector) syns);
    }
    
    protected void setAuthorityDateForNode(Node node, int date) {
    	node.setNameDate(date);
    }
    
    protected OtherName getOtherNameInstanceForNode(Node node) {
    	return new OtherName(node);
    }
    
    
    protected void setYearForOtherName(OtherName name, int year) {
    	name.setDate(year);
    }
    
    protected void setOrderForOtherName(OtherName name, int order) {
    	name.setOrder(order);
    }    
    
    protected void decodeImageListElement(Element imageListElem, Page tempPage) {
        /*List imageList = imageListElem.getChildren();
        Vector tempImageVector=new Vector();
        Iterator it = imageList.iterator();
        while (it.hasNext()) {
            Element imageElem = (Element)it.next();
            PageImage img = new PageImage(tempPage);
            String imgPath = imageElem.getAttributeValue(XMLConstants.URL);
            String hostName = Controller.getController().getWebPath();
            hostName = hostName.substring(0, hostName.length() - 1);
            String urlString;
            if (imgPath != null && imgPath.startsWith("/")) {
                urlString = hostName + imgPath;
            } else {
                urlString = imgPath;
            }
            String orderString = imageElem.getAttributeValue(XMLConstants.ORDER);
            String imageId = imageElem.getAttributeValue(XMLConstants.IMAGEID);
            img.setImageUrl(urlString);
            img.setOrder(new Integer(orderString).intValue());
            img.setImageId(imageId);
            tempImageVector.add(img);
        }
        
        Collections.sort(tempImageVector);
        tempPage.setImageList(tempImageVector);*/    	
    }
    
    
    private void fetchImages() {
        Element imagesElt = rootElement.getChild(XMLConstants.IMAGELIST);
        if (imagesElt != null) {
            Iterator it = imagesElt.getChildren(XMLConstants.IMAGE).iterator();
            while (it.hasNext()) { 
                Element imageElt = (Element) it.next();
                NodeImage img = getNodeImageFromElement(imageElt);
                nodeImagesList.add(img);
            }
        }
    }
    
    public int fetchDownloadId() {
    	int result = super.fetchDownloadId();
    	if (isRootTree) {
    		Controller.getController().setDownloadId(result);
    	}
    	return result;
    }
    
    private void fetchObsoleteMessage() {
    	Element obsoleteElem = rootElement.getChild(XMLConstants.OBSOLETEMESSAGE);
    	if (obsoleteElem != null) {
    		String obsoleteMessage = obsoleteElem.getTextTrim();
    		if (StringUtils.notEmpty(obsoleteMessage)) {
    			Controller.getController().setObsoleteMessage(obsoleteMessage);
    		}
    	}
    }
    
    private int fetchDepth() {
        Element depthElem = rootElement.getChild(XMLConstants.DEPTH);
        if (depthElem != null) {
            String depthStr = depthElem.getTextTrim();
            if (StringUtils.notEmpty(depthStr)) {
                depth = new Integer(depthStr).intValue();
                // If the depth is 0, then it's a single node's info being
                // read, so don't set the depth for the whole tree.
                if (depth > 0) {
                    Controller.getController().setDepth(depth);
                    
                }
                return 1;
            }
        }
        
        return -1;
    }
    
    private int fetchFileName() {
        Element fileNameElem = rootElement.getChild(XMLConstants.FILENAME);
        if (fileNameElem != null) {
            String fileName = fileNameElem.getTextTrim();
            if (fileName!=null && fileName!="") {
                Controller.getController().setFileName(fileName);
                return 1;
            }
        }
        
        return -1;
    }
    
    private int fetchNodeId() {
        try {
            String idString = rootElement.getChild(XMLConstants.NODES).getChild(XMLConstants.NODE).getAttribute(XMLConstants.ID).getValue();
            if (idString != null) {
                Controller.getController().setNodeId(new Integer(idString).intValue());
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }  
    
    
    private int fetchFileChanged() {
        Element fileChangedElem = rootElement.getChild(XMLConstants.FILECHANGED);
        if (fileChangedElem != null) {
            String fileChangedStr = fileChangedElem.getTextTrim();
            if (fileChangedStr != null && fileChangedStr != "") {
                boolean fileChanged = fileChangedStr.equals(XMLConstants.TRUE);
                Controller.getController().setFileChanged(fileChanged);
                return 1;
            }
        }
        
        return -1;
    }    
    
    
    /**
     * Method to open file and fetch data common to the entire tree.  This includes
     * the downloadId, fileName, depth, and changed properties.
     *
     * @return true If no problems were encountered opening the file and reading
     *         the metadata properties, false otherwise.
     */
    public boolean fetchTreeMetadata() {
        int filenameOk, downloadIdOk, depthOk, changedOk, nodeIdOk;
        filenameOk = fetchFileName();
        downloadId = fetchDownloadId();
        depthOk = fetchDepth();
        changedOk = fetchFileChanged();
        nodeIdOk = fetchNodeId();
        //return filenameOk == 1 && downloadId > 0 && depthOk == 1 && changedOk == 1;
        return true;
    }    
    
    private int fetchTreeStructure()  {
        Element treeStrucElem = rootElement.getChild(XMLConstants.TREESTRUCTURE);
        if (treeStrucElem != null) {
            treeStructure = treeStrucElem.getTextTrim();
            if (treeStructure!=null && treeStructure!="") {
                return 1;
            }
        }
        
        return -1;
    } 
    
    private void fetchEditableContributors() {
        Controller controller = Controller.getController();
        Element contributorElt = rootElement.getChild(XMLConstants.CONTRIBUTORLIST);

        if (contributorElt != null) {
            List contributors = contributorElt.getChildren(XMLConstants.CONTRIBUTOR);
            if (contributors != null) {
                Iterator it = contributors.iterator();
                while (it.hasNext()) {
                    Element contrElt = (Element) it.next();
                    Contributor contr = getContributorFromElement(contrElt);
                    editableContributorsList.add(contr);
                }
            }
        }
    }    
    
    public ArrayList getEditableContributors() {
        return editableContributorsList;
    }
    
    public ArrayList getImageList() {
        return nodeImagesList;
    }
    
    
    /**
     * Constructs and returns a new tree object based on the node list and tree
     * structure already existing in the reader.
     *
     * @return A new Tree object
     */
    public Tree getTree() {
        Tree tree = new Tree(getTreeStructure(), getNodeList());
        tree.setDownloadDate(downloadDate);
        tree.setUploadDate(uploadDate);
        tree.setModifiedDate(modifiedDate);
        
        return tree;
    }
    
    public long getDownloadDate() {
        return downloadDate;
    }
    
    public long getUploadDate() {
        return uploadDate;
    }
    
    public long getModifiedDate() {
        return modifiedDate;
    } 
    
    /**
     * Returns the String that describes the relationships between nodes in the
     * tree
     *
     * @return the String that describes the relationships between nodes in the
     *         tree
     */
    public String getTreeStructure() {
        return treeStructure;
    }
    
    /**
     * Returns the list of nodes constructed during reading
     * 
     * @return the list of nodes constructed during reading
     */
    public ArrayList getNodeList() {
        return nodeList;
    } 
    
    /**
     * Returns when this tree was locked (if it is)
     *
     * @return When this tree was locked (if it is)
     */
    public String getLockTime() {
        return lockTime;
    }
    
    /**
     * Returns the user who locked the tree (if it is)
     *
     * @return The user who locked the tree (if it is)
     */
    public String getLockUser() {
        return lockUser;
    }
    
    /**
     * Attempts to download the XML from the passed-in url.  If there is found
     * to be no activity over a 5-second stretch, then an error is raised
     *
     * @param urlForDoc The URL to try and read XML from
     * @param additionalWait Whether or not to wait more at the beginning of 
     *        of the download.  This is used for an initial download since the
     *        server may take longer than the default wait time to write out a 
     *        response
     * @return A ByteArrayInputStream for the bytes downloaded
     */
    private ByteArrayInputStream fetchInputStream(URL urlForDoc, boolean additionalWait) {
        try {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(USERNAME, PASSWORD);
            byte[] bytes = BaseHttpRequestMaker.makeHttpRequest(urlForDoc.toString(), credentials, REALM);
            if (bytes == null) {
                throw new RuntimeException();
            }
            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
        }
        errorString = "Server error";
        errorNum = -1;
        return null;
    }

	/* (non-Javadoc)
	 * @see org.tolweb.treegrow.main.XMLReader#decodeTextListElement(org.jdom.Element, org.tolweb.treegrow.page.Page)
	 */
	protected void decodeTextListElement(Element textListElement, Page tempPage) {
		// TODO Auto-generated method stub
        List textSectionList = textListElement.getChildren();
        Vector tempTextVector = new Vector();
        
        Iterator it = textSectionList.iterator();
        while (it.hasNext()) {
            Element textSectionElem = (Element)it.next();
            TextSection tempTextSection = new TextSection(tempPage);
            
            String attribValue = textSectionElem.getAttributeValue(XMLConstants.CHANGED);
            tempTextSection.setChangedFromServer( attribValue!=null  && attribValue.equals(XMLConstants.TRUE) );
            
            Element headingElem = textSectionElem.getChild(XMLConstants.HEADING);
            Element textElem = textSectionElem.getChild(XMLConstants.TEXT);
            String tempOrderString = textSectionElem.getAttributeValue(XMLConstants.SEQUENCE);
            
            if (headingElem != null) {
                tempTextSection.setHeading(headingElem.getTextTrim());
            }
            if(textElem != null) {
                tempTextSection.setText(textElem.getTextTrim());
            }
            tempTextSection.setOrder(new Integer(tempOrderString).intValue());
            
            tempTextVector.add(tempTextSection);
        }
        Collections.sort(tempTextVector);
        tempPage.setTextList(tempTextVector);		
	}

	/* (non-Javadoc)
	 * @see org.tolweb.treegrow.main.XMLReader#getCollectionInstanceForPageContributors()
	 */
	protected Collection getCollectionInstanceForPageContributors() {
		// TODO Auto-generated method stub
		return new Vector();
	}

	/* (non-Javadoc)
	 * @see org.tolweb.treegrow.main.XMLReader#setPageContributorsForPage(org.tolweb.treegrow.page.Page, java.util.Collection)
	 */
	protected void setPageContributorsForPage(Page page, Collection pageContributors) {
		// TODO Auto-generated method stub
        Collections.sort((List) pageContributors);
		page.setContributorList((Vector) pageContributors);
	}
	
	public static UploadBatch getUploadBatchFromElement(Element batchElmt) {
        ArrayList pageNames = new ArrayList();
        ArrayList downloadIDs = new ArrayList(); 
        int batchId = Integer.parseInt(batchElmt.getAttributeValue(XMLConstants.BATCHID));
        int rootGroupId = Integer.parseInt(batchElmt.getAttributeValue(XMLConstants.ROOT_GROUP_ID));
        boolean submitted = Integer.parseInt(batchElmt.getAttributeValue(XMLConstants.SUBMITTED)) == 1;
        boolean checkedOut = Integer.parseInt(batchElmt.getAttributeValue(XMLConstants.CHECKED_OUT_FILE)) == 1;
        String lastUser = batchElmt.getAttributeValue(XMLConstants.LAST_USER);
        String lastDate = batchElmt.getAttributeValue(XMLConstants.LAST_DATE);                
        String rootGroup = batchElmt.getChild(XMLConstants.ROOT_GROUP).getTextTrim();
        String pushPublicStr = batchElmt.getAttributeValue(XMLConstants.CAN_PUSH_PUBLIC);
        String hasOther = batchElmt.getAttributeValue(XMLConstants.OTHER_ACTIVE_DOWNLOAD);
        boolean canPushPublic = pushPublicStr != null && !pushPublicStr.equals(XMLConstants.ZERO);
        String isSoleAuthorStr = batchElmt.getAttributeValue(XMLConstants.IS_SOLE_AUTHOR);
        boolean isSoleAuthor = isSoleAuthorStr != null && !isSoleAuthorStr.equals(XMLConstants.ZERO);
        boolean hasOtherActiveDownload = hasOther != null && hasOther.equals(XMLConstants.ONE);
        Iterator it2;
        if (checkedOut) {
            it2 = batchElmt.getChild(XMLConstants.DOWNLOADS).getChildren(XMLConstants.DOWNLOAD_ID).iterator();
            while (it2.hasNext()) {
                Element downIDElem = (Element) it2.next();
                downloadIDs.add( new Integer(downIDElem.getTextTrim()) );
            }         
        }
        
        it2 = batchElmt.getChild(XMLConstants.PAGES).getChildren(XMLConstants.PAGE).iterator();
        while (it2.hasNext()) {
            Element page = (Element) it2.next();
            if (page.getTextTrim().equals(rootGroup)) {
                pageNames.add(0, page.getTextTrim());
            } else {
                pageNames.add(page.getTextTrim());
            }
        }                
        
        UploadBatch batch = new UploadBatch(batchId, rootGroupId, lastUser, rootGroup, lastDate, pageNames, downloadIDs, submitted, checkedOut, canPushPublic, isSoleAuthor, hasOtherActiveDownload);
        return batch;
	}
	
	public static FileMetadata parseFileMetadataFromFile(String fileName) {
        long longVal;
        DateFormat formatter = Controller.getController().getDateFormatter();
        String name = "", downloadDate = "", modifiedDate = "", uploadDate = "", rootNodeName = "", editingBatchId = "";
        try {
	        Element root = new SAXBuilder().build(new File(fileName)).getRootElement();
	        name = root.getChildText(XMLConstants.FILENAME);
	        
	        // Find the root node's name in addition to the filename since they
	        // may have changed the name of the root node
	        Element rootNode = root.getChild(XMLConstants.NODES).getChild(XMLConstants.NODE);
	        rootNodeName = rootNode.getChildText(XMLConstants.NAME);
	        
	        editingBatchId = root.getAttributeValue(XMLConstants.BATCHID);
	        
	        longVal = Long.parseLong(root.getAttributeValue(XMLConstants.DOWNLOADDATE));
	        downloadDate = formatter.format(new Date(longVal));
	        
	        longVal = Long.parseLong(root.getAttributeValue(XMLConstants.MODIFIEDDATE));
	        modifiedDate = formatter.format(new Date(longVal));
	        
	        longVal = Long.parseLong(root.getAttributeValue(XMLConstants.UPLOADDATE));
	        // If it hasn't been uploaded, then just leave the empty string as the default
	        if (longVal != -1) {
	            uploadDate = formatter.format(new Date(longVal));
	        }
	        
	        String rootIdString = root.getChildText(XMLConstants.NODEID);
	        int downloadId = Integer.parseInt(root.getChildText(XMLConstants.DOWNLOAD_ID));
	        int nodeId = Integer.parseInt(rootIdString);
	        boolean changed = root.getChildText(XMLConstants.FILECHANGED).equals(XMLConstants.TRUE);
	        FileMetadata newMetadata = new FileMetadata(rootNodeName, name, fileName, uploadDate, modifiedDate, downloadDate, editingBatchId, downloadId, nodeId, changed);
	        return newMetadata;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
}
