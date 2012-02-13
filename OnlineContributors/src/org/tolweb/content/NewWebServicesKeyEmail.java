package org.tolweb.content;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.hibernate.WebServicesKey;

public abstract class NewWebServicesKeyEmail extends BaseComponent {
	public static String EMAIL_TITLE = "Tree of Life Web Services - User Key Registration";
	
	@Parameter
	public abstract WebServicesKey getWebServicesKey();

}
