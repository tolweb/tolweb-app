/*
 * EditImageData.java
 *
 * Created on May 3, 2004, 4:48 PM
 */

package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.apache.tapestry.request.IUploadFile;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.ImagePermission;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.Movie;
import org.tolweb.hibernate.Sound;
import org.tolweb.hivemind.ImageHelper;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.treehouse.SimpleEditImageData;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.Node;

/**
 *
 * @author  dmandel
 */
public abstract class EditImageData extends AbstractEditPage implements PageBeginRenderListener, IExternalPage, EditIdPage,
		RequestInjectable, UserInjectable, ImageInjectable, MiscInjectable {
    private int copyHolderSelection = COPYHOLDER_DEFAULT;
    private int imageSexSelection = -1;
	private int aliveSelection = -1;
    private String imageSexText;
    private String otherAliveText;
    private static final int COPYHOLDER_DEFAULT = -650;
    public static final int CONTRIBUTOR_COPY_OWNER = 0;
    public static final int OTHER_COPY_OWNER = 1;
    public static final int PUBLIC_DOMAIN = NodeImage.ALL_USES;
    public static final int OTHER_TOL_CONTRIBUTOR_OWNER = 3;
    public static final int MALE_SELECTION = 0;
    public static final int FEMALE_SELECTION = 1;
    public static final int OTHER_SELECTION = 2;
    public static final int LIVE_SPECIMEN = 0;
    public static final int DEAD_SPECIMEN = 1;
    public static final int FOSSIL_SPECIMEN = 2;
    public static final int OTHER_SPECIMEN = 3;
    public static final int MODEL_SPECIMEN = 4;
	public static final String EDIT_IMAGE_WINDOW_NAME = "editImage";

	public abstract void setFromPageEditing(boolean value);
	public abstract boolean getFromPageEditing();
    public abstract NodeImage getImage();
    public abstract void setImage(NodeImage value);
    public abstract void setCopyrightContributorId(String value);
    public abstract String getCopyrightContributorId();
	public abstract void setEditImageVersionsSelected(boolean value);
	public abstract boolean getEditImageVersionsSelected();
	public abstract void setSubmitImageDataSelected(boolean value);
	public abstract boolean getSubmitImageDataSelected();
	public abstract void setCopyImageDataSelected(boolean value);
	public abstract boolean getCopyImageDataSelected();
	public abstract void setSubmitContributorSearchSelected(boolean value);
	public abstract boolean getSubmitContributorSearchSelected();
	public abstract void setNodeToDelete(Node value);
	public abstract Node getNodeToDelete();
	public abstract void setWrongPassword(boolean value);
	public abstract boolean getWrongPassword();
	public abstract boolean getRememberSelected();
	public abstract void setPermissionsEditSelected(boolean value);
	public abstract boolean getPermissionsEditSelected();
	public abstract ImagePermission getEditorToDelete();
	public abstract IUploadFile getUploadFile();
    @InjectObject("service:org.tolweb.tapestry.ImageHelper")
    public abstract ImageHelper getImageHelper();  
    @InjectPage("treehouses/SimpleEditImageData")
    public abstract SimpleEditImageData getSimpleEditPage();
    @InjectComponent("mediaUsePermsBox")
    public abstract MediaUsePermissionBox getMediaUsePermissionBox();
	private static final String NO_IMAGE_TYPE = "Select an image type"; 
	public static IPropertySelectionModel IMAGETYPE_MODEL; 
	private static final String NO_SEASON = "season";
	public static final IPropertySelectionModel SEASON_MODEL = 
		new StringPropertySelectionModel(new String[] {NO_SEASON, "spring", "summer", "autumn/fall", "winter"});
	private static final String OTHER = "Other";
	@SuppressWarnings("unchecked")
	private static List PREDEFINED_TYPES; 
	public static IPropertySelectionModel TYPE_MODEL;
	
	static {
		PREDEFINED_TYPES = new ArrayList(NodeImage.TYPES_LIST);
		PREDEFINED_TYPES.add(0, OTHER);
	}
	
	public void pageValidate(PageEvent event) {
	}
	
    public Long getEditedObjectId() {
        NodeImage image = getImage();
        return image != null ? Long.valueOf(image.getId()) : null;
    }
    
    public String getHelpPageName() {
        return "ImageHelpMessagesPage";
    }
    
    public String getMediaType() {
        return ImageSearch.IMAGE;
    }
    
    public String getLowercaseMediaType() {
        return getMediaType().toLowerCase();
    }
    
    public NodeImage getMedia() {
        return getImage();
    }
    
    public void setEditedObjectId(Long id) {
        if (id != null) {
            NodeImage image = getImageDAO().getImageWithId(id.intValue());
            if (image == null) {
                setImage(null);
                throw new PageRedirectException(this);
            }
            setImage(image);
        } else {
            setImage(new NodeImage());
        }
    }	
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
	    String imageId = getRequest().getParameterValue(RequestParameters.IMAGE_ID);
	    if (imageId == null) {
	        // Check the service parameters in case it was called that way
	        Integer imgId = (Integer) parameters[2];
	        if (imgId != null) {
	            imageId = imgId.toString();
	        }
	        if (parameters.length > 3) {
	        	setFromPageEditing((Boolean) parameters[3]);
	        }
	    }
	    Contributor contr = getCookieAndContributorSource().authenticateExternalPage(cycle);
	    if (contr != null) {
	        NodeImage image = getImageDAO().getImageWithId(Integer.parseInt(imageId));
	        setImage(image);
	    } else {
	        setWrongPassword(true);
	    }
	}
	
	/**
	 * Needed for the form listener since all the other pages have it
	 * @param cycle
	 */
	public void previewSubmit(IRequestCycle cycle) {}

    public void detach() {
        super.detach();
        imageSexSelection = -1;
        copyHolderSelection = COPYHOLDER_DEFAULT;
        aliveSelection = -1;
    }
    
    @SuppressWarnings("unchecked")
	protected void removeNode() {
		MappedNode toRemove = (MappedNode) getNodeToDelete();
		Iterator it = getNodesSet().iterator();
		while (it.hasNext()) {
			MappedNode next = (MappedNode) it.next();
			if (next.getNodeId().equals(toRemove.getNodeId())) {
				getNodesSet().remove(next);
				break;
			}
		}	
	} 
    
    @SuppressWarnings("unchecked")
	protected void removeEditor() {
	    ImagePermission editor = getEditorToDelete();
	    Iterator it = new HashSet(getImage().getPermissionsSet()).iterator();
	    while (it.hasNext()) {
	        ImagePermission next = (ImagePermission) it.next();
	        if (next.getContributor().getId() == editor.getContributor().getId()) {
	            getImage().getPermissionsSet().remove(next);
	            break;
	        }
	    }
	}
	
    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        if (!event.getRequestCycle().isRewinding()) {
        	/*        	boolean isImage = getImage() == null || getImage().getMediaType() == NodeImage.IMAGE;
            if (getImageHelper().getContributorShouldUseSimpleMedia(getContributor()) && isImage) {
            	SimpleEditImageData imagePage = getSimpleEditPage();
            	if (getImage() != null) {
            		imagePage.setImage(getImage());
            	}
            	throw new PageRedirectException(imagePage);
            }*/
            	
        	setCopyrightContributorId(null);
        }
	    initSelectionModels();        
    }
	protected void initSelectionModels() {
		if (TYPE_MODEL == null || IMAGETYPE_MODEL == null) {
		    TYPE_MODEL = getPropertySelectionFactory().createModelFromList(PREDEFINED_TYPES);
		    IMAGETYPE_MODEL = getPropertySelectionFactory().createModelFromList(NodeImage.IMAGE_TYPES_LIST, NO_IMAGE_TYPE);	        
	    }
	}
    
    public String getImageTypeString() {
    	return getImage().getImageType();
    }
    
    public void setImageTypeString(String value) {
    	if (value != null && !value.equals(getNoMediaTypeString())) {
    		getImage().setImageType(value);
    	} else {
    		getImage().setImageType(null);
    	}
    }
    
    protected String getNoMediaTypeString() {
        return NO_IMAGE_TYPE;
    }
    
    public String getTypeString() {
    	if (getImage().getType() != null && !(getImage().getType().equals(OTHER))) {
			return getImage().getType();    		
    	} else {
    		return "";
    	}
    }
    
    public void setTypeString(String value) {
    	if (value != null && !value.equals(OTHER)) {
    		getImage().setType(value);	
    	} else {
    		getImage().setType(null);
    	}
    }
    
	public String getOtherTypeString() {
	    //System.out.println("does it have it? " + TYPE_MODEL.translateValue(getTypeString()));
		if (PREDEFINED_TYPES.contains(getTypeString())) {
			return "";
		} else {
			return getImage().getType();
		}    	
	}
    
	public void setOtherTypeString(String value) {
		// Only pay attention to this value if 'Other' is selected in the type box
		// Due to the way Tapestry rewinds forms, we are assured that the setTypeString method
		// has been called before us.
		if (StringUtils.isEmpty(getTypeString())) {
			getImage().setType(value);
		}
	}    
    
    public String getSeasonString() {
    	return getImage().getSeason();
    }
    
    public void setSeasonString(String value) {
    	if (value != null && !value.equals(NO_SEASON)) {
    		getImage().setSeason(value);
    	} else {
    		getImage().setSeason(null);
    	}
    }

    public int getImageSexSelection() {
        if (imageSexSelection == -1) {
            // Initialize to the appropriate selection
            if (getImage().getSex() != null && getImage().getSex().equals(NodeImage.MALE)) {
                imageSexSelection = MALE_SELECTION;
            } else if (getImage().getSex() != null && getImage().getSex().equals(NodeImage.FEMALE)) {
                imageSexSelection = FEMALE_SELECTION;
            } else {
                imageSexSelection = OTHER_SELECTION;
            }
        }
        return imageSexSelection;
    }
    
    public void setImageSexSelection(int value) {
        imageSexSelection = value;
    }
    
    public int getAliveSelection() {
    	if (aliveSelection == -1) {
    		if (getImage().getAlive() != null && getImage().getAlive().equals(NodeImage.ALIVE)) {
    			aliveSelection = LIVE_SPECIMEN;
    		} else if (getImage().getAlive() != null && getImage().getAlive().equals(NodeImage.DEAD)) {
    			aliveSelection = DEAD_SPECIMEN;
    		} else if (getImage().getAlive() != null && getImage().getAlive().equals(NodeImage.FOSSIL)) {
    			aliveSelection = FOSSIL_SPECIMEN;
    		} else if (getImage().getAlive() != null && getImage().getAlive().equals(NodeImage.MODEL)) {
    		    aliveSelection = MODEL_SPECIMEN;
    		} else {
    			aliveSelection = OTHER_SPECIMEN;
    		}
    	}
    	return aliveSelection;
    }
    
    public void setAliveSelection(int value) {
    	aliveSelection = value;
    }
    
    public int getCopyHolderSelection() {
        if (copyHolderSelection == COPYHOLDER_DEFAULT) {
        	if (getImage() != null) {
	        	byte use = getImage().getUsePermission();
	            if (use == NodeImage.ALL_USES) {
	                copyHolderSelection = PUBLIC_DOMAIN;
	            } else {
	                if (getImage().getCopyrightOwnerContributor() != null && getImage().getCopyrightOwnerContributor().getId() == (getContributor().getId())) {
	                    copyHolderSelection = CONTRIBUTOR_COPY_OWNER;
	                } else if(getImage().getCopyrightOwnerContributor() != null) {
	                    copyHolderSelection = OTHER_TOL_CONTRIBUTOR_OWNER;
	                } else {
	                    copyHolderSelection = OTHER_COPY_OWNER;
	                }
	            }
        	}
        }
        return copyHolderSelection;
    }
    
    public void setCopyHolderSelection(int value) {
        copyHolderSelection = value;
    }
    
    public void editImageVersions(IRequestCycle cycle) {
        setEditImageVersionsSelected(true);
    }
    
    public void copyImageData(IRequestCycle cycle) {
    	setCopyImageDataSelected(true);   	
    }
    
    public void submitContributorSearchSubmit(IRequestCycle cycle) {
        setSubmitContributorSearchSelected(true);
    }
    
    public void editImagePermissions(IRequestCycle cycle) {
        setPermissionsEditSelected(true);
    }
    
    public IPage doSave() {
    	IRequestCycle cycle = getRequestCycle();
		setupCopyrightInfo();
		setupAliveInfo();
		setupImageSex();  
		if (getRememberSelected()) {
		    getUsePermissionHelper().saveContributorUsePermissionDefault(getContributor(), getImage(), true);
		}
		handleRestrictedLicense();
		verifyPublicDomainSet(); // handles when JavaScript disables MediaUsePermissionBox
		saveImage();
        IPage page = checkForPageRedirect(cycle);
        if (page != null) {
        	return page;
        }
        if (getUploadFile() != null && getUploadFile().getSize() > 0) {
            getImageHelper().writeOutImage(getImage(), getUploadFile());
        }
        getImage().setLastEditedDate(new Date());
        getImage().setLastEditedContributor(getContributor());
		saveImage();
		return goToConfirmPage();
    }
    
    /**
     * Ensures that if there is no copyright owner, i.e. it's Public Domain media, then the image's 
     * usePermission is set to reflect that. 
     * @author lenards
     */
    private void verifyPublicDomainSet() {
    	/* This method is needed because if the JavaScript to disable the UsePermission component is 
    	 * executed, it does not run the code in that component to set the permissions */
		if (getCopyHolderSelection() == PUBLIC_DOMAIN) {
			getImage().setUsePermission(NodeImage.ALL_USES);
			getImage().setInPublicDomain(true);
		}
	}
    
    private void handleRestrictedLicense() {
    	if (getImage().getUsePermission() == NodeImage.RESTRICTED_USE || 
    		getImage().getUsePermission() == ContributorLicenseInfo.ONE_TIME_USE) {
    		getImage().setModificationPermitted(false);
    	}
    }
    
    /**
     * When another ToL Contributor is selected as the copyright owner, this will 
     * update the license defaults in the Media Permission component such that they 
     * represent the correct defaults of that "other ToL contributor."
     */
    public void useOtherTolContributorDefaults() {
    	Byte b = getMedia().getCopyrightOwnerContributor().getImageUseDefault();
    	if (b != null) {
    		getMediaUsePermissionBox().setUsePermissionSelection(b.byteValue());
    	}
    }
    
	protected IPage goToConfirmPage() {
		ImageDataConfirm confirmPage = (ImageDataConfirm) getRequestCycle().getPage("ImageDataConfirm");
		confirmPage.setImage(getImage());
		confirmPage.setReturnPageName(getPageName());
		confirmPage.setMediaType(getMediaType());
		confirmPage.setFromPageEditing(getFromPageEditing());
		return confirmPage;    	
    }
	protected IPage checkForPageRedirect(IRequestCycle cycle) {
		if (getRemovedGroup()) {
        	if (getNodeToDelete() != null) {
				removeNode();
				saveImage();
        	}
            return this;
        } else if (getEditorToDelete() != null) {
            removeEditor();
            saveImage();
            return this;
        } else if (getEditImageVersionsSelected() && !getSubmitImageDataSelected()) {
            EditImageVersions versions = (EditImageVersions) cycle.getPage("EditImageVersions");
            versions.setEditedObjectId(getEditedObjectId());
            return versions;
        } else if (getSearchSelected() && !getSubmitImageDataSelected()) {
        	setSearchSelected(false);
            FindNodes findPage = (FindNodes) cycle.getPage("FindNodes");
            findPage.doActivate(cycle, AbstractWrappablePage.FORM_WRAPPER, getPageName(), false, 
                    getEditedObjectId(), FindNodesResults.EDIT_IMAGE_CALLBACK, false);
            return findPage;
        } else if (getContributorSearchSelected() && !getSubmitImageDataSelected()) {
        	setContributorSearchSelected(false);
        	ContributorSearchPage contrSearchPage = (ContributorSearchPage) cycle.getPage("ContributorSearchPage");
        	contrSearchPage.doActivate(cycle, AbstractWrappablePage.FORM_WRAPPER, getPageName(), AbstractContributorSearchOrResultsPage.SELECT_COPYRIGHT, getEditedObjectId());
        	return contrSearchPage;
        } else if (getCopyImageDataSelected() && !getSubmitImageDataSelected()) {
            CopyImageDataSearch copyImageDataSearch = (CopyImageDataSearch) cycle.getPage("CopyImageDataSearch");
            copyImageDataSearch.setEditedObjectId(getEditedObjectId());
            copyImageDataSearch.setReturnPageName(getPageName());
            copyImageDataSearch.setMediaType(getMediaType());
        	return copyImageDataSearch;
        } else if (getSubmitContributorSearchSelected() && !getSubmitImageDataSelected()) {
            setSubmitContributorSearchSelected(false);
        	ContributorSearchPage contrSearchPage = (ContributorSearchPage) cycle.getPage("ContributorSearchPage");
        	contrSearchPage.doActivate(cycle, AbstractWrappablePage.FORM_WRAPPER, getPageName(), AbstractContributorSearchOrResultsPage.SELECT_SUBMITTER, getEditedObjectId());
        	return contrSearchPage;
        } else if (getPermissionsEditSelected()) {
            setPermissionsEditSelected(false);
            ContributorSearchPage contrSearchPage = (ContributorSearchPage) cycle.getPage("ContributorSearchPage");
        	contrSearchPage.doActivate(cycle, AbstractWrappablePage.FORM_WRAPPER, getPageName(), AbstractContributorSearchOrResultsPage.EDIT_IMAGE_PERMISSIONS, getEditedObjectId());
        	return contrSearchPage;
        }
		return null;
	}
        
    public void setImageSexText(String value) {
        imageSexText = value;
    }
    
    public String getImageSexText() {
        if (getImage().getSex() != null && (getImage().getSex().equals(NodeImage.MALE) || getImage().getSex().equals(NodeImage.FEMALE))) {
            return "";
        } else {
            if (getImage().getSex() != null) {
                return getImage().getSex();
            } else {
                return NodeImage.UNKNOWN;
            }
        }
    }
    
    public String getOtherAliveText() {
		if (getImage().getAlive() != null && (getImage().getAlive().equals(NodeImage.ALIVE) || getImage().getAlive().equals(NodeImage.DEAD)
			|| getImage().getAlive().equals(NodeImage.FOSSIL) || getImage().getAlive().equals(NodeImage.MODEL))) {
			return "";
		} else {
			if (getImage().getAlive() != null) {
				return getImage().getAlive();
			} else {
				return NodeImage.UNKNOWN;
			}
		}    	
    }
    
    public void setOtherAliveText(String value) {
    	otherAliveText = value;
    }
    
    private void setupImageSex() {
        if (getImageSexSelection() == MALE_SELECTION) {
            getImage().setSex(NodeImage.MALE);
        } else if (getImageSexSelection() == FEMALE_SELECTION) {
            getImage().setSex(NodeImage.FEMALE);
        } else {
            getImage().setSex(imageSexText);
        }
    }
    
	private void setupAliveInfo() {
		if (getAliveSelection() == LIVE_SPECIMEN) {
			getImage().setAlive(NodeImage.ALIVE);
		} else if (getAliveSelection() == DEAD_SPECIMEN) {
			getImage().setAlive(NodeImage.DEAD);
		} else if (getAliveSelection() == FOSSIL_SPECIMEN) {
			getImage().setAlive(NodeImage.FOSSIL);
		} else if (getAliveSelection() == MODEL_SPECIMEN) {
		    getImage().setAlive(NodeImage.MODEL);
		} else {
			getImage().setAlive(otherAliveText);
		}
	}
    
    private void setupCopyrightInfo() {
        if (copyHolderSelection == PUBLIC_DOMAIN) {
            getImage().setInPublicDomain(true);
            getImage().setCopyrightOwnerContributor(null);            
        } else {
            getImage().setInPublicDomain(false);
            if (copyHolderSelection == CONTRIBUTOR_COPY_OWNER) {
                getImage().setCopyrightOwnerContributor(getContributor());
            } else if (copyHolderSelection != OTHER_TOL_CONTRIBUTOR_OWNER) {
                getImage().setCopyrightOwnerContributor(null);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public Set getNodesSet() {
    	return getImage().getNodesSet();
    }
    
    public String getLoginPageName() {
    	return Login.IMAGE_LOGIN_PAGE;
    }
    
    public Object getEditedObject() {
    	return getImage();
    }
    
    public void submitImageData(IRequestCycle cycle) {
    	setSubmitImageDataSelected(true);
    }
    
    public void saveImage() {
		getImageDAO().saveImage(getImage());
    }
    
    public static String getEditPageNameForMedia(NodeImage media) {
        String editPageName;
	    if (Document.class.isInstance(media)) {
	        editPageName = "EditDocumentData";
	    } else if (Movie.class.isInstance(media)) {
	        editPageName = "EditMovieData";
	    } else if (Sound.class.isInstance(media)) {
	        editPageName = "EditSoundData";
	    } else {
	        editPageName = "EditImageData";
	    }        
	    return editPageName;
    }
}
