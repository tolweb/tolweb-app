package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.annotations.InjectStateFlag;
import org.tolweb.btol.Project;
import org.tolweb.btol.dao.AdditionalFieldsDAO;
import org.tolweb.btol.dao.ProjectDAO;
import org.tolweb.hivemind.ProjectHelper;

public interface ProjectInjectable {
	@InjectObject("spring:projectDAO")
	public ProjectDAO getProjectDAO();
	@InjectObject("spring:additionalFieldsDAO")
	public AdditionalFieldsDAO getAdditionalFieldsDAO();
	@InjectObject("service:org.tolweb.tapestry.ProjectHelper")
	public ProjectHelper getProjectHelper();
	@InjectState("project")
	public Project getProject();
	public void setProject(Project value);
	@InjectStateFlag("project")
	public boolean getProjectExists();	
}
