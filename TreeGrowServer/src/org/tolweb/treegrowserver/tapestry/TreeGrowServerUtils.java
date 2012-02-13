/*
 * Created on Feb 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.EditHistoryDAO;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.AccessoryPageNode;
import org.tolweb.hibernate.EditHistory;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.TitleIllustration;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.HttpRequestMaker;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.BatchResultsBuilder;
import org.tolweb.treegrowserver.Download;
import org.tolweb.treegrowserver.ServerXMLReader;
import org.tolweb.treegrowserver.UploadBatch;

/**
 * @author dmandel
 */
public abstract class TreeGrowServerUtils extends AuthenticatedPage implements IExternalPage, 
		NodeInjectable, ImageInjectable, BaseInjectable, TreeGrowServerInjectable {
    @InjectObject("spring:workingPageDAO")
    public abstract PageDAO getWorkingPageDAO();
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        super.activateExternalPage(parameters, cycle);
        Document errorDoc = authenticateUser(cycle);
        if (errorDoc != null) {
            setResultDocument(errorDoc);
        } else {
            String optype = getRequest().getParameterValue(RequestParameters.OPTYPE);
            Contributor contributor = getContributorFromCycle(cycle);
            if (optype.equals(RequestParameters.USER_VALIDATION) || optype.equals(RequestParameters.EDITING_ROOTNODE_ID)) {
                doUserValidation(contributor);
            } else if (optype.equals(RequestParameters.CURRENT_DOWNLOAD)) {
                doCurrentDownload();
            } else if (optype.equals(RequestParameters.CHECKED_OUT_FILES)) {
                doCheckedOutFiles(contributor);
            } else if (optype.equals(RequestParameters.SEARCH_CONTRIBUTORS)) {
                searchContributors();
            } else if (optype.equals(RequestParameters.SEARCH_IMAGES)) {
                searchImages();
            } else if (optype.equals(RequestParameters.TITLE_ILLUSTRATIONS)) {
                doTitleIllustrations();
            } else if (optype.equals(RequestParameters.NODE_IMAGES)) {
                doNodeImages();
            } else if (optype.equals(RequestParameters.UPLOAD_BATCH)) {
                doUploadBatch(contributor);
            } else if (optype.equals(RequestParameters.ACC_PAGES)) {
                doAccessoryPages(contributor);
            } else if (optype.equals(RequestParameters.NODES_WITH_PAGES)) {
                doNodesWithPages(contributor);
            } else if (optype.equals(RequestParameters.EMAIL_UPLOAD_PROBLEM)) {
                emailUploadProblem(contributor);
            }
        }
    }
    
    private void emailUploadProblem(Contributor contributor) {
        // set the message on a new page, activate to that page
        String errorMessage = getRequest().getParameterValue(RequestParameters.ERROR_MESSAGE);
        IPage errorPage = getRequestCycle().getPage("EmailErrorPage");
        PropertyUtils.write(errorPage, "errorMessage", errorMessage);
        getRequestCycle().activate(errorPage);
    }

    private void doNodesWithPages(Contributor contributor) {
        String downloadId = getRequest().getParameterValue(RequestParameters.DOWNLOAD_ID);
        List nodeIds = new ArrayList();
        if (StringUtils.notEmpty(downloadId)) {
            // look up the node ids etc.
            nodeIds = getDownloadDAO().getNodeIdsWithPages(new Long(downloadId));
        }
        PageDAO pageDAO = getWorkingPageDAO();
        EditHistoryDAO editHistoryDAO = getEditHistoryDAO();
        Element rootElement = new Element("RESPONSE");
        for (Iterator iter = nodeIds.iterator(); iter.hasNext();) {
            Long nextNodeId = (Long) iter.next();
            // find out who created this page
            Long pageId = pageDAO.getPageIdForNodeId(nextNodeId);
            List pageIds = new ArrayList();
            pageIds.add(pageId);
            String nodeName = getWorkingNodeDAO().getNameForNodeWithId(nextNodeId);
            Long editHistoryId = (Long) pageDAO.getEditHistoryIdsForPageIds(pageIds).get(0);
            EditHistory history = editHistoryDAO.getEditHistoryWithId(editHistoryId);
            Contributor createdContributor = null;
            if (history != null) {
                if (history.getCreatedContributorId() != null) {
                    createdContributor = getContributorDAO().getContributorWithId(history.getCreatedContributorId().toString());
                }
            }
            Element pageElement = new Element(XMLConstants.PAGE);
            Element nodeElement = new Element(XMLConstants.NODE);
            nodeElement.setAttribute(XMLConstants.NODEID, nextNodeId.toString());
            nodeElement.setAttribute(XMLConstants.NAME, nodeName);
            pageElement.addContent(nodeElement);
            Element contributorElement = new Element(XMLConstants.CONTRIBUTOR);
            String contributorName = createdContributor != null ? createdContributor.getName() : "";
            String contributorEmail = createdContributor != null ? createdContributor.getEmail() : "";
            contributorElement.setAttribute(XMLConstants.NAME, contributorName);
            contributorElement.setAttribute(XMLConstants.EMAIL, contributorEmail);
            pageElement.addContent(contributorElement);
            rootElement.addContent(pageElement);
        }
        setResultDocument(new Document(rootElement));
    }
    
    private void doUserValidation(Contributor contributor) {
        String newPassword = getRequest().getParameterValue(RequestParameters.NEW_PASSWORD);
        if (StringUtils.notEmpty(newPassword)) {
            PasswordUtils utils = getPasswordUtils();
            utils.setContributorPassword(contributor, newPassword);
        }
        Element rootElement = new Element("RESPONSE");
        String editingRootIdString = contributor.getEditingRootNodeId() != null ? contributor.getEditingRootNodeId().toString() : "0";
        rootElement.setAttribute(XMLConstants.ROOTNODE_ID, editingRootIdString);
        Element successElement = new Element(XMLConstants.SUCCESS);
        successElement.setAttribute(XMLConstants.PASSWORD, contributor.getPassword());
        Document newDoc = new Document(rootElement);
        newDoc.getRootElement().addContent(successElement);
        setResultDocument(newDoc);        
    }
    
    private void doCurrentDownload() {
        Element rootElement = new Element("RESULTS");
        String downloadId = getRequest().getParameterValue(RequestParameters.DOWNLOAD_ID);
        boolean isActive = getDownloadDAO().getDownloadIsActive(new Long(downloadId));
        if (isActive) {
            rootElement.setText(XMLConstants.TRUE);
        } else {
            rootElement.setText(XMLConstants.FALSE);
        }
        setResultDocument(new Document(rootElement));        
    }
    
    private void doTitleIllustrations() {
        String nodeId = getRequest().getParameterValue(RequestParameters.NODE_ID);
        Long nodeIdLong = new Long(nodeId);
        Element rootElement = new Element(XMLConstants.IMAGELIST);
        Document doc = new Document(rootElement);
        if (nodeIdLong.intValue() > 0) {
            MappedNode node = getWorkingNodeDAO().getNodeWithId(nodeIdLong);
            if (node != null) {
                MappedPage page = getWorkingPageDAO().getPageForNode(node);
                if (page != null) {
	                for (Iterator iter = page.getTitleIllustrations().iterator(); iter.hasNext();) {
	                    TitleIllustration ill = (TitleIllustration) iter.next();
	                    NodeImage image = ill.getImage();
	                    Element imgElement = getServerXMLWriter().encodeImage(image);
	                    rootElement.addContent(imgElement);
	                }
                }
            }
        }
        setResultDocument(doc);
    }
    
    private void doNodeImages() {
        String nodeIds = getRequest().getParameterValue(RequestParameters.NODE_ID);
        Element rootElement = new Element(XMLConstants.IMAGELIST);
        Document doc = new Document(rootElement);
        List imgs = getImageDAO().getImagesAttachedToNodes(nodeIds);
        for (Iterator iter = imgs.iterator(); iter.hasNext();) {
            NodeImage img = (NodeImage) iter.next();
            rootElement.addContent(getServerXMLWriter().encodeImage(img));
        }
        setResultDocument(doc);
    }
    
    @InjectObject("spring:workingAccessoryPageDAO")
    public abstract AccessoryPageDAO getWorkingAccessoryPageDAO();
    
    private void doAccessoryPages(Contributor contr) {
        String nodeIds = getRequest().getParameterValue(RequestParameters.NODE_ID);
        Element rootElement = new Element(XMLConstants.NODES);
        Document doc = new Document(rootElement);
        List pages = getWorkingAccessoryPageDAO().getAccessoryPagesForNodes(nodeIds);
        String[] nodeIdsArray = nodeIds.split(",");
        for (int i = 0; i < nodeIdsArray.length; i++) {
            String string = nodeIdsArray[i];
            Element nodeElement = new Element(XMLConstants.NODE);
            nodeElement.setAttribute(XMLConstants.ID, string);
            rootElement.addContent(nodeElement);
            for (Iterator iter = pages.iterator(); iter.hasNext();) {
                MappedAccessoryPage page = (MappedAccessoryPage) iter.next();
                nodeElement.addContent(getServerXMLWriter().encodeAccessoryPage(page, contr));
            }
        }
        setResultDocument(doc);
    }
    
    private void doUploadBatch(Contributor contributor) {
        String downloadId = getRequest().getParameterValue(RequestParameters.DOWNLOAD_ID);
        Download download = getDownloadDAO().getDownloadWithId(new Long(downloadId));
        UploadBatch batch = getUploadBatchDAO().getUploadBatchForDownload(download);
        if (batch != null) {
            Element uploadBatchElement = getBatchResultsBuilder().getUploadBatchElementForBatch(batch, contributor);
            setResultDocument(new Document(uploadBatchElement));
        } else {
            setResultDocument(new Document(new Element(XMLConstants.ERROR)));
        }
    }
    
    private void doCheckedOutFiles(Contributor contributor) {
        List checkedOutFiles = getDownloadDAO().getActiveDownloadsForUser(contributor);
        Element rootElement = new Element("CHECKED_OUT_FILES");
        Document doc = new Document(rootElement);
        for (Iterator iter = checkedOutFiles.iterator(); iter.hasNext();) {
            Download download = (Download) iter.next();
            Element element = new Element(XMLConstants.CHECKED_OUT_FILE);
            element.setAttribute(XMLConstants.ROOTNODE_ID, download.getRootNode().getNodeId().toString());
            element.setAttribute(XMLConstants.FILENAME, download.getRootNode().getName());
            element.setAttribute(XMLConstants.DOWNLOAD_ID, download.getDownloadId().toString());
            element.setAttribute(XMLConstants.DATE_TIME, BatchResultsBuilder.FORMATTER.format(download.getDownloadDate()));
            rootElement.addContent(element);
        }
        setResultDocument(doc);        
    }
    
    private void searchContributors() {
        Hashtable searchArgs = new Hashtable();
        checkParameterAndAddToHash(RequestParameters.FIRST_NAME, searchArgs, ContributorDAO.FIRSTNAME);
        checkParameterAndAddToHash(RequestParameters.SURNAME, searchArgs, ContributorDAO.SURNAME);
        checkParameterAndAddToHash(RequestParameters.EMAIL, searchArgs, ContributorDAO.EMAIL);
        checkParameterAndAddToHash(RequestParameters.INSTITUTION, searchArgs, ContributorDAO.INSTITUTION);
        checkParameterAndAddToHash(RequestParameters.ADDRESS, searchArgs, ContributorDAO.ADDRESS);
        List contributors = getContributorDAO().findContributors(searchArgs);
        Element rootElement = new Element("RESULTS");
        Document doc = new Document(rootElement);
        for (Iterator iter = contributors.iterator(); iter.hasNext();) {
            Contributor contr = (Contributor) iter.next();
            Element contrElement = getServerXMLWriter().encodeContributor(contr, true);
            rootElement.addContent(contrElement);
        }
        setResultDocument(doc);        
    }
    
    private void searchImages() {
        Hashtable searchArgs = new Hashtable();
        checkParameterAndAddToHash(RequestParameters.FILENAME, searchArgs, ImageDAO.FILENAME);
        checkParameterAndAddToHash(RequestParameters.SCIENTIFIC_NAME, searchArgs, ImageDAO.SCIENTIFIC_NAME);
        checkParameterAndAddToHash(RequestParameters.NODE_NAME, searchArgs, ImageDAO.GROUP);
        checkParameterAndAddToHash(RequestParameters.COPYOWNER, searchArgs, ImageDAO.COPYOWNER);
        String contributorEmail = getRequest().getParameterValue(RequestParameters.CONTRIBUTOR_ID);
        if (StringUtils.notEmpty(contributorEmail)) {
            Contributor contr = getContributorDAO().getContributorWithEmail(contributorEmail);
            if (contr != null) {
                searchArgs.put(ImageDAO.CONTRIBUTOR, contr);
            }
        }
        List imgs = getImageDAO().getImagesMatchingCriteria(searchArgs);
        Element rootElement = new Element("RESULTS");
        for (Iterator iter = imgs.iterator(); iter.hasNext();) {
            NodeImage image = (NodeImage) iter.next();
            Element imgElement = getServerXMLWriter().encodeImage(image);
            rootElement.addContent(imgElement);
        }
        setResultDocument(new Document(rootElement));        
    }
    
    private void checkParameterAndAddToHash(String param, Hashtable hash, Object searchKey) {
        String value = getRequest().getParameterValue(param);
        if (StringUtils.notEmpty(value)) {
            hash.put(searchKey, value);
        }
    }
}
