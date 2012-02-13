/*
 * ImageSearchResults.java
 *
 * Created on May 7, 2004, 2:49 PM
 */

package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.contrib.table.components.Table;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.TeacherResource;
import org.tolweb.hivemind.ImageHelper;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

/**
 *
 * @author  dmandel
 */
public abstract class ImageSearchResults extends AbstractSearchResults implements UserInjectable, TreehouseInjectable, NodeInjectable, 
		ImageInjectable, AccessoryInjectable, PageInjectable, BaseInjectable {
	@SuppressWarnings("unchecked")
    public abstract void setImages(List value);
	@SuppressWarnings("unchecked")
    public abstract List getImages();
    public abstract void setDontShowFilename(boolean value);
    public abstract boolean getDontShowFilename();
    public abstract void setEditedObjectId(Long value);
    public abstract Long getEditedObjectId();
    public abstract Long getSelectedImageId();
    public abstract void setEditWindowName(String value);
    public abstract String getMediaType();
    public abstract void setMediaType(String value);
    public abstract void setAdditionalReturnPageName(String value);
    public abstract String getAdditionalReturnPageName();
    @InjectObject("service:org.tolweb.tapestry.ImageHelper")
    public abstract ImageHelper getImageHelper();
	
	public static final int EDIT_IMAGE_CALLBACK = 0;
	public static final int COPY_IMAGE_CALLBACK = 1;
	public static final int DELETE_IMAGE_CALLBACK = 2;
	public static final int NO_CALLBACK = 3;
	public static final int TILLUS_CALLBACK = 4;
	public static final int TR_STATE_STANDARDS_CALLBACK = 5;
	public static final int TR_NATIONAL_STANDARDS_CALLBACK = 6;
	public static final int TR_SUPPORT_DOCUMENT_CALLBACK = 7;
    public static final int PORTFOLIO_ADDIMAGE_CALLBACK = 8;
    public static final int PAGE_IMAGE_NOEDITOR_CALLBACK = 9;
    public static final int PAGE_MEDIA_CALLBACK = 10;
    public static final int ADD_NODE_TO_IMAGE_CALLBACK = 11;
	
	public void searchAgain(IRequestCycle cycle) {
	    IPage page = cycle.getPage(getSearchPageName());
	    try {
	        PropertyUtils.write(page, "editedObjectId", getEditedObjectId());
        } catch (Exception e) {}
        try {
	        PropertyUtils.write(page, "mediaType", getMediaType());
        } catch (Exception e) {}
        try {
	        PropertyUtils.write(page, "returnPageName", getAdditionalReturnPageName());
        } catch (Exception e) {}            
	    try {
	        PropertyUtils.write(page, "notHtmlEditor", Boolean.valueOf(getCallbackType() == PAGE_IMAGE_NOEDITOR_CALLBACK));
        } catch (Exception e) {}
        try {
            PropertyUtils.write(page, "searchMedia", Boolean.valueOf(getCallbackType() == PAGE_MEDIA_CALLBACK));
        } catch (Exception e) {}
        try {
        	PropertyUtils.write(page, "nodeId", getEditedObjectId());
        } catch (Exception e) {}
        cycle.activate(page);
	}
	
	public void performAction(IRequestCycle cycle) {
	    Long selectedImageId = getSelectedImageId();
		NodeImage img = getImageDAO().getImageWithId(selectedImageId.intValue());
		IImageCallback callback = null;
		if (getCallbackType() == EDIT_IMAGE_CALLBACK) {
		    callback = getImageHelper().getEditCallback();
		} else if (getCallbackType() == COPY_IMAGE_CALLBACK) {
		    callback = new IImageCallback() {
		        public void actOnImage(NodeImage img, IRequestCycle cycle) {
					EditImageData editPage = (EditImageData) cycle.getPage(EditImageData.getEditPageNameForMedia(img));
					Long editedObjectId = getEditedObjectId();
					editPage.setEditedObjectId(editedObjectId);
					editPage.getImage().setValues(img, false, false);
					editPage.saveImage();
					cycle.activate(editPage);				            
		        }
		    };

		} else if (getCallbackType() == DELETE_IMAGE_CALLBACK){
		    callback = getImageHelper().getDeleteCallback(getEditedObjectId(), getSearchPageName());
		} else if (getCallbackType() == TILLUS_CALLBACK) {
		    callback = getImageHelper().getTillusCallback(getEditedObjectId(), getAdditionalReturnPageName());
		} else if (getCallbackType() == TR_STATE_STANDARDS_CALLBACK) {
		    callback = new IImageCallback() {
		        public void actOnImage(NodeImage img, IRequestCycle cycle) {
		            TeacherResource resource = (TeacherResource) getTreehouse();
		            resource.setStateStandardsDocument((Document) img);
		            getWorkingAccessoryPageDAO().saveAccessoryPage(resource);
		            cycle.activate("TeacherResourceEditLearning");
		        }
		    };
		} else if (getCallbackType() == TR_NATIONAL_STANDARDS_CALLBACK) {
		    callback = new IImageCallback() {
		        public void actOnImage(NodeImage img, IRequestCycle cycle) {
		            TeacherResource resource = (TeacherResource) getTreehouse();
		            resource.setNationalStandardsDocument((Document) img);
		            getWorkingAccessoryPageDAO().saveAccessoryPage(resource);
		            cycle.activate("TeacherResourceEditLearning");
		        }
		    };		    
		} else if (getCallbackType() == TR_SUPPORT_DOCUMENT_CALLBACK){
		    // Add a support document to a teacher resource
		    callback = new IImageCallback() {
		        public void actOnImage(NodeImage img, IRequestCycle cycle) {
		            TeacherResourceEditSupportMaterials editPage = (TeacherResourceEditSupportMaterials) cycle.getPage("TeacherResourceEditSupportMaterials");
		            editPage.doAddSupportDocument((Document) img, cycle);
		        }
		    };
		} else if (getCallbackType() == ADD_NODE_TO_IMAGE_CALLBACK) {
			callback = new IImageCallback() {
				public void actOnImage(NodeImage img, IRequestCycle cycle) {
					MappedNode node = getMiscNodeDAO().getNodeWithId(getEditedObjectId());
					img.addToNodesSet(node);
					getImageDAO().saveImage(img);
					AddImageToNode addPage = (AddImageToNode) cycle.getPage("AddImageToNode");
					addPage.finishNodeAdding(getEditedObjectId(), cycle);
				}
			};
		} else {
		    callback = new IImageCallback() {
		        public void actOnImage(NodeImage img, IRequestCycle cycle) {
		            TreehouseEditPortfolio editPage = (TreehouseEditPortfolio) cycle.getPage(getAdditionalReturnPageName());
                    editPage.addImageToPortfolioPage(img, cycle);
                }
            };
        }
		callback.actOnImage(img, cycle);
	}

    public boolean getCanEdit() {
    	NodeImage img = getImage();
		boolean result;
    	if (getCallbackType() == EDIT_IMAGE_CALLBACK) {
            Contributor contr = getContributor();
            result = getPermissionChecker().checkEditingPermissionForImage(contr, img);
    	} else {
            // If the user is just choosing an img to copy data from, there is no need to
            // check if the image can be edited
            result = true;             
    	}
    	return result;   
    }
    
    public boolean getWasUserSearch() {
        return getReturnPageName().equals("ImageSearchPage");
    }
    
    public String getEditImageJavascriptActionString() {
		return getJavascriptActionString("editObject", "edit", ((NodeImage) ((Table) getComponent("table")).getTableRow()).getId() + "");
    }    
	
	public boolean getCopyingImageData() {
	    return getCallbackType() == COPY_IMAGE_CALLBACK || getCallbackType() == TILLUS_CALLBACK;
	}
	
	public String getColumnsString() {
	    String filenameString = getDontShowFilename() ? "" : "Filename:getLocation(), ";
	    return filenameString + "!thumbnail, !data"; 
	}
	
	public NodeImage getImage() {
	    return (NodeImage) ((Table) getComponents().get("table")).getTableRow();
	}
	
	public IPrimaryKeyConvertor getConvertor() {
	    return new IPrimaryKeyConvertor() {
			public Object getPrimaryKey(Object objValue) {
				return Integer.valueOf(((NodeImage) objValue).getId());
			}

			/**
			 * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getValue(java.lang.Object)
			 */
			@SuppressWarnings("unchecked")
			public Object getValue(Object objPrimaryKey) {
			    Integer order = (Integer) objPrimaryKey;
			    for (Iterator iter = getImages().iterator(); iter.hasNext();) {
		            NodeImage img = (NodeImage) iter.next();
		            Integer nextId = Integer.valueOf(img.getId());
		            if (nextId.equals(order)) {
		                return img;
		            }
		        }
			    return null;
			} 

	    };
	}
	
	public String getActionLinkClass() {
	    if (getCallbackType() == DELETE_IMAGE_CALLBACK) {
	        return "deletelink";
	    } else {
	        return "editlink";
	    }
	}
	
	public String getInsertImageString() {
        if (getCallbackType() != PAGE_IMAGE_NOEDITOR_CALLBACK) {
            return getUrlBuilder().getJavascriptInsertImageUrl(getImage(), Integer.valueOf(250), false, true);
        } else {
            return getUrlBuilder().getNonJavascriptInsertImageHtml(getImage(), Integer.valueOf(250));
        }
	}
    
    public boolean getIncludeTinyMceScript() {
        return getWrapperType() == AbstractWrappablePage.NEW_FORM_WRAPPER && (getCallbackType() == NO_CALLBACK ||
            getCallbackType() == PAGE_MEDIA_CALLBACK);
    }
}
