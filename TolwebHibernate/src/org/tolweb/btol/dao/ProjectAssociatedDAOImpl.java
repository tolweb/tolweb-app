package org.tolweb.btol.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tolweb.dao.BaseDAOImpl;
import org.tolweb.hibernate.PersistentObject;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ProjectAssociatedDAOImpl extends BaseDAOImpl {
	public abstract String getJoinTableName();
	public abstract String getForeignKeyColumnName();
	
	protected String getSelectPrefix(String alias, String assnName, Long projectId) {
		return getSelectPrefix(alias, assnName, projectId, null);
	}
	
	protected String getSelectPrefix(String alias, String assnName, Long projectId, String additionalJoin) {
		String queryString = "select " + alias + " from org.tolweb.btol.Project p join p." + assnName + " as " + alias;
		if (StringUtils.notEmpty(additionalJoin)) {
			queryString += " " + additionalJoin + " ";
		}
		queryString += " where p.id=" + projectId;
		return queryString;
	}
	protected void doProjectAssociatedSave(PersistentObject obj, Long projectId, String tableName) {
		List<PersistentObject> objs = new ArrayList();
		objs.add(obj);
		doProjectAssociatedSave(objs, projectId, tableName);
	}
	protected void doProjectAssociatedSave(Collection<? extends PersistentObject> objects, Long projectId, String tableName) {
		Collection<PersistentObject> newObjects = new ArrayList<PersistentObject>();
		for (PersistentObject obj : objects) {
			if (obj.getId() == null || obj.getId() < 1L) {
				newObjects.add(obj);
			}
		}
		getHibernateTemplate().saveOrUpdateAll(objects);
		if (newObjects.size() > 0) {
			String insertSql = "insert into " + tableName + "(projectId," + getForeignKeyColumnName() + ") values ";
			for (PersistentObject object : newObjects) {
				insertSql += "(" + projectId + "," + object.getId() + "),";
			}
			// chop off the last comma
			insertSql = insertSql.substring(0, insertSql.length() - 1);
			executeRawSQLInsert(insertSql);
		}
	}
    public void deleteObject(Object object) {
    	if (object != null) {
    		Long objectId = ((PersistentObject) object).getId();
    		String deleteSql = "delete from " + getJoinTableName() + " where " + getForeignKeyColumnName() + "=" + objectId;
    		executeRawSQLDelete(deleteSql);
    		super.deleteObject(object);
    	}
    }
}
