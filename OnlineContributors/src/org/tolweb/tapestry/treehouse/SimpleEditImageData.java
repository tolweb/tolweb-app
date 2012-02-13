package org.tolweb.tapestry.treehouse;

import org.apache.tapestry.IForm;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.EditImageData;
import org.tolweb.tapestry.ImageDataBlock;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.wrappers.EditSimpleMediaWrapper;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

public abstract class SimpleEditImageData extends EditImageData implements BaseInjectable, PageBeginRenderListener {
	@InjectState("image")
	public abstract NodeImage getImage();
	public abstract void setImage(NodeImage value);
	public abstract boolean getUseOption1Selected();
	public abstract void setUseOption1Selected(boolean value);
	public abstract boolean getUseOption2Selected();
	public abstract void setUseOption2Selected(boolean value);
	public abstract boolean getUseOption3Selected();
	public abstract void setUseOption3Selected(boolean value);
	public abstract boolean getUseOption4Selected();
	public abstract void setUseOption4Selected(boolean value);
	public abstract boolean getUseOption5Selected();	
	public abstract void setUseOption5Selected(boolean value);
	public abstract boolean getGoToStep3Selected();
	public abstract void setGoToStep3Selected(boolean value);
	@InjectComponent("wrapper")
	public abstract EditSimpleMediaWrapper getWrapper();
	@InjectPage("treehouses/SimpleEditImageDataStep3")
	public abstract SimpleEditImageDataStep3 getStep3Page();
	
	public abstract MappedNode getCurrentNode();
	public abstract void setCurrentNode(MappedNode value);
	
	public abstract int getPublicDomainSourceSelection();
	public abstract void setPublicDomainSourceSelection(int value);
	private static final int WWW_SELECTION = 1;
	private static final int PRINT_SELECTION = 2;
	
	// overridden to cancel out behavior of the superclass
	public void setEditedObjectId(Long value) {}
	
	/**
	 * Set by the wrapper to be used in various javascript stuffs
	 * @return
	 */
	public IForm getImageForm() {
		IForm form = getWrapper().getForm();
		return form;
	}
	
	public void pageBeginRender(PageEvent event) {
		if (!event.getRequestCycle().isRewinding()) {
			// check to see if none of the use options have been checked.
			// if not, we'll need to try and establish which one
			if (!getUseOption1Selected() && !getUseOption2Selected() && !getUseOption3Selected()
					&& !getUseOption4Selected() && !getUseOption5Selected()) {
				Contributor contr = getImage().getCopyrightOwnerContributor();
				if (contr != null) {
					// if it's the same as the person editing, select option 1
					if (getContributor() != null && contr.getId() == getContributor().getId()) {
						setUseOption1Selected(true);
					} else {
						setUseOption2Selected(true);
					}
				} else if (getImage().getInPublicDomain()) {
					setUseOption5Selected(true);
				} else if (StringUtils.notEmpty(getImage().getCopyrightOwner()) || 
						StringUtils.notEmpty(getImage().getCopyrightEmail()) ||
						StringUtils.notEmpty(getImage().getCopyrightUrl())) {
					setUseOption3Selected(true);
				}
			}
			if (getPublicDomainSourceSelection() <= 0) {
				// first check to see if any of the fields are filled out
				if (StringUtils.notEmpty(getImage().getPublicDomainAuthor()) ||
						StringUtils.notEmpty(getImage().getPublicDomainPublicationDate()) ||
						StringUtils.notEmpty(getImage().getPublicDomainPublisher()) ||
						StringUtils.notEmpty(getImage().getPublicDomainTitle())) {
					setPublicDomainSourceSelection(PRINT_SELECTION);
				} else {
					setPublicDomainSourceSelection(WWW_SELECTION);
				}
			}
			if (StringUtils.isEmpty(getImage().getImageType())) {
				// make photo the default
				getImage().setImageType(NodeImage.PHOTOGRAPH);
			}
		}
	    initSelectionModels();		
	}
	
