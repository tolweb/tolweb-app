package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.WebServicesKey;

public class WebServicesKeyDAOImpl extends BaseDAOImpl implements WebServicesKeyDAO {
	public WebServicesKey getWebServicesKeyWithId(Long value) {
		try {
			WebServicesKey wsKey = (WebServicesKey)getHibernateTemplate().load(org.tolweb.hibernate.WebServicesKey.class, value);
			return wsKey;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}
	}

	public WebServicesKey getWebServicesKeyWithEmail(String email) {
		try {
			String queryString = "from org.tolweb.hibernate.WebServicesKey s where s.userEmail=?";
			Object[] args = new Object[1];
			args[0] = email;
			List results = getHibernateTemplate().find(queryString, args);
			if (results != null) {
				return (WebServicesKey)results.get(0);
			} 
			return null;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}
	}
	public void createWebServicesKey(WebServicesKey wsKey) {
		getHibernateTemplate().save(wsKey);
	}
	
	public void saveWebServicesKey(WebServicesKey wsKey) {
		getHibernateTemplate().update(wsKey);
	}
	
	public boolean getWebServicesKeyExistsWithEmail(String email) {
		String queryString = "select count(*) from org.tolweb.hibernate.WebServicesKey s where s.userEmail=?";
		Object[] args = new Object[1];
		args[0] = email;			
		List results = getHibernateTemplate().find(queryString, args);
		if (results != null) {
			Integer count = (Integer)results.get(0);
			return count.intValue() >= 1;
		} else {
			return false;
		}
	}

	public List getAllWebServicesKeys() {
		return getHibernateTemplate().find("from org.tolweb.hibernate.WebServicesKey");
	}
}