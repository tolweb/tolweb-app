package org.tolweb.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Session;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;


public class OtherNameDAOImpl extends WorkingAwareDAO implements OtherNameDAO {
	public void reattachOtherName(Long otherNameId, Long oldNodeId, Long newNodeId, int sequence) {
		Object[] args = new Object[] {newNodeId, otherNameId, oldNodeId, sequence};
		String fmt = "UPDATE OTHERNAMES SET node_id = %1$d, name_order = %4$d, is_important = 0, is_preferred = 0 WHERE othername_id = %2$d AND node_id = %3$d";
		executeRawSQLUpdate(String.format(fmt, args));
		getHibernateTemplate().flush();
	} 	
	
	public void cleanseOtherNameDataForNode(Long nodeId) {
		Object[] args = new Object[] {nodeId};
		String fmt = "UPDATE OTHERNAMES SET is_important = 0, is_preferred = 0 WHERE node_id = %1$d";
		executeRawSQLUpdate(String.format(fmt, args));
		getHibernateTemplate().flush();		
	}
	
	public Map<Long, String> getOtherNamesInfoForNode(MappedNode nd) {
		if (nd == null) {
			throw new IllegalArgumentException("MappedNode cannot be null");
		}
		return getOtherNamesInfoForNode(nd.getNodeId());
	}
	
	public Map<Long, String> getOtherNamesInfoForNode(Long nodeId) {
		Object[] args = new Object[] {nodeId};
		String fmt = "SELECT othername_id, `name` FROM OTHERNAMES WHERE node_id = %1$d";
		Map<Long, String> idsToNames = new HashMap<Long, String>();
		ResultSet results;
		Session session = null;
		try {
			session = getSession();
			Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			results = selectStatement.executeQuery(String.format(fmt, args));
			while(results.next()) {
				Long id = Long.valueOf(results.getLong(1));
				String name = results.getString(2);
				idsToNames.put(id, name);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<Long, String>();
		}
		return idsToNames;
	}
	
	public void flushQueryCache() {
		getSessionFactory().evictQueries();
	}
}
