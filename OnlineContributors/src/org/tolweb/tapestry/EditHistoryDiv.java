package org.tolweb.tapestry;

import java.util.Date;

import org.apache.tapestry.BaseComponent;
import org.tolweb.hibernate.EditHistory;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public abstract class EditHistoryDiv extends BaseComponent implements UserInjectable {
    public abstract EditHistory getEditHistory();
    public String getCreatedContributorName() {
        return getDisplayNameFromContributorId(getEditHistory().getCreatedContributorId());
    }
    public String getLastEditedContributorName() {
        return getDisplayNameFromContributorId(getEditHistory().getLastEditedContributorId());
    }
    private String getDisplayNameFromContributorId(Long id) {
    	Contributor lastEdited = getContributorDAO().getContributorWithId(id.toString());
    	if (lastEdited != null) {
    		return lastEdited.getDisplayName();
    	} else {
    		return null;
    	}
    }
    public String getCreatedDate() {
        return getDateStringFromDate(getEditHistory().getCreationDate());        
    }
    public String getLastEditedDate() {
        return getDateStringFromDate(getEditHistory().getLastEditedDate());
    }
    private String getDateStringFromDate(Date date) {
        return StringUtils.getGMTDateString(date);
    }
}
