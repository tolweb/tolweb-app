package org.tolweb.tapestry;

import ognl.Ognl;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.asset.ExternalAsset;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.tolweb.hibernate.EditHistory;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

public abstract class EditBranchLeafPageWrapper extends BaseComponent implements PageInjectable, BaseInjectable {
	private ExternalAsset tolformsNewCss;
	
    public abstract String getPreviewAnchor();
    public abstract void setIsThisPage(boolean value);
    @Parameter
    public abstract String getNotesProperty();
    public abstract MappedTextSection getCurrentTextSection();
    public abstract Object getNotesObject();
    
    public ExternalAsset getTolformsNewCss() {
    	if (tolformsNewCss == null) {
    		tolformsNewCss = new ExternalAsset("/tree/css/tolformsnew.css", null);
    	}
    	return tolformsNewCss;
    }

    public EditHistory getEditHistory() {
        MappedPage page = ((AbstractPageEditingPage) getPage()).getEditedPage();
        return getEditHistoryDAO().getEditHistoryWithId(page.getEditHistoryId());
    }
    public IPrimaryKeyConvertor getTextSectionsConvertor() {
        return new OrderedObjectConvertor(getTolPage().getTextSections());
    }
    public MappedPage getTolPage() {
        return ((AbstractPageEditingPage) getPage()).getTolPage();
    }
    public MappedNode getNode() {
        return ((AbstractPageEditingPage) getPage()).getNode();    	
    }
    public boolean getHasPage() {
        return getTolPage() != null;
    }
    public String getPreviewUrl() {
    	String anchorString = getPreviewAnchor() != null ? "#" + getPreviewAnchor() : "";
    	return getUrlBuilder().getWorkingURLForObject(getTolPage()) + anchorString;
    }
    public String getPageType() {
        if (((AbstractPageEditingPage) getPage()).getNode().getIsLeaf()) {
            return "Leaf";
        } else {
            return "Branch";
        }
    }
    public String getTaxonNamesClass() {
        return getThisPageClass("EditTaxonNames");
    }
    public String getGroupPropertiesClass() {
        return getThisPageClass("EditGroupProperties");
    }    
    public String getTillusClass() {
        return getThisPageClass("EditTitleIllustrationsNew");
    }
    public String getLeadTextClass() {
        return getThisPageClass("EditTreeText");
    }
    public String getManageTextSectionsClass() {
        return getThisPageClass("EditManageTextSections");
    }
    public String getCurrentTextSectionsClass() {
        String textSectionPageClass = getThisPageClass("EditTextSection");
        if (textSectionPageClass != null) {
            MappedTextSection sectionBeingEdited = ((EditTextSection) getPage()).getTextSection();
            if (sectionBeingEdited != null && 
                    sectionBeingEdited.getTextSectionId().equals(getCurrentTextSection().getTextSectionId())) {
                return textSectionPageClass;
            }
        }
        // clear this because normally we only care about page name, but different for text sections
        setIsThisPage(false);
        return null;
    }
    public String getReferencesClass() {
        return getThisPageClass("EditReferences");
    }
    public String getInternetInfoClass() {
        return getThisPageClass("EditInternetLinks");
    }
    public String getPageAuthorsClass() {
        return getThisPageClass("EditPageContributors");
    }
    public String getMediaManagerClass() {
        return getThisPageClass("EditBranchLeafMedia");
    }
    public String getMoveBranchClass() {
    	return getThisPageClass("EditMoveBranch");
    }
    public String getTextSectionEditTag() {
         return getTextSectionEditTag(getCurrentTextSection());
    }  
    public String getTextSectionEditTag(MappedTextSection textSection) {
        return AbstractPageEditingPage.EDIT_TEXT_SECTION_PAGE + AbstractPageEditingPage.SEPARATOR + textSection.getTextSectionId();
    }
    public String getOpenManageSpan() {
        String classString = getManageTextSectionsClass();
        if (classString != null) {
            return "<span class=\"" + classString + "\">";
        } else {
            return null;
        }
    }
    public String getCloseManageSpan() {
        String classString = getManageTextSectionsClass();
        if (classString != null) {
            return "</span>";
        } else {
            return null;
        }
    }
    public void setNotes(String value) {
        try {
            String notesProperty = getNotesProperty();
            Ognl.setValue(notesProperty, getPage(), value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getNotes() {
        try {
            return (String) Ognl.getValue(getNotesProperty(), getPage());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    private String getThisPageClass(String pageName) {
        String returnVal = getPage().getPageName().equals(pageName) ? "thispage" : null;
        // set this so we know whether to disable the link to the page
        setIsThisPage(returnVal != null);
        return returnVal;
    }
}
