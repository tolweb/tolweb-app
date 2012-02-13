package org.tolweb.btol.tapestry;

import java.util.Collections;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.tolweb.btol.Project;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.dao.BaseDAO;
import org.tolweb.misc.ContributorNameComparator;
import org.tolweb.tapestry.annotations.MolecularPermissionNotRequired;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

@MolecularPermissionNotRequired
public abstract class ViewAllPeople extends AbstractViewAllObjects implements UserInjectable, ProjectInjectable {
	public abstract Contributor getCurrentPerson();
	@InjectPage("btol/EditContributor")
	public abstract AbstractEditPage getEditPage();
	
	@SuppressWarnings("unchecked")
	public List<Contributor> getPeople() {
		Project project = getProject();
		List members = project.getAllProjectMembers();
		Collections.sort(members, new ContributorNameComparator());
		return members;
	}
	public AbstractEditPage editPerson(IRequestCycle cycle) {
		Integer id = (Integer) cycle.getListenerParameters()[0];
		Contributor contr = getContributorDAO().getContributorWithId(id);
		EditContributor editPage = (EditContributor) getEditPage();
		editPage.setContributorToEdit(contr);
		editPage.setPreviousPageName(getPageName());
		return editPage;
	}
	public void removePersonFromProject(Integer contributorId) {
		getProject().removeContributorFromProject(contributorId);
		getProjectDAO().saveProject(getProject());
	}
    public BaseDAO getDAO() {
        return getContributorDAO();
    }
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
        return Contributor.class;
    }
    public boolean getShowRemoveLink() {
    	return getProject().getContributorIsAdministrator(getContributor());
    }
    public boolean getCanEditCurrentPerson() {
    	return getCurrentPerson().getId() == getContributor().getId() ||
    		getCanDoMolecular();
    }
    public boolean getCanDoMolecular() {
    	return getProject().getContributorCanViewAndEditMolecularData(getContributor());
    }
}
