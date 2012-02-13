package org.tolweb.btol.tapestry;

import org.tolweb.btol.Project;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.tapestry.Login;
import org.tolweb.treegrow.main.Contributor;

public abstract class BtolLoginForm extends Login implements ProjectInjectable {
	protected void doAdditionalChecking(Contributor contr) {
		if (contr != null) {
			Project btolProject = getProjectDAO().getProjectWithId(Long.valueOf(1));
			if (!btolProject.getContributorCanViewProject(contr)) {
				setError("You are not a registered member of the BTOL Project.  Only registered members of the project have access to the editing tools");
			} else {
				getCookieAndContributorSource().loginProject(btolProject);
			}
		}
	}
}