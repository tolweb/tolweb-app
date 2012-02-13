/*
 * Created on Mar 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hivemind.ImageHelper;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ArticleNoteImageSearch extends ReturnablePage implements IExternalPage, 
		PageBeginRenderListener, UserInjectable, NodeInjectable, ImageInjectable, MiscInjectable {
    public abstract int getNewObjectId();
    public abstract void setPageId(Long value);
    @Persist("client")
    public abstract Long getPageId();
    public abstract void setIsPageSearch(boolean value);
    @Persist("client")
    public abstract boolean getIsPageSearch();
    public abstract void setGroupName(String value);
    @Persist("client")
    public abstract String getGroupName();
    public abstract void setDelegate(IRender value);
    public abstract void setEditObjectUrl(String value);
    @Persist("client")
    @InitialValue("false")
    public abstract Boolean getSearchMedia();
    public abstract void setSearchMedia(Boolean searchMedia);
    @Persist("client")
    @InitialValue("false")
    public abstract Boolean getNotHtmlEditor();
    public abstract void setNotHtmlEditor(Boolean notHTMLEditor);
    public abstract boolean getNewObjectUploaded();
    @InjectObject("service:org.tolweb.tapestry.ImageHelper")
    public abstract ImageHelper getImageHelper();    
    
    public void pageBeginRender(PageEvent event) {
        if (getNewObjectId() > 0 && !getIsPageSearch()) {
            NodeImage media = getImageDAO().getImageWithId(getNewObjectId());
            String editObjectUrl = getImageHelper().getEditUrlForMedia(media, getRequestCycle(), getContributor());
            setEditObjectUrl(editObjectUrl);
            final String url = getTapestryHelper().getExternalServiceUrl(getPageName(), 
            		new Object[]{Integer.valueOf(getNewObjectId()), getPageId()});
            setDelegate(getImageHelper().getRedirectDelegate(url));
        }
    }
    
    public Long getNewImageNodeId() {
        if (getPageId() != null && getPageId().intValue() > 0) {
            return getPageId();
        } else {
            return null;
        }
    }

    /**
     * http://working.tolweb.org/onlinecontributors/app?service=external&page=ArticleNoteImageSearch&sp=l16421&sp=T&sp=F
     * Uses:
     * Editor: /onlinecontributors/app?service=external&page=ArticleNoteImageSearch&sp=l0&sp=TF1
     * EditArticleNote: {editedObjectId, true}
     */
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters != null) {
	        Object firstParam = parameters[0];
	        if (firstParam != null && Long.class.isInstance(firstParam)) {
		        Long pageId = (Long) parameters[0];
		        Boolean isPageSearch = (Boolean) parameters[1];
                if (parameters.length > 2) {
                    setNotHtmlEditor((Boolean) parameters[2]);
                    if (parameters.length > 3) {
                        setSearchMedia((Boolean) parameters[3]);
                    }
                }
		        setPageId(pageId);
		        setIsPageSearch(isPageSearch.booleanValue());
                // if the page id is set, but no group name, try and find the group name
                if (getIsPageSearch() && getGroupName() == null && !getIsTreehouses()) {
                    if (getPageId() != null && getPageId().intValue() > 0) {
                        setGroupName(getMiscNodeDAO().getNameForNodeWithId(getPageId()));
                    }
                }
	        } else {
	            // Ok, redirection code of the year!  This is here in the case
	            // that a new image was uploaded to this page, so we redirect 
	            // back to the title illustrations page with the newly uploaded
	            // image added as a title illustration.  This listener was called
	            // by a meta refresh added in the pageBeginRender after a new
	            // image was uploaded.
	            Integer imgId = (Integer) parameters[0];
	            Long pageId = (Long) parameters[1];
	    	    NodeImage img = getImageDAO().getImageWithId(imgId.intValue());
	    	    getImageHelper().getTillusCallback(pageId, getReturnPageName()).actOnImage(img, cycle);
	        }
        }
    }
    
    public String getInstructionsString() {
        if (getNotHtmlEditor()) {
            return "<p class=\"yblock\">When you find the image you want to use, copy the code from the yellow box. Then go back to the article/note form and paste the code into the text box where you want the image to appear. Click on the <strong>options</strong>     link for information about image size/formatting options.</p>";
        } else if (!getSearchMedia()){
            return "<p class=\"yblock\">When you find the image you want to use, click on the 'select' link to insert it into your page.  Click on the <strong>options</strong> link for information about image size/formatting options.</p>";
        } else {
            return "<p class=\"yblock\">When you find the media file you want to use, click on the 'select' link to insert it into your page.";
        }
    }
    
	public abstract String getEditObjectUrl();
	
	public byte getCallbackType() {
	    if (getIsPageSearch()) {
            if (getNotHtmlEditor() != null && getNotHtmlEditor().booleanValue()) {
                return ImageSearchResults.PAGE_IMAGE_NOEDITOR_CALLBACK;
            } else {
                if (getSearchMedia() != null && getSearchMedia().booleanValue()) {
                    return ImageSearchResults.PAGE_MEDIA_CALLBACK;
                } else {
                    return ImageSearchResults.NO_CALLBACK;
                }
            }
	    } else {
	        return ImageSearchResults.TILLUS_CALLBACK;
	    }
	}
	
	public String getSelectString() {
	    if (getIsPageSearch()) {
	        return null;
	    } else {
	        return "select";
	    }
	}
	public boolean getIsTreehouses() {
		return getPageName().contains("Treehouse");
	}
	public String getTitleString() {
		if (getIsPageSearch()) {
			if (!getSearchMedia()) {
				return "Add an Image to a Page";
			} else {
				return "Add a Media File to a Page"; 
			}
		} else {
			return "Add a Title Illustration";
		}
	}
	public String getInstructionsPClass() {
		String className = "small";
		if (showTopBorderForInstructions()) {
			className += " topborder";
		}
		return className;
	}
	protected boolean showTopBorderForInstructions() {
		return getNewObjectUploaded();
	}
}
