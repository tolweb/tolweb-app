/*
 * Created on Oct 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlossaryDAOImpl extends HibernateDaoSupport implements GlossaryDAO {

	/* (non-Javadoc)
	 * @see org.tolweb.dao.GlossaryDAO#getGlossaryEntries()
	 */
	public List getGlossaryEntries() {
		return getHibernateTemplate().find("from org.tolweb.hibernate.GlossaryEntry where hide=0");
	}
	
	public List getGlossaryEntriesInOrder() {
		return getHibernateTemplate().find("from org.tolweb.hibernate.GlossaryEntry where hide=0 order by word");
	}
}
