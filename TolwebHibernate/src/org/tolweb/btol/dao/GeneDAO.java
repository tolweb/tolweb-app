package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.Gene;
import org.tolweb.btol.NamedObject;
import org.tolweb.dao.BaseDAO;

public interface GeneDAO extends BaseDAO {
	public void saveGene(NamedObject gene, Long projectId);
	public List getAllGenesInProject(Long projectId);
	public List getAllGenesInProject(Long projectId, boolean showHidden);
	public Gene getGeneWithId(Long id, Long projectId);
	public Gene getGeneWithName(String name, Long projectId);
}
