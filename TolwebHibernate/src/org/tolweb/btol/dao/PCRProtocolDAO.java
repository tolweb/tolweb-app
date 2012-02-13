package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.PCRProtocol;
import org.tolweb.dao.BaseDAO;


public interface PCRProtocolDAO extends BaseDAO {
	public void saveProtocol(PCRProtocol value, Long projectId);
	public List<PCRProtocol> getAllProtocolsInProject(Long projectId);
	public PCRProtocol getProtocolWithId(Long value, Long projectId);
	public PCRProtocol getProtocolWithName(String name, Long projectId);
	public List<PCRProtocol> getProtocolsWithNameAndNotId(String name, Long id, Long projectId);
}
