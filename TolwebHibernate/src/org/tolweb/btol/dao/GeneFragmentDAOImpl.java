package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneFragment;

public class GeneFragmentDAOImpl extends ProjectAssociatedDAOImpl implements GeneFragmentDAO {
	public static final String MT_GENOME_NAME = "Mitochondrial Genome";
	
	@Override
	public String getForeignKeyColumnName() {
		return "geneFragmentId";
	}

	@Override
	public String getJoinTableName() {
		return "GeneFragmentsToProjects";
	}

	public List getAllGeneFragmentsForGene(Gene gene, Long projectId) {
		String queryString = getGeneFragmentSelectPrefix(projectId, "join geneFragment.gene as g") + " and g.id=" + gene.getId();
		return getHibernateTemplate().find(queryString);
	}

	public List getAllGeneFragmentsInProject(Long projectId) {
		return getAllGeneFragmentsInProject(projectId, false);
	}

	public List getAllGeneFragmentsInProject(Long projectId, boolean mtGenomeInFront) {
		String orderByHql = " order by ";
		orderByHql += " geneFragment.name asc";
		List frags = getHibernateTemplate().find(getGeneFragmentSelectPrefix(projectId) + orderByHql);
		if (mtGenomeInFront) {
			for (int i = 0; i < frags.size(); i++) {
				GeneFragment frag = (GeneFragment)frags.get(i);
				if (frag.getName().equals(MT_GENOME_NAME)) {
					Object tmp = frags.remove(i);
					frags.add(0, tmp);
					break;
				}
			}
		}
		return frags;
	}
	
	protected String getGeneFragmentSelectPrefix(Long projectId) {
		return getGeneFragmentSelectPrefix(projectId, null);
	}
	
	private String getGeneFragmentSelectPrefix(Long projectId, String additionalJoin) {
		return getSelectPrefix("geneFragment", "geneFragmentsSet", projectId, additionalJoin);
	}
		
	public GeneFragment getGeneFragmentWithId(Long id, Long projectId) {
		return (GeneFragment) getFirstObjectFromQuery(getGeneFragmentSelectPrefix(projectId) + " and geneFragment.id=" + id);
	}

	public GeneFragment getGeneFragmentWithName(String name, Long projectId) {
		return (GeneFragment) getFirstObjectFromQuery(getGeneFragmentSelectPrefix(projectId) + " and geneFragment.name=?", name);
	}

	public void saveGeneFragment(GeneFragment geneFrag, Long projectId) {
		doProjectAssociatedSave(geneFrag, projectId, getJoinTableName());
	}
}
