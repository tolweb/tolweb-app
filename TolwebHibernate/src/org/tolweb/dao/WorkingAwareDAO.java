/*
 * Created on Nov 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.sql.Statement;

import org.hibernate.Session;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkingAwareDAO extends BaseDAOImpl {
    private boolean isWorking;
    
    
    public boolean getIsWorking() {
        return isWorking;
    }
    public void setIsWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }
    
    public void addObjectWithId(Object id, String tableName, String columnName) {
        addObjectWithId(id.toString(), tableName, columnName);
    }

	public void addObjectWithId(String values, String tableName, String columnName) {
		Session session = getSession();
		try {
			Statement insertStatement = session.connection().createStatement();
            String sql = "insert into " + tableName + "(" + columnName + ") values (" + values + ")";
			insertStatement.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}    
}
