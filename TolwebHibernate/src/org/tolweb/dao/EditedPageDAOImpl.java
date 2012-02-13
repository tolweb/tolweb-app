package org.tolweb.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.tolweb.hibernate.EditedPage;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public class EditedPageDAOImpl extends HibernateDaoSupport implements
        EditedPageDAO {

    public void addEditedPageForContributor(Long pageId, Contributor contr) {
    	addEditedPageForContributor(pageId, contr, EditedPage.BRANCH_LEAF_TYPE);
    }
	public void addEditedPageForContributor(MappedPage page, Contributor contr) {
    	addEditedPageForContributor(page.getPageId(), contr, EditedPage.BRANCH_LEAF_TYPE);
    }
	public void addEditedPageForContributor(MappedAccessoryPage page, Contributor contr) {
    	addEditedPageForContributor(page.getAccessoryPageId(), contr, EditedPage.ARTICLE_NOTE_TYPE);		
	}
	private void addEditedPageForContributor(Long pageId, Contributor contr, int pageType) {
        if (!getContributorHasEditedPage(pageId, contr, pageType)) {
            EditedPage editPage = new EditedPage();
            editPage.setContributorId(Long.valueOf(contr.getId()));
            editPage.setPageId(pageId);
            editPage.setPageType(pageType);
            getHibernateTemplate().saveOrUpdate(editPage);
        }		
	}
	
    public boolean getContributorHasEditedPage(MappedPage page, Contributor contr) {
    	return getContributorHasEditedPage(page.getPageId(), contr, EditedPage.BRANCH_LEAF_TYPE);
    }
	public boolean getContributorHasEditedPage(MappedAccessoryPage page, Contributor contr) {
    	return getContributorHasEditedPage(page.getAccessoryPageId(), contr, EditedPage.ARTICLE_NOTE_TYPE);
	}   
	private boolean getContributorHasEditedPage(Long pageId, Contributor contr, int pageType) {
        String selectString = "select count(*) from org.tolweb.hibernate.EditedPage ep where ep.pageId=" + 
        pageId + " and ep.contributorId=" + contr.getId() + " and ep.pageType=" + pageType;
        List results = getHibernateTemplate().find(selectString);
        return results != null && ((Integer) results.get(0)).intValue() > 0;
	}
    public List getEditedPageIdsForContributor(Contributor contr, int pageType) {
        return getHibernateTemplate().find("select ep.pageId from org.tolweb.hibernate.EditedPage ep where ep.contributorId=" + contr.getId() + " and ep.pageType=" + pageType);
    }
    public void deleteEditedPagesWithIds(final Set pageIds, final int pageType) {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                String queryString = "delete from org.tolweb.hibernate.EditedPage where pageId in (" + 
                    StringUtils.returnCommaJoinedString(pageIds) + ") and pageType=" + pageType; 
                session.createQuery(queryString).executeUpdate();
                return null;
            }
        });
    }
}
