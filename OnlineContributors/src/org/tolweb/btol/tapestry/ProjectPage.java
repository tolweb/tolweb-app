package org.tolweb.btol.tapestry;

import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Lifecycle;
import org.apache.tapestry.event.PageAttachListener;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.btol.validation.ProjectValidator;
import org.tolweb.tapestry.AbstractContributorPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;

public abstract class ProjectPage extends AbstractContributorPage implements PageAttachListener, UserInjectable, ProjectInjectable, PageDetachListener, BaseInjectable {
    @Bean(lifecycle = Lifecycle.REQUEST)
    public abstract ProjectValidator getProjectValidator();
    
	public void pageAttached(PageEvent event) {
		ProjectValidator validator = getProjectValidator();
		validator.setRequestValues(getCookieAndContributorSource().getContributorFromSessionOrAuthCookie(), 
				getCookieAndContributorSource().getProjectFromSessionOrProjectCookie(), getProjectHelper(), 
				getCookieAndContributorSource());
		addPageBeginRenderListener(getProjectValidator());
	}
	
	public void pageDetached(PageEvent event) {
		removePageBeginRenderListener(getProjectValidator());
	}
	
	public String getLoginPageName() {
		return "Home";
	}
}
