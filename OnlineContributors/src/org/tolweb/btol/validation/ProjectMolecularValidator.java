package org.tolweb.btol.validation;

import org.tolweb.btol.Project;

public class ProjectMolecularValidator extends ProjectValidator {
	protected boolean doValidation(Project project) {
		return project.getContributorCanViewAndEditMolecularData(getContr());
	}
}
