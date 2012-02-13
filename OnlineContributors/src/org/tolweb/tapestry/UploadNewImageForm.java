/*
 * Created on Mar 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.request.IUploadFile;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.Movie;
import org.tolweb.hibernate.Sound;
import org.tolweb.hivemind.ImageHelper;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class UploadNewImageForm extends BaseComponent implements UserInjectable, TreehouseInjectable, NodeInjectable, AccessoryInjectable {
    private static Logger logger;
    protected Long editedObjectId;
    protected Long nodeId;
	public abstract IUploadFile getUploadFile();
	public abstract void setError(boolean value);
	public abstract boolean getIsTreehouses();
	public abstract String getMediaType();
	public abstract boolean getFromPageEditing();
    @InjectObject("service:org.tolweb.tapestry.ImageHelper")
    public abstract ImageHelper getImageHelper();	
	
	static {
	    logger = Logger.getLogger(UploadNewImageForm.class);
	}
    
    protected void cleanupAfterRender(IRequestCycle cycle) {
        super.cleanupAfterRender(cycle);
        editedObjectId = null;
        nodeId = null;
    }
	
	public void uploadNewImageFormSubmit(IRequestCycle cycle) {
		if (getUploadFile() != null && getUploadFile().getSize() > 0) {
			IPage page = getPage();
			PropertyUtils.write(page, "newObjectUploaded", Boolean.valueOf(true));
			NodeImage img = constructNewImageInstance();
            if (getNodeId() != null) {
                img.addToNodesSet(getMiscNodeDAO().getNodeWithId(getNodeId()));
            }
            Contributor contr = getContributor();
			getImageHelper().saveAndWriteOutImage(img, getUploadFile(), null);
			if (NewImageCreatedListener.class.isInstance(page)) {
				((NewImageCreatedListener) page).newImageCreated(img);
			}
            try {
                PropertyUtils.write(page, "newObjectId", Integer.valueOf(img.getId()));
            } catch (Exception e) { e.printStackTrace(); }
            String url = getImageHelper().getEditUrlForMedia(img, cycle, contr, getFromPageEditing());
            try {
                PropertyUtils.write(page, "editObjectUrl", url);
            } catch (Exception e) { e.printStackTrace(); }
            try {
                PropertyUtils.write(page, "editedObjectId", getEditedObjectId());
            } catch (Exception e) { e.printStackTrace(); }
			if (getIsTreehouses()) {
			    MappedAccessoryPage treehouse = getTreehouse();
			    if (treehouse != null) {
				    if (treehouse.getMediaProgress() != MappedAccessoryPage.COMPLETE) {
				        treehouse.setMediaProgress(MappedAccessoryPage.WORK_IN_PROGRESS);
				        getWorkingAccessoryPageDAO().saveAccessoryPage(treehouse);
				    }
			    }
			}
			if (logger.isDebugEnabled()) {	
				logger.debug("Contributor " + getContributor().getName() + " just created image: " + 
					img.getLocation() + " with id: " + img.getId());
			}			
		}  else {
			setError(true);
		}
	}
	
	protected NodeImage constructNewImageInstance() {
	    if (getMediaType().equals(ImageSearch.DOCUMENT)) {
	        return new Document();
	    } else if (getMediaType().equals(ImageSearch.IMAGE)) {
	        return new NodeImage();
	    } else if (getMediaType().equals(ImageSearch.MOVIE)) {
	        return new Movie();
	    } else {
	        return new Sound();
	    }
	}
    /**
     * @return Returns the editedObjectId.
     */
    public Long getEditedObjectId() {
        return editedObjectId;
    }
    /**
     * @param editedObjectId The editedObjectId to set.
     */
    public void setEditedObjectId(Long editedObjectId) {
        this.editedObjectId = editedObjectId;
    }
    /**
     * @return Returns the nodeId.
     */
    public Long getNodeId() {
        return nodeId;
    }
    /**
     * @param nodeId The nodeId to set.
     */
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }
}
