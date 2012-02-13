package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.BaseInjectable;

public abstract class TreehouseTitleCondLink extends BaseComponent implements BaseInjectable {
	public abstract MappedAccessoryPage getTreehouse();
	@InjectObject("spring:publicAccessoryPageDAO")
	public abstract AccessoryPageDAO getPublicAccessoryPageDAO();
	
    public boolean getIsPublished() {
        return getPublicAccessoryPageDAO().getAccessoryPageExistsWithId(getTreehouse().getAccessoryPageId());
    }
    
    public String getPublishedUrl() {
        return getUrlBuilder().getURLForTreehouse(getTreehouse().getAccessoryPageId().intValue());
    }
}
