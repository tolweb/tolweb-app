package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectState;
import org.tolweb.hibernate.MappedAccessoryPage;

public interface TreehouseInjectable extends AccessoryInjectable {
	@InjectState("treehouse")
	public MappedAccessoryPage getTreehouse();
	public void setTreehouse(MappedAccessoryPage value);	
	@InjectState("isToolTryout")
	public Boolean getIsToolTryout();
	public abstract void setIsToolTryout(Boolean value);
	@InjectState("previousTreehousePageName")
	public String getPreviousTreehouseEditPageName();
	public abstract void setPreviousTreehouseEditPageName(String value);
	@InjectState("useRegularImageForm")
	public abstract Boolean getUseRegularImageForm();
	public abstract void setUseRegularImageForm(Boolean value);	
}
