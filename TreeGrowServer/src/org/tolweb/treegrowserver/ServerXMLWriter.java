/*
 * Created on Nov 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.hibernate.TitleIllustration;
import org.tolweb.misc.BaseTextPreparer;
import org.tolweb.misc.ImageUtils;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrow.main.XMLWriter;
import org.tolweb.treegrow.page.AccessoryPage;
import org.tolweb.treegrow.page.Page;
import org.tolweb.treegrow.page.TextSection;
import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.OtherName;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

import com.megginson.sax.DataWriter;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServerXMLWriter extends XMLWriter {
	private NodeDAO nodeDAO;
	private PageDAO pageDAO;
	private AccessoryPageDAO accessoryPageDAO;
	private ContributorDAO contributorDAO;
	private ImageDAO imageDAO;
	private ImageUtils imageUtils;
	private BaseTextPreparer textPreparer;
	private PermissionChecker permissionChecker;
	public static final SimpleDateFormat DATEFORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    
    public Document getBadVersionDocument() {
        Element errorElement = new Element(XMLConstants.ERROR);
        errorElement.setAttribute(XMLConstants.NEW_VERSION, XMLConstants.ONE);
        Document doc = new Document(errorElement);
        return doc;
    }
	
	public Element constructAndFillOutNodeElement(MappedNode node, boolean isComplete, Set imgs, Set contributors, Contributor contr, 
			boolean includeContributors) {
		Element nodeElement = constructNodeElement();
		fleshOutNode(node, nodeElement, isComplete, imgs, contributors, contr, includeContributors);
		if (isComplete) {
		    List imgsForNode = imageDAO.getImagesAttachedToNode(node.getNodeId());
		    for (Iterator iter = imgsForNode.iterator(); iter.hasNext();) {
                NodeImage img = (NodeImage) iter.next();
                imgs.add(new Integer(img.getId()));
            }
		}
		return nodeElement;
	}
	
	protected void writeOutCopyrightInfo(Page page, Element pageElement) {
	    Element copyrightElement = new Element(XMLConstants.COPYRIGHT);
	    pageElement.addContent(copyrightElement);
	    Element copyYearElement = new Element(XMLConstants.DATE);
	    copyrightElement.addContent(copyYearElement);
	    copyYearElement.setText(((MappedPage) page).getCopyrightDate());
	    Element copyHolderElement = new Element(XMLConstants.HOLDER);
	    copyrightElement.addContent(copyHolderElement);
	    copyHolderElement.setText(((MappedPage) page).getCopyrightHolder());
	}
	
	protected String getFirstOnlineStringForPage(Page page) {
	    Date date = ((MappedPage) page).getFirstOnlineDate();
	    if (date != null) {
	        return DATEFORMATTER.format(date);
	    } else {
	        return "0000-00-00";
	    }
	}
	
	public int getNodeChildcount(Node node) {
		return getNodeDAO().getNumChildrenForNode((MappedNode) node).intValue();
	}
	
	public boolean getNodeHasPage(Node node) {
		return getPageDAO().getNodeHasPage((MappedNode) node);
	}
	
	public boolean saveToFile() {
		return false;
	}
	
	protected boolean outputDocument(Document outDoc, Node root) {
		return true;
	}
	
	protected int getDateForOtherName(OtherName otherName) {
	    Integer dateInteger = ((MappedOtherName) otherName).getAuthorityYear();
	    if (dateInteger != null) {
	        return dateInteger.intValue();
	    } else {
	        return 0;
	    }
	}
	
	protected Collection getOtherNamesForNode(Node node) {
		return node.getSynonyms();
	}
	
	protected Page getPageForNode(Node node) {
		return getPageDAO().getPageForNode((MappedNode) node);
	}
	
	protected Collection getTextSectionsForPage(Page page) {
	    Collection textSections = ((MappedPage) page).getTextSections(); 
		return textSections;
	}
	
	protected Collection getAccessoryPagesForPage(Page page, Node node) {
		return getAccessoryPageDAO().getAccessoryPagesForNode((MappedNode) node);
	}
	
	protected Collection getContributorsForPage(Page page) {
		return ((MappedPage) page).getContributors();
	}
	
	protected Collection getContributorsForAccessoryPage(AccessoryPage accPage) {
		return ((MappedAccessoryPage) accPage).getContributors();
	}
	
	protected int getNameDateForNode(Node node) {
	    Integer authDate = ((MappedNode) node).getAuthorityDate();
	    if (authDate != null) {
	        return authDate.intValue();
	    } else {
	        return 0;
	    }
	}
	
	protected int getNodeRankForNode(Node node) {
	    Integer nodeRank = ((MappedNode) node).getNodeRankInteger();
	    if (nodeRank != null) {
	        return nodeRank.intValue();
	    } else {
	        return 0;
	    }
	}
	
	protected int getNodeIdForNode(Node node) {
	    return ((MappedNode) node).getNodeId().intValue();
	}
	
	protected void addTitleIllustrations(Element imagelist, Page page, Set imgs) {
		Iterator it = ((MappedPage) page).getTitleIllustrations().iterator();
		int counter = 0;
		while (it.hasNext()) {
			TitleIllustration nextIllustration = (TitleIllustration) it.next();
			Element imageElement = new Element(XMLConstants.IMAGE);
			imagelist.addContent(imageElement);
			String locString = nextIllustration.getImage().getLocation();
			imageElement.setAttribute(XMLConstants.URL, getImageUtils().getUrlPrefix() + locString);
			imageElement.setAttribute(XMLConstants.IMAGEID, "" + nextIllustration.getImage().getId());
			imageElement.setAttribute(XMLConstants.ORDER, counter++ + "");
			imgs.add(new Integer(nextIllustration.getImage().getId()));
		}
	}
	
	protected void doAdditionalTextSectionProcessing(TextSection textSection, Set imgs) {
		if (imgs != null) {
			imgs.addAll(textPreparer.getImagesInText(textSection.getText()));
		}
	}	
	
	protected void doAdditionalAccessoryPageProcessing(AccessoryPage accPage, Set imgs) {
		if (imgs != null) {
			imgs.addAll(textPreparer.getImagesInText(accPage.getText()));
		}
	}
	
	protected void writeOutNodesForImage(NodeImage img, Element nodesElmt) {
	    Iterator it = img.getNodesSet().iterator();
	    while (it.hasNext()) {
	        MappedNode node = (MappedNode) it.next();
	        Element element = constructNodeElement();
	        element.setAttribute(XMLConstants.ID, node.getNodeId().toString());
	        addCDATAElement(node.getName(), XMLConstants.NAME, element);
	        nodesElmt.addContent(element);
	    }
	}
	
	protected void writeOutVersionsForImage(NodeImage img, Element versionsElement) {
	    List versions = getImageDAO().getVersionsForImage(img);
	    Collections.sort(versions);
	    Integer lastHeightInteger = new Integer(0);
	    for (Iterator iter = versions.iterator(); iter.hasNext();) {
            ImageVersion version = (ImageVersion) iter.next();
            // If the previous version had the same height as us, that
            // means that there is a custom version of the same height
            // and we are autogenerated, so don't send ourselves down.
            if (!version.getHeight().equals(lastHeightInteger)) {
                versionsElement.addContent(encodeImageVersion(version));
            }
        }
	}
	
	protected void writeOutPermissionsForContributor(Contributor contr, Element contributor) {
	    List permissions = getContributorDAO().getNodeIdsForContributor(contr);
	    Iterator it = permissions.iterator();
	    while (it.hasNext()) {
	        Long nextNodeId = (Long) it.next();
	        try {
		        String nodeName = getNodeDAO().getNameForNodeWithId(nextNodeId);
	            Element permission = new Element(XMLConstants.PERMISSION);
	            permission.setAttribute(XMLConstants.ID, nextNodeId.toString());
	            addCDATAElement(nodeName, XMLConstants.NODE, permission);
	            contributor.addContent(permission);
	        } catch (Exception e) {}
	    }
	}
	
    /**
     * Checks if the contributor can edit this page -- if they are the author,
     * copyright owner, or the submitter of the page.
     */
    protected void writeOutPermissionForAccPage(Element accPageElmt, AccessoryPage accPage, Contributor contr) {
        boolean hasPermissions;
        if (contr != null) {
            hasPermissions = getPermissionChecker().checkEditingPermissionForPage(contr, (MappedAccessoryPage) accPage);
        } else {
            hasPermissions = false;
        }
        accPageElmt.setAttribute(XMLConstants.PERMISSION, hasPermissions ? XMLConstants.ONE : XMLConstants.ZERO);
    }
    
    protected CDATA getCDATA(String value) {
	    return new CDATA(textPreparer.replaceControlCharacters(value));
    }
    
    public Document getNotFoundDocument() {
    	Document doc = new Document();
    	Element rootElement = new Element(XMLConstants.ERROR);
    	rootElement.setAttribute(XMLConstants.ERRORNUM, "404");
    	doc.setRootElement(rootElement);
    	return doc;
    }
    
    public Element createNoPermissionsElement() {
    	return createLockElement(XMLConstants.PERMISSION, null, null, null, null, null);
    }
    
    public Element createDownloadedElement(Download download) {
    	return createDownloadLockElement(XMLConstants.DOWNLOADED, download,
    			null);
    }
    
    public Element createSubmittedElementForUploadBatch(Download download, boolean isSubmitted, Long batchId) {
    	String lockType = getLockTypeFromSubmitted(isSubmitted); 
        return createDownloadLockElement(lockType, download, batchId);
    }
    
    public Element createElementForLockedNode(boolean isSubmitted, String email, Date date, String rootName, 
    		Long downloadId, Long batchId) {
        String lockType = getLockTypeFromSubmitted(isSubmitted);
        return createLockElement(lockType, email, date, rootName, downloadId, batchId);
    }
    
    private Element createDownloadLockElement(String lockType, Download download, Long batchId) {
    	return createLockElement(lockType, download.getContributor().getEmail(),
    			download.getDownloadDate(), download.getRootNode().getName(), download.getDownloadId(),
    			batchId);    	
    }
    
    private String getLockTypeFromSubmitted(boolean isSubmitted) {
    	return isSubmitted ? XMLConstants.SUBMITTED : XMLConstants.DOWNLOADED; 
    }
    
    private Element createLockElement(String lockType, String email, Date date, 
    		String rootName, Long downloadId, Long batchId) {
        Element lockedElement = new Element(XMLConstants.LOCK_INFO);
        lockedElement.setAttribute(XMLConstants.TYPE, lockType);
        if (StringUtils.notEmpty(email)) {
        	lockedElement.setAttribute(XMLConstants.USER, email);
        }
        if (date != null) {
        	lockedElement.setAttribute(XMLConstants.DATE_TIME, date.toString());
        }
        if (StringUtils.notEmpty(rootName)) {
        	lockedElement.setAttribute(XMLConstants.ROOT, rootName);
        }
        if (downloadId != null) {
            lockedElement.setAttribute(XMLConstants.DOWNLOAD_ID, downloadId.toString());
        }
        if (batchId != null) {
            lockedElement.setAttribute(XMLConstants.BATCHID, batchId.toString());
        }
        return lockedElement;
    }
    


	/**
	 * @return Returns the nodeDao.
	 */
	public NodeDAO getNodeDAO() {
		return nodeDAO;
	}
	/**
	 * @param nodeDao The nodeDao to set.
	 */
	public void setNodeDAO(NodeDAO nodeDao) {
		this.nodeDAO = nodeDao;
	}
	
	/**
	 * @return Returns the pageDao.
	 */
	public PageDAO getPageDAO() {
		return pageDAO;
	}
	/**
	 * @param pageDao The pageDao to set.
	 */
	public void setPageDAO(PageDAO pageDao) {
		this.pageDAO = pageDao;
	}
	/**
	 * @return Returns the accessoryPageDAO.
	 */
	public AccessoryPageDAO getAccessoryPageDAO() {
		return accessoryPageDAO;
	}
	/**
	 * @param accessoryPageDAO The accessoryPageDAO to set.
	 */
	public void setAccessoryPageDAO(AccessoryPageDAO accessoryPageDAO) {
		this.accessoryPageDAO = accessoryPageDAO;
	}
	/**
	 * @return Returns the imageUtils.
	 */
	public ImageUtils getImageUtils() {
		return imageUtils;
	}
	/**
	 * @param imageUtils The imageUtils to set.
	 */
	public void setImageUtils(ImageUtils imageUtils) {
		this.imageUtils = imageUtils;
	}
	/**
	 * @return Returns the textPreparer.
	 */
	public BaseTextPreparer getTextPreparer() {
		return textPreparer;
	}
	/**
	 * @param textPreparer The textPreparer to set.
	 */
	public void setTextPreparer(BaseTextPreparer textPreparer) {
		this.textPreparer = textPreparer;
	}
    public ContributorDAO getContributorDAO() {
        return contributorDAO;
    }
    public void setContributorDAO(ContributorDAO contributorDAO) {
        this.contributorDAO = contributorDAO;
    }
    /**
     * @return Returns the imageDAO.
     */
    public ImageDAO getImageDAO() {
        return imageDAO;
    }
    /**
     * @param imageDAO The imageDAO to set.
     */
    public void setImageDAO(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }
    /**
     * @return Returns the permissionChecker.
     */
    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }
    /**
     * @param permissionChecker The permissionChecker to set.
     */
    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }
    public DataWriter getDataWriterForServletResponse(HttpServletResponse response) {
		DataWriter w = null;
		try {
			w = getDataWriterFromWriter(new OutputStreamWriter(response.getOutputStream()));
		} catch (Exception e1) { 
			e1.printStackTrace();
		}
		return w;    	
    }
    public void writeXMLToServletResponse(HttpServletResponse response, Long rootNodeId, boolean includeTreeStructure, boolean includeChildren) {
		StringWriter stringWriter = new StringWriter();
		DataWriter writer = getDataWriterForServletResponse(response);
		try {
			startDocument(writer);
			startTree(writer);
			addTreeStructureXMLForNode(rootNodeId, includeTreeStructure, writer, includeChildren, false);
			endTree(writer);
			endDocument(writer);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    public DataWriter getDataWriterFromWriter(Writer writer) {
    	return new DataWriter(writer);
    }
    public void startDocument(DataWriter w) throws SAXException {
		w.startDocument();
		w.setIndentStep(2);    	
    }
    public void endDocument(DataWriter w) throws SAXException {
    	w.endDocument();
    }
    public void startTree(DataWriter w) throws SAXException {
    	w.startElement(XMLConstants.TREE);
    }
    public void endTree(DataWriter w) throws SAXException {
    	w.endElement(XMLConstants.TREE);
    }
    
    private Long traverseAncestors(MappedNode node) {
    	if (node != null) {
    		boolean parentHasPage = getPageDAO().getNodeHasPage(node.getParentNodeId());
    		if (parentHasPage) {
    			return node.getParentNodeId();
    		} else {
    			MappedNode parentNode = getNodeDAO().getNodeWithId(node.getParentNodeId());
    			return traverseAncestors(parentNode);
    		}
    	}
    	return new Long(1);
    }
    
    private Long getAncestorPageId(MappedNode node) {
    	return traverseAncestors(node);
    }
    
    public void addTreeStructureXMLForNode(Long nodeId, boolean includeTreeStructure, DataWriter w, boolean includeChildren, boolean useMinimalData) throws SAXException {
    	addTreeStructureXMLForNode(nodeId, includeTreeStructure, w, includeChildren, -1, useMinimalData);
    }
    
    public void addTreeStructureXMLForNode(Long nodeId, boolean includeTreeStructure, DataWriter w, boolean includeChildren, int pageDepth, boolean useMinimalData) throws SAXException {
    	AttributesImpl atts = new AttributesImpl();
    	MappedNode node = getNodeDAO().getNodeWithId(nodeId);
    	boolean hasPage = getPageDAO().getNodeHasPage(nodeId);
    	Long ancestorPageId = getAncestorPageId(node);    	
    	List<Long> children = getNodeDAO().getChildrenNodeIds(nodeId);
    	int childCount = (children != null) ? children.size() : 0;
    	
    	if (!useMinimalData) {
    	    addNodeAttributes(nodeId, atts, node, hasPage, childCount, ancestorPageId);
    	} else { 
    		addMinimalNodeAttributes(nodeId, atts, hasPage);
    	}
    		
    	w.startElement("", XMLConstants.NODE, "", atts);
    	
    	if (!useMinimalData) {
    		addNodeContentElements(w, node);
    	}

    	if (node.getSynonyms().size() > 0 && !useMinimalData) {
    		w.startElement(XMLConstants.OTHERNAMES);
    		int counter = 0;
    		for (Iterator iter = node.getSynonyms().iterator(); iter.hasNext();) {
				MappedOtherName nextName = (MappedOtherName) iter.next();

                AttributesImpl othernameAtts = new AttributesImpl();
                counter = addSynonymAttributes(counter, nextName, othernameAtts);
                addSynonymElement(w, nextName, othernameAtts);
			}
    		w.endElement(XMLConstants.OTHERNAMES);
    	}
    	if (!includeTreeStructure) {
        	w.endElement(XMLConstants.NODE);    		
    	}
    	if (includeChildren) {
	    	
	    	List<MappedNode> childNodes = getNodeDAO().getChildrenNodes(node);
	    	if (childNodes.size() > 0) {
	    		if (includeTreeStructure) {
	    			w.startElement(XMLConstants.NODES);
	    		}
		    	for (MappedNode child : childNodes) {
		    		if (pageDepth>0){
		    			boolean childHasPage = getPageDAO().getNodeHasPage(child.getNodeId());
		    			if (childHasPage)
		    				addTreeStructureXMLForNode(child.getNodeId(), includeTreeStructure, w, includeChildren, pageDepth - 1, useMinimalData);
		    			else
		    				addTreeStructureXMLForNode(child.getNodeId(), includeTreeStructure, w, includeChildren, pageDepth, useMinimalData);

		    		}
		    		else if (pageDepth < 0)
		    			addTreeStructureXMLForNode(child.getNodeId(), includeTreeStructure, w, includeChildren, useMinimalData);
		    		
				}
		    	if (includeTreeStructure) {
		    		w.endElement(XMLConstants.NODES);
		    	}
	    	}
    	}
    	if (includeTreeStructure) {
    		w.endElement(XMLConstants.NODE);
    	}
    }
    
	private void addSynonymElement(DataWriter w, MappedOtherName nextName, AttributesImpl othernameAtts) throws SAXException {
		w.startElement("", XMLConstants.OTHERNAME, "", othernameAtts);                
		outputCDATAElement(XMLConstants.NAME, nextName.getName(), w);
		outputCDATAElement(XMLConstants.AUTHORITY, nextName.getAuthority(), w);
		outputCDATAElement(XMLConstants.COMMENTS, nextName.getComment(), w);
		w.endElement(XMLConstants.OTHERNAME);
	}

	private void addNodeContentElements(DataWriter w, MappedNode node) throws SAXException {
		outputNodeNameElement(node.getName(), w);
    	outputCDATAElement(XMLConstants.DESCRIPTION, node.getDescription(), w);
    	outputCDATAElement(XMLConstants.AUTHORITY, node.getNameAuthority(), w);
    	outputCDATAElement(XMLConstants.NAMECOMMENT, node.getNameComment(), w);
    	outputCDATAElement(XMLConstants.COMBINATION_AUTHOR, node.getCombinationAuthor(), w);
    	outputElement(XMLConstants.AUTHDATE, node.getAuthorityDate() + "", w);
	}

	private int addSynonymAttributes(int counter, MappedOtherName nextName, AttributesImpl othernameAtts) {
		addDataWriterAttribute(othernameAtts, XMLConstants.ISIMPORTANT, XMLWriter.getBooleanString(nextName.getIsImportant()));
		addDataWriterAttribute(othernameAtts, XMLConstants.ISPREFERRED, XMLWriter.getBooleanString(nextName.getIsPreferred()));
		addDataWriterAttribute(othernameAtts, XMLConstants.SEQUENCE, counter++ + "");
		addDataWriterAttribute(othernameAtts, XMLConstants.DATE, "" + nextName.getAuthorityYear());
		addDataWriterAttribute(othernameAtts, XMLConstants.ITALICIZE_NAME, XMLWriter.getBooleanString(nextName.getItalicize()));
		return counter;
	}

	private void addNodeAttributes(Long nodeId, AttributesImpl atts, MappedNode node, boolean hasPage, int childCount, Long ancestorPageId) {
		if (node != null) {
			addDataWriterAttribute(atts, XMLConstants.EXTINCT, "" + node.getExtinct());
			addDataWriterAttribute(atts, XMLConstants.ID, nodeId.toString());
			addDataWriterAttribute(atts, XMLConstants.CONFIDENCE, "" + node.getConfidence());
			addDataWriterAttribute(atts, XMLConstants.PHYLESIS, "" + node.getPhylesis());
			addDataWriterAttribute(atts, XMLConstants.LEAF, XMLWriter.getBooleanString(node.getIsLeaf()));
			addDataWriterAttribute(atts, XMLConstants.HASPAGE, XMLWriter.getBooleanString(hasPage));
			addDataWriterAttribute(atts, XMLConstants.ANCESTORPAGEID, ancestorPageId.toString());
			addDataWriterAttribute(atts, XMLConstants.ITALICIZE_NAME, XMLWriter.getBooleanString(node.getItalicizeName()));
			addDataWriterAttribute(atts, XMLConstants.INCOMPLETESUBGROUPS, XMLWriter.getBooleanString(node.getHasIncompleteSubgroups()));
			addDataWriterAttribute(atts, XMLConstants.SHOWAUTHORITY, XMLWriter.getBooleanString(node.getShowNameAuthority()));
			addDataWriterAttribute(atts, XMLConstants.SHOWAUTHORITYCONTAINING, XMLWriter.getBooleanString(node.getShowAuthorityInContainingGroup()));
			addDataWriterAttribute(atts, XMLConstants.IS_NEW_COMBINATION, XMLWriter.getBooleanString(node.getIsNewCombination()));
			addDataWriterAttribute(atts, XMLConstants.COMBINATION_DATE, "" + node.getCombinationDate());
			addDataWriterAttribute(atts, XMLConstants.CHILDCOUNT, "" + childCount);
		}
	}
	private void addMinimalNodeAttributes(Long nodeId, AttributesImpl atts, boolean hasPage) {
    	addDataWriterAttribute(atts, XMLConstants.ID, nodeId.toString());
    	addDataWriterAttribute(atts, XMLConstants.HASPAGE, XMLWriter.getBooleanString(hasPage));
	}	
    private void addDataWriterAttribute(AttributesImpl atts, String attrName, String attrValue) {
    	atts.addAttribute("", attrName, attrName, "CDATA", attrValue);
    }
    public void outputNodeNameElement(String name, DataWriter w) throws SAXException {
    	outputCDATAElement(XMLConstants.NAME, name, w);
    }
    public void outputElement(String elementName, String data, DataWriter w) throws SAXException {
    	w.startElement(elementName);
    	if (StringUtils.notEmpty(data)) {
    		w.characters(data);
    	}
    	w.endElement(elementName);
    }
    public void outputCDATAElement(String elementName, String data, DataWriter w) throws SAXException {
    	w.startElement(elementName);
    	if (StringUtils.notEmpty(data)) {
    		outputCDATA(data, w);
    	}
    	w.endElement(elementName);    	
    }
    public void outputCDATA(String characters, DataWriter w) throws SAXException {
    	w.startCDATA();
    	if (StringUtils.notEmpty(characters)) {
    		w.characters(characters);
    	}
    	w.endCDATA();
    }
}
