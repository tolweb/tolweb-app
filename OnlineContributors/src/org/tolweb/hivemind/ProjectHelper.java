package org.tolweb.hivemind;

import org.tolweb.btol.Project;

public interface ProjectHelper {
	public String getProjectNameFromRequest();
	public Project getProjectFromRequest();
	public Long getProjectIdFromRequest();
	public Long getProjectIdOrDefault();
}
