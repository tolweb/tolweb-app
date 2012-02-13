package org.tolweb.hivemind;

import org.apache.tapestry.engine.state.ApplicationStateManager;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.treegrow.main.Contributor;

public abstract class AppStateManagerAware {
	private static final String CONTRIBUTOR = "contributor";
	private static final String TREEHOUSE = "treehouse";
	private static final String USE_REGULAR_IMAGE_FORM = "useRegularImageForm";
	private ApplicationStateManager appStateManager;
	
	public ApplicationStateManager getAppStateManager() {
		return appStateManager;
	}
	public void setAppStateManager(ApplicationStateManager appStateManager) {
		this.appStateManager = appStateManager;
	}
	public boolean getContributorExists() {
		return getAppStateManager().exists(CONTRIBUTOR);
	}
	public Contributor getContributor() {
		return (Contributor) getAppStateManager().get(CONTRIBUTOR);		
	}
	public void setContributor(Contributor contr) {
		getAppStateManager().store(CONTRIBUTOR, contr);
	}
	public MappedAccessoryPage getTreehouse() {
		return (MappedAccessoryPage) getAppStateManager().get(TREEHOUSE);
	}
	public void setTreehouse(MappedAccessoryPage value) {
		getAppStateManager().store(TREEHOUSE, value);
	}
	public boolean getUseRegularImageForm() {
		boolean value = (Boolean) getAppStateManager().get(USE_REGULAR_IMAGE_FORM);
		return value;
	}
}
