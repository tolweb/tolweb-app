package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.NamedObject;
import org.tolweb.btol.Primer;
import org.tolweb.dao.BaseDAO;

public interface PrimerDAO extends BaseDAO {
	public void savePrimer(Primer value, Long projectId);
	public List getAllPrimersInProject(Long projectId);
	public List getPrimersForGene(NamedObject gene, Long projectId);
	public Primer getPrimerWithId(Long value, Long projectId);
	public List getPrimersWithNameOrSynonymAndNotId(String primerName, Long notId, Long projectId);
	public Primer getPrimerWithName(String primerName, Long projectId);
	public Primer getPrimerWithName(String primerName, boolean isForward, Long projectId);
	public Primer getPrimerWithCode(String primerCode, Long projectId);
	public Primer getPrimerWithSynonym(String synonym, Long projectId);
}
