package org.tolweb.dao;

import java.util.Date;
import java.util.List;

import org.tolweb.hibernate.ArchivedPage;

public interface ArchivedPageDAO {
	public void addArchivedPageForPage(Long pageId, String title, boolean isMajorRevision, Long nodeId, 
			String status, boolean isNewPage);
	public List getArchivedPagesForPage(Long pageId, boolean onlyMajor);
	public List getArchivedPagesForPage(Long pageId, boolean onlyMajor, Date lastRevisionDate);	
	public ArchivedPage getArchivedPageForPageClosestToDate(Long pageId, Date archiveDate);
	public Date getMostRecentVersionDateForPage(Long pageId);
	public ArchivedPage getArchivedPageForPageOnDate(Long pageId, Date archiveDate);	
	public void updateArchivedPageToMajorRevision(Long archivedPageId);
}
