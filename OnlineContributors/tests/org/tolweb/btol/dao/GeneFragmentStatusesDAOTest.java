package org.tolweb.btol.dao;

import org.tolweb.btol.GeneFragment;
import org.tolweb.btol.GeneFragmentNodeStatus;
import org.tolweb.dao.ApplicationContextTestAbstract;

public class GeneFragmentStatusesDAOTest extends ApplicationContextTestAbstract {
	GeneFragmentDAO geneFragmentDAO;
	GeneDAO geneDAO;
	GeneFragmentNodeStatusDAO geneFragNodeStatDAO;
	
	public GeneFragmentStatusesDAOTest(String name) {
		super(name);
		geneFragmentDAO = (GeneFragmentDAO)context.getBean("geneFragmentDAO");
		geneDAO = (GeneDAO)context.getBean("geneDAO");
		geneFragNodeStatDAO = (GeneFragmentNodeStatusDAO)context.getBean("geneFragmentNodeStatusDAO");
	}
	
	public void testGeneFragmentNodeStatusCreate() {
		//Gene g = geneDAO.getGeneWithName("CAD", 1L);
		GeneFragment gf = geneFragmentDAO.getGeneFragmentWithName("CAD3", 1L);
		
		GeneFragmentNodeStatus nodeStat = geneFragNodeStatDAO.getStatusForGeneFragmentAndNodeIdInProject(gf, 9000L, 1L);
		
		assertNotNull(nodeStat);
		
/*		GeneFragmentNodeStatus gfNodeStat = new GeneFragmentNodeStatus();
		gfNodeStat.setGeneFragment((GeneFragment)frags.get(0));
		gfNodeStat.setNodeId(66L);
		gfNodeStat.setSource(999);
		gfNodeStat.setSourceDbId("999");
		gfNodeStat.setStatus(999);
		
		//geneFragNodeStatDAO.createStatusForGeneFragmentAndNodeIdInProject((GeneFragment)frags.get(0), -999L, 1L);
		GeneFragmentNodeStatus rahr = geneFragNodeStatDAO.getStatusForGeneFragmentAndNodeIdInProject((GeneFragment)frags.get(0), -999L, 1L);
		assertNotNull(rahr);
		assertEquals(rahr.getGeneFragment().getGene().getName(), "CAD");
		
		geneFragNodeStatDAO.updateSourceDbForStatusWithId(rahr.getId(), GeneFragmentNodeStatus.DRM_SOURCE);
		
		rahr = geneFragNodeStatDAO.getStatusForGeneFragmentAndNodeIdInProject((GeneFragment)frags.get(0), -999L, 1L);
		assertNotNull(rahr);
		assertEquals(rahr.getSource(), GeneFragmentNodeStatus.DRM_SOURCE);
		GeneFragmentNodeStatusesSource gfnss = new GeneFragmentNodeStatusesSource();
		
		String s = gfnss.getEditLinkTextForStatus(rahr);
		geneFragNodeStatDAO.updateSourceDbIdForStatusWithId(rahr.getId(), s);
		rahr = null;
		rahr = geneFragNodeStatDAO.getStatusForGeneFragmentAndNodeIdInProject((GeneFragment)frags.get(0), -999L, 1L);
		assertNotNull(rahr);
		assertEquals(rahr.getSourceDbId(), s);
*/		
	}

}
