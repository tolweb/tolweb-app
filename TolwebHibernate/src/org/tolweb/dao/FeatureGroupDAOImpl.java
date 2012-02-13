package org.tolweb.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.tolweb.hibernate.FeatureGroup;
import org.tolweb.misc.FeatureGroupCategory;

public class FeatureGroupDAOImpl extends BaseDAOImpl implements FeatureGroupDAO {
	public FeatureGroup getFeatureGroupWithId(Long id) {
		try {
			FeatureGroup group = (FeatureGroup)getHibernateTemplate().load(org.tolweb.hibernate.FeatureGroup.class, id);
			return group;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public FeatureGroup getRandomFeatureGroup() {
		return (FeatureGroup) getFirstObjectFromQuery("from org.tolweb.hibernate.FeatureGroup order by rand()");
	}
	
	public void create(FeatureGroup fgrp) {
		getHibernateTemplate().save(fgrp);
	}
	
	public void save(FeatureGroup fgrp) {
		getHibernateTemplate().update(fgrp);
	}
	
	public List<FeatureGroup> getFeatureGroupsWithCategory(FeatureGroupCategory category) {
        String queryString = "from org.tolweb.hibernate.FeatureGroup as grp where grp.categoryValue = :cat";
		Session session = getSession();
        Query catQuery = session.createQuery(queryString);
        catQuery.setInteger("cat", category.toInt());
        catQuery.setCacheable(true);
		
		return (List<FeatureGroup>)catQuery.list();		
	}
	
	public List<FeatureGroup> getFeatureGroupsWithString(String category) {
		return getFeatureGroupsWithCategory(FeatureGroupCategory.getValueBy(category));
	}
	
	public List<FeatureGroup> getAllFeatureGroups() {
		return getHibernateTemplate().find("from org.tolweb.hibernate.FeatureGroup");
	}
}
