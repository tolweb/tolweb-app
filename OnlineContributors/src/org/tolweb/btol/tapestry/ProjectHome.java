package org.tolweb.btol.tapestry;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.InjectPage;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.tapestry.annotations.MolecularPermissionNotRequired;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.Contributor;

@MolecularPermissionNotRequired
public abstract class ProjectHome extends ProjectPage implements IExternalPage,
		ProjectInjectable, NodeInjectable {	
	public void activateExternalPage(Object[] params, IRequestCycle cycle) {
		Number projectId = (Number) params[0];
		getCookieAndContributorSource().loginProject(getProjectDAO().getProjectWithId(Long.valueOf(projectId.longValue())));
	}
	public String getRootNodeName() {
		return getWorkingNodeDAO().getNameForNodeWithId(getProject().getRootNodeId());
	}
    @InjectPage("btol/EditGene")
    public abstract EditGene getEditGenePage();
    @InjectPage("btol/EditGeneFragment")
    public abstract EditGeneFragment getEditGeneFragmentPage();
    @InjectPage("btol/EditPrimer")
    public abstract EditPrimer getEditPrimerPage();
    @InjectPage("btol/EditContributor")
    public abstract EditContributor getEditPersonPage();
    @InjectPage("btol/EditPCRProtocol")
    public abstract EditPCRProtocol getEditProtocolPage();
    @Asset("img/Gloriosaresized.jpg")
    public abstract IAsset getBeetleImage();

    public IPage addNewGene() {
    	EditGene page = getEditGenePage();
    	return page.editNewObject(this);
    }
    public IPage addNewPrimer() {
    	EditPrimer page = getEditPrimerPage();
    	return page.editNewObject(this);
    } 
    public IPage addNewPerson() {
    	EditContributor page = getEditPersonPage();
    	return page.editNewObject(this);
    }
    public IPage addNewGeneFragment() {
    	EditGeneFragment page = getEditGeneFragmentPage();
    	return page.editNewObject(this);
    }
    public IPage editMyInformation() {
    	EditContributor editPage = getEditPersonPage();
    	editPage.setContributorToEdit(getCookieAndContributorSource().getContributorFromSessionOrAuthCookie());
    	editPage.setPreviousPageName(getPageName());
    	return editPage;
    }
    public IPage addNewProtocol() {
    	EditPCRProtocol page = getEditProtocolPage();
    	return page.editNewObject(this);
    }
    public boolean getCanViewMolecular() {
    	Contributor c = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
    	boolean canView = getProject().getContributorCanViewAndEditMolecularData(c);
    	return canView;
    }
}
