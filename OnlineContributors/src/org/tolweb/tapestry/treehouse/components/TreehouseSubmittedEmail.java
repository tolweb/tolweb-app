package org.tolweb.tapestry.treehouse.components;

import java.util.Collection;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.BaseInjectable;

public abstract class TreehouseSubmittedEmail extends BaseComponent implements BaseInjectable {
	@Parameter(required = true)
	public abstract MappedAccessoryPage getTreehouse();
	@Parameter
	public abstract boolean getIsStudent();
	@Parameter(required = true)
	public abstract Collection<String> getEmailAddresses();
	@Parameter
	public abstract boolean getIsTeacher();
	
	public String getWorkingUrl() {
		return getUrlBuilder().getWorkingURLForObject(getTreehouse());
	}
}
