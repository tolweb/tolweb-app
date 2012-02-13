package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneFragment;
import org.tolweb.dao.BaseDAO;

public interface GeneFragmentDAO extends BaseDAO {
	public void saveGeneFragment(GeneFragment geneFrag, Long projectId);
	public List getAllGeneFragmentsInProject(Long projectId);
	public List getAllGeneFragmentsInProject(Long projectId, boolean mtGenomeInFront);
	public List getAllGeneFragmentsForGene(Gene gene, Long projectId);
	public GeneFragment getGeneFragmentWithId(Long id, Long projectId);
	public GeneFragment getGeneFragmentWithName(String name, Long projectId);
}
