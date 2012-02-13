package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.UserInjectable;

public abstract class ClassroomProjectOverview extends BasePage implements IExternalPage, UserInjectable, AccessoryInjectable {
	public abstract void setProject(ClassroomProject project);
	@SuppressWarnings("unchecked")
	public abstract void setTreehouses(List value);
	public abstract void setStudentId(Integer value);

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		Long projectId = (Long) parameters[0];
		ClassroomProject project = getContributorDAO().getProjectWithId(projectId);
		setProject(project);
		setTreehouses(getPublicAccessoryPageDAO().getTreehousesForProject(project));
		if (parameters.length > 1) {
			setStudentId((Integer) parameters[1]);
		}
	}
}
