package org.tolweb.dao;

import java.util.Collection;

import org.tolweb.hibernate.EditHistory;
import org.tolweb.treegrow.main.Contributor;

public interface EditHistoryDAO {
    public void updateLastEdited(Long historyId, Contributor contributor);
    public void updateLock(Long historyId, Long contributorId);
    public void updateLock(Long historyId, Contributor contributor);
    public void clearAllLocksForContributor(Contributor contributor);
    public void clearAllLocksForPages(Collection pageIds);
    public void clearLock(Long historyId);
    public Long createNewHistory(Contributor contributor);
    public EditHistory createAndReturnNewHistory(Contributor contributor);
    public EditHistory getEditHistoryWithId(Long historyId);
    public void deleteHistoryWithId(Long historyId);
    
}
