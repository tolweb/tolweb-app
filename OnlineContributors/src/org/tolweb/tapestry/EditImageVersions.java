/*
 * Created on Jan 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.request.IUploadFile;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.TitleIllustration;
import org.tolweb.hivemind.ImageHelper;
import org.tolweb.misc.ImageUtils;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditImageVersions extends AbstractImageContributorPage 
		implements PageBeginRenderListener, IPrimaryKeyConvertor, UserInjectable, 
		PageInjectable, ImageInjectable, BaseInjectable {
    public static final int PADDING = 18;
    public abstract NodeImage getImage();
    public abstract void setImage(NodeImage image);
    @SuppressWarnings("unchecked")
    public abstract List getVersions();
    @SuppressWarnings("unchecked")
    public abstract void setVersions(List versions);
    public abstract int getSelectedMasterIndex();
    public abstract void setSelectedMasterIndex(int value);
    public abstract int getSelectedMaxIndex();
    public abstract void setSelectedMaxIndex(int value);
    public abstract ImageVersion getCurrentVersion();
    public abstract void setNewVersionUrl(String value);
    public abstract String getNewVersionUrl();
    public abstract ImageVersion getVersionToGenerate();
    public abstract ImageVersion getVersionToDelete();
    public abstract IUploadFile getUploadFile();
    public abstract int getOtherIndex();
    public abstract void setReturnSelected(boolean value);
    public abstract boolean getReturnSelected();
    public abstract void setNewVersion(ImageVersion version);
    public abstract ImageVersion getNewVersion();
    @InjectObject("service:org.tolweb.tapestry.ImageHelper")
    public abstract ImageHelper getImageHelper();    
    
    public Long getEditedObjectId() {
        NodeImage image = getImage();
        return image != null ? Long.valueOf(image.getId()) : null;
    }
    
    public void setEditedObjectId(Long id) {
        if (id != null) {
            NodeImage image = getImageDAO().getImageWithId(id.intValue());
            if (image == null) {
                setImage(null);
                throw new PageRedirectException(this);
            }
            setImage(image);
            doVersionsInit();
        } else {
            setImage(new NodeImage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void doVersionsInit() {
        ImageDAO imgDAO = getImageDAO();
        List versions = imgDAO.getVersionsForImage(getImage());
        Collections.sort(versions);
        setVersions(versions);
        int maxIndex = 0, masterIndex = 0;
        // Find and set these indices so the radio buttons show up as selected
        int currentIndex = 0;
        for (Iterator iter = versions.iterator(); iter.hasNext();) {
            ImageVersion nextVersion = (ImageVersion) iter.next();
            if (nextVersion.getIsMaster()) {
                masterIndex = currentIndex; 
            }
            if (nextVersion.getIsMaxSize()) {
                maxIndex = currentIndex;
            }
            currentIndex++;
        }
        setSelectedMaxIndex(maxIndex);
        setSelectedMasterIndex(masterIndex);        
    }
    
    public void returnSelected(IRequestCycle cycle) {
        setReturnSelected(true);
    }
    
    public void uploadNewVersionSubmit(IRequestCycle cycle) {
    	if (getUploadFile() != null && getUploadFile().getSize() > 0) {
	        ImageVersion version = new ImageVersion();
	        version.setImage(getImage());
	        version.setContributor(getContributor());
	        getImageHelper().writeOutImageVersion(version, getUploadFile());
	        setNewVersion(version);
    	}
    }
    
    @SuppressWarnings("unchecked")
    public void editVersionsSubmit(IRequestCycle cycle) {
        boolean newVersionIsNewMaster = false;
        int selectedMasterIndex = getSelectedMasterIndex();
        int selectedMaxIndex = getSelectedMaxIndex();        
        if (getNewVersion() != null) {
            ImageVersion version = getNewVersion();
            List versions = getVersions();
            versions.add(version);
            Collections.sort(versions);
            int newVersionIndex = versions.indexOf(version);
            // If the newVersionIndex is less than or equal to either the selectedMasterIndex
            // or the selectedMaxIndex, then we need to bump those up by one since there
            // is one more item in the list.
            if (newVersionIndex <= selectedMasterIndex) {
                selectedMasterIndex++;
            }
            if (newVersionIndex <= selectedMaxIndex) {
                selectedMaxIndex++;
            }
            // Check to see if this new version uploaded is now the first item in the versions
            // list.  If it is, then it is larger than any previous version and should be marked as
            // the master version
            ImageVersion firstVersion = (ImageVersion) getVersions().get(0);
            if (firstVersion.getVersionId().equals(version.getVersionId())) {
                newVersionIsNewMaster = true;
            }            
        }
        if (newVersionIsNewMaster) {
            selectedMasterIndex = 0;
        }
        int index = 0;
        ImageVersion deletedVersion = getVersionToDelete();
        String masterFilename = "";
        boolean isNewMasterVersion = false;
        boolean isNewMaxVersion = false;
        ImageVersion maxSizeVersion = null;
        for (Iterator iter = getVersions().iterator(); iter.hasNext();) {
            ImageVersion version = (ImageVersion) iter.next();
            if (deletedVersion == null || !version.getVersionId().equals(getVersionToDelete().getVersionId())) {
	            if (index == selectedMasterIndex) {
	                // Check to see if it's already the master.  If not, we need to wipe out
	                // all autogenerated versions because a new master is available.
	                if (!version.getIsMaster()) {
	                    isNewMasterVersion = true;
	                }
	                version.setIsMaster(true);
	                masterFilename = version.getFileName();
	            } else {
	                version.setIsMaster(false);
	            }
	            if (index == selectedMaxIndex) {
	                if (!version.getIsMaxSize()) {
	                    isNewMaxVersion = true;
	                }
	                version.setIsMaxSize(true);
	                maxSizeVersion = version;
	            } else {
	                version.setIsMaxSize(false);
	            }
            }
            index++;
        }
        // If max size wasn't marked, it was just deleted, so the first
        // one is the new max size.
        if (maxSizeVersion == null) {
            maxSizeVersion = (ImageVersion) getVersions().get(0);
        }        
        // Remove the deleted image version from the list of versions
        if (deletedVersion != null) {   
            Long deletedVersionId = deletedVersion.getVersionId();
            resetTitleIllustrationsForDeletedVersion(deletedVersionId, maxSizeVersion);
            getVersions().remove(deletedVersion);
        }
        ImageDAO dao = getImageDAO();
        if (isNewMasterVersion) {
            getImage().setLocation(masterFilename);
            // Save the image because the location is where the master lives.
            dao.saveImage(getImage());
            cleanAutogeneratedVersions(maxSizeVersion);            
        } else if (isNewMaxVersion) {
            cleanAutogeneratedVersions(maxSizeVersion);
        }
        // Do the save and set images to be master and so on....
        for (Iterator iter = getVersions().iterator(); iter.hasNext();) {
            ImageVersion nextVersion = (ImageVersion) iter.next();
            dao.saveImageVersion(nextVersion);
        }
        // Make sure the selected indices are all set up
        doVersionsInit();
        if (getReturnSelected()) {
            EditImageData editPage = (EditImageData) cycle.getPage("EditImageData");
            editPage.setImage(getImage());
            cycle.activate(editPage);
        }
    }
    
    /**
     * Looops through all the versions and clears their filesize info
     * and deletes existing versions from the filesystem.
     */
    @SuppressWarnings("unchecked")
    private void cleanAutogeneratedVersions(ImageVersion maxSizeVersion) {
        ImageUtils utils = getImageUtils();
        NodeImage image = getImage();
        List removedVersions = utils.reinitializeAutogeneratedVersions(image, getVersions());
        ImageDAO dao = getImageDAO();
        for (Iterator iter = removedVersions.iterator(); iter.hasNext();) {
            ImageVersion version = (ImageVersion) iter.next();
            Long versionId = version.getVersionId();
            // Check to see if any title illustrations refer to this, if they do,
            // choose the next available version.
            dao.deleteImageVersion(version);
            resetTitleIllustrationsForDeletedVersion(versionId, maxSizeVersion);
        }
    }
    
    private void resetTitleIllustrationsForDeletedVersion(Long oldVersionId, ImageVersion newVersion) {
        resetTitleIllustrationsForDeletedVersion(oldVersionId, newVersion, getWorkingPageDAO());
        resetTitleIllustrationsForDeletedVersion(oldVersionId, newVersion, getPublicPageDAO());
    }
    
    @SuppressWarnings("unchecked")
    private void resetTitleIllustrationsForDeletedVersion(Long oldVersionId, ImageVersion newVersion, PageDAO dao) {
        // Check to see if any title illustrations refer to the deleted version.
        // If they do, point them to the closest version
        List titleIllustrations = dao.getTitleIllustrationsPointingAtVersion(oldVersionId);
        if (titleIllustrations != null && titleIllustrations.size() > 0) {
            // Go ahead and set it to the max size so there are no dangling references
            for (Iterator iter = titleIllustrations.iterator(); iter
                    .hasNext();) {
                TitleIllustration till = (TitleIllustration) iter.next();
                if (newVersion != null) {
                    till.setVersion(newVersion);
                    dao.saveTitleIllustration(till);
                }
            }
        }        
    }
    
    public void generateNewVersion(IRequestCycle cycle) {
        ImageUtils utils = getImageUtils();
        ImageVersion version = getVersionToGenerate();
        utils.writeVersionToDisk(version);
        ImageDAO dao = getImageDAO();
        dao.saveImageVersion(version);
        setNewVersionUrl(utils.getUrlForVersion(version));
    }
    
    public void deleteVersion(IRequestCycle cycle) {
        ImageDAO dao = getImageDAO();
        ImageVersion version = getVersionToDelete();
        dao.deleteImageVersion(version);
    }
    
    private int getHeight() {
    	return getCurrentVersion().getHeight().intValue() + PADDING;
    }
    
    private int getWidth() {
    	return getCurrentVersion().getWidth().intValue() + PADDING;
    }
    
    public String getWidthString() { 
        return getWidth() + "";
    }
    
    public String getHeightString() {
        return getHeight() + "";
    }
    
    public PopupLinkRenderer getPopupRenderer() {
    	return getRendererFactory().getLinkRenderer(getCurrentVersion().getVersionId().toString(), getWidth(), getHeight(), "resizable");
    }
    
    public boolean getCanDeleteVersion() {
        ImageVersion version = getCurrentVersion();
        return !version.getIsMaster() && !version.getIsGenerated();
    }
    
    public boolean getCanSelectMaster() {
        ImageVersion versionToCheck = (ImageVersion) getVersions().get(getOtherIndex());
        return !versionToCheck.getIsGenerated();
    }
    
    public String getImageVersionClassname() {
        int index = getVersions().indexOf(getCurrentVersion());
        if (index == 0) {
            // First item in the list, so it can't be deprecated.
            return null;
        } else {
            ImageVersion previousVersion = (ImageVersion) getVersions().get(index - 1);
            boolean isDeprecated = getIsDeprecatedAutoVersion(getCurrentVersion(), previousVersion);
            if (isDeprecated) {
                return "dimmed";
            } else {
                return null;
            }
        }
    }
    
    public boolean getIsDeprecatedAutoVersion() {
        int index = getOtherIndex();
        if (index == 0) {
            return false;
        } else {
            ImageVersion version = (ImageVersion) getVersions().get(index);
            ImageVersion previousVersion = (ImageVersion) getVersions().get(index - 1);
            return getIsDeprecatedAutoVersion(version, previousVersion);
        }
    }
    
    private boolean getIsDeprecatedAutoVersion(ImageVersion versionToCheck, ImageVersion previousVersion) {
        if (!versionToCheck.getIsGenerated()) {
            return false;
        } else {
            if (versionToCheck.getHeight().equals(previousVersion.getHeight())) {
                return true;
            } else {
                return false;
            }
        }
    }
    
	/**
	 * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getPrimaryKey(java.lang.Object)
	 */
	public Object getPrimaryKey(Object objValue) {
		return ((ImageVersion) objValue).getVersionId();
	}

	/**
	 * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getValue(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Object getValue(Object objPrimaryKey) {
	    Long versionId = (Long) objPrimaryKey;
	    for (Iterator iter = getVersions().iterator(); iter.hasNext();) {
            ImageVersion version = (ImageVersion) iter.next();
            if (version.getVersionId().equals(versionId)) {
                return version;
            }
        }
		return null;
	} 
	
	public IPrimaryKeyConvertor getConvertor() {
		return this;
	}	
}