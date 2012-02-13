package org.tolweb.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class BaseDAOImpl extends HibernateDaoSupport implements BaseDAO {
	public static final String HTTP_PREFIX = "http://";
	public static final String HTTPS_PREFIX = "https://";
	public static final String FTP_PREFIX = "ftp://";
	
	public void deleteAll(Collection objects) {
		getHibernateTemplate().deleteAll(objects);
	}
	public Object getObjectWithId(Class classObj, Number id) {
		try {
			return getHibernateTemplate().load(classObj, id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public List executeFetchQuery(final String queryString, final Hashtable<String, Object> args) {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
            	Query query = buildQuery(session, queryString, args);
            	return query.list();
            }
        });
	}
    public void executeUpdateQuery(final String queryString) {
    	executeUpdateQuery(queryString, null);
    }
    public void executeUpdateQuery(final String queryString, final Hashtable<String, Object> args) {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
            	Query query = buildQuery(session, queryString, args);
                int numRowsUpdated = query.executeUpdate();
                System.out.println("num rows updated was: " + numRowsUpdated);
                return null;
            }
        });
    }
    
    private Query buildQuery(Session session, String queryString, Hashtable<String, Object> args) {
    	Query query = session.createQuery(queryString);
    	if (args != null) {
        	for (Iterator iter = args.keySet().iterator(); iter.hasNext();) {
				String nextString = (String) iter.next();
				Object nextValue = args.get(nextString);
				query.setParameter(nextString, nextValue);
			}
    	}
    	return query;
    }
    
    public int getCountResultFromQuery(String queryString) {
    	return ((Number) getFirstObjectFromQuery(queryString)).intValue();
    }
    
    public Object getFirstObjectFromQuery(String queryString, Object arg) {
        List results = null;
        if (arg != null) {
        	results = getHibernateTemplate().find(queryString, arg);
        } else {
        	results = getHibernateTemplate().find(queryString);
        }
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }    	
    }
    public Object getFirstObjectFromQuery(String queryString) {
    	return getFirstObjectFromQuery(queryString, null);
    }
    
	/**
	 * Mimics getFirstObjectFromQuery() defined, but 
	 * simply grabs the first element in a list and returns it. This method 
	 * actually executed HQL queries and returned the first element.  This 
	 * DAO implementation use Criteria-based Queries and not HQL, so the 
	 * functionality for this convenience method was defined here. 
	 *   
	 * @return Returns null if the list is null or empty
	 */
	protected Object getFirstElementFromList(List lst) {
		return (lst != null && lst.size() > 0) ? lst.get(0) : null;
	}    
    
    /**
     * Execute a sql select and return the first (numeric) elements from the result set
     * in a list
     * @param queryString
     * @return
     */
    public Set executeRawSQLSelectForLongs(String queryString) {
        return executeRawSQLSelectForNumbers(queryString, Long.class);
    }
    
    public Set executeRawSQLSelectForIntegers(String queryString) {
    	return executeRawSQLSelectForNumbers(queryString, Integer.class);
    }
    
    public Set executeRawSQLSelectForNumbers(String queryString, Class numberClass) {
        Set returnVals = new HashSet();
        ResultSet results;
        Session session = null;
        try {
            session = getSession();
            Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            results = selectStatement.executeQuery(queryString);
            while (results.next()) {
            	if (numberClass.equals(Long.class)) {
            		returnVals.add(Long.valueOf(results.getLong(1)));
            	} else {
            		returnVals.add(Integer.valueOf(results.getInt(1)));
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new HashSet();
        }
        return returnVals;    	
    }
    public void executeRawSQLInsert(String sql) {
    	executeRawSQLUpdate(sql);
    }
    public void executeRawSQLDelete(String sql) {
    	executeRawSQLUpdate(sql);
    }
    public void executeRawSQLUpdate(String sql) {
        Session session = null;
        try {
            session = getSession();
            Statement updateStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            updateStatement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    public void deleteObject(Object object) {
        getHibernateTemplate().delete(object);
    }
	protected void addStringMatchIfNotNull(String value, String keyName, Criteria imgsCriteria) {
	    if (value != null) {
	        imgsCriteria.add(Restrictions.like(keyName, value, MatchMode.ANYWHERE));	        
	    }
	}
	protected void addBooleanMatchIfNotNull(Boolean value, String keyName, Criteria imgsCriteria) {
	    if (value != null) {
	        imgsCriteria.add(Restrictions.eq(keyName, value));
	    }
	}    
	protected boolean hasHttpPrefix(String unverifiedUrl) {
		return unverifiedUrl.startsWith(HTTP_PREFIX) || 
			unverifiedUrl.startsWith(HTTPS_PREFIX) ||
			unverifiedUrl.startsWith(FTP_PREFIX);
	}	
}
