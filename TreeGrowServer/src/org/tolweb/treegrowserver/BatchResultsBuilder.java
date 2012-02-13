/*
 * Created on Jan 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.dao.DownloadDAO;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchResultsBuilder {
    private UploadBatchDAO uploadBatchDAO;
    private DownloadDAO downloadDAO;
    private PageDAO pageDAO;
    private NodeDAO nodeDAO;
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd-HH-mm");        

    
    /**
     * Builds an xml document containing a list of all upload batches this user
     * has either editing or authoring control over.
     * @param contributor
     * @return An XML document with all the pertinent information about the upload batches
     */
    public Document buildUploadBatchResultsDocument(Contributor contributor) {
        Element rootElement = new Element(XMLConstants.UPLOAD_BATCHES);
        Set batches = uploadBatchDAO.getAllBatchesForContributor(contributor);
        for (Iterator iter = batches.iterator(); iter.hasNext();) {
            UploadBatch batch = (UploadBatch) iter.next();
            rootElement.addContent(getUploadBatchElementForBatch(batch, contributor));
        }
        return new Document(rootElement);
    }
    
    public Element getUploadBatchElementForBatch(UploadBatch batch, Contributor contributor) {
        Element uploadBatchElement = constructUploadBatchElementForBatch(batch, contributor);        
        Set nodesSet = batch.getUploadedAndDownloadedNodeIdsSet();
        List checkedOutDownloads = downloadDAO.getActiveDownloadsForNodeIds(nodesSet);
        Element downloadsElement = new Element(XMLConstants.DOWNLOADS);
        boolean addToList = true;
        boolean hasCheckedOutFiles = false;
        for (Iterator iterator = checkedOutDownloads.iterator(); iterator
                .hasNext();) {
            hasCheckedOutFiles = true;
            Object[] element = (Object[]) iterator.next();
            // Index #0 is the download id
            Number downloadId = (Number) element[0];
            Number contributorId = (Number) element[1];
            // Still checked out by someone else, go to the next batch
            if (contributorId.intValue() != contributor.getId()) {
                uploadBatchElement.setAttribute(XMLConstants.OTHER_ACTIVE_DOWNLOAD, XMLConstants.ONE);
            }
            Element downloadIdElement = new Element(XMLConstants.DOWNLOAD_ID);
            downloadIdElement.setText(downloadId.toString());
            downloadsElement.addContent(downloadIdElement);
        }
        if (hasCheckedOutFiles) {
            uploadBatchElement.addContent(downloadsElement);
        }
        uploadBatchElement.setAttribute(XMLConstants.CHECKED_OUT_FILE, hasCheckedOutFiles ? XMLConstants.ONE : XMLConstants.ZERO);
        return uploadBatchElement;
    }
    
    private Element constructUploadBatchElementForBatch(UploadBatch batch, Contributor contributor) {
        Element uploadBatchElement = new Element(XMLConstants.UPLOAD_BATCH);
        uploadBatchElement.setAttribute(XMLConstants.BATCHID, batch.getBatchId().toString());
        uploadBatchElement.setAttribute(XMLConstants.SUBMITTED, batch.getIsSubmitted() ? XMLConstants.ONE : XMLConstants.ZERO);
        Element pagesElement = new Element(XMLConstants.PAGES);
        uploadBatchElement.addContent(pagesElement);
        // Add the page elements to the batch
        for (Iterator iter = batch.getUploadedPagesSet().iterator(); iter.hasNext();) {
            UploadPage nextPage = (UploadPage) iter.next();
            String pageName = pageDAO.getGroupNameForPage(nextPage.getPageId());
            Element pageElement = new Element(XMLConstants.PAGE);
            if (StringUtils.notEmpty(pageName)) {
	            pageElement.addContent(new CDATA(pageName));
	            pagesElement.addContent(pageElement);
            }
        }
        // Set the root group id and name
        Long rootGroupId = uploadBatchDAO.getRootNodeIdForBatch(batch);
        if (rootGroupId != null) {
	        uploadBatchElement.setAttribute(XMLConstants.ROOT_GROUP_ID, rootGroupId.toString());
	        String rootGroupName = "Undefined";
	        try {
	            rootGroupName = nodeDAO.getNameForNodeWithId(rootGroupId);
	        } catch (Exception e) {
	            e.printStackTrace();
	            try {
	                throw new RuntimeException("root group id that doesn't exist is : " + rootGroupId + " broken batch is: " + batch.getBatchId());
	            } catch (Exception e2) {
	                e2.printStackTrace();
	            }
	        }
	        Element rootGroupElement = new Element(XMLConstants.ROOT_GROUP);
	        rootGroupElement.addContent(new CDATA(rootGroupName));
	        uploadBatchElement.addContent(rootGroupElement);
        } else {
            uploadBatchElement.setAttribute(XMLConstants.ROOT_GROUP_ID, "0");
            Element rootGroupElement = new Element(XMLConstants.ROOT_GROUP);
            rootGroupElement.addContent(new CDATA("Undefined"));
            uploadBatchElement.addContent(rootGroupElement);
        }
        boolean hasEditingPrivileges = false;
        // Check to see if the contributor is an editor for this batch
        for (Iterator iter = batch.getEditors().iterator(); iter.hasNext();) {
            Contributor contr = (Contributor) iter.next();
            if (contributor.getId() == contr.getId()) {
                hasEditingPrivileges = true;
                break;
            }
        }
        uploadBatchElement.setAttribute(XMLConstants.CAN_PUSH_PUBLIC, hasEditingPrivileges ? XMLConstants.ONE : XMLConstants.ZERO);
        if (hasEditingPrivileges) {
            if (batch.getHasOnlyOneAuthor()) {
                uploadBatchElement.setAttribute(XMLConstants.IS_SOLE_AUTHOR, XMLConstants.ONE);
            }
        } else {
            uploadBatchElement.setAttribute(XMLConstants.IS_SOLE_AUTHOR, XMLConstants.ZERO);
        }
        // Add the last contributor and last uploaded information
        Upload upload = batch.getMostRecentUpload();
        if (upload != null) {
	        uploadBatchElement.setAttribute(XMLConstants.LAST_DATE, FORMATTER.format(upload.getUploadDate()));
	        uploadBatchElement.setAttribute(XMLConstants.LAST_USER, upload.getDownload().getContributor().getEmail());
        }
        return uploadBatchElement;
    }
    
    /**
     * @return Returns the downloadDAO.
     */
    public DownloadDAO getDownloadDAO() {
        return downloadDAO;
    }
    /**
     * @param downloadDAO The downloadDAO to set.
     */
    public void setDownloadDAO(DownloadDAO downloadDAO) {
        this.downloadDAO = downloadDAO;
    }
    /**
     * @return Returns the nodeDAO.
     */
    public NodeDAO getNodeDAO() {
        return nodeDAO;
    }
    /**
     * @param nodeDAO The nodeDAO to set.
     */
    public void setNodeDAO(NodeDAO nodeDAO) {
        this.nodeDAO = nodeDAO;
    }
    /**
     * @return Returns the pageDAO.
     */
    public PageDAO getPageDAO() {
        return pageDAO;
    }
    /**
     * @param pageDAO The pageDAO to set.
     */
    public void setPageDAO(PageDAO pageDAO) {
        this.pageDAO = pageDAO;
    }
    /**
     * @return Returns the uploadBatchDAO.
     */
    public UploadBatchDAO getUploadBatchDAO() {
        return uploadBatchDAO;
    }
    /**
     * @param uploadBatchDAO The uploadBatchDAO to set.
     */
    public void setUploadBatchDAO(UploadBatchDAO uploadBatchDAO) {
        this.uploadBatchDAO = uploadBatchDAO;
    }
}
