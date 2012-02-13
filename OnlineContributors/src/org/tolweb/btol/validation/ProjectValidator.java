package org.tolweb.btol.validation;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageValidateListener;
import org.tolweb.btol.Project;
import org.tolweb.btol.tapestry.BtolContributorLogin;
import org.tolweb.hivemind.CookieAndContributorSource;
import org.tolweb.hivemind.ProjectHelper;
import org.tolweb.tapestry.annotations.MolecularPermissionNotRequired;
import org.tolweb.treegrow.main.Contributor;

public class ProjectValidator implements PageValidateListener, PageBeginRenderListener {
	private Project project;
	private Contributor contr;
	private ProjectHelper projectHelper;
	private CookieAndContributorSource contrSource;
	private Object[] externalParams;
	private String externalPageName;

	public void pageValidate(PageEvent evt) {
		checkProjectAndContributor(evt.getRequestCycle());
	}

	private void checkProjectAndContributor(IRequestCycle cycle) {
		boolean redirect = false;
		if (getContr() == null) {
			redirect = true;
		}
		if (getProject() == null) {
			Project project = getProjectHelper().getProjectFromRequest();
			if (doValidation(project, cycle.getPage())) {
				contrSource.loginProject(project);
			} else {
				redirect = true;
			}
		} else if (!getProject().getContributorCanViewProject(getContr())) {
			redirect = true;
		}
		if (redirect) {
			BtolContributorLogin loginPage = (BtolContributorLogin) cycle.getPage("btol/BtolContributorLogin");
			loginPage.setExternalParameters(getExternalParams());
			loginPage.setExternalPageName(getExternalPageName());
			throw new PageRedirectException(loginPage);
		}
	}

	protected boolean doValidation(Project project, IPage page) {
		// By default, you need molecular permissions to view a page 
		// in here unless it is marked with the special annotation
		// that says you don't need it
		if (page.getClass().isAnnotationPresent(MolecularPermissionNotRequired.class)) {
			return project.getContributorCanViewProject(getContr());
		} else {
			return project.getContributorCanViewAndEditMolecularData(getContr());
		}
	}
	
	public void pageBeginRender(PageEvent arg0) {
		if (!arg0.getRequestCycle().isRewinding()) {
			checkProjectAndContributor(arg0.getRequestCycle());
		}
	}	

	public Contributor getContr() {
		return contr;
	}

	public void setContr(Contributor contr) {
		this.contr = contr;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public ProjectHelper getProjectHelper() {
		return projectHelper;
	}

	public void setProjectHelper(ProjectHelper projectHelper) {
		this.projectHelper = projectHelper;
	}
	public void setRequestValues(Contributor contr, Project project, 
			ProjectHelper helper, CookieAndContributorSource contrSource) {
		setContr(contr);
		setProject(project);
		setProjectHelper(helper);
		setContrSource(contrSource);
	}

	public CookieAndContributorSource getContrSource() {
		return contrSource;
	}

	public void setContrSource(CookieAndContributorSource contrSource) {
		this.contrSource = contrSource;
	}

	public Object[] getExternalParams() {
		return externalParams;
	}

	public void setExternalParams(Object[] externalParams) {
		this.externalParams = externalParams;
	}

	public String getExternalPageName() {
		return externalPageName;
	}

	public void setExternalPageName(String externalPageName) {
		this.externalPageName = externalPageName;
	}
}
