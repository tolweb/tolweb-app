package org.tolweb.tapestry;

import java.util.Collection;
import java.util.HashSet;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.treegrow.main.Contributor;

public abstract class ScientificContributorsLogin extends BasePage implements PageBeginRenderListener {
	@Persist("client")
	@InitialValue("'ScientificMaterialsManager'")
	public abstract String getDestinationPageName();
	public abstract void setDestinationPageName(String value);
	@Persist("client")
	@InitialValue("null")
	public abstract Object[] getExternalPageParameters();
	public abstract void setExternalPageParameters(Object[] parameters);
	@Persist("client")
	public abstract boolean getGoToWorking();
	public abstract void setGoToWorking(boolean value);
	public abstract void setUserTypes(Collection<Byte> value);
	@Persist("client")
	public abstract Collection<Byte> getUserTypes();
	@Persist("client")
	public abstract boolean getDynamicPageName();
	public abstract void setDynamicPageName(boolean value);
	
	public void pageBeginRender(PageEvent event) {
		if (getUserTypes() == null) {
			Collection<Byte> userTypes = new HashSet<Byte>();
			userTypes.add(Contributor.SCIENTIFIC_CONTRIBUTOR);
			userTypes.add(Contributor.ACCESSORY_CONTRIBUTOR);
			setUserTypes(userTypes);
		}
	}
	
	public void setupPageCallback(boolean isBranchOrLeaf, Object[] parameters, boolean goToWorking, IRequestCycle cycle) {
		String pageName = isBranchOrLeaf ? "ViewBranchOrLeaf" : "ViewBonusPage";
		setDestinationPageName(pageName);
		setExternalPageParameters(parameters);
		setGoToWorking(goToWorking);
		Collection<Byte> userTypes = new HashSet<Byte>();
		userTypes.add(Contributor.SCIENTIFIC_CONTRIBUTOR);
		if (isBranchOrLeaf) {
			userTypes.add(Contributor.REVIEWER);
		} else {
			userTypes.add(Contributor.ACCESSORY_CONTRIBUTOR);
		}
		setUserTypes(userTypes);
		cycle.activate(this);
	}
}
