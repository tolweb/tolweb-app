package org.tolweb.tapestry;

import java.util.Date;

import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class TreehouseAutoSave extends AutoSave implements UserInjectable, AccessoryInjectable {
    protected Object getObjectWithId(Long id) {
        return getWorkingAccessoryPageDAO().getAccessoryPageWithId(id);
    }
    protected void saveObject(Object toSave) {
        getWorkingAccessoryPageDAO().saveAccessoryPage((MappedAccessoryPage) toSave);
    }
    protected void updateLastEdited(Long contributorId, Long editHistoryId, Object toSave) {
        if (toSave != null) {
            Contributor contr = getContributorDAO().getContributorWithId(contributorId.toString());
            ((MappedAccessoryPage) toSave).setLastEditedDate(new Date());
            ((MappedAccessoryPage) toSave).setLastEditedContributor(contr);
            saveObject(toSave);
        }
    }    
}
