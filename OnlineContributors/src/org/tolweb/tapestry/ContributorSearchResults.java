/*
 * ContributorSearchResults.java
 *
 * Created on June 7, 2004, 2:36 PM
 */

package org.tolweb.tapestry;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.contrib.table.components.Table;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.ImagePermission;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.page.AccessoryPageContributor;
import org.tolweb.treegrow.page.PageContributor;

/**
 *
 * @author  dmandel
 */
public abstract class ContributorSearchResults extends AbstractContributorSearchOrResultsPage implements UserInjectable, TreehouseInjectable {
	@SuppressWarnings("unchecked")
    public abstract void setContributors(List value);
	@SuppressWarnings("unchecked")
    public abstract List getContributors();
    public abstract void setActionType(byte value);
    public abstract byte getActionType();
    public abstract void setEditedObjectId(Long value);
    public abstract Long getEditedObjectId();
    public abstract int getSelectedContributorId();
    public abstract byte getContributorType();

    public String getSelectLinkString() {
        Contributor contr = (Contributor) ((Table) getComponents().get("table")).getTableRow();
        return "<a onclick=\"javascript:selectAndClose(" + contr.getId() + ")\" href=\"#\">Select</a>";
    }
    
    @SuppressWarnings("unchecked")
    public void selectContributor(IRequestCycle cycle) {
		int contrId = getSelectedContributorId();
		Contributor contr = getContributorDAO().getContributorWithId("" + contrId);
        BasePage editPage = (BasePage) cycle.getPage(getReturnPageName());
        if (EditIdPage.class.isInstance(editPage)) {
            ((EditIdPage) editPage).setEditedObjectId(getEditedObjectId());
        }
		if (editPage instanceof EditImageData) {
			EditImageData editImagePage = (EditImageData) editPage;
			if (getActionType() == AbstractContributorSearchOrResultsPage.SELECT_COPYRIGHT) {
			    editImagePage.getImage().setCopyrightOwnerContributor(contr);
			    editImagePage.getImage().setInPublicDomain(false);
		    	editImagePage.setCopyHolderSelection(EditImageData.OTHER_TOL_CONTRIBUTOR_OWNER);
			    editImagePage.useOtherTolContributorDefaults(); // process the other ToL contributors license defaults as what is selected
			    editImagePage.setSelectedAnchor("copyright");
			} else if (getActionType() == AbstractContributorSearchOrResultsPage.SELECT_SUBMITTER) {
			    editImagePage.getImage().setContributor(contr);
			    editImagePage.getImage().setCreationDate(new Date());
			} else if (getActionType() == AbstractContributorSearchOrResultsPage.EDIT_IMAGE_PERMISSIONS) {
			    ImagePermission permission = new ImagePermission();
			    permission.setContributor(contr);
			    editImagePage.getImage().getPermissionsSet().add(permission);
			}
			editImagePage.saveImage();
			cycle.activate(editPage);		
		} else if (editPage instanceof TreehouseEditAboutPage) {
			MappedAccessoryPage treehouse = getTreehouse();
			TreehouseEditAboutPage aboutPage = (TreehouseEditAboutPage) editPage;
			treehouse.addToContributors((AccessoryPageContributor) getNewPageContributor(contr, true));
			cycle.activate(aboutPage);
			aboutPage.setSelectedAnchor("trhscontributors");
		} else if (editPage instanceof EditArticleNote) {
		    EditArticleNote editANPage = (EditArticleNote) editPage;
		    editANPage.addContributor((AccessoryPageContributor) getNewPageContributor(contr, true));
		    editANPage.setSelectedAnchor("articlecredits");
		    editANPage.saveAccessoryPage();
		    cycle.activate(editANPage);
		} else if (editPage instanceof ClassroomProjectPage) {
		    ClassroomProjectPage projectPage = (ClassroomProjectPage) editPage;
            projectPage.addEditor(contr, cycle);
        } else if (editPage instanceof EditPageContributors) {
            EditPageContributors editContrsPage = (EditPageContributors) editPage;
            editContrsPage.addContributor(getNewPageContributor(contr, false));
            editContrsPage.useFirstAuthorCopyrightOwnersLicenseDefault(); // process the first author/copyright owner's license default as what is selected
            cycle.activate(editContrsPage);
        } else if (AbstractContributorRegistration.class.isInstance(editPage)) {
        	if (getActionType() == AbstractContributorSearchOrResultsPage.EDIT_CONTRIBUTOR_PERMISSIONS) {
	        	((AbstractContributorRegistration) editPage).getEditedContributor().addToContributorPermissions(contr);
        	} else {
        		// going to edit the contributor
        		((AbstractContributorRegistration) editPage).editContributor(contr);
        	}
        	cycle.activate(editPage);
        } else if (getActionType() == AbstractContributorSearchOrResultsPage.ADD_NODE_TO_CONTRIBUTOR_PERMISSIONS) {
        	// add the contributor to the list
        	getPermissionChecker().addNodeAttachmentForContributor(contrId, getEditedObjectId(), true);
        	// then redirect to the working url for the node
        	returnToBranchLeaf();
        }
    }
    
    private PageContributor getNewPageContributor(Contributor contr, boolean isAccPage) {
		PageContributor contributor;
        if (isAccPage) {
            contributor = new AccessoryPageContributor();
        } else {
            contributor = new PageContributor();
        }
		contributor.setContributor(contr);
		contributor.setIsAuthor(true);
		contributor.setIsContact(true);
		contributor.setIsCopyOwner(true);
		return contributor;
    }
    
    public void returnToSearchPage(IRequestCycle cycle) {
        ContributorSearchPage page = (ContributorSearchPage) cycle.getPage("ContributorSearchPage");
        page.doActivate(cycle, getWrapperType(), getReturnPageName(), getActionType(), getEditedObjectId(), getContributorType(), false, false);
    }
    
    public IRender getDelegate() {
        return new IRender() {
            public void render(IMarkupWriter writer, IRequestCycle cycle) {
                writer.printRaw("<script>\n");
                writer.printRaw("function selectAndClose(id) {\n");
                writer.printRaw("\twindow.opener.document.Form0.$TextField.value = id;\n");
                writer.printRaw("\t//window.opener.document.Form0.submit();\n");
                writer.printRaw("\twindow.close();\n}\n");
                writer.printRaw("\n</script>\n");
            }
        };
    }
    
	public IPrimaryKeyConvertor getConvertor() {
	    return new IPrimaryKeyConvertor() {
			public Object getPrimaryKey(Object objValue) {
				return Integer.valueOf(((Contributor) objValue).getId());
			}

			/**
			 * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getValue(java.lang.Object)
			 */
			@SuppressWarnings("unchecked")
			public Object getValue(Object objPrimaryKey) {
			    Integer order = (Integer) objPrimaryKey;
			    for (Iterator iter = getContributors().iterator(); iter.hasNext();) {
		            Contributor contr = (Contributor) iter.next();
		            Integer nextId = Integer.valueOf(contr.getId());
		            if (nextId.equals(order)) {
		                return contr;
		            }
		        }
			    return null;
			} 

	    };
	}    
}
