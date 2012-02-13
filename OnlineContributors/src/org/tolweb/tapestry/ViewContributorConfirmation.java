package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.Set;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class ViewContributorConfirmation extends BasePage implements IExternalPage, PageBeginRenderListener, 
		CookieInjectable, BaseInjectable {
	@InjectObject("spring:contributorDAO")
	public abstract ContributorDAO getContributorDAO();
    @InjectObject("spring:permissionChecker")
    public abstract PermissionChecker getPermissionChecker();	
	public abstract boolean getIsFromRegistration();
	public abstract void setContributor(Contributor contr);
	public abstract Contributor getContributor();
	@SuppressWarnings("unchecked")
	public abstract Set getNodesSet();	
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		Integer contributorId = (Integer) parameters[0];
		Contributor contr = getContributorDAO().getContributorWithId(contributorId.toString());
		setContributor(contr);
	}
	
	public void pageBeginRender(PageEvent event) {
		Contributor viewingContributor = getEditingContributor(); 
		boolean canView = getIsFromRegistration() ||
			getPermissionChecker().isEditor(viewingContributor);
		// we don't want normal people to be able to see these pages 
		if (!canView) {
			throw new PageRedirectException("ContributorLogin");
		}
	}
	
	public boolean getIsCoreScientificContributor() {
		return getContributor().getUnapprovedContributorType() == Contributor.SCIENTIFIC_CONTRIBUTOR;
	}
	
	public boolean getIsGeneralScientificContributor() {
		return getContributor().getUnapprovedContributorType() == Contributor.ACCESSORY_CONTRIBUTOR;
	}
	
	public boolean getEmailOtherRegistrationNotice() {
		boolean sameContributor = true;
		Contributor registeringContributor = getEditingContributor();
		if (registeringContributor != null) {
			sameContributor = registeringContributor.getId() == getContributor().getId();
		}
		return !sameContributor;
	}
	
	public Contributor getEditingContributor() {
		return getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
	}
	
	@SuppressWarnings("unchecked")
	public String getNodesString() {
		Set nodesSet = getNodesSet();
		StringBuilder nodesNamesString = new StringBuilder(); 
		if (nodesSet != null) {
			for (Iterator iter = nodesSet.iterator(); iter.hasNext();) {
				MappedNode element = (MappedNode) iter.next();
				nodesNamesString.append(element.getName());
				if (iter.hasNext()) {
					nodesNamesString.append(", ");
				}
			}
		}
		return nodesNamesString.toString();
	}
}
