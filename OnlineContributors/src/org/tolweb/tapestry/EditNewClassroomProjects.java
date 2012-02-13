/*
 * Created on Dec 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditNewClassroomProjects extends AbstractLearningEditorPage implements PageBeginRenderListener, 
		UserInjectable, BaseInjectable {
	@SuppressWarnings("unchecked")
    public abstract void setNewProjects(List projects);
	@SuppressWarnings("unchecked")
    public abstract List getNewProjects();
	public abstract void setWasRejectedProject(boolean value);
	public abstract void setWasApprovedProject(boolean value);
	public abstract void setApprovedOrRejectedProject(ClassroomProject value);
	public abstract void setToEmail(String value);
	public abstract void setComments(String value);   
    
	public void pageBeginRender(PageEvent event) {
		setNewProjects(getContributorDAO().getUnapprovedProjects());
	}
	
	public void approveProject(IRequestCycle cycle) {
		gotoApproveRejectPage(cycle, true);			
	}
	
	public void rejectProject(IRequestCycle cycle) {
		gotoApproveRejectPage(cycle, false);		
	}
	
	private void gotoApproveRejectPage(IRequestCycle cycle, boolean isApprove) {
		ApproveRejectProjectPage page = (ApproveRejectProjectPage) cycle.getPage("ApproveRejectProjectPage");
		page.setIsApprove(Boolean.valueOf(isApprove));
		page.setProject(getProjectFromCycleParams(cycle));
		cycle.activate(page);
	}
	
	@SuppressWarnings("unchecked")
	private ClassroomProject getProjectFromCycleParams(IRequestCycle cycle) {
		Long projectId = (Long) cycle.getListenerParameters()[0];
		Iterator it = getNewProjects().iterator();
		while (it.hasNext()) {
			ClassroomProject project = (ClassroomProject) it.next();
			if (project.getProjectId().equals(projectId)) {
				return project;
			}
		}
		// this should never happen
		return null;
	}
	
}
