package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.PCRProtocol;

public class PCRProtocolDAOImpl extends ProjectAssociatedDAOImpl implements PCRProtocolDAO {
	public void saveProtocol(PCRProtocol value, Long projectId) {
		doProjectAssociatedSave(value, projectId, getJoinTableName());
	}
	public List<PCRProtocol> getAllProtocolsInProject(Long projectId) {
		return getHibernateTemplate().find(getProtocolSelectPrefix(projectId) + " order by protocol.name asc");
	}
	public PCRProtocol getProtocolWithId(Long value, Long projectId) {
		return (PCRProtocol) getObjectWithId(PCRProtocol.class, value);
	}
	public PCRProtocol getProtocolWithName(String name, Long projectId) {
		return (PCRProtocol) getFirstObjectFromQuery(getProtocolSelectPrefix(projectId) + " and protocol.name=?", name);
	}
	public List<PCRProtocol> getProtocolsWithNameAndNotId(String name, Long id, Long projectId) {
		String queryString = getProtocolSelectPrefix(projectId) + " and protocol.name=?";
		if (id != null) {
			queryString += " and protocol.id != " + id;
		}
		return getHibernateTemplate().find(queryString, name);
	}
	protected String getProtocolSelectPrefix(Long projectId) {
		return getSelectPrefix("protocol", "protocolsSet", projectId);
	}	
	public String getForeignKeyColumnName() {
		return "protocolId";
	}
	public String getJoinTableName() {
		return "ProtocolsToProjects";
	}	
}
