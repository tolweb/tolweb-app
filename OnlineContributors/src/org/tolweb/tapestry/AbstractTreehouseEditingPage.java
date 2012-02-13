/*
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Date;
import java.util.TreeSet;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.EditHistoryDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.Webquest;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.tapestry.treehouse.injections.BetaTreehouseInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractTreehouseEditingPage extends AbstractTreehouseContributorPage implements PageBeginRenderListener, RequestInjectable, 
		BetaTreehouseInjectable, PageInjectable, CookieInjectable, BaseInjectable {
    private ValidationDelegate validationDelegate;
	public abstract void setPreviewSelected(boolean value);
	public abstract boolean getPreviewSelected();
	public abstract void setReturnToEditorSelected(boolean value);
	public abstract boolean getReturnToEditorSelected();
	public abstract void setRedirectLocation(String value);
	public abstract void setOtherEditPageName(String value);
	public abstract String getOtherEditPageName();
	public abstract boolean getDontUseEditor();
	public abstract void setDontUseEditor(boolean value);
    public abstract void setSelectedAnchor(String value);
    public abstract String getSelectedAnchor();
    public abstract void setGoToManagerSelected(boolean value);
    public abstract boolean getGoToManagerSelected();	
	public abstract String getLockedOrSubmittedMessage();
	public abstract void setLockedOrSubmittedMessage(String lockedOrSubmittedMessage);    
	
	public void pageBeginRender(PageEvent event) {
		if (getTreehouse() != null) {
			Long historyId = getTreehouse().getEditHistoryId();
			if (historyId != null) {
				PermissionChecker checker = getPermissionChecker();
				Contributor lockedContributor = checker.checkAccessoryPageIsLocked(getContributor(), getTreehouse());			
				if (lockedContributor != null) {
					String lockedMessage = checker.getLockedMessageForEditHistory(historyId);
					lockedMessage += " You won't be able to edit it until they logout or until their lock expires in an hour.";
					setLockedOrSubmittedMessage(lockedMessage);
				}
			}
			if (!event.getRequestCycle().isRewinding()) {
				getAccessoryPageHelper().establishEditorUseInRequestCycle();
			}
		}
	}
	public boolean getIsEditedOrSubmitted() {
		return StringUtils.notEmpty(getLockedOrSubmittedMessage());
	}    
	public boolean getUseHTMLEditor() {
		return getAccessoryPageHelper().getShouldUseEditor();
	}
	
	public boolean doAdditionalFormProcessing(IRequestCycle cycle) {
	    return true;
	}
    
    public void returnToEditor(IRequestCycle cycle) {
        setReturnToEditorSelected(true);        
    }
    
    public void goToManager(IRequestCycle cycle) {
        setGoToManagerSelected(true);
    }
    
	public void previewSubmit(IRequestCycle cycle) {
		setPreviewSelected(true);
	} 
    
    public String getPreviewPageName() {
        return "ViewTreehouse";
    }
	
	public void setEditorPreference(IRequestCycle cycle) {
		boolean dontUseEditor = getDontUseEditor();
		getContributor().setDontUseEditor(dontUseEditor);
	    getContributorDAO().saveContributor(getContributor());
	    setPreviewSelected(false);
	    try {
	        PropertyUtils.write(this, "selectedAnchor", "trhscomposetext");
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}	
    
    public String getWorkingUrl() {
        return getUrlBuilder().getWorkingURLForObject(getTreehouse());
    }
	
	public void formSubmitted(IRequestCycle cycle) {
	    if (!getValidationDelegate().getHasErrors()) {
			MappedAccessoryPage treehouse = getTreehouse();
			treehouse.setLastEditedContributor(getContributor());
			treehouse.setLastEditedDate(new Date());
			doAdditionalFormProcessing(cycle);
			updateWorkInProgress(treehouse);
			doSave();
			if (getReturnToEditorSelected()) {
			    cycle.activate("TreehouseEditor");
			} else if (StringUtils.notEmpty(getOtherEditPageName())) {
			    goToOtherEditPageName(cycle);
			} else if (getGoToManagerSelected()) {
			    cycle.activate("TreehouseMaterialsManager");
            } else if (getPreviewSelected()) {
                goToPreview(cycle);
            }
	    }
	}
    
    protected void goToPreview(IRequestCycle cycle) {
        String host = getRequest().getHeader("X-Forwarded-Host");
        String url = "http://" + host + "/onlinecontributors/app?service=external&page=" + getPreviewPageName() + "&sp="  
            + getTreehouse().getAccessoryPageId() + "&" + CacheAndPublicAwarePage.WORKING + "=1";
        if (getIsToolTryout()) {
            url += "&sp=Sbeta";
        } else {
            url += "&sp=1";
        }
        String editPageName = getPageName(); 
        setPreviousTreehouseEditPageName(editPageName);
        //setRedirectLocation(url);
        throw new RedirectException(url);        
    }
	
	protected void goToOtherEditPageName(IRequestCycle cycle) {
	    cycle.activate(getOtherEditPageName());
	}
	
	public void doSave() {
		AccessoryPageDAO dao = getAccessoryPageDAOReference();
		// MAGIC!!! (well, MORE MAGIC) - this line is pivotal in allowing the editing 
		// of InternetLinks for Treehouses.  It turns out that being an embedded entity 
		// within AccessoryPages means that you values (all object fields) define your 
		// identity.  So when ever anything changes in an object - the current one is 
		// delete and a new one is created.  Well, that's the idea - but it doesn't 
		// happen before Hibernate generates valid, but hapless SQL that is *never* 
		// true because the clause includes "comments=null." You must use the 'is' 
		// operating in MySQL's SQL dialect to test for nullability, so it should be 
		// generating the clause "comments is null" and then the row would be deleted. 
		// For more see "Treehouse Internet Links Debacle" in TolwebDocumentation/incidents
		getTreehouse().setInternetLinks(new TreeSet(getTreehouse().getInternetLinks()));
		dao.saveAccessoryPage(getTreehouse());
		EditHistoryDAO editDAO = getEditHistoryDAO();
		editDAO.updateLastEdited(getTreehouse().getEditHistoryId(), getContributor());
	}
	
	private AccessoryPageDAO getAccessoryPageDAOReference() {
		// If this contributor doesn't exist, or the emails don't match,
		// then it must be the case that things are on beta.  Make sure to use 
		// the beta acc page dao
		if (getIsToolTryout()) {
		    return getBetaAccPageDAO();		    
		} else {
		    return getWorkingAccessoryPageDAO();
		}		
	}
	
	private void updateWorkInProgress(MappedAccessoryPage treehouse) {
	    if (treehouse.getPageContentProgress() != MappedAccessoryPage.COMPLETE) {
	        boolean hasText = StringUtils.notEmpty(treehouse.getText()); 
	        if (hasText) {
	            treehouse.setPageContentProgress(MappedAccessoryPage.WORK_IN_PROGRESS);
	        }
	    }
	    if (treehouse.getRefsProgress() != MappedAccessoryPage.COMPLETE) {
	        boolean hasRefs = StringUtils.notEmpty(treehouse.getReferences()); 
	        boolean hasLinks = treehouse.getHasInternetLinks(); 
	        if (hasRefs || hasLinks) {
	            treehouse.setRefsProgress(MappedAccessoryPage.WORK_IN_PROGRESS);
	        }
	    }
	    if (treehouse.getLearningProgress() != MappedAccessoryPage.COMPLETE) {
	        boolean hasComments = StringUtils.notEmpty(treehouse.getComments());
	        boolean isAdvanced = treehouse.getIsAdvancedLevel();
	        boolean isBeginner = treehouse.getIsBeginnerLevel();
	        boolean isIntermediate = treehouse.getIsIntermediateLevel();
	        boolean isArtAndCulture = treehouse.getIsArtAndCulture();
	        boolean isInvestigation = treehouse.getIsInvestigation();
	        boolean isStory = treehouse.getIsStory();
	        boolean isTeacherResource = treehouse.getIsTeacherResource();
	        boolean isBiography = treehouse.getIsBiography();
	        boolean isGame = treehouse.getIsGame();
	        if (hasComments || isAdvanced || isBeginner || isIntermediate
	                || isArtAndCulture || isInvestigation  || isStory
	                || isTeacherResource || isBiography || isGame) {
	            treehouse.setLearningProgress(MappedAccessoryPage.WORK_IN_PROGRESS);
	        }
	    }
	    if (treehouse.getAttachProgress() != MappedAccessoryPage.COMPLETE) {
	        boolean hasScientificName = StringUtils.notEmpty(treehouse.getScientificName());
	        boolean hasNodes = treehouse.getNodesSet().size() > 0; 
	        if (hasScientificName || hasNodes) {
	            treehouse.setAttachProgress(MappedAccessoryPage.WORK_IN_PROGRESS);
	        }
	    }
	    if (treehouse.getNotesProgress() != MappedAccessoryPage.COMPLETE) {
	        boolean hasNotes = StringUtils.notEmpty(treehouse.getNotes()); 
	        if (hasNotes) {
	            treehouse.setNotesProgress(MappedAccessoryPage.WORK_IN_PROGRESS);
	        }
	    }
	}
	
	public void goToAbout(IRequestCycle cycle) {
	    setOtherEditPageName("TreehouseEditAboutPage");	    
	}
	
	public void goToContent(IRequestCycle cycle) {
	    String pageName;
	    if (getIsTeacherResource() && !getIsOther()) {
	        pageName = "TeacherResourceEditMainContent";
	    } else if (getIsWebquest()) {
	        pageName = "WebquestEditIntroduction";
	    } else {
	        pageName = "TreehouseEditPageContent";
	    }
	    setOtherEditPageName(pageName);	    
	}
	
	public void goToMedia(IRequestCycle cycle) {
	    setOtherEditPageName("TreehouseEditMedia");	    
	}	
	
	public void goToRefs(IRequestCycle cycle) {
	    setOtherEditPageName("TreehouseEditRefsInternetInfo");	    
	}
	
	public void goToLearning(IRequestCycle cycle) {
	    setOtherEditPageName("TeacherResourceEditLearning");	    
	}
	
	public void goToAttachments(IRequestCycle cycle) {
	    setOtherEditPageName("TreehouseEditAttachments");	    
	}
	
	public void goToNotes(IRequestCycle cycle) {
	    setOtherEditPageName("TreehouseEditNotes");	        
	}
	
	public void goToPublish(IRequestCycle cycle) {
	    setOtherEditPageName("TreehouseEditPublish");
	}
	
	public void goToProcess(IRequestCycle cycle) {
	    setOtherEditPageName("WebquestEditProcess");
	}
	
	public void goToConclusion(IRequestCycle cycle) {
	    setOtherEditPageName("WebquestEditConclusion");
	}
	
	public void goToTeacherPage(IRequestCycle cycle) {
	    setOtherEditPageName("WebquestEditTeacher");
	}
	
	public void goToSupport(IRequestCycle cycle) {
	    setOtherEditPageName("TeacherResourceEditSupportMaterials");
	}
	
	public void goToPortfolio(IRequestCycle cycle) {
	    setOtherEditPageName("TreehouseEditPortfolio");
	}
	
	/*public boolean getIsToolTryout() {
		return ((OnlineContributorsVisit) getVisit()).getIsToolTryout();
	}*/
	
	public PopupLinkRenderer getHelpLinkRenderer() {
        return getRendererFactory().getHelpRenderer();
	}
	
	public boolean getStepIsComplete() {
	    return getProgressProperty(getProgressMethodPropertyName()) == MappedAccessoryPage.COMPLETE;
	}
	
	public void setStepIsComplete(boolean value) {
	    int progress;
	    if (value) {
	        progress = MappedAccessoryPage.COMPLETE;
	    } else {
	        int currentValue = getProgressProperty(getProgressMethodPropertyName());
	        if (currentValue == MappedAccessoryPage.COMPLETE) {
		        // set to work in progress if it is no longer complete
		        progress = MappedAccessoryPage.WORK_IN_PROGRESS;
	        } else {
	            // otherwise leave it at whatever it currently is.
	            progress = currentValue;
	        }
	    }
	    try {
	        Ognl.setValue(getProgressMethodPropertyName(), getTreehouse(), Integer.valueOf(progress));
	    } catch (OgnlException e) {
	        e.printStackTrace();
	    }
	}
	
	public String getProgressMethodPropertyName() {
	    return null;
	}
	
	private int getProgressProperty(String propertyName) {
	    try {
		    Number complete = (Number) Ognl.getValue(propertyName, getTreehouse());
		    return complete.intValue();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return MappedAccessoryPage.NEED_TO_EDIT;
	    }
	}
	
	public String getProgressClassName(String progressMethodPropertyName, boolean onlyComplete) {
	    int value = getProgressProperty(progressMethodPropertyName);
	    if (onlyComplete && value != MappedAccessoryPage.COMPLETE) {
	        return null;
	    }
	    switch(value) {
	    	case MappedAccessoryPage.NEED_TO_EDIT: return "needtoedit";
	    	case MappedAccessoryPage.WORK_IN_PROGRESS: return "workinprocess";
	    	case MappedAccessoryPage.COMPLETE: return "complete";
	    	default: return null;
	    }
	}
	
	public String getAboutProgressClassname() {
	    return getProgressClassName(TreehouseEditAboutPage.PROGRESS_PROPERTY, false);
	}
	
	public String getAboutProgressClassnameA() {
	    return getProgressClassName(TreehouseEditAboutPage.PROGRESS_PROPERTY, true);
	}
	
	public String getContentProgressClassname() {
	    return getProgressClassName(TreehouseEditPageContent.PROGRESS_PROPERTY, false);
	}
	public String getContentProgressClassnameA() {
	    return getProgressClassName(TreehouseEditPageContent.PROGRESS_PROPERTY, true);
	}
	
	public String getMediaProgressClassname() {
	    return getProgressClassName(TreehouseEditMedia.PROGRESS_PROPERTY, false);
	}
	
	public String getMediaProgressClassnameA() {
	    return getProgressClassName(TreehouseEditMedia.PROGRESS_PROPERTY, true);
	}	
	
	public String getRefsProgressClassname() {
	    return getProgressClassName(TreehouseEditRefsInternetInfo.PROGRESS_PROPERTY, false);
	}
	
	public String getRefsProgressClassnameA() {
	    return getProgressClassName(TreehouseEditRefsInternetInfo.PROGRESS_PROPERTY, true);
	}	
	
	public String getLearningProgressClassname() {
	    return getProgressClassName(TreehouseEditLearning.PROGRESS_PROPERTY, false);
	}
	
	public String getLearningProgressClassnameA() {
	    return getProgressClassName(TreehouseEditLearning.PROGRESS_PROPERTY, true);
	}	
	
	public String getAttachProgressClassname() {
	    return getProgressClassName(TreehouseEditAttachments.PROGRESS_PROPERTY, false);
	}
	
	public String getAttachProgressClassnameA() {
	    return getProgressClassName(TreehouseEditAttachments.PROGRESS_PROPERTY, true);
	}	
	
	public String getNotesProgressClassname() {
	    return getProgressClassName(TreehouseEditNotes.PROGRESS_PROPERTY, false);
	}
	
	public String getNotesProgressClassnameA() {
	    return getProgressClassName(TreehouseEditNotes.PROGRESS_PROPERTY, true);
	}	
	
	public String getProcessProgressClassname() {
	    return getProgressClassName(WebquestEditProcess.PROGRESS_PROPERTY, false);
	}

	public String getProcessProgressClassnameA() {
	    return getProgressClassName(WebquestEditProcess.PROGRESS_PROPERTY, true);
	}	
	
	public String getConclusionProgressClassname() {
	    return getProgressClassName(WebquestEditConclusion.PROGRESS_PROPERTY, false);
	}
	
	public String getConclusionProgressClassnameA() {
	    return getProgressClassName(WebquestEditConclusion.PROGRESS_PROPERTY, true);
	}
	
	public String getTeacherPageProgressClassname() {
	    return getProgressClassName(WebquestEditTeacher.PROGRESS_PROPERTY, false);
	}
	
	public String getTeacherPageProgressClassnameA() {
	    return getProgressClassName(WebquestEditTeacher.PROGRESS_PROPERTY, true);
	}	
	
	public String getSupportProgressClassname() {
	    return getProgressClassName(TeacherResourceEditSupportMaterials.PROGRESS_PROPERTY, false);
	}
	
	public String getSupportProgressClassnameA() {
	    return getProgressClassName(TeacherResourceEditSupportMaterials.PROGRESS_PROPERTY, true);
	}
	
	public String getPortfolioProgressClassname() {
	    return getProgressClassName(TreehouseEditPortfolio.PROGRESS_PROPERTY, false);
	}
	
	public String getPortfolioProgressClassnameA() {
	    return getProgressClassName(TreehouseEditPortfolio.PROGRESS_PROPERTY, true);
	}		
	
	public ValidationDelegate getValidationDelegate() {
	    if (validationDelegate == null) {
	    	validationDelegate = new ValidationDelegate();
	    }
	    return validationDelegate;
	}
	
    public boolean getIsOther() {
        return getTreehouse().getIsOther();
    }
    
    public boolean getIsTeacherResource() {
        return getTreehouse() != null ? getTreehouse().getIsTeacherResourceInstance() : false;
    }
    
    public boolean getIsWebquest() {
        return Webquest.class.isInstance(getTreehouse());
    }
    
    public boolean getIsPortfolio() {
        return getTreehouse().getTreehouseType() == MappedAccessoryPage.PORTFOLIO;
    }
    
    public boolean getHasPortfolio() {
        return getIsTeacherResource() || getIsPortfolio();
    }
    
    public boolean getHasSupportMaterials() {
        return getIsTeacherResource() && !getIsOther() && !getIsWebquest();
    }	
	
	public IRender getDelegate() {
	    if (getUseHTMLEditor()) {
	        return new HTMLEditorDelegate(true, getTreehouse().getAccessoryPageId());
	    } else {
	        return null;
	    }
	    /*return new IRender() {
	        public void render(IMarkupWriter writer, IRequestCycle cycle) {
	            
	            if (contr != null && !contr.getDontUseEditor()) {
		            String stylesInfo = "theme_advanced_styles: \"captionleft=captionleft;red=red;green=green;purple=purple;grey=grey;brown=brown;orange=orange;lightblue=lightblue;darkblue=darkblue;tanbox=rightbox;bluebox=bluebox;bluebox2=bluebox2;yellowbox=yellowbox;spacedlist=libottomspex;largetext=largetext;xlargetext=xlargetext;smalltext=small\"";
		            String blocksInfo = ", theme_advanced_blockformats: \"p,address,pre,h3,h4,h5,h6\"";
		            writer.printRaw("\n<script language=\"javascript\" type=\"text/javascript\" src=\"/tree/js/tiny_mce/tiny_mce.js\"></script>\n");
		            writer.printRaw("<script language=\"javascript\" type=\"text/javascript\"> tinyMCE.init({ mode : \"textareas\", theme: \"advanced\", valid_elements: \"*[*]\", trim_span_elements: \"false\", theme_advanced_toolbar_location: \"top\", " + stylesInfo + blocksInfo + " });</script>\n");
	            }
	        }
	    };*/
	}
}
