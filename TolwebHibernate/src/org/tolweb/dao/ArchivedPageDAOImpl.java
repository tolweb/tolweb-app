package org.tolweb.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.tolweb.archive.BranchLeafPageArchiver;
import org.tolweb.hibernate.ArchivedPage;

public class ArchivedPageDAOImpl extends BaseDAOImpl implements ArchivedPageDAO {
	private PageDAO workingPageDAO;
	private PageDAO publicPageDAO;
	private BranchLeafPageArchiver archiver;
	
	public void addArchivedPageForPage(Long pageId, String title, boolean isMajorRevision, Long nodeId, String status,
			boolean isNewPage) {
		// Set the date of the revision to be 12:00am on the same day
		Date revisionDate = archiver.getRevisionDateFromDate(new Date());
		// first check to see if an archived page exists on the date
		ArchivedPage existingVersion = getArchivedPageForPageOnDate(pageId, revisionDate);
		if (existingVersion != null && !existingVersion.getMajorRevision() && isMajorRevision) {
			// need to update this archived page to be a major revision because
			// a major revision overrides a non-major revision
			updateArchivedPageToMajorRevision(existingVersion.getId());
		} else if (existingVersion == null) {
			// no existing version, so create one
			ArchivedPage archive = new ArchivedPage();
			archive.setPageId(pageId);
			archive.setMajorRevision(isMajorRevision);
			archive.setPageTitle(title);
			archive.setStatus(status);
	
			archive.setArchiveDate(revisionDate);
			archive.setNodeId(nodeId);
			getHibernateTemplate().saveOrUpdate(archive);
			if (!isNewPage) {
				// revise the content changed dates
				getWorkingPageDAO().updateContentChangedDateForPage(pageId, revisionDate);
				getPublicPageDAO().updateContentChangedDateForPage(pageId, revisionDate);
			}
		}
	}
	public List getArchivedPagesForPage(Long pageId, boolean onlyMajor) {
		return getArchivedPagesForPage(pageId, onlyMajor, null);
	}
	public List getArchivedPagesForPage(Long pageId, boolean onlyMajor, Date lastRevisionDate) {
		String queryString = "from org.tolweb.hibernate.ArchivedPage where pageId=" + pageId;
		if (onlyMajor) {
			queryString +=  " and majorRevision=true";
		}
		if (lastRevisionDate != null) {
			queryString += " and archiveDate!=:date";
		}
		queryString += " order by archiveDate desc";
		Query query = getSession().createQuery(queryString);
		if (lastRevisionDate != null) {
			query.setDate("date", lastRevisionDate);
		}
		return query.list();		
	}
	public ArchivedPage getArchivedPageForPageClosestToDate(Long pageId, Date archiveDate) {
		String queryString = "from org.tolweb.hibernate.ArchivedPage where pageId=" + pageId + " and archiveDate <=:date order by archiveDate desc";
		Query query = getSession().createQuery(queryString);
		query.setDate("date", archiveDate);
		query.setMaxResults(1);
		return (ArchivedPage) query.uniqueResult();
	}
	public Date getMostRecentVersionDateForPage(Long pageId) {
		String queryString = "select max(p.archiveDate) from org.tolweb.hibernate.ArchivedPage p where p.pageId=" + pageId;
		Query query = getSession().createQuery(queryString);
		return (Date) query.uniqueResult();
	}
	public ArchivedPage getArchivedPageForPageOnDate(Long pageId, Date archiveDate) {
		String queryString = "from org.tolweb.hibernate.ArchivedPage where pageId=" + pageId + " and archiveDate=:date";
		Query query = getSession().createQuery(queryString);
		query.setDate("date", archiveDate);
		query.setMaxResults(1);
		return (ArchivedPage) query.uniqueResult();
	}
	public void updateArchivedPageToMajorRevision(Long archivedPageId) {
		String updateString = "update org.tolweb.hibernate.ArchivedPage set majorRevision=1 where id=" + archivedPageId;
		executeUpdateQuery(updateString);
	}
	public PageDAO getPublicPageDAO() {
		return publicPageDAO;
	}
	public void setPublicPageDAO(PageDAO publicPageDAO) {
		this.publicPageDAO = publicPageDAO;
	}
	public PageDAO getWorkingPageDAO() {
		return workingPageDAO;
	}
	public void setWorkingPageDAO(PageDAO workingPageDAO) {
		this.workingPageDAO = workingPageDAO;
	}
	public BranchLeafPageArchiver getArchiver() {
		return archiver;
	}
	public void setArchiver(BranchLeafPageArchiver archiver) {
		this.archiver = archiver;
	}
}
