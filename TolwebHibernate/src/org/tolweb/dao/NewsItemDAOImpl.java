package org.tolweb.dao;

import java.util.Date;
import java.util.List;

import org.tolweb.hibernate.MediaSurvey;
import org.tolweb.hibernate.NewsItem;

public class NewsItemDAOImpl extends BaseDAOImpl implements NewsItemDAO {

	public void save(NewsItem item) {
		getHibernateTemplate().saveOrUpdate(item);
	}

	public List getAllNewsItems() {
		return getHibernateTemplate().find("from org.tolweb.hibernate.NewsItem");
	}

	public List getLatestActiveNewsItems() {
		String hql = "from org.tolweb.hibernate.NewsItem where active=1 order by createdDate";
		return getHibernateTemplate().find(hql);
	}
	
	public NewsItem getNewsItemWithId(Long id) {
        try {
        	NewsItem item = (NewsItem) getHibernateTemplate().load(org.tolweb.hibernate.NewsItem.class, id);
            return item;
        } catch (Exception e) {
        	e.printStackTrace();
//        	String s = e.toString();
            return null;
        }
	}
}
