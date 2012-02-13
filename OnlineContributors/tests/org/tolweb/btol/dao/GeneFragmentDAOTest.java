package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneFragment;
import org.tolweb.dao.ApplicationContextTestAbstract;

public class GeneFragmentDAOTest extends ApplicationContextTestAbstract {
	GeneFragmentDAO geneFragmentDAO;
	GeneDAO geneDAO;
	
	public GeneFragmentDAOTest(String name) {
		super(name);
		geneFragmentDAO = (GeneFragmentDAO) context.getBean("geneFragmentDAO");
		geneDAO = (GeneDAO) context.getBean("geneDAO"); 
	}
	
	@SuppressWarnings("unchecked")
	public void testGeneFragments() {
		// test if you can create a gene fragment given a gene
		Gene g = geneDAO.getGeneWithName("CAD", 1L);
		GeneFragment gFrag = new GeneFragment();
		gFrag.setName("TestFrag");
		gFrag.setAbbreviatedName("TFx");
		gFrag.setGene(g);
		gFrag.setImportant(false);
		geneFragmentDAO.saveGeneFragment(gFrag, 1L);
		
		// test if you can retrieve a gene fragment with the same name, etc 
		GeneFragment gf2 = geneFragmentDAO.getGeneFragmentWithName("TestFrag", 1L);
		assertEquals(gFrag.getName(), gf2.getName());
		assertEquals(gFrag.getAbbreviatedName(), gf2.getAbbreviatedName());
		assertEquals(gFrag.getGene().getName(), gf2.getGene().getName());

		// test if you can get the first object that matches the gene fragment name
		GeneFragment CAD4 = geneFragmentDAO.getGeneFragmentWithName("CAD4", 1L);
		assertNotNull(CAD4);
		assertEquals(CAD4.getName(), "CAD4");

		// test if you can get the first object that matches the ID for the gene fragment
		GeneFragment pepFrag = geneFragmentDAO.getGeneFragmentWithId(19L, 1L);
		assertNotNull(pepFrag);
		assertEquals(pepFrag.getName(), "PepCK");
		
		// test if you get back null when a gene fragment is not in the table
		GeneFragment notThere = geneFragmentDAO.getGeneFragmentWithName("NotFrag", 1L);
		assertNull(notThere);
		
		// while there are still 'TestFrag' objects, delete them
		while(true) {
			GeneFragment tmp = geneFragmentDAO.getGeneFragmentWithName("TestFrag", 1L);
			if (tmp != null) { 
				geneFragmentDAO.deleteObject(tmp);
			} else {
				break;
			}
		}
		// make sure the deletes worked by trying to get the object
		assertNull(geneFragmentDAO.getGeneFragmentWithName("TestFrag", 1L));

		// make sure we're getting all 25 defined gene fragments for BToL
		List geneFrags = geneFragmentDAO.getAllGeneFragmentsInProject(1L);
		assertNotNull(geneFrags);
		assertEquals(geneFrags.size(), 25);
		
		// make sure we can get all gene fragments for a given gene
		geneFrags = geneFragmentDAO.getAllGeneFragmentsForGene(g, 1L);
		assertNotNull(geneFrags);
		assertEquals(geneFrags.size(), 4);
	}	
}
