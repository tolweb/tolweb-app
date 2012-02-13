package org.tolweb.btol.tapestry;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.valid.ValidationConstraint;
import org.tolweb.btol.ProjectContributor;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.dao.BaseDAO;
import org.tolweb.tapestry.annotations.MolecularPermissionNotRequired;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

@MolecularPermissionNotRequired
public abstract class EditContributor extends AbstractEditPage implements PageBeginRenderListener, 
	ProjectInjectable {
	@Persist("client")
	public abstract Contributor getContributorToEdit();
	public abstract void setContributorToEdit(Contributor value);
	public abstract String getPassword1();
	public abstract String getPassword2();
	public abstract boolean getIsProjectMember();
	public abstract void setIsProjectMember(boolean value);
	public abstract boolean getCanSetDnaFlag();
	public abstract void setCanSetDnaFlag(boolean value);
	public abstract boolean getCanViewMolecular();
	public abstract void setCanViewMolecular(boolean value);
	
	public void pageBeginRender(PageEvent event) {
		if (getContributorToEdit() == null) {
			Contributor newContributor = new Contributor();
			newContributor.setId(-1);
			setContributorToEdit(newContributor);
		} else {
			if (!getRequestCycle().isRewinding() && getEditorIsAdministrator()) {
				// initialize checkboxes for existing users when the user can edit these fields
				setIsProjectMember(getProject().getContributorCanViewProject(getContributorToEdit()));
				setCanSetDnaFlag(getProject().getContributorCanEditDna(getContributorToEdit()));
				setCanViewMolecular(getProject().getContributorCanViewAndEditMolecularData(getContributorToEdit()));
			}
		}
	}
	public IPage saveContributor() {
		Contributor contributorToEdit = getContributorToEdit();
		boolean shouldSaveProject = false;
		if (contributorToEdit.getId() < 0) {
			getContributorDAO().addContributor(contributorToEdit);
			int membershipStatus = getIsProjectMember() ? ProjectContributor.PROJECT_MEMBER : ProjectContributor.OTHER_AFFILIATE;
			// also add this new contributor to the project
			getProject().addContributorToProject(contributorToEdit, membershipStatus);
			shouldSaveProject = true;
		} else {
			if (!checkAndSetPassword()) {
				return null;
			}
			getContributorDAO().saveContributor(contributorToEdit);
		}
		boolean editedIsAdministrator = getEditedIsAdministrator();
		// only worry about the individual checkboxes if the person is not an administrator
		// (as those checkboxes can't be turned off if they are)
		// and if the editor is an administrator (as the ui isn't there otherwise)
		if (!editedIsAdministrator && getEditorIsAdministrator()) {
			if (getIsProjectMember()) {
				getProject().ensureContributorIsMember(contributorToEdit);
			} else {
				getProject().ensureContributorIsntMember(contributorToEdit);
			}
			// don't set the dna flag if they aren't marked as a member, doesn't make sense
			if (getCanSetDnaFlag() && getIsProjectMember()) {
				getProject().ensureContributorCanEditDna(contributorToEdit);
			} else {
				getProject().ensureContributorCantEditDna(contributorToEdit);
			}
			// don't set the molecular flag if they aren't marked as a member, doesn't make sense
			if (getCanViewMolecular() && getIsProjectMember()) {
				getProject().ensureContributorCanViewMolecular(contributorToEdit);
			} else {
				getProject().ensureContributorCantViewMolecular(contributorToEdit);
			}
			shouldSaveProject = true;			
		}
		if (shouldSaveProject) {
			getProjectDAO().saveProject(getProject());
		}
		return conditionallyGotoPreviousPage();
	}
	public AbstractEditPage editNewObject(IPage prevPage) {
		if (prevPage != null) {
			setPreviousPageName(prevPage.getPageName());
		}
		return this;
	}
	public BaseDAO getDAO() {
		return getContributorDAO();
	}
	@SuppressWarnings("unchecked")
	public Class getEditObjectClass() {
		return Contributor.class;
	}
	public void setObjectToEdit(Object value) {
		setContributorToEdit((Contributor) value);
	}
	public boolean getSamePerson() {
		return getContributorToEdit().getId() == getContributor().getId();
	}
	public boolean getEditorIsAdministrator() {
		return getProject().getContributorIsAdministrator(getCookieAndContributorSource().getContributorFromSessionOrAuthCookie());
	}
	public boolean getEditedIsAdministrator() {
		return getProject().getContributorIsAdministrator(getContributorToEdit());
	}
	/**
	 * stolen from regular ToL contr registration.  not really elegant.
	 * would be better to write some kind of dual-field validator
	 * that could be reused
	 * @return
	 */
	private boolean checkAndSetPassword() {
		// If they have a value for one or the other password fields, make
		// sure the two passwords are equal
		if (StringUtils.notEmpty(getPassword1())) {
			if (!getPassword1().equals(getPassword2())) {
				getValidationDelegate().record("Your passwords do not match.  Please retype them and ensure that they match.", ValidationConstraint.CONSISTENCY);
				return false;
			} else {
				getContributorToEdit().setPassword(getPasswordUtils().getMD5Hash(getPassword1()));
			}
		}
		return true;
	}	
}
