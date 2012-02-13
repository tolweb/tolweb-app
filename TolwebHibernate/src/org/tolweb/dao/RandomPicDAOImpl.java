/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.tolweb.hibernate.MappedPage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RandomPicDAOImpl extends HibernateDaoSupport implements RandomPicDAO {
    /**
     * Returns a list of object arrays with the first element being the img loc and the
     * 2nd element being the group name of the page to link to
     */
    public List getRandomPicsForPage(MappedPage page, boolean includeCurrentPage) {
        String selectString = "select i.location, p.groupName from org.tolweb.hibernate.PageAncestor a, org.tolweb.hibernate.TitleIllustration t, " +
        " org.tolweb.treegrow.main.NodeImage i, org.tolweb.treegrow.MappedPage p where a.ancestorId=" + 
    	page.getPageId() + "and t.pageId=a.pageId and t.imgId=i.imgId and i.usePermission > 0 and p.pageId=t.pageId";  
        if (!includeCurrentPage) {
            selectString += " and t.pageId!=" + page.getPageId();
        }
        return getHibernateTemplate().find(selectString);
    }

    public List getRandomPicsForPage(MappedPage page) {
        return getRandomPicsForPage(page, true);
    }
}
