/*
 * Created on Nov 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.EditHistoryDAO;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.misc.BaseTextPreparer;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.tapestry.CommonGlobal;
import org.tolweb.treegrowserver.BatchComparer;
import org.tolweb.treegrowserver.BatchPusher;
import org.tolweb.treegrowserver.BatchResultsBuilder;
import org.tolweb.treegrowserver.BatchSubmitter;
import org.tolweb.treegrowserver.DownloadBuilder;
import org.tolweb.treegrowserver.DownloadCheckin;
import org.tolweb.treegrowserver.NodeSearchResultsBuilder;
import org.tolweb.treegrowserver.ServerXMLReader;
import org.tolweb.treegrowserver.ServerXMLWriter;
import org.tolweb.treegrowserver.UploadBuilder;
import org.tolweb.treegrowserver.dao.DownloadDAO;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Global extends CommonGlobal {
    private NodeSearchResultsBuilder searchResultsBuilder;
    private DownloadBuilder downloadBuilder;
    private PasswordUtils passwordUtils;
    private PermissionChecker permissionChecker;
    private ContributorDAO contributorDAO;
    private DownloadDAO downloadDAO;
    private UploadBatchDAO uploadBatchDAO;
    private ServerXMLReader serverXMLReader;
    private ServerXMLWriter serverXMLWriter;
    private UploadBuilder uploadBuilder;
    private BatchResultsBuilder batchResultsBuilder;
    private DownloadCheckin checkin;
    private BatchSubmitter batchSubmitter;
    private NodeDAO nodeDAO;
    private BatchPusher batchPusher;
    private ImageDAO imageDAO;
    private PageDAO pageDAO;
    private AccessoryPageDAO accessoryPageDAO;
    private BaseTextPreparer textPreparer;
    private BatchComparer batchComparer;
    private EditHistoryDAO editHistoryDAO;
    
    public Global() {
        searchResultsBuilder = (NodeSearchResultsBuilder) context.getBean("nodeSearchResultsBuilder");
        downloadBuilder = (DownloadBuilder) context.getBean("downloadBuilder");
        passwordUtils = (PasswordUtils) context.getBean("passwordUtils");
        permissionChecker = (PermissionChecker) context.getBean("permissionChecker");
        contributorDAO = (ContributorDAO) context.getBean("contributorDAO");
        downloadDAO = (DownloadDAO) context.getBean("downloadDAO");
        uploadBatchDAO = (UploadBatchDAO) context.getBean("uploadBatchDAO");
        serverXMLReader = (ServerXMLReader) context.getBean("serverXMLReader");
        uploadBuilder = (UploadBuilder) context.getBean("uploadBuilder");
        batchResultsBuilder = (BatchResultsBuilder) context.getBean("batchResultsBuilder");
        checkin = (DownloadCheckin) context.getBean("downloadCheckin");
        batchSubmitter = (BatchSubmitter) context.getBean("batchSubmitter");
        nodeDAO = (NodeDAO) context.getBean("workingNodeDAO");
        batchPusher = (BatchPusher) context.getBean("batchPusher");
        serverXMLWriter = (ServerXMLWriter) context.getBean("serverXMLWriter");
        imageDAO = (ImageDAO) context.getBean("imageDAO");
        pageDAO = (PageDAO) context.getBean("workingPageDAO");
        accessoryPageDAO = (AccessoryPageDAO) context.getBean("workingAccessoryPageDAO");
        textPreparer = (BaseTextPreparer) context.getBean("textPreparer");
        batchComparer = (BatchComparer) context.getBean("batchComparer");
    }
    

    public NodeSearchResultsBuilder getSearchResultsBuilder() {
        return searchResultsBuilder;
    }
    public void setSearchResultsBuilder(NodeSearchResultsBuilder searchResultsBuilder) {
        this.searchResultsBuilder = searchResultsBuilder;
    }
	/**
	 * @return Returns the downloadBuilder.
	 */
	public DownloadBuilder getDownloadBuilder() {
		return downloadBuilder;
	}
	/**
	 * @param downloadBuilder The downloadBuilder to set.
	 */
	public void setDownloadBuilder(DownloadBuilder downloadBuilder) {
		this.downloadBuilder = downloadBuilder;
	}
	/**
	 * @return Returns the passwordUtils.
	 */
	public PasswordUtils getPasswordUtils() {
		return passwordUtils;
	}
	/**
	 * @param passwordUtils The passwordUtils to set.
	 */
	public void setPasswordUtils(PasswordUtils passwordUtils) {
		this.passwordUtils = passwordUtils;
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
	/**
	 * @return Returns the contributorDAO.
	 */
	public ContributorDAO getContributorDAO() {
		return contributorDAO;
	}
	/**
	 * @param contributorDAO The contributorDAO to set.
	 */
	public void setContributorDAO(ContributorDAO contributorDAO) {
		this.contributorDAO = contributorDAO;
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
	/**
	 * @return Returns the serverXMLReader.
	 */
	public ServerXMLReader getServerXMLReader() {
		return serverXMLReader;
	}
	/**
	 * @param serverXMLReader The serverXMLReader to set.
	 */
	public void setServerXMLReader(ServerXMLReader serverXMLReader) {
		this.serverXMLReader = serverXMLReader;
	}
	/**
	 * @return Returns the uploadBuilder.
	 */
	public UploadBuilder getUploadBuilder() {
		return uploadBuilder;
	}
	/**
	 * @param uploadBuilder The uploadBuilder to set.
	 */
	public void setUploadBuilder(UploadBuilder uploadBuilder) {
		this.uploadBuilder = uploadBuilder;
	}
    /**
     * @return Returns the batchResultsBuilder.
     */
    public BatchResultsBuilder getBatchResultsBuilder() {
        return batchResultsBuilder;
    }
    /**
     * @param batchResultsBuilder The batchResultsBuilder to set.
     */
    public void setBatchResultsBuilder(BatchResultsBuilder batchResultsBuilder) {
        this.batchResultsBuilder = batchResultsBuilder;
    }
    /**
     * @return Returns the checkin.
     */
    public DownloadCheckin getCheckin() {
        return checkin;
    }
    /**
     * @param checkin The checkin to set.
     */
    public void setCheckin(DownloadCheckin checkin) {
        this.checkin = checkin;
    }
    /**
     * @return Returns the batchSubmitter.
     */
    public BatchSubmitter getBatchSubmitter() {
        return batchSubmitter;
    }
    /**
     * @param batchSubmitter The batchSubmitter to set.
     */
    public void setBatchSubmitter(BatchSubmitter batchSubmitter) {
        this.batchSubmitter = batchSubmitter;
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
     * @return Returns the batchPusher.
     */
    public BatchPusher getBatchPusher() {
        return batchPusher;
    }
    /**
     * @param batchPusher The batchPusher to set.
     */
    public void setBatchPusher(BatchPusher batchPusher) {
        this.batchPusher = batchPusher;
    }
    /**
     * @return Returns the serverXMLWriter.
     */
    public ServerXMLWriter getServerXMLWriter() {
        return serverXMLWriter;
    }
    /**
     * @param serverXMLWriter The serverXMLWriter to set.
     */
    public void setServerXMLWriter(ServerXMLWriter serverXMLWriter) {
        this.serverXMLWriter = serverXMLWriter;
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
    /**
     * @return Returns the batchComparer.
     */
    public BatchComparer getBatchComparer() {
        return batchComparer;
    }
    /**
     * @param batchComparer The batchComparer to set.
     */
    public void setBatchComparer(BatchComparer batchComparer) {
        this.batchComparer = batchComparer;
    }


	public EditHistoryDAO getEditHistoryDAO() {
		return editHistoryDAO;
	}


	public void setEditHistoryDAO(EditHistoryDAO editHistoryDAO) {
		this.editHistoryDAO = editHistoryDAO;
	}
}
