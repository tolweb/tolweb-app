package org.tolweb.btol.xml;

import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.treegrowserver.tapestry.XMLPage;

public abstract class ProjectXMLPage extends XMLPage implements ProjectInjectable, MiscInjectable{
	public Long getProjectIdOrDefault() {
		Long projectId = getProjectHelper().getProjectIdFromRequest();
		if (projectId == null) {
			projectId = Long.valueOf(1);
		}
		return projectId;
	}
}