	public boolean getIsNew() {
		return getImage().getId() <= 0;
	}
	public String getCopyrightString() {
		return getTextPreparer().getCopyrightOwnerString(getImage(), true, false, false);
	}
	public boolean getHasAcks() {
		return StringUtils.notEmpty(getImage().getAcknowledgements());
	}
	public String getUseString() {
		return ImageDataBlock.getUsePermissionString(getImage());
	}
	public String getOtherPersonTextClass() {
		return getUseOption3Selected() ? null : "hide";
	}
	public void goToStep3() {
		setGoToStep3Selected(true);
	}
	public void submitData() {
		doValidation();
		if (StringUtils.isEmpty(getErrorMessage())) {
			setSubmitImageDataSelected(true);
		}
	}
	public IPage simpleImageSave() {
		IPage returnPage = checkForPageRedirect(getRequestCycle());
		// no redirect, so check for what to do on this page
		if (returnPage == null) {
			if (getUseOption1Selected()) {
				getImage().setCopyrightOwnerContributor(getContributor());
			} else if (getUseOption3Selected()) {
				getImage().setCopyrightOwnerContributor(null);
			} else if (getUseOption4Selected()) {
				setErrorMessage("You may not use images if you have not received permission to use them.");
			} else if (getUseOption5Selected()) {
				getImage().setInPublicDomain(true);
				getImage().setCopyrightOwnerContributor(null);
			}
			if (getSubmitImageDataSelected()) {
				returnPage = goToConfirmPage();
			} else {
				returnPage = null;
			}
		}
		saveImage();		
		return returnPage;
	}
	public IPage checkForPageRedirect(IRequestCycle cycle) {
		IPage page = super.checkForPageRedirect(cycle);
		if (page == null) {
			if (getGoToStep3Selected()) {
				doValidation();
				if (StringUtils.isEmpty(getErrorMessage())) {
					SimpleEditImageData editPage = getStep3Page();
					editPage.setImage(getImage());
					page = editPage;
				}
			}
		}
		return page;
	}
	public int getWwwSelection() {
		return WWW_SELECTION;
	}
	public int getPrintSelection() {
		return PRINT_SELECTION;
	}
	public String getPhotoString() {
		return NodeImage.PHOTOGRAPH;
	}
	public String getDiagramString() {
		return NodeImage.DIAGRAM;
	}
	public String getDrawingString() {
		return NodeImage.DRAWING_PAINTING;
	}
    public String getAOrAnother() {
    	if (getImage().getNodesSet().size() > 0) {
    		return "another";
    	} else {
    		return "a";
    	}
    }
    private void doValidation() {
    	if (!getUseOption1Selected() && !getUseOption2Selected() && !getUseOption3Selected()
    			&& !getUseOption5Selected()) {
    		setErrorMessage("You must select one of the copyright Options.");
    		return;
    	}
    	boolean hasCopyrightDate = StringUtils.notEmpty(getImage().getCopyrightDate()); 
    	if (getUseOption1Selected()) {
    		if (!hasCopyrightDate) {
    			setErrorMessage("You must enter a copyright date for Option 1.");
    		}
    	} else if (getUseOption2Selected()) {
    		if (getImage().getCopyrightOwnerContributor() == null) {
    			setErrorMessage("You must select another ToL Contributor as the copyright owner for Option 2.");
    		} else if (!hasCopyrightDate) {
    			setErrorMessage("You must enter a copyright date for Option 2.");
    		}
    	} else if (getUseOption3Selected()) {
    		if (StringUtils.isEmpty(getImage().getCopyrightOwner())) {
    			setErrorMessage("You must enter the name of the copyright owner for Option 3.");
    		}
    	} else if (getUseOption5Selected()) {
    		if (getPublicDomainSourceSelection() == WWW_SELECTION) {
    			if (StringUtils.isEmpty(getImage().getPublicDomainAddress()) || StringUtils.isEmpty(getImage().getPublicDomainSourceName())
    					|| StringUtils.isEmpty(getImage().getPublicDomainSourceUrl())) {
    				setErrorMessage("You must enter all required WWW fields for Option 5.");
    			}
    		} else {
    			if (StringUtils.isEmpty(getImage().getPublicDomainAuthor()) || StringUtils.isEmpty(getImage().getPublicDomainPublicationDate())
    					|| StringUtils.isEmpty(getImage().getPublicDomainTitle())
    					|| StringUtils.isEmpty(getImage().getPublicDomainPublisher())) {
    				setErrorMessage("You must enter all required Print fields for Option 5.");
    			}
    		}
    	}
    	// check to make sure that one of node attachment, scientific name, or don't know is
    	// filled out for the node attachment area
    	if (getImage().getNodesSet().size() == 0 && StringUtils.isEmpty(getImage().getScientificName())
    			&& !getImage().getDontKnowAttachment()) {
    		setErrorMessage("You must atttach your image to a group, enter its scientific name, or check the \"I do not know where to attach this media file\" box.");
    	}
    }
}
